<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>月度汇总 - 健康追踪</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>
                <c:choose>
                    <c:when test="${userRole == 'Admin'}">系统健康统计</c:when>
                    <c:otherwise>月度健康汇总</c:otherwise>
                </c:choose>
            </h1>
        </header>
        
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/main">主菜单</a></li>
                <li><a href="${pageContext.request.contextPath}/summary">月度汇总</a></li>
                <li><a href="${pageContext.request.contextPath}/logout">退出登录</a></li>
            </ul>
        </nav>
        
        <div class="section">
            <h3>选择月份</h3>
            <form method="get" action="${pageContext.request.contextPath}/summary">
                <div class="input-group">
                    <div class="form-group">
                        <label for="year">年份：</label>
                        <input type="number" id="year" name="year" value="${selectedYear}" min="2000" max="2100" required>
                    </div>
                    <div class="form-group">
                        <label for="month">月份：</label>
                        <input type="number" id="month" name="month" value="${selectedMonth}" min="1" max="12" required>
                    </div>
                    <div class="form-group">
                        <label>&nbsp;</label>
                        <button type="submit" style="width: 100%;">查看汇总</button>
                    </div>
                </div>
            </form>
        </div>
        
        <c:if test="${not empty summary}">
            <div class="section">
                <h3>${selectedYear}年${selectedMonth < 10 ? '0' : ''}${selectedMonth}月汇总</h3>
                <div class="stats-grid">
                    <div class="stat-card">
                        <h3><fmt:formatNumber value="${summary.totalSteps}" pattern="#,###" /></h3>
                        <p>总步数</p>
                    </div>
                    <div class="stat-card">
                        <h3>${summary.totalAppointments}</h3>
                        <p>预约总数</p>
                    </div>
                    <div class="stat-card">
                        <h3><fmt:formatDate value="${summary.lastUpdated}" pattern="MM-dd HH:mm" /></h3>
                        <p>最后更新</p>
                    </div>
                </div>
            </div>
        </c:if>
        
        <div class="section">
            <h3>健康指标统计（${selectedYear}年）</h3>
            <div class="stats-grid">
                <div class="stat-card">
                    <h3><fmt:formatNumber value="${avgSteps}" maxFractionDigits="0" pattern="#,###" /></h3>
                    <p>平均步数/月</p>
                </div>
                <div class="stat-card">
                    <h3><fmt:formatNumber value="${minSteps}" pattern="#,###" /></h3>
                    <p>最少步数</p>
                </div>
                <div class="stat-card">
                    <h3><fmt:formatNumber value="${maxSteps}" pattern="#,###" /></h3>
                    <p>最多步数</p>
                </div>
            </div>
        </div>
        
        <div class="section">
            <h3>所选月份预约总数</h3>
            <div class="stat-card" style="max-width: 300px; margin: 0 auto;">
                <h3>${totalAppointments}</h3>
                <p>预约次数</p>
            </div>
        </div>
        
        <div class="section">
            <h3>最受欢迎的挑战</h3>
            <c:if test="${not empty topChallenges}">
                <table>
                    <thead>
                        <tr>
                            <th>目标</th>
                            <th>开始日期</th>
                            <th>结束日期</th>
                            <th>状态</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="challenge" items="${topChallenges}">
                            <tr>
                                <td>${challenge.goal}</td>
                                <td><fmt:formatDate value="${challenge.startDate}" pattern="yyyy-MM-dd" /></td>
                                <td><fmt:formatDate value="${challenge.endDate}" pattern="yyyy-MM-dd" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${challenge.status == 'Active'}">
                                            <span class="badge badge-success">进行中</span>
                                        </c:when>
                                        <c:when test="${challenge.status == 'Completed'}">
                                            <span class="badge badge-success">已完成</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-info">${challenge.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <c:if test="${empty topChallenges}">
                <p style="text-align: center; color: #666; padding: 20px;">暂无挑战记录</p>
            </c:if>
        </div>
        
        <c:if test="${sessionScope.user.userRole == 'Admin'}">
            <div class="section">
                <h3>最活跃用户</h3>
                <c:if test="${not empty mostActiveUsers}">
                    <table>
                        <thead>
                            <tr>
                                <th>健康ID</th>
                                <th>姓名</th>
                                <th>账户状态</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="user" items="${mostActiveUsers}">
                                <tr>
                                    <td>${user.healthId}</td>
                                    <td>${user.fullName}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${user.accountStatus == 'Active'}">
                                                <span class="badge badge-success">活跃</span>
                                            </c:when>
                                            <c:when test="${user.accountStatus == 'Inactive'}">
                                                <span class="badge badge-warning">未激活</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-warning">${user.accountStatus}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
                <c:if test="${empty mostActiveUsers}">
                    <p style="text-align: center; color: #666; padding: 20px;">暂无活跃用户</p>
                </c:if>
            </div>
        </c:if>
    </div>
</body>
</html>
