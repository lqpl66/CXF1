package com.yl.pay.Alipay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.yl.Utils.GetProperties;
import net.sf.json.JSONObject;

/**
 * 
 * alipay支付
 * 
 */

public class AlipayUtil {

	public static final String ALIPAY_APPID = GetProperties.getAppID(); // appid
	public static String APP_PRIVATE_KEY = GetProperties.getPrivateKey();// app支付私钥
	public static String ALIPAY_PUBLIC_KEY = GetProperties.getPublicKey(); // 支付宝公钥

	public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//
	// static {
	// try {
	// Resource resource = new
	// ClassPathResource("alipay_private_key_pkcs8.pem");
	// APP_PRIVATE_KEY =
	// FileUtil.readInputStream2String(resource.getInputStream());
	// resource = new ClassPathResource("alipay_public_key.pem");
	// ALIPAY_PUBLIC_KEY =
	// FileUtil.readInputStream2String(resource.getInputStream());
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	// 统一收单交易创建接口
	private static AlipayClient alipayClient = null;

	public static AlipayClient getAlipayClient() {
		if (alipayClient == null) {
			synchronized (AlipayUtil.class) {
				if (null == alipayClient) {
					alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", ALIPAY_APPID,
							APP_PRIVATE_KEY, AlipayConstants.FORMAT_JSON, AlipayConstants.CHARSET_UTF8,
							ALIPAY_PUBLIC_KEY);
				}
			}
		}
		return alipayClient;
	}

	/*
	 * type 1：特产消费；2：充值；3：腕带消费
	 */

	public static String orderString(String tradeNo, String total_amount, Integer type, String body) {
		String orderString = null;
		Map<String, String> param = new HashMap<>();
		Map<String, String> pcont = new HashMap<>();
		// 公共请求参数
		param.put("app_id", AlipayUtil.ALIPAY_APPID);// 商户订单号
		param.put("method", "alipay.trade.app.pay");// 交易金额
		param.put("format", AlipayConstants.FORMAT_JSON);
		param.put("charset", AlipayConstants.CHARSET_UTF8);
		param.put("timestamp", df.format(new Date()));
		param.put("version", "1.0");
		if (type == 2) {
			param.put("notify_url", GetProperties.getNotify_url_Alipay_recharge());
			pcont.put("enable_pay_channels", "balance,moneyFund,debitCardExpress");
		} else if (type == 1) {
			param.put("notify_url", GetProperties.getNotify_url_Alipay_expense());
		} else {
			param.put("notify_url", GetProperties.getNotify_url_Alipay_card());
		}
		param.put("sign_type", AlipayConstants.SIGN_TYPE_RSA);
		// 支付业务请求参数
		pcont.put("out_trade_no", tradeNo); // 商户订单号
		pcont.put("total_amount", total_amount);// 交易金额
		pcont.put("subject", "******"); // 订单标题
		pcont.put("body", body);// 对交易或商品的描述
		pcont.put("timeout_express", "30m");
		pcont.put("product_code", "QUICK_MSECURITY_PAY");
		JSONObject biz_content = JSONObject.fromObject(pcont);
		System.out.println(biz_content.toString());
		param.put("biz_content", biz_content.toString()); // 业务请求参数
		try {
			param.put("sign", PayUtil.getSign(param, APP_PRIVATE_KEY)); // 业务请求参数
			orderString = PayUtil.getSignEncodeUrl(param, true);
			System.out.println(orderString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderString;
	}
  public static void main(String args[]){
//	  AlipayClient a =   getAlipayClient();
//	  System.out.println(a.execute(arg0));
  }
	
	
}
