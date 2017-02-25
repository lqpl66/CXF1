package com.yl.Controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;
import com.yl.Utils.CommonDateParseUtil;
import com.yl.Utils.GetProperties;
import com.yl.beans.Collect;
import com.yl.beans.Evaluate;
import com.yl.beans.EvaluateImg;
import com.yl.beans.Postagetemplate;
import com.yl.beans.SpecialityAttributes;
import com.yl.beans.SpecialityBanner;
import com.yl.beans.SpecialityDetails;
import com.yl.beans.SpecialityInfo;
import com.yl.beans.SpecialityStandard;
import com.yl.beans.TravelNotes;
import com.yl.beans.TravelNotesInfo;
import com.yl.beans.Userinfo;
import com.yl.mapper.ScenicMapper;
import com.yl.mapper.ShopMapper;
import com.yl.mapper.TravelNoteMapper;
import com.yl.mapper.UserMapper;
import com.yl.webRestful.ShopWebRestful;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class ShareController {
	@Autowired
	private ScenicMapper scenicMapper;

	@Autowired
	private ShopMapper shopmapper;
	@Autowired
	private TravelNoteMapper travelmapper;
	// 游记路径
	private String travelImgUrl = GetProperties.gettravelImgUrl();
	// 商品图片路径
	private String shopImgUrl = GetProperties.getshopImgUrl();
	// 用户头像路径
	private String userImgUrl = GetProperties.getuserImgUrl();
	// 游记路径
	private String TravelImgUrl = GetProperties.getTravelImgUrl();
	// 评论特产小图url
	private String evaImgSpecialityMinUrl = GetProperties.getevaImgSpecialityMinUrl();
	
	private String scenicImgUrl = GetProperties.getscenicImgUrl();
	private static Logger log = Logger.getLogger(ShopWebRestful.class);

	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@RequestMapping("/sharetravelNote.html")
	public String sharetravelNote(HttpServletRequest request, HttpServletResponse response, ModelMap param) {
		String travelId_to = request.getParameter("travelId");
		JSONArray js1 = new JSONArray();
		JSONArray js2 = new JSONArray();
		JSONObject j = new JSONObject();
		try {
			if (travelId_to != null && !travelId_to.isEmpty()) {
				Map<String, Object> map = new HashMap<String, Object>();
				Integer travelId = Integer.parseInt(travelId_to);
				map.put("travelId", travelId);
				map.put("id", travelId);
				map.put("state", 1);
					map.put("isUnderway", 1);
					map.put("openLevel", 0);
				TravelNotes travelNotes = travelmapper.gettravelNotes(map);
				Integer totalPraiseNum = 0;
				if (travelNotes != null) {
					List<TravelNotesInfo> travelNotesInfoList = scenicMapper.gettravelNotesInfoList(map);
					if (!travelNotesInfoList.isEmpty()) {
						Map<String, Object> map2 = new HashMap<String, Object>();
						for (int i = 0; i < travelNotesInfoList.size(); i++) {
							map2.clear();
							map2.put("noteId", travelNotesInfoList.get(i).getId());
							Integer count = travelmapper.getpraiseCount(map2);
							travelNotesInfoList.get(i).setPraiseNum(count);
							totalPraiseNum = totalPraiseNum + count;
							if (travelNotesInfoList.get(i).getImgUrl() != null
									&& !travelNotesInfoList.get(i).getImgUrl().isEmpty()) {
								travelNotesInfoList.get(i).setMaximgUrl(travelImgUrl + travelNotes.getFileCode() + "/"
										+ "Maxfile/" + travelNotesInfoList.get(i).getImgUrl());
								travelNotesInfoList.get(i).setImgUrl(travelImgUrl + travelNotes.getFileCode() + "/"
										+ "Minfile/" + travelNotesInfoList.get(i).getImgUrl());
							}
							if (i == 0) {
//								j.clear();
//								j.put("date",  travelNotesInfoList.get(i).getCrateDate());
//								j.put("list",  travelNotesInfoList.get(i));
//								js1.add(j);
								js1.add(0, travelNotesInfoList.get(i).getCrateDate());
								js1.add(travelNotesInfoList.get(i));
							} else {
								if (!travelNotesInfoList.get(i).getCrateDate()
										.equals(travelNotesInfoList.get(i - 1).getCrateDate())) {
									js2.add(js1);
									js1.clear();
									j.clear();
//									j.put("date",  travelNotesInfoList.get(i).getCrateDate());
//									j.put("list",  travelNotesInfoList.get(i));
//									js1.add(j);
									js1.add(0, travelNotesInfoList.get(i).getCrateDate());
									js1.add(travelNotesInfoList.get(i));
								} else {
//									j.put("list",  travelNotesInfoList.get(i));
//									js1.add(j);
									js1.add(travelNotesInfoList.get(i));
								}
							}
						}
						js2.add(js1);
						param.put("travelNotesInfoList", js2);
					} else {
						param.put("travelNotesInfoList", "");
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
					param.put("travelNotes", travelNotes);
					param.put("code", "0001");
					param.put("message", "游记详情查询成功！");
				} else {
					param.put("code", "0003");
					param.put("message", "暂无数据！");
				}
			} else {
				param.put("code", "0002");
				param.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取游记详情失败：" , e);
			param.put("code", "0000");
			param.put("message", "平台繁忙，请稍候！");
		}
//		param.put("s", s);
		return "travelNote";
	}

	@RequestMapping(value = "/shareShop.html", method = RequestMethod.GET)
	public String shareShop(HttpServletRequest request, HttpServletResponse response, ModelMap param) {
		String specialityId_to = request.getParameter("specialityId");
		Integer specialityId = Integer.parseInt(specialityId_to);
		String goodsStandardId_to = request.getParameter("goodsStandardId");
		Integer goodsStandardId = Integer.parseInt(goodsStandardId_to);
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (specialityId != null && specialityId > 0&&goodsStandardId!=null&&goodsStandardId>0) {
				map.put("id", specialityId);
				SpecialityDetails sd = shopmapper.getSpecialityDetails(map);
				if (sd != null) {
					if (sd.getLogo() != null && !sd.getLogo().isEmpty()) {
						sd.setLogo(shopImgUrl + sd.getShopFileCode() + "/" + sd.getLogo());
					}
					// 查询快递信息
					if (!sd.getFreightId().equals(0)) {
						map.clear();
						map.put("id", sd.getFreightId());
						Postagetemplate f = shopmapper.getPostagetemplate(map);
						String intro = null;
						if (f.getDefaultPrice().compareTo(new BigDecimal(0)) == 0) {
							sd.setFreightIntro("包邮");
						} else {
							if (f.getMaxPrice().compareTo(new BigDecimal(0)) == 0) {
								sd.setFreightIntro("默认" + f.getDefaultPrice().setScale(2) + "元");
							} else {
								sd.setFreightIntro("默认" + f.getDefaultPrice().setScale(2) + "元,满"
										+ f.getMaxPrice().setScale(2) + "元包邮");
							}
						}
					} else {
						sd.setFreightIntro("包邮");
					}
					// 查询特产轮播表
					HttpSession s = request.getSession();
					s.setAttribute("list", "");
					map.clear();
					map.put("goodId", specialityId);
					List<SpecialityBanner> sbList = shopmapper.getSpecialityBannerList(map);
					List<String> list = new ArrayList<String>();
					StringBuilder ssb = new StringBuilder();
					if (sbList != null && !sbList.isEmpty()) {
						for (SpecialityBanner sb : sbList) {
							if (sb.getImgUrl() != null && !sb.getImgUrl().isEmpty()) {
								sb.setImgUrl(shopImgUrl + sd.getShopFileCode() + "/Speciality/" + sd.getFileCode() + "/"
										+ sb.getImgUrl());
								list.add(sb.getImgUrl().substring(sb.getImgUrl().indexOf("files")));
								ssb.append(sb.getImgUrl() + ",");
							}
						}
						param.put("SpecialityBannerList", sbList);
						String sbbb = ssb.substring(0, ssb.lastIndexOf(","));
						s.setAttribute("list", sbbb);
					} else {
						param.put("SpecialityBannerList", "");
					}
					// 选中特产规格
					map.clear();
					map.put("goodId", specialityId);
					map.put("goodsStandardId", goodsStandardId);
					List<SpecialityStandard> ss = shopmapper.getSpecialityStandardList(map);
					if (ss != null && !ss.isEmpty()) {
						param.put("SpecialityStandard", ss.get(0));
					} else {
						param.put("SpecialityStandard", "");
					}
					// 特产规格列表
					map.clear();
					map.put("goodId", specialityId);
					List<SpecialityStandard> ssList = shopmapper.getSpecialityStandardList(map);
					if (ssList != null && !ssList.isEmpty()) {
						param.put("SpecialityStandardList", ssList);
					} else {
						param.put("SpecialityStandardList", "");
					}
					// 店铺收藏数
					map.clear();
					map.put("fk", sd.getShopId());
					map.put("model", "shop");
					String collectTotalNum = scenicMapper.getcollectListCount(map);
					sd.setCollectTotalNum(collectTotalNum);
					// 店铺产品总数
					map.clear();
					map.put("shopId", sd.getShopId());
					String goodsTotalNum = shopmapper.getspecialityCount(map);
					sd.setGoodsTotalNum(goodsTotalNum);
					param.put("specialityDetails", sd);
					// 评论总数
					map.clear();
					map.put("fk", specialityId);
					map.put("model", "speciality");
					String toatalEvaluateNum = scenicMapper.getevaluateListCount(map);
					param.put("toatalEvaluateNum", toatalEvaluateNum);
					// 特产详情
					map.clear();
					map.put("goodId", specialityId);
					List<SpecialityInfo> siList = shopmapper.getSpecialityInfoList(map);
					if (siList != null && !siList.isEmpty()) {
						for (SpecialityInfo si : siList) {
							if (si.getImgUrl() != null && !si.getImgUrl().isEmpty()) {
								si.setImgUrl(shopImgUrl + sd.getShopFileCode() + "/Speciality/" + sd.getFileCode() + "/"
										+ si.getImgUrl());
							}
						}
						param.put("SpecialityInfoList", siList);
					} else {
						param.put("SpecialityInfoList", "");
					}
					// 特产属性参数
					map.clear();
					map.put("id", specialityId);
					List<SpecialityAttributes> saList = shopmapper.getspcialityAttributes(map);
					if (saList != null && !saList.isEmpty()) {
						param.put("SpecialityAttributesList", saList);
					} else {
						param.put("SpecialityAttributesList", "");
					}
					// 商品下评论列表
					map.put("start", 0);
					map.put("num", 5);
					map.put("fk", specialityId);
					map.put("model", "speciality");
					List<Evaluate> evaluteList = scenicMapper.getevaluateList(map);
					if (evaluteList != null && !evaluteList.isEmpty()) {
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
										MinUrl = evaImgSpecialityMinUrl;
										evaluateImg.setImgUrl(MinUrl + evaluateImg.getImgUrl());
									}
								}
								evaluate.setEvaluateImgList(evaluateImgjson.toString());
							}
							evaluate.setEvaluateImglist(evaluateImgList);
						}
						param.put("evaluteList", evaluteList);
					} else {
						param.put("evaluteList", "");
					}
					JSONArray sss = JSONArray.fromObject(evaluteList);
					s.setAttribute("evaluete", sss);
					// 评价列表页面
					map.put("start", 0);
					map.put("num", 10);
					map.put("fk", specialityId);
					map.put("model", "speciality");
					String totalNum = scenicMapper.getevaluateListCount(map);
					List<Evaluate> evaluteList3 = scenicMapper.getevaluateList(map);
					map.clear();
					map.put("fk", specialityId);
					map.put("model", "speciality");
					map.put("type", "l");
					String lEvaluateNum = scenicMapper.getevaluateListCount(map);
					param.put("lEvaluateNum", lEvaluateNum);
					map.clear();
					map.put("fk", specialityId);
					map.put("model", "speciality");
					map.put("type", "m");
					String mEvaluateNum = scenicMapper.getevaluateListCount(map);
					param.put("mEvaluateNum", mEvaluateNum);
					map.clear();
					map.put("fk", specialityId);
					map.put("model", "speciality");
					map.put("type", "s");
					String sEvaluateNum = scenicMapper.getevaluateListCount(map);
					param.put("sEvaluateNum", sEvaluateNum);
					if (!evaluteList3.isEmpty()) {
						// 评价中的图片信息封装
						for (Evaluate evaluate : evaluteList3) {
							if (evaluate.getHeadimg() != null && !evaluate.getHeadimg().isEmpty()) {
								evaluate.setHeadimg(userImgUrl + evaluate.getHeadimg());
							}
							map.clear();
							map.put("evaluateId", evaluate.getId());
							List<EvaluateImg> evaluateImgList3 = scenicMapper.getevaluateImgList(map);
							JSONArray evaluateImgjson = new JSONArray();
							if (!evaluateImgList3.isEmpty()) {
								for (EvaluateImg evaluateImg : evaluateImgList3) {
									if (evaluateImg.getImgUrl() != null && evaluateImg.getImgUrl() != "") {
										String MinUrl = null;
										MinUrl = evaImgSpecialityMinUrl;
										evaluateImg.setImgUrl(MinUrl + evaluateImg.getImgUrl());
									}
								}
								evaluateImgjson = JSONArray.fromObject(evaluateImgList3);
								evaluate.setEvaluateImgList(evaluateImgjson.toString());
							}
							evaluate.setEvaluateImglist(evaluateImgList3);
						}
					}
					JSONArray sss3 = JSONArray.fromObject(evaluteList3);
					s.setAttribute("evaluete3", sss3);
					param.put("totalNum", totalNum);
					param.put("evaluteList3", evaluteList3);
				} else {
					param.put("code", "0003");
					param.put("message", "暂无数据！");
				}
			} else {
				param.put("code", "0002");
				param.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取特产详情失败：" , e);
			param.put("code", "0000");
			param.put("message", "平台繁忙，请稍候！");
		}
		return "shop";
	}

	@ResponseBody
	@RequestMapping("/getevaluateList")
	public Map<String, Object> getevaluateList(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> param = new HashMap<String, Object>();
		try {
			String specialityId_to = request.getParameter("specialityId");
			String pageNum_to = request.getParameter("pageNum");
			String type = request.getParameter("type");
			HashMap<String, Object> map = new HashMap<String, Object>();
//			HttpSession s = request.getSession();
			if (specialityId_to != null && !specialityId_to.isEmpty() && pageNum_to != null
					&& !pageNum_to.isEmpty()) {
				// 评价列表页面
				Integer specialityId = Integer.parseInt(specialityId_to);
				Integer pageNum = Integer.parseInt(pageNum_to);
//				if(pageNum==2){
//					map.put("start", 0);
//					map.put("num", 6);
//				}else{
					map.put("start", (pageNum - 1) *2);
					map.put("num", 2);	
//				}
				map.put("fk", specialityId);
				map.put("model", "speciality");
				map.put("type", type);
				List<Evaluate> evaluteList3 = scenicMapper.getevaluateList(map);
				if (!evaluteList3.isEmpty()) {
					// 评价中的图片信息封装
					for (Evaluate evaluate : evaluteList3) {
						if (evaluate.getHeadimg() != null && !evaluate.getHeadimg().isEmpty()) {
							evaluate.setHeadimg(userImgUrl + evaluate.getHeadimg());
						}
						map.clear();
						map.put("evaluateId", evaluate.getId());
						List<EvaluateImg> evaluateImgList3 = scenicMapper.getevaluateImgList(map);
						JSONArray evaluateImgjson = new JSONArray();
						if (!evaluateImgList3.isEmpty()) {
							for (EvaluateImg evaluateImg : evaluateImgList3) {
								if (evaluateImg.getImgUrl() != null && evaluateImg.getImgUrl() != "") {
									String MinUrl = null;
									MinUrl = evaImgSpecialityMinUrl;
									evaluateImg.setImgUrl(MinUrl + evaluateImg.getImgUrl());
								}
							}
							evaluateImgjson = JSONArray.fromObject(evaluateImgList3);
							evaluate.setEvaluateImgList(evaluateImgjson.toString());
						}
						evaluate.setEvaluateImglist(evaluateImgList3);
					}
					JSONArray sss3 = JSONArray.fromObject(evaluteList3);
//					s.setAttribute("evaluete3", sss3);
					param.put("evaluteList3", evaluteList3);
					param.put("code", "0001");
				} else {
					param.put("code", "0002");
					if(pageNum==1){
						param.put("message", "暂无商品评价！");
					}else{
						param.put("message", "已无更多商品评价！");
					}
//					param.put("message", "暂无数据！");
				}
			} else {
				param.put("code", "0003");
				param.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("分享商品的评论列表接口异常：", e);
			param.put("code", "0000");
			param.put("message", "平台繁忙，稍候再试！");
		}
		return param;
	}
	
	@RequestMapping("/files/{a}/{b}/{c}/{d}/{e}")
	public String playmusic(@PathVariable String a,@PathVariable String b,@PathVariable String c,@PathVariable String d,@PathVariable String e,HttpServletRequest request, HttpServletResponse response, ModelMap param) {
//		String url =scenicImgUrl + a+"/"+b+"/"+c+"/"+d;
		String strBackUrl = "http://" + request.getServerName() //服务器地址  
         + ":"   
         + request.getServerPort()           //端口号  
         + request.getContextPath()      //项目名称  
         + request.getServletPath() ;     //请求页面或其他地址  );
		param.put("fileUrl","http://192.168.1.87:18080/YlServer/files/Scenic/00000001/Attract/00000001/1.mp3");
		return "playmusic";
	}
}
