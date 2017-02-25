package com.yl.Utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;

public class CodeUtils {

	private final static int OFFSET = 538309;
//	private static Logger log = Logger.getLogger(CodeUtils.class);
	private static DateFormat df = new SimpleDateFormat("MMyydd");

	/*
	 * // 返回一个六位随机数验证码
	 */
	public static String getCode(int num) {
		long seed = System.currentTimeMillis() + OFFSET;
		SecureRandom secureRandom = null; // 安全随机类
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(seed);
			// ThreadLocalRandom
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		String codeList = "1234567890"; // 验证码数字取值范围
		String sRand = ""; // 定义一个验证码字符串变量
		for (int i = 0; i < num; i++) {
			// int code = secureRandom.nextInt(codeList.length() - 1); //
			// 随即生成一个0-9之间的整数
			int code = ThreadLocalRandom.current().nextInt(codeList.length() - 1);
			String rand = codeList.substring(code, code + 1);
			sRand += rand;
		}
		return sRand;
	}

	/*
	 * 返回一个三位随机数验证码
	 */
//	public static String getFiveCode(int) {
//		long seed = System.currentTimeMillis() + OFFSET;
//		SecureRandom secureRandom = null; // 安全随机类
//		try {
//			secureRandom = SecureRandom.getInstance("SHA1PRNG");
//			secureRandom.setSeed(seed);
//			// ThreadLocalRandom
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}
//		String codeList = "1234567890"; // 验证码数字取值范围
//		String sRand = ""; // 定义一个验证码字符串变量
//		for (int i = 0; i < 5; i++) {
//			// int code = secureRandom.nextInt(codeList.length() - 1); //
//			// 随即生成一个0-9之间的整数
//			int code = ThreadLocalRandom.current().nextInt(codeList.length() - 1);
//			String rand = codeList.substring(code, code + 1);
//			sRand += rand;
//		}
//		return sRand;
//	}

	/*
	 * 返回一个四位随机数验证码
	 */
//	public static String getFourCode() {
//		long seed = System.currentTimeMillis() + OFFSET;
//		SecureRandom secureRandom = null; // 安全随机类
//		try {
//			secureRandom = SecureRandom.getInstance("SHA1PRNG");
//			secureRandom.setSeed(seed);
//			// ThreadLocalRandom
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}
//		String codeList = "1234567890"; // 验证码数字取值范围
//		String sRand = ""; // 定义一个验证码字符串变量
//		for (int i = 0; i < 4; i++) {
//			// int code = secureRandom.nextInt(codeList.length() - 1); //
//			// 随即生成一个0-9之间的整数
//			int code = ThreadLocalRandom.current().nextInt(codeList.length() - 1);
//			String rand = codeList.substring(code, code + 1);
//			sRand += rand;
//		}
//		return sRand;
//	}

	/*
	 * 返回一个三位随机数验证码
	 */
//	public static String getThreeCode() {
//		long seed = System.currentTimeMillis() + OFFSET;
//		SecureRandom secureRandom = null; // 安全随机类
//		try {
//			secureRandom = SecureRandom.getInstance("SHA1PRNG");
//			secureRandom.setSeed(seed);
//			// ThreadLocalRandom
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}
//		String codeList = "1234567890"; // 验证码数字取值范围
//		String sRand = ""; // 定义一个验证码字符串变量
//		for (int i = 0; i < 3; i++) {
//			// int code = secureRandom.nextInt(codeList.length() - 1); //
//			// 随即生成一个0-9之间的整数
//			int code = ThreadLocalRandom.current().nextInt(codeList.length() - 1);
//			String rand = codeList.substring(code, code + 1);
//			sRand += rand;
//		}
//		return sRand;
//	}

	/*
	 * 时间戳zifu
	 */
	public static String getfileName() {
		String fileName = String.valueOf(Calendar.getInstance().getTimeInMillis());
		return fileName;
	}

	/*
	 * +月+年后两位+日
	 */
	public static String getMMYYDD() {
		String MMYYDD = df.format(Calendar.getInstance().getTime());
		return MMYYDD;
	}

	/*
	 * UserCode（APP用户，管理员，商家）（15位）：0-9 随机6位数字 userCode
	 */
	public static String getuserCode() {
		// String userCode = getSixCode();
		String userCode = String.valueOf(Calendar.getInstance().getTimeInMillis());
		userCode = userCode.substring(userCode.length() - 11, userCode.length()) + getCode(4);
		return userCode;
	}

	/*
	 * 店铺编号（6位）：0-9 随机6位数字 shopNo
	 */
	public static String getshopNo() {
		String shopNo = getCode(6);
		return shopNo;
	}

	/*
	 * 景区编号：0-9 随机6位数字 scenicNo
	 */
	public static String getscenicCode() {
		String scenicCode = getCode(3);
		return scenicCode;
	}

	/*
	 * 景点编号：0-9 随机6位数字 attractsCode
	 */
	public static String getattractsCode() {
		String attractsCode = getCode(6);
		return attractsCode;
	}

	/*
	 * FileCode规则（15位）： 3位随机数+月+年后两位+日+6位userCode
	 */
	public static String getfileCode(String userCode) {
		String fileCode = String.valueOf(Calendar.getInstance().getTimeInMillis());
		fileCode = getCode(3) + getMMYYDD() + userCode;
		return fileCode;
	}

	/*
	 * 游乐卡绑定流水（20位）：3位随机+时间戳后11位+6位userCode
	 */
	public static String getylCardCode(String userCode) {
		String ylCardCode = String.valueOf(Calendar.getInstance().getTimeInMillis());
		ylCardCode = getCode(3) + ylCardCode.substring(ylCardCode.length() - 11, ylCardCode.length()) + userCode;
		return ylCardCode;
	}

	/*
	 * 交易流水号（20位）：3位随机数+时间戳后11位+6位userCode
	 */
	public static String gettransactionFlowCode(String userCode) {
		String transactionFlowCode = String.valueOf(Calendar.getInstance().getTimeInMillis());
		transactionFlowCode = getCode(3)
				+ transactionFlowCode.substring(transactionFlowCode.length() - 11, transactionFlowCode.length())
				+ userCode;
		return transactionFlowCode;
	}

	/*
	 * 合并交易订单号（20位）：7位随机数+时间戳后13位
	 */
	public static String gettradeNo() {
		String tradeNo = String.valueOf(Calendar.getInstance().getTimeInMillis());
		tradeNo = getCode(7) + tradeNo.substring(tradeNo.length() - 13, tradeNo.length());
		return tradeNo;
	}

	/*
	 * 订单编号（15位）：3位随机数+月+年后两位+日+6位userCode
	 */
	public static String getorderCode(String userCode) {
		// String orderCode =
		// String.valueOf(Calendar.getInstance().getTimeInMillis());
		String orderCode = getCode(3) + getMMYYDD() + userCode;
		return orderCode;
	}

	/*
	 * 商品编号（10位）：6位店铺编号+4位商品数（不够补全）
	 */
	public static String getgoodNo(String shopNo, Integer number) {
		String goodNo = shopNo;
//		String goodTypestring = getformatter(goodType, 3);
		String numberstring = getformatter(number, 4);
		goodNo = shopNo  + numberstring;
		return goodNo;
	}

	/*
	 * 游乐卡编号（8位）：32位游乐卡类型+6位随机
	 */
	public static String getylCardNo(Integer scenicId, Integer typeId) {
//		String scenicId_str = getformatter(scenicId, 3);
		String typeId_str = getformatter(typeId, 2);
		String ylCardCode =  typeId_str + getCode(6);
		return ylCardCode;
	}

	/*
	 * 补全数字
	 */
	public static String getformatter(Integer number, Integer length) {
		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMinimumIntegerDigits(length);
		formatter.setGroupingUsed(false);
		String s = formatter.format(number);
		return s;
	}
    //账号隐藏
	public  static String getAccountName(String accountName){
		String Name = null;
		if(accountName.contains("@")){
			Name = accountName.substring(0, 2)+"****"+accountName.substring(accountName.indexOf("@"));
		}else{
			if(accountName.length()<=6){
				Name = accountName.substring(0, 2)+"****"+accountName.substring(accountName.length()-2);
			}else{
				Name = accountName.substring(0, 2)+"****"+accountName.substring(accountName.length()-4);
			}
		}
		return Name;
	} 
	public static void main(String[] args) {
		// filecode
		 String s = "123212";
		 String a = null;
		 for (int i = 0; i < 25; i++) {
		 a = getfileCode(s);
		 System.out.println(a);
		 }
//		BigDecimal dd = new BigDecimal(125.02);
//		// BigDecimal divisor = new BigDecimal(10);
//		// Integer score = dd.divide(divisor).intValue();
//		dd = dd.add(dd);
//		System.out.println(dd.setScale(2, BigDecimal.ROUND_UP));
		
//		String s = getfileCode("123212");
//		System.out.println(s);

	}

}
