package com.yl.mapper;

import java.util.List;
import java.util.Map;

import com.yl.beans.AttractsLine;
import com.yl.beans.CardExpand;
import com.yl.beans.City;
import com.yl.beans.Evaluate;
import com.yl.beans.EvaluateImg;
import com.yl.beans.LineLog;
import com.yl.beans.LinePrice;
import com.yl.beans.LinePriceDetails;
import com.yl.beans.LineTime;
import com.yl.beans.LogRecord;
import com.yl.beans.ScenicCardType;
import com.yl.beans.ScenicName;
import com.yl.beans.UserCard;
import com.yl.beans.Userinfo;

public interface CardMapper {
	// 查询用户绑定的游乐卡
	List<UserCard> getuserCard(Map map);

	// 获取排队记录
	List<LineLog> getlineLog(Map map);

	// 人数
	Integer getlineLogNum(Map map);

	// 获取排队景点
	List<AttractsLine> getattractsLine(Map map);

	// 预约排队
	void saveLineLog(Map map);

	// 排队状态修改
	void updateLineLog(Map map);

	// VIP时间段
	List<LineTime> getlineTime(Map map);

	// 某个VIP时间段所有景点的信息
	List<LinePrice> getlinePrice(Map map);

	// 某个景点所有VIP时间段的信息(免费VIP和付费VIP)
	List<LinePrice> getlineTimeByAttractsId(Map map);

	// 某一VIP时间某一景点的信息
	LinePriceDetails getlinePriceDetails(Map map);

	// 排队使用记录的人数统计
	Integer getlineUseLogNum(Map map);

	void savecard(Map map);

	// 游乐卡支持使用的景区
	List<ScenicName> getScenicName(Map map);

	// 支持使用的景区游乐卡类型
	List<ScenicCardType> getScenicCardType(Map map);

	// 支持使用的景区游乐卡个数
	List<Map> getCardNum(Map map);

	// 订单的附属
	List<CardExpand> getCardExpand(Map map);

	// 修改腕带的附属订单
	void operateCardexpand(Map map);

	void saveCardExpand(Map<String, Object> map);
}
