<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>健康追踪 - 登录</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container login-form">
        <h1>健康追踪</h1>
        <h2>个人健康管理平台</h2>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        
        <form method="post" action="${pageContext.request.contextPath}/login">
            <div class="form-group">
                <label for="healthId">健康ID：</label>
                <input type="text" id="healthId" name="healthId" required placeholder="请输入您的健康ID">
            </div>
            
            <button type="submit" style="width: 100%;">登录</button>
        </form>
        <p style="margin-top: 15px; text-align: center;">
            没有账号？<a href="${pageContext.request.contextPath}/register">前往注册</a>
        </p>
        
        <p style="margin-top: 25px; text-align: center; color: #666; font-size: 14px;">
            示例健康ID：<br>
            HT001 (患者), HT002 (患者), HT003 (患者), HT004 (照顾者), HT005 (患者)<br>
            HT006 (患者), HT007 (医疗服务提供者), HT008 (患者), HT009 (照顾者), HT010 (管理员)
        </p>
    </div>
</body>
</html>
