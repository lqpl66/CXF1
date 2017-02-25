<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	String file = "files";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script src="<%=basePath%>assets/third/jquery/jquery-2.1.3.js"></script>
<script type="text/javascript">
function addUser() {  
    var form = document.forms[0];  
    form.action = "${pageContext.request.contextPath}/mvc/add";  
    //form.action = "${pageContext.request.contextPath}/user/addUser2";  
    //form.action = "${pageContext.request.contextPath}/user/addUser3";  
    form.method = "post";  
    form.submit();  
} 
</script>
</head>
<body>
<!-- <form > -->
<form action="<%=basePath%>/mvc/add" method="POST" enctype="multipart/form-data" >  
    name: <input type="text" name="userName"><br/>  
    pass: <input type="password" name="password"><br/>  
    <input type="submit">  
<!--  <input type="button" value="提交" onclick="addUser()">  -->
</form> 
</body>
</html>