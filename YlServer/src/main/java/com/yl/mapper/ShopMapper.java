package com.yl.mapper;

import java.util.List;
import java.util.Map;
import com.yl.beans.Goods;
import com.yl.beans.Postageappend;
import com.yl.beans.Postagetemplate;
import com.yl.beans.Shop;
import com.yl.beans.ShopNear;
import com.yl.beans.ShoppingCar;
import com.yl.beans.Speciality;
import com.yl.beans.SpecialityAttributes;
import com.yl.beans.SpecialityBanner;
import com.yl.beans.SpecialityDetails;
import com.yl.beans.SpecialityInfo;
import com.yl.beans.SpecialityStandard;

public interface ShopMapper {

	// 获取景区附近的店铺
	List<ShopNear> getshopNearList(Map map);

	// 商品列表
	List<Speciality> getSpecialityList(Map map);

	// 特产详情
	SpecialityDetails getSpecialityDetails(Map map);

	// 特产轮播图
	List<SpecialityBanner> getSpecialityBannerList(Map map);

	// 特产规格
	List<SpecialityStandard> getSpecialityStandardList(Map map);

	// 特产详情图片
	List<SpecialityInfo> getSpecialityInfoList(Map map);

	// 店铺所属特产数量
	String getspecialityCount(Map map);

	List<SpecialityAttributes> getspcialityAttributes(Map map);

	Shop getshopInfo(Map map);

	// 店铺收藏
	List<Shop> getshopCollectList(Map map);

	// 购物车删除
	void deleteCar(Map map);

	// 获取购物车列表
	String getShoppingCarList(Map map);

	// 获取购物车单个特产
	ShoppingCar getShoppingCar(Map map);

	// 加入购物车
	void saveShoppingCar(Map map);

	// 修改购物车数量
	void updateShoppingCar(Map map);

	// 填写订单的数据模型
	Goods getSpecialityDetailsForOrder(Map map);

	// 快递模板
	Postagetemplate getPostagetemplate(Map map);

	// 快递附加模板
	Postageappend getPostageappend(Map map);
	
	//修改特产库存数量（在特产订单下单或取消操作）
	void operateSpecialityStandard(Map map);

}
