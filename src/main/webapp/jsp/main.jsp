<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Health Track - Main Menu</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Health Track</h1>
            <h2>Personal Wellness Platform</h2>
        </header>
        
        <c:if test="${not empty sessionScope.user}">
            <div class="user-info">
                <p><strong>Welcome, ${sessionScope.user.fullName}!</strong></p>
                <p>Health ID: ${sessionScope.user.healthId}</p>
            </div>
        </c:if>
        
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/account">Account Info</a></li>
                <li><a href="${pageContext.request.contextPath}/appointment">Book Appointment</a></li>
                <li><a href="${pageContext.request.contextPath}/challenge">Create Challenge</a></li>
                <li><a href="${pageContext.request.contextPath}/summary">Monthly Summary</a></li>
                <li><a href="${pageContext.request.contextPath}/search">Search Records</a></li>
                <li><a href="${pageContext.request.contextPath}/logout">Sign Out</a></li>
            </ul>
        </nav>
        
        <div class="card">
            <h3>Quick Overview</h3>
            <p>Welcome to Health Track! Use the navigation menu above to:</p>
            <ul style="margin-left: 20px; margin-top: 10px;">
                <li>View and manage your account information</li>
                <li>Book appointments with healthcare providers</li>
                <li>Create and participate in wellness challenges</li>
                <li>View your monthly health summaries</li>
                <li>Search your health records</li>
            </ul>
        </div>
    </div>
</body>
</html>

