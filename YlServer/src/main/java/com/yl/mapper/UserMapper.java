package com.yl.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yl.beans.City;
import com.yl.beans.District;
import com.yl.beans.Evaluate;
import com.yl.beans.EvaluateImg;
import com.yl.beans.ExpenseUserLog;
import com.yl.beans.FeedbackType;
import com.yl.beans.LogRecord;
import com.yl.beans.Message;
import com.yl.beans.PresentApplication;
import com.yl.beans.Province;
import com.yl.beans.PushMessageDevice;
import com.yl.beans.PushMessageLog;
import com.yl.beans.UserAddress;
import com.yl.beans.UserAmount;
import com.yl.beans.UserPayAccount;
import com.yl.beans.Userinfo;

public interface UserMapper {

	// 添加用户
	void Adduser(Userinfo userinfo);

	// 获取用户信息
	Userinfo Getuserinfo(Map map);

	// 修改用户信息
	void Updateuserinfo(Userinfo userinfo);

	// 添加短息日志
	void AddlogRecord(LogRecord logRecord);

	// 获取短信日志
	LogRecord getlogRecord(Map map);

	City getCity(String cityName);

	// 保存评论
	void saveEvaluate(Evaluate ev);

	// 保存评论图片
	void saveEvaluateImg(EvaluateImg evImg);

	// 保存收藏
	void saveCollect(Map map);

	// 取消收藏
	void deleteCollectById(Map map);

	// 绑解卡操作日志
	void saveBindCardLog(Map map);

	// 绑卡操作
	void saveBindCard(Map map);

	// 开启关闭支付功能
	void updateUserCard(Map map);

	// 冻结景区卡
	void updateScenicCard(Map map);

	// 解卡操作
	void deleteCard(Map map);

	// 获取省份
	List<Province> getProvince();

	// 获取城市列表
	List<City> getCity(Map map);

	// 获取城市列表
	List<District> getDistrict(Map map);

	// 获取用户地址
	List<UserAddress> getuserAddress(Map map);

	// 保存用户地址
	void saveUserAddress(UserAddress ua);

	// 修改用户地址
	void updateuserAddress(UserAddress ua);

	// 获取用户地址
	void deleteuserAddress(Map map);

	// 获取所有地址的省市区
	String getAddress();

	// 获取用户余额账户
	UserAmount getuserAmount(Map map);

	// 添加用户余额账户
	void saveUserAmount(Map map);

	// 修改用户余额账户
	void updateUserAmount(Map map);

	// 保存消息
	void saveMessage(Map map);

	// 修改消息
	void updateMessage(Map map);

	// 获取消息
	List<Message> getMessage(Map map);

	// 获取未读总数
	String getMessageNum(Map map);

	// 获取反馈问题种类
	List<FeedbackType> getfeedbackList();

	// 添加反馈问题
	void savefeedback(Map map);

	// 获取历史搜索记录
	List<Map> getsearchlog(Map map);

	// 保存搜索关键字
	void savesearchlog(Map map);

	// 清楚搜索历史记录
	void updatesearchlog(Map map);

	// 积分保存操作
	void savescore(Map map);

	// 积分统计 type 1:统计历史总积分
	String gettotalScore(Map map);

	// 积分明细
	List<Map> getscore(Map map);

	// 消费记录保存操作
	void saveexpenselog(Map map);

	// 消费明细
	List<ExpenseUserLog> getexpenselog(Map map);

	// 插入提现账号
	void saveUserPayAccount(Map map);

	// 查询提现账号
	List<UserPayAccount> getUserPayAccount(Map map);

	// 修改提现账号
	void updateUserPayAccount(Map map);

	// 发出提现申请
	void savePresentApplication(Map map);

	// 提现申请查找
	List<PresentApplication> getPresentApplication(Map map);

	// 查找设备
	List<PushMessageDevice> getPushMessageDevice(Map map);

	// 设备修改
	void updatePushMessageDevice(Map map);

	// 设备入库
	void savePushMessageDevice(Map map);

	// 消息操作记录入库
	void savePushMessageLog(PushMessageLog pml);
	//用户账户明细流水统计
	String gettotalAmount(Map map);
	//积分状态修改
	void updateStore(Map map);
}
