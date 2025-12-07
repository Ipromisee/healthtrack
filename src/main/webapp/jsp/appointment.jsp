<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>预约服务 - 健康追踪</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>预约服务</h1>
        </header>
        
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/main">主菜单</a></li>
                <li><a href="${pageContext.request.contextPath}/appointment">预约服务</a></li>
                <li><a href="${pageContext.request.contextPath}/logout">退出登录</a></li>
            </ul>
        </nav>
        
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        
        <!-- 患者和照顾者可以创建预约 -->
        <c:if test="${userRole == 'Patient' || userRole == 'Caregiver'}">
            <div class="section">
                <h3>新建预约</h3>
                <c:if test="${userRole == 'Caregiver'}">
                    <form method="get" action="${pageContext.request.contextPath}/appointment" class="form-inline" style="gap:10px;">
                        <label for="patientSwitcher">切换患者以查看预约：</label>
                        <select id="patientSwitcher" name="patientId" onchange="this.form.submit()">
                            <c:forEach var="cp" items="${patients}">
                                <option value="${cp.patientId}" ${selectedPatientId == cp.patientId ? 'selected' : ''}>
                                    ${cp.patient.fullName} (${cp.patient.healthId})
                                </option>
                            </c:forEach>
                        </select>
                    </form>
                </c:if>
                <form method="post" action="${pageContext.request.contextPath}/appointment">
                    <input type="hidden" name="action" value="book">
                    <c:if test="${userRole == 'Caregiver'}">
                        <div class="form-group">
                            <label for="patientId">选择患者：</label>
                            <select id="patientId" name="patientId" required>
                                <c:forEach var="cp" items="${patients}">
                                    <option value="${cp.patientId}" ${selectedPatientId == cp.patientId ? 'selected' : ''}>
                                        ${cp.patient.fullName} (${cp.patient.healthId})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </c:if>
                    <div class="form-group">
                        <label for="providerId">医疗服务提供者：</label>
                        <select id="providerId" name="providerId" required>
                            <option value="">-- 请选择提供者 --</option>
                            <c:forEach var="userProvider" items="${providers}">
                                <option value="${userProvider.provider.providerId}">
                                    ${userProvider.provider.providerName} (${userProvider.provider.licenseNo})
                                    <c:if test="${userProvider.primary}">★</c:if>
                                </option>
                            </c:forEach>
                        </select>
                        <c:if test="${empty providers}">
                            <small class="form-hint">当前没有可用的关联医生，请先在账户页关联</small>
                        </c:if>
                    </div>
                    <div class="form-group">
                        <label for="scheduledAt">预约日期时间：</label>
                        <input type="datetime-local" id="scheduledAt" name="scheduledAt" required>
                    </div>
                    <div class="form-group">
                        <label for="consultationType">就诊类型：</label>
                        <select id="consultationType" name="consultationType" required>
                            <option value="InPerson">面诊</option>
                            <option value="Virtual">远程</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="memo">备注（可选）：</label>
                        <textarea id="memo" name="memo" rows="3" placeholder="请输入备注信息"></textarea>
                    </div>
                    <button type="submit">提交预约</button>
                </form>
            </div>
        </c:if>

        <!-- 医疗服务提供者可以看到不同的标题 -->
        <c:if test="${userRole == 'Provider'}">
            <div class="section">
                <h3>预约管理</h3>
                <p>您可以查看和管理患者的预约请求。</p>
                <form method="get" action="${pageContext.request.contextPath}/appointment" class="form-inline" style="gap:12px;">
                    <input type="hidden" name="filter" value="true">
                    <div class="form-group">
                        <label for="healthIdFilter">患者健康ID：</label>
                        <input type="text" id="healthIdFilter" name="healthIdFilter" value="${healthIdFilter}">
                    </div>
                    <div class="form-group">
                        <label for="consultationType">就诊类型：</label>
                        <select id="consultationType" name="consultationType">
                            <option value="">全部</option>
                            <option value="InPerson" ${consultationFilter == 'InPerson' ? 'selected' : ''}>面诊</option>
                            <option value="Virtual" ${consultationFilter == 'Virtual' ? 'selected' : ''}>远程</option>
                        </select>
                    </div>
                    <button type="submit" class="btn-small">应用筛选</button>
                </form>
            </div>
        </c:if>
        
        <div class="section">
            <h3>我的预约</h3>
            <c:if test="${not empty appointments}">
                <table>
                    <thead>
                        <tr>
                            <c:if test="${userRole == 'Provider' || userRole == 'Caregiver'}">
                                <th>患者</th>
                            </c:if>
                            <th>提供者</th>
                            <th>日期时间</th>
                            <th>类型</th>
                            <th>状态</th>
                            <th>备注</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="appointment" items="${appointments}">
                            <tr>
                                <c:if test="${userRole == 'Provider' || userRole == 'Caregiver'}">
                                    <td>
                                        <c:choose>
                                            <c:when test="${appointment.user != null}">
                                                ${appointment.user.fullName} (${appointment.user.healthId})
                                            </c:when>
                                            <c:otherwise> - </c:otherwise>
                                        </c:choose>
                                    </td>
                                </c:if>
                                <td>${appointment.provider.providerName}</td>
                                <td><fmt:formatDate value="${appointment.scheduledAt}" pattern="yyyy-MM-dd HH:mm" /></td>
                                <td>${appointment.consultationType == 'InPerson' ? '面诊' : '远程'}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${appointment.status == 'Scheduled'}">
                                            <span class="badge badge-success">已预约</span>
                                        </c:when>
                                        <c:when test="${appointment.status == 'Cancelled'}">
                                            <span class="badge badge-warning">已取消</span>
                                        </c:when>
                                        <c:otherwise>${appointment.status}</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${appointment.memo}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${appointment.status == 'Scheduled'}">
                                            <form method="post" action="${pageContext.request.contextPath}/appointment" style="display: flex; gap: 8px; align-items: center;">
                                                <input type="hidden" name="action" value="cancel">
                                                <input type="hidden" name="actionId" value="${appointment.actionId}">
                                                <input type="text" name="cancelReason" placeholder="取消原因" required style="flex: 1; padding: 8px; border: 2px solid #e0e0e0; border-radius: 6px;">
                                                <button type="submit" class="btn btn-danger">取消</button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <button class="btn btn-small" disabled>不可操作</button>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <c:if test="${empty appointments}">
                <p style="text-align: center; color: #666; padding: 20px;">暂无预约记录</p>
            </c:if>
        </div>
    </div>
</body>
</html>
