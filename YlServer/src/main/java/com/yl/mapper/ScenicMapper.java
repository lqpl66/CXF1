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
import com.yl.beans.Collect;
import com.yl.beans.Evaluate;
import com.yl.beans.EvaluateImg;
import com.yl.beans.Guide;
import com.yl.beans.RcmdPath;
import com.yl.beans.Scenic;
import com.yl.beans.ScenicCard;
import com.yl.beans.ScenicCollect;
import com.yl.beans.ScenicImg;
import com.yl.beans.ScenicMenu;
import com.yl.beans.ScenicRcmd;
import com.yl.beans.ScenicRecommend;
import com.yl.beans.TravelNotes;
import com.yl.beans.TravelNotesInfo;
import com.yl.beans.TravelNotesRcmd;
import com.yl.beans.Version;

public interface ScenicMapper {
	// 景区详情
	Scenic getscenic(Map map);

	// 根据城市名称获得景区列表或者根据景区id获得单个景区的信息
	List<ScenicRcmd> getscenicList(Map map);

	// 景区图片列表
	List<ScenicImg> getscenicImgList(Map map);

	// 根据景区ID获取景区菜单
	List<ScenicMenu> getscenicMenuList(Map map);

	// 根据景区ID获取景区内的推荐列表
	List<ScenicRecommend> getscenicRcmdList(Map map);

	// 根据景点id获取景点详情
	AttractsInfo getattractsInfoList(Map map);

	// 根据景点id获取景点介绍列表

	List<AttractsIntorduction> getattractIntorductionList(Map map);

	// 根据景点id获取景点图片列表

	List<AttractsImg> getattractImgList(Map map);

	// 根据景区id获取该景区的评价列表

	List<Evaluate> getevaluateList(Map map);

	// 根据参数获取评价列表总数
	String getevaluateListCount(Map map);

	// 根据评价id获取该景区的评价图片列表
	List<EvaluateImg> getevaluateImgList(Map map);

	// 景区推荐景点路线
	List<RcmdPath> getrcmdPath(Map map);

	// 根据景点id数组获取景点推荐路线列表(用于景区详情接口)
	List<AttractsRcmdPath> getattractRcmdPathList(List<Integer> attractsidList);

	// 根据景区id和isRecommend=1获取推荐景点列表
	List<AttractsRcmd> getattractRcmdList(Map map);

	// 根据景区id获取景点列表 （支持分页）
	List<Attracts> getattractsList(Map map);

	// 获取推荐游记列表
	List<TravelNotesRcmd> gettravelNotesRcmdList(Map map);

	// 根据景点id数组获取景点推荐路线列表(用于导览接口)
	List<AttractsPathGuide> getattractPathGuideList(Map map);

	// 获取游记列表（支持分页）
	List<TravelNotes> gettravelNotesList(Map map);

	// 获取游记详情
	List<TravelNotesInfo> gettravelNotesInfoList(Map map);

	// 获取收藏列表
	List<Collect> getcollectList(Map map);

	// 获取景区收藏列表
	List<ScenicCollect> getscenicCollectList(Map map);

	// 根据参数获取收藏总数
	String getcollectListCount(Map map);
	
	//获取景区的游乐卡
	List<ScenicCard> getscenicCard(Map map);
	
	//自动导览路径
	Guide getGuideUrl(Map map);
	
	//版本校验
	List<Version> getVersion(Map map);
}
