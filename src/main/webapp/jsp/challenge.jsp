<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create Challenge - Health Track</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Create Wellness Challenge</h1>
        </header>
        
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/main">Main Menu</a></li>
                <li><a href="${pageContext.request.contextPath}/challenge">Create Challenge</a></li>
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
            <h3>Create New Challenge</h3>
            <form method="post" action="${pageContext.request.contextPath}/challenge">
                <input type="hidden" name="action" value="create">
                <div class="form-group">
                    <label for="goal">Goal:</label>
                    <textarea id="goal" name="goal" rows="3" required placeholder="Describe the challenge goal"></textarea>
                </div>
                <div class="form-group">
                    <label for="startDate">Start Date:</label>
                    <input type="date" id="startDate" name="startDate" required>
                </div>
                <div class="form-group">
                    <label for="endDate">End Date:</label>
                    <input type="date" id="endDate" name="endDate" required>
                </div>
                <div class="form-group">
                    <label for="status">Status:</label>
                    <select id="status" name="status" required>
                        <option value="Draft">Draft</option>
                        <option value="Active">Active</option>
                    </select>
                </div>
                <button type="submit">Create Challenge</button>
            </form>
        </div>
        
        <div class="section">
            <h3>Add Participant</h3>
            <form method="post" action="${pageContext.request.contextPath}/challenge">
                <input type="hidden" name="action" value="addParticipant">
                <div class="form-group">
                    <label for="actionId">Challenge:</label>
                    <select id="actionId" name="actionId" required>
                        <option value="">-- Select Challenge --</option>
                        <c:forEach var="challenge" items="${challenges}">
                            <option value="${challenge.actionId}">${challenge.goal} (${challenge.status})</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="healthId">Participant Health ID:</label>
                    <input type="text" id="healthId" name="healthId" required placeholder="Enter Health ID">
                </div>
                <button type="submit">Add Participant</button>
            </form>
        </div>
        
        <div class="section">
            <h3>My Challenges</h3>
            <c:if test="${not empty challenges}">
                <table>
                    <thead>
                        <tr>
                            <th>Goal</th>
                            <th>Start Date</th>
                            <th>End Date</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="challenge" items="${challenges}">
                            <tr>
                                <td>${challenge.goal}</td>
                                <td><fmt:formatDate value="${challenge.startDate}" pattern="yyyy-MM-dd" /></td>
                                <td><fmt:formatDate value="${challenge.endDate}" pattern="yyyy-MM-dd" /></td>
                                <td>${challenge.status}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <c:if test="${empty challenges}">
                <p>No challenges found.</p>
            </c:if>
        </div>
    </div>
</body>
</html>

