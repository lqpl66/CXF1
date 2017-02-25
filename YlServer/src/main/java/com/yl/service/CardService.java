package com.yl.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yl.Utils.CommonDateParseUtil;
import com.yl.beans.AttractsLine;
import com.yl.beans.CardExpand;
import com.yl.beans.LineLog;
import com.yl.beans.LinePrice;
import com.yl.beans.ScenicCardType;
import com.yl.beans.UserCard;
import com.yl.beans.Userinfo;
import com.yl.mapper.CardMapper;
import com.yl.mapper.ExpenseMapper;
import com.yl.mapper.ShopMapper;
import com.yl.mapper.UserMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class CardService {
	@Autowired
	private UserMapper usermapper;
	@Autowired
	private ShopMapper shopmapper;
	@Autowired
	private ExpenseMapper expenseMapper;
	@Autowired
	private CardMapper cardMapper;

	@Autowired
	private MessageService messageService;
	@Autowired
	private static Logger log = Logger.getLogger(CardService.class);

	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat df3 = new SimpleDateFormat("yyyy-MM-dd");
	DateFormat df1 = new SimpleDateFormat("HH:mm:ss");

	/*
	 * 确认订单是否可以生成
	 */
	public JSONObject checkOrder(JSONArray list, Integer scenicId, String date, Integer userId) {
		JSONObject result = new JSONObject();
		Map<String, Object> map = new HashMap<String, Object>();
		if (list != null && !list.isEmpty() && scenicId != null && date != null && !date.isEmpty() && userId != null) {
			try {
				Integer userCardNum = 0;
				Integer expandNum = 0;
				Integer userLimitNum = 4;
				Integer totalNum = 0;
				BigDecimal totalPrice = new BigDecimal("0.00");
				// 查出当前景区支持的腕带类型
				map.clear();
				map.put("scenicId", scenicId);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(df3.parse(date));
				Date c_now = df3.parse(date);
				String nowtime = df3.format(new Date());
				calendar.setTime(df3.parse(nowtime));
				Date c_Limit_down = calendar.getTime();
				calendar.add(Calendar.DATE, 15);
				Date c_Limit_up = calendar.getTime();
				if (c_now.compareTo(c_Limit_down) != -1 && c_now.compareTo(c_Limit_up) != 1) {
					map.put("date", date);
				} else {
					result.put("code", "0005");
					result.put("flag", false);
					result.put("message", "选择购买时间不符！");
					return result;
				}

				for (int i = 0; i < list.size(); i++) {
					Integer num = list.optJSONObject(i).optInt("num");
					totalNum = totalNum + num;
					Integer cardType = list.optJSONObject(i).optInt("cardType");
					map.put("cardType", cardType);
					List<ScenicCardType> typeList = cardMapper.getScenicCardType(map);
					if (typeList != null && !typeList.isEmpty()) {
						// 查出当前景区当前腕带类型还可以使用的数量
						for (ScenicCardType sct : typeList) {
							map.clear();
							map.put("scenicId", scenicId);
							map.put("type", "1");// 未使用的景区腕带
							map.put("cardType", cardType);
							List<Map> list1 = cardMapper.getCardNum(map);
							// 查询所用用户购买日期相同的未使用的腕带个数
							map.clear();
							map.put("scenicId", scenicId);
							map.put("appointment", date);
							map.put("status", 1);
							map.put("cardType", sct.getCardType());
							List<CardExpand> cardExpandList1 = cardMapper.getCardExpand(map);
							Integer cardExpandAllNum = 0;
							if (cardExpandList1 != null && !cardExpandList1.isEmpty()) {
								for (CardExpand ce : cardExpandList1) {
									cardExpandAllNum = cardExpandAllNum + ce.getCardNum() - ce.getCardUseNum();
								}
							}
							if (list1 != null && !list1.isEmpty()) {
								if (list1.size() - cardExpandAllNum < num) {
									result.put("flag", false);
									result.put("code", "0010");
									result.put("message", "剩余腕带不足！");
									return result;
								}
							}
						}

						// 当前用户绑定的腕带个数
						if (c_now.compareTo(c_Limit_down) == 0) {
							map.clear();
							map.put("scenicId", scenicId);
							map.put("userId", userId);// 未使用的景区腕带
							List<Map> list1 = cardMapper.getCardNum(map);
							if (list1 != null && !list1.isEmpty()) {
								userCardNum = list1.size();
							}
						}
						typeList.get(0).setTotalPrice(typeList.get(0).getPrice().add(typeList.get(0).getUsePrice()));
						totalPrice = totalPrice.add(typeList.get(0).getTotalPrice().multiply(new BigDecimal(num)));
					} else {
						result.put("flag", false);
						result.put("code", "0010");
						result.put("message", "暂无腕带！");
						return result;
					}
				}
				// 查询购买日期相同
				map.clear();
				map.put("scenicId", scenicId);
				map.put("appointment", date);
				map.put("status", 1);
				List<CardExpand> cardExpandList = cardMapper.getCardExpand(map);
				if (cardExpandList != null && !cardExpandList.isEmpty()) {
					for (CardExpand ce : cardExpandList) {
						expandNum = expandNum + ce.getCardNum() - ce.getCardUseNum();
					}
				}
				userLimitNum = userLimitNum - userCardNum - expandNum;
				if (userLimitNum <= 0) {
					result.put("flag", false);
					result.put("code", "0010");
					result.put("message", "腕带购买已达上限，无法购买！");
				}
				if (userLimitNum < totalNum) {
					result.put("flag", false);
					result.put("code", "0010");
					result.put("message", "剩余腕带不足！");
				}
				result.put("flag", true);
				result.put("totalPrice", totalPrice);
			} catch (Exception e) {
				log.error("腕带订单校验失败", e);
				result.put("flag", false);
				result.put("code", "0000");
				result.put("message", "平台繁忙，请稍候再试！");
			}
		} else {
			result.put("flag", false);
			result.put("code", "0002");
			result.put("message", "参数不全！");
		}
		return result;
	}

	public void saveCardExpand(Integer cardNum, Integer cardType, Integer scenicId, String orderNo,
			String appointment) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cardNum", cardNum);
		map.put("cardUseNum", 0);
		map.put("cardType", cardType);
		map.put("orderNo", orderNo);
		map.put("status", 1);
		map.put("appointment", appointment);
		map.put("scenicId", scenicId);
		cardMapper.saveCardExpand(map);
	}

	public void operateCardExpand(CardExpand ce, Integer status) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.clear();
		map.put("orderNo", ce.getOrderNo());
		map.put("scenicId", ce.getScenicId());
		map.put("cardType", ce.getCardType());
		map.put("appointment", ce.getAppointment());
		map.put("status1", status);
		cardMapper.operateCardexpand(map);
	}

	public JSONObject checkUser(Userinfo userinfo) {
		JSONObject result = new JSONObject();
		if (userinfo != null) {
			Date nowdate = CommonDateParseUtil.date2date(new Date());
			Date uuIDExpiry = CommonDateParseUtil.string2date(userinfo.getUuIDExpiry());
			if (nowdate.getTime() >= uuIDExpiry.getTime()) {
				result.put("code", "0004");
				result.put("flag", false);
				result.put("message", "用户登录已过期，请重新登录！");
			} else if (userinfo.getUserStatus() == 2) {
				result.put("flag", false);
				result.put("code", "0006");
				result.put("message", "账号已被禁用，请联系客服！");
			} else {
				result.put("flag", true);
			}
		} else {
			result.put("flag", false);
			result.put("code", "0003");
			result.put("message", "暂无用户信息！");
		}
		return result;
	}

	/*
	 * 景点详情中的排队数据 type 1:未登录状态 2：登录状态 flag 1:vip免费 2：VIP付费和普通排队
	 */
	public JSONObject getLineList(String type, Userinfo userinfo, Integer attractsId) {
		JSONObject result = new JSONObject();
		Map<String, Object> map = new HashMap<String, Object>();
		Calendar time = Calendar.getInstance();
		String lineDate = df3.format(time.getTime());
		String nowTime = df.format(time.getTime());
		String nowTime1 = df1.format(time.getTime());
		//// 更改普通排队的尚未使用但已超时的排队记录
		map.clear();
		map.put("nowTime", nowTime);
		map.put("state", 1);
		map.put("state1", 3);
		cardMapper.updateLineLog(map);
		// 先查看是否有排队的
		map.clear();
		map.put("attractsId", attractsId);
		result.put("flag", true);
		List<AttractsLine> alineList = cardMapper.getattractsLine(map);
		if (alineList != null && !alineList.isEmpty()) {
			AttractsLine al = alineList.get(0);
			al.setLimitNum(0);
			// 查出该景点的所有的排队记录
			map.clear();
			map.put("state", 1);
			map.put("isLine", 0);
			map.put("attractsId", al.getAttractsId());
			List<LineLog> totallineLogList = cardMapper.getlineLog(map);
			Integer useTotalNum = 0;
			// 计算出该景点已使用记录的总数
			for (LineLog ll : totallineLogList) {
				map.clear();
				map.put("lineLogId", ll.getLineLogId());
				useTotalNum = useTotalNum + cardMapper.getlineUseLogNum(map);
			}
			// 计算出该景点未使用的总数
			Integer totalNum = cardMapper.getlineLogNum(map);
			if (totalNum == 0) {
				al.setTotalNum(0);
			} else {
				al.setTotalNum(totalNum - useTotalNum);
			}
			if (type.equals("1")) {// 未登录状态
				al.setMarkedWords("现在排队预计等待"
						+ (int) Math.ceil(((double) al.getTotalNum() * al.getAvgTime()) / ((double) 60)) + "分钟");
				result.put("lineLog", "");
				result.put("aline", al);
			} else {// 登录状态
					// 查询当前用户的绑定的可使用的腕带
				map.clear();
				map.put("userId", userinfo.getId());
				List<UserCard> ucList = cardMapper.getuserCard(map);
				Integer userCardNum = 0;
				if (ucList != null && !ucList.isEmpty()) {
					userCardNum = ucList.size();
				} 
//				else {
//					result.put("code", "0005");
//					result.put("message", "该用户没有绑定腕带，无法排队！");
//					result.put("flag", false);
//					return result;
//				}
				al.setLimitNum(userCardNum);
				// 获取当前用户该景点的未使用的排队记录
				map.clear();
				map.put("state", 1);
				map.put("userId", userinfo.getId());
				map.put("isLine", 0);
				map.put("attractsId", al.getAttractsId());
				List<LineLog> userlineLogList = cardMapper.getlineLog(map);
				if (userlineLogList != null && !userlineLogList.isEmpty()
						&& userlineLogList.get(0).getLineLogId() != null) {// 没有排队预约记录的就没有linelogId
					map.put("attractsId", userlineLogList.get(0).getAttractsId());
					// 查出当前用户该景点的已使用排队使用记录总数
					map.put("lineLogId", userlineLogList.get(0).getLineLogId());
					Integer useTotalNum1 = cardMapper.getlineUseLogNum(map);
					userlineLogList.get(0).setRemindNum(userlineLogList.get(0).getNumber() - useTotalNum1);
					userlineLogList.get(0).setAttractsId(attractsId);
					result.put("lineLog", userlineLogList.get(0));
					result.put("aline", "");
				} else {
					al.setMarkedWords("现在排队预计等待"
							+ (int) Math.ceil(((double) al.getTotalNum() * al.getAvgTime()) / ((double) 60)) + "分钟");
					result.put("lineLog", "");
					result.put("aline", al);
				}
			}
		} else {
			result.put("aline", "");
			result.put("lineLog", "");
		}
		// 更改VIP排队的尚未使用但已超时的排队记录
		map.clear();
		map.put("nowTime", nowTime);
		map.put("state", 1);
		map.put("state1", 3);
		map.put("typeId", 1);
		map.put("amount", 1);
		cardMapper.updateLineLog(map);
		// 查询该景点当天的所有VIP的时间段
		map.clear();
		map.put("lineDate", lineDate);
		map.put("attractsId", attractsId);
		List<LinePrice> AttractsLinePriceList = cardMapper.getlineTimeByAttractsId(map);
		if (AttractsLinePriceList != null && !AttractsLinePriceList.isEmpty()) {
			List<LineLog> UserlineLogList =new ArrayList<LineLog>();
              for(LinePrice lp:AttractsLinePriceList){
          		// 判断该时间段是否过期1：无法购买；2：可以购买
					Date dt1;
					try {
						dt1 = df.parse(lp.getLineDate() + " "+ lp.getEndTime() + ":00");
						Date dt2 = new Date();
						if (dt1.getTime() < dt2.getTime()) {
							lp.setIsBuy(1);
						} else {
							lp.setIsBuy(2);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
            	  lp.setLimitNum(4);
            	  //统计该景点的每个时间段的可购买数量，1：统计有效的购买数量，2：统计已使用的数量
  				// 获取该景点的所有的VIP排队购买记录
					map.put("typeId", 1);
					map.put("amount", 1);
					map.put("attractsId", attractsId);
					map.put("linepriceId", lp.getLinepriceId());
					map.put("isVIPLine", 0);
					Integer num2 = cardMapper.getlineLogNum(map);
					lp.setPollNumber(lp.getPollNumber() - num2);
					lp.setAttractsId(attractsId);
					//VIP预约记录的购买上限
					if(type.equals("2")){//登录状态
						// 获取当前用户的每个时间段的的VIP排队记录
						map.clear();
						map.put("userId", userinfo.getId());
						map.put("isVIPLine", 0);
						map.put("typeId", 1);
						map.put("amount", 1);
						map.put("attractsId", attractsId);
						map.put("linepriceId", lp.getLinepriceId());
						map.put("isVIPLine", 0);
						List<LineLog> lineLogList = cardMapper.getlineLog(map);
						Integer useTotalNum = 0;
//						Integer useNum = 0;
						if(lineLogList!=null&&!lineLogList.isEmpty()){
							for(LineLog ll :lineLogList){
								// 计算出景点每个时间段已使用记录的总数
								map.clear();
								map.put("lineLogId", ll.getLineLogId());
								useTotalNum = cardMapper.getlineUseLogNum(map);
//								 useTotalNum =useTotalNum+ll.getNumber();
								ll.setUsedNum(useTotalNum);
								UserlineLogList.add(ll);
									useTotalNum = useTotalNum +useTotalNum;
							}
							lp.setLimitNum(4-useTotalNum);
							
						}
					}
              }
              if(UserlineLogList.isEmpty()){
            	  result.put("VIPLineLogList", "");
              }else{
            	  result.put("VIPLineLogList", UserlineLogList);
              }
              result.put("VipLineList", AttractsLinePriceList);
		} else {
			result.put("VipLineList", "");
			result.put("VIPLineLogList", "");
		}
		return result;
	}

}
