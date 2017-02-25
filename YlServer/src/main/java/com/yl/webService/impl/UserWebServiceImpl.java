package com.yl.webService.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.yl.Utils.CodeUtils;
import com.yl.Utils.CommonDateParseUtil;
import com.yl.Utils.InitUtils;
import com.yl.Utils.MD5Utils;
import com.yl.Utils.SMSUtils;
import com.yl.beans.LogRecord;
import com.yl.beans.Userinfo;
import com.yl.mapper.UserMapper;
import com.yl.webService.UserWebService;

@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
@WebService(endpointInterface = "com.yl.webService.UserWebService", serviceName = "us")
public class UserWebServiceImpl implements UserWebService {
	@Autowired
	private UserMapper usermapper;

	private static Logger log = Logger.getLogger(UserWebServiceImpl.class);

	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 用户的登录验证
	 * 
	 */
	@Override
	public String login(String userName, String userPwd, String smsCode, String logintype) {
		JSONObject result = new JSONObject();
		if (userName != null && userName != "" && logintype != null && logintype != "") {
			try {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("userName", userName);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				Calendar time = Calendar.getInstance();
				if (userinfo != null) {
					// 登录方式为密码登录
					if (logintype.equals("1")) {
						if (userinfo.getUserPwd().equals(MD5Utils.string2MD5(userPwd))) {
							result.put("code", "0001");
							result.put("message", "登录成功！");
						} else {
							result.put("code", "0004");
							result.put("message", "登录密码有误！");
						}
						// 登录方式为短信验证码登录
					} else if (logintype.equals("2")) {
						Date nowdate = null;
						Date SmsPwdExpiry = null;
						try {
							nowdate = (Date) time.getTime();
							nowdate = df.parse(df.format(nowdate));
							SmsPwdExpiry = df.parse(userinfo.getSmsPwdExpiry());
						} catch (ParseException e) {
							log.error("登录操作的时间格式化异常：" ,e);
						}
						if (nowdate.getTime() > SmsPwdExpiry.getTime()) {
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
						String logintime = df.format(new Date());
						userinfo.setLoginTime(logintime);
						time.add(Calendar.HOUR_OF_DAY, 24);
						String uuIDExpiry = df.format((Date) time.getTime());
						String uuID = InitUtils.getUUID();
						userinfo.setUuID(uuID);
						userinfo.setUuIDExpiry(uuIDExpiry);
						usermapper.Updateuserinfo(userinfo);
						result.put("userinfo", userinfo);
					}
				} catch (Exception e) {
					log.error("登录成功时修改登录时间和设置登录失效时间失败", e);
				}
			} catch (Exception e) {
				log.error("登录用户查询异常", e);
				result.put("code", "0000");
				result.put("message", "平台繁忙，请稍候再试！");
			}
		} else {
			result.put("code", "0002");
			result.put("message", "手机号不能为空！");
		}
		return result.toString();
	}

	/**
	 * 用戶获取验证码 codetype 1:注册；2：登录；3：修改密码或忘记密码；4：更换手机号；
	 */
	@Override
	public String getcheckcode(String userName, String codetype) {
		JSONObject result = new JSONObject();
		Map map = new HashMap();
		if (userName != null && userName != "") {
			try {
				Calendar time = Calendar.getInstance();
				String smsPwdExpiry = null;
				map.put("userName", userName);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				String smsCode = CodeUtils.getCode(6);
				Boolean flag = SMSUtils.send(smsCode, userName);
				log.info("获取验证码的手机号：" + userName + ";该手机号的验证码：" + smsCode);
				if (!flag) {
					result.put("code", "0001");
					result.put("message", "获取验证码发送失败！");
					return result.toString();
				}
				// 注册验证码存放在log表中;tongshi
				if (codetype.equals("1")) {
					if (userinfo == null) {
						time.add(Calendar.MINUTE, 3);
						result.put("code", "0001");
						result.put("message", "验证码已发送！");
						// 存放在log表中操作
					} else {
						result.put("code", "0004");
						result.put("message", "该手机号已被注册，请登录！");
					}
				} else {
					if (userinfo != null) {
						// 修改发出短信验证码的时间的字段
						time.add(Calendar.MINUTE, 5);
						result.put("code", "0001");
						result.put("message", "验证码已发送！");
					} else {
						result.put("code", "0003");
						result.put("message", "手机号尚未注册！");
					}
				}
				smsPwdExpiry = df.format((Date) time.getTime());
				if (result.get("code").equals("0001")) {
					if (codetype.equals("1")) {
						LogRecord logRecord = new LogRecord();
						logRecord.setAddTime(df.format(new Date()));
						logRecord.setSmsCode(smsCode);
						logRecord.setSmsPwdExpiry(smsPwdExpiry);
						logRecord.setUserName(userName);
						usermapper.AddlogRecord(logRecord);
					} else {
						userinfo.setSmsPwdExpiry(smsPwdExpiry);
						userinfo.setSmsCode(smsCode);
						usermapper.Updateuserinfo(userinfo);
					}
				}
			} catch (Exception e) {
				log.error("获取验证码失败：" ,e);
				result.put("code", "0000");
				result.put("message", "平台繁忙，请稍候再试！");
			}
		} else {
			result.put("code", "0002");
			result.put("message", "手机号不能为空！");
		}
		return result.toString();
	}

	@Override
	public String register(String userName, String userPwd, String smsCode) {
		JSONObject result = new JSONObject();
		if (userName != null && userName != "") {
			try {
				HashMap<String, Object> map = new HashMap<>();
				map.put("userName", userName);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				if (userinfo == null) {
					// 先从log日志表中查询验证码和有效期限
					// 用户注册信息初始化
					Userinfo user = new Userinfo();
					user.setUserName(userName);
					user.setNickName(userName);
					user.setCreateTime(df.format(new Date()));
					String md5Pwd = MD5Utils.string2MD5(userPwd);
					user.setUserPwd(md5Pwd);
					user.setLeavel(0);
					String logintime = df.format(new Date());
					user.setLoginTime(logintime);
					Calendar time = Calendar.getInstance();
					time.add(Calendar.HOUR_OF_DAY, 24);
					String uuIDExpiry = df.format((Date) time.getTime());
					String uuID = InitUtils.getUUID();
					user.setUuID(uuID);
					user.setUuIDExpiry(uuIDExpiry);
					user.setUuID(uuID);
					user.setUuIDExpiry(uuIDExpiry);
					usermapper.Adduser(user);
					Userinfo userinfoback = usermapper.Getuserinfo(map);
					result.put("code", "0001");
					result.put("message", "注册成功！");
					result.put("userinfo", userinfoback);
				} else {
					result.put("code", "0003");
					result.put("message", "该手机号已被注册！");
				}
			} catch (Exception e) {
				log.error("用户注册失败" ,e);
				result.put("code", "0000");
				result.put("message", "平台繁忙，请稍候再试！");
			}
		} else {
			result.put("code", "0002");
			result.put("message", "手机号不能为空！");
		}
		return result.toString();
	}

	/**
	 * 
	 */
	@Override
	public String getuserinfo(String userName, String uuID) {
		JSONObject result = new JSONObject();
		if (userName != null && userName != "" && uuID != null && uuID != "") {
			try {
				HashMap<String, Object> map = new HashMap<>();
				map.put("userName", userName);
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				if (userinfo != null) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
					} else {
						result.put("code", "0001");
						result.put("message", "查询成功！");
						result.put("userinfo", userinfo);
					}
				} else {
					result.put("code", "0003");
					result.put("message", "暂无用户信息！");
				}
			} catch (Exception e) {
				result.put("code", "0000");
				result.put("message", "平台繁忙，请稍候再试！");
			}
		} else {
			result.put("code", "0002");
			result.put("message", "参数不全！");
		}
		return result.toString();
	}

	@Override
	public String modifyPwd(String userName, String userPwd, String smsCode) {
		JSONObject result = new JSONObject();
		if (userName != null && userName != "" && smsCode != null && smsCode != "" && userPwd != null
				&& userPwd != "") {
			try {
				HashMap<String, Object> map = new HashMap<>();
				map.put("userName", userName);
				// map.put("UUID", UUID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				if (userinfo != null) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					Date smsCodeExpiry = CommonDateParseUtil.string2date(userinfo.getSmsPwdExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0005");
						result.put("message", "用户登录已过期，请重新登录！");
					} else {
						if (smsCodeExpiry.getTime() >= nowdate.getTime()) {
							if (smsCode.equals(userinfo.getSmsCode())) {
								userinfo.setUserPwd(userPwd);
								usermapper.Updateuserinfo(userinfo);
								result.put("code", "0001");
								result.put("message", "修改成功！");
							} else {
								result.put("code", "0006");
								result.put("message", "短信验证码不正确！");
							}
						} else {
							result.put("code", "0004");
							result.put("message", "短信验证码失效,请重新请求！");
						}
					}
				} else {
					result.put("code", "0003");
					result.put("message", "暂无用户信息！");
				}
			} catch (Exception e) {
				log.error("修改密码失败" ,e);
				result.put("code", "0000");
				result.put("message", "平台繁忙，请稍候再试！");
			}
		} else {
			result.put("code", "0002");
			result.put("message", "参数不全！");
		}
		return result.toString();
	}

	@Override
	public String modifyuserinfo(String userName, String uuID, Userinfo userinfo) {
		JSONObject result = new JSONObject();
		if (userName != null && userName != "" && uuID != null && uuID != "" && userinfo != null) {
			try {
				HashMap<String, Object> map = new HashMap<>();
				map.put("userName", userName);
				map.put("uuID", uuID);
				Userinfo userinfo1 = usermapper.Getuserinfo(map);
				if (userinfo1 != null) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo1.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
					} else {
						userinfo.setId(userinfo1.getId());
						usermapper.Updateuserinfo(userinfo);
						result.put("code", "0001");
						result.put("message", "修改成功！");
					}
				} else {
					result.put("code", "0003");
					result.put("message", "暂无用户信息！");
				}
			} catch (Exception e) {
				log.error("修改密码失败" ,e);
				result.put("code", "0000");
				result.put("message", "平台繁忙，请稍候再试！");
			}
		} else {
			result.put("code", "0002");
			result.put("message", "参数不全！");
		}
		return result.toString();
	}

	@Override
	public String changeuserName(String userName, String newUserName, String uuID, String smsCode, String changetype) {
		JSONObject result = new JSONObject();
		if (changetype != null && changetype != "" && userName != null && userName != "" && uuID != null && uuID != ""
				&& smsCode != null && smsCode != "" && newUserName != null && newUserName != "") {
			try {
				HashMap<String, Object> map = new HashMap<>();
				map.put("userName", userName);
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				if (userinfo != null) {
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
					result.put("code", "0003");
					result.put("message", "暂无用户信息！");
				}
			} catch (Exception e) {
				log.error("更换手机号失败" ,e);
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
