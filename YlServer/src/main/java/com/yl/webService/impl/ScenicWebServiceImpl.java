package com.yl.webService.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jws.WebService;
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
import com.yl.mapper.ScenicMapper;
import com.yl.mapper.UserMapper;
import com.yl.webService.ScenicWebService;
import com.yl.webService.UserWebService;

@WebService(endpointInterface = "com.yl.webService.ScenicWebService", serviceName = "sc")
public class ScenicWebServiceImpl implements ScenicWebService {
	@Autowired
	private ScenicMapper scenicMapper;

	private static Logger log = Logger.getLogger(ScenicWebServiceImpl.class);

	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public String getscenicList(String cityName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getallinfo(String scenicId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getscenicDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getattractsList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getattractsDetail() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String gettravelNotesList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String gettravelNotesDetails() {
		// TODO Auto-generated method stub
		return null;
	}

}
