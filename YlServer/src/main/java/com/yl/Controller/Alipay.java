package com.yl.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.yl.service.ConsumeService;

@Controller
public class Alipay {
//	private static Logger log = Logger.getLogger(Alipay.class);
	private static Logger log = Logger.getLogger(Alipay.class);
//	@Autowired
//	private UserMapper usermapper;
//	@Autowired
//	private ExpenseMapper expenseMapper;
	@Autowired
	private ConsumeService consumeService;

	/**
	 * 订单支付支付宝服务器异步通知(用于特产消费回调地址)
	 * 
	 * @param request
	 * @param response
	 *            paymentType 1:微信;2:支付宝 ;3:游乐币 useType 1:VIP; 2:门票;3: 特产;4:
	 *            酒店;5: 美食;6:线路 expenseType 1:充值;2:提现;3:消费; 4:退款
	 */
	@RequestMapping(value = "/Alipay/expenseNotify", method ={ RequestMethod.POST,RequestMethod.GET})
	public void orderPayNotify(HttpServletRequest request, HttpServletResponse response) {
		log.info("[/order/pay/notify]");
		consumeService.orderAliPayNotify(request, response, 2, 3, 3,"1");

	}
	
	/**
	 * 订单支付支付宝服务器异步通知(用于用户充值回调地址)
	 * type:1:消费；2：充值
	 * @param request
	 * @param response
	 *            paymentType 1:微信;2:支付宝 ;3:游乐币
	 *             useType 1:VIP; 2:门票;3: 特产;4:酒店;5: 美食;6:线路 
	 *             expenseType 1:充值;2:提现;3:消费; 4:退款
	 */
	@RequestMapping(value = "/Alipay/rechargeNotify", method ={ RequestMethod.POST,RequestMethod.GET})
	public void orderPayNotifyRecharge(HttpServletRequest request, HttpServletResponse response) {
		log.info("[/order/pay/recharge]");

		consumeService.orderAliPayNotify(request, response, 2, 0, 1,"2");

	}
	
	/**
	 * 订单支付支付宝服务器异步通知(用于用户购买腕带回调地址)
	 * type:1:消费；2：充值
	 * @param request
	 * @param response
	 *            paymentType 1:微信;2:支付宝 ;3:游乐币
	 *             useType 1:VIP; 2:门票;3: 特产;4:酒店;5: 美食;6:线路 ;7:腕带
	 *             expenseType 1:充值;2:提现;3:消费; 4:退款
	 */
	@RequestMapping(value = "/Alipay/cardNotify", method = { RequestMethod.POST,RequestMethod.GET})
	public void orderPayNotifyCard(HttpServletRequest request, HttpServletResponse response) {
		log.info("[/order/pay/card]");
		consumeService.orderAliPayNotify(request, response, 2, 7, 3,"1");
	}
	

	// /**
	// * 订单支付支付宝服务器异步通知(用于特产消费回调地址)
	// *
	// * @param request
	// * @param response
	// */
	// @RequestMapping(value = "/pay/notify", method = RequestMethod.POST)
	// public void orderPayNotify(HttpServletRequest request,
	// HttpServletResponse response) {
	// log.info("[/order/pay/notify]");
	// System.out.println(request.getParameter("trade_status"));
	// String resultResponse = "failure";
	// PrintWriter printWriter = null;
	// // 获取到返回的所有参数 先判断是否交易成功trade_status 再做签名校验
	// // 1、商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
	// // 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
	// // 3、校验通知中的seller_id（或者seller_email)
	// // 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email），
	// // 4、验证app_id是否为该商户本身。上述1、2、3、4有任何一个验证不通过，则表明本次通知是异常通知，务必忽略。
	// // 在上述验证通过后商户必须根据支付宝不同类型的业务通知，正确的进行不同的业务处理，并且过滤重复的通知结果数据。
	// // 在支付宝的业务通知中，只有交易通知状态为TRADE_SUCCESS或TRADE_FINISHED时，支付宝才会认定为买家付款成功。
	// // if ("TRADE_SUCCESS".equals(request.getParameter("trade_status"))) {
	// Enumeration<?> pNames = request.getParameterNames();
	// Map<String, String> param = new HashMap<String, String>();
	// Map<String, Object> map = new HashMap<String, Object>();
	// try {
	// while (pNames.hasMoreElements()) {
	// String pName = (String) pNames.nextElement();
	// param.put(pName, request.getParameter(pName));
	// }
	// boolean signVerified = AlipaySignature.rsaCheckV1(param,
	// AlipayUtil.ALIPAY_PUBLIC_KEY,
	// AlipayConstants.CHARSET_UTF8); // 校验签名是否正确
	// printWriter = response.getWriter();
	// if (signVerified) {
	// // TODO 验签成功后
	// //
	// 按照支付结果异步通知中的描述，对支付结果中的业务内容进行1\2\3\4二次校验，校验成功后在response中返回success，校验失败返回failure
	// String tradeNo = param.get("out_trade_no");
	// BigDecimal total_amount = new BigDecimal(param.get("total_amount"));
	// String app_id = param.get("app_id");
	// String seller_id = param.get("seller_id");
	// String trade_no = param.get("trade_no");
	// if ("TRADE_SUCCESS".equals(request.getParameter("trade_status"))) {
	// map.put("tradeNo", tradeNo);
	// List<Order> orderList = expenseMapper.getOrder(map);
	// if (orderList != null && !orderList.isEmpty()) {
	// map.clear();
	// map.put("id", orderList.get(0).getUserId());
	// Userinfo userinfo = usermapper.Getuserinfo(map);
	// if (consumeService.checktradeNo(total_amount, orderList, app_id,
	// seller_id)
	// && userinfo != null) {
	// for (Order o : orderList) {
	// consumeService.paySuccess(userinfo, trade_no, o.getPaymentAmount(),
	// o.getOrderNo(), 2,
	// 3, 3);
	// }
	// log.info("订单支付成功：" + param.toString());
	// resultResponse = "success";
	// }
	// }
	// }
	// }
	// } catch (Exception e) {
	// log.error("alipay notify error :", e);
	// printWriter.close();
	// }
	// if (printWriter != null) {
	// printWriter.print(resultResponse);
	// }
	// }

}
