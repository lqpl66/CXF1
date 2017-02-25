package com.yl.testServer;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
//@WebService
@Path("/ts")
public interface Testwebservice {
	public String sayHi(@WebParam(name = "text") String text);

	public int getNumber();

	public String getNameAndAge(@WebParam(name = "nameid") String nameid, @WebParam(name = "number") int number);

	public List getList();
}
