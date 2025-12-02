<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>健康挑战 - 健康追踪</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>
                <c:choose>
                    <c:when test="${userRole == 'Provider'}">🩺 健康挑战管理</c:when>
                    <c:when test="${userRole == 'Patient'}">🎯 我的健康挑战</c:when>
                    <c:when test="${userRole == 'Caregiver'}">💝 健康挑战监护</c:when>
                    <c:when test="${userRole == 'Admin'}">⚙️ 挑战系统管理</c:when>
                    <c:otherwise>健康挑战</c:otherwise>
                </c:choose>
            </h1>
        </header>
        
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/main">🏠 主菜单</a></li>
                <li><a href="${pageContext.request.contextPath}/challenge" class="active">🎯 健康挑战</a></li>
                <li><a href="${pageContext.request.contextPath}/logout">🚪 退出登录</a></li>
            </ul>
        </nav>
        
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        
        <!-- ==================== 医疗提供者视图 ==================== -->
        <c:if test="${userRole == 'Provider'}">
            <!-- 创建新挑战 -->
            <div class="section">
                <h3>📝 创建新健康挑战</h3>
                <p class="section-desc">为您的患者创建健康挑战，帮助他们改善生活方式。</p>
                <form method="post" action="${pageContext.request.contextPath}/challenge">
                    <input type="hidden" name="action" value="create">
                    <div class="form-group">
                        <label for="goal">挑战目标：</label>
                        <textarea id="goal" name="goal" rows="3" required 
                            placeholder="请描述挑战目标，例如：每天步行10000步，持续30天"></textarea>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label for="startDate">开始日期：</label>
                            <input type="date" id="startDate" name="startDate" required>
                        </div>
                        <div class="form-group">
                            <label for="endDate">结束日期：</label>
                            <input type="date" id="endDate" name="endDate" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="status">初始状态：</label>
                        <select id="status" name="status" required>
                            <option value="Draft">草稿（稍后发布）</option>
                            <option value="Active">立即激活</option>
                        </select>
                    </div>
                    <button type="submit">🎯 创建挑战</button>
                </form>
            </div>
            
            <!-- 我创建的挑战 -->
            <div class="section">
                <h3>📋 我创建的挑战</h3>
                <c:if test="${not empty createdChallenges}">
                    <c:forEach var="challenge" items="${createdChallenges}">
                        <div class="challenge-card">
                            <div class="challenge-header">
                                <span class="challenge-goal">${challenge.goal}</span>
                                <span class="badge ${challenge.status == 'Active' ? 'badge-success' : challenge.status == 'Draft' ? 'badge-warning' : 'badge-info'}">
                                    <c:choose>
                                        <c:when test="${challenge.status == 'Draft'}">草稿</c:when>
                                        <c:when test="${challenge.status == 'Active'}">进行中</c:when>
                                        <c:when test="${challenge.status == 'Completed'}">已完成</c:when>
                                        <c:when test="${challenge.status == 'Cancelled'}">已取消</c:when>
                                        <c:when test="${challenge.status == 'Expired'}">已过期</c:when>
                                        <c:otherwise>${challenge.status}</c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                            <div class="challenge-dates">
                                📅 <fmt:formatDate value="${challenge.startDate}" pattern="yyyy年MM月dd日" /> 
                                至 <fmt:formatDate value="${challenge.endDate}" pattern="yyyy年MM月dd日" />
                            </div>
                            
                            <!-- 参与者列表 -->
                            <c:if test="${not empty challenge.participants}">
                                <div class="participants-section">
                                    <h4>👥 参与者 (${challenge.participants.size()})</h4>
                                    <div class="participants-list">
                                        <c:forEach var="participant" items="${challenge.participants}">
                                            <div class="participant-item">
                                                <span class="participant-name">${participant.user.fullName}</span>
                                                <span class="participant-health-id">(${participant.user.healthId})</span>
                                                <span class="badge ${participant.participantStatus == 'Joined' ? 'badge-success' : participant.participantStatus == 'Invited' ? 'badge-warning' : 'badge-info'}">
                                                    <c:choose>
                                                        <c:when test="${participant.participantStatus == 'Invited'}">待接受</c:when>
                                                        <c:when test="${participant.participantStatus == 'Joined'}">已加入</c:when>
                                                        <c:when test="${participant.participantStatus == 'Declined'}">已拒绝</c:when>
                                                        <c:otherwise>${participant.participantStatus}</c:otherwise>
                                                    </c:choose>
                                                </span>
                                                <c:if test="${participant.participantStatus == 'Joined' && participant.progressValue != null}">
                                                    <span class="participant-progress">
                                                        进度: ${participant.progressValue} ${participant.progressUnit}
                                                    </span>
                                                </c:if>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:if>
                            
                            <!-- 邀请患者表单 -->
                            <form method="post" action="${pageContext.request.contextPath}/challenge" class="invite-form">
                                <input type="hidden" name="action" value="addParticipant">
                                <input type="hidden" name="actionId" value="${challenge.actionId}">
                                <div class="input-group">
                                    <div class="form-group">
                                        <input type="text" name="healthId" placeholder="输入患者健康ID" required>
                                    </div>
                                    <button type="submit" class="btn-invite">📨 邀请患者</button>
                                </div>
                            </form>
                        </div>
                    </c:forEach>
                </c:if>
                <c:if test="${empty createdChallenges}">
                    <p class="empty-message">您还没有创建任何健康挑战</p>
                </c:if>
            </div>
        </c:if>
        
        <!-- ==================== 患者/照顾者视图 ==================== -->
        <c:if test="${userRole == 'Patient' || userRole == 'Caregiver'}">
            <!-- 待处理的邀请 -->
            <c:if test="${not empty pendingInvitations}">
                <div class="section pending-section">
                    <h3>📬 待处理的挑战邀请</h3>
                    <div class="invitation-list">
                        <c:forEach var="invitation" items="${pendingInvitations}">
                            <div class="invitation-card">
                                <div class="invitation-header">
                                    <span class="challenge-goal">${invitation.challenge.goal}</span>
                                    <span class="badge badge-warning">待接受</span>
                                </div>
                                <div class="invitation-info">
                                    <p>👨‍⚕️ 发起者: ${invitation.challenge.creatorName}</p>
                                    <p>📅 时间: <fmt:formatDate value="${invitation.challenge.startDate}" pattern="yyyy-MM-dd" /> 
                                       至 <fmt:formatDate value="${invitation.challenge.endDate}" pattern="yyyy-MM-dd" /></p>
                                </div>
                                <div class="invitation-actions">
                                    <form method="post" action="${pageContext.request.contextPath}/challenge" class="inline-form">
                                        <input type="hidden" name="action" value="acceptInvitation">
                                        <input type="hidden" name="challengeParticipantId" value="${invitation.challengeParticipantId}">
                                        <button type="submit" class="btn btn-success">✓ 接受挑战</button>
                                    </form>
                                    <form method="post" action="${pageContext.request.contextPath}/challenge" class="inline-form">
                                        <input type="hidden" name="action" value="declineInvitation">
                                        <input type="hidden" name="challengeParticipantId" value="${invitation.challengeParticipantId}">
                                        <button type="submit" class="btn btn-danger">✗ 拒绝</button>
                                    </form>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
            
            <!-- 已加入的挑战 -->
            <div class="section">
                <h3>🎯 我参与的挑战</h3>
                <c:if test="${not empty joinedChallenges}">
                    <div class="challenge-grid">
                        <c:forEach var="participation" items="${joinedChallenges}">
                            <div class="challenge-card joined">
                                <div class="challenge-header">
                                    <span class="challenge-goal">${participation.challenge.goal}</span>
                                    <span class="badge badge-success">已加入</span>
                                </div>
                                <div class="challenge-meta">
                                    <p>👨‍⚕️ 发起者: ${participation.challenge.creatorName}</p>
                                    <p>📅 <fmt:formatDate value="${participation.challenge.startDate}" pattern="yyyy-MM-dd" /> 
                                       至 <fmt:formatDate value="${participation.challenge.endDate}" pattern="yyyy-MM-dd" /></p>
                                </div>
                                
                                <!-- 进度更新 -->
                                <div class="progress-section">
                                    <h4>📊 我的进度</h4>
                                    <c:if test="${participation.progressValue != null}">
                                        <p class="current-progress">
                                            当前: ${participation.progressValue} ${participation.progressUnit}
                                        </p>
                                    </c:if>
                                    <form method="post" action="${pageContext.request.contextPath}/challenge" class="progress-form">
                                        <input type="hidden" name="action" value="updateProgress">
                                        <input type="hidden" name="challengeParticipantId" value="${participation.challengeParticipantId}">
                                        <div class="input-group">
                                            <input type="number" name="progressValue" step="0.01" placeholder="数值" required>
                                            <input type="text" name="progressUnit" placeholder="单位(如:步)" required>
                                            <button type="submit" class="btn-small">更新</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
                <c:if test="${empty joinedChallenges}">
                    <div class="empty-state">
                        <p class="empty-message">您还没有参与任何健康挑战</p>
                        <p class="empty-hint">当医疗服务提供者邀请您参与挑战时，您会在这里看到通知。</p>
                    </div>
                </c:if>
            </div>
        </c:if>
        
        <!-- ==================== 管理员视图 ==================== -->
        <c:if test="${userRole == 'Admin'}">
            <!-- 热门挑战 -->
            <c:if test="${not empty popularChallenges}">
                <div class="section">
                    <h3>🔥 热门挑战</h3>
                    <table>
                        <thead>
                            <tr>
                                <th>目标</th>
                                <th>创建者</th>
                                <th>参与人数</th>
                                <th>状态</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="challenge" items="${popularChallenges}">
                                <tr>
                                    <td>${challenge.goal}</td>
                                    <td>${challenge.creatorName}</td>
                                    <td>${challenge.participantCount} 人</td>
                                    <td>
                                        <span class="badge ${challenge.status == 'Active' ? 'badge-success' : 'badge-info'}">
                                            ${challenge.status}
                                        </span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
            
            <!-- 所有活跃挑战 -->
            <div class="section">
                <h3>📋 所有活跃挑战</h3>
                <c:if test="${not empty allChallenges}">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>目标</th>
                                <th>创建者</th>
                                <th>开始日期</th>
                                <th>结束日期</th>
                                <th>状态</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="challenge" items="${allChallenges}">
                                <tr>
                                    <td>${challenge.actionId}</td>
                                    <td>${challenge.goal}</td>
                                    <td>${challenge.creatorName}</td>
                                    <td><fmt:formatDate value="${challenge.startDate}" pattern="yyyy-MM-dd" /></td>
                                    <td><fmt:formatDate value="${challenge.endDate}" pattern="yyyy-MM-dd" /></td>
                                    <td>
                                        <span class="badge ${challenge.status == 'Active' ? 'badge-success' : challenge.status == 'Draft' ? 'badge-warning' : 'badge-info'}">
                                            <c:choose>
                                                <c:when test="${challenge.status == 'Draft'}">草稿</c:when>
                                                <c:when test="${challenge.status == 'Active'}">进行中</c:when>
                                                <c:otherwise>${challenge.status}</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
                <c:if test="${empty allChallenges}">
                    <p class="empty-message">暂无活跃的健康挑战</p>
                </c:if>
            </div>
        </c:if>
    </div>
</body>
</html>
