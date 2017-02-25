package com.yl.Controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sun.tools.internal.ws.processor.model.Request;

@Controller
public class TestController {

 @RequestMapping("/test1")
 public String login(){
	 
	return "test";
	 
 }
 
 @RequestMapping(value="/add",method=RequestMethod.POST)
 public String addUser(HttpServletRequest request) {  
     System.out.println("userName is:"+request.getParameter("userName"));  
     System.out.println("password is:"+request.getParameter("password"));  
     return "/user/success";  
 } 
}
