package com.yl.TestClient;

import java.util.List;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.alibaba.fastjson.JSONObject;
import com.yl.beans.Userinfo;
import com.yl.testServer.Testwebservice;
import com.yl.webService.UserWebService;

public class TestClient {

	public static void main(String args[]) throws Exception {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.getInInterceptors().add(new LoggingInInterceptor());
		factory.getOutInterceptors().add(new LoggingOutInterceptor());
		factory.setServiceClass(UserWebService.class);
		factory.setAddress("http://localhost:8080/YlServer/us");
		UserWebService client = (UserWebService) factory.create();
		String result = null;
		Userinfo userinfo = new Userinfo();
		//注册  codetype 1:注册；2：登录；3：修改密码或忘记密码；4：更换手机号；
		 result = client.getcheckcode("18651641256", "2");
     // result = client.register("18651641256", "123456", "646789");
		
//		result = client.login("18651641256", "123456", "586873", "1");
//		result = client.sayHi("sss");
		System.out.println("返回值----" + result);
		
		
		
		
		
//		MobileCodeWS mc = new MobileCodeWS();
//		          MobileCodeWSSoap soap = mc.getMobileCodeWSSoap();
//		          String str = soap.getMobileCodeInfo("13011286707", null);
//		          System.out.println(str);
		
//		String hi = client.sayHi("萧_瑟");
//		int number = client.getNumber();
//		String NameAge = client.getNameAndAge("萧_瑟", 18);
//		List list = client.getList();
//
//		System.out.println("hi----" + hi);
//		System.out.println("number----" + number);
//		System.out.println("NameAge----" + NameAge);
//		System.out.print("list----");
//		for (int i = 0; i < list.size(); i++) {
//			System.out.print(list.get(i) + ",");
//		}
	}

}
