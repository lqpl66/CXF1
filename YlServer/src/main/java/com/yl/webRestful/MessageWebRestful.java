package com.yl.webRestful;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.yl.Utils.CodeUtils;
import com.yl.Utils.CommonDateParseUtil;
import com.yl.Utils.GetProperties;
import com.yl.Utils.HttpUtils;
import com.yl.Utils.LineUtils;
import com.yl.Utils.MD5Utils;
import com.yl.beans.Admin;
import com.yl.beans.AttractsInfo;
import com.yl.beans.AttractsLine;
import com.yl.beans.Guide;
import com.yl.beans.LineLog;
import com.yl.beans.LinePrice;
import com.yl.beans.LinePriceDetails;
import com.yl.beans.LineTime;
import com.yl.beans.Message;
import com.yl.beans.Order;
import com.yl.beans.PushMessageDevice;
import com.yl.beans.Scenic;
import com.yl.beans.UserCard;
import com.yl.beans.Userinfo;
import com.yl.beans.Version;
import com.yl.mapper.CardMapper;
import com.yl.mapper.ExpenseMapper;
import com.yl.mapper.ScenicMapper;
import com.yl.mapper.UserMapper;
import com.yl.service.CardService;
import com.yl.service.MessageService;

import net.sf.json.JSONObject;

/*
 * 消息推送和用户app定位接口
 */
@Component("messagewebRest")
public class MessageWebRestful {

	@Autowired
	private UserMapper usermapper;
	@Autowired
	private ScenicMapper scenicMapper;

	@Autowired
	private MessageService messageService;
	@Autowired
	private CardService cardService;
	// 景区路径回显
	private String scenicImgUrl = GetProperties.getscenicImgUrl();
	private String scenicFileUrl = GetProperties.getscenicFileUrl();
	private static Logger log = Logger.getLogger(MessageWebRestful.class);
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat df3 = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat df1 = new SimpleDateFormat("HH:mm:ss");

	/*
	 * 更新用户app定位 mbSystemType 1:IOS ;2:Andriod
	 * 
	 * operateType 1:游乐App角色；
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Path("operateLocation")
	@Produces({ MediaType.APPLICATION_JSON })
	public String operateLocation(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		Integer mbSystemType = jsonobject.optInt("mbSystemType");
		Integer operateId = jsonobject.optInt("userId");
		Integer operateType = jsonobject.optInt("operateType");
		String device_token = jsonobject.optString("device_token");
		Double placeX = jsonobject.optDouble("placeX");
		Double placeY = jsonobject.optDouble("placeY");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (placeX != null && placeY != null && mbSystemType != null && mbSystemType > 0 && device_token != null
					&& !device_token.isEmpty() && !device_token.equals("(null)")) {
				if (operateId != null && operateId > 0 && operateType != null && operateType > 0) {// 当前用户登录状态
					// 校验用户userId是游乐app用户
					map.clear();
					if (operateType == 1) {
						map.put("id", operateId);
						Userinfo userinfo = usermapper.Getuserinfo(map);
						if (userinfo == null) {
							result.put("code", "0006");
							result.put("message", "尚未注册，操作失败！");
							return result.toString();
						}
						result = cardService.checkUser(userinfo);
						if (!result.optBoolean("flag")) {
							return result.toString();
						}
						// 查出该用户下所有关联的设备，
						List<PushMessageDevice> pmdListbyUser = messageService.getPushMessageDeviceList(operateId,
								operateType, "1", null, null);
						// 该用户下所有关联的设备存在
						if (pmdListbyUser != null && !pmdListbyUser.isEmpty()) {
							// 判断是否该设配和当前设配一致，如果一致不作处理，如不一致，推送消息到原先设配上
							for (PushMessageDevice pd : pmdListbyUser) {
								if (!pd.getDevice_token().equals(device_token)) {
									if (mbSystemType == 1) {// IOS
										String content = "您的账号(" + CodeUtils.getAccountName(userinfo.getUserName())
												+ ")在另一个地方登录，请重新登录；如果不是您本人操作请及时修改密码!";
										messageService.pushUserMessageByIOSUnicast(pd.getDevice_token(), content);
									} else {// Android
									}
								}
								// 清除以用户为基准的设备关联关系
								messageService.updatePushMessageDevice(null, null, 0, 0, pd.getDevice_token(),
										pd.getMbSystemType());
							}
							// 清除以用户为基准的设备关联关系
							// messageService.updatePushMessageDevice(null,
							// null, 0, 0, device_token, mbSystemType);
							// 查出该设备下所有关联的用户，
							List<PushMessageDevice> pmdListbyDevice = messageService.getPushMessageDeviceList(null,
									null, "2", device_token, mbSystemType);
							// 该设备下所有关联的用户存在
							if (pmdListbyDevice != null && !pmdListbyDevice.isEmpty()) {
								// 清除以设备为基准的用户关联关系
								messageService.updatePushMessageDevice(null, null, 0, 0, device_token, mbSystemType);
								// 设备和当前用户关联
								messageService.updatePushMessageDevice(placeX, placeY, operateId, operateType,
										device_token, mbSystemType);
							} else {// 该设备下所有关联的用户不存在
								// 添加新纪录
								messageService.savePushMessageDevice(placeX, placeY, operateId, operateType,
										device_token, mbSystemType);
							}
						} else {// 不存在
							// 查出该设备下所有关联的用户，
							List<PushMessageDevice> pmdListbyDevicve = messageService.getPushMessageDeviceList(null,
									null, "2", device_token, mbSystemType);
							// 该设备下所有关联的用户存在
							if (pmdListbyDevicve != null && !pmdListbyDevicve.isEmpty()) {
								// 清除以设备为基准的用户关联关系
								messageService.updatePushMessageDevice(null, null, 0, 0, device_token, mbSystemType);
								// 设备和当前用户关联
								messageService.updatePushMessageDevice(placeX, placeY, operateId, operateType,
										device_token, mbSystemType);
							} else {// 该设备下所有关联的用户不存在
								// 添加新纪录
								messageService.savePushMessageDevice(placeX, placeY, operateId, operateType,
										device_token, mbSystemType);
							}
						}
					} else {
						result.put("code", "1000");
						result.put("message", "接口暂未开放！");
						return result.toString();
					}
				} else {// 未登录状态打开app
					// 查出该设备下所有关联的用户，
					List<PushMessageDevice> pmdListbyDevicve = messageService.getPushMessageDeviceList(null, null, "2",
							device_token, mbSystemType);
					if (pmdListbyDevicve != null && !pmdListbyDevicve.isEmpty()) {
						// 清除以设备为基准的用户关联关系即可
						messageService.updatePushMessageDevice(null, null, 0, 0, device_token, mbSystemType);
					} else {// 不存在
						// 添加新纪录
						messageService.savePushMessageDevice(placeX, placeY, 0, 0, device_token, mbSystemType);
					}
				}
				result.put("code", "0001");
				result.put("message", "操作成功！");
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("更新设备位置失败：", e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 获取用户消息type 1:未登录；2：已登录
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getMessage")
	public String getMessage(String json) {
		JSONObject result = new JSONObject();
		JSONObject data = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		String device_token = jsonobject.optString("device_token");
		Integer messageType = jsonobject.optInt("messageType");
		Integer pageNum = jsonobject.optInt("pageNum");
		Integer mbSystemType = jsonobject.optInt("mbSystemType");
		Integer Num = jsonobject.optInt("num");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (messageType != null && messageType > 0 && pageNum != null && pageNum > 0 && Num != null && Num > 0) {
				if (type.equals("2")) {
					if (uuID != null && !uuID.isEmpty()) {
						map.put("uuID", uuID);
						Userinfo userinfo = usermapper.Getuserinfo(map);
						result = cardService.checkUser(userinfo);
						// if (userinfo != null) {
						if (result.optBoolean("flag")) {
							Date nowdate = CommonDateParseUtil.date2date(new Date());
							Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
							if (nowdate.getTime() >= uuIDExpiry.getTime()) {
								result.put("code", "0004");
								result.put("message", "用户登录已过期，请重新登录！");
								return result.toString();
							} else {
								map.clear();
								map.put("operateId", userinfo.getId());
								map.put("operateType", 1);
							}
						} else {
							// result.put("code", "0003");
							// result.put("message", "暂无用户信息！");
							return result.toString();
						}
					} else {
						result.put("code", "0002");
						result.put("message", "参数不全！");
						return result.toString();
					}
				} else {
					if (device_token != null && !device_token.isEmpty() && mbSystemType > 0 && mbSystemType != null) {
						map.clear();
						map.put("device_token", device_token);
						map.put("mbSystemType", mbSystemType);
						map.put("operateId", 0);
						map.put("operateType", 0);
					} else {
						result.put("code", "0002");
						result.put("message", "参数不全！");
						return result.toString();
					}
				}
				map.put("messageType", messageType);
				map.put("start", (pageNum - 1) * Num);
				map.put("num", Num);
				List<Message> umList = usermapper.getMessage(map);
				map.put("isRead", 0);
				String num = usermapper.getMessageNum(map);
				if (messageType == 1) {
					messageType = 2;
				} else {
					messageType = 1;
				}
				map.put("messageType", messageType);
				String anothernum = usermapper.getMessageNum(map);
				if (umList != null && !umList.isEmpty()) {
					result.put("message", "查询消息列表成功！");
					data.put("umList", umList);
				} else {
					if (pageNum == 1) {
						result.put("message", "暂无消息！");
					} else {
						result.put("message", "已无更多消息！");
					}
					data.put("umList", "");
				}
				data.put("num", num);
				data.put("anothernum", anothernum);
				result.put("data", data);
				result.put("code", "0001");
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("查询消息列表失败", e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
		}
		return result.toString();
	}

	/*
	 * 修改用户消息 type : 1:单个已读；2：单个删除；3：全部设为已读；4：清空
	 * 
	 * 登录与否 loginType 1: 登录状态；2：未登录状态
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("operateMessage")
	public String operateMessage(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		String loginType = jsonobject.optString("loginType");
		Integer id = jsonobject.optInt("id");
		Integer messageType = jsonobject.optInt("messageType");
		String device_token = jsonobject.optString("device_token");
		Integer mbSystemType = jsonobject.optInt("mbSystemType");
		Map<String, Object> map = new HashMap<String, Object>();
		boolean flag = false;
		try {
			if (type != null && !type.isEmpty() && loginType != null && !loginType.isEmpty()) {
				if (loginType.equals("1")) {// 登录状态
					if (uuID != null && !uuID.isEmpty()) {
						map.put("uuID", uuID);
						Userinfo userinfo = usermapper.Getuserinfo(map);
						result = cardService.checkUser(userinfo);
						if (!result.optBoolean("flag")) {
							return result.toString();
						}
						if (userinfo != null) {
							Date nowdate = CommonDateParseUtil.date2date(new Date());
							Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
							if (nowdate.getTime() >= uuIDExpiry.getTime()) {
								result.put("code", "0004");
								result.put("message", "用户登录已过期，请重新登录！");
								return result.toString();
							} else {
								map.clear();
								map.put("operateId", userinfo.getId());
								map.put("operateType", 1);
							}
						} else {
							result.put("code", "0003");
							result.put("message", "暂无用户信息！");
						}
					} else {
						result.put("code", "0002");
						result.put("message", "参数不全！");
						return result.toString();
					}
				} else {
					if (device_token != null && !device_token.isEmpty() && mbSystemType != null && mbSystemType > 0) {
						map.clear();
						map.put("operateId", 0);
						map.put("operateType", 0);
						map.put("device_token", device_token);
						map.put("mbSystemType", mbSystemType);
					} else {
						flag = true;
					}
				}
				if (type.equals("1")) {
					if (id != null && id > 0) {
						map.put("id", id);
						map.put("isRead", 1);
					} else {
						flag = true;
					}
				} else if (type.equals("2")) {
					if (id != null && id > 0) {
						map.put("id", id);
						map.put("isDel", 1);
					} else {
						flag = true;
					}
				} else if (type.equals("3")) {
					if (messageType != null && messageType > 0) {
						map.put("messageType", messageType);
						map.put("isRead", 1);
					} else {
						flag = true;
					}
				} else {
					if (messageType != null && messageType > 0) {
						map.put("messageType", messageType);
						map.put("isDel", 1);
					} else {
						flag = true;
					}
				}
				if (flag) {
					result.put("code", "0002");
					result.put("message", "参数不全！");
				} else {
					usermapper.updateMessage(map);
					result.put("code", "0001");
					result.put("message", "操作成功！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("操作消息失败", e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 
	 * 获取蓝牙基站对应的语音路径
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getBlueTooth")
	public String getBlueTooth(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String deviceNo = jsonobject.optString("deviceNo");
		Map<String, Object> map = new HashMap<String, Object>();
		if (deviceNo != null && !deviceNo.isEmpty()) {
			try {
				map.put("deviceNo", deviceNo);
				map.put("deviceType", 1);
				map.put("state", 1);
				Guide guide = scenicMapper.getGuideUrl(map);
				if (guide != null) {
					map.clear();
					map.put("scenicId", guide.getScenicId());
					Scenic s = scenicMapper.getscenic(map);
					if (s != null) {
						guide.setFileUrl(scenicFileUrl + s.getFileCode() + "/Attract/" + guide.getFileCode() + "/"
								+ guide.getFileUrl());
					}
					result.put("data", guide);
				} else {
					result.put("data", "");
				}
				result.put("code", "0001");
				result.put("message", "操作成功！");
			} catch (Exception e) {
				log.error("操作消息失败", e);
				result.put("code", "0000");
				result.put("message", "平台繁忙，请稍候再试！");
			}
		} else {
			result.put("code", "0002");
			result.put("message", "参数不全！");
		}
		return result.toString();
	}

	/*
	 * app版本校验 mbSystemType 1:IOS ;2:Andriod
	 * 
	 * operateType 1:游乐App角色；
	 */
	@POST
	@Path("getVersion")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getVersion(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		Integer mbSystemType = jsonobject.optInt("mbSystemType");
		Integer operateType = jsonobject.optInt("operateType");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (mbSystemType != null && mbSystemType > 0 && operateType != null && operateType > 0) {
				map.clear();
				map.put("mbSystemType", mbSystemType);
				map.put("operateType", operateType);
				List<Version> list = scenicMapper.getVersion(map);
				if (list != null && !list.isEmpty()) {
					result.put("data", list.get(0));
				} else {
					result.put("data", "");
				}
				result.put("code", "0001");
				result.put("message", "操作成功！");
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("APP版本校验失败：", e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

	/*
	 * 获取用户消息总数type 1:未登录；2：已登录
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getMessageNum")
	public String getMessageNum(String json) {
		JSONObject result = new JSONObject();
		JSONObject data = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		String device_token = jsonobject.optString("device_token");
		// Integer messageType = jsonobject.optInt("messageType");
		Integer mbSystemType = jsonobject.optInt("mbSystemType");
		Map<String, Object> map = new HashMap<String, Object>();
		// if (messageType != null && messageType > 0 ) {
		try {
			if (type.equals("2")) {
				if (uuID != null && !uuID.isEmpty()) {
					map.put("uuID", uuID);
					Userinfo userinfo = usermapper.Getuserinfo(map);
					result = cardService.checkUser(userinfo);
					// if (userinfo != null) {
					if (result.optBoolean("flag")) {
						Date nowdate = CommonDateParseUtil.date2date(new Date());
						Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
						if (nowdate.getTime() >= uuIDExpiry.getTime()) {
							result.put("code", "0004");
							result.put("message", "用户登录已过期，请重新登录！");
							return result.toString();
						} else {
							map.clear();
							map.put("operateId", userinfo.getId());
							map.put("operateType", 1);
						}
					} else {
						// result.put("code", "0003");
						// result.put("message", "暂无用户信息！");
						return result.toString();
					}
				} else {
					result.put("code", "0002");
					result.put("message", "参数不全！");
					return result.toString();
				}
			} else {
				if (device_token != null && !device_token.isEmpty() && mbSystemType > 0 && mbSystemType != null) {
					map.clear();
					map.put("device_token", device_token);
					map.put("mbSystemType", mbSystemType);
					map.put("operateId", 0);
					map.put("operateType", 0);
				} else {
					result.put("code", "0002");
					result.put("message", "参数不全！");
					return result.toString();
				}
			}
			map.put("isRead", 0);
			String offReadTotalNum = usermapper.getMessageNum(map);
			map.put("messageType", 1);
			String offReadNum = usermapper.getMessageNum(map);
			data.put("offReadTotalNum", offReadTotalNum);
			data.put("offReadNum", offReadNum);
			// result.put("data", offReadTotalNum);
			result.put("data", data);
			result.put("code", "0001");
			result.put("message", "查询消息总数成功！");
		} catch (Exception e) {
			log.error("查询消息列表失败", e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
		}
		// } else {
		// result.put("code", "0002");
		// result.put("message", "参数不全！");
		// }
		return result.toString();
	}

	/*
	 * 
	 * 百度地图API调用: type :1 附近地点；2：关键字搜索（范围全国）
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getSearch")
	public String getSearch(String json) {
		JSONObject result = new JSONObject();
		JSONObject data = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String key = jsonobject.optString("key");
		String type = jsonobject.optString("type");
		String location = jsonobject.optString("location");
		Integer pageNum = jsonobject.optInt("pageNum");
		Integer num = jsonobject.optInt("num");
		String region = jsonobject.optString("region");
		Map<String, String> paramsMap = new LinkedHashMap<String, String>();
		if (type != null && !type.isEmpty() && pageNum != null && num != null) {
			try {
				if (type.equals("1")) {
					paramsMap.put("location", location);
					paramsMap.put("radius", "3000");
//					美食$酒店$购物$旅游景点$休闲娱乐$文化传媒$交通设施
					paramsMap.put("query", "美食$酒店$购物$旅游景点$休闲娱乐$文化传媒$交通设施");
				} else {
					paramsMap.put("region",region);
					paramsMap.put("query", key);
				}
				paramsMap.put("page_size", String.valueOf(num));
				paramsMap.put("page_num", String.valueOf(pageNum-1));
				paramsMap.put("ak", "3r6o0LH8SRfWiQ0VyglnPExlpMWa5bUr");
				paramsMap.put("scope", "1");
				paramsMap.put("output", "json");
				data  = HttpUtils.httpGet(paramsMap);
				if(data.optJSONArray("results").isEmpty()){
					result.put("data","");
				}else{
					result.put("data", data.optJSONArray("results"));
					if(data.optInt("total")==0){
						result.put("data","");
					}
				}
				result.put("code", "0001");
				result.put("message", "操作成功！");
			} catch (Exception e) {
				log.error("获取信息失败", e);
				result.put("code", "0000");
				result.put("message", "平台繁忙，请稍候再试！");
			}
		} else {
			result.put("code", "0002");
			result.put("message", "参数不全！");
		}
		return result.toString();
	}

}
