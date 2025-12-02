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
                <form method="post" action="${pageContext.request.contextPath}/appointment">
                    <input type="hidden" name="action" value="book">
                    <div class="form-group">
                        <label for="providerId">医疗服务提供者：</label>
                        <select id="providerId" name="providerId" required>
                            <option value="">-- 请选择提供者 --</option>
                            <c:forEach var="provider" items="${providers}">
                                <option value="${provider.providerId}">${provider.providerName} (${provider.licenseNo})</option>
                            </c:forEach>
                        </select>
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
            </div>
        </c:if>
        
        <div class="section">
            <h3>我的预约</h3>
            <c:if test="${not empty appointments}">
                <table>
                    <thead>
                        <tr>
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
                                    <c:if test="${appointment.status == 'Scheduled'}">
                                        <form method="post" action="${pageContext.request.contextPath}/appointment" style="display: flex; gap: 8px; align-items: center;">
                                            <input type="hidden" name="action" value="cancel">
                                            <input type="hidden" name="actionId" value="${appointment.actionId}">
                                            <input type="text" name="cancelReason" placeholder="取消原因" required style="flex: 1; padding: 8px; border: 2px solid #e0e0e0; border-radius: 6px;">
                                            <button type="submit" class="btn btn-danger">取消</button>
                                        </form>
                                    </c:if>
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
