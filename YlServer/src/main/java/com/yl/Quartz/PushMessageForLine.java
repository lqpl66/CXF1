package com.yl.Quartz;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.yl.service.MessageService;

public class PushMessageForLine {
	@Autowired
	private MessageService messageService;
	@Autowired
	private static Logger log = Logger.getLogger(PushMessageForLine.class);
	public void doIt() throws JobExecutionException {
		try {
			Date date = new Date();
//			String dd = " " + date.getMinutes() + ":" + date.getSeconds() + " ";
//			System.out.println("sss1" + dd);
			messageService.PushMessageLine();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("定时任务：", e);
		}
	}
}
