package com.yl.TestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.yl.beans.Attracts;
import com.yl.beans.AttractsImg;
import com.yl.beans.AttractsInfo;
import com.yl.beans.AttractsIntorduction;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Test4 {

	public static void main(String[] args) {
		JSONObject result = new JSONObject();
		JSONObject data = new JSONObject();
		AttractsInfo attractsInfo = new AttractsInfo();
		// 查询轮播图片和简介
		List<AttractsImg> attractsImgList = new ArrayList<AttractsImg>();
		attractsImgList.add(new AttractsImg());
		attractsImgList.add(new AttractsImg());
		attractsImgList.add(new AttractsImg());
		if (!attractsImgList.isEmpty()) {
			JSONArray attractsImgListjsonArray = new JSONArray();
			attractsImgListjsonArray = JSONArray.fromObject(attractsImgList);
			attractsInfo.setAttractsImgList(attractsImgListjsonArray.toString());
		}
		List<AttractsIntorduction> attractsIntorductionList = new ArrayList<AttractsIntorduction>();
		attractsIntorductionList.add(new AttractsIntorduction());
		attractsIntorductionList.add(new AttractsIntorduction());
		JSONArray attractsIntorductionListjsonArray = new JSONArray();
		if (!attractsIntorductionList.isEmpty()) {
			attractsIntorductionListjsonArray = JSONArray.fromObject(attractsIntorductionList);
			attractsInfo.setAttractsIntorductionList(attractsIntorductionListjsonArray.toString());
		}
		data = JSONObject.fromObject(attractsInfo);
//		data.put("attractsInfo", attractsInfo);
		result.put("code", "0001");
		result.put("message", "景点详情查询成功！");
		result.put("data", data);
		System.out.println(result.toString());
	}

}
