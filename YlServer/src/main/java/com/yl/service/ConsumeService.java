package com.yl.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import com.yl.Mail.MailConfig;
import com.yl.Mail.MailUtil;
import com.yl.Utils.AlipayUtil;
import com.yl.Utils.CodeUtils;
import com.yl.Utils.GetProperties;
import com.yl.Utils.MD5Utils;
import com.yl.beans.Admin;
import com.yl.beans.ExpenseUserLog;
import com.yl.beans.Goods;
import com.yl.beans.GoodsOrder;
import com.yl.beans.GoodsOrderDetail;
import com.yl.beans.Mail;
import com.yl.beans.Order;
import com.yl.beans.Postageappend;
import com.yl.beans.Postagetemplate;
import com.yl.beans.Shop;
import com.yl.beans.TemporaryOrder;
import com.yl.beans.UserAddress;
import com.yl.beans.Userinfo;
import com.yl.mapper.ExpenseMapper;
import com.yl.mapper.ShopMapper;
import com.yl.mapper.UserMapper;
import com.yl.pay.Wechat.WeChatUtil;
import com.yl.pay.Wechat.WechatConfig;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class ConsumeService {
	@Autowired
	private UserMapper usermapper;
	@Autowired
	private ShopMapper shopmapper;
	@Autowired
	private ExpenseMapper expenseMapper;

	@Autowired
	private MessageService messageService;
	@Autowired
	private static Logger log = Logger.getLogger(ConsumeService.class);

	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/*
	 * 确认订单是否可以生成
	 */
	@SuppressWarnings("unused")
	public JSONObject checkOrder(JSONArray list, Integer addressId, Integer isSelf, Integer isUseScore,
			Integer userId) {
		JSONObject result = new JSONObject();
		Map<String, Object> map = new HashMap<String, Object>();
		if (list != null && !list.isEmpty() && isUseScore != null && isSelf != null && userId != null) {
			try {
				// 查看用户的收货地址
				UserAddress ua = null;
				map.clear();
				if (addressId != null && addressId > 0) {
					map.put("userId", userId);
					map.put("addressId", addressId);
					List<UserAddress> uas = usermapper.getuserAddress(map);
					if (uas != null && !uas.isEmpty()) {
						ua = uas.get(0);
					}
				}
				// 当前用户积分
				map.clear();
				map.put("userId", userId);
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
					map.clear();
					map.put("id", jb.optInt("shopId"));
					map.put("type", 1);
					Shop s = shopmapper.getshopInfo(map);
					// 店铺是否正常营业
					if (s == null || s.getWorkState() == 2) {
						result.put("flag", false);
						result.put("code", "0010");
						result.put("message", "店铺休息，暂不营业！");
						return result;
					}
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
							if (gs.getIsSelf() == 1 && isSelf == 0) {
								result.put("flag", false);
								result.put("code", "0010");
								result.put("message", "特产不允许自提，无法下单！");
								return result;
							}
							if (gs.getIsSell() == 1) {
								result.put("flag", false);
								result.put("code", "0010");
								result.put("message", "特产已下架，无法下单！");
								return result;
							}
							if (gs.getGoodNumber() > gs.getInventory()) {
								result.put("flag", false);
								result.put("code", "0010");
								result.put("message", "库存不足，无法下单！");
								return result;
							}
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
										price = price.add(new BigDecimal(goodlist.optJSONObject(l).optDouble("price")));
										goodlist.optJSONObject(l).put("price", price);
									} else {
										goods.put("goodId", gs.getGoodId());
										goods.put("price", price);
										goods.put("freghtId", gs.getFreightId());
										goodlist.add(goods);
									}
								}
							}
						} else {
							result.put("flag", false);
							result.put("code", "0010");
							result.put("message", "特产商品已失效，无法下单！");
							return result;
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
					if ((totalAmont.compareTo(new BigDecimal(50)) == -1 || availableDeductionScore == 0)
							&& isUseScore == 0) {
						result.put("flag", false);
						result.put("code", "0010");
						result.put("message", "不满足积分折扣，无法下单！");
					}
				}
				result.put("flag", true);
				result.put("message", "订单校验成功！");
			} catch (Exception e) {
				log.error("订单校验失败" , e);
				result.put("flag", false);
				result.put("code", "0000");
				result.put("message", "平台繁忙，请稍候再试！");
			}
		} else {
			result.put("flag", false);
			result.put("code", "0002");
			result.put("message", "参数不全！");
		}
		return result;
	}

	public Order saveOrder(Userinfo userinfo, String tradeNo, Integer status, String remark, Integer orderType) {
		Order order = new Order();
		String addTime = df.format(new Date());
		String orderNo = getOrderNo(userinfo.getUserCode());
		// order.setOrderNo(CodeUtils.getorderCode(userinfo.getUserCode().substring(userinfo.getUserCode().length()
		// - 6)));
		order.setOrderNo(orderNo);
		order.setIsDel(0);
		order.setTradeNo(tradeNo);
		order.setOrderType(orderType);
		order.setRemark(remark);
		order.setStatus(status);
		order.setUserId(userinfo.getId());
		order.setAddTime(addTime);
		return order;
	}

	public void savelog(String orderNo, Integer operateId, Integer status, Integer operateType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderNo", orderNo);
		map.put("operateId", operateId);
		map.put("status", status);
		map.put("operateTime", df.format(new Date()));
		map.put("operateType", operateType);
		expenseMapper.saveOrderLog(map);
	}

	// 平台钱包 1:消费，2:充值到钱包，3:提现第三方平台
	public JSONObject payment(Userinfo userinfo, Double totalAmount, String payPwd, String type) {
		JSONObject result = new JSONObject();
		Map<String, Object> map = new HashMap<String, Object>();
		BigDecimal dd = new BigDecimal(totalAmount);
		BigDecimal balance = new BigDecimal("0.00");
		dd = dd.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		// try {
		if (userinfo.getStatus() == 2) {
			result.put("code", "0006");
			result.put("flag", false);
			result.put("message", "账户冻结，无法支付！");
			return result;
		}
		if (type.equals("1")) {// 支付消费
			// 判断是否开启0:关闭；1：开启// 免付金额小于支付金额
			if (userinfo.getIsOpen() == 1 && userinfo.getMinAmount().compareTo(dd) == -1) {
				if (payPwd == null || payPwd.isEmpty()) {
					result.put("flag", false);
					result.put("code", "0010");
					result.put("message", "支付密码不能为空！");
					return result;
				} else {
					if (!userinfo.getPayPwd().equals(MD5Utils.string2MD5(payPwd))) {
						result.put("flag", false);
						result.put("code", "0010");
						result.put("message", "支付密码不对！");
						return result;
					}
				}
			}
			// 判断账户余额是否充足
			if (userinfo.getBalance().compareTo(dd) == -1) {
				result.put("flag", false);
				result.put("code", "0010");
				result.put("message", "余额不足！");
				return result;
			}
			balance = userinfo.getBalance().subtract(dd);
		} else if (type.equals("2")) {// 充值到账
			balance = userinfo.getBalance().add(dd);
		} else {// 提現
			if (payPwd == null || payPwd.isEmpty()) {
				result.put("flag", false);
				result.put("code", "0010");
				result.put("message", "支付密码不能为空！");
				return result;
			} else {
				if (userinfo.getPayPwd() != null && !userinfo.getPayPwd().isEmpty()) {
					if (!userinfo.getPayPwd().equals(MD5Utils.string2MD5(payPwd))) {
						result.put("flag", false);
						result.put("code", "0010");
						result.put("message", "支付密码不对！");
						return result;
					}
				} else {
					result.put("flag", false);
					result.put("code", "0010");
					result.put("message", "支付密码不能为空，无法提现！");
					return result;
				}
			}
			balance = userinfo.getBalance().subtract(dd);
		}
		map.clear();
		map.put("userId", userinfo.getId());
		map.put("status", 1);
		balance = balance.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		map.put("balance", balance);
		usermapper.updateUserAmount(map);
		result.put("flag", true);
		result.put("code", "0001");
		result.put("message", "操作成功！");
		// } catch (Exception e) {
		// log.error("账户操作异常", e);
		// result.put("flag", false);
		// result.put("code", "0000");
		// result.put("message", "平台繁忙，请稍候操作！");
		// }
		return result;
	}

	public JSONObject paySuccess(Userinfo userinfo, String serialNo, String tradeNo, BigDecimal paymentAmount,
			String orderNo, Integer paymentType, Integer useType, Integer expenseType) {
		JSONObject result = new JSONObject();
		paymentAmount = paymentAmount.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		Map<String, Object> map = new HashMap<String, Object>();
		// try {
		// 用户消费流水
		map.clear();
		String expenseUserNo = CodeUtils
				.gettransactionFlowCode(userinfo.getUserCode().substring(userinfo.getUserCode().length() - 6));
		map.put("expenseUserNo", expenseUserNo);
		map.put("serialNo", serialNo);
		map.put("userId", userinfo.getId());
		if (expenseType == 3 || expenseType == 2) {
			map.put("paymentAmount", new BigDecimal("-" + paymentAmount));
		} else {
			map.put("paymentAmount", paymentAmount);
		}
		map.put("useType", useType);
		map.put("paymentType", paymentType);
		map.put("expenseType", expenseType);
		// 线上
		map.put("sourceType", 1);
		map.put("addTime", df.format(new Date()));
		expenseMapper.saveExpenseUserlog(map);
		if (expenseType == 1) {// 充值，临时订单关联用户流水编号
			map.put("tradeNo", tradeNo);
			map.put("expenseUserNo", expenseUserNo);
			expenseMapper.updateTemporaryOrder(map);
		}
		if (expenseType == 1 || expenseType == 2 || (expenseType == 3 && (paymentType == 1 || paymentType == 2))) { // 平台交易流水(充值或提现或者第三方消费)
			map.clear();
			map.put("parentId", -1);
			map.put("adminType", 1);
			Admin ad = expenseMapper.getAdmin(map);
			String expenseSystemNo = CodeUtils
					.gettransactionFlowCode(ad.getAdminCode().substring(ad.getAdminCode().length() - 6));
			map.put("expenseSystemNo", expenseSystemNo);
			map.put("expenseNo", expenseUserNo);
			map.put("adminId", ad.getId());
			// map.put("scenicId", ad.getFk());
			if (expenseType == 2) {
				paymentAmount = paymentAmount.multiply(new BigDecimal("-1"));
			}
			map.put("amount", paymentAmount);
			map.put("operateType", 1);// 游乐app用户
			map.put("addTime", df.format(new Date()));
			expenseMapper.saveExpenseSystemlog(map);
		}
		if (expenseType == 3) {// 消费
			// 订单状态改为支付完成
			map.clear();
			map.put("userId", userinfo.getId());
			map.put("expenseId", expenseUserNo);
			// if(useType==7){
			// map.put("endTime", df.format(new Date()));
			// }
			map.put("status", 4);
			map.put("orderNo", orderNo);
			expenseMapper.updateOrder(map);
			// 订单操作记录
			savelog(orderNo, userinfo.getId(), 4, 1);
			// 积分回赠 sourceId 2:消费赠送积分 (只针对特产消费)
			if (useType == 3) {
				BigDecimal divisor = new BigDecimal(50);// 支付宝和微信消费，积分赠送比例50:1
				if (paymentType == 3) {// 平台钱包10:1
					divisor = new BigDecimal(10);
				}
				Integer score1 = paymentAmount.divide(divisor).intValue();
				if (score1 > 0) {
					map.clear();
					map.put("userId", userinfo.getId());
					map.put("sourceId", 2);
					map.put("score", score1);
					map.put("status", 1);
					map.put("orderNo", orderNo);
					map.put("orderType", useType);
					String addTime1 = df.format(new Date());
					map.put("addTime", addTime1);
					usermapper.savescore(map);
					updateLevel(userinfo);
				}
			}
		}
		result.put("code", "0001");
		// } catch (Exception e) {
		// log.error("添加交易流水，修改订单状态失败(Service)：" ,e);
		// result.put("code", "0000");
		// }
		return result;
	}

	// 订单校验商品是否有评价接口
	public boolean checkOrderEvaluate(Integer status, String endTime) {
		boolean flag = false;
		Calendar time = Calendar.getInstance();
		if (status == 6 || status == 7 || status == 8 || status == 9 || status == 12) {
			flag = true;
		} else if (status == 10) {// 订单关闭1个月内可以评价
			if (endTime != null) {
				time.add(Calendar.MONTH, -1);
				Date nowdate = null;
				try {
					Date endTime1 = df.parse(endTime);
					nowdate = time.getTime();
					if (endTime1.getTime() >= nowdate.getTime()) {
						flag = true;
					}
				} catch (ParseException e) {
					log.error("订单是否可以评价校验方法的时间转化异常（checkOrderEvaluate）：", e);
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	// 评价接口中的校验
	public JSONObject checkEvaluate(String orderNo, Userinfo userinfo) {
		JSONObject result = new JSONObject();
		Calendar time = Calendar.getInstance();
		Map<String, Object> map = new HashMap<String, Object>();
		map.clear();
		map.put("userId", userinfo.getId());
		map.put("orderType", 3);
		map.put("orderNo", orderNo);
		GoodsOrderDetail god = expenseMapper.getGoodsOrderDetail(map);
		if (god != null) {
			if (god.getStatus() == 6 || god.getStatus() == 7 || god.getStatus() == 8 || god.getStatus() == 9) {
				result.put("flag", true);
			} else if (god.getStatus() == 10) {
				time.add(Calendar.MONTH, -1);
				Date nowdate = null;
				try {
					Date endTime1 = df.parse(god.getEndTime());
					nowdate = time.getTime();
					if (endTime1.getTime() >= nowdate.getTime()) {
						result.put("flag", true);
					} else {
						result.put("flag", false);
						result.put("code", "0010");
						result.put("message", "当前订单已关闭，无法评价！");
					}
				} catch (ParseException e) {
					result.put("flag", false);
					result.put("code", "0000");
					result.put("message", "平台繁忙，请稍候！");
					log.error("评价接口里是否可以评价校验方法的时间转化异常（checkEvaluate）：", e);
					e.printStackTrace();
				}
			} else {
				result.put("flag", false);
				result.put("code", "0010");
				result.put("message", "当前订单无法评价！");
			}
		} else {
			result.put("flag", false);
			result.put("code", "0010");
			result.put("message", "订单不存在，无法评价！");
		}
		return result;
	}

	// 取消特产订单附属表同时还原相应规格的库存
	public void operateSpecialityStandard(Integer goodId, Integer goodNumber, Integer goodsStandardId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.clear();
		map.put("goodId", goodId);
		map.put("goodNumber", goodNumber);
		map.put("goodsStandardId", goodsStandardId);
		map.put("type", "add");
		shopmapper.operateSpecialityStandard(map);
	}

	// paymentType 1:微信；2：支付宝
	public boolean checktradeNo(BigDecimal total_amount, List<Order> list, String app_id, String seller_id,
			Integer paymentType) {
		boolean flag = false;
		String appID = GetProperties.getAppID();
		String sellerID = GetProperties.getSellerId();
		if (paymentType == 1) {
			appID = WechatConfig.AppId;
			sellerID = WechatConfig.MchId;
		} else {
			appID = GetProperties.getAppID();
			sellerID = GetProperties.getSellerId();
		}
		BigDecimal totalAmont = new BigDecimal("0.00");
		for (Order o : list) {
			totalAmont = o.getPaymentAmount().add(totalAmont);
		}
		if (total_amount.compareTo(totalAmont) == 0 && appID.equals(app_id) && sellerID.equals(seller_id)) {
			flag = true;
		}
		return flag;
	}

	// 支付宝回调地址的公共方法（特产消费和游乐币充值）
	@Transactional
	public void orderAliPayNotify(HttpServletRequest request, HttpServletResponse response, Integer paymentType,
			Integer useType, Integer expenseType, String type) {
		log.info("[/order/pay/notify]");
		String resultResponse = "failure";
		PrintWriter printWriter = null;
		// 获取到返回的所有参数 先判断是否交易成功trade_status 再做签名校验
		// 1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
		// 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
		// 3、校验通知中的seller_id（或者seller_email)
		// 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email），
		// 4、验证app_id是否为该商户本身。上述1、2、3、4有任何一个验证不通过，则表明本次通知是异常通知，务必忽略。
		// 在上述验证通过后商户必须根据支付宝不同类型的业务通知，正确的进行不同的业务处理，并且过滤重复的通知结果数据。
		// 在支付宝的业务通知中，只有交易通知状态为TRADE_SUCCESS或TRADE_FINISHED时，支付宝才会认定为买家付款成功。
		// if ("TRADE_SUCCESS".equals(request.getParameter("trade_status"))) {
		System.out.println(request);
		Enumeration<?> pNames = request.getParameterNames();
		Map<String, String> param = new HashMap<String, String>();
		Map<String, Object> map = new HashMap<String, Object>();
		String trade_no = null;
		String tradeNo = null;
		try {
			while (pNames.hasMoreElements()) {
				String pName = (String) pNames.nextElement();
				param.put(pName, request.getParameter(pName));
			}
			if (param != null && !param.isEmpty()) {
				// AlipaySignature.
				boolean signVerified = AlipaySignature.rsaCheckV1(param, AlipayUtil.ALIPAY_PUBLIC_KEY,
						AlipayConstants.CHARSET_UTF8); // 校验签名是否正确
				printWriter = response.getWriter();
				tradeNo = param.get("out_trade_no");
				BigDecimal total_amount = new BigDecimal(param.get("total_amount"));
				String app_id = param.get("app_id");
				String seller_id = param.get("seller_id");
				trade_no = param.get("trade_no");
				if (signVerified) {
					// TODO 验签成功后
					// 按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success，校验失败返回failure
					if ("TRADE_SUCCESS".equals(request.getParameter("trade_status"))) {
						map.put("tradeNo", tradeNo);
						if (type.equals("1")) {// 消费
							List<Order> orderList = expenseMapper.getOrder(map);
							if (orderList != null && !orderList.isEmpty()) {
								map.clear();
								map.put("id", orderList.get(0).getUserId());
								Userinfo userinfo = usermapper.Getuserinfo(map);
								if (checktradeNo(total_amount, orderList, app_id, seller_id, paymentType)
										&& userinfo != null) {
									for (Order o : orderList) {
										if (o.getStatus() == 1) {
											paySuccess(userinfo, trade_no, tradeNo, o.getPaymentAmount(),
													o.getOrderNo(), paymentType, useType, expenseType);
										}
									}
									log.info("订单支付成功：" + param.toString());
									resultResponse = "success";
									messageService.insertUserMessage("1", userinfo, paymentType, expenseType,
											total_amount);
								}
							}
						} else if (type.equals("2")) {// 充值
							TemporaryOrder to = expenseMapper.getTemporaryOrder(map);
							map.clear();
							if (to != null && to.getExpenseUserNo() == null) {
								map.put("id", to.getUserId());
								Userinfo userinfo = usermapper.Getuserinfo(map);
								if (userinfo != null) {
									System.out.println("1:");
									if (to.getPaymentAmount().compareTo(total_amount) == 0) {
										JSONObject payMnet = payment(userinfo, total_amount.doubleValue(), null, "2");
										if (payMnet != null && payMnet.getBoolean("flag")) {// 充值成功
											log.info("支付宝充值成功：" + param.toString());
											System.out.println("2:");
											JSONObject paySuccess = paySuccess(userinfo, trade_no, tradeNo,
													total_amount, null, paymentType, useType, expenseType);
											System.out.println("3:" + paySuccess.get("code"));
											// if (!paySuccess.isEmpty() &&
											// paySuccess.get("code").equals("0001"))
											// {
											resultResponse = "success";
											// paySuccess.get("4:");
											messageService.insertUserMessage("2", userinfo, paymentType, expenseType,
													total_amount);
											// }
										}
									}
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			log.error("alipay notify error :", e);
			// 失败发送邮箱
			if (resultResponse.equals("fail")) {
				Mail mail = new Mail();
				mail.setHost(MailConfig.host); // 设置邮件服务器,如果不用163的,自己找找看相关的
				mail.setSender(MailConfig.sender);
				mail.setReceiver(MailConfig.receiver); // 接收人
				mail.setUsername(MailConfig.sender); // 登录账号,一般都是和邮箱名一样吧
				mail.setPassword(MailConfig.password); // 发件人邮箱的登录密码
				mail.setSubject("支付宝回调接口异常");
				if (type.equals("2")) {// 充值失败
					mail.setMessage("	充值失败：第三方交易流水号（trade_no）:" + trade_no + ";平台合并订单号（tradeNo）" + tradeNo);
				} else {// 消费
					mail.setMessage("	消费失败：第三方交易流水号（trade_no）:" + trade_no + ";平台合并订单号（tradeNo）" + tradeNo);
				}
				MailUtil.send(mail);
			}
			printWriter.close();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			if (printWriter != null) {
				printWriter.print(resultResponse);
			}
		}

	}

	public void saveScore(Userinfo userinfo, Integer score, Integer sourceId, String orderNo, Integer orderType,
			Integer status) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userinfo.getId());
		map.put("sourceId", sourceId);
		map.put("score", score);
		map.put("status", status);
		map.put("orderNo", orderNo);
		map.put("orderType", orderType);
		String addTime = df.format(new Date());
		map.put("addTime", addTime);
		usermapper.savescore(map);
		updateLevel(userinfo);
	}

	public void operateOrder(GoodsOrder o, Userinfo userinfo, Integer status, Integer operateType) {
		// try {
		Map<String, Object> map = new HashMap<String, Object>();
		map.clear();
		map.put("orderNo", o.getOrderNo());
		map.put("userId", userinfo.getId());
		map.put("status", status);
		if (status == 2 || status == 8 || status == 10) {// 取消或者退款加入订单关闭时间或者订单关闭
			map.put("endTime", df.format(new Date()));
		}
		expenseMapper.updateOrder(map);
		savelog(o.getOrderNo(), userinfo.getId(), status, operateType);
		if (status == 6) {// 收货，加入商家流水
			map.clear();
			map.put("fk", o.getShopId());
			map.put("parentId", 0);
			map.put("adminType", 3);
			Admin ad = expenseMapper.getAdmin(map);
			map.clear();
			map.put("orderNo", o.getOrderNo());
			map.put("userId", userinfo.getId());
			List<Order> oo = expenseMapper.getOrder(map);
			if (ad != null && oo != null && !oo.isEmpty() && !oo.get(0).getExpenseId().isEmpty()) {
				map.clear();
				String expenseSpecialtyNo = CodeUtils
						.gettransactionFlowCode(ad.getAdminCode().substring(ad.getAdminCode().length() - 6));
				map.put("expenseSpecialtyNo", expenseSpecialtyNo);
				map.put("expenseUserNo", oo.get(0).getExpenseId());
				map.put("adminId", ad.getId());
				map.put("amount", o.getPaymentAmount());
				map.put("expenseType", 4);
				map.put("orderNo", o.getOrderNo());
				map.put("addTime", df.format(new Date()));
				expenseMapper.saveExpenseSpecialtylog(map);
			}
		}
		// } catch (Exception e) {
		// log.error("订单状态修改异常(service:operateOrder):" ,e);
		// }
	}

	// 微信回调地址的公共方法（特产消费和游乐币充值）
	public void orderWeChatPayNotify(HttpServletRequest request, HttpServletResponse response, Integer paymentType,
			Integer useType, Integer expenseType, String type) {
		PrintWriter printWriter = null;
		HashMap<String, String> param1 = new HashMap<String, String>();
		param1.put("return_code", "FAIL");
		param1.put("return_msg", "fail");
		String trade_no = null;
		String tradeNo = null;
		try {
			InputStream inStream = request.getInputStream();
			ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outSteam.write(buffer, 0, len);
			}
			outSteam.close();
			inStream.close();
			String result = new String(outSteam.toByteArray(), "utf-8");// 获取微信调用我们notify_url的返回信息
			Map<String, String> param = WeChatUtil.getInfoByXml(result);
			printWriter = response.getWriter();
			if (param != null && !param.isEmpty()) {
				Map<String, Object> map = new HashMap<String, Object>();
				tradeNo = param.get("out_trade_no");
				BigDecimal total_fee = new BigDecimal(param.get("total_fee"));
				String app_id = param.get("appid");
				String seller_id = param.get("mch_id");
				trade_no = param.get("transaction_id");
				if (param.get("result_code").equals("SUCCESS")) {
					map.put("tradeNo", tradeNo);
					if (type.equals("1")) {// 消费
						List<Order> orderList = expenseMapper.getOrder(map);
						if (orderList != null && !orderList.isEmpty()) {
							map.clear();
							map.put("id", orderList.get(0).getUserId());
							Userinfo userinfo = usermapper.Getuserinfo(map);
							if (checktradeNo(total_fee.divide(new BigDecimal(100)), orderList, app_id, seller_id,
									paymentType) && userinfo != null) {
								for (Order o : orderList) {
									if (o.getStatus() == 1) {
										paySuccess(userinfo, trade_no, tradeNo, o.getPaymentAmount(), o.getOrderNo(),
												paymentType, useType, expenseType);
									}
								}
								log.info("微信订单支付成功：" + param.toString());
								param1.put("return_code", "SUCCESS");
								param1.put("return_msg", "OK");
								map.clear();
								map.put("userId", orderList.get(0).getUserId());
								map.put("serialNo", trade_no);
								List<ExpenseUserLog> list = expenseMapper.getexpenselogList(map);
								if (list != null && !list.isEmpty()) {
									messageService.insertUserMessage("1", userinfo, paymentType, expenseType, total_fee
											.divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP));
								}
							}
						}
					} else if (type.equals("2")) {// 充值
						TemporaryOrder to = expenseMapper.getTemporaryOrder(map);
						map.clear();
						map.put("id", to.getUserId());
						Userinfo userinfo = usermapper.Getuserinfo(map);
						if (to != null && userinfo != null && to.getExpenseUserNo() == null) {
							if (to.getPaymentAmount().compareTo(total_fee.divide(new BigDecimal(100))) == 0) {
								JSONObject payMnet = payment(userinfo,
										total_fee.divide(new BigDecimal(100)).doubleValue(), null, "2");
								if (payMnet != null && payMnet.getBoolean("flag")) {// 充值成功
									JSONObject paySuccess = paySuccess(userinfo, trade_no, tradeNo,
											total_fee.divide(new BigDecimal(100)), null, paymentType, useType,
											expenseType);
									if (paySuccess != null && paySuccess.get("code").equals("0001")) {
										param1.put("return_code", "SUCCESS");
										param1.put("return_msg", "OK");
										log.info("微信充值成功：" + param.toString());
										map.clear();
										map.put("userId", to.getUserId());
										map.put("serialNo", trade_no);
										List<ExpenseUserLog> list = expenseMapper.getexpenselogList(map);
										if (list != null && !list.isEmpty()) {
											messageService.insertUserMessage("2", userinfo, paymentType, expenseType,
													total_fee.divide(new BigDecimal(100)).setScale(2,
															BigDecimal.ROUND_HALF_UP));
										}
									}
								}
							}
						}
					}
				} else {
					log.error("微信订单支付失败：" + param.toString());
				}
			}
		} catch (Exception e) {
			log.error("alipay notify error :", e);
			// 失败发送邮箱
			if (param1.get("return_code").equals("FAIL")) {
				Mail mail = new Mail();
				mail.setHost(MailConfig.host); // 设置邮件服务器,如果不用163的,自己找找看相关的
				mail.setSender(MailConfig.sender);
				mail.setReceiver(MailConfig.receiver); // 接收人
				mail.setUsername(MailConfig.sender); // 登录账号,一般都是和邮箱名一样吧
				mail.setPassword(MailConfig.password); // 发件人邮箱的登录密码
				mail.setSubject("微信回调接口异常");
				if (type.equals("2")) {// 充值失败
					mail.setMessage("	充值失败：第三方交易流水号（trade_no）:" + trade_no + ";平台合并订单号（tradeNo）" + tradeNo);
				} else {// 消费
					mail.setMessage("	消费失败：第三方交易流水号（trade_no）:" + trade_no + ";平台合并订单号（tradeNo）" + tradeNo);
				}
				MailUtil.send(mail);
			}
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			printWriter.close();
		} finally {
			if (printWriter != null) {
				String s = WeChatUtil.creatXml(param1);
				System.out.println(s);
				printWriter.print(s);
			}
		}
	}

	public String getOrderNo(String userCode) {
		String orderNo = null;
		String orderNoby = null;
		Map<String, String> map = new HashMap<String, String>();
		do {
			orderNo = CodeUtils.getorderCode(userCode.substring(userCode.length() - 6));
			map.clear();
			map.put("orderNo", orderNo);
			orderNoby = expenseMapper.getOrderNo(map);
		} while (orderNo == orderNoby);
		return orderNo;
	}

	public void saveUserLog(Userinfo userinfo, Integer expenseType, BigDecimal paymentAmount, Integer useType,
			Integer paymentType) {
		Map<String, Object> map = new HashMap<String, Object>();
		String expenseUserNo = CodeUtils
				.gettransactionFlowCode(userinfo.getUserCode().substring(userinfo.getUserCode().length() - 6));
		map.put("expenseUserNo", expenseUserNo);
		map.put("userId", userinfo.getId());
		if (expenseType == 3 || expenseType == 2) {
			map.put("paymentAmount", new BigDecimal("-" + paymentAmount));
		} else {
			map.put("paymentAmount", paymentAmount);
		}
		map.put("useType", useType);
		map.put("paymentType", paymentType);
		map.put("expenseType", expenseType);
		// 线上
		map.put("sourceType", 1);
		map.put("addTime", df.format(new Date()));
		expenseMapper.saveExpenseUserlog(map);
	}

	// 订单状态的校验
	public JSONObject checkOrderStatus(Order o) {
		JSONObject result = new JSONObject();
		boolean flag = false;
		String code = "0010";
		String message = "订单状态已改变，请到我的订单查看！";
		switch (o.getStatus()) {
		case 1:
			flag = true;
			break;
		case 2:
			message = "订单已取消，无法支付！";
			break;
		case 3:
			message = "订单已失效，无法支付！";
			break;
		case 4:
			message = "订单已支付，请不要重复支付！";
			break;
		case 10:
			message = "订单已关闭，无法支付！";
			break;
		default:
			flag = false;
			break;
		}
		result.put("flag", flag);
		result.put("code", code);
		result.put("message", message);
		return result;
	}

	// 修改积分为无效状态
	public void updateStore(Integer userId, String orderNo, Integer orderType, Integer status) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderNo", orderNo);
		map.put("orderType", orderType);
		map.put("status", status);
		map.put("userId", userId);
		usermapper.updateStore(map);
	}

	// 设置用户
	public void updateLevel(Userinfo userinfo) {
		Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", 1);
			map.put("userId", userinfo.getId());
			String score = usermapper.gettotalScore(map);
			Integer level = getLevel(Integer.valueOf(score));
			if (level != userinfo.getLeavel() && userinfo.getLeavel() != null && level > userinfo.getLeavel()) {
				userinfo.setLeavel(level);
				usermapper.Updateuserinfo(userinfo);
			}	
	}

	public Integer getLevel(Integer score) {
		Integer level = 0;
		if (score > 0 && score < 500) {
			level = 1;
		} else if (score >= 500 && score < 1000) {
			level = 2;
		} else if (score > 1000 && score < 2000) {
			level = 3;
		} else if (score >= 2000 && score < 3000) {
			level = 4;
		} else if (score >= 3000) {
			level = 5;
		}
		return level;
	}

}
