package com.yl.mapper;

import java.util.List;
import java.util.Map;

import com.yl.beans.AttractsInfo;
import com.yl.beans.AttractsImg;
import com.yl.beans.Attracts;
import com.yl.beans.AttractsIntorduction;
import com.yl.beans.AttractsPathGuide;
import com.yl.beans.AttractsRcmd;
import com.yl.beans.AttractsRcmdPath;
import com.yl.beans.City;
import com.yl.beans.Collect;
import com.yl.beans.Evaluate;
import com.yl.beans.EvaluateImg;
import com.yl.beans.Province;
import com.yl.beans.RcmdPath;
import com.yl.beans.Scenic;
import com.yl.beans.ScenicImg;
import com.yl.beans.ScenicMenu;
import com.yl.beans.ScenicRcmd;
import com.yl.beans.ScenicRecommend;
import com.yl.beans.TravelNotes;
import com.yl.beans.TravelNotesInfo;
import com.yl.beans.TravelNotesRcmd;

public interface TravelNoteMapper {

	// 保存游记
	public Integer savetravelNotes(TravelNotes travelNotes);

	// 修改游记
	void updatetravelNotes(TravelNotes travelNotes);

	// 浏览量增加
	void updatebrowseCount(Map map);

	// 批量增加游记详情
	void savetravelNotesInfoList(List<TravelNotesInfo> list);

	// 批量修改游记详情
	void updatetravelNotesInfoList(TravelNotesInfo tr);

	// 获取省份
	List<Province> getProvince();

	// 获取城市列表
	List<City> getCity(Map map);

	// 游记详情删除
	void deleteById(Map map);

	// 查询游记
	TravelNotes gettravelNotes(Map map);

	// 查询游记详情
	TravelNotesInfo gettravelNotesInfo(Map map);

	// 获取游记收藏
	List<TravelNotes> gettravelNoteCollcet(Map map);

	// 获取游记详情点赞个数
	Integer getpraiseCount(Map map);

	// 游记详情点赞记录
	void savepraiseLog(Map map);

	// 删除游记详情点赞记录
	void deletepraiseById(Map map);
}
