<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Health Track - Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container login-form">
        <h1>Health Track</h1>
        <h2>Personal Wellness Platform</h2>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        
        <form method="post" action="${pageContext.request.contextPath}/login">
            <div class="form-group">
                <label for="healthId">Health ID:</label>
                <input type="text" id="healthId" name="healthId" required placeholder="Enter your Health ID">
            </div>
            
            <button type="submit">Login</button>
        </form>
        
        <p style="margin-top: 20px; text-align: center; color: #666;">
            Sample Health IDs: HT001, HT002, HT003, HT004, HT005
        </p>
    </div>
</body>
</html>

