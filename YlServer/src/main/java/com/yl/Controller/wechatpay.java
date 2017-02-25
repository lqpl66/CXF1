package com.yl.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.yl.service.ConsumeService;
import com.yl.webRestful.CardWebRestful;

@Controller
public class wechatpay {
	private static Logger log = Logger.getLogger(wechatpay.class);
	// @Autowired
	// private UserMapper usermapper;
	// @Autowired
	// private ExpenseMapper expenseMapper;
	@Autowired
	private ConsumeService consumeService;

	/**
	 * 订单支付微信服务器异步通知(用于特产消费回调地址)
	 * 
	 * @param request
	 * @param response
	 *            paymentType 1:微信;2:支付宝 ;3:游乐币 useType 1:VIP; 2:门票;3: 特产;4:
	 *            酒店;5: 美食;6:线路 expenseType 1:充值;2:提现;3:消费; 4:退款
	 * 
	 *            Integer paymentType, Integer useType, Integer expenseType,
	 *            String type
	 */
	@RequestMapping(value = "/Wechatpay/expenseNotify", method = RequestMethod.POST)
	public void orderPayNotify(HttpServletRequest request, HttpServletResponse response) {
		log.info("[/order/Wechatpay/expenseNotify]");

		consumeService.orderWeChatPayNotify(request, response, 1, 3, 3, "1");

	}

	/**
	 * 订单支付微信服务器异步通知(用于用户充值回调地址) type:1:消费；2：充值
	 * 
	 * @param request
	 * @param response
	 *            paymentType 1:微信;2:支付宝 ;3:游乐币 useType 1:VIP; 2:门票;3:
	 *            特产;4:酒店;5: 美食;6:线路 expenseType 1:充值;2:提现;3:消费; 4:退款
	 */
	@RequestMapping(value = "/Wechatpay/rechargeNotify", method = RequestMethod.POST)
	public void orderPayNotifyRecharge(HttpServletRequest request, HttpServletResponse response) {
		log.info("[/order/Wechatpay/rechargeNotify]");
		consumeService.orderWeChatPayNotify(request, response, 1, 0, 1, "2");
	}
	
	/**
	 * 订单支付微信服务器异步通知(用于用户充值回调地址) type:1:消费；2：充值
	 * 
	 * @param request
	 * @param response
	 *            paymentType 1:微信;2:支付宝 ;3:游乐币 useType 1:VIP; 2:门票;3:
	 *            特产;4:酒店;5: 美食;6:线路 expenseType 1:充值;2:提现;3:消费; 4:退款
	 */
	@RequestMapping(value = "/Wechatpay/cardNotify", method = RequestMethod.POST)
	public void orderPayNotifyCard(HttpServletRequest request, HttpServletResponse response) {
		log.info("[/order/Wechatpay/rechargeNotify]");
		consumeService.orderWeChatPayNotify(request, response, 1, 7, 3, "1");
	}
}
