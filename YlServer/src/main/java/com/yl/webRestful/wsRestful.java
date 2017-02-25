package com.yl.webRestful;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

//import com.yl.webService.UserWebService;
import com.yl.Utils.BaseParseImage;
import com.yl.Utils.CodeUtils;
import com.yl.Utils.CommonDateParseUtil;
import com.yl.Utils.GetProperties;
import com.yl.Utils.InitUtils;
import com.yl.Utils.MD5Utils;
import com.yl.Utils.SMSUtils;
import com.yl.beans.City;
import com.yl.beans.ExpenseUserLog;
import com.yl.beans.LogRecord;
import com.yl.beans.UserAddress;
import com.yl.beans.Userinfo;
import com.yl.mapper.UserMapper;
import com.yl.service.CardService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*
 * 用户接口
 */
@Component("wswebRest")
public class wsRestful {
	@Autowired
	private UserMapper usermapper;
	@Autowired
	private CardService cardService;

	private static Logger log = Logger.getLogger(wsRestful.class);

	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// 头像显示
	private String userImgUrl = GetProperties.getuserImgUrl();
	// 头像存放
	private String UserImgUrl = GetProperties.getUserImgUrl();

	public static void main(String[] args) {

	}

	/**
	 * 用户的登录验证
	 * 
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("login")
	public String login(String json) {
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String userName = jsonobject.optString("userName");
		String loginType = jsonobject.optString("loginType");
		JSONObject result = new JSONObject();
		try {
			if (userName != null && !userName.isEmpty() && loginType != null && !loginType.isEmpty()) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("userName", userName);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				if (!result.optBoolean("flag")) {
					return result.toString();
				}
				Calendar time = Calendar.getInstance();
				if (userinfo != null) {
					// 登录方式为密码登录
					if (loginType.equals("1")) {
						String userPwd = jsonobject.optString("userPwd");
						if (userinfo.getUserPwd().equals(MD5Utils.string2MD5(userPwd))) {
							result.put("code", "0001");
							result.put("message", "登录成功！");
						} else {
							result.put("code", "0004");
							result.put("message", "登录密码有误！");
						}
						// 登录方式为短信验证码登录
					} else if (loginType.equals("2")) {
						String smsCode = jsonobject.optString("smsCode");
						Date nowdate = null;
						Date SmsPwdExpiry = null;
						try {
							nowdate = (Date) time.getTime();
							nowdate = df.parse(df.format(nowdate));
							SmsPwdExpiry = df.parse(userinfo.getSmsPwdExpiry());
						} catch (ParseException e) {
							log.error("登录操作的时间格式化异常：" ,e);
						}
						if (nowdate.getTime() <= SmsPwdExpiry.getTime()) {
							if (userinfo.getSmsCode().equals(smsCode)) {
								result.put("code", "0001");
								result.put("userinfo", userinfo);
								result.put("message", "登录成功！");
							} else {
								result.put("code", "0006");
								result.put("message", "短信验证密码有误！");
							}
						} else {
							result.put("code", "0005");
							result.put("message", "该短信验证码已失效，请重新请求新的验证码！");
						}
					}
				} else {
					result.put("code", "0003");
					result.put("message", "该手机号尚未注册！");
				}
				try {
					// 设置用户登录时间和登录有效时间
					if (result.get("code").equals("0001")) {
						if (userinfo.getUserStatus() == 2) {
							result.put("code", "0010");
							result.put("message", "该用户已被禁用！");  
							return result.toString();
						}
						String logintime = df.format(new Date());
						userinfo.setLoginTime(logintime);
						time.add(Calendar.HOUR_OF_DAY, 240);
						String uuIDExpiry = df.format((Date) time.getTime());
						String uuID = InitUtils.getUUID();
						userinfo.setUuID(uuID);
						userinfo.setUuIDExpiry(uuIDExpiry);
						// 当前所处登录城市
						String cityName = jsonobject.optString("cityName");
						if (cityName == null || cityName == "") {
							cityName = "上海";
						}
						City city = usermapper.getCity(cityName);
						if (city != null) {
							userinfo.setLoginCity(city.getCityId());
						} else {
							userinfo.setLoginCity(null);
						}
						usermapper.Updateuserinfo(userinfo);
						userinfo.setHeadImg(userImgUrl + userinfo.getHeadImg());
						// 查询用户地址
						map.clear();
						map.put("id", userinfo.getAddressId());
						map.put("userId", userinfo.getId());
						map.put("isDefault", 1);
						List<UserAddress> ua = usermapper.getuserAddress(map);
						if (ua != null && !ua.isEmpty()) {
							userinfo.setAdprovinceName(ua.get(0).getProvinceName());
							userinfo.setAdcityName(ua.get(0).getCityName());
							userinfo.setAddistrictName(ua.get(0).getDistrictName());
							userinfo.setAddressId(ua.get(0).getId());
							userinfo.setAddressInfo(ua.get(0).getAddressInfo());
							userinfo.setAdrecipient(ua.get(0).getRecipient());
							userinfo.setAdrecipientMobile(ua.get(0).getRecipientMobile());
							userinfo.setAdzip(ua.get(0).getZip());
						}
						System.out.println(userinfo.toString());
						userinfo.setBalance(userinfo.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP));
						result.put("data", userinfo);
					}
				} catch (Exception e) {
					log.error("登录成功时修改登录时间和设置登录失效时间失败", e);
					result.put("code", "0000");
					result.put("message", "平台繁忙，请稍候再试！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "手机号不能为空！");
			}
		} catch (Exception e) {
			log.error("登录用户查询异常", e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}finally {
			return result.toString();
		}
	}

	/**
	 * 用戶获取验证码 codetype 1:注册；2：登录；3：修改密码或忘记密码；4：更换手机号；
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getcheckcode")
	// public String getcheckcode(String userName, String codetype)
	public String getcheckcode(String json) {
		System.out.println("ssss:" + json);
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String userName = jsonobject.optString("userName");
		String codeType = jsonobject.optString("codeType");
		JSONObject result = new JSONObject();
		HashMap<String, Object> map = new HashMap<String, Object>();
		System.out.println(userName);
		try {
			if (userName != null && !userName.isEmpty() && codeType != null && !codeType.isEmpty()) {
				Calendar time = Calendar.getInstance();
				String smsPwdExpiry = null;
				map.put("userName", userName);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				// 注册验证码存放在log表中;tongshi
				if (codeType.equals("1") || codeType.equals("4")) {
					if (userinfo == null) {
						time.add(Calendar.MINUTE, 3);
						result.put("code", "0001");
						result.put("message", "验证码已发送！");
						// 存放在log表中操作
					} else {
						result.put("code", "0003");
						result.put("message", "该手机号已被注册，请登录！");
						return result.toString();
					}
				} else {
					if (userinfo != null) {
						// 修改发出短信验证码的时间的字段
						time.add(Calendar.MINUTE, 5);
						result.put("code", "0001");
						result.put("message", "验证码已发送！");
					} else {
						result.put("code", "0005");
						result.put("message", "手机号尚未注册！");
						return result.toString();
					}
				}
				smsPwdExpiry = df.format((Date) time.getTime());
				if (result.get("code").equals("0001")) {
					String smsCode = CodeUtils.getCode(6);
					Boolean flag = SMSUtils.send(smsCode, userName);
					log.info("获取验证码的手机号：" + userName + ";该手机号的验证码：" + smsCode);
					if (!flag) {
						result.put("code", "0004");
						result.put("message", "获取验证码发送失败！");
						return result.toString();
					}
					result.put("smsCode", smsCode);
					if (!codeType.equals("1")) {
						result = cardService.checkUser(userinfo);
						if (result.optBoolean("flag")) {
							if (codeType.equals("4")) {
								String uuID = jsonobject.optString("uuID");
								map.clear();
								map.put("uuID", uuID);
								Userinfo userinfo1 = usermapper.Getuserinfo(map);
								if (userinfo1 != null) {
									userinfo1.setSmsPwdExpiry(smsPwdExpiry);
									userinfo1.setSmsCode(smsCode);
									usermapper.Updateuserinfo(userinfo1);
								}
							} else {
								userinfo.setSmsPwdExpiry(smsPwdExpiry);
								userinfo.setSmsCode(smsCode);
								usermapper.Updateuserinfo(userinfo);
							}
						} else {
							return result.toString();
						}
					}
					LogRecord logRecord = new LogRecord();
					logRecord.setAddTime(df.format(new Date()));
					logRecord.setSmsCode(smsCode);
					logRecord.setSmsPwdExpiry(smsPwdExpiry);
					logRecord.setUserName(userName);
					logRecord.setOperateType(1);
					usermapper.AddlogRecord(logRecord);
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取验证码失败：" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("register")
	public String register(String json) {
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String userName = jsonobject.optString("userName");
		String userPwd = jsonobject.optString("userPwd");
		String smsCode = jsonobject.optString("smsCode");
		JSONObject result = new JSONObject();
		try {
			if (userName != null && !userName.isEmpty() && userPwd != null && !userPwd.isEmpty() && smsCode != null
					&& !smsCode.isEmpty()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("userName", userName);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				if (userinfo == null) {
					// 先从log日志表中查询验证码和有效期限
					map.put("operateType", 1);
					LogRecord logRecord = usermapper.getlogRecord(map);
					if (logRecord.getSmsCode().equals(smsCode)) {
						Calendar time = Calendar.getInstance();
						Date nowdate = null;
						Date SmsPwdExpiry = null;
						try {
							nowdate = (Date) time.getTime();
							nowdate = df.parse(df.format(nowdate));
							SmsPwdExpiry = df.parse(logRecord.getSmsPwdExpiry());
							if (nowdate.getTime() <= SmsPwdExpiry.getTime()) {
								// 用户注册信息初始化
								Userinfo user = new Userinfo();
								user.setUserName(userName);
								user.setNickName("YL_" + userName.substring(userName.length() - 6, userName.length()));
								user.setCreateTime(df.format(new Date()));
								String md5Pwd = MD5Utils.string2MD5(userPwd);
								user.setUserPwd(md5Pwd);
								user.setLeavel(0);
								String logintime = df.format(new Date());
								user.setLoginTime(logintime);
								user.setCreateTime(logintime);
								user.setUserCode(CodeUtils.getuserCode());
								// Calendar time = Calendar.getInstance();
								time.add(Calendar.HOUR_OF_DAY, 240);
								String uuIDExpiry = df.format((Date) time.getTime());
								String uuID = InitUtils.getUUID();
								user.setUuID(uuID);
								user.setUuIDExpiry(uuIDExpiry);
								user.setUuID(uuID);
								user.setUuIDExpiry(uuIDExpiry);
								usermapper.Adduser(user);
								Userinfo userinfoback = usermapper.Getuserinfo(map);
								// 添加余额账号
								map.clear();
								map.put("userId", userinfoback.getId());
								map.put("balance", 0);
								map.put("minAmount", 0);
								map.put("status", 1);
								map.put("isOpen", 0);
								usermapper.saveUserAmount(map);
								// 注册送一百积分sourceId 1:注册
								map.clear();
								map.put("userId", userinfoback.getId());
								map.put("sourceId", 1);
								map.put("score", 100);
								String addTime = df.format(new Date());
								map.put("addTime", addTime);
								usermapper.savescore(map);
								result.put("code", "0001");
								result.put("message", "注册成功！");
								result.put("data", userinfoback);
							} else {
								result.put("code", "0005");
								result.put("message", "验证码失效！");
							}
						} catch (ParseException e) {
							log.error("注册操作的时间格式化异常：" ,e);
							result.put("code", "0000");
							result.put("message", "平台繁忙，请稍候！");
						}
					} else {
						result.put("code", "0004");
						result.put("message", "验证码不正确！");
					}
				} else {
					result.put("code", "0003");
					result.put("message", "该手机号已被注册！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("用户注册失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getuserinfo")
	public String getuserinfo(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		try {
			if (uuID != null && uuID != "") {
				HashMap<String, Object> map = new HashMap<>();
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				if (result.optBoolean("flag")) {
					// if (userinfo != null) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
					} else {
						// map.put("userId", userinfo.getId());
						map.put("isRead", 0);
						map.put("operateId", userinfo.getId());
						map.put("operateType", 1);
						String offReadNum = usermapper.getMessageNum(map);
						map.put("userId", userinfo.getId());
						String totalScore = usermapper.gettotalScore(map);
						userinfo.setOffReadNum(offReadNum);
						userinfo.setTotalScore(totalScore);
						result.put("code", "0001");
						result.put("message", "查询成功！");
						userinfo.setHeadImg(userImgUrl + userinfo.getHeadImg());
						userinfo.setBalance(userinfo.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP));
						result.put("userinfo", userinfo);
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
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
		}
		return result.toString();
	}

	/*
	 * type 1:修改密码；1：找回密码
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("modifyPwd")
	public String modifyPwd(String json) {
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String smsCode = jsonobject.optString("smsCode");
		String userPwd = jsonobject.optString("userPwd");
		String type = jsonobject.optString("type");
		String userName = jsonobject.optString("userName");
		JSONObject result = new JSONObject();
		try {
			if (smsCode != null && !smsCode.isEmpty() && userPwd != null && !userPwd.isEmpty() && type != null
					&& !type.isEmpty()) {
				HashMap<String, Object> map = new HashMap<>();
				if (type.equals("2") && userName != null && !userName.isEmpty()) {
					map.put("userName", userName);
				} else if (type.equals("1") && uuID != null && !uuID.isEmpty()) {
					map.put("uuID", uuID);
				}
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				if (result.optBoolean("flag")) {
					// if (userinfo != null) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					Date smsCodeExpiry = CommonDateParseUtil.string2date(userinfo.getSmsPwdExpiry());
					if (type.equals("1")) {
						if (nowdate.getTime() >= uuIDExpiry.getTime()) {
							result.put("code", "0004");
							result.put("message", "用户登录已过期，请重新登录！");
							return result.toString();
						}
					}
					if (smsCodeExpiry.getTime() >= nowdate.getTime()) {
						if (smsCode.equals(userinfo.getSmsCode())) {
							String md5Pwd = MD5Utils.string2MD5(userPwd);
							userinfo.setUserPwd(md5Pwd);
							usermapper.Updateuserinfo(userinfo);
							result.put("code", "0001");
							result.put("message", "修改成功！");
						} else {
							result.put("code", "0006");
							result.put("message", "短信验证码不正确！");
						}
					} else {
						result.put("code", "0005");
						result.put("message", "短信验证码失效,请重新请求！");
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
			log.error("修改密码失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("modifyuserinfo")
	public String modifyuserinfo(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		JSONObject userjson = jsonobject.getJSONObject("userinfo");
		Userinfo userinfo = (Userinfo) JSONObject.toBean(userjson, Userinfo.class);
		try {
			if (uuID != null && uuID != "" && userinfo != null) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("uuID", uuID);
				Userinfo userinfo1 = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo1);
				if (result.optBoolean("flag")) {
					// if (userinfo1 != null) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo1.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
					} else {
						userinfo.setId(userinfo1.getId());
						if (userinfo.getHeadImg() != null && !userinfo.getHeadImg().isEmpty()) {
							String headImg = BaseParseImage.generateImage(userinfo.getHeadImg(), UserImgUrl, ".jpg",
									null);
							userinfo.setHeadImg(headImg);
						}
						usermapper.Updateuserinfo(userinfo);
						result.put("code", "0001");
						result.put("message", "修改成功！");
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
			log.error("修改用户信息失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * changetype 1:原手机号和验证码验证；2：新手机号和验证码验证
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("changeuserName")
	public String changeuserName(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String userName = jsonobject.optString("userName");
		String newUserName = jsonobject.optString("newUserName");
		String smsCode = jsonobject.optString("smsCode");
		String changetype = jsonobject.optString("changetype");
		try {
			if (changetype != null && !changetype.isEmpty() && uuID != null && !uuID.isEmpty() && smsCode != null
					&& !smsCode.isEmpty()) {
				if (changetype.equals("1")) {
					if (userName == null || userName.isEmpty()) {
						result.put("code", "0002");
						result.put("message", "参数不全！");
						return result.toString();
					}
				} else {
					if (newUserName == null || newUserName.isEmpty()) {
						result.put("code", "0002");
						result.put("message", "参数不全！");
						return result.toString();
					}
				}
				HashMap<String, Object> map = new HashMap<>();
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				if (result.optBoolean("flag")) {
					// if (userinfo != null) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					Date smsCodeExpiry = CommonDateParseUtil.string2date(userinfo.getSmsPwdExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
					} else {
						if (smsCodeExpiry.getTime() >= nowdate.getTime()) {
							if (smsCode.equals(userinfo.getSmsCode())) {
								result.put("code", "0001");
								result.put("message", "验证正确！");
								if (changetype.equals("2")) {
									userinfo.setUserName(newUserName);
									usermapper.Updateuserinfo(userinfo);
									result.put("message", "更换成功！");
								}
							} else {
								result.put("code", "0006");
								result.put("message", "短信验证码不正确！");
							}
						} else {
							result.put("code", "0005");
							result.put("message", "短信验证码失效,请重新请求！");
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
			log.error("更换手机号失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * type:1:积分明细;2:消费明细
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getdetails")
	public String getdetails(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		Integer pageNum = jsonobject.optInt("pageNum");
		Integer num = jsonobject.optInt("num");
		try {
			if (uuID != null && !uuID.isEmpty() && type != null && !type.isEmpty() && pageNum != null && pageNum > 0
					&& num != null && num > 0) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				if (result.optBoolean("flag")) {
					// if (userinfo != null) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
					} else {
						map.put("start", num * (pageNum - 1));
						map.put("num", num);
						if (type.equals("1")) {
							map.put("userId", userinfo.getId());
							List<Map> scoreList = usermapper.getscore(map);
							if (scoreList != null && !scoreList.isEmpty()) {
								// for (int i = 0; i < scoreList.size(); i++) {
								// Integer sourceId = Integer
								// .parseInt(String.valueOf(scoreList.get(i).get("sourceId")));
								// if (sourceId == 1) {
								// scoreList.get(i).put("typeString", "注册");
								// }else if (sourceId == 2){
								// scoreList.get(i).put("typeString", "消费");
								// }else{
								// scoreList.get(i).put("typeString", "抵扣");
								// }
								// }
								result.put("data", scoreList);
							} else {
								if (pageNum == 1) {
									result.put("message", "暂无积分明细！");
								} else {
									result.put("message", "已无更多积分明细！");
								}
								result.put("data", "");
							}
						} else {
							map.put("userId", userinfo.getId());
							List<ExpenseUserLog> expenseList = usermapper.getexpenselog(map);
							if (expenseList != null && !expenseList.isEmpty()) {
								JSONArray list = new JSONArray();
								JSONObject ex = new JSONObject();
								for (ExpenseUserLog e : expenseList) {
									String expenseTypeString = null;
									String remark = null;
									ex.put("addTime", e.getAddTime());
									ex.put("expenseUserNo", e.getExpenseUserNo());
									BigDecimal amount = e.getPaymentAmount().setScale(2);
									ex.put("amount", amount.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
									if (e.getExpenseType() == 1) {// 充值
										expenseTypeString = "充值";
										if (e.getPaymentType() == 1) {
											remark = "微信充值";
										} else if (e.getPaymentType() == 2) {
											remark = "支付宝充值";
										} else {
											remark = "其他";
										}
									} else if (e.getExpenseType() == 2) {// 提现
										if (e.getStatus() == 0) {// 提现失败
											expenseTypeString = "退还";
											if (e.getPaymentType() == 1) {
												remark = "微信提现退还-" + e.getRemark();
											} else if (e.getPaymentType() == 2) {
												remark = "支付宝提现退还-" + e.getRemark();
											} else {
												remark = "其他";
											}
										} else {
											expenseTypeString = "提现";
											if (e.getStatus() == 1) {
												if (e.getPaymentType() == 1) {
													remark = "微信提现处理中";
												} else if (e.getPaymentType() == 2) {
													remark = "支付宝提现处理中";
												} else {
													remark = "其他";
												}
											} else if (e.getStatus() == 2) {
												if (e.getPaymentType() == 1) {
													remark = "微信提现成功";
												} else if (e.getPaymentType() == 2) {
													remark = "支付宝提现成功";
												} else {
													remark = "其他";
												}
											} else {
												if (e.getPaymentType() == 1) {
													remark = "微信提现失败";
												} else if (e.getPaymentType() == 2) {
													remark = "支付宝提现失败";
												} else {
													remark = "其他";
												}
											}
										}
									} else if (e.getExpenseType() == 3) {// 消费
										expenseTypeString = "支付";
										if (e.getUseType() == 1) {
											remark = "VIP消费";
										} else if (e.getUseType() == 3) {
											remark = "特产商品消费";
										} else if (e.getUseType() == 7) {
											remark = "腕带消费";
										} else {
											remark = "其他消费";
										}
										if (e.getPaymentType() == 1) {
											remark = remark + "-微信支付";
										} else if (e.getPaymentType() == 2) {
											remark = remark + "-支付宝支付";
										} else {
											remark = remark + "-游乐钱包支付";
										}
									} else if (e.getExpenseType() == 5) {// 退款
										expenseTypeString = "退款";
										if (e.getUseType() == 1) {
											remark = "VIP退款";
										} else if (e.getUseType() == 3) {
											remark = "特产商品退款";
										} else if (e.getUseType() == 7) {
											remark = "腕带退款";
										} else {
											remark = "其他退款";
										}
									} else if (e.getExpenseType() == 6) {// 押金退还
										expenseTypeString = "押金退还";
										if (e.getUseType() == 7) {
											remark = "腕带押金退还";
										} else {
											remark = "其他退款";
										}
									} else {
										expenseTypeString = "其他";
									}
									ex.put("remark", remark);
									ex.put("expenseTypeString", expenseTypeString);
									list.add(ex);
								}
								result.put("data", list);
							} else {
								if (pageNum == 1) {
									result.put("message", "暂无账户明细！");
								} else {
									result.put("message", "已无更多账户明细！");
								}
								result.put("data", "");
							}
						}
						result.put("code", "0001");
						// result.put("message", "查询成功！");
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
			System.out.println(e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
		}
		return result.toString();
	}

}
