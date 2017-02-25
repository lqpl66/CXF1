<%@page language="java" contentType="text/html;charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	String file = "files";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>游记名称</title>
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0">
	<script src="<%=basePath%>assets/third/jquery/jquery-2.1.3.js"></script>
	<script src="<%=basePath%>assets/jquery.lazyload.js"></script>
	<link href="<%=basePath%>CSS/bootstrap.min.css" rel="stylesheet" />
	<link href="<%=basePath%>CSS/default.css" rel="stylesheet" />
	<link href="<%=basePath%>CSS/main.css" rel="stylesheet" />
	<link href="<%=basePath%>CSS/viewer.css" rel="stylesheet" />
	<link href="<%=basePath%>CSS/normalize.css" rel="stylesheet" />
	<script  type="text/javascript" src="<%=basePath%>/js/bootstrap.min.js"></script>
	<script  type="text/javascript" src="<%=basePath%>/js/main.js"></script>
	<script type="text/javascript" src="<%=basePath%>/js/viewer.min.js"></script>
	
<%-- 	<script type="text/javascript" src="<%=basePath%>/assets/third/jquery/jquery-2.1.3.min.js"></script> --%>
<%-- 	<script type="text/javascript" src="<%=basePath%>/assets/third/jquery/jquery.mobile.custom.min.js"></script> --%>
<%-- 	<script type="text/javascript" src="<%=basePath%>/assets/third/iscroll/iscroll-probe.js"></script> --%>
<%-- 	<script type="text/javascript" src="<%=basePath%>/assets/agile/js/agile.js"></script> --%>
<%-- 	<script type="text/javascript" src="<%=basePath%>/assets/bridge/exmobi.js"></script> --%>
<%-- 	<script type="text/javascript" src="<%=basePath%>/assets/bridge/agile.exmobi.js"></script> --%>
<%-- 	<script type="text/javascript" src="<%=basePath%>/assets/app/js/app.seedsui.js"></script> --%>
	<script>
        $(function () {
            var browser = {
                versions: function () {
                    var u = navigator.userAgent, app = navigator.appVersion;
                    return {
                        trident: u.indexOf('Trident') > -1, //IE内核
                        presto: u.indexOf('Presto') > -1, //opera内核
                        webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
                        gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1,//火狐内核
                        mobile: !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端
                        ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
                        android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
                        iPhone: u.indexOf('iPhone') > -1, //是否为iPhone或者QQHD浏览器
                        iPad: u.indexOf('iPad') > -1, //是否iPad
                        webApp: u.indexOf('Safari') == -1, //是否web应该程序，没有头部与底部
                        weixin: u.indexOf('MicroMessenger') > -1, //是否微信 （2015-01-22新增）
                        qq: u.match(/\sQQ/i) == " qq" //是否QQ
                    };
                }(),
                language: (navigator.browserLanguage || navigator.language).toLowerCase()
            }
            if (browser.versions.mobile || browser.versions.android || browser.versions.ios)
            {
                jQuery("head").append("<link>");
                a = jQuery("head").children(":last");
                a.attr({
                    rel: "stylesheet",
                    type: "text/css",
                    href: "<%=basePath%>CSS/travelnotes.mobile.css"
                });
            } else {
                jQuery("head").append("<link>");
                a = jQuery("head").children(":last");
                a.attr({
                    rel: "stylesheet",
                    type: "text/css",
                    href: "<%=basePath%>CSS/travelnotes.pc.css"
				});
			}
		})
	</script>
</head>
<body>
<c:choose>
<c:when test="${code=='0001'}">
<div class="conten-main">
		<div class="trave-content">
			<div class="cover-content">
				<img src="${travelNotes.coverImg}" />
				<div class="cover-bottom">
					<div class="header-img">
						<img src="${travelNotes.headImg}" />
					</div>
					<div class="cover-bottom-right">
						<div class="middle-name">
							<span>${travelNotes.nickName}</span>
							<c:if test="${travelNotes.leavel==1}">
								<img src="<%=basePath%>Image/icon/V1.png" />
							</c:if>
							<c:if test="${travelNotes.leavel==2}">
								<img src="<%=basePath%>Image/icon/V2.png" />
							</c:if>
							<c:if test="${travelNotes.leavel==3}">
								<img src="<%=basePath%>Image/icon/V3.png" />
							</c:if>
							<c:if test="${travelNotes.leavel==4}">
								<img src="<%=basePath%>Image/icon/V4.png" />
							</c:if>
							<c:if test="${travelNotes.leavel==5}">
								<img src="<%=basePath%>Image/icon/V5.png" />
							</c:if>
						</div>
						<div class="middle-bottom">
							<ul>
								<li><img src="<%=basePath%>Image/icon/Icon-address.png" />
									<span>${travelNotes.provinceName}·${travelNotes.cityName}</span>
								</li>
								<li class="floatright"><img class="icon-img"
									src="<%=basePath%>Image/icon/Icon-bowser.png" /> <span>${travelNotes.totalPraiseNum}</span>
								</li>
								<li class="floatright"><img class="icon-img"
									src="<%=basePath%>Image/icon/icon-like.png" /> <span>${travelNotes.browseCount}</span>
								</li>
							</ul>
						</div>
					</div>
				</div>
			</div>
			<div class="trave-info">
				<ul class="docs-pictures">
					<c:forEach items="${travelNotesInfoList}" var="travelNotesInfo"
						varStatus="status" begin="0" step="1">
						<c:choose>
							<c:when test="${status.index==0 }">
								<c:forEach items="${travelNotesInfo}" var="list"
									varStatus="status1">
									<c:choose>
										<c:when test="${status1.index==0 }">
											<li><div style="height: 30px"></div></li>
											<li><a class="icon-trave-day"></a><span>${list}</span></li>
										</c:when>
										<c:otherwise>
											<li><div class="icon-border-left"></div></li>
											<li>
											<c:if test="${list.imgUrl!=''}">
												<img class="trave-info-img" max-original="${list.maximgUrl}"
												data-original="${list.imgUrl}"
												src="<%=basePath%>Image/默认背景图@2x.png" />
											</c:if>
										
												<div class="trave-info-bottom">
													<span class="trave-note">${list.describeInfo}</span>
													<table style="width: 100%; color: #B6B6B6">
														<tr>
														<c:if test="${list.addTime!=''}">
														<td class="width1"><a class="icon-time"></a></td>
															<td><span>${list.addTime}</span></td>
														</c:if>
													<c:if test="${list.placeName!=''}">
												        	<td class="width1"><a class="icon-address"></a></td>
															<td style="min-width: 100px;"><span>${list.placeName}</span></td>
														</c:if>
														</tr>
													</table>
												</div></li>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<c:forEach items="${travelNotesInfo}" var="list"
									varStatus="status3">
									<c:choose>
										<c:when test="${status3.index==0 }">
											<li><div class="icon-border-left"></div></li>
											<li><a class="icon-trave-day"></a><span>${list}</span></li>
										</c:when>
										<c:otherwise>
											<li><div class="icon-border-left"></div></li>
											<li>
										<c:if test="${list.imgUrl!=''}">
												<img class="trave-info-img" max-original="${list.maximgUrl}"
												data-original="${list.imgUrl}"
												src="<%=basePath%>Image/默认背景图@2x.png" />
											</c:if>
												<div class="trave-info-bottom">
													<span class="trave-note">${list.describeInfo}</span>
													<table style="width: 100%; color: #B6B6B6">
														<tr>
																	<c:if test="${list.addTime!=''}">
														<td class="width1"><a class="icon-time"></a></td>
															<td><span>${list.addTime}</span></td>
														</c:if>
													<c:if test="${list.placeName!=''}">
												        	<td class="width1"><a class="icon-address"></a></td>
															<td style="min-width: 100px;"><span>${list.placeName}</span></td>
														</c:if>	</tr>
													</table>
												</div></li>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</ul>
			</div>
		</div>
	</div>
</c:when>
<c:otherwise>
${message}
</c:otherwise>
</c:choose>
<!-- 	<article data-role="article" id="slider_article" > -->
	<div class="footer">
		<ul>
			<li class="middle-line footer-left download">下载游乐APP</li>
			<li class="footer-right download">在游乐中查看</li>
		</ul>
	</div>
<!-- 	</article> -->
	<script>
		$(function() {
			$("img").lazyload({
				effect : "fadeIn"
			});
			$(".download").on("touchstart",function(){
				var ua = navigator.userAgent.toLowerCase();	
				if (/iphone|ipad|ipod/.test(ua)) {
			        ios();
				} else if (/android/.test(ua)) {
					alert("暂不支持安卓版本下载！");
				}
			});
		    function ios(){
		    	window.location.href = "uto168Alone://";
		        window.setTimeout(function(){
		           window.location.href = "https://itunes.apple.com/cn/app/uto168.com.Apps"; /***下载app的地址***/
		        },1000)
		      };
		});
	</script>
</body>
</html>
