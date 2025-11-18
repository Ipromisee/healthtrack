<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Monthly Summary - Health Track</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Monthly Health Summary</h1>
        </header>
        
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/main">Main Menu</a></li>
                <li><a href="${pageContext.request.contextPath}/summary">Monthly Summary</a></li>
                <li><a href="${pageContext.request.contextPath}/logout">Sign Out</a></li>
            </ul>
        </nav>
        
        <div class="section">
            <h3>Select Month</h3>
            <form method="get" action="${pageContext.request.contextPath}/summary">
                <div class="form-group" style="display: flex; gap: 10px; align-items: end;">
                    <div style="flex: 1;">
                        <label for="year">Year:</label>
                        <input type="number" id="year" name="year" value="${selectedYear}" min="2000" max="2100" required>
                    </div>
                    <div style="flex: 1;">
                        <label for="month">Month:</label>
                        <input type="number" id="month" name="month" value="${selectedMonth}" min="1" max="12" required>
                    </div>
                    <div>
                        <button type="submit">View Summary</button>
                    </div>
                </div>
            </form>
        </div>
        
        <c:if test="${not empty summary}">
            <div class="section">
                <h3>Monthly Summary for ${selectedYear}-${selectedMonth < 10 ? '0' : ''}${selectedMonth}</h3>
                <div class="stats-grid">
                    <div class="stat-card">
                        <h3>${summary.totalSteps}</h3>
                        <p>Total Steps</p>
                    </div>
                    <div class="stat-card">
                        <h3>${summary.totalAppointments}</h3>
                        <p>Total Appointments</p>
                    </div>
                    <div class="stat-card">
                        <h3><fmt:formatDate value="${summary.lastUpdated}" pattern="MM-dd HH:mm" /></h3>
                        <p>Last Updated</p>
                    </div>
                </div>
            </div>
        </c:if>
        
        <div class="section">
            <h3>Health Metrics Statistics (Year ${selectedYear})</h3>
            <div class="stats-grid">
                <div class="stat-card">
                    <h3><fmt:formatNumber value="${avgSteps}" maxFractionDigits="0" /></h3>
                    <p>Average Steps/Month</p>
                </div>
                <div class="stat-card">
                    <h3>${minSteps}</h3>
                    <p>Minimum Steps</p>
                </div>
                <div class="stat-card">
                    <h3>${maxSteps}</h3>
                    <p>Maximum Steps</p>
                </div>
            </div>
        </div>
        
        <div class="section">
            <h3>Total Appointments in Selected Month</h3>
            <div class="stat-card">
                <h3>${totalAppointments}</h3>
                <p>Appointments</p>
            </div>
        </div>
        
        <div class="section">
            <h3>Challenges with Most Participants</h3>
            <c:if test="${not empty topChallenges}">
                <table>
                    <thead>
                        <tr>
                            <th>Goal</th>
                            <th>Start Date</th>
                            <th>End Date</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="challenge" items="${topChallenges}">
                            <tr>
                                <td>${challenge.goal}</td>
                                <td><fmt:formatDate value="${challenge.startDate}" pattern="yyyy-MM-dd" /></td>
                                <td><fmt:formatDate value="${challenge.endDate}" pattern="yyyy-MM-dd" /></td>
                                <td>${challenge.status}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <c:if test="${empty topChallenges}">
                <p>No challenges found.</p>
            </c:if>
        </div>
        
        <div class="section">
            <h3>Most Active Users</h3>
            <c:if test="${not empty mostActiveUsers}">
                <table>
                    <thead>
                        <tr>
                            <th>Health ID</th>
                            <th>Full Name</th>
                            <th>Account Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="user" items="${mostActiveUsers}">
                            <tr>
                                <td>${user.healthId}</td>
                                <td>${user.fullName}</td>
                                <td>${user.accountStatus}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <c:if test="${empty mostActiveUsers}">
                <p>No active users found.</p>
            </c:if>
        </div>
    </div>
</body>
</html>

