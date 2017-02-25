package com.yl.webService;

import javax.jws.WebService;
import javax.ws.rs.Produces;
import com.yl.beans.Userinfo;
import javax.ws.rs.core.MediaType;

@WebService
public interface UserWebService {

	public String login(String userName, String userPwd, String smsCode, String logintype);

	public String register(String userName, String userPwd, String smsCode);

	public String getcheckcode(String userName,String codetype);

	public String getuserinfo(String userName, String uuID);
	
	public String  modifyuserinfo(String userName, String uuID,Userinfo userinfo);
	
	public  String  modifyPwd(String userName,String userPwd, String smsCode);
	
	public String   changeuserName(String userName, String newUserName,String uuID,String smsCode,String changetype);

}
