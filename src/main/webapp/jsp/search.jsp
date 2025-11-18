<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Search Records - Health Track</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Search Records</h1>
        </header>
        
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/main">Main Menu</a></li>
                <li><a href="${pageContext.request.contextPath}/search">Search Records</a></li>
                <li><a href="${pageContext.request.contextPath}/logout">Sign Out</a></li>
            </ul>
        </nav>
        
        <div class="section">
            <h3>Search Appointments</h3>
            <form method="post" action="${pageContext.request.contextPath}/search">
                <div class="form-group">
                    <label for="healthId">Health ID:</label>
                    <input type="text" id="healthId" name="healthId" value="${searchHealthId}" placeholder="Enter Health ID">
                </div>
                <div class="form-group">
                    <label for="providerId">Provider:</label>
                    <select id="providerId" name="providerId">
                        <option value="">-- All Providers --</option>
                        <c:forEach var="provider" items="${providers}">
                            <option value="${provider.providerId}" ${searchProviderId == provider.providerId ? 'selected' : ''}>
                                ${provider.providerName} (${provider.licenseNo})
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="consultationType">Consultation Type:</label>
                    <select id="consultationType" name="consultationType">
                        <option value="">-- All Types --</option>
                        <option value="InPerson" ${searchConsultationType == 'InPerson' ? 'selected' : ''}>In Person</option>
                        <option value="Virtual" ${searchConsultationType == 'Virtual' ? 'selected' : ''}>Virtual</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="startDate">Start Date:</label>
                    <input type="date" id="startDate" name="startDate" value="${searchStartDate}">
                </div>
                <div class="form-group">
                    <label for="endDate">End Date:</label>
                    <input type="date" id="endDate" name="endDate" value="${searchEndDate}">
                </div>
                <button type="submit">Search</button>
            </form>
        </div>
        
        <c:if test="${not empty appointments}">
            <div class="section">
                <h3>Search Results</h3>
                <table>
                    <thead>
                        <tr>
                            <th>Health ID</th>
                            <th>Patient Name</th>
                            <th>Provider</th>
                            <th>Date & Time</th>
                            <th>Type</th>
                            <th>Status</th>
                            <th>Memo</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="appointment" items="${appointments}">
                            <tr>
                                <td>${appointment.user.healthId}</td>
                                <td>${appointment.user.fullName}</td>
                                <td>${appointment.provider.providerName}</td>
                                <td><fmt:formatDate value="${appointment.scheduledAt}" pattern="yyyy-MM-dd HH:mm" /></td>
                                <td>${appointment.consultationType}</td>
                                <td>${appointment.status}</td>
                                <td>${appointment.memo}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
        
        <c:if test="${empty appointments && not empty searchHealthId}">
            <div class="alert alert-error">No appointments found matching your search criteria.</div>
        </c:if>
    </div>
</body>
</html>

