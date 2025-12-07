<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>记录搜索 - 健康追踪</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>记录搜索</h1>
        </header>
        
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/main">主菜单</a></li>
                <li><a href="${pageContext.request.contextPath}/search">记录搜索</a></li>
                <li><a href="${pageContext.request.contextPath}/logout">退出登录</a></li>
            </ul>
        </nav>
        
        <div class="section">
            <h3>
                <c:choose>
                    <c:when test="${userRole == 'Provider'}">搜索患者记录</c:when>
                    <c:when test="${userRole == 'Admin'}">系统记录搜索</c:when>
                    <c:otherwise>搜索预约记录</c:otherwise>
                </c:choose>
            </h3>
            <form method="post" action="${pageContext.request.contextPath}/search">
                <div class="form-group">
                    <label for="healthId">健康ID：</label>
                    <input type="text" id="healthId" name="healthId" value="${searchHealthId}" placeholder="请输入健康ID">
                </div>
                <div class="form-group">
                    <label for="providerId">医疗服务提供者：</label>
                    <select id="providerId" name="providerId" ${not empty lockedProviderId ? 'disabled' : ''}>
                        <option value="">-- 所有提供者 --</option>
                        <c:forEach var="provider" items="${providers}">
                            <option value="${provider.providerId}"
                                    ${searchProviderId == provider.providerId ? 'selected' : ''}
                                    ${not empty lockedProviderId && lockedProviderId != provider.providerId ? 'disabled' : ''}>
                                ${provider.providerName} (${provider.licenseNo})
                            </option>
                        </c:forEach>
                    </select>
                    <c:if test="${not empty lockedProviderId}">
                        <input type="hidden" name="providerId" value="${lockedProviderId}">
                        <small class="form-hint">医生只能查看自己的预约记录</small>
                    </c:if>
                </div>
                <div class="form-group">
                    <label for="consultationType">就诊类型：</label>
                    <select id="consultationType" name="consultationType">
                        <option value="">-- 所有类型 --</option>
                        <option value="InPerson" ${searchConsultationType == 'InPerson' ? 'selected' : ''}>面诊</option>
                        <option value="Virtual" ${searchConsultationType == 'Virtual' ? 'selected' : ''}>远程</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="startDate">开始日期：</label>
                    <input type="date" id="startDate" name="startDate" value="${searchStartDate}">
                </div>
                <div class="form-group">
                    <label for="endDate">结束日期：</label>
                    <input type="date" id="endDate" name="endDate" value="${searchEndDate}">
                </div>
                <button type="submit" style="width: 100%;">搜索</button>
            </form>
        </div>
        
        <c:if test="${not empty appointments}">
            <div class="section">
                <h3>搜索结果</h3>
                <p style="margin-bottom: 15px; color: #666;">找到 ${appointments.size()} 条记录</p>
                <table>
                    <thead>
                        <tr>
                            <th>健康ID</th>
                            <th>患者姓名</th>
                            <th>提供者</th>
                            <th>日期时间</th>
                            <th>类型</th>
                            <th>状态</th>
                            <th>备注</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="appointment" items="${appointments}">
                            <tr>
                                <td>${appointment.user.healthId}</td>
                                <td>${appointment.user.fullName}</td>
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
                                        <c:otherwise>
                                            <span class="badge badge-info">${appointment.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${appointment.memo}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
        
        <c:if test="${empty appointments && not empty searchHealthId}">
            <div class="alert alert-error">未找到匹配的预约记录</div>
        </c:if>
    </div>
</body>
</html>
