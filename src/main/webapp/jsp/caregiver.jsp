<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>患者监护 - 健康追踪</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>💝 患者监护中心</h1>
            <h2>关爱您的家人和朋友</h2>
        </header>
        
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/main">🏠 主菜单</a></li>
                <li><a href="${pageContext.request.contextPath}/caregiver" class="active">💝 患者监护</a></li>
                <li><a href="${pageContext.request.contextPath}/account">👤 账户信息</a></li>
                <li><a href="${pageContext.request.contextPath}/logout">🚪 退出登录</a></li>
            </ul>
        </nav>
        
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        
        <!-- 添加新患者 -->
        <div class="section">
            <h3>🔗 关联新患者</h3>
            <p class="section-desc">输入患者的健康ID来发送照顾请求，患者确认后您即可监护其健康状况。</p>
            <form method="post" action="${pageContext.request.contextPath}/caregiver">
                <input type="hidden" name="action" value="requestLink">
                <div class="form-row">
                    <div class="form-group">
                        <label for="patientHealthId">患者健康ID：</label>
                        <input type="text" id="patientHealthId" name="patientHealthId" 
                               placeholder="例如: HT001" required>
                    </div>
                    <div class="form-group">
                        <label for="relationship">关系：</label>
                        <select id="relationship" name="relationship" required>
                            <option value="配偶">配偶</option>
                            <option value="父母">父母</option>
                            <option value="子女">子女</option>
                            <option value="兄弟姐妹">兄弟姐妹</option>
                            <option value="亲属">其他亲属</option>
                            <option value="朋友">朋友</option>
                            <option value="其他">其他</option>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label for="notes">备注说明：</label>
                    <textarea id="notes" name="notes" rows="2" 
                              placeholder="简要说明照顾情况（可选）"></textarea>
                </div>
                <button type="submit">📨 发送照顾请求</button>
            </form>
        </div>
        
        <!-- 我监护的患者 -->
        <div class="section">
            <h3>👥 我监护的患者</h3>
            <c:if test="${not empty patients}">
                <div class="patient-grid">
                    <c:forEach var="cp" items="${patients}">
                        <div class="patient-card">
                            <div class="patient-header">
                                <div class="patient-avatar">👤</div>
                                <div class="patient-basic">
                                    <h4>${cp.patient.fullName}</h4>
                                    <p class="patient-id">ID: ${cp.patient.healthId}</p>
                                    <span class="badge badge-info">${cp.relationship}</span>
                                </div>
                                <form method="post" action="${pageContext.request.contextPath}/caregiver" class="inline-form">
                                    <input type="hidden" name="action" value="terminateLink">
                                    <input type="hidden" name="caregiverPatientId" value="${cp.caregiverPatientId}">
                                    <button type="submit" class="btn btn-warning btn-small" 
                                            onclick="return confirm('确定要解除与该患者的照顾关系吗？')">
                                        解除关系
                                    </button>
                                </form>
                            </div>
                            
                            <!-- 健康状态概览 -->
                            <div class="health-overview">
                                <div class="health-stat">
                                    <span class="stat-icon">📅</span>
                                    <span class="stat-label">近期预约</span>
                                    <span class="stat-value">
                                        <c:choose>
                                            <c:when test="${not empty patientAppointments[cp.patientId]}">
                                                ${patientAppointments[cp.patientId].size()} 个
                                            </c:when>
                                            <c:otherwise>无</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                <div class="health-stat">
                                    <span class="stat-icon">🎯</span>
                                    <span class="stat-label">参与挑战</span>
                                    <span class="stat-value">
                                        <c:choose>
                                            <c:when test="${not empty patientChallenges[cp.patientId]}">
                                                ${patientChallenges[cp.patientId].size()} 个
                                            </c:when>
                                            <c:otherwise>无</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </div>
                            
                            <!-- 预约详情 -->
                            <c:if test="${not empty patientAppointments[cp.patientId]}">
                                <div class="patient-detail-section">
                                    <h5>📅 预约记录</h5>
                                    <ul class="detail-list">
                                        <c:forEach var="apt" items="${patientAppointments[cp.patientId]}" end="2">
                                            <li>
                                                <span class="apt-date">
                                                    <fmt:formatDate value="${apt.scheduledAt}" pattern="MM月dd日 HH:mm" />
                                                </span>
                                                <span class="apt-provider">${apt.provider.providerName}</span>
                                                <span class="badge ${apt.status == 'Scheduled' ? 'badge-success' : 'badge-warning'}">
                                                    ${apt.status == 'Scheduled' ? '已预约' : '已取消'}
                                                </span>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </c:if>
                            
                            <!-- 挑战详情 -->
                            <c:if test="${not empty patientChallenges[cp.patientId]}">
                                <div class="patient-detail-section">
                                    <h5>🎯 健康挑战</h5>
                                    <ul class="detail-list">
                                        <c:forEach var="challenge" items="${patientChallenges[cp.patientId]}" end="2">
                                            <li>
                                                <span class="challenge-goal">${challenge.challenge.goal}</span>
                                                <span class="badge ${challenge.participantStatus == 'Joined' ? 'badge-success' : 'badge-warning'}">
                                                    ${challenge.participantStatus == 'Joined' ? '进行中' : '待接受'}
                                                </span>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </c:if>
                            
                            <!-- 月度汇总 -->
                            <c:if test="${not empty patientSummaries[cp.patientId]}">
                                <div class="patient-detail-section">
                                    <h5>📊 月度汇总</h5>
                                    <c:forEach var="summary" items="${patientSummaries[cp.patientId]}" end="0">
                                        <div class="summary-stats">
                                            <div class="mini-stat">
                                                <span class="mini-stat-value">${summary.totalSteps}</span>
                                                <span class="mini-stat-label">总步数</span>
                                            </div>
                                            <div class="mini-stat">
                                                <span class="mini-stat-value">${summary.totalAppointments}</span>
                                                <span class="mini-stat-label">预约次数</span>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
            
            <c:if test="${empty patients}">
                <div class="empty-state">
                    <div class="empty-icon">💝</div>
                    <p class="empty-message">您还没有关联任何患者</p>
                    <p class="empty-hint">使用上方表单发送照顾请求，患者确认后即可开始监护其健康状况。</p>
                </div>
            </c:if>
        </div>
    </div>
</body>
</html>


