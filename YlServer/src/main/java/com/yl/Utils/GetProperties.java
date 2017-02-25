package com.yl.Utils;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class GetProperties {

	public static String getAppID() {
		Resource resource = null;
		Properties props = null;
		String appID = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			appID = (String) props.get("appID");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return appID;
	}

	public static String getFileUrlPath() {
		Resource resource = null;
		Properties props = null;
		String imgUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			imgUrl = (String) props.get("fileurl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imgUrl;
	}

	public static String getImgUrlPath() {
		Resource resource = null;
		Properties props = null;
		String imgUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			imgUrl = (String) props.get("ImgUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imgUrl;
	}

	public static String getimgUrlPath() {
		Resource resource = null;
		Properties props = null;
		String imgUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			imgUrl = (String) props.get("imgurl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imgUrl;
	}

	// 用于返回数据时评价景区小图的回显路径拼接
	public static String getevaImgScenicMinUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("evaImgScenicMinUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 用于返回数据时评价景区大图的回显路径拼接
	public static String getevaImgScenicMaxUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("evaImgScenicMaxUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 用于返回数据时评价酒店小图的回显路径拼接
	public static String getevaImgHotelMinUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("evaImgHotelMinUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 用于返回数据时评价酒店大图的回显路径拼接
	public static String getevaImgHotelMaxUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("evaImgHotelMaxUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 用于返回数据时评价特产小图的回显路径拼接
	public static String getevaImgSpecialityMinUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("evaImgSpecialityMinUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 用于返回数据时评价特产大图的回显路径拼接
	public static String getevaImgSpecialityMaxUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("evaImgSpecialityMaxUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 用于返回数据时评价美食小图的回显路径拼接
	public static String getevaImgTastyFoodMinUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("evaImgTastyFoodMinUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 用于返回数据时评价美食大图的回显路径拼接
	public static String getevaImgTastyFoodMaxUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("evaImgTastyFoodMaxUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 用于返回数据时游记图片的回显路径拼接
	public static String gettravelImgUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("travelImgUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 用于返回数据时景区图片回显路径拼接
	public static String getscenicImgUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("scenicImgUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}
	
	// 用于返回数据时景区mp3文件的Nginx指向
	public static String getscenicFileUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("scenicFileUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 读取评价景区小图的回显路径
	public static String getEvaImgScenicMinUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("EvaImgScenicMinUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 读取评价景区大图的回显路径
	public static String getEvaImgScenicMaxUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("EvaImgScenicMaxUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 读取评价酒店小图的回显路径
	public static String getEvaImgHotelMinUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("EvaImgHotelMinUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 读取评价酒店大图的回显路径
	public static String getEvaImgHotelMaxUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("EvaImgHotelMaxUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 读取评价特产小图的回显路径
	public static String getEvaImgSpecialityMinUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("EvaImgSpecialityMinUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 读取评价特产大图的回显路径
	public static String getEvaImgSpecialityMaxUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("EvaImgSpecialityMaxUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 读取评价美食小图的回显路径
	public static String getEvaImgTastyFoodMinUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("EvaImgTastyFoodMinUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 读取评价美食大图的回显路径
	public static String getEvaImgTastyFoodMaxUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("EvaImgTastyFoodMaxUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 读取游记图片的回显路径
	public static String getTravelImgUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("TravelImgUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 读取景区图片回显路径
	public static String getScenicImgUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("ScenicImgUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 读取特产图片的
	public static String getShopImgUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("ShopImgUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 用于特产图片的回显拼接
	public static String getshopImgUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("shopImgUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 读取用户头像
	public static String getUserImgUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("UserImgUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	// 用户头像回显拼接
	public static String getuserImgUrl() {
		Resource resource = null;
		Properties props = null;
		String fileUrl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			fileUrl = (String) props.get("userImgUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	public static String getevaluateTypeH() {
		Resource resource = null;
		Properties props = null;
		String H = null;
		try {
			resource = new ClassPathResource("/config.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			H = (String) props.get("evaluateTypeH");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return H;
	}

	public static String getevaluateTypeW() {
		Resource resource = null;
		Properties props = null;
		String W = null;
		try {
			resource = new ClassPathResource("/config.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			W = (String) props.get("evaluateTypeW");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return W;
	}
	public static String getevaluateTypeW1() {
		Resource resource = null;
		Properties props = null;
		String W1 = null;
		try {
			resource = new ClassPathResource("/config.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			W1 = (String) props.get("evaluateTypeW1");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return W1;
	}
	public static String getevaluateTypeW2() {
		Resource resource = null;
		Properties props = null;
		String W2 = null;
		try {
			resource = new ClassPathResource("/config.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			W2 = (String) props.get("evaluateTypeW2");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return W2;
	}
	public static String getevaluateTypeHW() {
		Resource resource = null;
		Properties props = null;
		String HW = null;
		try {
			resource = new ClassPathResource("/config.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			HW = (String) props.get("evaluateTypeHW");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return HW;
	}

	public static String getPublicKey() {
		Resource resource = null;
		Properties props = null;
		String publicKey = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			publicKey = (String) props.get("publicKey");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return publicKey;
	}

	public static String getPrivateKey() {
		Resource resource = null;
		Properties props = null;
		String privateKey = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			privateKey = (String) props.get("privateKey");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return privateKey;
	}

	public static String getSellerId() {
		Resource resource = null;
		Properties props = null;
		String sellerID = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			sellerID = (String) props.get("sellerID");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sellerID;
	}
	
	public static String getNotify_url_Alipay_expense() {
		Resource resource = null;
		Properties props = null;
		String notify_url_Alipay_expense = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			notify_url_Alipay_expense = (String) props.get("notify_url_Alipay_expense");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return notify_url_Alipay_expense;
	}
	public static String getNotify_url_Alipay_recharge() {
		Resource resource = null;
		Properties props = null;
		String notify_url_Alipay_recharge = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			notify_url_Alipay_recharge = (String) props.get("notify_url_Alipay_recharge");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return notify_url_Alipay_recharge;
	}
	
	public static String getNotify_url_Alipay_card() {
		Resource resource = null;
		Properties props = null;
		String notify_url_Alipay_card = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			notify_url_Alipay_card = (String) props.get("notify_url_Alipay_card");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return notify_url_Alipay_card;
	}
	
	public static String getNotify_url_Wechat_expense() {
		Resource resource = null;
		Properties props = null;
		String notify_url_Wechat_expense = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			notify_url_Wechat_expense = (String) props.get("notify_url_Wechat_expense");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return notify_url_Wechat_expense;
	}
	public static String getNotify_url_Wechat_recharge() {
		Resource resource = null;
		Properties props = null;
		String notify_url_Wechat_recharge = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			notify_url_Wechat_recharge = (String) props.get("notify_url_Wechat_recharge");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return notify_url_Wechat_recharge;
	}
	public static String getNotify_url_Wechat_card() {
		Resource resource = null;
		Properties props = null;
		String notify_url_Wechat_card = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			notify_url_Wechat_card = (String) props.get("notify_url_Wechat_card");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return notify_url_Wechat_card;
	}
	
	// 分享
	public static String getshareUrl() {
		Resource resource = null;
		Properties props = null;
		String shareurl = null;
		try {
			resource = new ClassPathResource("/resources.properties");
			props = PropertiesLoaderUtils.loadProperties(resource);
			shareurl = (String) props.get("shareurl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return shareurl;
	}
	
}
