<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>健康追踪 - 主菜单</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>🏥 健康追踪</h1>
            <h2>个人健康管理平台</h2>
        </header>
        
        <c:if test="${not empty sessionScope.user}">
            <!-- 用户信息卡片 -->
            <div class="user-welcome-card role-${sessionScope.user.userRole}">
                <div class="welcome-avatar">
                    <c:choose>
                        <c:when test="${sessionScope.user.userRole == 'Patient'}">👤</c:when>
                        <c:when test="${sessionScope.user.userRole == 'Provider'}">🩺</c:when>
                        <c:when test="${sessionScope.user.userRole == 'Caregiver'}">💝</c:when>
                        <c:when test="${sessionScope.user.userRole == 'Admin'}">⚙️</c:when>
                    </c:choose>
                </div>
                <div class="welcome-info">
                    <h3>欢迎回来，${sessionScope.user.fullName}！</h3>
                    <p>健康ID: <code>${sessionScope.user.healthId}</code></p>
                    <div class="welcome-badges">
                        <span class="role-badge role-${sessionScope.user.userRole}">
                            <c:choose>
                                <c:when test="${sessionScope.user.userRole == 'Patient'}">患者</c:when>
                                <c:when test="${sessionScope.user.userRole == 'Provider'}">医疗服务提供者</c:when>
                                <c:when test="${sessionScope.user.userRole == 'Caregiver'}">照顾者</c:when>
                                <c:when test="${sessionScope.user.userRole == 'Admin'}">系统管理员</c:when>
                                <c:otherwise>${sessionScope.user.userRole}</c:otherwise>
                            </c:choose>
                        </span>
                        <span class="status-badge status-${sessionScope.user.accountStatus}">
                            <c:choose>
                                <c:when test="${sessionScope.user.accountStatus == 'Active'}">● 活跃</c:when>
                                <c:when test="${sessionScope.user.accountStatus == 'Inactive'}">○ 未激活</c:when>
                                <c:when test="${sessionScope.user.accountStatus == 'Suspended'}">⊘ 已暂停</c:when>
                            </c:choose>
                        </span>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- 功能菜单 -->
        <nav class="main-menu">
            <h3>快捷功能</h3>
            <ul>
                <!-- 通用功能 -->
                <li><a href="${pageContext.request.contextPath}/account" class="menu-item">
                    <span class="menu-icon">👤</span>
                    <span class="menu-text">账户信息</span>
                    <span class="menu-desc">管理个人资料和联系方式</span>
                </a></li>

                <!-- ==================== 患者专属菜单 ==================== -->
                <c:if test="${sessionScope.user.userRole == 'Patient'}">
                    <li><a href="${pageContext.request.contextPath}/appointment" class="menu-item">
                        <span class="menu-icon">📅</span>
                        <span class="menu-text">预约服务</span>
                        <span class="menu-desc">预约和管理医疗服务</span>
                    </a></li>
                    <li><a href="${pageContext.request.contextPath}/challenge" class="menu-item">
                        <span class="menu-icon">🎯</span>
                        <span class="menu-text">健康挑战</span>
                        <span class="menu-desc">查看和参与健康挑战</span>
                    </a></li>
                    <li><a href="${pageContext.request.contextPath}/summary" class="menu-item">
                        <span class="menu-icon">📊</span>
                        <span class="menu-text">月度汇总</span>
                        <span class="menu-desc">查看健康数据统计</span>
                    </a></li>
                </c:if>

                <!-- ==================== 医疗提供者专属菜单 ==================== -->
                <c:if test="${sessionScope.user.userRole == 'Provider'}">
                    <li><a href="${pageContext.request.contextPath}/appointment" class="menu-item">
                        <span class="menu-icon">📋</span>
                        <span class="menu-text">预约管理</span>
                        <span class="menu-desc">查看和管理患者预约</span>
                    </a></li>
                    <li><a href="${pageContext.request.contextPath}/challenge" class="menu-item">
                        <span class="menu-icon">🎯</span>
                        <span class="menu-text">创建挑战</span>
                        <span class="menu-desc">为患者创建健康挑战</span>
                    </a></li>
                    <li><a href="${pageContext.request.contextPath}/search" class="menu-item">
                        <span class="menu-icon">🔍</span>
                        <span class="menu-text">患者搜索</span>
                        <span class="menu-desc">搜索患者健康记录</span>
                    </a></li>
                </c:if>

                <!-- ==================== 照顾者专属菜单 ==================== -->
                <c:if test="${sessionScope.user.userRole == 'Caregiver'}">
                    <li><a href="${pageContext.request.contextPath}/caregiver" class="menu-item highlight">
                        <span class="menu-icon">💝</span>
                        <span class="menu-text">患者监护</span>
                        <span class="menu-desc">监控被照顾者健康状况</span>
                    </a></li>
                    <li><a href="${pageContext.request.contextPath}/appointment" class="menu-item">
                        <span class="menu-icon">📅</span>
                        <span class="menu-text">预约服务</span>
                        <span class="menu-desc">代患者预约医疗服务</span>
                    </a></li>
                    <li><a href="${pageContext.request.contextPath}/challenge" class="menu-item">
                        <span class="menu-icon">🎯</span>
                        <span class="menu-text">健康挑战</span>
                        <span class="menu-desc">查看和参与健康挑战</span>
                    </a></li>
                </c:if>

                <!-- ==================== 管理员专属菜单 ==================== -->
                <c:if test="${sessionScope.user.userRole == 'Admin'}">
                    <li><a href="${pageContext.request.contextPath}/admin" class="menu-item highlight">
                        <span class="menu-icon">⚙️</span>
                        <span class="menu-text">系统管理</span>
                        <span class="menu-desc">用户和医疗提供者管理</span>
                    </a></li>
                    <li><a href="${pageContext.request.contextPath}/search" class="menu-item">
                        <span class="menu-icon">🔍</span>
                        <span class="menu-text">数据搜索</span>
                        <span class="menu-desc">搜索全系统健康记录</span>
                    </a></li>
                    <li><a href="${pageContext.request.contextPath}/summary" class="menu-item">
                        <span class="menu-icon">📊</span>
                        <span class="menu-text">系统统计</span>
                        <span class="menu-desc">查看系统级别统计</span>
                    </a></li>
                    <li><a href="${pageContext.request.contextPath}/challenge" class="menu-item">
                        <span class="menu-icon">🎯</span>
                        <span class="menu-text">挑战管理</span>
                        <span class="menu-desc">管理健康挑战活动</span>
                    </a></li>
                </c:if>

                <!-- 退出登录 -->
                <li><a href="${pageContext.request.contextPath}/logout" class="menu-item logout">
                    <span class="menu-icon">🚪</span>
                    <span class="menu-text">退出登录</span>
                    <span class="menu-desc">安全退出系统</span>
                </a></li>
            </ul>
        </nav>

        <!-- 功能说明卡片 -->
        <div class="card feature-card">
            <h3>📖 功能说明</h3>

            <!-- 患者功能说明 -->
            <c:if test="${sessionScope.user.userRole == 'Patient'}">
                <p>作为<strong>患者</strong>，您可以：</p>
                <ul class="feature-list">
                    <li>预约医疗服务提供者并管理预约</li>
                    <li>接受医生发起的健康挑战邀请</li>
                    <li>查看个人月度健康汇总和统计</li>
                    <li>管理个人信息和联系方式</li>
                    <li>批准或拒绝照顾者的关联请求</li>
                </ul>
                <div class="feature-note">
                    <strong>注意：</strong>账户状态和用户角色由管理员管理，健康挑战由医疗服务提供者创建。
                </div>
            </c:if>

            <!-- 医疗提供者功能说明 -->
            <c:if test="${sessionScope.user.userRole == 'Provider'}">
                <p>作为<strong>医疗服务提供者</strong>，您可以：</p>
                <ul class="feature-list">
                    <li>查看和管理患者的预约请求</li>
                    <li>创建健康挑战并邀请患者参与</li>
                    <li>搜索和查看患者健康记录</li>
                    <li>管理个人信息和资质信息</li>
                </ul>
            </c:if>

            <!-- 照顾者功能说明 -->
            <c:if test="${sessionScope.user.userRole == 'Caregiver'}">
                <p>作为<strong>照顾者</strong>，您可以：</p>
                <ul class="feature-list">
                    <li>申请关联患者，监护其健康状况</li>
                    <li>查看被照顾者的预约和健康挑战</li>
                    <li>代被照顾者预约医疗服务</li>
                    <li>接受医生发起的健康挑战邀请</li>
                    <li>管理个人信息和联系方式</li>
                </ul>
                <div class="feature-note">
                    <strong>提示：</strong>您需要先在"患者监护"页面发送关联请求，待患者确认后才能监护其健康状况。
                </div>
            </c:if>

            <!-- 管理员功能说明 -->
            <c:if test="${sessionScope.user.userRole == 'Admin'}">
                <p>作为<strong>系统管理员</strong>，您可以：</p>
                <ul class="feature-list">
                    <li>管理所有用户的账户状态和角色</li>
                    <li>验证医疗服务提供者资质</li>
                    <li>搜索和查看全系统的健康记录</li>
                    <li>查看系统级别的统计信息</li>
                    <li>管理健康挑战活动</li>
                </ul>
            </c:if>
        </div>
    </div>
</body>
</html>
