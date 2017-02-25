package com.yl.TestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;

import com.yl.beans.AttractsInfo;
import com.yl.beans.AttractsRcmd;
import com.yl.beans.Evaluate;
import com.yl.beans.EvaluateImg;
import com.yl.beans.Scenic;
import com.yl.beans.ScenicImg;
import com.yl.beans.ScenicMenu;
import com.yl.beans.ScenicRecommend;
import com.yl.beans.TravelNotesRcmd;
import com.yl.beans.Userinfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Tset2 {
	public static void main(String[] args) {
		JSONObject result = new JSONObject();
		JSONObject data = new JSONObject();
		List<Scenic> scenicList = new ArrayList<Scenic>();
		scenicList.add(new Scenic());
		if (scenicList.isEmpty()) {
			data.put("scenicInfo", "");
		} else {
			data.put("scenicInfo", scenicList.get(0));
		}
		// 菜单列表
		List<ScenicMenu> scenicMenuList = new ArrayList<ScenicMenu>();
		scenicMenuList.add(new ScenicMenu());
		if (scenicMenuList.isEmpty()) {
			data.put("scenicMenuList", "");
		} else {
			data.put("scenicMenuList", scenicMenuList);
		}
		List<Evaluate> evaluteList = new ArrayList<Evaluate>();
		Evaluate d = new Evaluate();
		d.setId(1);
		evaluteList.add(d);
		if (!evaluteList.isEmpty()) {
			// 评价中的图片信息
			for (Evaluate evaluate : evaluteList) {
				List<EvaluateImg> evaluateImgList = new ArrayList<EvaluateImg>();
				evaluateImgList.add(new EvaluateImg());
				evaluateImgList.add(new EvaluateImg());
				JSONArray evaluateImgjson = new JSONArray();
				if (evaluateImgList.isEmpty()) {
					evaluate.setEvaluateImgList("");
				} else {
					evaluateImgjson = JSONArray.fromObject(evaluateImgList);
					evaluate.setEvaluateImgList(evaluateImgjson.toString());
				}
			}
			data.put("evaluteList", evaluteList);
		} else {
			data.put("evaluteList", "");
		}
		List<ScenicRecommend> scenicRcmdList = new ArrayList<ScenicRecommend>();
		scenicRcmdList.add(new ScenicRecommend());
		if (scenicRcmdList.isEmpty()) {
			data.put("scenicRcmdList", "");
			data.put("attractsList", "");
			data.put("tastyfoodList", "");
			data.put("hotelList", "");
			data.put("roadlineList", "");
			data.put("noteList", "");
			data.put("specialityList", "");
		} else {
			data.put("scenicRcmdList", scenicRcmdList);
			List<AttractsRcmd> attractsList = new ArrayList<AttractsRcmd>();
			attractsList.add(new AttractsRcmd());
			data.put("attractsList", attractsList);
			List<TravelNotesRcmd> noteList = new ArrayList<TravelNotesRcmd>();
			noteList.add(new TravelNotesRcmd());
			data.put("noteList",noteList);
			data.put("tastyfoodList", "");
			data.put("hotelList", "");
			data.put("roadlineList", "");
			data.put("specialityList", "");
		}
		result.put("data", data);
		System.out.println(result.toString());
	}
}
