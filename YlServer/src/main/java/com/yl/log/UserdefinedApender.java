package com.yl.log;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Priority;

public class UserdefinedApender extends DailyRollingFileAppender {
	
	@Override
	public boolean isAsSevereAsThreshold(Priority priority) {
		// TODO Auto-generated method stub
//		return super.isAsSevereAsThreshold(priority);
		//只输出相同等级
		 return this.getThreshold().equals(priority);
	}
	
	
}
