<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>系统管理 - 健康追踪</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>⚙️ 系统管理中心</h1>
            <h2>健康追踪平台管理控制台</h2>
        </header>
        
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/main">🏠 主菜单</a></li>
                <li><a href="${pageContext.request.contextPath}/admin" class="active">⚙️ 系统管理</a></li>
                <li><a href="${pageContext.request.contextPath}/search">🔍 数据搜索</a></li>
                <li><a href="${pageContext.request.contextPath}/summary">📊 系统统计</a></li>
                <li><a href="${pageContext.request.contextPath}/logout">🚪 退出登录</a></li>
            </ul>
        </nav>
        
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        
        <!-- 系统概览 -->
        <div class="section">
            <h3>📈 系统概览</h3>
            <div class="stats-grid">
                <div class="stat-card">
                    <h3>${stats.totalUsers}</h3>
                    <p>总用户数</p>
                </div>
                <div class="stat-card" style="background: linear-gradient(135deg, #28a745 0%, #20c997 100%);">
                    <h3>${stats.activeCount}</h3>
                    <p>活跃用户</p>
                </div>
                <div class="stat-card" style="background: linear-gradient(135deg, #17a2b8 0%, #6610f2 100%);">
                    <h3>${stats.totalProviders}</h3>
                    <p>医疗提供者</p>
                </div>
                <div class="stat-card" style="background: linear-gradient(135deg, #fd7e14 0%, #dc3545 100%);">
                    <h3>${stats.activeChallenges}</h3>
                    <p>活跃挑战</p>
                </div>
            </div>
        </div>
        
        <!-- 用户角色分布 -->
        <div class="section">
            <h3>👥 用户角色分布</h3>
            <div class="role-distribution">
                <div class="role-stat">
                    <span class="role-icon">👤</span>
                    <span class="role-count">${stats.patientCount}</span>
                    <span class="role-label">患者</span>
                </div>
                <div class="role-stat">
                    <span class="role-icon">🩺</span>
                    <span class="role-count">${stats.providerCount}</span>
                    <span class="role-label">医疗提供者</span>
                </div>
                <div class="role-stat">
                    <span class="role-icon">💝</span>
                    <span class="role-count">${stats.caregiverCount}</span>
                    <span class="role-label">照顾者</span>
                </div>
                <div class="role-stat">
                    <span class="role-icon">⚙️</span>
                    <span class="role-count">${stats.adminCount}</span>
                    <span class="role-label">管理员</span>
                </div>
            </div>
        </div>
        
        <!-- 用户管理 -->
        <div class="section">
            <h3>👥 用户管理</h3>
            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>健康ID</th>
                            <th>姓名</th>
                            <th>角色</th>
                            <th>状态</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="user" items="${allUsers}">
                            <tr>
                                <td>${user.userId}</td>
                                <td><code>${user.healthId}</code></td>
                                <td>${user.fullName}</td>
                                <td>
                                    <form method="post" action="${pageContext.request.contextPath}/admin" class="inline-form">
                                        <input type="hidden" name="action" value="updateUserRole">
                                        <input type="hidden" name="userId" value="${user.userId}">
                                        <select name="newRole" onchange="this.form.submit()" class="small-select">
                                            <option value="Patient" ${user.userRole == 'Patient' ? 'selected' : ''}>患者</option>
                                            <option value="Provider" ${user.userRole == 'Provider' ? 'selected' : ''}>医疗提供者</option>
                                            <option value="Caregiver" ${user.userRole == 'Caregiver' ? 'selected' : ''}>照顾者</option>
                                            <option value="Admin" ${user.userRole == 'Admin' ? 'selected' : ''}>管理员</option>
                                        </select>
                                    </form>
                                </td>
                                <td>
                                    <form method="post" action="${pageContext.request.contextPath}/admin" class="inline-form">
                                        <input type="hidden" name="action" value="updateUserStatus">
                                        <input type="hidden" name="userId" value="${user.userId}">
                                        <select name="newStatus" onchange="this.form.submit()" 
                                                class="small-select status-${user.accountStatus}">
                                            <option value="Active" ${user.accountStatus == 'Active' ? 'selected' : ''}>活跃</option>
                                            <option value="Inactive" ${user.accountStatus == 'Inactive' ? 'selected' : ''}>未激活</option>
                                            <option value="Suspended" ${user.accountStatus == 'Suspended' ? 'selected' : ''}>已暂停</option>
                                        </select>
                                    </form>
                                </td>
                                <td>
                                    <span class="badge badge-info">
                                        <fmt:formatDate value="${user.createdAt}" pattern="yyyy-MM-dd" />
                                    </span>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        
        <!-- 医疗提供者管理 -->
        <div class="section">
            <h3>🏥 医疗提供者管理</h3>
            <p class="section-desc">已验证: ${stats.verifiedProviders} / ${stats.totalProviders}</p>
            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>执照号</th>
                            <th>名称</th>
                            <th>验证状态</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="provider" items="${allProviders}">
                            <tr>
                                <td>${provider.providerId}</td>
                                <td><code>${provider.licenseNo}</code></td>
                                <td>${provider.providerName}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${provider.verified}">
                                            <span class="badge badge-success">✓ 已验证</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-warning">○ 未验证</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:if test="${!provider.verified}">
                                        <form method="post" action="${pageContext.request.contextPath}/admin" class="inline-form">
                                            <input type="hidden" name="action" value="verifyProvider">
                                            <input type="hidden" name="providerId" value="${provider.providerId}">
                                            <button type="submit" class="btn btn-success btn-small">验证</button>
                                        </form>
                                    </c:if>
                                    <c:if test="${provider.verified}">
                                        <span class="text-muted">-</span>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>





