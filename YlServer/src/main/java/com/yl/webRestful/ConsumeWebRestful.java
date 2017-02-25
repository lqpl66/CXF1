package com.yl.webRestful;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.yl.Utils.CodeUtils;
import com.yl.Utils.CommonDateParseUtil;
import com.yl.Utils.GetProperties;
import com.yl.beans.Freight;
import com.yl.beans.Goods;
import com.yl.beans.GoodsExpandDetail;
import com.yl.beans.GoodsOrder;
import com.yl.beans.GoodsOrderDetail;
import com.yl.beans.Order;
import com.yl.beans.Postageappend;
import com.yl.beans.Postagetemplate;
import com.yl.beans.PresentApplication;
import com.yl.beans.Shop;
import com.yl.beans.SpecialityBanner;
import com.yl.beans.TemporaryOrder;
import com.yl.beans.UserAddress;
import com.yl.beans.UserPayAccount;
import com.yl.beans.Userinfo;
import com.yl.http.httpUtilLogistics;
import com.yl.mapper.ExpenseMapper;
import com.yl.mapper.ShopMapper;
import com.yl.mapper.UserMapper;
import com.yl.pay.Alipay.AlipayUtil;
import com.yl.pay.Wechat.WeChatUtil;
import com.yl.pay.Wechat.WechatConfig;
import com.yl.service.CardService;
import com.yl.service.ConsumeService;
import com.yl.service.MessageService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*
 * 消费订单接口
 */
@Component("consumewebRest")
public class ConsumeWebRestful {
	@Autowired
	private UserMapper usermapper;
	@Autowired
	private ShopMapper shopmapper;
	@Autowired
	private ExpenseMapper expenseMapper;
	@Autowired
	private ConsumeService consumeService;
	@Autowired
	private MessageService messageService;
	@Autowired
	private CardService cardService;
	// 商品图片路径
	private String shopImgUrl = GetProperties.getshopImgUrl();
	private static Logger log = Logger.getLogger(ConsumeWebRestful.class);

	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/*
	 * 订单填写
	 */
	@SuppressWarnings("finally")
	@POST
	@Path("getOrderFill")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getOrderFill(String json) {
		JSONObject result = new JSONObject();
		JSONObject data = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		Integer addressId = jsonobject.optInt("addressId");
		JSONArray list = jsonobject.getJSONArray("list");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && list != null && list.isArray() && !list.isEmpty()) {
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				// if (userinfo != null) {
				if (result.optBoolean("flag")) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
						return result.toString();
					} else {
						// 查看用户的收货地址
						UserAddress ua = null;
						map.clear();
						if (addressId != null && addressId > 0) {
							map.put("userId", userinfo.getId());
							map.put("addressId", addressId);
							List<UserAddress> uas = usermapper.getuserAddress(map);
							if (uas != null && !uas.isEmpty()) {
								ua = uas.get(0);
								data.put("userAdress", ua);
							} else {
								data.put("userAdress", "");
							}
						} else {
							ua = null;
							data.put("userAdress", "");
						} // 标记是否可以自提
						boolean flag = false;
						// 标记是否可以使用积分折扣
						boolean flagscore = false;
						// 总运费
						BigDecimal totalpostageAmount = new BigDecimal("0.00");
						// 特产总价
						BigDecimal totalgoodsAmount = new BigDecimal("0.00");
						// 折扣总积分
						Integer totaldeductionScore = 0;
						// 当前用户积分
						map.clear();
						map.put("userId", userinfo.getId());
						String score_str = usermapper.gettotalScore(map);
						Integer score = Integer.valueOf(score_str);
						// 当前用户所有可用积分
						Integer availableDeductionScore = 0;
						if (score >= 100) {
							availableDeductionScore = (int) Math.floor(score / 100) * 100;
						} else {
							availableDeductionScore = 0;
						}
						for (int i = 0; i < list.size(); i++) {
							JSONObject jb = list.getJSONObject(i);
							JSONArray ja = jb.getJSONArray("specialityList");
							JSONArray goodlist = new JSONArray();
							// 总运费
							BigDecimal postageAmount = new BigDecimal("0.00");
							// 特产总价
							BigDecimal goodsAmount = new BigDecimal("0.00");
							// 特产遍历，封装到运费模板数组
							for (int j = 0; j < ja.size(); j++) {
								JSONObject jb1 = ja.getJSONObject(j);
								map.clear();
								map.put("goodId", jb1.get("goodId"));
								map.put("goodsStandardId", jb1.get("goodsStandardId"));
								Goods gs = shopmapper.getSpecialityDetailsForOrder(map);
								gs.setGoodNumber(jb1.optInt("goodNumber"));
								if (gs != null) {
									BigDecimal d = new BigDecimal(gs.getGoodNumber());
									BigDecimal price = gs.getPrice().multiply(d);
									// 计算特产数量和价格
									goodsAmount = goodsAmount.add(price);
									JSONObject goods = new JSONObject();
									if (j == 0) {
										goods.put("goodId", gs.getGoodId());
										goods.put("price", price);
										goods.put("freghtId", gs.getFreightId());
										goodlist.add(goods);
									} else {
										for (int l = 0; l < goodlist.size(); l++) {
											if (gs.getGoodId() == goodlist.optJSONObject(l).optInt("goodId")) {
												price = price.add(
														new BigDecimal(goodlist.optJSONObject(l).optDouble("price")));
												goodlist.optJSONObject(l).put("price", price);
											} else {
												goods.put("goodId", gs.getGoodId());
												goods.put("price", price);
												goods.put("freghtId", gs.getFreightId());
												goodlist.add(goods);
											}
										}
									}
									if (gs.getIsSelf() == 1) {
										flag = true;
									}
								}
							} // 计算运费
							if (goodlist != null && !goodlist.isEmpty()) {
								for (int k = 0; k < goodlist.size(); k++) {
									if (ua != null) {
										if (goodlist.optJSONObject(k).optInt("freghtId") != 0) {
											map.clear();
											map.put("id", goodlist.optJSONObject(k).optInt("freghtId"));
											Postagetemplate pt = shopmapper.getPostagetemplate(map);
											if (pt != null) {
												// 查看附加模板是否存在
												map.clear();
												map.put("provinceId", ua.getProvinceId());
												map.put("postageId", pt.getId());
												Postageappend pa = shopmapper.getPostageappend(map);
												if (pa != null) {// 默认邮费
													if (pa.getDefaultPrice().compareTo(new BigDecimal(0)) != 0) {
														if (pa.getMaxPrice().compareTo(new BigDecimal(
																goodlist.optJSONObject(k).getDouble("price"))) == 1) {
															postageAmount = postageAmount.add(pa.getDefaultPrice());
														}
													}
													// 没有附件模板
												} else {
													if (pt.getDefaultPrice().compareTo(new BigDecimal(0)) != 0) {
														if (pt.getMaxPrice().compareTo(new BigDecimal(
																goodlist.optJSONObject(k).getDouble("price"))) == 1) {
															postageAmount = postageAmount.add(pt.getDefaultPrice());
														}
													}
												}
											}
										}
									}
								}
							}
							// 每个订单商品总价不小于50元
							BigDecimal totalAmont = goodsAmount.add(postageAmount);
							if (totalAmont.compareTo(new BigDecimal(50)) != -1 && availableDeductionScore != 0) {
								flagscore = true;
								// 商品价格的30%的折扣金额
								BigDecimal deductionPrice = totalAmont.multiply(new BigDecimal(0.3));
								// 商品价格的30%的折扣积分
								int deductionScore = deductionPrice.multiply(new BigDecimal(100)).intValue();
								if (deductionScore >= availableDeductionScore) {
									totaldeductionScore = totaldeductionScore + availableDeductionScore;
									if (availableDeductionScore != 0) {
										availableDeductionScore = 0;
									}
								} else {
									totaldeductionScore = totaldeductionScore + deductionScore;
									availableDeductionScore = availableDeductionScore - deductionScore;
								}
							}

							totalgoodsAmount = totalgoodsAmount.add(goodsAmount);
							totalpostageAmount = totalpostageAmount.add(postageAmount);
						}
						if (flag) {// 不允许自提
							data.put("isSelf", 1);
						} else {// 允许自提
							data.put("isSelf", 0);
						}
						if (flagscore) {
							data.put("isUseScore", 0);
							data.put("deductionScore", totaldeductionScore);
							data.put("deductionPrice", totaldeductionScore / 100);
						} else {
							data.put("isUseScore", 1);
							data.put("deductionScore", "");
							data.put("deductionPrice", "");
						}
						data.put("score", score);
						data.put("goodsAmount", totalgoodsAmount.setScale(2, BigDecimal.ROUND_UP));
						data.put("postageAmount", totalpostageAmount.setScale(2, BigDecimal.ROUND_UP));
						result.put("data", data);
						result.put("code", "0001");
						result.put("message", "订单填写页面成功！");
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
			log.error("订单填写失败" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 订单确认，生成订单并返回订单号 type 1:从商品详情；2：购物车
	 */
	@SuppressWarnings("finally")
	@POST
	@Path("orderConfirm")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public String orderConfirm(String json) {
		JSONObject result = new JSONObject();
		JSONObject data = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		Integer addressId = jsonobject.optInt("addressId");
		Integer isSelf = jsonobject.optInt("isSelf");
		Integer isUserScore = jsonobject.optInt("isUserScore");
		String type = jsonobject.optString("type");
		String remark = jsonobject.optString("remark");
		JSONArray list = jsonobject.getJSONArray("list");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && list != null && list.isArray() && !list.isEmpty() && isSelf != null
					&& isUserScore != null && type != null && !type.isEmpty()) {
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				// if (userinfo != null) {
				if (result.optBoolean("flag")) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
						return result.toString();
					} else {// 判断是否选择自提
						UserAddress ua = null;
						if (isSelf != 0) {
							// 查看用户的收货地址
							map.clear();
							map.put("userId", userinfo.getId());
							map.put("addressId", addressId);
							List<UserAddress> uas = usermapper.getuserAddress(map);
							if (uas != null && !uas.isEmpty()) {
								ua = uas.get(0);
							} else {
								result.put("code", "0005");
								result.put("message", "请选择收货地址！");
								return result.toString();
							}
						}
						// boolean flag = false;
						// 总运费
						BigDecimal totalpostageAmount = new BigDecimal("0.00");
						// 特产总价
						BigDecimal totalgoodsAmount = new BigDecimal("0.00");
						// 折扣总积分
						Integer totaldeductionScore = 0;
						// 当前用户积分
						map.clear();
						map.put("userId", userinfo.getId());
						String score_str = usermapper.gettotalScore(map);
						Integer score = Integer.valueOf(score_str);
						// 当前用户所有可用积分
						Integer availableDeductionScore = (int) Math.floor(score / 100) * 100;
						result = consumeService.checkOrder(list, addressId, isSelf, isUserScore, userinfo.getId());
						if (result != null && !result.getBoolean("flag")) {
							return result.toString();
						}
						// 返回订单号列表
						JSONArray orderNolist = new JSONArray();
						String tradeNo = CodeUtils.gettradeNo();
						for (int i = 0; i < list.size(); i++) {
							// 订单参数
							Order or = new Order();
							or = consumeService.saveOrder(userinfo, tradeNo, 1, remark, 3);
							orderNolist.add(or.getOrderNo());
							JSONObject jb = list.getJSONObject(i);
							JSONArray ja = jb.getJSONArray("specialityList");
							JSONArray goodlist = new JSONArray();
							// 运费
							BigDecimal postageAmount = new BigDecimal("0.00");
							// 特产金额
							BigDecimal goodsAmount = new BigDecimal("0.00");
							// 特产遍历，封装到运费模板数组
							for (int j = 0; j < ja.size(); j++) {
								JSONObject jb1 = ja.getJSONObject(j);
								map.clear();
								map.put("goodId", jb1.get("goodId"));
								map.put("goodsStandardId", jb1.get("goodsStandardId"));
								Goods gs = shopmapper.getSpecialityDetailsForOrder(map);
								gs.setGoodNumber(jb1.optInt("goodNumber"));
								if (gs != null) {
									BigDecimal d = new BigDecimal(gs.getGoodNumber());
									BigDecimal price = gs.getPrice().multiply(d);
									// 计算特产数量和价格
									goodsAmount = goodsAmount.add(price);
									JSONObject goods = new JSONObject();
									if (j == 0) {
										goods.put("goodId", gs.getGoodId());
										goods.put("price", price);
										goods.put("freghtId", gs.getFreightId());
										goodlist.add(goods);
									} else {
										for (int l = 0; l < goodlist.size(); l++) {
											if (gs.getGoodId() == goodlist.optJSONObject(l).optInt("goodId")) {
												price = price.add(
														new BigDecimal(goodlist.optJSONObject(l).optDouble("price")));
												goodlist.optJSONObject(l).put("price", price);
											} else {
												goods.put("goodId", gs.getGoodId());
												goods.put("price", price);
												goods.put("freghtId", gs.getFreightId());
												goodlist.add(goods);
											}
										}
									}
									// 生成特产订单附属表同时减去相应规格的库存
									map.clear();
									map.put("goodId", gs.getGoodId());
									map.put("goodNumber", gs.getGoodNumber());
									map.put("goodsStandardId", gs.getGoodsStandardId());
									map.put("orderNo", or.getOrderNo());
									map.put("goodName", gs.getGoodName());
									map.put("goodsStandardName", gs.getGoodsStandardName());
									map.put("price", gs.getPrice());
									expenseMapper.saveGoodsExpand(map);
									map.remove("orderNo");
									map.put("type", "sub");
									shopmapper.operateSpecialityStandard(map);
								}
							} // 选择快递
							if (isSelf != 0) {
								// 计算运费
								if (goodlist != null && !goodlist.isEmpty()) {
									for (int k = 0; k < goodlist.size(); k++) {
										if (ua != null) {
											if (goodlist.optJSONObject(k).optInt("freghtId") != 0) {
												map.clear();
												map.put("id", goodlist.optJSONObject(k).optInt("freghtId"));
												Postagetemplate pt = shopmapper.getPostagetemplate(map);
												if (pt != null) {
													// 查看附加模板是否存在
													map.clear();
													map.put("provinceId", ua.getProvinceId());
													map.put("postageId", pt.getId());
													Postageappend pa = shopmapper.getPostageappend(map);
													if (pa != null) {// 默认邮费
														if (pa.getDefaultPrice().compareTo(new BigDecimal(0)) != 0) {
															if (pa.getMaxPrice().compareTo(new BigDecimal(goodlist
																	.optJSONObject(k).getDouble("price"))) == 1) {
																postageAmount = postageAmount.add(pa.getDefaultPrice());
															}
														}
														// 没有附件模板
													} else {
														if (pt.getDefaultPrice().compareTo(new BigDecimal(0)) != 0) {
															if (pt.getMaxPrice().compareTo(new BigDecimal(goodlist
																	.optJSONObject(k).getDouble("price"))) == 1) {
																postageAmount = postageAmount.add(pt.getDefaultPrice());
															}
														}
													}
												}
											}
										} else {
											result.put("code", "0005");
											result.put("message", "请选择收货地址！");
											return result.toString();
										}
									}
								}
							}
							// 可以使用积分折扣
							if (isUserScore == 0) {
								// 每个订单商品总价不小于50元
								BigDecimal totalAmont = goodsAmount.add(postageAmount);
								if (totalAmont.compareTo(new BigDecimal(50)) != -1) {
									// 商品价格的30%的折扣金额
									BigDecimal deductionPrice = totalAmont.multiply(new BigDecimal(0.3));
									// 商品价格的30%的折扣积分
									int deductionScore = deductionPrice.multiply(new BigDecimal(100)).intValue();
									if (deductionScore >= availableDeductionScore) {
										totaldeductionScore = totaldeductionScore + availableDeductionScore;
										if (availableDeductionScore != 0) {
											availableDeductionScore = 0;
										}
									} else {
										totaldeductionScore = totaldeductionScore + deductionScore;
										availableDeductionScore = availableDeductionScore - deductionScore;
									}
								}
							} // 生成订单
							totalgoodsAmount = totalgoodsAmount.add(goodsAmount).subtract(
									new BigDecimal(totaldeductionScore / 100).setScale(2, BigDecimal.ROUND_UP));
							totalpostageAmount = totalpostageAmount.add(postageAmount);
							or.setDeductionAmount(
									new BigDecimal(totaldeductionScore / 100).setScale(2, BigDecimal.ROUND_UP));
							or.setGoodsAmount(goodsAmount.setScale(2, BigDecimal.ROUND_UP));
							or.setPostageAmount(postageAmount.setScale(2, BigDecimal.ROUND_UP));
							or.setPayableAmount(goodsAmount.add(postageAmount).setScale(2, BigDecimal.ROUND_UP));
							or.setPaymentAmount(or.getPayableAmount().subtract(or.getDeductionAmount()).setScale(2,
									BigDecimal.ROUND_UP));
							expenseMapper.saveOrder(or);
							// 使用积分折扣
							Integer score2 = or.getDeductionAmount().multiply(new BigDecimal(100)).intValue();
							if (score2 > 0) {
								map.clear();
								map.put("userId", userinfo.getId());
								map.put("sourceId", 4);
								map.put("score", -score2);
								map.put("orderNo", or.getOrderNo());
								map.put("orderType", or.getOrderType());
								map.put("status",1);
								String addTime2 = df.format(new Date());
								map.put("addTime", addTime2);
								usermapper.savescore(map);
							}
							// 快递附属
							map.clear();
							map.put("orderNo", or.getOrderNo());
							map.put("shopId", jb.get("shopId"));
							map.put("isSelf", isSelf);
							if (isSelf == 1) {
								map.put("userAddressId", addressId);
							}
							expenseMapper.saveGoodsFreightExpand(map);
							// 订单操作记录表
							consumeService.savelog(or.getOrderNo(), userinfo.getId(), 1, 1);
							// 去除购物车记录
							if (type.equals("2")) {
								for (int j = 0; j < ja.size(); j++) {
									JSONObject jb1 = ja.getJSONObject(j);
									map.clear();
									map.put("goodId", jb1.get("goodId"));
									map.put("goodsStandardId", jb1.get("goodsStandardId"));
									map.put("userId", userinfo.getId());
									shopmapper.deleteCar(map);
								}
							}

						}
						//
						data.put("tradeNo", tradeNo);
						data.put("totalAmount",
								totalgoodsAmount.add(totalpostageAmount).setScale(2, BigDecimal.ROUND_HALF_DOWN));
						result.put("data", data);
						result.put("code", "0001");
						result.put("message", "订单生成成功！");
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
			log.error("订单生成失败" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 支付签名获取type:2:支付宝；1：微信；3:游乐币
	 * 
	 * useType 3:特产；7：腕带
	 * 
	 * 
	 * 同时校验订单状态是未支付状态
	 * 
	 * 返回回调地址（第三方接口）
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getSignKey")
	public String getSignKey(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		String useType = jsonobject.optString("useType");
		String tradeNo = jsonobject.optString("tradeNo");
		JSONObject data = new JSONObject();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && type != null && !type.isEmpty() && tradeNo != null
					&& !tradeNo.isEmpty()) {// &&useType!=null&&!useType.isEmpty()
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				// if (userinfo != null) {
				if (result.optBoolean("flag")) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
						return result.toString();
					} else {
						map.clear();
						map.put("tradeNo", tradeNo);
						List<Order> o = expenseMapper.getOrder(map);
						if (o != null && !o.isEmpty()) {
							if (o.get(0).getStatus() == 1) {
								if (type.equals("2")) {
									// String appID = GetProperties.getAppID();
									// String privateKey =
									// GetProperties.getPrivateKey();
									// data.put("appID", appID);
									// data.put("privateKey", privateKey);
									// if (useType.equals("3")) {
									// data.put("body", "特产购买");
									// data.put("notify_url",
									// GetProperties.getNotify_url_Alipay_expense());
									// } else if (useType.equals("7")) {
									// data.put("body", "腕带购买");
									// data.put("notify_url",
									// GetProperties.getNotify_url_Alipay_card());
									// } else {
									// data.put("body", "特产购买");
									// data.put("notify_url",
									// GetProperties.getNotify_url_Alipay_expense());
									// }
									// data.put("tradeNo", tradeNo);
									// data.put("enable_pay_channels", "");
									String body = "";
									Integer type_str = 0;
									if (useType.equals("3")) {
										body = "特产购买";
										type_str = 1;
									} else if (useType.equals("7")) {
										body = "腕带购买";
										type_str = 3;
									}
									String orderString = AlipayUtil.orderString(tradeNo,
											o.get(0).getPaymentAmount().setScale(2, BigDecimal.ROUND_UP).toString(),
											type_str, body);
									// result.put("data", orderString);
									if (orderString == null || orderString.isEmpty()) {
										result.put("code", "0000");
										result.put("message", "平台繁忙，请稍候再试！");
										return result.toString();
									}
									data.put("orderString", orderString);
									result.put("data", data);
									result.put("code", "0001");
									result.put("message", "获取签名成功！");
								} else if (type.equals("1")) {// 微信先生成预支付订单
									Integer u_type = 0;
									if (useType.equals("3")) {
										u_type = 1;
									} else if (useType.equals("7")) {
										u_type = 3;
									} else {
										u_type = 1;
									}
									Map<String, String> s = WeChatUtil.getPreyId(tradeNo,
											o.get(0).getPaymentAmount().multiply(new BigDecimal("100"))
													.setScale(2, BigDecimal.ROUND_HALF_UP).intValue(),
											u_type);
									if (s.get(WeChatUtil.ReturnCode).equals("SUCCESS")) {
										data.put("appid", s.get("appid"));
										data.put("partnerid", s.get("mch_id"));
										data.put("prepayid", s.get("prepay_id"));
										data.put("package", "Sign=WXPay");
										data.put("noncestr", s.get("nonce_str"));
										data.put("timestamp", WeChatUtil.getTenTimes());
										HashMap<String, String> d = new HashMap<String, String>();
										d.put("appid", s.get("appid"));
										d.put("partnerid", s.get("mch_id"));
										d.put("prepayid", s.get("prepay_id"));
										d.put("package", "Sign=WXPay");
										d.put("noncestr", data.getString("noncestr"));
										d.put("timestamp", data.getString("timestamp"));
										data.put("sign", WeChatUtil.getSign(d));
										System.out.println(data.toString());
										result.put("data", data);
										result.put("code", "0001");
										result.put("message", "获取微信签名成功！");
									} else {
										result.put("code", "0007");
										result.put("message", "获取微信支付签名失败！");
									}
								}
							} else {
								result.put("code", "0006");
								result.put("message", "该订单不是未支付状态，请确认！");
								return result.toString();
							}
						} else {
							result.put("code", "0005");
							result.put("message", "该订单不存在，请确认！");
							return result.toString();
						}
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
			log.error("获取签名" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
		}
		return result.toString();
	}

	/*
	 * 去支付（校验订单状态是未支付状态） 1 VIP 2 门票 3 特产 4 酒店 5 美食 6 线路
	 */
	@SuppressWarnings("finally")
	@POST
	@Path("payConfirm")
	@Transactional
	@Produces({ MediaType.APPLICATION_JSON })
	public String payConfirm(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String orderNo = jsonobject.optString("orderNo");
		String type = jsonobject.optString("type");
		boolean flag = false;
		JSONObject data = new JSONObject();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && orderNo != null && !orderNo.isEmpty() && type != null
					&& !type.isEmpty()) {
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				// if (userinfo != null) {
				if (result.optBoolean("flag")) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
					} else {
						map.clear();
						map.put("orderNo", orderNo);
						List<Order> o = expenseMapper.getOrder(map);
						if (o != null && !o.isEmpty()) {
							if (o.get(0).getStatus() == 1) {// 先查询合并订单号，
								if (type.equals("3")) {
									List<GoodsExpandDetail> gedList = expenseMapper.getGoodsExpandDetail(map);
									if (gedList != null && !gedList.isEmpty()) {
										for (GoodsExpandDetail ged : gedList) {
											if (ged.getWorkState() == 2 || ged.getWorkState() == 3) {
												result.put("code", "0010");
												result.put("message", "特产店铺已休息，无法支付！");
												return result.toString();
											} else if (ged.getIsSell() == 1) {
												result.put("code", "0010");
												result.put("message", "特产商品已下架，无法支付！");
												return result.toString();
											} else if (ged.getIsDel() == 1) {
												result.put("code", "0010");
												result.put("message", "特产商品已失效，无法支付！");
												return result.toString();
											}
										}
									}
									if (o.get(0).getTradeNo().equals("0")) {
										String tradeNo = CodeUtils.gettradeNo();
										map.clear();
										map.put("orderNo", orderNo);
										map.put("tradeNo1", tradeNo);
										expenseMapper.updateOrder(map);
										data.put("tradeNo", tradeNo);
										data.put("totalAmount", o.get(0).getPaymentAmount());
										flag = true;
									} else {
										map.clear();
										map.put("tradeNo", o.get(0).getTradeNo());
										map.put("status", 1);
										List<Order> oList = expenseMapper.getOrder(map);
										if (oList != null && !oList.isEmpty()) {
											for (Order order : oList) {
												if (!order.getOrderNo().equals(o.get(0).getOrderNo())) {
													map.clear();
													map.put("orderNo", order.getOrderNo());
													map.put("tradeNo1", "0");
													expenseMapper.updateOrder(map);
												}
											}
											data.put("tradeNo", o.get(0).getTradeNo());
											data.put("totalAmount", o.get(0).getPaymentAmount());
											flag = true;
										}
									}
								} else if (type.equals("7")) {
									data.put("tradeNo", o.get(0).getTradeNo());
									data.put("totalAmount", o.get(0).getPaymentAmount());
									flag = true;
								} else {
									result.put("code", "1000");
									result.put("message", "其他类型订单暂不支持，无法支付！");
									return result.toString();
								}
							}
							if (flag) {
								result.put("data", data);
								result.put("code", "0001");
								result.put("message", "操作成功！");
							} else {
								result.put("code", "0006");
								result.put("message", "该订单状态不是支付状态，无法支付！");
								return result.toString();
							}
						} else {
							result.put("code", "0005");
							result.put("message", "该订单为无效订单！");
							return result.toString();
						}
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
			log.error("订单去支付校验失败:" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 游乐币 type 1：消费；2：提现
	 * 
	 * 支付类型paymentType 3:游乐币
	 * 
	 * 消费类型useType 1:VIP; 2:门票;3: 特产;4: 酒店;5: 美食;6:线路；7：腕带
	 * 
	 * expenseType 1:充值;2:提现;3:消费; 4:退款
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Path("operateYlcoin")
	@Produces({ MediaType.APPLICATION_JSON })
	public String operateYlcoin(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		Double totalAmount = jsonobject.optDouble("totalAmount");
		Integer useType = jsonobject.optInt("useType");
		String payPwd = jsonobject.optString("payPwd");
		String tradeNo = jsonobject.optString("tradeNo");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && type != null && !type.isEmpty() && totalAmount != null) {
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				// if (userinfo != null) {
				if (result.optBoolean("flag")) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
					} else {
						if (type.equals("1")) {// 消费支付

							// if (result != null && !result.getBoolean("flag"))
							// {
							// return result.toString();
							// } else {
							map.put("tradeNo", tradeNo);
							BigDecimal total_amount = new BigDecimal("0.00");
							List<Order> orderList = expenseMapper.getOrder(map);
							if (orderList != null && !orderList.isEmpty()) {
								for (Order o : orderList) {
									total_amount = total_amount.add(o.getPaymentAmount());
									result = consumeService.checkOrderStatus(o);
									if (!result.optBoolean("flag")) {
										return result.toString();
									}
								}
								total_amount = total_amount.setScale(2, BigDecimal.ROUND_HALF_UP);
								BigDecimal totalAmount_D = new BigDecimal(totalAmount).setScale(2,
										BigDecimal.ROUND_HALF_UP);
								if (total_amount.compareTo(totalAmount_D) == 0) {
									result = consumeService.payment(userinfo, totalAmount, payPwd, type);
									if (result != null && !result.getBoolean("flag")) {
										return result.toString();
									}
									for (Order o : orderList) {
										JSONObject paySuccess = consumeService.paySuccess(userinfo, tradeNo, tradeNo,
												o.getPaymentAmount(), o.getOrderNo(), 3, useType, 3);
										if (paySuccess.get("code").equals("0000")) {
											result.put("code", "0000");
											result.put("message", "平台繁忙，请稍候再试！");
											return result.toString();
										}
									}
								} else {
									result.put("code", "0010");
									result.put("message", "支付异常，请重新下单！");
									return result.toString();
								}
							}
							result.put("message", "支付成功！");
							map.put("uuID", uuID);
							Userinfo userinfo1 = usermapper.Getuserinfo(map);
							result.put("data", userinfo1.getBalance());
							// 消息入库
							messageService.insertUserMessage("1", userinfo, 3, 3, total_amount);
							// }
						} else if (type.equals("2")) {
							result.put("code", "1000");
							result.put("message", "提现功能暂未开通！");
							return result.toString();
						}
						result.put("code", "0001");
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
			log.error("游乐币账户操作：" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 订单详情 type:2：门票；3：特产
	 */
	@SuppressWarnings("finally")
	@POST
	@Path("getOrderDetails")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public String getOrderDetails(String json) {
		JSONObject result = new JSONObject();
		JSONObject data = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String orderNo = jsonobject.optString("orderNo");
		String type = jsonobject.optString("type");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && orderNo != null && !orderNo.isEmpty() && type != null
					&& !type.isEmpty()) {
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				// if (userinfo != null) {
				if (result.optBoolean("flag")) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
					} else {
						map.clear();
						if (type.equals("3")) {
							map.clear();
							map.put("userId", userinfo.getId());
							map.put("orderType", 3);
							map.put("orderNo", orderNo);
							GoodsOrderDetail god = expenseMapper.getGoodsOrderDetail(map);
							if (god != null) {
								map.clear();
								map.put("id", god.getShopId());
								Shop s = shopmapper.getshopInfo(map);
								god.setShopName(s.getShopName());
								god.setShopFileCode(s.getFileCode());
								if (god.getIsSelf() == 1) {// 收货地址
									map.clear();
									map.put("id", god.getUserAddressId());
									map.put("userId", userinfo.getId());
									List<UserAddress> ua = usermapper.getuserAddress(map);
									if (ua != null && !ua.isEmpty()) {
										data.put("freightAddress", ua.get(0));
									} else {
										data.put("freightAddress", "");
									}
								}
								boolean flag = false;
								map.clear();
								map.put("orderNo", god.getOrderNo());
								List<GoodsExpandDetail> gedList = expenseMapper.getGoodsExpandDetail(map);
								JSONArray js = new JSONArray();
								boolean flag1 = consumeService.checkOrderEvaluate(god.getStatus(), god.getEndTime());
								if (gedList != null && !gedList.isEmpty()) {
									for (GoodsExpandDetail ged : gedList) {// 校验是否可以有评价入口
										if ((ged.getEvaluateId() == null || ged.getEvaluateId() == 0) && flag1) {
											flag = true;
										}
										map.clear();
										map.put("goodId", ged.getGoodId());
										List<SpecialityBanner> sbList = shopmapper.getSpecialityBannerList(map);
										if (sbList != null && !sbList.isEmpty()) {
											ged.setImgUrl(shopImgUrl + god.getShopFileCode() + "/Speciality/"
													+ ged.getFileCode() + "/" + sbList.get(0).getImgUrl());
										}
										js.add(ged);
									}
									data.put("goodsOrderList", js);
								} else {
									data.put("goodsOrderList", "");
								}
								if (flag) {
									god.setIsEvaluate(0);
								} else {
									god.setIsEvaluate(1);
								}
								data.put("goodsOrder", god);
							} else {
								result.put("code", "0005");
								result.put("message", "暂无订单详情信息！");
							}
						} else {
							result.put("code", "1000");
							result.put("message", "门票订单详情功能暂未开通！");
							return result.toString();
						}
						result.put("data", data);
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
			log.error("订单详情(type=" + type + "):" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 订单列表 type:2：门票；3：特产
	 * 
	 */
	@SuppressWarnings("finally")
	@POST
	@Path("getOrderList")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getOrderList(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		Integer pageNum = jsonobject.optInt("pageNum");
		Integer num = jsonobject.optInt("num");
		String orderNo = jsonobject.optString("orderNo");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && type != null && !type.isEmpty()) {
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				// if (userinfo != null) {
				if (result.optBoolean("flag")) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
					} else {
						map.clear();
						if (type.equals("2")) {
							result.put("code", "1000");
							result.put("message", "门票订单列表功能暂未开通！");
							return result.toString();
						} else if (type.equals("3")) {
							boolean flag = false;
							map.clear();
							map.put("userId", userinfo.getId());
							map.put("orderType", 3);
							if (orderNo != null && !orderNo.isEmpty()) {
								map.put("orderNo", orderNo);
							} else {
								if (pageNum != null && pageNum > 0 && num != null && num > 0) {
									map.put("start", (pageNum - 1) * num);
									map.put("num", num);
								} else {
									result.put("code", "0002");
									result.put("message", "参数不全！");
									return result.toString();
								}
							}
							List<GoodsOrder> list = expenseMapper.getGoodsOrder(map);
							JSONArray jsonList = new JSONArray();
							if (list != null && !list.isEmpty()) {
								for (GoodsOrder go : list) {// 遍历订单查询订单下的所属商品
									flag = false;
									map.clear();
									map.put("orderNo", go.getOrderNo());
									Integer goodNumber = 0;
									List<GoodsExpandDetail> gedList = expenseMapper.getGoodsExpandDetail(map);
									// 查询下单地址
									if (go.getIsSelf() != null && go.getUserAddressId() != null) {
										if (go.getIsSelf() == 1 && go.getUserAddressId() != 0) {
											map.clear();
											map.put("id", go.getUserAddressId());
											map.put("userId", userinfo.getId());
											List<UserAddress> ua = usermapper.getuserAddress(map);
											if (ua != null && !ua.isEmpty()) {
												go.setAddressInfo(ua.get(0).getAddressInfo());
												go.setCityName(ua.get(0).getCityName());
												go.setDistrictName(ua.get(0).getDistrictName());
												go.setRecipient(ua.get(0).getRecipient());
												go.setRecipientMobile(ua.get(0).getRecipientMobile());
												go.setProvinceName(ua.get(0).getProvinceName());
												go.setZip(ua.get(0).getZip());
											}
										}
									}
									JSONArray js = new JSONArray();
									JSONObject jsongo = new JSONObject();
									jsongo = JSONObject.fromObject(go);
									boolean flag1 = consumeService.checkOrderEvaluate(go.getStatus(), go.getEndTime());
									if (gedList != null && !gedList.isEmpty()) {
										for (GoodsExpandDetail ged : gedList) {// 校验是否可以有评价入口
											if ((ged.getEvaluateId() == null || ged.getEvaluateId() == 0) && flag1) {
												flag = true;
											}
											map.clear();
											map.put("goodId", ged.getGoodId());
											List<SpecialityBanner> sbList = shopmapper.getSpecialityBannerList(map);
											if (sbList != null && !sbList.isEmpty()) {
												ged.setImgUrl(shopImgUrl + go.getShopFileCode() + "/Speciality/"
														+ ged.getFileCode() + "/" + sbList.get(0).getImgUrl());
											}
											goodNumber = goodNumber + ged.getGoodNumber();
											js.add(ged);
										}
										go.setGoodsTotalNum(goodNumber);
										jsongo.put("goodsTotalNum", goodNumber);
										jsongo.put("goodsList", js);
									} else {
										jsongo.put("goodsList", "");
									}
									if (flag) {
										go.setIsEvaluate(0);
										jsongo.put("isEvaluate", 0);
									} else {
										go.setIsEvaluate(1);
										jsongo.put("isEvaluate", 1);
									}
									jsonList.add(jsongo);
								}
								result.put("orderList", jsonList);
							} else {
								result.put("code", "0001");
								result.put("orderList", "");
								if (pageNum == 1) {
									result.put("message", "暂无订单信息！");
								} else if (pageNum > 1) {
									result.put("message", "已无更多订单信息！");
								}
								return result.toString();
							}
						} else {
							result.put("code", "1000");
							result.put("message", "其他订单列表功能暂未开通！");
							return result.toString();
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
			log.error("订单列表(type=" + type + "):" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
		} finally {
			return result.toString();
		}
	}

	/*
	 * 订单操作1：取消；2：签收；3：申请退款；4：自提；5:删除
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Path("operateOrder")
	@Produces({ MediaType.APPLICATION_JSON })
	public String operateOrder(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String orderNo = jsonobject.optString("orderNo");
		String type = jsonobject.optString("type");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && orderNo != null && !orderNo.isEmpty() && type != null
					&& !type.isEmpty()) {
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				// if (userinfo != null) {
				if (result.optBoolean("flag")) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
					} else {
						map.clear();
						map.put("orderNo", orderNo);
						List<GoodsOrder> o = expenseMapper.getGoodsOrder(map);
						if (o != null && !o.isEmpty()) {
							if (type.equals("1")) {// 首先判断订单是未支付状态
								if (o.get(0).getStatus() == 1) {
									consumeService.operateOrder(o.get(0), userinfo, 2, 1);
									List<GoodsExpandDetail> gedList = expenseMapper.getGoodsExpandDetail(map);
									// 特产库存还原
									if (gedList != null && !gedList.isEmpty()) {
										for (GoodsExpandDetail ged : gedList) {
											consumeService.operateSpecialityStandard(ged.getGoodId(),
													ged.getGoodNumber(), ged.getGoodsStandardId());
										}
									} // 积分退还和将消耗的积分改为无效
									if (o.get(0).getDeductionAmount().intValue() > 0) {
										consumeService.updateStore(userinfo.getId(),o.get(0).getOrderNo(),o.get(0).getOrderType(), 2);
										consumeService.saveScore(userinfo,
												o.get(0).getDeductionAmount().intValue() * 100, 5,o.get(0).getOrderNo(),o.get(0).getOrderType(),2);
									}
								} else {
									result.put("code", "0006");
									result.put("message", "该订单无法取消！");
									return result.toString();
								}
							} else if (type.equals("5")) {// 删除订单(未支付，取消，失效，完成关闭)
								if (o.get(0).getStatus() == 2 || o.get(0).getStatus() == 3 || o.get(0).getStatus() == 10
										|| o.get(0).getStatus() == 8 || o.get(0).getStatus() == 9
										|| o.get(0).getStatus() == 12) {
									map.clear();
									map.put("orderNo", orderNo);
									map.put("userId", userinfo.getId());
									map.put("isDel", 1);
									expenseMapper.updateOrder(map);
									consumeService.savelog(orderNo, userinfo.getId(), 0, 1);
								} else {
									result.put("code", "0007");
									result.put("message", "该订单无法删除！");
									return result.toString();
								}
							} else if (type.equals("2")) {// 签收
								if (o.get(0).getStatus() == 5 && o.get(0).getIsSelf() == 1) {// 不是自提特产
									consumeService.operateOrder(o.get(0), userinfo, 6, 1);
								} else {
									result.put("code", "0008");
									result.put("message", "该订单无法签收！");
									return result.toString();
								}
							} else if (type.equals("4")) {// 自提
								if ((o.get(0).getStatus() == 4 || o.get(0).getStatus() == 13)
										&& o.get(0).getIsSelf() == 0) {// 自提特产
									consumeService.operateOrder(o.get(0), userinfo, 12, 1);
									consumeService.operateOrder(o.get(0), userinfo, 10, 1);
								} else {
									result.put("code", "0008");
									result.put("message", "该订单无法自提取货！");
									return result.toString();
								}
							} else if (type.equals("3")) {// 申请退款
								if (o.get(0).getStatus() == 4 || o.get(0).getStatus() == 5 || o.get(0).getStatus() == 6
										|| o.get(0).getStatus() == 11 || o.get(0).getStatus() == 13) {// 不是自提特产
									consumeService.operateOrder(o.get(0), userinfo, 7, 1);
								} else {
									result.put("code", "0008");
									result.put("message", "该订单无法申请退款！");
									return result.toString();
								}
							} else {
								result.put("code", "1000");
								result.put("message", "其他订单操作功能尚未开通！");
								return result.toString();
							}
							result.put("code", "0001");
							result.put("message", "操作成功！");
						} else {
							result.put("code", "0005");
							result.put("message", "该订单为无效订单！");
							return result.toString();
						}
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
			log.error("订单操作失败(type=" + type + "):" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 查看物流信息
	 */
	@POST
	@Path("getOrderLogistics")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getOrderLogistics(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String freightNo = jsonobject.optString("freightNo");
		String freightOrderNo = jsonobject.optString("freightOrderNo");
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject data = new JSONObject();
		boolean flag = false;
		try {
			if (uuID != null && !uuID.isEmpty() && freightNo != null && !freightNo.isEmpty() && freightOrderNo != null
					&& !freightOrderNo.isEmpty()) {
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				// if (userinfo != null) {
				if (result.optBoolean("flag")) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
					} else {
						JSONObject s = new JSONObject();
						s.put("LogisticCode", freightOrderNo);
						s.put("ShipperCode", freightNo);
						String re = httpUtilLogistics.getOrderTraces(s.toString());
						JSONObject rejson = JSONObject.fromObject(re);
						if (rejson != null) {
							if ((boolean) rejson.opt("Success") && rejson.optJSONArray("Traces").isArray()
									&& !rejson.optJSONArray("Traces").isEmpty()) {
								// List<Freight> freightList=
								// JSONArray.toList(rejson.optJSONArray("Traces"),
								// Freight.class);
								List<Freight> freightList = new ArrayList<Freight>();
								JSONArray freightListjsa = rejson.optJSONArray("Traces");
								int j = freightListjsa.size();
								for (int i = 0; i < freightListjsa.size(); i++) {
									Freight f = new Freight();
									f.setAcceptStation(freightListjsa.getJSONObject(i).getString("AcceptStation"));
									f.setAcceptTime(freightListjsa.getJSONObject(i).getString("AcceptTime"));
									if (freightListjsa.getJSONObject(i).containsKey("Remark")) {
										f.setRemark(freightListjsa.getJSONObject(i).getString("Remark"));
									}
									freightList.add(f);
								}
								Collections.reverse(freightList);
								data.put("freightList", freightList);
								flag = true;
							} else {
								data.put("freightList", "");
							}
						} else {
							data.put("freightList", "");
						}
						map.clear();
						map.put("freightNo", freightNo);
						String freighName = expenseMapper.getFreight(map);
						if (freighName != null && !freighName.isEmpty()) {
							data.put("freighName", freighName);
						} else {
							data.put("freighName", "");
						}
						data.put("freightNo", freightNo);
						data.put("freightOrderNo", freightOrderNo);
						result.put("data", data);
						if (flag) {
							result.put("message", "查看物流信息！");
						} else {
							result.put("message", "暂无物流信息！");
						}
						result.put("code", "0001");
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
			log.error("查看物流信息失败" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
		}
		return result.toString();
	}

	/*
	 * 商品可评价列表
	 * 
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getGoodsEvaluateList")
	public String getGoodsEvaluateList(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String orderNo = jsonobject.optString("orderNo");
		// JSONObject data = new JSONObject();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && orderNo != null && !orderNo.isEmpty()) {
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				// if (userinfo != null) {
				if (result.optBoolean("flag")) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
						return result.toString();
					} else {
						map.clear();
						map.put("userId", userinfo.getId());
						map.put("orderType", 3);
						map.put("orderNo", orderNo);
						GoodsOrderDetail god = expenseMapper.getGoodsOrderDetail(map);
						if (god != null) {
							map.clear();
							map.put("id", god.getShopId());
							Shop s = shopmapper.getshopInfo(map);
							god.setShopFileCode(s.getFileCode());
							boolean flag1 = consumeService.checkOrderEvaluate(god.getStatus(), god.getEndTime());
							if (flag1) {
								map.clear();
								map.put("orderNo", orderNo);
								List<GoodsExpandDetail> gedList = expenseMapper.getGoodsExpandDetail(map);
								List<GoodsExpandDetail> gedList1 = new ArrayList<GoodsExpandDetail>();
								if (gedList != null && !gedList.isEmpty()) {
									for (int i = 0; i < gedList.size(); i++) {
										// System.out.println(gedList.get(i));
										if (gedList.get(i).getEvaluateId() == 0 || gedList.get(i).getIsDel() == 1) {
											map.clear();
											map.put("goodId", gedList.get(i).getGoodId());
											List<SpecialityBanner> sbList = shopmapper.getSpecialityBannerList(map);
											if (sbList != null && !sbList.isEmpty()) {
												gedList.get(i)
														.setImgUrl(shopImgUrl + god.getShopFileCode() + "/Speciality/"
																+ gedList.get(i).getFileCode() + "/"
																+ sbList.get(0).getImgUrl());
											}
											gedList1.add(gedList.get(i));
										}
									}
									if (gedList1 != null && !gedList1.isEmpty()) {
										result.put("data", gedList1);
									} else {
										result.put("data", "");
									}
									// result.put("data", gedList);
									result.put("code", "0001");
									result.put("message", "特产评价列表！");
								} else {
									result.put("code", "0005");
									result.put("message", "目前无法评价！");
									return result.toString();
								}
							} else {
								result.put("code", "0005");
								result.put("message", "目前无法评价！");
								return result.toString();
							}
						} else {
							result.put("code", "0005");
							result.put("message", "该订单不存在！");
							return result.toString();
						}
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
			log.error("获取可评价列表异常：" , e);
			// TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
		}

		return result.toString();
	}

	/*
	 * 第三方充值接口 生成临时订单号 type:1：微信；2：支付宝
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Path("getRecharge")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getRecharge(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		Double totalAmount = jsonobject.optDouble("totalAmount");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && type != null && !type.isEmpty() && totalAmount != null) {
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				// if (userinfo != null) {
				if (result.optBoolean("flag")) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
					} else {
						BigDecimal total_Amount = new BigDecimal(totalAmount);
						total_Amount = total_Amount.setScale(2, BigDecimal.ROUND_HALF_UP);
						if (type.equals("1")) {
							map.clear();
							String tradeNo = CodeUtils.gettradeNo();
							String addTime = df.format(new Date());
							map.put("tradeNo", tradeNo);
							map.put("userId", userinfo.getId());
							map.put("paymentAmount", total_Amount);
							map.put("addTime", addTime);
							expenseMapper.saveTemporaryOrder(map);
							JSONObject data = new JSONObject();
							Map<String, String> s = WeChatUtil.getPreyId(tradeNo, total_Amount
									.multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP).intValue(),
									2);
							if (s.get(WeChatUtil.ReturnCode).equals("SUCCESS")) {
								data.put("appid", s.get("appid"));
								data.put("partnerid", s.get("mch_id"));
								data.put("prepayid", s.get("prepay_id"));
								data.put("package", "Sign=WXPay");
								data.put("noncestr", s.get("nonce_str"));
								data.put("timestamp", WeChatUtil.getTenTimes());
								HashMap<String, String> d = new HashMap<String, String>();
								d.put("appid", s.get("appid"));
								d.put("partnerid", s.get("mch_id"));
								d.put("prepayid", s.get("prepay_id"));
								d.put("package", "Sign=WXPay");
								d.put("noncestr", data.getString("noncestr"));
								d.put("timestamp", data.getString("timestamp"));
								data.put("tradeNo", tradeNo);
								data.put("sign", WeChatUtil.getSign(d));
								result.put("data", data);
							} else {
								result.put("code", "0007");
								result.put("message", "微信充值功能异常，请稍候操作！");
								return result.toString();
							}
						} else if (type.equals("2")) {
							map.clear();
							String tradeNo = CodeUtils.gettradeNo();
							String addTime = df.format(new Date());
							map.put("tradeNo", tradeNo);
							map.put("userId", userinfo.getId());
							map.put("paymentAmount", total_Amount);
							map.put("addTime", addTime);
							map.put("body", "充值");
							JSONObject data = new JSONObject();
							expenseMapper.saveTemporaryOrder(map);
							// String appID = GetProperties.getAppID();
							// String privateKey =
							// GetProperties.getPrivateKey();
							// data.put("appID", appID);
							// data.put("privateKey", privateKey);
							// //
							// "http://101.81.226.213:18080/YlServer/mvc/Alipay/rechargeNotify"
							// data.put("notify_url",
							// GetProperties.getNotify_url_Alipay_recharge());
							// data.put("enable_pay_channels", "moneyFund");
							// data.put("tradeNo", tradeNo);
							String total_Amount_Str = total_Amount.setScale(2, BigDecimal.ROUND_UP).toString();
							String orderString = AlipayUtil.orderString(tradeNo, total_Amount_Str, 2, "游乐币充值");
							if (orderString == null || orderString.isEmpty()) {
								result.put("code", "0000");
								result.put("message", "平台繁忙，请稍候再试！");
								return result.toString();
							}
							data.put("orderString", orderString);
							data.put("tradeNo", tradeNo);
							result.put("data", data);
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
			log.error("游乐币账户充值操作：" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 第三方提现接口 type:1：微信；2：支付宝Withdrawals
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Path("getWithdrawals")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getWithdrawals(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		String payPwd = jsonobject.optString("payPwd");
		Integer accountId = jsonobject.optInt("accountId");
		Double Amount = jsonobject.optDouble("Amount");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && type != null && !type.isEmpty() && Amount != null && payPwd != null
					&& !payPwd.isEmpty() && accountId != null && accountId > 0) {
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				// if (userinfo != null) {
				if (result.optBoolean("flag")) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
					} else {
						if (userinfo.getBalance().compareTo(new BigDecimal(Amount)) == -1) {
							result.put("code", "0005");
							result.put("message", "余额不足，无法提现！");
							return result.toString();
						}
						// 账户查找
						map.clear();
						map.put("operateId", userinfo.getId());
						map.put("operateType", 1);
						map.put("isDel", 0);
						map.put("accountId", accountId);
						map.put("typeId", Integer.parseInt(type));
						List<UserPayAccount> upa = usermapper.getUserPayAccount(map);
						if (upa != null && !upa.isEmpty()) {
							if (upa.get(0).getStatus() == 3) {
								result.put("code", "0006");
								result.put("message", "提现账号校验失败，不可提现！");
								return result.toString();
							}
						} else {
							result.put("code", "0007");
							result.put("message", "提现账号不存在，不可提现！");
							return result.toString();
						}
						map.clear();
						map.put("operateId", userinfo.getId());
						map.put("operateType", 1);
						map.put("accountId", accountId);
						map.put("status", 1);
						List<PresentApplication> paList = usermapper.getPresentApplication(map);
						if (paList != null && !paList.isEmpty() && paList.get(0).getStatus() == 1) {
							result.put("code", "0009");
							result.put("message", "该账号提现中状态，暂不可用！");
							return result.toString();
						}
						// 用户流水总和（不包括第三方消费）与用户余额一致
						map.clear();
						map.put("userId", userinfo.getId());
						map.put("type", 1);
						String amount = usermapper.gettotalAmount(map);
						if (userinfo.getBalance().compareTo(new BigDecimal(amount)) != 0) {
							result.put("code", "0010");
							result.put("message", "平台繁忙，请稍候再试或联系客服！");
							return result.toString();
						}
						// 账户扣除
						result = consumeService.payment(userinfo, Amount, payPwd, "3");
						if (result != null && !result.getBoolean("flag")) {
							return result.toString();
						}
						// 用户消费流水
						map.clear();
						String expenseUserNo = CodeUtils.gettransactionFlowCode(
								userinfo.getUserCode().substring(userinfo.getUserCode().length() - 6));
						map.put("expenseUserNo", expenseUserNo);
						// map.put("serialNo", serialNo);
						map.put("userId", userinfo.getId());
						map.put("paymentAmount",
								new BigDecimal(Amount).setScale(2, BigDecimal.ROUND_UP).multiply(new BigDecimal("-1")));
						map.put("useType", 0);
						map.put("paymentType", Integer.parseInt(type));
						map.put("expenseType", 2);
						// 线上
						map.put("sourceType", 1);
						map.put("addTime", df.format(new Date()));
						expenseMapper.saveExpenseUserlog(map);
						// 提现申请
						map.clear();
						map.put("typeId", Integer.parseInt(type));
						String addTime = df.format(new Date());
						map.put("expenseNo", expenseUserNo);
						map.put("accountId", accountId);
						map.put("operateId", userinfo.getId());
						map.put("operateType", 1);
						map.put("status", 1);
						map.put("amount", new BigDecimal(Amount));
						map.put("addTime", addTime);
						map.put("modifyTime", addTime);
						usermapper.savePresentApplication(map);
						result.put("message", "提现申请成功！");
						result.put("code", "0001");
						messageService.insertUserMessage("2", userinfo, Integer.parseInt(type), 2,
								new BigDecimal(Amount));
						JSONObject data = new JSONObject();
						data.put("accountName", CodeUtils.getAccountName(upa.get(0).getAccountName()));
						data.put("Amount", Amount);
						result.put("data", data);
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
			log.error("提现操作失败：" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 校验订单状态 type 1 充值 2消费
	 */
	@SuppressWarnings("finally")
	@POST
	@Path("getTradeNoStatus")
	@Transactional
	@Produces({ MediaType.APPLICATION_JSON })
	public String getTradeNoStatus(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String tradeNo = jsonobject.optString("tradeNo");
		String type = jsonobject.optString("type");
		boolean flag = false;
		JSONObject data = new JSONObject();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && tradeNo != null && !tradeNo.isEmpty() && type != null
					&& !type.isEmpty()) {
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				// if (userinfo != null) {
				if (result.optBoolean("flag")) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
					} else {
						map.put("tradeNo", tradeNo);
						map.put("userId", userinfo.getId());
						if (type.equals("1")) {
							TemporaryOrder to = expenseMapper.getTemporaryOrder(map);
							result.put("code", "0001");
							if (to != null && to.getExpenseUserNo() != null && !to.getExpenseUserNo().isEmpty()) {
								result.put("data", true);
							} else {
								result.put("data", false);
							}
						} else {
							map.clear();
							map.put("tradeNo", tradeNo);
							List<Order> o = expenseMapper.getOrder(map);
							result.put("code", "0001");
							if (o != null && !o.isEmpty() && o.get(0).getStatus() == 4) {
								result.put("data", true);
							} else {
								result.put("data", false);
							}
						}
					}
				} else {
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("订单状态校验失败:" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

}
