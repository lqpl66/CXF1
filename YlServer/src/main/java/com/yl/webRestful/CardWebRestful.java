package com.yl.webRestful;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.yl.Utils.CodeUtils;
import com.yl.Utils.CommonDateParseUtil;
import com.yl.Utils.GetProperties;
import com.yl.Utils.LineUtils;
import com.yl.Utils.MD5Utils;
import com.yl.beans.AttractsInfo;
import com.yl.beans.AttractsLine;
import com.yl.beans.CardExpand;
import com.yl.beans.CardOrder;
import com.yl.beans.GoodsOrder;
import com.yl.beans.LineLog;
import com.yl.beans.LinePrice;
import com.yl.beans.LinePriceDetails;
import com.yl.beans.LineTime;
import com.yl.beans.Order;
import com.yl.beans.Scenic;
import com.yl.beans.ScenicCardType;
import com.yl.beans.UserCard;
import com.yl.beans.Userinfo;
import com.yl.mapper.CardMapper;
import com.yl.mapper.ExpenseMapper;
import com.yl.mapper.ScenicMapper;
import com.yl.mapper.UserMapper;
import com.yl.service.CardService;
import com.yl.service.ConsumeService;
import com.yl.service.MessageService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*
 * 景区排队游乐卡接口
 */
@Component("cardwebRest")
public class CardWebRestful {
	@Autowired
	private ScenicMapper scenicMapper;
	@Autowired
	private CardMapper cardMapper;

	@Autowired
	private UserMapper usermapper;

	@Autowired
	private ExpenseMapper expenseMapper;
	@Autowired
	private MessageService messageService;
	@Autowired
	private ConsumeService consumeService;
	// 景区路径回显
	private String scenicImgUrl = GetProperties.getscenicImgUrl();
	@Autowired
	private CardService cardService;
	private static Logger log = Logger.getLogger(CardWebRestful.class);
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat df3 = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat df1 = new SimpleDateFormat("HH:mm:ss");

	/*
	 * 获取景点排队 type 1:登录状态；2：未登录状态
	 */
	@SuppressWarnings("finally")
	@POST
	@Path("getline")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public String getline(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		JSONObject data = new JSONObject();
		Integer scenicId = jsonobject.optInt("scenicId");
		String uuID = jsonobject.optString("uuID");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (scenicId != null && scenicId > 0) {
				// 更改普通排队的尚未使用但已超时的排队记录
				Calendar time = Calendar.getInstance();
				String nowTime = df.format(time.getTime());
				map.clear();
				map.put("nowTime", nowTime);
				map.put("state", 1);
				map.put("state1", 3);
				cardMapper.updateLineLog(map);
				if (uuID != null && !uuID.isEmpty()) {// 登录状态
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
							// 查询当前用户的绑定的可使用的腕带
							map.put("userId", userinfo.getId());
							List<UserCard> ucList = cardMapper.getuserCard(map);
							Integer userCardNum = 0;
							if (ucList != null && !ucList.isEmpty()) {
								userCardNum = ucList.size();
							} 
//							else {
//								result.put("code", "0005");
//								result.put("message", "该用户没有绑定腕带，无法排队！");
//								return result.toString();
//							}
							// data.put("userCardNum", userCardNum);
							// 获取当前用户的未使用的排队记录
							map.clear();
							map.put("scenicId", scenicId);
							map.put("state", 1);
							map.put("userId", userinfo.getId());
							map.put("isLine", 0);
							List<LineLog> userlineLogList = cardMapper.getlineLog(map);
							List<Integer> attractsIdList = new ArrayList<Integer>();
							if (userlineLogList != null && !userlineLogList.isEmpty()) {
								for (int i = 0; i < userlineLogList.size(); i++) {
									if (userlineLogList.get(i).getLineLogId() == null) {// 没有排队预约记录的就没有linelogId
										userlineLogList.remove(i);
									} else {
										map.put("attractsId", userlineLogList.get(i).getAttractsId());
										attractsIdList.add(userlineLogList.get(i).getAttractsId());
										// 查出当前用户每个景点的已使用排队使用记录总数
										map.put("lineLogId", userlineLogList.get(i).getLineLogId());
										Integer useTotalNum = cardMapper.getlineUseLogNum(map);
										userlineLogList.get(i)
												.setRemindNum(userlineLogList.get(i).getNumber() - useTotalNum);
									}
								}
								if (userlineLogList != null && !userlineLogList.isEmpty()) {
									data.put("lineLogList", userlineLogList);
								} else {
									data.put("lineLogList", "");
								}
							} else {
								data.put("lineLogList", "");
							}
							// 获取可以排队的景点数组,去除已在排队中的景点
							map.remove("userId");
							map.remove("attractsId");
							if (attractsIdList != null && !attractsIdList.isEmpty()) {
								map.put("attractsIdList", attractsIdList);
							}
							List<AttractsLine> alineList = cardMapper.getattractsLine(map);
							if (alineList != null && !alineList.isEmpty()) {
								for (AttractsLine al : alineList) {
									al.setLimitNum(userCardNum);
									// 查出每个景点的所有的排队记录
									map.clear();
									map.put("scenicId", scenicId);
									map.put("state", 1);
									map.put("isLine", 0);
									map.put("attractsId", al.getAttractsId());
									List<LineLog> totallineLogList = cardMapper.getlineLog(map);
									Integer useTotalNum = 0;
									// 计算出该景点已使用记录的总数
									for (LineLog ll : totallineLogList) {
										map.clear();
										map.put("lineLogId", ll.getLineLogId());
										useTotalNum = useTotalNum + cardMapper.getlineUseLogNum(map);
									}
									// 计算出该景点未使用的总数
									Integer totalNum = cardMapper.getlineLogNum(map);
									if (totalNum == 0) {
										al.setTotalNum(0);
									} else {
										al.setTotalNum(totalNum - useTotalNum);
									}
									al.setMarkedWords("现在排队预计等待"
											+ (int) Math
													.ceil(((double) al.getTotalNum() * al.getAvgTime()) / ((double) 60))
											+ "分钟");
								}
								data.put("alineList", alineList);
							} else {
								data.put("alineList", "");
							}
							result.put("data", data);
							result.put("code", "0001");
							result.put("message", "操作成功！");
						}
					} else {
						return result.toString();
					}
				} else {// 未登录状态
						// 获取可以排队的景点数组
					map.clear();
					map.put("scenicId", scenicId);
					map.put("state", 1);
					map.put("isLine", 0);
					List<AttractsLine> alineList = cardMapper.getattractsLine(map);
					if (alineList != null && !alineList.isEmpty()) {
						for (AttractsLine al : alineList) {
							al.setLimitNum(0);
							// 查出每个景点的所有的排队记录
							map.clear();
							map.put("scenicId", scenicId);
							map.put("state", 1);
							map.put("isLine", 0);
							map.put("attractsId", al.getAttractsId());
							List<LineLog> totallineLogList = cardMapper.getlineLog(map);
							Integer useTotalNum = 0;
							// 计算出该景点已使用记录的总数
							for (LineLog ll : totallineLogList) {
								map.clear();
								map.put("lineLogId", ll.getLineLogId());
								useTotalNum = useTotalNum + cardMapper.getlineUseLogNum(map);
							}
							// 计算出该景点未使用的总数
							Integer totalNum = cardMapper.getlineLogNum(map);
							if (totalNum == 0) {
								al.setTotalNum(0);
							} else {
								al.setTotalNum(totalNum - useTotalNum);
							}
							al.setMarkedWords("现在排队预计等待"
									+ (int) Math.ceil(((double) al.getTotalNum() * al.getAvgTime()) / ((double) 60))
									+ "分钟");
						}
						data.put("alineList", alineList);
					} else {
						data.put("alineList", "");
					}
					data.put("lineLogList", "");
					data.put("userCardNum", 0);
					result.put("data", data);
					result.put("code", "0001");
					result.put("message", "操作成功！");
				}

			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取排队景点列表失败：", e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 景点排队操作type : 1:添加排隊；2：取消排队
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Path("operateLine")
	@Produces({ MediaType.APPLICATION_JSON })
	public String operateLine(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		Integer attractsId = jsonobject.optInt("attractsId");
		Integer number = jsonobject.optInt("number");
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		Integer lineLogId = jsonobject.optInt("lineLogId");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (attractsId != null && attractsId > 0 && uuID != null && !uuID.isEmpty() && type != null
					&& !type.isEmpty()) {
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
						Integer userCardNum = 0;
						Integer totalNum = 0;
						// 预约排队
						if (type.equals("1")) {
							//
							if(number != null && number > 0){
								map.put("id", attractsId);
								AttractsInfo a = scenicMapper.getattractsInfoList(map);
								if (a == null) {
									result.put("code", "0010");
									result.put("message", "该景点不存在，无法排队！");
									return result.toString();
								}
								// 查询当前用户的绑定的可使用的腕带
								map.clear();
								map.put("userId", userinfo.getId());
								map.put("scenicId", a.getScenicId());
								List<UserCard> ucList = cardMapper.getuserCard(map);
								if (ucList != null && !ucList.isEmpty()) {
									userCardNum = ucList.size();
								} else {
									result.put("code", "0005");
									result.put("message", "用户没有绑定当前景区腕带，无法排队！");
									return result.toString();
								}
								// 更改普通排队的尚未使用但已超时的排队记录
								Calendar time = Calendar.getInstance();
								String nowTime = df.format(time.getTime());
								map.clear();
								map.put("nowTime", nowTime);
								map.put("state", 1);
								map.put("state1", 3);
								cardMapper.updateLineLog(map);
								// 获取当前用户是否预约该景点，一个景点只能预约一次
								map.clear();
								map.put("attractsId", attractsId);
								map.put("state", 1);
								map.put("userId", userinfo.getId());
								map.put("isLine", 0);
								List<LineLog> userll = cardMapper.getlineLog(map);
								List<AttractsLine> aline = cardMapper.getattractsLine(map);
								if (aline == null || aline.isEmpty()) {
									result.put("code", "0007");
									result.put("message", "该景点没有开启排队功能，无法排队！");
									return result.toString();
								}
								if (userll != null && !userll.isEmpty()) {
									result.put("code", "0008");
									result.put("message", "该景点已经预约，无法排队！");
									return result.toString();
								}
								boolean flag = false;
								if (number <= userCardNum) {
									flag = true;
								}
								if (flag) {
									String addTime = df.format(time.getTime());
									// 获取当前该景点的未使用的排队记录
									map.clear();
									map.put("attractsId", attractsId);
									map.put("state", 1);
									map.put("isLine", 0);
									Integer LineTotalNum = cardMapper.getlineLogNum(map);
									List<LineLog> totalll = cardMapper.getlineLog(map);
									Integer useTotalNum = 0;
									// 计算出该景点已使用记录的总数
									for (LineLog ll : totalll) {
										map.clear();
										map.put("lineLogId", ll.getLineLogId());
										useTotalNum = useTotalNum + cardMapper.getlineUseLogNum(map);
									}
									// 获取当前该景点的未使用的VIP排队记录
									// map.clear();
									// map.put("attractsId", attractsId);
									// map.put("state", 1);
									// map.put("isVIPLine", 0);
									// map.put("arriveEndTime",
									// df1.format(time.getTime()));
									// Integer VIPLineTotalNum =
									// cardMapper.getlineLogNum(map);
									// 获取当前景点的线下人数
									map.clear();
									map.put("attractsId", attractsId);
									map.put("state", 1);
									map.put("isVIPLine", 0);
									map.put("isLine", 0);
									List<AttractsLine> alineList = cardMapper.getattractsLine(map);
									totalNum = alineList.get(0).getLineUserCount() + LineTotalNum - useTotalNum;
									// 计算排队时间
									List<String> s = LineUtils.getarriveStartTime(totalNum, alineList.get(0).getAvgTime());
									// 添加排队记录
									map.clear();
									map.put("userId", userinfo.getId());
									map.put("attractsId", attractsId);
									map.put("number", number);
									map.put("arriveStartTime", s.get(0));
									map.put("arriveEndTime", s.get(1));
									map.put("state", 1);
									map.put("addTime", addTime);
									cardMapper.saveLineLog(map);
								} else {
									result.put("code", "0006");
									result.put("message", "排队数量已超过绑定腕带个数，无法排队！");
									return result.toString();
								}	
							}else{
								result.put("code", "0002");
								result.put("message", "参数不全！");
								return result.toString();
							}
						} else if (type.equals("2")) {
							if (lineLogId != null && lineLogId > 0) {
								// 取消排队
								map.clear();
								map.put("state1", 2);
								map.put("userId", userinfo.getId());
								map.put("attractsId", attractsId);
								map.put("lineLogId", lineLogId);
								cardMapper.updateLineLog(map);
							} else {
								result.put("code", "0002");
								result.put("message", "参数不全！");
								return result.toString();
							}
						}
						result.put("code", "0001");
						result.put("message", "操作成功！");
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取排队景点列表失败：", e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 获取VIP景点排队 type 1:默认获取当前时间；2：时间段的切换
	 */
	@SuppressWarnings("finally")
	@POST
	@Path("getVIPline")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public String getVIPline(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		JSONObject data = new JSONObject();
		Integer scenicId = jsonobject.optInt("scenicId");
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		Integer lineTimeId = jsonobject.optInt("lineTimeId");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (type != null && !type.isEmpty()) {
				Calendar time = Calendar.getInstance();
				String lineDate = df3.format(time.getTime());
				String nowTime = df.format(time.getTime());
				String nowTime1 = df1.format(time.getTime());
				// 更改VIP排队的尚未使用但已超时的排队记录
				map.clear();
				map.put("nowTime", nowTime);
				map.put("state", 1);
				map.put("state1", 3);
				map.put("typeId", 1);
				map.put("amount", 1);
				cardMapper.updateLineLog(map);
				if (uuID != null && !uuID.isEmpty()) {// 登录状态
					map.put("uuID", uuID);
					Userinfo userinfo = usermapper.Getuserinfo(map);
					result = cardService.checkUser(userinfo);
					if (result.optBoolean("flag")) {
						Date nowdate = CommonDateParseUtil.date2date(new Date());
						Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
						if (nowdate.getTime() >= uuIDExpiry.getTime()) {
							result.put("code", "0004");
							result.put("message", "用户登录已过期，请重新登录！");
							return result.toString();
						} else {
							List<LineTime> vNowLinetimeList = new ArrayList<LineTime>();
							// 获取可以VIP排队的所有的时间段
							map.clear();
							map.put("scenicId", scenicId);
							map.put("lineDate", lineDate);
							List<LineTime> vLinetimeList = cardMapper.getlineTime(map);
							if (type.equals("1") && scenicId != null && scenicId > 0) {
								// 获取当前VIP排队时间段
								map.put("nowTime", nowTime1);
								// vNowLinetimeList =
								// cardMapper.getlineTime(map);
							} else if (type.equals("2") && lineTimeId != null && lineTimeId > 0) {
								map.put("lineTimeId", lineTimeId);
							} else {
								result.put("code", "0002");
								result.put("message", "参数不全！");
								return result.toString();
							}
							vNowLinetimeList = cardMapper.getlineTime(map);
							// 时间段列表的选中的时间段赋值
							if (vLinetimeList != null && !vLinetimeList.isEmpty()) {
								for (LineTime lt : vLinetimeList) {
									lt.setType(0);
									if (vNowLinetimeList != null && !vNowLinetimeList.isEmpty()
											&& lt.getLineTimeId() == vNowLinetimeList.get(0).getLineTimeId()) {
										lt.setType(1);
									}
								}
								data.put("vLinetimeList", vLinetimeList);
							} else {
								data.put("vLinetimeList", "");
							}
							if (vNowLinetimeList != null && !vNowLinetimeList.isEmpty()) {
								// 获取当前用户的所有的的VIP排队记录
								map.clear();
								map.put("scenicId", scenicId);
								map.put("userId", userinfo.getId());
								map.put("isVIPLine", 0);
								map.put("typeId", 1);
								map.put("amount", 1);
								map.put("startTime", vNowLinetimeList.get(0).getLineDate() + " "
										+ vNowLinetimeList.get(0).getStartTime());
								map.put("endTime", vNowLinetimeList.get(0).getLineDate() + " "
										+ vNowLinetimeList.get(0).getEndTime());
								List<LineLog> lineLogList = cardMapper.getlineLog(map);
								if (lineLogList != null && !lineLogList.isEmpty()) {
									for (int i = 0; i < lineLogList.size(); i++) {
										if (lineLogList.get(i).getLineLogId() == null) {
											lineLogList.remove(i);
										} else {
											// 计算出该景点已使用记录的总数
											map.clear();
											map.put("lineLogId", lineLogList.get(i).getLineLogId());
											Integer useTotalNum = cardMapper.getlineUseLogNum(map);
											lineLogList.get(i).setUsedNum(useTotalNum);
										}
									}
									if (lineLogList != null && !lineLogList.isEmpty()) {
										data.put("lineLogList", lineLogList);
									} else {
										data.put("lineLogList", "");
									}
								} else {
									data.put("lineLogList", "");
								}
								map.clear();
								map.put("lineTimeId", vNowLinetimeList.get(0).getLineTimeId());
								map.put("isVIPLine", 0);
								// 当前VIP排队时间段内的景点列表
								List<LinePrice> attractsLineList = cardMapper.getlinePrice(map);
								if (attractsLineList != null && !attractsLineList.isEmpty()) {
									map.put("scenicId", attractsLineList.get(0).getScenicId());
									Scenic s = scenicMapper.getscenic(map);
									// 设置景点VIP的剩余张数
									for (LinePrice lp : attractsLineList) {
										if (s != null) {
											lp.setScenicName(s.getScenicName());
										}
										// 当前用户最大预定张数
										lp.setLimitNum(4);
										Integer totalusedNum = 0;
										if (lineLogList != null && !lineLogList.isEmpty()) {
											for (LineLog ll : lineLogList) {
												if (ll.getAttractsId() == lp.getAttractsId()
														&& ll.getLinepriceId() == lp.getLinepriceId()) {
													totalusedNum = totalusedNum + ll.getNumber() - ll.getUsedNum();
												}
											}
										}
										lp.setLimitNum(4 - totalusedNum);
										// 判断该时间段是否过期1：无法购买；2：可以购买
										Date dt1 = df.parse(vNowLinetimeList.get(0).getLineDate() + " "
												+ vNowLinetimeList.get(0).getEndTime() + ":00");
										Date dt2 = new Date();
										if (dt1.getTime() < dt2.getTime()) {
											lp.setIsBuy(1);
										} else {
											lp.setIsBuy(2);
										}
										// 获取该景点的所有的VIP排队购买记录
										map.put("typeId", 1);
										map.put("amount", 1);
										map.put("userId", userinfo.getId());
										map.put("attractsId", lp.getAttractsId());
										map.put("linepriceId", lp.getLinepriceId());
										map.put("isVIPLine", 0);
										Integer num2 = cardMapper.getlineLogNum(map);
										lp.setPollNumber(lp.getPollNumber() - num2);
									}
									data.put("attractsLineList", attractsLineList);
								} else {
									data.put("attractsLineList", "");
								}
							} else {
								data.put("attractsLineList", "");
								data.put("lineLogList", "");
							}
							result.put("data", data);
							result.put("code", "0001");
							result.put("message", "操作成功！");
						}
					} else {
						return result.toString();
					}
				} else {// 未登录状态
					List<LineTime> vNowLinetimeList = new ArrayList<LineTime>();
					// 获取可以VIP排队的所有的时间段
					map.clear();
					map.put("scenicId", scenicId);
					map.put("lineDate", lineDate);
					List<LineTime> vLinetimeList = cardMapper.getlineTime(map);
					if (type.equals("1") && scenicId != null && scenicId > 0) {
						// 获取当前VIP排队时间段
						map.put("nowTime", nowTime1);
						// vNowLinetimeList = cardMapper.getlineTime(map);
					} else if (type.equals("2") && lineTimeId != null && lineTimeId > 0) {
						map.put("lineTimeId", lineTimeId);
					} else {
						result.put("code", "0002");
						result.put("message", "参数不全！");
						return result.toString();
					}
					vNowLinetimeList = cardMapper.getlineTime(map);
					// 时间段列表的选中的时间段赋值
					if (vLinetimeList != null && !vLinetimeList.isEmpty()) {
						for (LineTime lt : vLinetimeList) {
							lt.setType(0);
							if (vNowLinetimeList != null && !vNowLinetimeList.isEmpty()
									&& lt.getLineTimeId() == vNowLinetimeList.get(0).getLineTimeId()) {
								lt.setType(1);
							}
						}
						data.put("vLinetimeList", vLinetimeList);
					} else {
						data.put("vLinetimeList", "");
					}
					if (vNowLinetimeList != null && !vNowLinetimeList.isEmpty()) {
						map.clear();
						map.put("lineTimeId", vNowLinetimeList.get(0).getLineTimeId());
						map.put("isVIPLine", 0);
						// 当前VIP排队时间段内的景点列表
						List<LinePrice> attractsLineList = cardMapper.getlinePrice(map);
						if (attractsLineList != null && !attractsLineList.isEmpty()) {
							map.put("scenicId", attractsLineList.get(0).getScenicId());
							Scenic s = scenicMapper.getscenic(map);
							// 设置景点VIP的剩余张数
							for (LinePrice lp : attractsLineList) {
								if (s != null) {
									lp.setScenicName(s.getScenicName());
								}
								// 当前用户最大预定张数
								lp.setLimitNum(4);
								lp.setLimitNum(0);
								// 判断该时间段是否过期1：无法购买；2：可以购买
								Date dt1 = df.parse(vNowLinetimeList.get(0).getLineDate() + " "
										+ vNowLinetimeList.get(0).getEndTime() + ":00");
								Date dt2 = new Date();
								if (dt1.getTime() < dt2.getTime()) {
									lp.setIsBuy(1);
								} else {
									lp.setIsBuy(2);
								}
								// 获取该景点的所有的VIP排队购买记录
								map.put("typeId", 1);
								map.put("amount", 1);
								map.put("attractsId", lp.getAttractsId());
								map.put("linepriceId", lp.getLinepriceId());
								map.put("isVIPLine", 0);
								Integer num2 = cardMapper.getlineLogNum(map);
								lp.setPollNumber(lp.getPollNumber() - num2);
							}
							data.put("attractsLineList", attractsLineList);
						} else {
							data.put("attractsLineList", "");
						}
					} else {
						data.put("attractsLineList", "");
						data.put("lineLogList", "");
					}
					result.put("data", data);
					result.put("code", "0001");
					result.put("message", "操作成功！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取VIP排队景点列表失败：", e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 获取VIP的详情
	 */
	@SuppressWarnings("finally")
	@POST
	@Path("getVIPlineDetails")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public String getVIPlineDetails(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		JSONObject data = new JSONObject();
		// Integer scenicId = jsonobject.optInt("scenicId");
		String uuID = jsonobject.optString("uuID");
		Integer lineLogId = jsonobject.optInt("lineLogId");
		// Integer lineTimeId = jsonobject.optInt("lineTimeId");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && lineLogId != null && lineLogId > 0) {
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
						Calendar time = Calendar.getInstance();
						String nowTime = df.format(time.getTime());
						// 更改VIP排队的尚未使用但已超时的排队记录
						map.clear();
						map.put("nowTime", nowTime);
						map.put("state", 1);
						map.put("state1", 3);
						map.put("typeId", 1);
						map.put("amount", 1);
						cardMapper.updateLineLog(map);
						// 获取当前VIP记录的详情
						map.clear();
						map.put("userId", userinfo.getId());
						map.put("isVIPLine", 0);
						map.put("typeId", 1);
						map.put("amount", 1);
						map.put("id", lineLogId);
						List<LineLog> lineLogList = cardMapper.getlineLog(map);
						if (lineLogList != null && !lineLogList.isEmpty()) {
							// 查询使用记录
							map.clear();
							map.put("userId", userinfo.getId());
							map.put("lineLogId", lineLogId);
							Integer usedNum = cardMapper.getlineUseLogNum(map);
							lineLogList.get(0).setUsedNum(usedNum);
							result.put("data", lineLogList.get(0));
						} else {
							result.put("data", "");
						}
						result.put("code", "0001");
						result.put("message", "操作成功！");
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取VIP排队详情失败：", e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 景点VIP排队操作
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Path("operateVIPLine")
	@Produces({ MediaType.APPLICATION_JSON })
	public String operateVIPLine(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		Integer attractsId = jsonobject.optInt("attractsId");
		Integer number = jsonobject.optInt("number");
		String uuID = jsonobject.optString("uuID");
		String payPwd = jsonobject.optString("payPwd");
		Integer linepriceId = jsonobject.optInt("linepriceId");
		Double amount = jsonobject.optDouble("amount");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (attractsId != null && attractsId > 0 && uuID != null && !uuID.isEmpty() && number != null && number > 0
					&& amount != null  && linepriceId != null && linepriceId > 0) {
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
						Calendar time = Calendar.getInstance();
						String nowTime = df.format(time.getTime());
						Date dt2 = df.parse(nowTime);
						// 查询该时间段费用记录详情
						map.clear();
						map.put("linepriceId", linepriceId);
						map.put("attractsId", attractsId);
						LinePriceDetails lpd = cardMapper.getlinePriceDetails(map);
						if (lpd == null) {
							result.put("code", "0008");
							result.put("message", "该景点没有VIP预约，无法预约！");
							return result.toString();
						}
						String endTime = lpd.getLineDate() + " " + lpd.getEndTime();
						String startTime = lpd.getLineDate() + " " + lpd.getStartTime();
						Date dt1 = df.parse(endTime);
						if (dt1.getTime() < dt2.getTime()) {
							result.put("code", "0007");
							result.put("message", "预约VIP时间段已过期，无法预约！");
							return result.toString();
						}
						// 判断用户是否可以购买VIP
						map.clear();
						map.put("userId", userinfo.getId());
						map.put("isVIPLine", 0);
						map.put("typeId", 1);
						map.put("amount", 1);
						map.put("state", 1);
						map.put("linepriceId", linepriceId);
						List<LineLog> userlineLogList = cardMapper.getlineLog(map);
						Integer useTotalNum = 0;
						Integer totalNum = 0;
						if (userlineLogList != null && !userlineLogList.isEmpty()) {
							for (LineLog ull : userlineLogList) {
								totalNum = totalNum + ull.getNumber();
								map.clear();
								map.put("lineLogId", ull.getLineLogId());
								useTotalNum = useTotalNum + cardMapper.getlineUseLogNum(map);
							}
						}
						Integer remindNum = totalNum - useTotalNum;
						if (remindNum > 0 && number > (4 - remindNum)) {
							result.put("code", "0006");
							result.put("message", "VIP预约数量已超过个人限制数量，无法预约！");
							return result.toString();
						}
						// 判断余票是否充足
						map.remove("userId");
						map.remove("state");
						Integer allTotalNum = cardMapper.getlineLogNum(map);
						if (lpd.getPollNumber() < allTotalNum) {
							result.put("code", "0006");
							result.put("message", "VIP数量不足，无法预约！");
							return result.toString();
						}
						BigDecimal dd = new BigDecimal(number * amount);
						dd = dd.setScale(2, BigDecimal.ROUND_HALF_DOWN);
						if (dd.compareTo(new BigDecimal("0.00")) == 1) {// VIP付费
							if (userinfo.getStatus() == 2) {
								result.put("code", "0006");
								result.put("message", "账户冻结，无法支付！");
								return result.toString();
							}
							// 判断是否开启0:关闭；1：开启// 免付金额小于支付金额
							if (userinfo.getIsOpen() == 1 && (userinfo.getMinAmount().compareTo(dd) == -1
									|| userinfo.getMinAmount().compareTo(dd) == 0)) {
								if (payPwd == null || payPwd.isEmpty()) {
									result.put("code", "0002");
									result.put("message", "参数不全！");
									return result.toString();
								} else {
									if (!userinfo.getPayPwd().equals(MD5Utils.string2MD5(payPwd))) {
										result.put("code", "0009");
										result.put("message", "支付密码不对！");
										return result.toString();
									}
								}
							}
							// 判断账户余额是否充足
							if (userinfo.getBalance().compareTo(dd) == -1) {
								result.put("code", "0009");
								result.put("message", "余额不足！");
								return result.toString();
							}
							String addTime = df.format(time.getTime());
							Order o = new Order();
							// String orderNo = CodeUtils
							// .getorderCode(userinfo.getUserCode().substring(userinfo.getUserCode().length()
							// - 6));
							String orderNo = consumeService.getOrderNo(userinfo.getUserCode());
							String tradeNo = CodeUtils.gettradeNo();
							o.setOrderNo(orderNo);
							o.setTradeNo(tradeNo);
							o.setAddTime(addTime);
							o.setUserId(userinfo.getId());
							o.setOrderType(1);
							o.setStatus(1);
							o.setGoodsAmount(dd);
							o.setPostageAmount(new BigDecimal("0.00"));
							o.setPayableAmount(dd);
							o.setPaymentAmount(dd);
							o.setIsDel(0);
							o.setDeductionAmount(new BigDecimal("0.00"));
							expenseMapper.saveOrder(o);
							// 生成订单，状态未支付
							map.clear();
							// 钱包扣费，成功之后,，生成用户消费流水，订单状态改为已完成同时生成平台交易流水
							map.put("userId", userinfo.getId());
							map.put("status", 1);
							BigDecimal balance = userinfo.getBalance().subtract(dd);
							balance = balance.setScale(2, BigDecimal.ROUND_HALF_DOWN);
							map.put("balance", balance);
							usermapper.updateUserAmount(map);
							// 用户消费流水
							map.clear();
							String expenseUserNo = CodeUtils.gettransactionFlowCode(
									userinfo.getUserCode().substring(userinfo.getUserCode().length() - 6));
							map.put("expenseUserNo", expenseUserNo);
							map.put("serialNo", expenseUserNo);
							map.put("userId", userinfo.getId());
							map.put("paymentAmount", new BigDecimal("-" + dd));
							map.put("useType", 1);
							map.put("paymentType", 3);
							map.put("expenseType", 3);
							map.put("sourceType", 1);
							map.put("addTime", df.format(time.getTime()));
							expenseMapper.saveExpenseUserlog(map);
							// 订单状态改为已完成
							map.clear();
							map.put("userId", userinfo.getId());
							map.put("expenseId", expenseUserNo);
							map.put("endTime", df.format(time.getTime()));
							map.put("status", 4);
							map.put("orderNo", orderNo);
							expenseMapper.updateOrder(map);
							// 积分回赠 sourceId 2:消费赠送积分
							BigDecimal divisor = new BigDecimal(10);
							Integer score = dd.divide(divisor).intValue();
							map.clear();
							map.put("userId", userinfo.getId());
							map.put("sourceId", 2);
							map.put("score", score);
							map.put("status", 1);
							String addTime1 = df.format(new Date());
							map.put("addTime", addTime1);
							usermapper.savescore(map);
							// 设置用户等级
							consumeService.updateLevel(userinfo);
						}
						// 添加VIP排队记录
						map.clear();
						map.put("userId", userinfo.getId());
						map.put("attractsId", attractsId);
						map.put("number", number);
						map.put("state", 1);
						map.put("typeId", 3);
						map.put("amount", amount);
						map.put("linepriceId", linepriceId);
						map.put("addTime", df.format(time.getTime()));
						map.put("arriveStartTime", lpd.getStartTime());
						map.put("arriveEndTime", lpd.getEndTime());
						cardMapper.saveLineLog(map);
						result.put("message", "操作成功！");
						result.put("code", "0001");
						result.put("blance", userinfo.getBalance().subtract(dd));
						// 插入消息
						messageService.insertUserMessage("1", userinfo, 3, 3, dd);
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("景点VIP排队操作失败：", e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 获取腕带的信息
	 * 
	 * type 1:初次加载；2：选择时间切换
	 */
	@POST
	@Path("getcard")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getcard(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		JSONObject data = new JSONObject();
		Integer scenicId = jsonobject.optInt("scenicId");
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		String date = jsonobject.optString("date");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (scenicId != null && scenicId > 0 && type != null && !type.isEmpty()) {
				if (uuID != null && !uuID.isEmpty()) {// 登录状态
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
							Integer userCardNum = 0;
							Integer expandNum = 0;
							Integer userLimitNum = 4;
							// 查出当前景区支持的腕带类型
							map.clear();
							map.put("scenicId", scenicId);
							if (type.equals("1")) {// 默认当前
								date = df3.format(new Date());
								map.put("date", date);
							} else {// 切换
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(df3.parse(date));
								Date c_now = df3.parse(date);
								String nowtime = df3.format(new Date());
								calendar.setTime(df3.parse(nowtime));
								Date c_Limit_down = calendar.getTime();
								calendar.add(Calendar.DATE, 15);
								Date c_Limit_up = calendar.getTime();
								if (c_now.compareTo(c_Limit_down) != -1 && c_now.compareTo(c_Limit_up) != 1) {
									map.put("date", date);
								} else {
									result.put("code", "0005");
									result.put("message", "选择购买时间不符！");
									return result.toString();
								}
							}
							List<ScenicCardType> typeList = cardMapper.getScenicCardType(map);
							if (typeList != null && !typeList.isEmpty()) {
								// 查出当前景区腕带类型还可以使用的数量
								for (ScenicCardType sct : typeList) {
									sct.setTotalPrice(sct.getPrice().add(sct.getUsePrice()));
									map.clear();
									map.put("scenicId", scenicId);
									map.put("type", "1");// 未使用的景区腕带
									map.put("cardType", sct.getCardType());
									List<Map> list = cardMapper.getCardNum(map);
									// 查询所用用户购买日期相同的未使用的腕带个数
									map.clear();
									map.put("scenicId", scenicId);
									map.put("appointment", date);
									map.put("status", 1);
									map.put("cardType", sct.getCardType());
									List<CardExpand> cardExpandList1 = cardMapper.getCardExpand(map);
									Integer cardExpandAllNum = 0;
									if (cardExpandList1 != null && !cardExpandList1.isEmpty()) {
										for (CardExpand ce : cardExpandList1) {
											cardExpandAllNum = cardExpandAllNum + ce.getCardNum() - ce.getCardUseNum();
										}
									}
									if (list != null && !list.isEmpty()) {
										sct.setRemindNum(list.size() - cardExpandAllNum);
									}
								}
								// 当前用户绑定的腕带个数
								if (type.equals("1")) {
									map.clear();
									map.put("scenicId", scenicId);
									map.put("userId", userinfo.getId());// 未使用的景区腕带
									List<Map> list1 = cardMapper.getCardNum(map);
									if (list1 != null && !list1.isEmpty()) {
										userCardNum = list1.size();
									}
								}
								// 查询购买日期相同
								map.clear();
								map.put("scenicId", scenicId);
								map.put("appointment", date);
								map.put("status", 1);
								map.put("userId", userinfo.getId());
								List<CardExpand> cardExpandList = cardMapper.getCardExpand(map);
								if (cardExpandList != null && !cardExpandList.isEmpty()) {
									for (CardExpand ce : cardExpandList) {
										expandNum = expandNum + ce.getCardNum() - ce.getCardUseNum();
									}
								}
								userLimitNum = userLimitNum - userCardNum - expandNum;
								data.put("userLimitNum", String.valueOf(userLimitNum));
								data.put("cardTypeList", typeList);
								data.put("DayLimitNum", "15");
								result.put("data", data);
								result.put("code", "0001");
								result.put("message", "操作成功！");
							} else {
								data.put("DayLimitNum", "15");
								result.put("data", data);
								result.put("code", "0006");
								result.put("message", "当前日期暂未开放腕带！");
								return result.toString();
							}
						}
					} else {
						return result.toString();
					}
				} else {// 未登录状态
					Integer userCardNum = 0;
					Integer expandNum = 0;
					Integer userLimitNum = 4;
					// 查出当前景区支持的腕带类型
					map.clear();
					map.put("scenicId", scenicId);
					if (type.equals("1")) {// 默认当前
						date = df3.format(new Date());
						map.put("date", date);
					} else {// 切换
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(df3.parse(date));
						Date c_now = df3.parse(date);
						String nowtime = df3.format(new Date());
						calendar.setTime(df3.parse(nowtime));
						Date c_Limit_down = calendar.getTime();
						calendar.add(Calendar.DATE, 15);
						Date c_Limit_up = calendar.getTime();
						if (c_now.compareTo(c_Limit_down) != -1 && c_now.compareTo(c_Limit_up) != 1) {
							map.put("date", date);
						} else {
							result.put("code", "0005");
							result.put("message", "选择购买时间不符！");
							return result.toString();
						}
					}
					List<ScenicCardType> typeList = cardMapper.getScenicCardType(map);
					if (typeList != null && !typeList.isEmpty()) {
						// 查出当前景区腕带类型还可以使用的数量
						for (ScenicCardType sct : typeList) {
							sct.setTotalPrice(sct.getPrice().add(sct.getUsePrice()));
							map.clear();
							map.put("scenicId", scenicId);
							map.put("type", "1");// 未使用的景区腕带
							map.put("cardType", sct.getCardType());
							List<Map> list = cardMapper.getCardNum(map);
							// 查询所用用户购买日期相同的未使用的腕带个数
							map.clear();
							map.put("scenicId", scenicId);
							map.put("appointment", date);
							map.put("status", 1);
							map.put("cardType", sct.getCardType());
							List<CardExpand> cardExpandList1 = cardMapper.getCardExpand(map);
							Integer cardExpandAllNum = 0;
							if (cardExpandList1 != null && !cardExpandList1.isEmpty()) {
								for (CardExpand ce : cardExpandList1) {
									cardExpandAllNum = cardExpandAllNum + ce.getCardNum() - ce.getCardUseNum();
								}
							}
							if (list != null && !list.isEmpty()) {
								sct.setRemindNum(list.size() - cardExpandAllNum);
							}
						}
						userLimitNum = userLimitNum - userCardNum - expandNum;
						data.put("userLimitNum", String.valueOf(userLimitNum));
						data.put("cardTypeList", typeList);
						data.put("DayLimitNum", "15");
						result.put("data", data);
						result.put("code", "0001");
						result.put("message", "操作成功！");
					} else {
						data.put("DayLimitNum", "15");
						result.put("data", data);
						result.put("code", "0006");
						result.put("message", "当前日期暂未开放腕带！");
						return result.toString();
					}
				}

			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取购买腕带信息失败：", e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

	/*
	 * 生成腕带的购买订单
	 * 
	 * 
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Path("cardOrder")
	@Produces({ MediaType.APPLICATION_JSON })
	public String cardOrder(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		JSONObject data = new JSONObject();
		Integer scenicId = jsonobject.optInt("scenicId");
		String uuID = jsonobject.optString("uuID");
		String date = jsonobject.optString("date");
		JSONArray list = jsonobject.getJSONArray("list");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (scenicId != null && scenicId > 0 && uuID != null && !uuID.isEmpty() && date != null && !date.isEmpty()
					&& list != null && !list.isEmpty()) {
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
					} else {// 首先校验可不可生成订单
						result = cardService.checkOrder(list, scenicId, date, userinfo.getId());
						if (result.getBoolean("flag")) {
							String tradeNo = CodeUtils.gettradeNo();
							BigDecimal totalPrice = new BigDecimal("0.00");
							Order o = consumeService.saveOrder(userinfo, tradeNo, 1, null, 7);
							for (int i = 0; i < list.size(); i++) {// 生成附属订单
								Integer num = list.optJSONObject(i).optInt("num");
								Integer cardType = list.optJSONObject(i).optInt("cardType");
								map.put("cardType", cardType);
								map.put("date", date);
								map.put("scenicId", scenicId);
								List<ScenicCardType> typeList = cardMapper.getScenicCardType(map);
								if (typeList != null && !typeList.isEmpty()) {
									typeList.get(0).setTotalPrice(
											typeList.get(0).getPrice().add(typeList.get(0).getUsePrice()));
									totalPrice = totalPrice
											.add(typeList.get(0).getTotalPrice().multiply(new BigDecimal(num)));
								}
								cardService.saveCardExpand(num, cardType, scenicId, o.getOrderNo(), date);
							}
							o.setDeductionAmount(new BigDecimal("0.00"));
							o.setGoodsAmount(totalPrice.setScale(2, BigDecimal.ROUND_UP));
							o.setPostageAmount(new BigDecimal("0.00"));
							o.setPayableAmount(totalPrice.setScale(2, BigDecimal.ROUND_UP));
							o.setPaymentAmount(totalPrice.setScale(2, BigDecimal.ROUND_UP));
							expenseMapper.saveOrder(o);
							data.put("tradeNo", o.getTradeNo());
							data.put("totalPrice", o.getPayableAmount());
							result.put("data", data);
							result.put("code", "0001");
							result.put("message", "腕带下单成功！");
						} else {
							return result.toString();
						}
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("购买腕带信息失败：", e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
		// return result.toString();
	}

	/*
	 * 订单列表 type:7：腕带
	 * 
	 */
	@POST
	@Path("getOrderList")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getOrderList(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		Integer pageNum = jsonobject.optInt("pageNum");
		Integer num = jsonobject.optInt("num");
		String orderNo = jsonobject.optString("orderNo");
		Map<String, Object> map = new HashMap<String, Object>();
		if (uuID != null && !uuID.isEmpty() && type != null && !type.isEmpty()) {
			try {
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
					} else {
						if (type.equals("7")) {// 腕带
							map.clear();
							map.put("userId", userinfo.getId());
							map.put("orderType", Integer.parseInt(type));
							if (orderNo != null && !orderNo.isEmpty()) {
								map.put("orderNo", orderNo);
							} else {
								if (pageNum != null && pageNum > 0 && num != null && num > 0) {
									map.put("start", (pageNum - 1) * num);
									map.put("num", num);
								} else {
									result.put("code", "0002");
									result.put("message", "参数不全！");
									return result.toString();
								}
							}
							List<Order> o = expenseMapper.getOrder(map);
							List<CardOrder> coList = new ArrayList<CardOrder>();
							JSONArray coListjson = new JSONArray();
							JSONObject cojson = new JSONObject();
							if (o != null && !o.isEmpty()) {// 腕带附属订单
								for (Order or : o) {
									CardOrder co = new CardOrder();
									map.clear();
									map.put("orderNo", or.getOrderNo());
									co.setOrderNo(or.getOrderNo());
									co.setPaymentAmount(or.getPaymentAmount());
									co.setStatus(or.getStatus());
									List<CardExpand> gceList = cardMapper.getCardExpand(map);
									if (gceList != null && !gceList.isEmpty()) {
										map.clear();
										map.put("scenicId", gceList.get(0).getScenicId());
										Scenic s = scenicMapper.getscenic(map);
										if (s != null) {
											co.setIntro(s.getIntro());
											co.setScenicId(s.getId());
											co.setScenicName(s.getScenicName());
											co.setScenicCoverImgUrl(
													scenicImgUrl + s.getFileCode() + "/" + s.getCoverImg());
										}
										cojson = JSONObject.fromObject(co);
										// JSONArray gceListjson = new
										// JSONArray();

										cojson.put("cardExpandList", JSONArray.fromObject(gceList));
									} else {
										cojson = JSONObject.fromObject(co);
										cojson.put("cardExpandList", "");
									}
									coListjson.add(cojson);
								}
								result.put("code", "0001");
								if (!coListjson.isEmpty()) {
									Collections.reverse(coListjson);
									result.put("data", coListjson);
								} else {
									result.put("data", "");
								}
							} else {
								if (pageNum == 1) {
									result.put("message", "暂无订单信息！");
								} else if (pageNum > 1) {
									result.put("message", "已无更多订单信息！");
								}
								result.put("code", "0006");
								return result.toString();
							}
						} else {
							result.put("code", "1000");
							result.put("message", "其他订单列表功能暂未开通！");
							return result.toString();
						}
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} catch (Exception e) {
				log.error("订单列表(type=" + type + "):", e);
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
	 * 订单详情 type:7：腕带
	 * 
	 */
	@POST
	@Path("getOrderDetails")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getOrderDetails(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		String orderNo = jsonobject.optString("orderNo");
		Map<String, Object> map = new HashMap<String, Object>();
		if (uuID != null && !uuID.isEmpty() && type != null && !type.isEmpty() && orderNo != null
				&& !orderNo.isEmpty()) {
			try {
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
					} else {
						if (type.equals("7")) {// 腕带
							map.clear();
							map.put("userId", userinfo.getId());
							map.put("orderType", Integer.parseInt(type));
							map.put("orderNo", orderNo);
							// JSONArray coListjson = new JSONArray();
							JSONObject cojson = new JSONObject();
							List<Order> o = expenseMapper.getOrder(map);
							if (o != null && !o.isEmpty()) {// 腕带附属订单
								CardOrder co = new CardOrder();
								map.clear();
								map.put("orderNo", o.get(0).getOrderNo());
								co.setOrderNo(o.get(0).getOrderNo());
								co.setPaymentAmount(o.get(0).getPaymentAmount());
								co.setStatus(o.get(0).getStatus());
								co.setAddTime(o.get(0).getAddTime());
								List<CardExpand> gceList = cardMapper.getCardExpand(map);
								if (gceList != null && !gceList.isEmpty()) {
									co.setAppointment(gceList.get(0).getAppointment());
									map.clear();
									map.put("scenicId", gceList.get(0).getScenicId());
									Scenic s = scenicMapper.getscenic(map);
									if (s != null) {
										co.setIntro(s.getIntro());
										co.setScenicId(s.getId());
										co.setScenicName(s.getScenicName());
										co.setScenicCoverImgUrl(scenicImgUrl + s.getFileCode() + "/" + s.getCoverImg());
										co.setProvinceName(s.getProvinceName());
										co.setCityName(s.getCityName());
										co.setAddress(s.getAddress());
									}
									for (CardExpand ce : gceList) {
										map.clear();
										map.put("scenicId", ce.getScenicId());
										map.put("date", ce.getAppointment());
										map.put("cardType", ce.getCardType());
										List<ScenicCardType> typeList = cardMapper.getScenicCardType(map);
										if (typeList != null && !typeList.isEmpty()) {
											ce.setPrice(typeList.get(0).getPrice());
											ce.setUsePrice(typeList.get(0).getUsePrice());
											ce.setTotalPrice(ce.getPrice().add(ce.getUsePrice()));
											if (typeList.get(0).getCardType() == 1) {
												ce.setCardTypeName("普通卡");
											} else {
												ce.setCardTypeName("功能卡");
											}
										}
									}
									cojson = JSONObject.fromObject(co);
									cojson.put("cardExpandList", JSONArray.fromObject(gceList));
								} else {
									cojson = JSONObject.fromObject(co);
									cojson.put("cardExpandList", "");
								}
								// coListjson.add(cojson);
								result.put("code", "0001");
								result.put("data", cojson);
							} else {
								result.put("code", "0001");
								result.put("data", "");
							}
						} else {
							result.put("code", "1000");
							result.put("message", "其他订单列表功能暂未开通！");
							return result.toString();
						}
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} catch (Exception e) {
				log.error("订单详情(type=" + type + "):", e);
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
	 * 订单操作1：取消；2：申请退款；3:删除
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Path("operateOrder")
	@Produces({ MediaType.APPLICATION_JSON })
	public String operateOrder(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String orderNo = jsonobject.optString("orderNo");
		String type = jsonobject.optString("type");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && orderNo != null && !orderNo.isEmpty() && type != null
					&& !type.isEmpty()) {
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
					} else {
						map.clear();
						map.put("orderNo", orderNo);
						List<GoodsOrder> o = expenseMapper.getGoodsOrder(map);
						if (o != null && !o.isEmpty()) {
							if (type.equals("1")) {// 首先判断订单是未支付状态
								if (o.get(0).getStatus() == 1) {
									consumeService.operateOrder(o.get(0), userinfo, 2, 1);
									map.clear();
									map.put("orderNo", orderNo);
									List<CardExpand> list = cardMapper.getCardExpand(map);
									if (list != null && !list.isEmpty()) {
										for (CardExpand ce : list) {
											map.clear();
											map.put("orderNo", ce.getOrderNo());
											map.put("scenicId", ce.getScenicId());
											map.put("cardType", ce.getCardType());
											map.put("appointment", ce.getAppointment());
											map.put("status1", 5);
											cardMapper.operateCardexpand(map);
										}
									}
								} else {
									result.put("code", "0006");
									result.put("message", "该订单无法取消！");
									return result.toString();
								}
							} else if (type.equals("3")) {// 删除订单(未支付，取消，失效，完成关闭)
								if (o.get(0).getStatus() == 1 || o.get(0).getStatus() == 2 || o.get(0).getStatus() == 3
										|| o.get(0).getStatus() == 10 || o.get(0).getStatus() == 8
										|| o.get(0).getStatus() == 9) {
									map.clear();
									map.put("orderNo", orderNo);
									map.put("userId", userinfo.getId());
									map.put("isDel", 1);
									expenseMapper.updateOrder(map);
									consumeService.savelog(orderNo, userinfo.getId(), 0, 1);
								} else {
									result.put("code", "0007");
									result.put("message", "该订单无法删除！");
									return result.toString();
								}
							} else if (type.equals("2")) {// 申请退款
								if (o.get(0).getStatus() == 4) {
									consumeService.operateOrder(o.get(0), userinfo, 8, 1);
									map.clear();
									map.put("orderNo", orderNo);
									List<CardExpand> list = cardMapper.getCardExpand(map);
									BigDecimal totalAllprice = new BigDecimal("0.00");
									BigDecimal totaluseprice = new BigDecimal("0.00");
									BigDecimal totalprice = new BigDecimal("0.00");
									if (list != null && !list.isEmpty()) {
										for (CardExpand ce : list) {
											if (ce.getStatus() == 1 && ce.getCardNum() > ce.getCardUseNum()) {
												map.clear();
												map.put("scenicId", ce.getScenicId());
												map.put("date", ce.getAppointment());
												map.put("cardType", ce.getCardType());
												List<ScenicCardType> sctList = cardMapper.getScenicCardType(map);
												if (sctList != null && !sctList.isEmpty()) {// 未使用腕带退还租金和使用费
													cardService.operateCardExpand(ce, 4);
													ce.setPrice(sctList.get(0).getPrice());
													ce.setUsePrice(sctList.get(0).getUsePrice());
													ce.setTotalPrice(sctList.get(0).getPrice()
															.add(sctList.get(0).getUsePrice()));
													totalAllprice = totalAllprice.add(ce.getTotalPrice().multiply(
															new BigDecimal(ce.getCardNum() - ce.getCardUseNum())));
													totaluseprice = totaluseprice.add(ce.getUsePrice().multiply(
															new BigDecimal(ce.getCardNum() - ce.getCardUseNum())));
													totalprice = totalprice.add(ce.getPrice().multiply(
															new BigDecimal(ce.getCardNum() - ce.getCardUseNum())));
												}
											}
										}
									}
									if (userinfo.getStatus() == 2) {
										result.put("code", "0006");
										result.put("flag", false);
										result.put("message", "账户冻结，无法退款！");
										return result.toString();
									} // 退钱至钱包
									BigDecimal balance = new BigDecimal("0.00");
									balance = userinfo.getBalance().add(totalAllprice);
									map.put("userId", userinfo.getId());
									map.put("status", 1);
									balance = balance.setScale(2, BigDecimal.ROUND_HALF_DOWN);
									map.put("balance", balance);
									usermapper.updateUserAmount(map);
									// 插入流水
									consumeService.saveUserLog(userinfo, 5, totalAllprice, 7, 3);
									// 插入消息
									messageService.insertUserMessage("1", userinfo, 3, 5, totalAllprice);
								} else {
									result.put("code", "0008");
									result.put("message", "该订单无法申请退款！");
									return result.toString();
								}
							} else {
								result.put("code", "1000");
								result.put("message", "其他订单操作功能尚未开通！");
								return result.toString();
							}
							result.put("code", "0001");
							result.put("message", "操作成功！");
						} else {
							result.put("code", "0005");
							result.put("message", "该订单为无效订单！");
							return result.toString();
						}
					}
				} else {
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("腕带订单操作失败(type=" + type + "):", e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}
}
