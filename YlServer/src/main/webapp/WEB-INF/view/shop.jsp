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
<%-- <base href="<%=basePath%>" /> --%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<!-- <meta charset="utf-8" /> -->
<meta name='apple-itunes-app' content='app-id=uto168.com.Apps'>
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, user-scalable=0, minimum-scale=1.0, maximum-scale=1.0">
<title>${specialityDetails.goodName}</title>
<link rel="stylesheet"
	href="<%=basePath%>assets/agile/css/agile.layout.css">
<link rel="stylesheet"
	href="<%=basePath%>assets/third/seedsui/plugin/seedsui/seedsui.min.css">
<script src="<%=basePath%>assets/third/jquery/jquery-2.1.3.js"></script>
<link rel="stylesheet" href="<%=basePath%>CSS/share.css">
<script>
			window.onload=function (){
// 				alert("111");
// 				var evauetelist3 = [];
			}
				window.addEventListener('orientationchange', function(event) {
					if (window.orientation == 180 || window.orientation == 0) {
						//A.alert('提示', '竖屏');
						//return false;
						history.go(0)
					}
					if (window.orientation == 90 || window.orientation == -90) {
						//A.alert('提示', '横屏');
						//return false;
						history.go(0)
					}
				});
			</script>
<style>
</style>
</head>
<body>
	<div id="section_container">
		<section id="slider_section" data-role="section" class="active">
		<header>
		<div id="tabbarOuter" data-scroll="horizontal">
			<div class="scroller">
				<ul class="slidebar">
					<li class="tab active" data-role="tab" href="#page1"
						data-toggle="page"><label class="tab-label font-size17">商品</label>
					</li>
					<li class="tab" data-role="tab" href="#page2" data-toggle="page">
						<label class="tab-label font-size17">详情</label>
					</li>
					<li class="tab" data-role="tab" href="#page3" data-toggle="page">
						<label class="tab-label font-size17">评价</label>
					</li>
				</ul>
			</div>
		</div>
		</header> <article data-role="article" id="slider_article"
			class="active page-bottom">
		<div id="sliderPage" data-role="slider" class="full">
			<div class="scroller">
				<div id="page1" class="slide" data-role="page"
					data-scroll="verticle">
					<div class="scroller">
						<div id="slide" data-role="slider"
							class="full scroller-border-bottom"
							style="overflow: hidden; height: 180px;">
							<div class="scroller">
								<c:forEach items="${SpecialityBannerList}"
									var="SpecialityBanner">
									<div class="slide slide-img">
										<img class="slide-img" src="${SpecialityBanner.imgUrl}" />
									</div>
								</c:forEach>
							</div>
						</div>
						<ul class="list margin-top10">
							<li class="noborder">
								<div class="padding-goodinfo font-size15">
									商品名称：<label>${specialityDetails.goodName}</label>
								</div>
							</li>
							<li class="noborder">
								<div class="padding-goodinfo font-size14">
									${specialityDetails.intro}</div>
							</li>
							<li class="noborder">
								<div class="justify-content font-size14">
									<label class="part-note-left">价格：</label>
									<p class="part-note-right">
										<s class="mark-price"><label>￥${SpecialityStandard.marketPrice}</label></s><label
											class="price">￥${SpecialityStandard.price}</label>
									</p>
								</div>
							</li>
							<li class="noborder" style="padding: 0px 10px;">
								<hr style="width: 100%;" />
							</li>
							<li class="noborder">
								<div class="justify-content font-size14">
									<label class="part-note-left">选择：</label>
									<p class="part-note-right">
										<c:forEach var="specialityStandard"
											items="${SpecialityStandardList}">
											<c:choose>
												<c:when
													test="${specialityStandard.id == SpecialityStandard.id}">
													<a class="good-norms good-norms-selected">${specialityStandard.name}</a>
												</c:when>
												<c:otherwise>
													<a
														href="<%=basePath%>shareShop.html?specialityId=${specialityStandard.goodId}&goodsStandardId=${specialityStandard.id}"
														class="good-norms change">${specialityStandard.name}</a>
													<input class="" type="hidden"
														value="${specialityStandard.id}" />
												</c:otherwise>
											</c:choose>
										</c:forEach>
									</p>
								</div>
							</li>
							<li class="noborder" style="padding: 0px 10px;">
								<hr style="width: 100%;" />
							</li>
							<li>
								<div class="justify-content font-size14">
									<label class="part-note-left">运费：</label>
									<p class="part-note-right font-color1">${specialityDetails.freightIntro}</p>
								</div>
							</li>
							<li>
								<div class="justify-content font-size12 prompt">
									<ul>
										<li><img src="<%=basePath%>/Image/icon/prompt.png" />库存：${SpecialityStandard.inventory}</li>
										<li><img src="<%=basePath%>/Image/icon/prompt.png" />支持快递</li>
										<c:if test="${specialityDetails.isSelf==0}">
											<li><img src="<%=basePath%>/Image/icon/prompt.png" />支持自取</li>
										</c:if>
										<li><img src="<%=basePath%>/Image/icon/prompt.png" />${specialityDetails.returnTime}天内可退货</li>
									</ul>
								</div>
							</li>
							<li>
								<div class="justify-content font-size14 prompt-next"></div>
							</li>
						</ul>
						<div>
							<ul class="comment">
								<li>评论</li>
								<li class="noborder"><c:forEach items="${evaluteList}"
										var="evalute" varStatus="status">
										<ul class="comment-list" number="${status.index}">
											<li class="noborder"><span style="float: left">${evalute.nickName}</span>
												<span class="spanstar"><img class="graystar"
													src="<%=basePath%>Image/icon/star-gray.png" />
													<div style="width: ${evalute.score*20}px"
														class="yellowstar">
														<img src="<%=basePath%>/Image/icon/star-yellow.png" />
													</div></span> <span style="float: right" class="font-size13">${evalute.addTime}</span>
											</li>
											<li style="clear: both;" class="noborder font-size13">
												${evalute.content}</li>
											<li class="comment-imglist"><c:forEach
													items="${evalute.evaluateImglist}" var="evaluateImg"
													varStatus="status">
													<img src="${evaluateImg.imgUrl }" number="${status.index}" />
												</c:forEach>
										</ul>
									</c:forEach></li>
								<li class="noborder">
									<div class="justify-content font-size14"
										style="text-align: center;">
										<a data-role="tab" href="#page3" data-toggle="page"
											style="display: block; border: 1px solid #ccc; border-radius: 5px; padding: 10px 15px;">查看全部评论</a>
									</div>
								</li>
							</ul>

						</div>
					</div>
				</div>
				<div id="page2" class="slide" data-role="page"
					data-scroll="verticle">
					<div id="tabbarOuter" data-scroll="horizontal">
						<div class="scroller">
							<ul class="slidebar goodinfo">
								<li class="tab active" data-role="tab" href="#page4"
									data-toggle="page"><label class="tab-label font-size14">商品详情</label>
								</li>
								<li class="tab" data-role="tab" href="#page5" data-toggle="page">
									<label class="tab-label font-size14">属性参数</label>
								</li>
							</ul>
						</div>
					</div>
					<article data-role="article" id="slider_article" class="active">
					<div id="sliderGoodInfo" data-role="slider" class="full">
						<div class="scroller">
							<div id="page4" class="slide" data-role="page"
								data-scroll="verticle">
								<div class="scroller">
									<c:forEach items="${SpecialityInfoList}" var="SpecialityInfo">
										<img class="img-full-width" src="${SpecialityInfo.imgUrl}" />
									</c:forEach>
								</div>
								<div class="scroller"></div>
							</div>
							<div id="page5" class="slide" data-role="page"
								data-scroll="verticle">
								<div class="scroller">
									<table cellpadding="0" cellspacing="0" class="tabattr">
										<c:forEach items="${SpecialityAttributesList}"
											var="SpecialityAttributes">
											<tr>
												<td class="tdpadding">${SpecialityAttributes.attributeName}</td>
												<td class="tdpadding">${SpecialityAttributes.attributeValue}</td>
											</tr>
										</c:forEach>
									</table>
								</div>
							</div>
						</div>
					</div>
					</article>

				</div>
				<div id="page3" class="slide" data-role="page" data-scroll="pullup">
					<div class="scroller">
						<ul class="slidebar comment-title">
							<li class="tab active commentli" data-role="tab" ischecked="0"
								onclick="checkcomment(this,'')"><span
								class="tab-label font-size12">全部评价<br />${totalNum}
							</span></li>
							<li class="tab commentli" data-role="tab" ischecked="0"
								onclick="checkcomment(this,'l')"><span
								class="tab-label font-size12">好评<br />${lEvaluateNum}
							</span></li>
							<li class="tab commentli" data-role="tab" ischecked="0"
								onclick="checkcomment(this, 'm')"><span
								class="tab-label font-size12">中评<br />${mEvaluateNum}
							</span></li>
							<li class="tab commentli" data-role="tab" ischecked="0"
								onclick="checkcomment(this,'s')"><span
								class="tab-label font-size12">差评<br />${sEvaluateNum}
							</span></li>
						</ul>
						<ul class="comment">
							<li></li>
							<li class="noborder" id="commentlist"><c:forEach
									items="${evaluteList3}" var="evalute3" varStatus="status">
									<ul class="comment-list3" number="${status.index}">
										<li class="noborder"><span style="float: left">${evalute3.nickName}</span>
											<span class="spanstar"><img class="graystar"
												src="<%=basePath%>Image/icon/star-gray.png" />
												<div style="width: ${evalute3.score*20}px"
													class="yellowstar">
													<img src="<%=basePath%>/Image/icon/star-yellow.png" />
												</div></span> <span style="float: right" class="font-size13">${evalute3.addTime}</span>
										</li>
										<li style="clear: both;" class="noborder font-size13">
											${evalute3.content}</li>
										<li class="comment-imglist3"><c:forEach
												items="${evalute3.evaluateImglist}" var="evaluateImg3"
												varStatus="status">
												<img src="${evaluateImg3.imgUrl }" number="${status.index}" />
											</c:forEach>
									</ul>
								</c:forEach></li>
						</ul>
					</div>
				</div>

			</div>
		</div>
		</article> <footer>
		<ul class="menubar">
			<li class="tab download" data-role="tab" id="download"><label
				class="tab-label  tabbar-bottom middle-line">下载游乐APP</label></li>

			<li class="tab download" data-role="tab"><label
				class="tab-label  tabbar-bottom check">在游乐中查看</label></li>
		</ul>
		</footer> </section>
	</div>
	<input type="hidden" id="specialityId" value="${specialityDetails.id}" />
	<input type="hidden" id="pageNum" value="1" />
	<input type="hidden" id="type" value="" />
	<!--- third -->
	<script src="<%=basePath%>/assets/third/jquery/jquery-2.1.3.min.js"></script>
	<script
		src="<%=basePath%>/assets/third/jquery/jquery.mobile.custom.min.js"></script>
	<script src="<%=basePath%>/assets/third/iscroll/iscroll-probe.js"></script>
	<!---  agile -->
	<script type="text/javascript"
		src="<%=basePath%>/assets/agile/js/agile.js"></script>
	<script type="text/javascript"
		src="<%=basePath%>/assets/bridge/exmobi.js"></script>
	<script type="text/javascript"
		src="<%=basePath%>/assets/bridge/agile.exmobi.js"></script>
	<!-- app -->
	<script type="text/javascript"
		src="<%=basePath%>/assets/app/js/app.seedsui.js"></script>
	<script>
	
	$(".download").on(A.options.clickEvent,function(){
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
		//初始化刷新评论
		var refresh = A.Refresh("#page3");
		var list="<%=session.getAttribute("list")%>"; 
        var s = list.split(',');
        var headimglist = [];
        s.forEach(function(i){ 
        	var headimg=new Object();
        	headimg.imgURL = i;
        	headimg.content = "";
        	headimglist.push(headimg);
        }) ;
			$('#slide img').on(A.options.clickEvent, function() {
				A.Component.pictureShow({
					id : 'picture',
					index : $("#slide .slide").index($("#slide .slide.active")),
					title : '',
					list : headimglist
				});
			});
		
		var evauetelist = [];
		evauetelist=<%=session.getAttribute("evaluete")%>; 
		var evaueteList = [];
		evauetelist.forEach(function(i){ 
			var evaueteImglist = [];
			i.evaluateImglist.forEach(function(e){ 
					var evaluateimg=new Object();
					var str1 = e.imgUrl.replace('Minfile', 'Maxfile');
		        	evaluateimg.imgURL = str1;
		        	evaluateimg.content = i.content;
		        	evaueteImglist.push(evaluateimg);
	        }) ;		
				evaueteList.push(evaueteImglist);
	        }) ;
		console.log(evaueteList);
		$('.comment-list img').on(A.options.clickEvent, function() {
			A.Component.pictureShow({
				id : 'picture1',
				index : $(this).attr("number"),
				title : '',
				//list: headimglist
				list : evaueteList[parseInt($(this).parent().parent().attr("number"))]
			});
		});
		evauetelist3=<%=session.getAttribute("evaluete3")%>; 
		foreach(evauetelist3);
		function foreach(evauetelist3){
			var evaueteList4 = [];
			evauetelist3.forEach(function(i){ 
				var evaueteImglist3 = [];
				i.evaluateImglist.forEach(function(e){ 
						var evaluateimg3=new Object();
						var str1 = e.imgUrl.replace('Minfile', 'Maxfile');
			        	evaluateimg3.imgURL = str1;
			        	evaluateimg3.content = i.content;
			        	evaueteImglist3.push(evaluateimg3);
		        }) ;		
					evaueteList4.push(evaueteImglist3);
		        }) ;
			console.log("l:"+evauetelist3.length);
			console.log("L:"+evaueteList4.length);
			$('.comment-list3 img').on(A.options.clickEvent, function() {
				A.Component.pictureShow({
					id : 'picture2',
					index : $(this).attr("number"),
					title : '',
					list : evaueteList4[parseInt($(this).parent().parent().attr("number"))]
				});
			});	
		}
		//切换评价类型
		function checkcomment(obj, model) {
// 			evauetelist3.splice(0,evauetelist3.length);
			if ($(obj).attr("ischecked") == "0") {
				$(".commentli").removeClass("active");
				$(".commentli").attr("ischecked", "0");
				$(obj).attr("ischecked", "1");
				$(obj).addClass("active");
				$("#commentlist").empty();
				var specialityId = $("#specialityId").val();
				 $("#pageNum").val("1");
				$("#type").val(model);
				evauetelist3 = [];
				getList(model,specialityId,1);
				foreach(evauetelist3);
			}
		}
		$('#slider_section').on('sectionshow', function() {
			A.Component.scroll('#tabbarOuter');
		});
		$('#slider_article')
				.on(
						'articleload',
						function() {
							A.Slider('#slide', {
								dots : 'center'
							});
							A.Slider('#sliderPage', {
								dots : 'hide'
							});
							A.Slider('#sliderGoodInfo', {
								dots : 'hide'
							});

							//上拉刷新评价
							refresh.on(
											'pullup',
											function() {
												//setTimeout是模拟异步效果，实际场景请勿使用
												setTimeout(
														function() {
                                                   var specialityId = $("#specialityId").val();
                                                   var pageNum = $("#pageNum").val();
                                                   var type =$("#type").val();
                                                      console.log("refresh before:"+evauetelist3.length);
                                                              getList(type,specialityId,pageNum);
                                                              alert("3333:::"+evauetelist3.length);
                                                              foreach(evauetelist3);
                                                              console.log("refresh after:"+evauetelist3.length);
// 															refresh.refresh();//当scroll区域有dom结构变化需刷新
														}, 1000);
											});
						});
		function getList(type,specialityId,pageNum){
			$.ajax({
				 url: '<%=basePath%>mvc/getevaluateList',
			     data: {
			    	 type: type,
			    	 specialityId:specialityId,
			    	 pageNum:pageNum
		            }, // 目标参数  
		            type: 'POST',
		            dataType: 'json',
		            async: false,
		            success: function (result) {
		                if (result.code=='0001') {
		                	$("#pageNum").val(parseInt(pageNum)+1);
		                	div(result.evaluteList3,pageNum);
		                	console.log("ajax before:"+evauetelist3.length);
		                	evauetelist3 = evauetelist3.concat(result.evaluteList3);
		                	console.log("ajax after:"+evauetelist3.length);
		                }
		                else {
		                	alert(result.message);
		                }
		            }
			});	
		}
		function div(evaluteList3,pageNum){
			var showimg ='' ;
			  $.each(evaluteList3, function (index, obj) {
				  var s =  (parseInt(pageNum)-1)*2+parseInt(index);
				  showimg = showimg+'<ul class="comment-list3" number="'+s+'">'+
					'<li class="noborder"><span style="float:left">'+obj.nickName+'</span>'+
					'<span class="spanstar">'+
					'<img class="graystar" src="<%=basePath%>Image/icon/star-gray.png" />'+
					'<div style="width:'+obj.score*20+'px" class="yellowstar">'+
				    	'<img src="<%=basePath%>Image/icon/star-yellow.png" />'
										+ '</div>'
										+ '</span>'
										+ '<span style="float:right" class="font-size13">'
										+ obj.addTime
										+ '</span>'
										+ '</li><li style="clear:both;" class="font-size13">'
										+ obj.content + ' </li>';
								if (obj.evaluateImglist.length != 0) {
									showimg = showimg
											+ '<li class="comment-imglist3">';
								}
								$
										.each(
												obj.evaluateImglist,
												function(index, ob) {
													var n = parseInt(index);
													showimg = showimg
															+ '<img src="'+ob.imgUrl+'" number="'+n+'" />';
												});

								showimg = showimg + '</ul>';
							});
			$('#commentlist').append(showimg);
		}
	</script>
</body>
</html>

