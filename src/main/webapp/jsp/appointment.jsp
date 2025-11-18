<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Book Appointment - Health Track</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Book Appointment</h1>
        </header>
        
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/main">Main Menu</a></li>
                <li><a href="${pageContext.request.contextPath}/appointment">Book Appointment</a></li>
                <li><a href="${pageContext.request.contextPath}/logout">Sign Out</a></li>
            </ul>
        </nav>
        
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        
        <div class="section">
            <h3>Book New Appointment</h3>
            <form method="post" action="${pageContext.request.contextPath}/appointment">
                <input type="hidden" name="action" value="book">
                <div class="form-group">
                    <label for="providerId">Provider:</label>
                    <select id="providerId" name="providerId" required>
                        <option value="">-- Select Provider --</option>
                        <c:forEach var="provider" items="${providers}">
                            <option value="${provider.providerId}">${provider.providerName} (${provider.licenseNo})</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="scheduledAt">Date & Time:</label>
                    <input type="datetime-local" id="scheduledAt" name="scheduledAt" required>
                </div>
                <div class="form-group">
                    <label for="consultationType">Consultation Type:</label>
                    <select id="consultationType" name="consultationType" required>
                        <option value="InPerson">In Person</option>
                        <option value="Virtual">Virtual</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="memo">Memo (Optional):</label>
                    <textarea id="memo" name="memo" rows="3"></textarea>
                </div>
                <button type="submit">Book Appointment</button>
            </form>
        </div>
        
        <div class="section">
            <h3>My Appointments</h3>
            <c:if test="${not empty appointments}">
                <table>
                    <thead>
                        <tr>
                            <th>Provider</th>
                            <th>Date & Time</th>
                            <th>Type</th>
                            <th>Status</th>
                            <th>Memo</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="appointment" items="${appointments}">
                            <tr>
                                <td>${appointment.provider.providerName}</td>
                                <td><fmt:formatDate value="${appointment.scheduledAt}" pattern="yyyy-MM-dd HH:mm" /></td>
                                <td>${appointment.consultationType}</td>
                                <td>${appointment.status}</td>
                                <td>${appointment.memo}</td>
                                <td>
                                    <c:if test="${appointment.status == 'Scheduled'}">
                                        <form method="post" action="${pageContext.request.contextPath}/appointment" style="display: inline;">
                                            <input type="hidden" name="action" value="cancel">
                                            <input type="hidden" name="actionId" value="${appointment.actionId}">
                                            <input type="text" name="cancelReason" placeholder="Cancel reason" required style="width: 150px; padding: 5px;">
                                            <button type="submit" class="btn btn-danger">Cancel</button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <c:if test="${empty appointments}">
                <p>No appointments found.</p>
            </c:if>
        </div>
    </div>
</body>
</html>

