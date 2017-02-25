package com.yl.webRestful;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.sf.json.JSONArray;
//import com.alibaba.fastjson.JSONObject;
import net.sf.json.JSONObject;
import com.yl.beans.AttractsInfo;
import com.yl.beans.AttractsIntorduction;
import com.yl.Utils.CommonDateParseUtil;
import com.yl.Utils.GetProperties;
import com.yl.beans.Attracts;
import com.yl.beans.AttractsImg;
import com.yl.beans.AttractsPathGuide;
import com.yl.beans.AttractsRcmd;
import com.yl.beans.AttractsRcmdPath;
import com.yl.beans.Collect;
import com.yl.beans.Evaluate;
import com.yl.beans.EvaluateImg;
import com.yl.beans.RcmdPath;
import com.yl.beans.Scenic;
import com.yl.beans.ScenicImg;
import com.yl.beans.ScenicMenu;
import com.yl.beans.ScenicRcmd;
import com.yl.beans.ScenicRecommend;
import com.yl.beans.ShopNear;
import com.yl.beans.ShopNearForGoods;
import com.yl.beans.Speciality;
import com.yl.beans.TravelNotes;
import com.yl.beans.TravelNotesInfo;
import com.yl.beans.TravelNotesRcmd;
import com.yl.beans.Userinfo;
import com.yl.mapper.ScenicMapper;
import com.yl.mapper.ShopMapper;
import com.yl.mapper.TravelNoteMapper;
import com.yl.mapper.UserMapper;
import com.yl.service.CardService;

/*
 * 景区接口
 */
@Component("scwebRest")
public class ScenicWebRestful {
	@Autowired
	private ScenicMapper scenicMapper;

	@Autowired
	private UserMapper usermapper;

	@Autowired
	private TravelNoteMapper travelmapper;

	@Autowired
	private ShopMapper shopmapper;
	@Autowired
	private CardService cardService;

	private String imgurl = GetProperties.getImgUrlPath();
	// 语音路径
	private String fileurl = GetProperties.getFileUrlPath();
	// 评论景区小图url
	private String evaImgScenicMinUrl = GetProperties.getevaImgScenicMinUrl();
	// 评论酒店小图url
	private String evaImgHotelMinUrl = GetProperties.getevaImgHotelMinUrl();
	// 评论特产小图url
	private String evaImgSpecialityMinUrl = GetProperties.getevaImgSpecialityMinUrl();
	// 评论美食小图url
	private String evaImgTastyFoodMinUrl = GetProperties.getevaImgTastyFoodMinUrl();
	// 商品图片路径
	private String shopImgUrl = GetProperties.getshopImgUrl();
	// 游记路径
	private String travelImgUrl = GetProperties.gettravelImgUrl();
	// 游记路径
	private String TravelImgUrl = GetProperties.getTravelImgUrl();
	// 景区路径回显
	private String scenicImgUrl = GetProperties.getscenicImgUrl();
	// 景区路径存放
	private String ScenicImgUrl = GetProperties.getScenicImgUrl();
	// 用户头像路径
	private String userImgUrl = GetProperties.getuserImgUrl();
	// 景点路径连接字段
	private String attract = "/Attract/";
	private String scenicFileUrl = GetProperties.getscenicFileUrl();
	// 分享
	private String shareurl = GetProperties.getshareUrl();

	private static Logger log = Logger.getLogger(ScenicWebRestful.class);

	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/*
	 * 获取当前城市的景区列表
	 */
	@POST
	@Path("getscenicList")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public JSONObject getscenicList(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String cityName = jsonobject.optString("cityName");
		HashMap<String, Object> map = new HashMap<String, Object>();
		System.out.println("dddd---" + cityName);
		if (cityName != null && !cityName.isEmpty()) {
			map.put("cityName", cityName);
		} else {
			map.put("cityName", "上海");
		}
		try {
			map.put("isRecommend", 1);
			List<ScenicRcmd> scenicList = scenicMapper.getscenicList(map);
			if (scenicList.size() > 0) {
				for (ScenicRcmd sc : scenicList) {
					if (sc.getRecommendImg() != null && sc.getRecommendImg() != "") {
						sc.setRecommendImg(scenicImgUrl + sc.getFileCode() + "/" + sc.getRecommendImg());
					}
				}
				result.put("code", "0001");
				result.put("message", "景区列表查询成功！");
				result.put("data", scenicList);
			} else {
				result.put("code", "0003");
				result.put("message", "暂无数据！");
			}
		} catch (Exception e) {
			log.error("获取景区列表失败：" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result;
	}

	/*
	 * 目的地主页列表
	 */
	@POST
	@Path("gethomeScenic")
	@Produces({ MediaType.APPLICATION_JSON })
	public String gethomeScenic(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		Integer scenicId = jsonobject.optInt("scenicId");
		String uuID = jsonobject.optString("uuID");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (scenicId != null && scenicId > 0) {
				map.put("scenicId", scenicId);
				List<ScenicRcmd> scenicList = scenicMapper.getscenicList(map);
				if (!scenicList.isEmpty()) {
					for (ScenicRcmd sc : scenicList) {
						if (sc.getCoverImg() != null && sc.getCoverImg() != "") {
							sc.setCoverImg(scenicImgUrl + sc.getFileCode() + "/" + sc.getCoverImg());
						}
					}
					JSONObject data = new JSONObject();
					data.put("scenicId", scenicList.get(0).getId());
					data.put("coverImg", scenicList.get(0).getCoverImg());
					data.put("welcoming", scenicImgUrl + "/tmp.mp3");
					// 菜单列表
					List<ScenicMenu> scenicMenuList = scenicMapper.getscenicMenuList(map);
					if (scenicMenuList.isEmpty()) {
						data.put("scenicMenuList", "");
					} else {
						data.put("scenicMenuList", scenicMenuList);
					}
					// 评价列表
					map.clear();
					map.put("fk", scenicId);
					map.put("model", "scenic");
					map.put("start", 0);
					map.put("num", 4);
					List<Evaluate> evaluteList = scenicMapper.getevaluateList(map);
					if (!evaluteList.isEmpty()) {
						// 评价中的图片信息封装
						for (Evaluate evaluate : evaluteList) {
							if (evaluate.getHeadimg() != null && !evaluate.getHeadimg().isEmpty()) {
								evaluate.setHeadimg(userImgUrl + evaluate.getHeadimg());
							}
							map.clear();
							map.put("evaluateId", evaluate.getId());
							List<EvaluateImg> evaluateImgList = scenicMapper.getevaluateImgList(map);
							JSONArray evaluateImgjson = new JSONArray();
							if (!evaluateImgList.isEmpty()) {
								for (EvaluateImg evaluateImg : evaluateImgList) {
									if (evaluateImg.getImgUrl() != null && evaluateImg.getImgUrl() != "") {
										evaluateImg.setImgUrl(evaImgScenicMinUrl + evaluateImg.getImgUrl());
									}
								}
								evaluateImgjson = JSONArray.fromObject(evaluateImgList);
								evaluate.setEvaluateImgList(evaluateImgjson.toString());
							} else {
								evaluate.setEvaluateImgList("[]");
							}
						}
						data.put("evaluteList", evaluteList);
					} else {
						data.put("evaluteList", "");
					}
					// 推荐列表
					map.clear();
					map.put("scenicId", scenicId);
					List<ScenicRecommend> scenicRcmdList = scenicMapper.getscenicRcmdList(map);
					if (scenicRcmdList.isEmpty()) {
						data.put("scenicRcmdList", "");
					} else {
						JSONArray scenicRcmdListJson = new JSONArray();
						// scenicRcmdListJson =
						// JSONArray.fromObject(scenicRcmdList);
						// 根据推荐列表查询
						for (ScenicRecommend scRcmd : scenicRcmdList) {
							JSONObject scRcmdJson = new JSONObject();
							scRcmdJson = JSONObject.fromObject(scRcmd);
							// 景点
							if (scRcmd.getRecommendModel().equals("r_attracts")) {
								map.clear();
								map.put("scenicId", scenicId);
								List<AttractsRcmd> attractsList = scenicMapper.getattractRcmdList(map);
								if (attractsList.isEmpty()) {
									scRcmdJson.put("rcmdList", "[]");
								} else {
									for (AttractsRcmd attractsRcmd : attractsList) {
										if (attractsRcmd.getAttractsImgUrl() != null
												&& attractsRcmd.getAttractsImgUrl() != "") {
											attractsRcmd
													.setAttractsImgUrl(scenicImgUrl + scenicList.get(0).getFileCode()
															+ attract + attractsRcmd.getFileCode() + "/"
															+ attractsRcmd.getAttractsImgUrl());
										}
									}
									scRcmdJson.put("rcmdList", attractsList);
								}
							} // 美食
							else if (scRcmd.getRecommendModel().equals("r_tastyfood")) {
								scRcmdJson.put("rcmdList", "[]");
							} // 酒店
							else if (scRcmd.getRecommendModel().equals("r_hotel")) {
								scRcmdJson.put("rcmdList", "[]");
							} // 推荐线路
							else if (scRcmd.getRecommendModel().equals("r_roadline")) {
								scRcmdJson.put("rcmdList", "[]");
							} // 热门游记
							else if (scRcmd.getRecommendModel().equals("r_note")) {
								map.clear();
								map.put("isUnderway", 1);
								map.put("openLevel", 0);
								map.put("isRecommend", 1);
								map.put("state", 1);
								List<TravelNotesRcmd> noteList = scenicMapper.gettravelNotesRcmdList(map);
								if (noteList.isEmpty()) {
									scRcmdJson.put("rcmdList", "[]");
								} else {
									for (TravelNotesRcmd travelNotesRcmd : noteList) {
										if (travelNotesRcmd.getCoverImg() != null
												&& travelNotesRcmd.getCoverImg() != "") {
											travelNotesRcmd.setCoverImg(travelImgUrl + travelNotesRcmd.getFileCode()
													+ "/" + travelNotesRcmd.getCoverImg());
										}
									}
									scRcmdJson.put("rcmdList", noteList);
								}
							} // 推荐土特产
							else if (scRcmd.getRecommendModel().equals("r_speciality")) {
								// 首先通过景区的经纬定位周边的店铺，然后查询符合条件的推荐特产
								map.clear();
								map.put("scenicId", scenicId);
								Scenic scenic = scenicMapper.getscenic(map);
								map.put("placeX", scenic.getPlaceX());
								map.put("placeY", scenic.getPlaceY());
								List<ShopNear> shopNearList = shopmapper.getshopNearList(map);
								if (!shopNearList.isEmpty()) {
									// JSONArray arraylist =
									// JSONArray.fromObject(shopNearList);
									// List<ShopNearForGoods> sgList =
									// JSONArray.toList(arraylist,
									// ShopNearForGoods.class);
									List<Integer> idlist = new ArrayList<Integer>();
									for (ShopNear sg : shopNearList) {
										idlist.add(sg.getId());
									}
									map.clear();
									map.put("list", idlist);
									map.put("isRecommend", 1);
									List<Speciality> spList = shopmapper.getSpecialityList(map);
									if (!spList.isEmpty()) {
										for (Speciality sp : spList) {
											sp.setImgUrl(shopImgUrl + sp.getShopFileCode() + "/Speciality/"
													+ sp.getFileCode() + "/" + sp.getImgUrl());
											map.clear();
											map.put("fk", sp.getId());
											map.put("model", "speciality");
											String evaluateTotalNum = scenicMapper.getevaluateListCount(map);
											map.put("type", "l");
											String evaluatelargeTotalNum = scenicMapper.getevaluateListCount(map);
											sp.setTotalEvaluateNum(Integer.valueOf(evaluateTotalNum));
											if (!evaluateTotalNum.equals("0")) {
												sp.setGoodEvaluateCate(Integer.valueOf(evaluateTotalNum)
														/ Integer.valueOf(evaluatelargeTotalNum));
											} else {
												sp.setGoodEvaluateCate(0);
											}
										}
										scRcmdJson.put("rcmdList", spList);
									} else {
										scRcmdJson.put("rcmdList", "[]");
									}
								} else {
									scRcmdJson.put("rcmdList", "[]");
								}
								// scRcmdJson.put("rcmdList", "");
							}
							scenicRcmdListJson.add(scRcmdJson);
						}
						data.put("scenicRcmdList", scenicRcmdListJson);
					}
					// 是否收藏
					if (uuID != null && !uuID.isEmpty()) {
						map.clear();
						map.put("uuID", uuID);
						Userinfo userinfo = usermapper.Getuserinfo(map);
						if (userinfo != null) {
							map.clear();
							map.put("userId", userinfo.getId());
							map.put("fk", scenicList.get(0).getId());
							map.put("model", "scenic");
							List<Collect> collect = scenicMapper.getcollectList(map);
							if (!collect.isEmpty()) {
								data.put("isCollect", 1);
							} else {
								data.put("isCollect", 0);
							}
						} else {
							data.put("isCollect", 0);
						}
					} else {
						data.put("isCollect", 0);
					}
					result.put("code", "0001");
					result.put("message", "景区主页查询成功！");
					result.put("data", data);
				} else {
					result.put("code", "0003");
					result.put("message", "暂无数据！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取景区主页景区数据失败：" , e);

			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

	/*
	 * 景区详情
	 * 
	 */
	@POST
	@Path("getscenicInfo")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getscenicInfo(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		Integer scenicId = jsonobject.optInt("scenicId");
		JSONObject scenicListjson = new JSONObject();
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (scenicId != null && scenicId > 0) {
				map.put("scenicId", scenicId);
				Scenic scenic = scenicMapper.getscenic(map);
				if (scenic != null) {
					if (scenic.getCoverImg() != null && scenic.getCoverImg() != "") {
						scenic.setCoverImg(scenicImgUrl + scenic.getFileCode() + "/" + scenic.getCoverImg());
					}
					if (scenic.getPlanPic() != null && scenic.getPlanPic() != "") {
						scenic.setPlanPic(scenicImgUrl + scenic.getFileCode() + "/" + scenic.getPlanPic());
					}
					if (scenic.getRecommendImg() != null && scenic.getRecommendImg() != "") {
						scenic.setRecommendImg(scenicImgUrl + scenic.getFileCode() + "/" + scenic.getRecommendImg());
					}
					scenicListjson.put("scenicInfo", scenic);
					// 景区轮播图片
					List<ScenicImg> scenicImgList = scenicMapper.getscenicImgList(map);
					if (scenicImgList.isEmpty()) {
						scenicListjson.put("scenicImgList", "");
					} else {
						for (ScenicImg scenicImg : scenicImgList) {
							if (scenicImg.getImgUrl() != null && scenicImg.getImgUrl() != "") {
								scenicImg.setImgUrl(scenicImgUrl + scenic.getFileCode() + "/" + scenicImg.getImgUrl());
							}
						}
						scenicListjson.put("scenicImgList", scenicImgList);
					}
					// 景区的景点推荐路线
					List<RcmdPath> rpList = scenicMapper.getrcmdPath(map);
					if (!rpList.isEmpty()) {
						JSONObject rpListjson = new JSONObject();
						JSONArray attractionsNameListjsonArray = new JSONArray();
						JSONArray attractionsListjsonArray = new JSONArray();
						for (RcmdPath rcmdPath : rpList) {
							rcmdPath.setAttractionsUrl(
									scenicImgUrl + scenic.getFileCode() + "/" + rcmdPath.getAttractionsUrl());
							String attracsIdList = rcmdPath.getAttractionsId();
							String attractionsName = rcmdPath.getAttractionsName();
							if (attracsIdList != null && attractionsName != null) {
								String attractionsNamereplace = attractionsName.replaceAll(",", "-");
								attractionsNameListjsonArray.add(attractionsNamereplace);
								String[] arr = attracsIdList.split(",");
								Integer[] is = (Integer[]) ConvertUtils.convert(arr, Integer[].class);
								List<Integer> attractsidList = Arrays.asList(is);
								// 获取景区中的推荐路线列表信息
								List<AttractsRcmdPath> attractsRcmdPathList = scenicMapper
										.getattractRcmdPathList(attractsidList);
								if (!attractsRcmdPathList.isEmpty()) {
									for (int i = 0; i < attractsRcmdPathList.size(); i++) {
										attractsRcmdPathList.get(i).setSortId(i + 1);
									}
									attractionsListjsonArray.add(attractsRcmdPathList);
								}
							} else {
								scenicListjson.put("attractsRcmdPathUrlList", "");
								// scenicListjson.put("attractsRcmdPathList",
								// "");
								// scenicListjson.put("attractsRcmdPathNameList",
								// "");
							}
						}
						scenicListjson.put("attractsRcmdPathUrlList", rpList);
						// scenicListjson.put("attractsRcmdPathList",
						// attractionsListjsonArray);
						// scenicListjson.put("attractsRcmdPathNameList",
						// attractionsNameListjsonArray);
					} else {
						scenicListjson.put("attractsRcmdPathUrlList", "");
						// scenicListjson.put("attractsRcmdPathList", "");
						// scenicListjson.put("attractsRcmdPathNameList", "");
					}
					result.put("code", "0001");
					result.put("message", "景区详情查询成功！");
					result.put("data", scenicListjson);
				} else {
					result.put("code", "0003");
					result.put("message", "暂无数据！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取景区详情失败：" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

	/*
	 * 获取景点列表
	 */
	@POST
	@Path("getattractsList")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getattractsList(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		Integer scenicId = jsonobject.optInt("scenicId");
		Integer pageNum = jsonobject.optInt("pageNum");
		Integer num = jsonobject.optInt("num");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (scenicId != null && scenicId > 0 && pageNum != null && pageNum > 0 && num != null && num > 0) {
				map.put("scenicId", scenicId);
				map.put("start", num * (pageNum - 1));
				map.put("num", num);
				List<Attracts> attractsList = scenicMapper.getattractsList(map);
				if (attractsList.size() > 0) {
					for (Attracts attracts : attractsList) {
						if (attracts.getAttractsImgUrl() != null && attracts.getAttractsImgUrl() != "") {
							attracts.setAttractsImgUrl(scenicImgUrl + attracts.getScenicFileCode() + attract
									+ attracts.getFileCode() + "/" + attracts.getAttractsImgUrl());
						}
						if (attracts.getFileUrl() != null && attracts.getFileUrl() != "") {
							attracts.setFileUrl(scenicFileUrl + attracts.getScenicFileCode() + attract
									+ attracts.getFileCode() + "/" + attracts.getFileUrl());
						}
					}
					result.put("code", "0001");
					result.put("message", "景点列表查询成功！");
					result.put("data", attractsList);
				} else {
					result.put("code", "0003");
					if (pageNum == 1) {
						result.put("message", "暂无景点信息！");
					} else {
						result.put("message", "已无更多景点信息！");
					}
					// result.put("message", "暂无数据！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取景区列表失败：" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

	/*
	 * 获取景点详情
	 */
	@POST
	@Path("getattractsInfo")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getattractsInfo(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		JSONObject data = new JSONObject();
		Integer attractsId = jsonobject.optInt("attractsId");
		String uuID =  jsonobject.optString("uuID");
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (attractsId != null && attractsId > 0) {
				map.put("id", attractsId);
				AttractsInfo attractsInfo = scenicMapper.getattractsInfoList(map);
				if (attractsInfo != null) {
					// 查询轮播图片和简介
					map.clear();
					map.put("attractsId", attractsInfo.getId());
					List<AttractsImg> attractsImgList = scenicMapper.getattractImgList(map);
					if (!attractsImgList.isEmpty()) {
						for (AttractsImg attractsImg : attractsImgList) {
							if (attractsImg.getImgUrl() != null && attractsImg.getImgUrl() != "") {
								attractsImg.setImgUrl(scenicImgUrl + attractsInfo.getScenicFileCode() + attract
										+ attractsInfo.getFileCode() + "/" + attractsImg.getImgUrl());
							}
						}
						JSONArray attractsImgListjsonArray = new JSONArray();
						attractsImgListjsonArray = JSONArray.fromObject(attractsImgList);
						attractsInfo.setAttractsImgList(attractsImgListjsonArray.toString());
					}
					List<AttractsIntorduction> attractsIntorductionList = scenicMapper.getattractIntorductionList(map);
					if (!attractsIntorductionList.isEmpty()) {
						JSONArray attractsIntorductionListjsonArray = new JSONArray();
						attractsIntorductionListjsonArray = JSONArray.fromObject(attractsIntorductionList);
						attractsInfo.setAttractsIntorductionList(attractsIntorductionListjsonArray.toString());
					}
					result.put("code", "0001");
					result.put("message", "景点详情查询成功！");
					data = JSONObject.fromObject(attractsInfo);
					String  type="1"; 
					Userinfo userinfo = null;
					//查询当前景点的所有的免费VIP的时间段和排队
					if(uuID!=null&&!uuID.isEmpty()){//登录状态
						map.clear();
						map.put("uuID", uuID);
						 userinfo = usermapper.Getuserinfo(map);
						JSONObject result1 = cardService.checkUser(userinfo);
						if (result1.optBoolean("flag")) {
							type="2"; 
						}else {
							result.clear();
							result = result1;
							return result.toString();
						}
					}
					JSONObject re = cardService.getLineList(type, userinfo, attractsId);
					if(re.optBoolean("flag")){
						data.put("lineLog",re.opt("lineLog"));
						data.put("aline",re.opt("aline"));
						data.put("VIPLineLogList",re.opt("VIPLineLogList"));
						data.put("VipLineList",re.opt("VipLineList"));
					}else{
						result.clear();
						result = re;
						return result.toString();
					}
					result.put("data", data);
				} else {
					result.put("code", "0003");
					result.put("message", "暂无数据！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取景点详情失败：" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

	/*
	 * 获取景点推荐列表详情
	 */
	@POST
	@Path("getattractsRcmdInfoList")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getattractsRcmdInfoList(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		JSONObject data = new JSONObject();
		Integer scenicId = jsonobject.optInt("scenicId");
		String uuID =  jsonobject.optString("uuID");
		HashMap<String, Object> map = new HashMap<String, Object>();
		JSONArray attractsRcmdInfoList = new JSONArray();
		try {
			if (scenicId != null && scenicId > 0) {
				map.put("scenicId", scenicId);
				List<AttractsRcmd> attractsList = scenicMapper.getattractRcmdList(map);
				if (!attractsList.isEmpty()) {
					for (AttractsRcmd attractsRcmd : attractsList) {
						map.clear();
						map.put("id", attractsRcmd.getId());
						AttractsInfo attractsInfo = scenicMapper.getattractsInfoList(map);
						JSONObject attractsInfojson = new JSONObject();
						if (attractsInfo != null) {
							// 查询轮播图片和简介
							map.clear();
							map.put("attractsId", attractsInfo.getId());
							List<AttractsImg> attractsImgList = scenicMapper.getattractImgList(map);
							if (!attractsImgList.isEmpty()) {
								for (AttractsImg attractsImg : attractsImgList) {
									if (attractsImg.getImgUrl() != null && attractsImg.getImgUrl() != "") {
										attractsImg.setImgUrl(scenicImgUrl + attractsInfo.getScenicFileCode() + attract
												+ attractsInfo.getFileCode() + "/" + attractsImg.getImgUrl());
									}
								}
								JSONArray attractsImgListjsonArray = new JSONArray();
								attractsImgListjsonArray = JSONArray.fromObject(attractsImgList);
								attractsInfo.setAttractsImgList(attractsImgListjsonArray.toString());
							}
							List<AttractsIntorduction> attractsIntorductionList = scenicMapper
									.getattractIntorductionList(map);
							if (!attractsIntorductionList.isEmpty()) {
								JSONArray attractsIntorductionListjsonArray = new JSONArray();
								attractsIntorductionListjsonArray = JSONArray.fromObject(attractsIntorductionList);
								attractsInfo.setAttractsIntorductionList(attractsIntorductionListjsonArray.toString());
							}
						}
						String  type="1"; 
						Userinfo userinfo = null;
						//查询当前景点的所有的免费VIP的时间段和排队
						if(uuID!=null&&!uuID.isEmpty()){//登录状态
							map.clear();
							map.put("uuID", uuID);
							 userinfo = usermapper.Getuserinfo(map);
							JSONObject result1 = cardService.checkUser(userinfo);
							if (result1.optBoolean("flag")) {
								type="2"; 
							}else {
								result.clear();
								result = result1;
								return result.toString();
							}
						}
						JSONObject re = cardService.getLineList(type, userinfo, attractsInfo.getId());
						if(re.optBoolean("flag")){
							attractsInfojson = JSONObject.fromObject(attractsInfo);
//							attractsInfo.setAline(re.opt("aline").toString());
//							attractsInfo.setLineLog(re.opt("lineLog").toString());
//							attractsInfo.setVipLineList(re.opt("VipLineList").toString());
//							attractsInfo.setVipLineLogList(re.opt("VIPLineLogList").toString());
							attractsInfojson.put("lineLog",re.opt("lineLog"));
							attractsInfojson.put("aline",re.opt("aline"));
							attractsInfojson.put("VIPLineLogList",re.opt("VIPLineLogList"));
							attractsInfojson.put("VipLineList",re.opt("VipLineList"));
						}else{
							result.clear();
							result = re;
							return result.toString();
						}
						attractsRcmdInfoList.add(attractsInfojson);
					}
					result.put("code", "0001");
					result.put("message", "景点详情查询成功！");
					// data = JSONObject.fromObject(attractsRcmdInfoList);
					result.put("data", attractsRcmdInfoList);
				} else {
					result.put("code", "0003");
					result.put("message", "暂无数据！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取景点详情失败：" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

	/*
	 * 导览接口
	 */
	@POST
	@Path("getattractsGuideList")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getattractsGuideList(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		Integer scenicId = jsonobject.optInt("scenicId");
		HashMap<String, Object> map = new HashMap<String, Object>();
		JSONObject rpListjson = new JSONObject();
		String planPic = "";
		int picWidth = 0;
		int picHeight = 0;
		try {
			if (scenicId != null && scenicId > 0) {
				map.put("scenicId", scenicId);
				List<ScenicRcmd> scenicList = scenicMapper.getscenicList(map);
				List<AttractsPathGuide> attractsPathGuideList = scenicMapper.getattractPathGuideList(map);
				if (!scenicList.isEmpty() && !attractsPathGuideList.isEmpty()) {
					if (scenicList.get(0).getPlanPic() != null && scenicList.get(0).getPlanPic() != "") {
						planPic = scenicImgUrl + scenicList.get(0).getFileCode() + "/" + scenicList.get(0).getPlanPic();
						// File f = new File(ScenicImgUrl +
						// scenicList.get(0).getFileCode() +"/"
						// + scenicList.get(0).getPlanPic());
						// FileInputStream fis = new FileInputStream(f);
						// BufferedImage bufferedImg = ImageIO.read(fis);
						// picWidth = bufferedImg.getWidth();
						// picHeight = bufferedImg.getHeight();
					}
					for (AttractsPathGuide ap : attractsPathGuideList) {
						if (ap.getFileUrl() != null && ap.getFileUrl() != "") {
							ap.setFileUrl(scenicFileUrl + scenicList.get(0).getFileCode() + attract + ap.getFileCode()
									+ "/" + ap.getFileUrl());
						}
					}
					rpListjson.put("attractsPathGuideList", attractsPathGuideList);
					rpListjson.put("planPic", planPic);
					rpListjson.put("picWidth", 1500);
					rpListjson.put("picHeight", 2668);
					rpListjson.put("hsWidth", 30);
					rpListjson.put("hsHeight", 30);
					result.put("data", rpListjson);
					result.put("code", "0001");
					result.put("message", "成功获取导览数据！");
				} else {
					result.put("code", "0003");
					result.put("message", "暂无数据！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取导览失败：" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

	/*
	 * 游记列表接口 type 1:个人中心的游记列表接口 2：公共列表
	 */
	@POST
	@Path("gettravelNotesList")
	@Produces(MediaType.APPLICATION_JSON)
	public String gettravelNotesList(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		Integer pageNum = jsonobject.optInt("pageNum");
		Integer num = jsonobject.optInt("num");
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		try {
			if (pageNum != null && pageNum > 0 && num != null && num > 0 && type != null && !type.isEmpty()) {
				Map<String, Object> map = new HashMap<String, Object>();
				if (type.equals("1")) {
					if (uuID != null && !uuID.isEmpty()) {
						map.put("uuID", uuID);
						Userinfo userinfo = usermapper.Getuserinfo(map);
						result = cardService.checkUser(userinfo);
						if (result.optBoolean("flag")) {
							// if (userinfo != null) {
							Date nowdate = CommonDateParseUtil.date2date(new Date());
							Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
							if (nowdate.getTime() >= uuIDExpiry.getTime()) {
								result.put("code", "0004");
								result.put("message", "用户登录已过期，请重新登录！");
								return result.toString();
							} else {
								map.clear();
								map.put("userId", userinfo.getId());
							}
						} else {
							// result.put("code", "0004");
							// result.put("message", "暂无用户信息！");
							return result.toString();
						}
					} else {
						result.put("code", "0002");
						result.put("message", "参数不全！");
						return result.toString();
					}
				} else {
					map.put("isUnderway", 1);
					map.put("openLevel", 0);
				}
				map.put("start", num * (pageNum - 1));
				map.put("num", num);
				map.put("state", 1);
				List<TravelNotes> travelNotesList = scenicMapper.gettravelNotesList(map);
				if (!travelNotesList.isEmpty()) {
					for (TravelNotes travelNotes : travelNotesList) {
						if (travelNotes.getCoverImg() != null && travelNotes.getCoverImg() != "") {
							travelNotes.setCoverImg(
									travelImgUrl + travelNotes.getFileCode() + "/" + travelNotes.getCoverImg());
						}
						if (travelNotes.getHeadImg() != null && !travelNotes.getHeadImg().isEmpty()) {
							travelNotes.setHeadImg(userImgUrl + travelNotes.getHeadImg());
						}
					}
					result.put("code", "0001");
					result.put("message", "游记查询成功！");
					result.put("data", travelNotesList);
				} else {
					result.put("code", "0001");
					result.put("data", "");
					if (pageNum == 1) {
						result.put("message", "暂无景点数据！");
					} else {
						result.put("message", "已无更多景点数据！");
					}
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取游记列表失败：" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

	/*
	 * 游记详情 * type 1:个人中心的游记接口 2：公共
	 */
	@POST
	@Path("gettravelNotesInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public String gettravelNotesInfo(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		Integer travelId = jsonobject.optInt("travelId");
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		JSONObject data = new JSONObject();
		JSONArray js1 = new JSONArray();
		JSONArray js2 = new JSONArray();
		try {
			if (travelId != null && travelId > 0 && type != null && !type.isEmpty()) {
				Map<String, Object> map = new HashMap<String, Object>();
				Userinfo userinfo = new Userinfo();
				if (uuID != null && !uuID.isEmpty()) {
					map.put("uuID", uuID);
					userinfo = usermapper.Getuserinfo(map);
				}
				map.put("travelId", travelId);
				map.put("id", travelId);
				map.put("state", 1);
				if (type.equals("1")) {
					if (uuID != null && !uuID.isEmpty()) {
						// map.put("uuID", uuID);
						// Userinfo userinfo = usermapper.Getuserinfo(map);
						result = cardService.checkUser(userinfo);
						if (result.optBoolean("flag")) {
							Date nowdate = CommonDateParseUtil.date2date(new Date());
							Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
							if (nowdate.getTime() >= uuIDExpiry.getTime()) {
								result.put("code", "0004");
								result.put("message", "用户登录已过期，请重新登录！");
								return result.toString();
							} else {
								map.put("userId", userinfo.getId());
							}
						} else {
							return result.toString();
						}
					} else {
						result.put("code", "0002");
						result.put("message", "参数不全！");
						return result.toString();
					}
				} else {
					map.put("isUnderway", 1);
					map.put("openLevel", 0);
				}
				TravelNotes travelNotes = travelmapper.gettravelNotes(map);
				Integer totalPraiseNum = 0;
				if (travelNotes != null) {
					if (type.equals("2") && userinfo != null) {
						map.put("userId", userinfo.getId());
					}
					List<TravelNotesInfo> travelNotesInfoList = scenicMapper.gettravelNotesInfoList(map);
					if (!travelNotesInfoList.isEmpty()) {
						Map<String, Object> map2 = new HashMap<String, Object>();
						for (int i = 0; i < travelNotesInfoList.size(); i++) {
							if (type.equals("2") && userinfo != null && travelNotesInfoList.get(i).getUserId() != null
									&& !travelNotesInfoList.get(i).getUserId().equals(userinfo.getId())) {
								travelNotesInfoList.get(i).setIsPraise("n");
							}
							map2.clear();
							map2.put("noteId", travelNotesInfoList.get(i).getId());
							Integer count = travelmapper.getpraiseCount(map2);
							travelNotesInfoList.get(i).setPraiseNum(count);
							totalPraiseNum = totalPraiseNum + count;
							if (travelNotesInfoList.get(i).getImgUrl() != null
									&& !travelNotesInfoList.get(i).getImgUrl().isEmpty()) {
								try {
									File f = new File(TravelImgUrl + travelNotes.getFileCode() + "/" + "Minfile/"
											+ travelNotesInfoList.get(i).getImgUrl());
									FileInputStream fis = new FileInputStream(f);
									BufferedImage bufferedImg = ImageIO.read(fis);
									travelNotesInfoList.get(i).setH(bufferedImg.getHeight());
									travelNotesInfoList.get(i).setW(bufferedImg.getWidth());
								} catch (Exception t) {
									log.error("读取游记详情图片异常：", t);
								}
								travelNotesInfoList.get(i).setImgUrl(travelImgUrl + travelNotes.getFileCode() + "/"
										+ "Minfile/" + travelNotesInfoList.get(i).getImgUrl());
							}
							if (i == 0) {
								js1.add(0, travelNotesInfoList.get(i).getCrateDate());
								js1.add(travelNotesInfoList.get(i));
							} else {
								if (!travelNotesInfoList.get(i).getCrateDate()
										.equals(travelNotesInfoList.get(i - 1).getCrateDate())) {
									js2.add(js1);
									js1.clear();
									js1.add(0, travelNotesInfoList.get(i).getCrateDate());
									js1.add(travelNotesInfoList.get(i));
								} else {
									js1.add(travelNotesInfoList.get(i));
								}
							}
						}
						js2.add(js1);
						data.put("travelNotesInfoList", js2);
					} else {
						data.put("travelNotesInfoList", "");
					}
					if (travelNotes.getCoverImg() != null && !travelNotes.getCoverImg().isEmpty()) {
						travelNotes.setCoverImg(
								travelImgUrl + travelNotes.getFileCode() + "/" + travelNotes.getCoverImg());
					}
					if (travelNotes.getHeadImg() != null && !travelNotes.getHeadImg().isEmpty()) {
						travelNotes.setHeadImg(userImgUrl + travelNotes.getHeadImg());
					}
					travelNotes.setTotalPraiseNum(totalPraiseNum);
					Map<String, Object> map1 = new HashMap<String, Object>();
					map1.put("id", travelNotes.getId());
					map1.put("afterbrowseCount", travelNotes.getBrowseCount() + 1);
					map1.put("prebrowseCount", travelNotes.getBrowseCount());
					travelmapper.updatebrowseCount(map1);
					travelNotes.setBrowseCount(travelNotes.getBrowseCount() + 1);
					if (travelNotes.getIsUnderway() == 1) {
						data.put("shareUrl", shareurl + "sharetravelNote.html?travelId=" + travelNotes.getId());
					} else {
						data.put("shareUrl", "");
					}
					data.put("travelNotes", travelNotes);
					// 是否收藏与是否是自身创建游记
					if (uuID != null && !uuID.isEmpty()) {
						// map.put("uuID", uuID);
						// userinfo = usermapper.Getuserinfo(map);
						if (userinfo != null) {
							map.clear();
							map.put("userId", userinfo.getId());
							map.put("fk", travelNotes.getId());
							map.put("model", "travelnote");
							List<Collect> collect = scenicMapper.getcollectList(map);
							if (collect != null && !collect.isEmpty()) {
								data.put("isCollect", 1);
							} else {
								data.put("isCollect", 0);
							}
						} else {
							data.put("isCollect", 0);
						}
					} else {
						data.put("isCollect", 0);
					}
					result.put("code", "0001");
					result.put("message", "游记详情查询成功！");
					result.put("data", data);
				} else {
					result.put("code", "0003");
					result.put("message", "暂无数据！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取游记详情失败：" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

	/*
	 * *评价列表接口
	 */
	@POST
	@Path("getevaluateList")
	@Produces(MediaType.APPLICATION_JSON)
	public String getevaluateList(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		Integer pageNum = jsonobject.optInt("pageNum");
		Integer num = jsonobject.optInt("num");
		Integer fkId = jsonobject.optInt("fkId");
		String model = jsonobject.optString("model");
		String type = jsonobject.optString("type");
		JSONObject data = new JSONObject();
		try {
			if (pageNum != null && pageNum > 0 && num != null && num > 0 && model != null && !model.isEmpty()
					&& fkId != null && fkId > 0) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("start", num * (pageNum - 1));
				map.put("num", num);
				map.put("fk", fkId);
				map.put("model", model);
				String totalNum = scenicMapper.getevaluateListCount(map);
				if (type != null && !type.isEmpty()) {
					map.put("type", type);
				}
				List<Evaluate> evaluteList = scenicMapper.getevaluateList(map);
				map.clear();
				map.put("fk", fkId);
				map.put("model", model);
				map.put("type", "l");
				String lEvaluateNum = scenicMapper.getevaluateListCount(map);
				data.put("lEvaluateNum", lEvaluateNum);
				map.clear();
				map.put("fk", fkId);
				map.put("model", model);
				map.put("type", "m");
				String mEvaluateNum = scenicMapper.getevaluateListCount(map);
				data.put("mEvaluateNum", mEvaluateNum);
				map.clear();
				map.put("fk", fkId);
				map.put("model", model);
				map.put("type", "s");
				String sEvaluateNum = scenicMapper.getevaluateListCount(map);
				data.put("sEvaluateNum", sEvaluateNum);
				if (!evaluteList.isEmpty()) {
					// 评价中的图片信息封装
					for (Evaluate evaluate : evaluteList) {
						if (evaluate.getHeadimg() != null && !evaluate.getHeadimg().isEmpty()) {
							evaluate.setHeadimg(userImgUrl + evaluate.getHeadimg());
						}
						map.clear();
						map.put("evaluateId", evaluate.getId());
						List<EvaluateImg> evaluateImgList = scenicMapper.getevaluateImgList(map);
						JSONArray evaluateImgjson = new JSONArray();
						if (!evaluateImgList.isEmpty()) {
							for (EvaluateImg evaluateImg : evaluateImgList) {
								if (evaluateImg.getImgUrl() != null && evaluateImg.getImgUrl() != "") {
									String MinUrl = null;
									if (model.equals("scenic")) {
										MinUrl = evaImgScenicMinUrl;
									} else if (model.equals("hotel")) {
										MinUrl = evaImgHotelMinUrl;
									} else if (model.equals("tastyfood")) {
										MinUrl = evaImgTastyFoodMinUrl;
									} else {
										MinUrl = evaImgSpecialityMinUrl;
									}
									evaluateImg.setImgUrl(MinUrl + evaluateImg.getImgUrl());
								}
							}
							evaluateImgjson = JSONArray.fromObject(evaluateImgList);
							evaluate.setEvaluateImgList(evaluateImgjson.toString());
						}
					}
					data.put("totalNum", totalNum);
					data.put("evaluteList", evaluteList);
					result.put("code", "0001");
					result.put("message", "评价列表查询成功！");
					result.put("data", data);
				} else {
					result.put("code", "0003");
					if (pageNum == 1) {
						result.put("message", "暂无评价！");
					} else {
						result.put("message", "已无更多评价！");
					}
					// result.put("message", "暂无数据！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取评价列表失败：" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

	/*
	 * 景区附近
	 * 
	 */
	@POST
	@Path("getScenicNear")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getScenicNear(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		Integer scenicId = jsonobject.optInt("scenicId");
		JSONObject scenicListjson = new JSONObject();
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (scenicId != null && scenicId > 0) {
				map.put("scenicId", scenicId);
				Scenic scenic = scenicMapper.getscenic(map);
				if (scenic != null) {
					map.put("placeX", scenic.getPlaceX());
					map.put("placeY", scenic.getPlaceY());
					List<ShopNear> shopNearList = shopmapper.getshopNearList(map);
					if (shopNearList != null && !shopNearList.isEmpty()) {
						result.put("code", "0001");
						result.put("message", "景区详情查询成功！");
						result.put("data", shopNearList);
					} else {
						result.put("code", "0003");
						result.put("message", "暂无数据！");
					}
				} else {
					result.put("code", "0003");
					result.put("message", "暂无数据！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取景区附近店铺失败：" , e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}
}
