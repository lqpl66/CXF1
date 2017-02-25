package com.yl.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yl.Push.PushConfig;
import com.yl.Push.IOS.IOSUnicast;
import com.yl.beans.Admin;
import com.yl.beans.LineLog;
import com.yl.beans.PushMessageDevice;
import com.yl.beans.PushMessageLog;
import com.yl.beans.Userinfo;
import com.yl.http.httpUtilPushMessage;
import com.yl.mapper.CardMapper;
import com.yl.mapper.ExpenseMapper;
import com.yl.mapper.UserMapper;

import net.sf.json.JSONObject;

@Service
public class MessageService {
	@Autowired
	private UserMapper usermapper;
	@Autowired
	private ExpenseMapper expenseMapper;
	@Autowired
	private CardMapper cardMapper;
	@Autowired
	private static Logger log = Logger.getLogger(MessageService.class);

	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void updatePushMessageDevice(Double x, Double y, Integer operateId, Integer operateType, String device_token,
			Integer mbSystemType) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (x != null && y != null) {
			map.put("placeX", new BigDecimal(x).setScale(6, BigDecimal.ROUND_HALF_UP));
			map.put("placeY", new BigDecimal(y).setScale(6, BigDecimal.ROUND_HALF_UP));
		}
		map.put("operateId1", operateId);
		map.put("operateType1", operateType);
		map.put("modifyTime", df.format(new Date()));
		map.put("device_token", device_token);
		map.put("mbSystemType", mbSystemType);
		usermapper.updatePushMessageDevice(map);
	}

	// type1：查出该用户下所有关联的设备；2：查出该设备下所有关联的用户，
	public List<PushMessageDevice> getPushMessageDeviceList(Integer operateId, Integer operateType, String type,
			String device_token, Integer mbSystemType) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.clear();
		if (type.equals("1")) {
			map.put("operateId", operateId);
			map.put("operateType", operateType);
		} else {
			map.put("device_token", device_token);
			map.put("mbSystemType", mbSystemType);
		}
		List<PushMessageDevice> pmdList = usermapper.getPushMessageDevice(map);
		return pmdList;
	}

	public void savePushMessageDevice(Double x, Double y, Integer operateId, Integer operateType, String device_token,
			Integer mbSystemType) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.clear();
		map.put("placeX", new BigDecimal(x).setScale(6, BigDecimal.ROUND_HALF_UP));
		map.put("placeY", new BigDecimal(y).setScale(6, BigDecimal.ROUND_HALF_UP));
		map.put("operateId", operateId);
		map.put("operateType", operateType);
		map.put("device_token", device_token);
		map.put("mbSystemType", mbSystemType);
		map.put("modifyTime", df.format(new Date()));
		map.put("addTime", df.format(new Date()));
		usermapper.savePushMessageDevice(map);
	}

	// 消息入库和推送type1：不推送；2推送
	// paymentType 1 微信;2 支付宝; 3 游乐币
	// expenseType 1 充值;2 提现;3 消费;5:退款（腕带）
	// messageType1:系统消息 ;2:服务消息
	// pushType 接收对象，目前都是单个推送 3
	// operateType 1 游乐app用户operateType 2 商家用户operateType 3 平台管理者 operateType 4
	// 景区用户
	public void insertUserMessage(String type, Userinfo userinfo, Integer paymentType, Integer expenseType,
			BigDecimal total_amount) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String messageContent = messageModel(paymentType, expenseType, total_amount);
		// 添加消息记录
		map.clear();
		map.put("parentId", -2);
		map.put("adminType", 1);
		Admin ad = expenseMapper.getAdmin(map);
		PushMessageLog pml = new PushMessageLog();
		pml.setMessageType(1);
		pml.setAddTime(df.format(new Date()));
		pml.setMessageContent(messageContent);
		if (ad != null) {
			pml.setOperateId(ad.getId());
		}
		pml.setOperateType(3);
		pml.setPushType(3);
		usermapper.savePushMessageLog(pml);
		// 插入用户消息表
		map.clear();
		map.put("operateId", userinfo.getId());
		map.put("operateType", 1);
		List<PushMessageDevice> pmdList = usermapper.getPushMessageDevice(map);
		if (pmdList != null && !pmdList.isEmpty()) {
			map.put("device_token", pmdList.get(0).getDevice_token());
			map.put("mbSystemType", pmdList.get(0).getMbSystemType());
		} else {
			map.put("device_token", "0");
			map.put("mbSystemType", 0);
		}
		map.put("messageType", 1);
		map.put("messageContent", messageContent);
		map.put("isDel", 0);
		map.put("addTime", df.format(new Date()));
		map.put("isRead", 0);
		map.put("operateType", 1);
		map.put("operateId", userinfo.getId());
		map.put("pmId", pml.getId());
		usermapper.saveMessage(map);
		// 是否推送消息
		if (type.equals("2")) {
			if (pmdList != null && !pmdList.isEmpty() && pmdList.get(0).getDevice_token() != null
					&& !pmdList.get(0).getDevice_token().isEmpty() && pmdList.get(0).getMbSystemType() != null) {
				if(pmdList.get(0).getMbSystemType()==1){//IOS推送
					pushUserMessageByIOSUnicast(pmdList.get(0).getDevice_token(), messageContent);
				}
			}
		}
	}

	// 消息推送
	public void pushUserMessageByIOSUnicast(String device_token, String messageContent) {// 针对个人推送
		try {
			IOSUnicast unicast = new IOSUnicast(PushConfig.IOS_appkey, PushConfig.IOS_app_master_secret, "unicast");
			unicast.setDeviceToken(device_token);
			unicast.setAlert(messageContent);
			unicast.setBadge(0);
			unicast.setSound("default");
			unicast.setTestMode();
//			unicast.setProductionMode();
			unicast.setTestMode();
			unicast.setCustomizedField("test", "helloworld");
			System.out.println(unicast.toString());
			httpUtilPushMessage.send(unicast);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// paymentType 1 微信;2 支付宝; 3 游乐币
	// expenseType 1 充值;2 提现;3 消费
	public String messageModel(Integer paymentType, Integer expenseType, BigDecimal total_amount) {
		String messageContent = null;
		if (expenseType == 2) {
			messageContent = "您已成功提现了" + total_amount.toString() + "元到";
			if (paymentType == 1) {
				messageContent = messageContent + "微信账号";
			} else {
				messageContent = messageContent + "支付宝账号";
			}
		} else if (expenseType == 1) {
			if (paymentType == 1) {
				messageContent = "您已成功使用微信充值了" + total_amount.toString() + "元";
			} else if (paymentType == 2) {
				messageContent = "您已成功使用支付宝充值了" + total_amount.toString() + "元";
			}
		} else if(expenseType == 3) {// 消费
			if (paymentType == 1) {
				messageContent = "您已成功使用微信消费了" + total_amount.toString() + "元";
			} else if (paymentType == 2) {
				messageContent = "您已成功使用支付宝消费了" + total_amount.setScale(2, BigDecimal.ROUND_UP).toString() + "元";
			} else if (paymentType == 3) {
				messageContent = "您已成功使用游乐钱包消费了" + total_amount.toString() + "元";
			}
		}else if(expenseType == 5){//退款
//			if (paymentType == 7) {//腕带
				messageContent = "您有一笔租卡订单已退款" + total_amount.toString() + "元至游乐钱包";
//			}
		}
		return messageContent;
	}

	// 查询普通排队的时间，发送推送消息
	public void PushMessageLine() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		// 更改普通排队的尚未使用但已超时的排队记录
		Calendar time = Calendar.getInstance();
		String nowTime = df.format(time.getTime());
		map.clear();
		map.put("nowTime", nowTime);
		map.put("state", 1);
		map.put("state1", 3);
		cardMapper.updateLineLog(map);
		map.put("typeId", 1);
		map.put("amount", 3);
		cardMapper.updateLineLog(map);
		map.clear();
		map.put("state", 1);
		map.put("isLine", 0);
		time.add(Calendar.MINUTE, 5);
		map.put("quartzNowTime_FROM", df.format(time.getTime()));
		time.add(Calendar.MINUTE, 15);
		map.put("quartzNowTime_TO", df.format(time.getTime()));
		List<LineLog> userlineLogList = cardMapper.getlineLog(map);
		if (userlineLogList != null && !userlineLogList.isEmpty()) {
			String messageContent = "您有排队项目即将到时，请及时使用，打开APP，查看详情";
			for (LineLog ll : userlineLogList) {
         	map.clear();
    		map.put("operateId", ll.getUserId());
    		map.put("operateType", 1);
    		List<PushMessageDevice> pmdList = usermapper.getPushMessageDevice(map);
    		if(pmdList!=null&&!pmdList.isEmpty()){
    			// 添加消息记录
         		map.clear();
         		map.put("parentId", -2);
         		map.put("adminType", 1);
         		Admin ad = expenseMapper.getAdmin(map);
         		PushMessageLog pml = new PushMessageLog();
         		pml.setMessageType(1);
         		pml.setAddTime(df.format(new Date()));
         		pml.setMessageContent(messageContent);
         		if (ad != null) {
         			pml.setOperateId(ad.getId());
         		}
         		pml.setOperateType(3);
         		pml.setPushType(3);
         		usermapper.savePushMessageLog(pml);
         		if(pmdList.get(0).getMbSystemType()==1){
         			pushUserMessageByIOSUnicast(pmdList.get(0).getDevice_token(), messageContent);
         		}
    		}
			}
		}
	}

	public static void main(String args[]) {
		Calendar time = Calendar.getInstance();
		String nowTime = df.format(time.getTime());
		System.out.println("1:" + nowTime);
		time.add(Calendar.MINUTE, 5);
		System.out.println("2:" + time.getTime());
		time.setTime(new Date());
		System.out.println("3:" + time.getTime());
	}

	public JSONObject getsearch(){
		HashMap<String, Object> map = new HashMap<String, Object>();
		JSONObject data = new JSONObject();
		return data;
	}
}
