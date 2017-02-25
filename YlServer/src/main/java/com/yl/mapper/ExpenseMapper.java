package com.yl.mapper;

import java.util.List;
import java.util.Map;

import com.yl.beans.Admin;
import com.yl.beans.AttractsLine;
import com.yl.beans.City;
import com.yl.beans.Evaluate;
import com.yl.beans.EvaluateImg;
import com.yl.beans.ExpenseUserLog;
import com.yl.beans.GoodsExpand;
import com.yl.beans.GoodsExpandDetail;
import com.yl.beans.GoodsOrder;
import com.yl.beans.GoodsOrderDetail;
import com.yl.beans.LineLog;
import com.yl.beans.LinePrice;
import com.yl.beans.LineTime;
import com.yl.beans.LogRecord;
import com.yl.beans.Order;
import com.yl.beans.TemporaryOrder;
import com.yl.beans.UserCard;
import com.yl.beans.Userinfo;

public interface ExpenseMapper {
	// 查询订单列表（基础表）
	List<Order> getOrder(Map map);

	// 订单保存（基础表）
	void saveOrder(Order o);

	// 订单修改（基础表）
	void updateOrder(Map map);

	// 用户消费流水记录的保存
	void saveExpenseUserlog(Map map);

	// 系统平台流水记录的入库
	void saveExpenseSystemlog(Map map);

	// 商家交易流水的记录的入库
	void saveExpenseSpecialtylog(Map map);

	// 获取平台管理员，商家管理员信息
	Admin getAdmin(Map map);
	// 查找快递信息
	String getFreight(Map map);
	// 特产订单附属保存入库
	void saveGoodsExpand(Map map);

	// 特产订单快递附属
	void saveGoodsFreightExpand(Map map);

	// 订单操作记录
	void saveOrderLog(Map map);

	// 订单列表
	List<GoodsOrder> getGoodsOrder(Map map);

	// 订单附属商品列表
	List<GoodsExpandDetail> getGoodsExpandDetail(Map map);

	// 订单详情
	GoodsOrderDetail getGoodsOrderDetail(Map map);

	// 临时订单（用于充值）
	TemporaryOrder getTemporaryOrder(Map map);

	// 保存临时订单
	void saveTemporaryOrder(Map map);

	// 修改临时订单表
	void updateTemporaryOrder(Map map);
	//获取已生成的订单号
	String getOrderNo(Map map);
	
	List<ExpenseUserLog>  getexpenselogList(Map map);
}
