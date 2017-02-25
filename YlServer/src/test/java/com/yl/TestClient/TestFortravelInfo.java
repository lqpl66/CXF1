package com.yl.TestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yl.beans.TravelNotes;
import com.yl.beans.TravelNotesInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TestFortravelInfo {

	public static void main(String[] args) {
		JSONObject result = new JSONObject();
		JSONObject data = new JSONObject();
		JSONArray js1 = new JSONArray();
		JSONArray js2 = new JSONArray();
		List<TravelNotesInfo> travelNotesInfoList = new ArrayList<TravelNotesInfo>();
		TravelNotesInfo a1 = new TravelNotesInfo();
		a1.setCrateDate("1");
		TravelNotesInfo a2 = new TravelNotesInfo();
		a2.setCrateDate("1");
		TravelNotesInfo a3 = new TravelNotesInfo();
		a3.setCrateDate("1");
		TravelNotesInfo a4 = new TravelNotesInfo();
		a4.setCrateDate("2");
		TravelNotesInfo a5 = new TravelNotesInfo();
		a5.setCrateDate("2");
		TravelNotesInfo a6 = new TravelNotesInfo();
		a6.setCrateDate("3");
		travelNotesInfoList.add(a1);
		travelNotesInfoList.add(a2);
		travelNotesInfoList.add(a3);
		travelNotesInfoList.add(a4);
		travelNotesInfoList.add(a5);
		travelNotesInfoList.add(a6);
		List<TravelNotes> travelNotesList = new ArrayList<TravelNotes>();
		travelNotesList.add(new TravelNotes());
		if (!travelNotesInfoList.isEmpty() && !travelNotesList.isEmpty()) {
			if (!travelNotesInfoList.isEmpty()) {
				for (int i = 0; i < travelNotesInfoList.size(); i++) {
					if (i == 0) {
						js1.add(0, travelNotesInfoList.get(i).getCrateDate());
						js1.add(travelNotesInfoList.get(i));
					} else {
						if (travelNotesInfoList.get(i).getCrateDate() != travelNotesInfoList.get(i - 1).getCrateDate()) {
							js2.add(js1);
							js1.clear();
							js1.add(0, travelNotesInfoList.get(i).getCrateDate());
							js1.add(travelNotesInfoList.get(i));
						} else {
							js1.add(travelNotesInfoList.get(i));
						}
					}
				}
				data.put("travelNotesInfoList", js2);
			} else {
				data.put("travelNotesInfoList", "");
			}
			if (!travelNotesList.isEmpty()) {
				data.put("travelNotesList", travelNotesList.get(0));
			} else {
				data.put("travelNotesList", "");
			}
			result.put("code", "0001");
			result.put("message", "游记详情查询成功！");
			result.put("data", data);
			System.out.println(result.toString());
		}
	}
}
