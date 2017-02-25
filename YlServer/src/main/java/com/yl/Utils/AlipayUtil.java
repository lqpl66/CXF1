package com.yl.Utils;

public class AlipayUtil {
	public static final String ALIPAY_APPID = GetProperties.getAppID(); // appid
	String appID = GetProperties.getAppID();
	String privateKey = GetProperties.getPrivateKey();
	public static String APP_PRIVATE_KEY = GetProperties.getPrivateKey(); // app支付私钥

	public static String ALIPAY_PUBLIC_KEY =  GetProperties.getPublicKey(); // 支付宝公钥

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
}
