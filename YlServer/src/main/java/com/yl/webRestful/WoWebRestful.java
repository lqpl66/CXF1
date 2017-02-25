package com.yl.webRestful;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

//import com.yl.webService.UserWebService;
import com.yl.Utils.BaseParseImage;
import com.yl.Utils.CodeUtils;
import com.yl.Utils.CommonDateParseUtil;
import com.yl.Utils.FileUtils;
import com.yl.Utils.GetProperties;
import com.yl.Utils.ImageResize;
import com.yl.Utils.MD5Utils;
import com.yl.beans.City;
import com.yl.beans.Evaluate;
import com.yl.beans.EvaluateImg;
import com.yl.beans.FeedbackType;
import com.yl.beans.GoodsExpandDetail;
import com.yl.beans.PresentApplication;
import com.yl.beans.Province;
import com.yl.beans.ScenicCollect;
import com.yl.beans.ScenicName;
import com.yl.beans.ScenicRcmd;
import com.yl.beans.Shop;
import com.yl.beans.Speciality;
import com.yl.beans.TravelNotes;
import com.yl.beans.TravelNotesInfo;
import com.yl.beans.UserAddress;
import com.yl.beans.UserAmount;
import com.yl.beans.UserCard;
import com.yl.beans.UserPayAccount;
import com.yl.beans.Userinfo;
import com.yl.mapper.CardMapper;
import com.yl.mapper.ExpenseMapper;
import com.yl.mapper.ScenicMapper;
import com.yl.mapper.ShopMapper;
import com.yl.mapper.TravelNoteMapper;
import com.yl.mapper.UserMapper;
import com.yl.service.CardService;
import com.yl.service.ConsumeService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*
 * 评价和游记接口
 */
@Component("wowebRest")
public class WoWebRestful {
	@Autowired
	private ScenicMapper scenicMapper;

	@Autowired
	private UserMapper usermapper;

	@Autowired
	private TravelNoteMapper travelmapper;

	@Autowired
	private ShopMapper shopmapper;

	@Autowired
	private CardMapper cardmapper;
	@Autowired
	private ExpenseMapper expenseMapper;
	@Autowired
	private ConsumeService consumeService;
	@Autowired
	private CardService cardService;
	// private String imgurl = GetProperties.getImgUrlPath();
	// // 语音路径
	// private String fileurl = GetProperties.getFileUrlPath();
	// 存放评论景区小图
	private String EvaImgScenicMinUrl = GetProperties.getEvaImgScenicMinUrl();
	// 存放评论景区大图
	private String EvaImgScenicMaxUrl = GetProperties.getEvaImgScenicMaxUrl();
	// 存放评论酒店小图
	private String EvaImgHotelMinUrl = GetProperties.getEvaImgHotelMinUrl();
	// 存放评论酒店大图
	private String EvaImgHotelMaxUrl = GetProperties.getEvaImgHotelMaxUrl();
	// 存放评论特产小图
	private String EvaImgSpecialityMinUrl = GetProperties.getEvaImgSpecialityMinUrl();
	// 存放评论特产大图
	private String EvaImgSpecialityMaxUrl = GetProperties.getEvaImgSpecialityMaxUrl();
	// 存放评论美食小图
	private String EvaImgTastyFoodMinUrl = GetProperties.getEvaImgTastyFoodMinUrl();
	// 存放评论美食大图
	private String EvaImgTastyFoodMaxUrl = GetProperties.getEvaImgTastyFoodMaxUrl();
	// 存放游记路径拼接
	private String TravelImgUrl = GetProperties.getTravelImgUrl();
	// 景区路径
	private String scenicImgUrl = GetProperties.getscenicImgUrl();
	// 游记路径
	private String travelImgUrl = GetProperties.gettravelImgUrl();
	// 用户头像路径
	private String userImgUrl = GetProperties.getuserImgUrl();
	// 商品图片路径
	private String shopImgUrl = GetProperties.getshopImgUrl();
	private static Logger log = Logger.getLogger(WoWebRestful.class);

	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/*
	 * 评价接口
	 */
	@SuppressWarnings("finally")
	@Path("saveEvaluate")
	@POST
	@Transactional
	@Produces({ MediaType.APPLICATION_JSON })
	public String saveEvaluate(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		System.out.println(json);
		String uuID = jsonobject.optString("uuID");
		BigDecimal score = new BigDecimal(jsonobject.optDouble("score"));
		Integer fk = jsonobject.optInt("fkId");
		Integer goodsStandardId = jsonobject.optInt("goodsStandardId");
		String orderNo = jsonobject.optString("orderNo");
		String content = jsonobject.optString("content");
		String model = jsonobject.optString("model");
		String evaluateImgList = jsonobject.optString("evaluateImgList");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && score != null && fk != null && fk > 0 && content != null
					&& !content.isEmpty() && model != null && !model.isEmpty()) {
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
						Evaluate ev = new Evaluate();
						ev.setContent(content);
						ev.setFk(fk);
						ev.setModel(model);
						ev.setScore(score);
						ev.setUserId(userinfo.getId());
						Calendar time = Calendar.getInstance();
						ev.setAddTime(df.format((Date) time.getTime()));
						if (model.equals("speciality")) {// 校验该特产
							if (goodsStandardId != null && goodsStandardId > 0 && orderNo != null
									&& !orderNo.isEmpty()) {
								JSONObject re = consumeService.checkEvaluate(orderNo, userinfo);
								if (re != null && re.getBoolean("flag")) {// 校验是否已经评价
									map.clear();
									map.put("orderNo", orderNo);
									map.put("goodsStandardId", goodsStandardId);
									map.put("goodId", fk);
									List<GoodsExpandDetail> gedList = expenseMapper.getGoodsExpandDetail(map);
									if (gedList != null && !gedList.isEmpty()) {
										if (gedList.get(0).getEvaluateId() != 0) {
											result.put("code", "0006");
											result.put("message", "您已评价该特产，无法追评！");
											return result.toString();
										} else {
											ev.setGoodsOrderExpandId(gedList.get(0).getId());
										}
									} else {
										result.put("code", "0005");
										result.put("message", "该特产不存在，无法评价！");
										return result.toString();
									}
								} else {
									return re.toString();
								}
								ev.setGoodsStandardId(goodsStandardId);
								ev.setOrderNo(orderNo);
							} else {
								result.put("code", "0002");
								result.put("message", "参数不全！");
								return result.toString();
							}
						}
						usermapper.saveEvaluate(ev);
						JSONArray ImgJsArray = new JSONArray();
						if (evaluateImgList != null && !evaluateImgList.isEmpty()) {
							ImgJsArray = JSONArray.fromObject(evaluateImgList);
							List<EvaluateImg> evaluateImgLists = JSONArray.toList(ImgJsArray, EvaluateImg.class);
							for (EvaluateImg evaluateImg : evaluateImgLists) {
								evaluateImg.setEvaluateId(ev.getId());
								String MaxUrl = null;
								String MinUrl = null;
								if (model.equals("hotel")) {
									MinUrl = EvaImgHotelMinUrl;
									MaxUrl = EvaImgHotelMaxUrl;
								} else if (model.equals("scenic")) {
									MinUrl = EvaImgScenicMinUrl;
									MaxUrl = EvaImgScenicMaxUrl;
								} else if (model.equals("tastyfood")) {
									MinUrl = EvaImgTastyFoodMinUrl;
									MaxUrl = EvaImgTastyFoodMaxUrl;
								} else {
									MinUrl = EvaImgSpecialityMinUrl;
									MaxUrl = EvaImgSpecialityMaxUrl;
								}
								String imgUrl = BaseParseImage.generateImage(evaluateImg.getImgUrl(), MaxUrl, ".jpg",
										null);
								evaluateImg.setImgUrl(imgUrl);
								// 缩略图
								String W1 = GetProperties.getevaluateTypeW1();
								ImageResize.getImgeResize(MaxUrl, MinUrl, W1, imgUrl);
								// 原图处理
								String W2 = GetProperties.getevaluateTypeW2();
								ImageResize.getImgeResize(MaxUrl, MinUrl, W2, imgUrl);
								usermapper.saveEvaluateImg(evaluateImg);
							}
						}
						result.put("code", "0001");
						result.put("message", "评价成功！");
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("评价" + model + "失败：" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 游记的编辑 type:1:游记新增；2：游记修改；3游记状态修改；游记详情删除;
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Path("operateTravelNotes")
	@Produces({ MediaType.APPLICATION_JSON })
	public String operateTravelNotes(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonObject = new JSONObject();
		jsonObject = JSONObject.fromObject(json);
		String type = jsonObject.optString("type");
		String uuID = jsonObject.optString("uuID");
		Map<String, Object> map = new HashMap<String, Object>();
		boolean flag = false;
		try {
			if (type != null && !type.isEmpty() && uuID != null && !uuID.isEmpty()) {
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
						Calendar time = Calendar.getInstance();
						if (type.equals("1")) {
							if (jsonObject.get("travelNotes") != null && jsonObject.get("travelNotes") != "") {
								JSONObject travelNoteJson = jsonObject.getJSONObject("travelNotes");
								TravelNotes travelNotes = (TravelNotes) JSONObject.toBean(travelNoteJson,
										TravelNotes.class);
								travelNotes.setId(null);
								String fileCode = CodeUtils.getfileCode(
										userinfo.getUserCode().substring(userinfo.getUserCode().length() - 6));
								travelNotes.setFileCode(fileCode);
								travelNotes.setUserId(userinfo.getId());
								String coverImg = BaseParseImage.generateImage(travelNotes.getCoverImg(),
										TravelImgUrl + fileCode + "/", ".jpg", "cover");
								ImageResize.resize1(TravelImgUrl + fileCode + "/", TravelImgUrl + fileCode + "/",
										coverImg);
								travelNotes.setCoverImg(coverImg);
								travelNotes.setAddTime(df.format((Date) time.getTime()));
								travelNotes.setIsRecommend(0);
								travelmapper.savetravelNotes(travelNotes);
								if (jsonObject.get("travelNotesInfo") != null && jsonObject.get("travelNotesInfo") != ""
										&& !jsonObject.getJSONArray("travelNotesInfo").isEmpty()) {
									JSONArray travelNotesInfoListJson = jsonObject.getJSONArray("travelNotesInfo");
									List<TravelNotesInfo> travelNotesInfoList = JSONArray
											.toList(travelNotesInfoListJson, TravelNotesInfo.class);
									for (TravelNotesInfo travelNotesInfo : travelNotesInfoList) {
										travelNotesInfo.setTravelId(travelNotes.getId());
										if (travelNotesInfo.getImgUrl() != null
												&& !travelNotesInfo.getImgUrl().isEmpty()) {
											// 大图
											String imgUrl = BaseParseImage.generateImage(travelNotesInfo.getImgUrl(),
													TravelImgUrl + fileCode + "/Maxfile/", ".jpg", null);
//											System.out.println("img:"+travelNotesInfo.getImgUrl());
											// 小图
											String W1 = GetProperties.getevaluateTypeW1();
											ImageResize.getImgeResize(TravelImgUrl + fileCode + "/Maxfile/",
													TravelImgUrl + fileCode + "/Minfile/", W1, imgUrl);
											// 大图处理
											String W2 = GetProperties.getevaluateTypeW2();
											ImageResize.getImgeResize(TravelImgUrl + fileCode + "/Maxfile/",
													TravelImgUrl + fileCode + "/Maxfile/", W2, imgUrl);
											travelNotesInfo.setImgUrl(imgUrl);
										}
									}
									travelmapper.savetravelNotesInfoList(travelNotesInfoList);
								}
							} else {
								flag = true;
							}
						} else if (type.equals("2")) {
							if (jsonObject.get("travelNotes") != null && jsonObject.get("travelNotes") != "") {
								JSONObject travelNoteJson = jsonObject.getJSONObject("travelNotes");
								TravelNotes travelNotes = (TravelNotes) JSONObject.toBean(travelNoteJson,
										TravelNotes.class);
								map.clear();
								map.put("userId", userinfo.getId());
								map.put("id", travelNotes.getId());
								TravelNotes preTravelNotes = travelmapper.gettravelNotes(map);
								if (travelNotes.getCoverImg() != null && !travelNotes.getCoverImg().isEmpty()
										&& !travelNotes.getCoverImg().contains("cover")) {
									// 删除原有的封面文件
									FileUtils.deleteFile(TravelImgUrl + preTravelNotes.getFileCode() + "/"
											+ preTravelNotes.getCoverImg());
									String coverImg = BaseParseImage.generateImage(travelNotes.getCoverImg(),
											TravelImgUrl + preTravelNotes.getFileCode() + "/", ".jpg", "cover");
									ImageResize.resize1(TravelImgUrl + preTravelNotes.getFileCode() + "/",
											TravelImgUrl + preTravelNotes.getFileCode() + "/", coverImg);
									travelNotes.setCoverImg(coverImg);
								}
								travelmapper.updatetravelNotes(travelNotes);
								if (jsonObject.get("travelNotesInfo") != null
										&& jsonObject.get("travelNotesInfo") != "") {
									JSONArray travelNotesInfoListJson = jsonObject.getJSONArray("travelNotesInfo");
									List<TravelNotesInfo> travelNotesInfoList = JSONArray
											.toList(travelNotesInfoListJson, TravelNotesInfo.class);
									List<TravelNotesInfo> l1 = new ArrayList<TravelNotesInfo>();
									List<TravelNotesInfo> l2 = new ArrayList<TravelNotesInfo>();
									for (TravelNotesInfo travelNotesInfo : travelNotesInfoList) {
										if (travelNotesInfo.getImgUrl() != null
												&& !travelNotesInfo.getImgUrl().isEmpty()
												&& !travelNotesInfo.getImgUrl().contains(".jpg")) {
											map.clear();
											map.put("travelId", preTravelNotes.getId());
											map.put("id", travelNotesInfo.getId());
											TravelNotesInfo preTravelNotesInfo = travelmapper.gettravelNotesInfo(map);
											if (preTravelNotesInfo != null && preTravelNotesInfo.getImgUrl() != null
													&& preTravelNotesInfo.getImgUrl() != "") {
												// 删除原有的详情的大小文件
												FileUtils.deleteFile(TravelImgUrl + preTravelNotes.getFileCode()
														+ "/Maxfile/" + preTravelNotesInfo.getImgUrl());
												FileUtils.deleteFile(TravelImgUrl + preTravelNotes.getFileCode()
														+ "/Minfile/" + preTravelNotesInfo.getImgUrl());
											}
											// 大图
											String imgUrl = BaseParseImage.generateImage(travelNotesInfo.getImgUrl(),
													TravelImgUrl + preTravelNotes.getFileCode() + "/Maxfile/", ".jpg",
													null);
//											log.info("ee:"+travelNotesInfo.getImgUrl());
//											System.out.println("img:"+travelNotesInfo.getImgUrl());
											// 小图
											String W1 = GetProperties.getevaluateTypeW1();
											ImageResize.getImgeResize(
													TravelImgUrl + preTravelNotes.getFileCode() + "/Maxfile/",
													TravelImgUrl + preTravelNotes.getFileCode() + "/Minfile/", W1,
													imgUrl);
											// 大图处理
											String W2 = GetProperties.getevaluateTypeW2();
											ImageResize.getImgeResize(
													TravelImgUrl + preTravelNotes.getFileCode() + "/Maxfile/",
													TravelImgUrl + preTravelNotes.getFileCode() + "/Maxfile/", W2,
													imgUrl);
											travelNotesInfo.setImgUrl(imgUrl);
										}
										if (travelNotesInfo.getId() != null && travelNotesInfo.getId() > 0) {
											travelNotesInfo.setTravelId(travelNotes.getId());
											l1.add(travelNotesInfo);
										} else {
											travelNotesInfo.setId(null);
											travelNotesInfo.setTravelId(travelNotes.getId());
											l2.add(travelNotesInfo);
										}
									}

									if (l1 != null && !l1.isEmpty()) {
										for (TravelNotesInfo tr : l1) {
											travelmapper.updatetravelNotesInfoList(tr);
										}
									}
									if (l2 != null && !l2.isEmpty()) {
										travelmapper.savetravelNotesInfoList(l2);
									}

								}
							} else {
								flag = true;
							}
						} else if (type.equals("3")) {
							Integer travelId = jsonObject.getInt("travelId");
							Integer isUnderway = jsonObject.getInt("isUnderway");
							Integer openLevel = jsonObject.getInt("openLevel");
							Integer state = jsonObject.getInt("state");
							Integer userId = userinfo.getId();
							if (travelId != null && travelId > 0
									&& ((state != null && state > 0) || (isUnderway != null) || (openLevel != null))) {
								TravelNotes t1 = new TravelNotes();
								t1.setId(travelId);
								t1.setState(state);
								t1.setUserId(userId);
								t1.setIsUnderway(isUnderway);
								t1.setOpenLevel(openLevel);
								travelmapper.updatetravelNotes(t1);
							} else {
								flag = true;
							}
						} else if (type.equals("4")) {
							Integer id = jsonObject.optInt("travelInfoId");
							Integer travelId = jsonObject.optInt("travelId");
							if (id != null && id > 0 && travelId != null && travelId > 0) {
								map.clear();
								map.put("userId", userinfo.getId());
								map.put("id", travelId);
								TravelNotes preTravelNotes = travelmapper.gettravelNotes(map);
								map.clear();
								map.put("id", id);
								map.put("travelId", travelId);
								TravelNotesInfo preTravelNotesInfo = travelmapper.gettravelNotesInfo(map);
								// 删除原有的详情的大小文件
								FileUtils.deleteFile(TravelImgUrl + preTravelNotes.getFileCode() + "/Maxfile/"
										+ preTravelNotesInfo.getImgUrl());
								FileUtils.deleteFile(TravelImgUrl + preTravelNotes.getFileCode() + "/Minfile/"
										+ preTravelNotesInfo.getImgUrl());
								travelmapper.deleteById(map);
							} else {
								flag = true;
							}
						}
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无该用户！");
					return result.toString();
				}
			} else {
				flag = true;
			}
			if (flag) {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			} else {
				result.put("code", "0001");
				result.put("message", "操作成功！");
			}
		} catch (Exception e) {
			log.error("游记数据处理接口异常：" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	@POST
	@Path("getProvince")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getProvince() {
		JSONObject result = new JSONObject();
		try {
			List<Province> provinceList = travelmapper.getProvince();
			if (!provinceList.isEmpty()) {
				result.put("code", "0001");
				result.put("message", "获取省份列表成功！");
				result.put("data", provinceList);
			} else {
				result.put("code", "0003");
				result.put("message", "暂无数据！");
			}
		} catch (Exception e) {
			log.error("获取省份失败：" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

	@POST
	@Path("getCity")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getCity(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonObject = new JSONObject();
		jsonObject = JSONObject.fromObject(json);
		Integer provinceId = jsonObject.optInt("provinceId");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (provinceId != null && provinceId > 0) {
				map.put("provinceId", provinceId);
				List<City> cityList = travelmapper.getCity(map);
				if (cityList != null && !cityList.isEmpty()) {
					result.put("code", "0001");
					result.put("message", "获取城市列表成功！");
					result.put("data", cityList);
				} else {
					result.put("code", "0003");
					result.put("message", "暂无数据！");
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("获取城市列表失败：" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候！");
		}
		return result.toString();
	}

	/*
	 * 收藏与取消 type 1:收藏；2：取消
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("operateCollect")
	public String operateCollect(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		Integer fk = jsonobject.optInt("fkId");
		String model = jsonobject.optString("model");
		String type = jsonobject.optString("type");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && fk != null && fk > 0 && model != null && !model.isEmpty()
					&& type != null && !type.isEmpty()) {
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
					} else {
						map.clear();
						map.put("userId", userinfo.getId());
						map.put("fk", fk);
						map.put("model", model);
						if (type.equals("1")) {// 自己的游记无法收藏
							if (model.equals("travelNote")) {
								map.clear();
								map.put("travelId", fk);
								map.put("userId", userinfo.getId());
								List<TravelNotes> tList = scenicMapper.gettravelNotesList(map);
								if (tList != null && !tList.isEmpty()) {
									result.put("code", "0005");
									result.put("message", "无法收藏自己的游记！");
									return result.toString();
								}
							}
							map.clear();
							map.put("userId", userinfo.getId());
							map.put("fk", fk);
							map.put("model", model);
							Calendar time = Calendar.getInstance();
							String addTime = df.format(time.getTime());
							map.put("addTime", addTime);
							usermapper.saveCollect(map);
						} else {
							usermapper.deleteCollectById(map);
						}
						result.put("code", "0001");
						result.put("message", "操作成功！");
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("收藏操作失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 收藏列表
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getCollectList")
	public String getCollectList(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String model = jsonobject.optString("model");
		Integer pageNum = jsonobject.optInt("pageNum");
		Integer num = jsonobject.optInt("num");
		Map<String, Object> map = new HashMap<String, Object>();
		boolean flag = false;
		try {
			if (pageNum != null && pageNum > 0 && num != null && num > 0 && uuID != null && !uuID.isEmpty()
					&& model != null && !model.isEmpty()) {
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
					} else {
						map.clear();
						map.put("userId", userinfo.getId());
						map.put("model", model);
						map.put("start", num * (pageNum - 1));
						map.put("num", num);
						if (model.endsWith("scenic")) {
							List<ScenicCollect> list = scenicMapper.getscenicCollectList(map);
							if (list != null && !list.isEmpty()) {
								for (ScenicCollect sc : list) {
									sc.setCoverImg(scenicImgUrl + sc.getFileCode() + "/" + sc.getCoverImg());
								}
								result.put("data", list);
							} else {
								flag = true;
							}
						} else if (model.equals("travelNote")) {
							List<TravelNotes> list = travelmapper.gettravelNoteCollcet(map);
							if (list != null && !list.isEmpty()) {
								for (TravelNotes tr : list) {
									if (tr.getCoverImg() != null && !tr.getCoverImg().isEmpty()) {
										tr.setCoverImg(travelImgUrl + tr.getFileCode() + "/" + tr.getCoverImg());
									}
									if (tr.getHeadImg() != null && !tr.getHeadImg().isEmpty()) {
										tr.setHeadImg(userImgUrl + tr.getHeadImg());
									}
									map.put("travelId", tr.getId());
									TravelNotesInfo tni = travelmapper.gettravelNotesInfo(map);
									if (tni != null) {
										tr.setDescribeInfo(tni.getDescribeInfo());
									}
								}
								result.put("data", list);
							} else {
								flag = true;
							}
						} else {
							// 店铺的收藏
							List<Shop> list = shopmapper.getshopCollectList(map);
							if (list != null && !list.isEmpty()) {
								for (Shop s : list) {
									map.clear();
									map.put("fk", s.getId());
									map.put("model", model);
									s.setLogo(shopImgUrl + s.getFileCode() + "/" + s.getLogo());
									String collectTotalNum = scenicMapper.getcollectListCount(map);
									s.setTotalCollectNum(collectTotalNum);
								}
								result.put("data", list);
							} else {
								flag = true;
							}
						}
						if (!flag) {
							result.put("code", "0001");
							result.put("message", "查询收藏列表成功！");
						} else {
							result.put("code", "0001");
							// result.put("message", "暂无数据！");
							if (pageNum == 1) {
								result.put("message", "暂无收藏！");
							} else {
								result.put("message", "已无更多收藏！");
							}
							result.put("data", "");
						}
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("查询收藏列表失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
		}
		return result.toString();
	}

	/*
	 * 点赞 type 1:增加；2：取消
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("operatePraise")
	public String operatePraise(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		Integer noteId = jsonobject.optInt("noteId");
		String type = jsonobject.getString("type");
		Map<String, Object> map = new HashMap<String, Object>();
		boolean flag = false;
		try {
			if (uuID != null && !uuID.isEmpty() && noteId != null && noteId > 0) {
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
						map.put("noteId", noteId);
						if (type.equals("1")) {
							Calendar time = Calendar.getInstance();
							String addTime = df.format(time.getTime());
							map.put("addTime", addTime);
							travelmapper.savepraiseLog(map);
							result.put("message", "点赞成功！");
						} else {
							travelmapper.deletepraiseById(map);
							result.put("message", "点赞取消成功！");
						}
						result.put("code", "0001");
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("点赞操作失败：" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}

	}

	/*
	 * 游乐卡 , @Context HttpServletRequest request, @Context HttpServletResponse
	 * re
	 */

	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getCardList")
	public String getCardList(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		JSONObject data = new JSONObject();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
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
						// 游乐卡列表
						List<UserCard> list = cardmapper.getuserCard(map);
						if (list != null && !list.isEmpty()) {
							// 查詢游乐卡可供支持使用的景区
							for (UserCard uc : list) {
								if (uc.getBaseCardId() != null) {
									map.clear();
									map.put("baseCardId", uc.getBaseCardId());
									List<ScenicName> listname = cardmapper.getScenicName(map);
									if (listname != null && !listname.isEmpty()) {
										JSONArray js = new JSONArray();
										js = JSONArray.fromObject(listname);

										uc.setScenicNamelist(js.toString());
									} else {
										uc.setScenicNamelist("");
									}
								} else {
									uc.setScenicNamelist("");
								}
							}
							data.put("list", list);
						} else {
							data.put("list", "");
						}
						// 查看账户余额
						map.clear();
						map.put("userId", userinfo.getId());
						UserAmount us = usermapper.getuserAmount(map);
						if (us != null) {
							data.put("balance", us.getBalance());
						} else {
							data.put("balance", "");
						}
						result.put("data", data);
						result.put("code", "0001");
						result.put("message", "查询游乐卡列表成功！");
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("查询游乐卡列表失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 游乐卡绑定和解绑type1:绑定；2：解绑;3:支付开关的开启与关闭
	 * 操作记录的status状态说明：1：开启支付开关的操作；2：关闭支付开关的操作；3：绑定操作；4：解绑操作
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("operateCard")
	public String operateCard(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		// String uuid = jsonobject.optString("uuid");
		Integer id = jsonobject.optInt("id");
		Integer status = jsonobject.optInt("status");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && type != null && !type.isEmpty() && id != null && id > 0) {
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
					} else {// 绑定
						//
						// result.put("code", "0100");
						// result.put("message", "用户绑定功能暂未开放！");
						// return result.toString();
						// Calendar time = Calendar.getInstance();
						// String addTime = df.format(time.getTime());
						// // 查询游乐卡是否存在和是否是激活状态
						// map.clear();
						// map.put("cardNo", cardNo);
						// // map.put("status", 1);
						// List<ScenicCard> sc =
						// scenicMapper.getscenicCard(map);
						// if (sc != null && !sc.isEmpty()) {
						// if (sc.get(0).getStatus() == 2) {
						// result.put("code", "0009");
						// result.put("message", "该游乐卡尚未激活！");
						// return result.toString();
						// }
						// if (sc.get(0).getScenicId() == 0) {
						// result.put("code", "0010");
						// result.put("message", "该游乐卡不可用！");
						// return result.toString();
						// }
						// }
						// 查询游乐卡是否已被绑定存在
						map.clear();
						map.put("id", id);
						// map.put("cardNo", cardNo);
						map.put("userId", userinfo.getId());
						List<UserCard> uc = cardmapper.getuserCard(map);
						// if (sc != null && !sc.isEmpty() && sc.size() == 1) {
						// if (type.equals("1")) {
						// map.clear();
						// map.put("userId", userinfo.getId());
						// // 查询绑定的游乐卡个数
						// List<UserCard> list = cardmapper.getuserCard(map);
						// if (list != null && !list.isEmpty() && list.size() >=
						// 4) {
						// result.put("code", "0005");
						// result.put("message", "用户绑定的游乐卡已达个人所属上限！");
						// return result.toString();
						// }
						// if (uc != null && !uc.isEmpty() && uc.size() == 1) {
						// result.put("code", "0006");
						// result.put("message", "该游乐卡已被绑定！");
						// return result.toString();
						// } else {
						// String serialNo = CodeUtils.getylCardCode(
						// userinfo.getUserCode().substring(userinfo.getUserCode().length()
						// - 6));
						// map.clear();
						// map.put("addTime", addTime);
						// map.put("serialNo", serialNo);
						// map.put("cardNo", cardNo);
						// // map.put("cardNo", cardNo);
						// map.put("userId", userinfo.getId());
						// map.put("status", 1);
						// usermapper.saveBindCard(map);
						// // 绑定记录
						// map.put("status", 3);
						// // usermapper.saveBindCardLog(map);
						// }
						// } else if (type.equals("2")) {
						// if (uc != null && !uc.isEmpty() && uc.size() == 1) {
						// map.clear();
						// map.put("addTime", addTime);
						// map.put("serialNo", CodeUtils.getylCardCode(
						// userinfo.getUserCode().substring(userinfo.getUserCode().length()
						// - 6)));
						// map.put("cardNo", cardNo);
						// // map.put("cardNo", cardNo);
						// map.put("userId", userinfo.getId());
						// map.put("status", 2);
						// usermapper.deleteCard(map);
						// // 冻结景区游乐卡账号
						// usermapper.updateScenicCard(map);
						// // 解绑记录
						// map.put("status", 4);
						// // usermapper.saveBindCardLog(map);
						//
						// } else {
						// result.put("code", "0007");
						// result.put("message", "该游乐卡用户尚未绑定，无法解绑！");
						// return result.toString();
						// }
						// } else {
						if (uc != null && !uc.isEmpty() && uc.size() == 1) {
							map.clear();
							// map.put("cardAuthCode", cardAuthCode);
							map.put("id", id);
							map.put("userId", userinfo.getId());
							map.put("status", status);
							// 支付或开关开启记录
							usermapper.updateUserCard(map);
							// 操作记录
							// map.clear();
							// map.put("cardAuthCode", cardAuthCode);
							// map.put("cardNo", cardNo);
							// map.put("serialNo", CodeUtils.getylCardCode(
							// userinfo.getUserCode().substring(userinfo.getUserCode().length()
							// - 6)));
							// map.put("status", status);
							// map.put("addTime", addTime);
							// map.put("userId", userinfo.getId());
							// usermapper.saveBindCardLog(map);
						} else {
							result.put("code", "0007");
							result.put("message", "该游乐卡用户尚未绑定，无法操作！");
							return result.toString();
						}
						// }
						result.put("code", "0001");
						result.put("message", "操作成功！");
						// } else {
						// result.put("code", "0008");
						// result.put("message", "游乐卡编号或验证码有误！");
						// return result.toString();
						// }
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("操作失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 获取地址列表
	 */
	// @Transactional
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getAddress")
	public String getAddress(String json) {
		JSONObject result = new JSONObject();
		try {
			String jsonaddress = usermapper.getAddress();
			result.put("data", jsonaddress);
			result.put("code", "0001");
			result.put("message", "查询地址成功！");
		} catch (Exception e) {
			log.error("操作失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			// TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		return result.toString();
	}

	/*
	 * 用户地址列表
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getuserAddress")
	public String getuserAddress(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
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
						List<UserAddress> userAddressList = usermapper.getuserAddress(map);
						if (userAddressList != null && !userAddressList.isEmpty()) {
							result.put("userAddressList", userAddressList);
						} else {
							result.put("userAddressList", "");
						}
						result.put("code", "0001");
						result.put("message", "查询地址列表成功！");
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("查询地址列表失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
		}
		return result.toString();
	}

	/*
	 * 编辑地址type 1:添加；2：修改；3：删除
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("operateuserAddress")
	public String operateuserAddress(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		JSONObject userAddress = jsonobject.getJSONObject("userAddress");
		Map<String, Object> map = new HashMap<String, Object>();
		boolean flag = false;
		try {
			if (uuID != null && !uuID.isEmpty() && type != null && !type.isEmpty() && userAddress != null
					&& !userAddress.isEmpty()) {
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
						UserAddress ua = (UserAddress) JSONObject.toBean(userAddress, UserAddress.class);
						// 删除
						if (type.equals("3")) {
							if (ua.getId() != null && ua.getId() > 0) {
								map.clear();
								map.put("id", ua.getId());
								map.put("userId", userinfo.getId());
								UserAddress ua1 = new UserAddress();
								ua1.setId(ua.getId());
								ua1.setUserId(userinfo.getId());
								ua1.setIsDel(1);
								usermapper.updateuserAddress(ua1);
								// usermapper.deleteuserAddress(map);
							} else {
								flag = true;
							}
						} else if (type.equals("2")) {
							if (ua.getId() != null && ua.getId() > 0) {
								if (ua.getIsDefault() == 1) {
									map.clear();
									map.put("userId", userinfo.getId());
									map.put("isDefault", 1);
									List<UserAddress> userAddressList = usermapper.getuserAddress(map);
									if (userAddressList != null && !userAddressList.isEmpty()) {
										userAddressList.get(0).setIsDefault(0);
										usermapper.updateuserAddress(userAddressList.get(0));
									}
								} else {
									map.clear();
									map.put("userId", userinfo.getId());
									map.put("addressId", ua.getId());
									map.put("isDefault", 1);
									List<UserAddress> userAddressList = usermapper.getuserAddress(map);
									if (userAddressList != null && !userAddressList.isEmpty()) {
										result.put("code", "0005");
										result.put("message", "当前不可取消默认地址！");
										return result.toString();
									}
								}
								ua.setUserId(userinfo.getId());
								usermapper.updateuserAddress(ua);
							} else {
								flag = true;
							}
						} else {
							if (ua.getAddressInfo() != null && !ua.getAddressInfo().isEmpty() && ua.getZip() != null
									&& !ua.getZip().isEmpty() && ua.getRecipient() != null
									&& !ua.getRecipient().isEmpty() && ua.getRecipientMobile() != null
									&& !ua.getRecipientMobile().isEmpty() && ua.getProvinceId() != null
									&& ua.getProvinceId() > 0 && ua.getCityId() != null && ua.getCityId() > 0
									&& ua.getDistrictId() != null && ua.getDistrictId() > 0) {
								// 首次添加地址设为默认
								map.clear();
								map.put("userId", userinfo.getId());
								List<UserAddress> userAddressList = usermapper.getuserAddress(map);
								if (userAddressList != null && !userAddressList.isEmpty()) {
								} else {
									ua.setIsDefault(1);
								}
								Calendar time = Calendar.getInstance();
								String addTime = df.format(time.getTime());
								ua.setUserId(userinfo.getId());
								ua.setAddTime(addTime);
								usermapper.saveUserAddress(ua);
							} else {
								flag = true;
							}
						}
						if (flag) {
							result.put("code", "0002");
							result.put("message", "参数不全！");
						} else {
							result.put("code", "0001");
							result.put("message", "操作成功！");
						}
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("编辑地址失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}

	}

	/*
	 * 支付密码开启与修改 type 1:开启或关闭支付密码功能和修改最低免支付数值；2：修改支付密码
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("operateuserAmount")
	public String operateuserAmount(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		String payPwd = jsonobject.optString("payPwd");
		Integer isOpen = jsonobject.optInt("isOpen");
		String minAmount = jsonobject.optString("minAmount");
		String smsCode = jsonobject.optString("smsCode");
		Map<String, Object> map = new HashMap<String, Object>();
		boolean flag = false;
		try {
			if (uuID != null && !uuID.isEmpty() && type != null && !type.isEmpty()) {
				map.put("uuID", uuID);
				Userinfo userinfo = usermapper.Getuserinfo(map);
				result = cardService.checkUser(userinfo);
				if (result.optBoolean("flag")) {
					// if (userinfo != null) {
					Date nowdate = CommonDateParseUtil.date2date(new Date());
					Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
					Date smsCodeExpiry = CommonDateParseUtil.string2date(userinfo.getSmsPwdExpiry());
					if (nowdate.getTime() >= uuIDExpiry.getTime()) {
						result.put("code", "0004");
						result.put("message", "用户登录已过期，请重新登录！");
						return result.toString();
					} else {
						// 开启或关闭，以及修改最小免支付金额
						if (type.equals("1")) {
							if (isOpen != null && minAmount != null && !minAmount.isEmpty()) {
								if (userinfo.getPayPwd() == null || userinfo.getPayPwd().isEmpty()
										|| userinfo.getPayPwd() == "") {
									result.put("code", "0010");
									result.put("message", "支付密码不能为空，无法操作！");
									return result.toString();
								}
								map.clear();
								BigDecimal bd = new BigDecimal(minAmount);
								// 设置小数位数，第一个变量是小数位数，第二个变量是取舍方法(四舍五入)
								bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
								map.put("isOpen", isOpen);
								map.put("userId", userinfo.getId());
								map.put("minAmount", bd);
								map.put("status", 1);
								usermapper.updateUserAmount(map);
							} else {
								flag = true;
							}
						} else if (type.equals("2")) {
							if (payPwd != null && !payPwd.isEmpty() && smsCode != null && !smsCode.isEmpty()) {
								if (smsCodeExpiry.getTime() >= nowdate.getTime()) {
									if (smsCode.equals(userinfo.getSmsCode())) {
										String md5payPwd = MD5Utils.string2MD5(payPwd);
										map.clear();
										map.put("payPwd", md5payPwd);
										map.put("userId", userinfo.getId());
										if (userinfo.getPayPwd() == null || userinfo.getPayPwd().isEmpty()
												|| userinfo.getPayPwd() == "") {
											map.put("minAmount", new BigDecimal("50"));
										}
										map.put("isOpen", 1);
										map.put("status", 1);
										usermapper.updateUserAmount(map);
									} else {
										result.put("code", "0006");
										result.put("message", "短信验证码不正确！");
										return result.toString();
									}
								} else {
									result.put("code", "0005");
									result.put("message", "短信验证码失效,请重新请求！");
									return result.toString();
								}
							} else {
								flag = true;
							}
						}
						if (flag) {
							result.put("code", "0002");
							result.put("message", "参数不全！");
						} else {
							result.put("code", "0001");
							result.put("message", "操作成功！");
						}
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("支付密码修改失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 获取用户反馈类型
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getfeedbackType")
	public String getfeedbackType(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
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
						List<FeedbackType> fbList = usermapper.getfeedbackList();
						if (fbList != null && !fbList.isEmpty()) {
							result.put("data", fbList);
						} else {
							result.put("data", "");
						}
						result.put("code", "0001");
						result.put("message", "查询反馈类型列表成功！");
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("查询反馈类型失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
		}
		return result.toString();
	}

	/*
	 * 提交用户反馈信息
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("operateFeedback")
	public String operateFeedback(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String feedbackContent = jsonobject.optString("feedbackContent");
		Integer codeType = jsonobject.optInt("codeType");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && feedbackContent != null && !feedbackContent.isEmpty()) {
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
						Calendar time = Calendar.getInstance();
						String addTime = df.format(time.getTime());
						map.clear();
						map.put("userId", userinfo.getId());
						map.put("codeType", codeType);
						map.put("feedbackContent", feedbackContent);
						map.put("addTime", addTime);
						usermapper.savefeedback(map);
						result.put("code", "0001");
						result.put("message", "消息反馈成功！");
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("消息反馈失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 获取用户搜索历史记录
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getsearchlog")
	public String getsearchlog(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
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
						List<Map> slList = usermapper.getsearchlog(map);
						if (slList != null && !slList.isEmpty()) {
							result.put("data", slList);
						} else {
							result.put("data", "");
						}
						result.put("code", "0001");
						result.put("message", "查询成功！");
					}
				} else {
					return result.toString();
				}
			} else {
				result.put("slList", "");
				result.put("code", "0001");
				result.put("message", "暂无数据！");
			}
		} catch (Exception e) {
			log.error("查询搜索历史记录失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
		}
		return result.toString();
	}

	/*
	 * 获取用户搜索信息
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getsearch")
	public String getsearch(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String keyword = jsonobject.optString("keyword");
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject data = new JSONObject();
		try {
			if (keyword != null && !keyword.isEmpty()) {
				// 登录状态
				if (uuID != null && !uuID.isEmpty()) {
					map.clear();
					map.put("uuID", uuID);
					Userinfo userinfo = usermapper.Getuserinfo(map);
					result = cardService.checkUser(userinfo);
					if (result.optBoolean("flag")) {
						Date nowdate = CommonDateParseUtil.date2date(new Date());
						Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
						if (nowdate.getTime() >= uuIDExpiry.getTime()) {
							result.put("code", "0004");
							result.put("message", "用户登录已过期，请重新登录！");
							return result.toString();
						} else {// 添加搜索历史记录
							Calendar time = Calendar.getInstance();
							String addTime = df.format(time.getTime());
							map.clear();
							map.put("userId", userinfo.getId());
							map.put("keyword", keyword);
							map.put("userType", 1);
							map.put("isDel", 0);
							map.put("addTime", addTime);
							usermapper.savesearchlog(map);
						}
					} else {
						return result.toString();
					}
				}
				map.clear();
				// 搜索景区
				map.put("scenicName", keyword);
				List<ScenicRcmd> scenicList = scenicMapper.getscenicList(map);
				map.clear();
				// 搜索特产
				map.put("goodName", keyword);
				map.put("start", 0);
				map.put("num", 5);
				List<Speciality> specialityList = shopmapper.getSpecialityList(map);
				map.clear();
				// 搜索游记
				map.put("title", keyword);
				map.put("start", 0);
				map.put("num", 5);
				map.put("state", 1);
				map.put("isUnderway", 1);
				map.put("openLevel", 0);
				List<TravelNotes> travelNotesList = scenicMapper.gettravelNotesList(map);
				if (specialityList != null && !specialityList.isEmpty()) {
					for (Speciality sp : specialityList) {
						sp.setImgUrl(shopImgUrl + sp.getFileCode() + "/Speciality/" + sp.getFileCode() + "/"
								+ sp.getImgUrl());
					}
					data.put("specialityList", specialityList);
				} else {
					data.put("specialityList", "");
				}
				if (scenicList != null && !scenicList.isEmpty()) {
					for (ScenicRcmd sc : scenicList) {
						if (sc.getCoverImg() != null && sc.getCoverImg() != "") {
							sc.setCoverImg(scenicImgUrl + sc.getFileCode() + "/" + sc.getCoverImg());
						}
					}
					data.put("scenicList", scenicList);
				} else {
					data.put("scenicList", "");
				}
				if (travelNotesList != null && !travelNotesList.isEmpty()) {
					for (TravelNotes travelNotes : travelNotesList) {
						if (travelNotes.getCoverImg() != null && travelNotes.getCoverImg() != "") {
							travelNotes.setCoverImg(
									travelImgUrl + travelNotes.getFileCode() + "/" + travelNotes.getCoverImg());
						}
						if (travelNotes.getHeadImg() != null && !travelNotes.getHeadImg().isEmpty()) {
							travelNotes.setHeadImg(userImgUrl + travelNotes.getHeadImg());
						}
					}
					data.put("travelNotesList", travelNotesList);
				} else {
					data.put("travelNotesList", "");
				}
				result.put("data", data);
				result.put("code", "0001");
				result.put("message", "查询成功！");
				return result.toString();
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("查询搜索历史记录失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
		}
		return result.toString();
	}

	/*
	 * 清除用户搜索历史记录
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("clearsearchlog")
	public String clearsearchlog(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
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
						usermapper.updatesearchlog(map);
						result.put("code", "0001");
						result.put("message", "操作成功！");
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("查询搜索历史记录失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

	/*
	 * 提现账户列表
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getUserPayAccount")
	public String getUserPayAccount(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		JSONObject data = new JSONObject();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
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
						// map.put("userId", userinfo.getId());
						map.put("operateId", userinfo.getId());
						map.put("operateType", 1);
						map.put("isDel", 0);
						List<UserPayAccount> list = usermapper.getUserPayAccount(map);
						if (list != null && !list.isEmpty()) {
							for (UserPayAccount upa : list) {
								upa.setAccountName(CodeUtils.getAccountName(upa.getAccountName()));
							}
							result.put("data", list);
						} else {
							result.put("data", "");
						}
						// result.put("data", data);
						result.put("code", "0001");
						result.put("message", "查询提现账户列表成功！");
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("查询提现账户列表失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
		}

		return result.toString();
	}

	/*
	 * 添加提现账户 type 1：添加 ；2：删除
	 */
	@SuppressWarnings("finally")
	@Transactional
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("operateUserPayAccount")
	public String operateUserPayAccount(String json) {
		JSONObject result = new JSONObject();
		JSONObject jsonobject = new JSONObject();
		jsonobject = JSONObject.fromObject(json);
		String uuID = jsonobject.optString("uuID");
		String type = jsonobject.optString("type");
		String accountName = jsonobject.optString("accountName");
		Integer typeId = jsonobject.optInt("typeId");
		Integer accountId = jsonobject.optInt("accountId");
		Boolean flag = false;
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (uuID != null && !uuID.isEmpty() && type != null && !type.isEmpty()) {
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
						if (type.equals("1")) {// 添加新账号
							if (accountName != null && !accountName.isEmpty() && typeId != null && typeId > 0) {
								if (accountName.length() < 6 || accountName.length() > 30) {
									result.put("code", "0008");
									result.put("message", "账号异常，请重新输入！");
									return result.toString();
								}
								map.clear();
								// map.put("userId", userinfo.getId());
								map.put("operateId", userinfo.getId());
								map.put("operateType", 1);
								map.put("accountName", accountName);
								map.put("typeId", typeId);
								map.put("isDel", 0);
								List<UserPayAccount> list = usermapper.getUserPayAccount(map);
								if (list != null && !list.isEmpty()) {
									result.put("code", "0006");
									result.put("message", "该账户已存在，不可重复添加！");
									return result.toString();
								} else {
									boolean f = false;
									map.clear();
									// map.put("userId", userinfo.getId());
									map.put("operateId", userinfo.getId());
									map.put("operateType", 1);
									map.put("isDel", 0);
									List<UserPayAccount> list1 = usermapper.getUserPayAccount(map);
									if (list1 != null && !list1.isEmpty()) {
										if (list1.size() >= 4) {
											f = true;
										}
									}
									if (f) {
										result.put("code", "0007");
										result.put("message", "提现账号个数已达上限，不可添加！");
										return result.toString();
									} else {
										map.clear();
										map.put("operateId", userinfo.getId());
										map.put("operateType", 1);
										map.put("accountName", accountName);
										map.put("typeId", typeId);
										map.put("isDel", 0);
										map.put("status", 1);
										usermapper.saveUserPayAccount(map);
									}
								}
							} else {
								flag = true;
							}
						} else if (type.equals("2")) {
							if (accountId != null && accountId > 0) {// 删除已有账号（该账号目前没有提现处理中）
								map.clear();
								// map.put("userId", userinfo.getId());
								map.put("operateId", userinfo.getId());
								map.put("operateType", 1);
								map.put("accountId", accountId);
								map.put("isDel", 0);
								List<UserPayAccount> list = usermapper.getUserPayAccount(map);
								if (list != null && !list.isEmpty()) {
									map.clear();
									map.put("operateId", userinfo.getId());
									map.put("operateType", 1);
									map.put("accountId", accountId);
									map.put("status", 1);
									List<PresentApplication> paList = usermapper.getPresentApplication(map);
									if (paList != null && !paList.isEmpty() && paList.get(0).getStatus() == 1) {
										result.put("code", "0009");
										result.put("message", "该账号正在提现中，尚未结束，不可删除！");
										return result.toString();
									} else {
										map.clear();
										// map.put("userId", userinfo.getId());
										map.put("operateId", userinfo.getId());
										map.put("operateType", 1);
										map.put("id", accountId);
										map.put("isDel", 1);
										usermapper.updateUserPayAccount(map);
									}
								} else {
									result.put("code", "0008");
									result.put("message", "该提现账号不存在，不可删除！");
									return result.toString();
								}
							} else {
								flag = true;
							}
						}
						if (flag) {
							result.put("code", "0002");
							result.put("message", "参数不全！");
						} else {
							result.put("code", "0001");
							result.put("message", "操作成功！");
						}
					}
				} else {
					// result.put("code", "0003");
					// result.put("message", "暂无用户信息！");
					return result.toString();
				}
			} else {
				result.put("code", "0002");
				result.put("message", "参数不全！");
			}
		} catch (Exception e) {
			log.error("提现账户操作失败" ,e);
			result.put("code", "0000");
			result.put("message", "平台繁忙，请稍候再试！");
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		} finally {
			return result.toString();
		}
	}

}
