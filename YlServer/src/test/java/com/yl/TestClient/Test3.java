package com.yl.TestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;

import com.yl.beans.AttractsPathGuide;
import com.yl.beans.RcmdPath;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Test3 {

	public static void main(String[] args) {
		JSONObject result = new JSONObject();
		List<RcmdPath> rpList = new ArrayList<RcmdPath>();
		RcmdPath r1 = new RcmdPath();
		r1.setAttractionsId("1,2");
		RcmdPath r2 = new RcmdPath();
		r2.setAttractionsId("3,4");
		rpList.add(r1);
		rpList.add(r2);
		if (!rpList.isEmpty()) {
			JSONObject rpListjson = new JSONObject();
			JSONArray attractionsListjsonArray = new JSONArray();
			for (RcmdPath rcmdPath : rpList) {
				String attracsIdList = rcmdPath.getAttractionsId();
				if (attracsIdList != null) {
					String[] arr = attracsIdList.split(",");
					Integer[] is = (Integer[]) ConvertUtils.convert(arr, Integer[].class);
					List<Integer> list1 = Arrays.asList(is);
					// 获取景区中的推荐路线列表信息
					List<AttractsPathGuide> attractsRcmdPathGuideList = new ArrayList<AttractsPathGuide>();
					AttractsPathGuide a1 = new AttractsPathGuide();
					AttractsPathGuide a2 = new AttractsPathGuide();
					AttractsPathGuide a3 = new AttractsPathGuide();
					AttractsPathGuide a4 = new AttractsPathGuide();
					a1.setId(1);
					a2.setId(2);
					// a1.setId(3);
					// a1.setId(4);
					attractsRcmdPathGuideList.add(a1);
					attractsRcmdPathGuideList.add(a2);
					if (!attractsRcmdPathGuideList.isEmpty()) {
						attractionsListjsonArray.add(attractsRcmdPathGuideList);
					}
					rpListjson.put("attractsRcmdPathGuideList", attractionsListjsonArray);
				} else {
					rpListjson.put("attractsRcmdPathGuideList", "");
				}
			}
			result.put("data", rpListjson);
			result.put("code", "0001");
			result.put("message", "成功获取导览数据！");
		}
		System.out.println(result.toString());
	}

}
