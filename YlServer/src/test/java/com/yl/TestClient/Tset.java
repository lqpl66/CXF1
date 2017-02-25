package com.yl.TestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;

import com.yl.beans.AttractsPathGuide;
import com.yl.beans.AttractsRcmdPath;
import com.yl.beans.RcmdPath;
import com.yl.beans.Scenic;
import com.yl.beans.ScenicImg;
import com.yl.beans.Userinfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Tset {
	public static void main(String[] args) {
		JSONObject result = new JSONObject();
		JSONObject scenicListjson = new JSONObject();
//		List<Scenic> scenicList = new ArrayList<Scenic>();
		JSONObject rpListjson = new JSONObject();
		JSONArray attractionsListjsonArray = new JSONArray();
//		scenicList.add(new Scenic());
//		if (scenicList.isEmpty()) {
//			scenicListjson.put("scenicInfo", "");
//		} else {
//			scenicListjson.put("scenicInfo", scenicList.get(0));
//		}
//		List<ScenicImg> scenicImgList = new ArrayList<ScenicImg>();
//		scenicImgList.add(new ScenicImg());
//		scenicImgList.add(new ScenicImg());
//		if (scenicImgList.isEmpty()) {
//			scenicListjson.put("scenicImgList", "");
//		} else {
//			scenicListjson.put("scenicImgList", scenicImgList);
//		}
		// List<AttractsPojo> attractsRcmdList = new ArrayList<AttractsPojo>();
		// attractsRcmdList.add(new AttractsPojo());
		// attractsRcmdList.add(new AttractsPojo());
		// if (attractsRcmdList.isEmpty()) {
		// scenicListjson.put("attractsRcmdList", "");
		// } else {
		// scenicListjson.put("attractsRcmdList", attractsRcmdList);
		// }
		// if(scenicListjson.get("attractsRcmdList")!=null&&scenicListjson.get("attractsRcmdList")!=""){
		// System.out.println("ddddddd:"+scenicListjson.get("attractsRcmdList"));
		// }else{
		// System.out.println("fsdfsd");
		// }
		List<RcmdPath> rpList = new ArrayList<RcmdPath>();
		RcmdPath r1 = new RcmdPath();
		RcmdPath r2 = new RcmdPath();
		RcmdPath r3 = new RcmdPath();
		r1.setAttractionsId("1,2,3,4");
		r2.setAttractionsId("5,6,7,8");
		r3.setAttractionsId("9,10");
		r1.setAttractionsName("a,b,c,d");
		r2.setAttractionsName("e,f,g,h");
		r3.setAttractionsName("i,j");
		rpList.add(r1);
		rpList.add(r2);
		rpList.add(r3);
		if (!rpList.isEmpty()) {
		
			JSONArray attractionsNameListjsonArray = new JSONArray();
		
			for (RcmdPath rcmdPath : rpList) {
				String attracsIdList = rcmdPath.getAttractionsId();
				String attractionsName = rcmdPath.getAttractionsName();
				if (attracsIdList != null && attractionsName != null) {
					String attractionsNamere =  attractionsName.replaceAll(",", "-");
					attractionsNameListjsonArray.add(attractionsNamere);
					String[] arr = attracsIdList.split(",");
					Integer[] is = (Integer[]) ConvertUtils.convert(arr, Integer[].class);
					List<Integer> list1 = Arrays.asList(is);
					// 获取景区中的推荐路线列表信息
					List<AttractsPathGuide> attractsRcmdPathList = new ArrayList<AttractsPathGuide>();
					AttractsPathGuide a1 = new AttractsPathGuide();
					AttractsPathGuide a2 = new AttractsPathGuide();
					AttractsPathGuide a3 = new AttractsPathGuide();
					AttractsPathGuide a4 = new AttractsPathGuide();
//					AttractsRcmdPath a5 = new AttractsPath();
//					AttractsPath a6 = new AttractsPath();
//					AttractsPath a7 = new AttractsPath();
//					AttractsPath a8 = new AttractsPath();
//					AttractsPath a9 = new AttractsPath();
//					AttractsPath a10 = new AttractsPath();
					a1.setId(1);
					a1.setAttractionsName("a");
					a2.setId(2);
					a2.setAttractionsName("b");
					a3.setId(3);
					a3.setAttractionsName("c");
					a4.setId(4);
					a4.setAttractionsName("d");
//					a5.setId(5);
//					a5.setAttractionsName("e");
//					a6.setId(6);
//					a6.setAttractionsName("f");
//					a7.setId(7);
//					a7.setAttractionsName("g");
//					a8.setId(8);
//					a8.setAttractionsName("h");
//					a9.setId(9);
//					a9.setAttractionsName("i");
//					a10.setId(10);
//					a10.setAttractionsName("j");
					attractsRcmdPathList.add(a1);
					attractsRcmdPathList.add(a2);
					attractsRcmdPathList.add(a3);
					attractsRcmdPathList.add(a4);
//					attractsRcmdPathList.add(a5);
//					attractsRcmdPathList.add(a6);
//					attractsRcmdPathList.add(a7);
//					attractsRcmdPathList.add(a8);
//					attractsRcmdPathList.add(a9);
//					attractsRcmdPathList.add(a10);
					if (!attractsRcmdPathList.isEmpty()) {
						attractionsListjsonArray.add(attractsRcmdPathList);
					}
				}
			}
			if (attractionsListjsonArray != null && attractionsNameListjsonArray != null) {
				rpListjson.put("attractsRcmdPathGuideList", attractionsListjsonArray);
//				rpListjson.put("attractsRcmdPathNameList", attractionsNameListjsonArray);
			} else {
				rpListjson.put("attractsRcmdPathList", "");
//				rpListjson.put("attractsRcmdPathNameList", "");
			}
			scenicListjson.put("rpList", rpListjson);
		} else {
			scenicListjson.put("rpList", "");
		}
		rpListjson.put("planPic", "");
		result.put("data", rpListjson);
		System.out.println(result.toString());
	}
}
