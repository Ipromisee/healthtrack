<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>注册 - 健康追踪</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container login-form">
        <h1>注册新账户</h1>
        <h2>提交后等待管理员审核</h2>

        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/register">
            <div class="form-group">
                <label for="healthId">健康ID：</label>
                <input type="text" id="healthId" name="healthId" required placeholder="自定义唯一健康ID">
            </div>
            <div class="form-group">
                <label for="fullName">姓名：</label>
                <input type="text" id="fullName" name="fullName" required placeholder="请输入您的姓名">
            </div>
            <div class="form-group">
                <label for="userRole">角色：</label>
                <select id="userRole" name="userRole">
                    <option value="Patient">患者</option>
                    <option value="Caregiver">照顾者</option>
                    <option value="Provider">医疗服务者</option>
                </select>
            </div>
            <button type="submit" style="width: 100%;">提交注册</button>
        </form>

        <p style="margin-top: 20px; text-align: center;">
            已有账号？ <a href="${pageContext.request.contextPath}/login">返回登录</a>
        </p>
    </div>
</body>
</html>

