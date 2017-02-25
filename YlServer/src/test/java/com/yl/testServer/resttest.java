package com.yl.testServer;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

@Component("restSample")
public class resttest {
	@POST
	@Path("/order/{name}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getOrder(@PathParam("name")String name) {
		System.out.println(name);
		Order o = new Order(10L, "李先生", "牛肉面", 20);
		JSONObject a = new JSONObject();
		a.put("code", "ddd");
		a.put("order", o);
		return a.toString();
	}
}
