package com.yl.testServerImpl;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import javax.servlet.Servlet;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.yl.testServer.Testwebservice;

//@WebService(endpointInterface = "com.yl.testServer.Testwebservice", serviceName = "ts")
@Path("/ts")
public class TestWebserviceImpl implements Testwebservice {

	@Override
	public String sayHi(String text) {
		return "sayHi" + text;
	}

	@Override
	@Path("/tt")
	@POST
	public int getNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getNameAndAge(String nameid, int number) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getList() {
		List<String> list = new ArrayList<String>();
		list.add("萧瑟");
		list.add("10");
		return list;
	}

}
