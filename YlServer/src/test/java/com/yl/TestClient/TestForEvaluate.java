package com.yl.TestClient;

import java.util.ArrayList;
import java.util.List;

import com.yl.beans.Evaluate;
import com.yl.beans.EvaluateImg;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TestForEvaluate {

	public static void main(String[] args) {
		JSONObject result = new JSONObject();
		JSONObject data = new JSONObject();
		List<Evaluate> evaluteList = new ArrayList<Evaluate>();
		Evaluate e1 = new Evaluate();
		Evaluate e2 = new Evaluate();
		evaluteList.add(e1);
		evaluteList.add(e2);
		if (!evaluteList.isEmpty()) {
			// 评价中的图片信息封装
			for (Evaluate evaluate : evaluteList) {
//				map.clear();
//				map.put("evaluateId", evaluate.getId());
				List<EvaluateImg> evaluateImgList = new ArrayList<EvaluateImg>();
				EvaluateImg ei1 = new EvaluateImg();
				EvaluateImg ei2 = new EvaluateImg();
				evaluateImgList.add(ei1);
				evaluateImgList.add(ei2);
				JSONArray evaluateImgjson = new JSONArray();
				if (!evaluateImgList.isEmpty()) {
//					for (EvaluateImg evaluateImg : evaluateImgList) {
//						if (evaluateImg.getImgUrl() != null && evaluateImg.getImgUrl() != "") {
//							evaluateImg.setImgUrl(imgurl + evaluateImg.getImgUrl());
//						}
//					}
					evaluateImgjson = JSONArray.fromObject(evaluateImgList);
					evaluate.setEvaluateImgList(evaluateImgjson.toString());
				}
			}
			data.put("evaluteList", evaluteList);
			result.put("code", "0001");
			result.put("message", "评价列表查询成功！");
			result.put("data", data);
		} 
		System.out.println(result.toString());
	}
}
