package com.yl.Sertvlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		System.out.println(" filter begin ");
		req.setCharacterEncoding("UTF-8");
//		resp.setContentType("text/html;charset=UTF-8");
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String json = (String) request.getAttribute("json");
		System.out.println(json);
		if (json == null) {
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("status", -1);
//			map.put("data", null);
//			map.put("msg", "you not login the system... can't request resource !");
//			PrintWriter writer = response.getWriter();
//			writer.print(com.alibaba.fastjson.JSONArray.toJSONString(map));
//			writer.flush();
//			writer.close();
		} else {
			chain.doFilter(req, resp);
		}
		System.out.println(" filter end ");
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
