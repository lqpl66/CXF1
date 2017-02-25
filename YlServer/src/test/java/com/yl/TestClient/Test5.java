package com.yl.TestClient;

import java.util.ArrayList;
import java.util.List;

import com.yl.beans.TravelNotes;

import net.sf.json.JSONObject;

public class Test5 {
	
	public static void main(String[] args){
		JSONObject result = new JSONObject();
		List<TravelNotes> travelNotesList =new ArrayList<TravelNotes>();
		travelNotesList.add(new TravelNotes());
		travelNotesList.add(new TravelNotes());
		if(!travelNotesList.isEmpty()){
			result.put("code", "0001");
			result.put("message", "游记查询成功！");
			result.put("data", travelNotesList);
		}else{
			result.put("code", "0003");
			result.put("message", "暂无数据！");
		}
		System.out.println(result);
	}
}
