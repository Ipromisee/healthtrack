<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>账户信息 - 健康追踪</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>
                <c:choose>
                    <c:when test="${sessionScope.user.userRole == 'Patient'}">👤 患者账户</c:when>
                    <c:when test="${sessionScope.user.userRole == 'Provider'}">🩺 医疗服务者账户</c:when>
                    <c:when test="${sessionScope.user.userRole == 'Caregiver'}">💝 照顾者账户</c:when>
                    <c:when test="${sessionScope.user.userRole == 'Admin'}">⚙️ 管理员账户</c:when>
                    <c:otherwise>账户信息</c:otherwise>
                </c:choose>
            </h1>
        </header>
        
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/main">🏠 主菜单</a></li>
                <li><a href="${pageContext.request.contextPath}/account" class="active">👤 账户信息</a></li>
                <li><a href="${pageContext.request.contextPath}/logout">🚪 退出登录</a></li>
            </ul>
        </nav>
        
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        
        <c:if test="${not empty sessionScope.user}">
            <!-- 角色标识卡片 -->
            <div class="role-card role-${sessionScope.user.userRole}">
                <div class="role-icon">
                    <c:choose>
                        <c:when test="${sessionScope.user.userRole == 'Patient'}">👤</c:when>
                        <c:when test="${sessionScope.user.userRole == 'Provider'}">🩺</c:when>
                        <c:when test="${sessionScope.user.userRole == 'Caregiver'}">💝</c:when>
                        <c:when test="${sessionScope.user.userRole == 'Admin'}">⚙️</c:when>
                    </c:choose>
                </div>
                <div class="role-info">
                    <h3>${sessionScope.user.fullName}</h3>
                    <p>健康ID: ${sessionScope.user.healthId}</p>
                    <span class="role-badge">
                        <c:choose>
                            <c:when test="${sessionScope.user.userRole == 'Patient'}">患者</c:when>
                            <c:when test="${sessionScope.user.userRole == 'Provider'}">医疗服务提供者</c:when>
                            <c:when test="${sessionScope.user.userRole == 'Caregiver'}">照顾者</c:when>
                            <c:when test="${sessionScope.user.userRole == 'Admin'}">系统管理员</c:when>
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
            
            <!-- 个人信息编辑 -->
            <div class="section">
                <h3>📝 个人信息</h3>
                <form method="post" action="${pageContext.request.contextPath}/account">
                    <input type="hidden" name="action" value="updateProfile">
                    <div class="form-group">
                        <label>健康ID：</label>
                        <input type="text" value="${sessionScope.user.healthId}" disabled class="readonly-field">
                        <small class="form-hint">健康ID不可修改</small>
                    </div>
                    <div class="form-group">
                        <label for="fullName">姓名：</label>
                        <input type="text" id="fullName" name="fullName" value="${sessionScope.user.fullName}" required>
                    </div>
                    
                    <!-- 只有管理员可以修改账户状态 -->
                    <c:if test="${sessionScope.user.userRole == 'Admin'}">
                        <div class="admin-only-section">
                            <div class="admin-badge">🔐 管理员专属</div>
                            <div class="form-group">
                                <label for="accountStatus">账户状态：</label>
                                <select id="accountStatus" name="accountStatus">
                                    <option value="Active" ${sessionScope.user.accountStatus == 'Active' ? 'selected' : ''}>活跃</option>
                                    <option value="Inactive" ${sessionScope.user.accountStatus == 'Inactive' ? 'selected' : ''}>未激活</option>
                                    <option value="Suspended" ${sessionScope.user.accountStatus == 'Suspended' ? 'selected' : ''}>已暂停</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="userRole">用户角色：</label>
                                <select id="userRole" name="userRole">
                                    <option value="Patient" ${sessionScope.user.userRole == 'Patient' ? 'selected' : ''}>患者</option>
                                    <option value="Provider" ${sessionScope.user.userRole == 'Provider' ? 'selected' : ''}>医疗服务提供者</option>
                                    <option value="Caregiver" ${sessionScope.user.userRole == 'Caregiver' ? 'selected' : ''}>照顾者</option>
                                    <option value="Admin" ${sessionScope.user.userRole == 'Admin' ? 'selected' : ''}>管理员</option>
                                </select>
                            </div>
                        </div>
                    </c:if>
                    
                    <!-- 非管理员显示只读信息 -->
                    <c:if test="${sessionScope.user.userRole != 'Admin'}">
                        <div class="form-group">
                            <label>账户状态：</label>
                            <input type="text" value="<c:choose><c:when test='${sessionScope.user.accountStatus == "Active"}'>活跃</c:when><c:when test='${sessionScope.user.accountStatus == "Inactive"}'>未激活</c:when><c:otherwise>已暂停</c:otherwise></c:choose>" disabled class="readonly-field">
                            <small class="form-hint">账户状态由管理员管理</small>
                        </div>
                        <div class="form-group">
                            <label>用户角色：</label>
                            <input type="text" value="<c:choose><c:when test='${sessionScope.user.userRole == "Patient"}'>患者</c:when><c:when test='${sessionScope.user.userRole == "Provider"}'>医疗服务提供者</c:when><c:when test='${sessionScope.user.userRole == "Caregiver"}'>照顾者</c:when><c:otherwise>管理员</c:otherwise></c:choose>" disabled class="readonly-field">
                            <small class="form-hint">用户角色由管理员管理</small>
                        </div>
                    </c:if>
                    
                    <button type="submit">💾 保存更改</button>
                </form>
            </div>
            
            <!-- 邮箱管理 -->
            <div class="section">
                <h3>📧 邮箱地址</h3>
                <c:if test="${not empty sessionScope.user.emails}">
                    <div class="contact-list">
                        <c:forEach var="email" items="${sessionScope.user.emails}">
                            <div class="contact-item">
                                <div class="contact-info">
                                    <span class="contact-value">${email.email}</span>
                                    <c:if test="${email.verified}">
                                        <span class="badge badge-success">✓ 已验证</span>
                                    </c:if>
                                    <c:if test="${!email.verified}">
                                        <span class="badge badge-warning">○ 未验证</span>
                                    </c:if>
                                </div>
                                <form method="post" action="${pageContext.request.contextPath}/account" class="inline-form">
                                    <input type="hidden" name="action" value="deleteEmail">
                                    <input type="hidden" name="email" value="${email.email}">
                                    <button type="submit" class="btn btn-danger btn-small">删除</button>
                                </form>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
                <c:if test="${empty sessionScope.user.emails}">
                    <p class="empty-message">暂无邮箱地址</p>
                </c:if>
                
                <form method="post" action="${pageContext.request.contextPath}/account" class="add-form">
                    <input type="hidden" name="action" value="addEmail">
                    <div class="input-group">
                        <div class="form-group">
                            <input type="email" name="email" placeholder="输入新邮箱地址" required>
                        </div>
                        <button type="submit" class="btn-add">+ 添加邮箱</button>
                    </div>
                </form>
            </div>
            
            <!-- 手机号管理 -->
            <div class="section">
                <h3>📱 手机号码</h3>
                <c:if test="${not empty sessionScope.user.phone}">
                    <div class="contact-list">
                        <div class="contact-item">
                            <div class="contact-info">
                                <span class="contact-value">${sessionScope.user.phone.phoneNumber}</span>
                                <c:if test="${sessionScope.user.phone.verified}">
                                    <span class="badge badge-success">✓ 已验证</span>
                                </c:if>
                                <c:if test="${!sessionScope.user.phone.verified}">
                                    <span class="badge badge-warning">○ 未验证</span>
                                </c:if>
                            </div>
                            <form method="post" action="${pageContext.request.contextPath}/account" class="inline-form">
                                <input type="hidden" name="action" value="deletePhone">
                                <input type="hidden" name="phoneNumber" value="${sessionScope.user.phone.phoneNumber}">
                                <button type="submit" class="btn btn-danger btn-small">删除</button>
                            </form>
                        </div>
                    </div>
                </c:if>
                <c:if test="${empty sessionScope.user.phone}">
                    <p class="empty-message">暂无手机号码</p>
                </c:if>
                
                <c:if test="${empty sessionScope.user.phone}">
                    <form method="post" action="${pageContext.request.contextPath}/account" class="add-form">
                        <input type="hidden" name="action" value="addPhone">
                        <div class="input-group">
                            <div class="form-group">
                                <input type="tel" name="phoneNumber" placeholder="输入手机号码" required>
                            </div>
                            <button type="submit" class="btn-add">+ 添加手机</button>
                        </div>
                    </form>
                </c:if>
            </div>
            
            <!-- 医疗服务提供者管理 - 仅对患者和照顾者显示 -->
            <c:if test="${sessionScope.user.userRole == 'Patient' || sessionScope.user.userRole == 'Caregiver'}">
                <div class="section">
                    <h3>🏥 医疗服务提供者</h3>
                    <c:if test="${not empty userProviders}">
                        <div class="provider-list">
                            <c:forEach var="userProvider" items="${userProviders}">
                                <div class="provider-item ${userProvider.primary ? 'primary' : ''}">
                                    <div class="provider-info">
                                        <span class="provider-name">${userProvider.provider.providerName}</span>
                                        <span class="provider-license">(${userProvider.provider.licenseNo})</span>
                                        <c:if test="${userProvider.primary}">
                                            <span class="badge badge-primary">★ 主治医生</span>
                                        </c:if>
                                    </div>
                                    <form method="post" action="${pageContext.request.contextPath}/account" class="inline-form">
                                        <input type="hidden" name="action" value="removeProvider">
                                        <input type="hidden" name="userProviderId" value="${userProvider.userProviderId}">
                                        <button type="submit" class="btn btn-danger btn-small">解除关联</button>
                                    </form>
                                </div>
                            </c:forEach>
                        </div>
                    </c:if>
                    <c:if test="${empty userProviders}">
                        <p class="empty-message">暂未关联医疗服务提供者</p>
                    </c:if>
                    
                    <form method="post" action="${pageContext.request.contextPath}/account" class="add-form">
                        <input type="hidden" name="action" value="addProvider">
                        <div class="form-group">
                            <label for="providerId">选择医疗服务提供者：</label>
                            <select id="providerId" name="providerId" required>
                                <option value="">-- 请选择 --</option>
                                <c:forEach var="provider" items="${allProviders}">
                                    <option value="${provider.providerId}">
                                        ${provider.providerName} (${provider.licenseNo})
                                        <c:if test="${provider.verified}"> ✓</c:if>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group checkbox-group">
                            <label class="checkbox-label">
                                <input type="checkbox" name="isPrimary" value="true">
                                <span>设为主治医生</span>
                            </label>
                        </div>
                        <button type="submit">🔗 关联医生</button>
                    </form>
                </div>
            </c:if>
            
            <!-- 照顾者请求管理 - 仅对患者显示 -->
            <c:if test="${sessionScope.user.userRole == 'Patient'}">
                <div class="section">
                    <h3>💝 照顾者管理</h3>
                    
                    <!-- 待处理的照顾请求 -->
                    <c:if test="${not empty pendingCaregivers}">
                        <div class="subsection">
                            <h4>⏳ 待处理的照顾请求</h4>
                            <div class="caregiver-list">
                                <c:forEach var="cp" items="${pendingCaregivers}">
                                    <div class="caregiver-item pending">
                                        <div class="caregiver-info">
                                            <span class="caregiver-name">${cp.caregiver.fullName}</span>
                                            <span class="caregiver-relation">关系: ${cp.relationship}</span>
                                            <c:if test="${not empty cp.notes}">
                                                <span class="caregiver-notes">备注: ${cp.notes}</span>
                                            </c:if>
                                        </div>
                                        <div class="caregiver-actions">
                                            <form method="post" action="${pageContext.request.contextPath}/account" class="inline-form">
                                                <input type="hidden" name="action" value="approveCaregiverRequest">
                                                <input type="hidden" name="caregiverPatientId" value="${cp.caregiverPatientId}">
                                                <button type="submit" class="btn btn-success btn-small">✓ 批准</button>
                                            </form>
                                            <form method="post" action="${pageContext.request.contextPath}/account" class="inline-form">
                                                <input type="hidden" name="action" value="rejectCaregiverRequest">
                                                <input type="hidden" name="caregiverPatientId" value="${cp.caregiverPatientId}">
                                                <button type="submit" class="btn btn-danger btn-small">✗ 拒绝</button>
                                            </form>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </c:if>
                    
                    <!-- 已关联的照顾者 -->
                    <c:if test="${not empty activeCaregivers}">
                        <div class="subsection">
                            <h4>✓ 已关联的照顾者</h4>
                            <div class="caregiver-list">
                                <c:forEach var="cp" items="${activeCaregivers}">
                                    <div class="caregiver-item active">
                                        <div class="caregiver-info">
                                            <span class="caregiver-name">${cp.caregiver.fullName}</span>
                                            <span class="caregiver-relation">关系: ${cp.relationship}</span>
                                            <span class="badge badge-success">● 活跃</span>
                                        </div>
                                        <form method="post" action="${pageContext.request.contextPath}/account" class="inline-form">
                                            <input type="hidden" name="action" value="terminateCaregiverRelation">
                                            <input type="hidden" name="caregiverPatientId" value="${cp.caregiverPatientId}">
                                            <button type="submit" class="btn btn-warning btn-small">解除关系</button>
                                        </form>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </c:if>
                    
                    <c:if test="${empty pendingCaregivers && empty activeCaregivers}">
                        <p class="empty-message">暂无照顾者关联</p>
                    </c:if>
                </div>
            </c:if>
        </c:if>
    </div>
</body>
</html>
