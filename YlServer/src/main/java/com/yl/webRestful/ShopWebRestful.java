package com.yl.webRestful;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

//import com.yl.webService.UserWebService;
import com.yl.Utils.CommonDateParseUtil;
import com.yl.Utils.GetProperties;
import com.yl.beans.Collect;
import com.yl.beans.Postagetemplate;
import com.yl.beans.Scenic;
import com.yl.beans.Shop;
import com.yl.beans.ShopNear;
import com.yl.beans.ShoppingCar;
import com.yl.beans.Speciality;
import com.yl.beans.SpecialityAttributes;
import com.yl.beans.SpecialityBanner;
import com.yl.beans.SpecialityDetails;
import com.yl.beans.SpecialityInfo;
import com.yl.beans.SpecialityStandard;
import com.yl.beans.Userinfo;
import com.yl.mapper.ScenicMapper;
import com.yl.mapper.ShopMapper;
import com.yl.mapper.UserMapper;
import com.yl.service.CardService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*
 * 特产和店铺接口
 */
@Component("shopwebRest")
public class ShopWebRestful {
	@Autowired
	private ScenicMapper scenicMapper;

	@Autowired
	private UserMapper usermapper;

	@Autowired
	private ShopMapper shopmapper;
	@Autowired
	private CardService cardService;

	// 商品图片路径
	private String shopImgUrl = GetProperties.getshopImgUrl();
	// 分享
	private String shareurl = GetProperties.getshareUrl();

	private static Logger log = Logger.getLogger(ShopWebRestful.class);

	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/*
	 * 特产列表 type 1:附近商品列表；2：价格 递减；3：价格 递增；4：销售量；5：推荐 shopType:1:一般查询；2：店铺下的
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getspecialityList")
	public String getspecialityList(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		Integer scenicId = jsonobject.optInt("scenicId");
		String type = jsonobject.optString("type");
		String shopType = jsonobject.optString("shopType");
		Integer pageNum = jsonobject.optInt("pageNum");
		Integer num = jsonobject.optInt("num");
		Integer shopId = jsonobject.optInt("shopId");
		HashMap<String, Object> map = new HashMap<String, Object>();
		// 首先通过景区的经纬定位周边的店铺，然后查询符合条件的推荐特产
		try {
			if (type != null && !type.isEmpty() && pageNum != null && pageNum > 0 && num != null && num > 0
					&& shopType != null && !shopType.isEmpty()) {
				List<Speciality> spList = null;
				if (type.equals("1")) {
					if (scenicId != null && scenicId > 0) {
						map.put("scenicId", scenicId);
						Scenic scenic = scenicMapper.getscenic(map);
						map.put("placeX", scenic.getPlaceX());
						map.put("placeY", scenic.getPlaceY());
						List<ShopNear> shopNearList = shopmapper.getshopNearList(map);
						if (shopNearList != null && !shopNearList.isEmpty()) {
							// JSONArray arraylist =
							// JSONArray.fromObject(shopNearList);
							// List<ShopNearForGoods> sgList =
							// JSONArray.toList(arraylist,
							// ShopNearForGoods.class);
							List<Integer> idlist = new ArrayList<Integer>();
							for (ShopNear sg : shopNearList) {
								idlist.add(sg.getId());
							}
							map.clear();
							map.put("list", idlist);
						} else {
							result.put("code", "0003");
							result.put("message", "暂无数据！");
							return result.toString();
						}
					} else {
						result.put("code", "0002");
						result.put("message", "参数不全！");
						return result.toString();
					}
				} else {
					map.clear();
					map.put("type", type);
					if (shopType.equals("2") && shopId != null && shopId > 0) {
						List<Integer> idlist1 = new ArrayList<Integer>();
						idlist1.add(shopId);
						map.put("list", idlist1);
						if(type.equals("5")){
							map.put("isSelfRecommend", 1);
							map.put("type", "6");
						}
					}
					if (shopType.equals("2") && (shopId == null || shopId == 0)) {
						result.put("code", "0002");
						result.put("message", "参数不全！");
						return result.toString();
					}
				}
				map.put("start", num * (pageNum - 1));
				map.put("num", num);
				spList = shopmapper.getSpecialityList(map);
				if (shopType.equals("2") && shopId != null && shopId > 0&&(spList == null||spList.isEmpty())&&type.equals("5")) {
					map.remove("isSelfRecommend");
					map.put("type", type);
					spList = shopmapper.getSpecialityList(map);
				}
				if (spList != null && !spList.isEmpty()) {
					for (Speciality sp : spList) {
						sp.setImgUrl(shopImgUrl + sp.getShopFileCode() + "/Speciality/" + sp.getFileCode() + "/"
								+ sp.getImgUrl());
						map.clear();
						map.put("fk", sp.getId());
						map.put("model", "speciality");
						String evaluateTotalNum = scenicMapper.getevaluateListCount(map);
						map.put("type", "l");
						String evaluatelargeTotalNum = scenicMapper.getevaluateListCount(map);
						sp.setTotalEvaluateNum(Integer.valueOf(evaluateTotalNum));
						if (!evaluateTotalNum.equals("0")) {
							float number = (float) Integer.valueOf(evaluatelargeTotalNum)
									/ Integer.valueOf(evaluateTotalNum) * 100;
							DecimalFormat df = new DecimalFormat("0");// 格式化小数
							String s = df.format(number);
							sp.setGoodEvaluateCate(Integer.valueOf(s));
						} else {
							sp.setGoodEvaluateCate(0);
						}
					}
					result.put("code", "0001");
					result.put("message", "查询成功！");
					result.put("data", spList);
				} else {
					result.put("code", "0003");
					if (pageNum == 1) {
						result.put("message", "暂无特产！");
					} else {
						result.put("message", "已无更多特产！");
					}
					// result.put("message", "暂无数据！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取特产商品列表失败：" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

	/*
	 * 特产信息
	 */
	@Path("getspecialityDetail")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public String getspecialityDetail(String json) {
		JSONObject result = new JSONObject();
		JSONObject data = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		Integer specialityId = jsonobject.optInt("specialityId");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (specialityId != null && specialityId > 0) {
				map.put("id", specialityId);
				SpecialityDetails sd = shopmapper.getSpecialityDetails(map);
				if (sd != null) {
					if (sd.getLogo() != null && !sd.getLogo().isEmpty()) {
						sd.setLogo(shopImgUrl + sd.getShopFileCode() + "/" + sd.getLogo());
					}
					// 查询快递信息
					if (!sd.getFreightId().equals(0)) {
						map.clear();
						map.put("id", sd.getFreightId());
						Postagetemplate f = shopmapper.getPostagetemplate(map);
						String intro = null;
						if (f.getDefaultPrice().compareTo(new BigDecimal(0)) == 0) {
							sd.setFreightIntro("包邮");
						} else {
							if (f.getMaxPrice().compareTo(new BigDecimal(0)) == 0) {
								sd.setFreightIntro("默认" + f.getDefaultPrice().setScale(2) + "元");
							} else {
								sd.setFreightIntro("默认" + f.getDefaultPrice().setScale(2) + "元,满"
										+ f.getMaxPrice().setScale(2) + "元包邮");
							}
						}
						// sd.setFreightIntro(intro);
					} else {
						sd.setFreightIntro("包邮");
					}
					// 查询特产轮播表
					map.clear();
					map.put("goodId", specialityId);
					List<SpecialityBanner> sbList = shopmapper.getSpecialityBannerList(map);
					if (sbList != null && !sbList.isEmpty()) {
						for (SpecialityBanner sb : sbList) {
							if (sb.getImgUrl() != null && !sb.getImgUrl().isEmpty()) {
								sb.setImgUrl(shopImgUrl + sd.getShopFileCode() + "/Speciality/" + sd.getFileCode() + "/"
										+ sb.getImgUrl());
							}
						}
						data.put("SpecialityBannerList", sbList);
					} else {
						data.put("SpecialityBannerList", "");
					}
					// 特产规格
					map.clear();
					map.put("goodId", specialityId);
					List<SpecialityStandard> ssList = shopmapper.getSpecialityStandardList(map);
					if (ssList != null && !ssList.isEmpty()) {
						data.put("SpecialityStandardList", ssList);
					} else {
						data.put("SpecialityStandardList", "");
					}
					// 店铺收藏数
					map.clear();
					map.put("fk", sd.getShopId());
					map.put("model", "shop");
					String collectTotalNum = scenicMapper.getcollectListCount(map);
					sd.setCollectTotalNum(collectTotalNum);
					// 店铺产品总数
					map.clear();
					map.put("shopId", sd.getShopId());
					String goodsTotalNum = shopmapper.getspecialityCount(map);
					sd.setGoodsTotalNum(goodsTotalNum);
					data.put("specialityDetails", sd);
					// 评论总数
					map.clear();
					map.put("fk", specialityId);
					map.put("model", "speciality");
					String toatalEvaluateNum = scenicMapper.getevaluateListCount(map);
					data.put("toatalEvaluateNum", toatalEvaluateNum);
					data.put("shareUrl", shareurl+"shareShop.html");
					result.put("code", "0001");
					result.put("data", data);
					result.put("message", "查询成功！");
				} else {
					result.put("code", "0003");
					result.put("message", "暂无数据！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取特产详情失败：" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();

	}

	/*
	 * 特产详情
	 */
	@Path("getspecialityInfo")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public String getspecialityInfo(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		Integer specialityId = jsonobject.optInt("specialityId");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (specialityId != null && specialityId > 0) {
				map.put("goodId", specialityId);
				List<SpecialityInfo> siList = shopmapper.getSpecialityInfoList(map);
				map.clear();
				map.put("id", specialityId);
				SpecialityDetails sd = shopmapper.getSpecialityDetails(map);
				if (siList != null && !siList.isEmpty()) {
					for (SpecialityInfo si : siList) {
						if (si.getImgUrl() != null && !si.getImgUrl().isEmpty()) {
							si.setImgUrl(shopImgUrl + sd.getShopFileCode() + "/Speciality/" + sd.getFileCode() + "/"
									+ si.getImgUrl());
						}
					}
					result.put("data", siList);
					result.put("code", "0001");
					result.put("message", "查询成功！");
				} else {
					result.put("code", "0003");
					result.put("message", "暂无数据！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取特产详情失败：" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

	/*
	 * 特产属性参数
	 */
	@Path("getspecialityAttributes")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public String getspecialityAttributes(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		Integer specialityId = jsonobject.optInt("specialityId");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (specialityId != null && specialityId > 0) {
				map.put("id", specialityId);
				List<SpecialityAttributes> saList = shopmapper.getspcialityAttributes(map);
				if (saList != null && !saList.isEmpty()) {
					result.put("data", saList);
					result.put("code", "0001");
					result.put("message", "查询成功！");
				} else {
					result.put("code", "0003");
					result.put("message", "暂无数据！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取特产属性参数失败：" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

	/*
	 * 特产店铺信息
	 */
	@Path("getshopInfo")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public String getshopInfo(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		Integer shopId = jsonobject.optInt("shopId");
		String uuID = jsonobject.optString("uuID");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (shopId != null && shopId > 0) {
				map.put("id", shopId);
				map.put("type", 1);
				Shop sp = shopmapper.getshopInfo(map);
				if (sp != null) {
					if (sp.getLogo() != null && !sp.getLogo().isEmpty()) {
						sp.setLogo(shopImgUrl + sp.getFileCode() + "/" + sp.getLogo());
					}
					// 店铺收藏数
					map.clear();
					map.put("fk", shopId);
					map.put("model", "shop");
					String collectTotalNum = scenicMapper.getcollectListCount(map);
					sp.setTotalCollectNum(collectTotalNum);
					// 店铺产品总数
					map.clear();
					map.put("shopId", sp.getId());
					String goodsTotalNum = shopmapper.getspecialityCount(map);
					sp.setTotalSpcialityNum(goodsTotalNum);
					// 是否收藏
					if (uuID != null && !uuID.isEmpty()) {
						map.put("uuID", uuID);
						Userinfo userinfo = usermapper.Getuserinfo(map);
						if (userinfo != null) {
							map.clear();
							map.put("userId", userinfo.getId());
							map.put("fk", sp.getId());
							map.put("model", "shop");
							List<Collect> collect = scenicMapper.getcollectList(map);
							if (collect != null && !collect.isEmpty()) {
								sp.setIsCollect(1);
							} else {
								sp.setIsCollect(0);
							}
						} else {
							sp.setIsCollect(0);
						}
					} else {
						sp.setIsCollect(0);
					}
					result.put("data", sp);
					result.put("code", "0001");
					result.put("message", "查询成功！");
				} else {
					result.put("code", "0003");
					result.put("message", "暂无数据！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取特产详情失败：" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

	/*
	 * 编辑购物车type 1：加入或修改；2：删除(单个) 3：批量 4：清空
	 * 
	 * sourcetype 1:在商品详情里添加进入购物车，2：在购物车里编辑商品数量
	 */
	@SuppressWarnings("finally")
	@Transactional
	@Path("operateShoppingCar")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public String operateShoppingCar(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String type = jsonobject.optString("type");
		String sourcetype = jsonobject.optString("sourcetype");
		String uuID = jsonobject.optString("uuID");
		Integer goodId = jsonobject.optInt("goodId");
		Integer goodNumber = jsonobject.optInt("goodNumber");
		Integer shopId = jsonobject.optInt("shopId");
		Integer goodsStandardId = jsonobject.optInt("goodsStandardId");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (type != null && !type.isEmpty() && uuID != null && !uuID.isEmpty()) {
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				if (result.optBoolean("flag")) {
					// if (userinfo != null) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
						return result.toString();
					} else {
						if (type.equals("1")) {
							if (goodId != null && goodId > 0 && goodNumber != null && goodNumber > 0
									&& goodsStandardId != null && goodsStandardId > 0 && shopId != null && shopId > 0) {
								Calendar time = Calendar.getInstance();
								String addTime = df.format(time.getTime());
								map.clear();
								map.put("userId", userinfo.getId());
								map.put("goodId", goodId);
								map.put("goodNumber", goodNumber);
								map.put("goodsStandardId", goodsStandardId);
								map.put("shopId", shopId);
								map.put("addTime", addTime);
								ShoppingCar sc = shopmapper.getShoppingCar(map);
								if (sc != null) {
									if (sourcetype != null && !sourcetype.isEmpty()) {
										if (sourcetype.equals("1")) {
											map.remove("goodNumber");
											map.put("goodNumber", sc.getGoodNumber() + goodNumber);
										}
									}
									shopmapper.updateShoppingCar(map);
								} else {
									shopmapper.saveShoppingCar(map);
								}
							} else {
								result.put("code", "0002");
								result.put("message", "参数不全！");
								return result.toString();
							}
						} else if (type.equals("2")) {
							if (goodId != null && goodId > 0 && goodsStandardId != null && goodsStandardId > 0
									&& shopId != null && shopId > 0) {
								map.clear();
								map.put("userId", userinfo.getId());
								map.put("goodId", goodId);
								map.put("goodsStandardId", goodsStandardId);
								map.put("shopId", shopId);
								shopmapper.deleteCar(map);
							} else {
								result.put("code", "0002");
								result.put("message", "参数不全！");
								return result.toString();
							}
						} else if (type.equals("3")) {
							if (jsonobject.optJSONArray("list") != null && !jsonobject.optJSONArray("list").isEmpty()) {
								JSONArray js = jsonobject.optJSONArray("list");
								List<ShoppingCar> carList = JSONArray.toList(js, ShoppingCar.class);
								for (ShoppingCar sc : carList) {
									map.clear();
									map.put("userId", userinfo.getId());
									map.put("goodId", sc.getGoodId());
									map.put("goodsStandardId", sc.getGoodsStandardId());
									shopmapper.deleteCar(map);
								}
							} else {
								result.put("code", "0002");
								result.put("message", "参数不全！");
								return result.toString();
							}
						} else if (type.equals("4")) {
							map.clear();
							map.put("userId", userinfo.getId());
							shopmapper.deleteCar(map);
						}
						result.put("code", "0001");
						result.put("message", "操作成功！");
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("购物车操作失败：" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 获取购物车列表
	 */
	@Path("getShoppingCarList")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public String getShoppingCarList(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty()) {
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				if (result.optBoolean("flag")) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
						return result.toString();
					} else {
						map.put("userId", userinfo.getId());
						String shoppingCarList = shopmapper.getShoppingCarList(map);
						if (shoppingCarList != null) {
							JSONArray js = JSONArray.fromObject(shoppingCarList);
							for (int i = 0; i < js.size(); i++) {
								JSONObject jo = js.getJSONObject(i);
								System.out.println(jo);
								JSONArray js1 = jo.getJSONArray("specialityList");
								for (int j = 0; j < js1.size(); j++) {
									JSONObject jo1 = js1.getJSONObject(j);
									System.out.println(jo1);
									map.clear();
									map.put("goodId", jo1.get("goodId"));
									List<SpecialityBanner> sbList = shopmapper.getSpecialityBannerList(map);
									String imgUrl = shopImgUrl + jo1.getString("shopfileCode") + "/Speciality/"
											+ jo1.getString("fileCode") + "/" + sbList.get(0).getImgUrl();
									jo1.put("imgUrl", imgUrl);
								}
							}
							result.put("data", js.toString());
						} else {
							result.put("data", "");
						}
						result.put("code", "0001");
						result.put("message", "查询成功！");
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取购物车列表：" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

}
