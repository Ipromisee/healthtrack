<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Account Info - Health Track</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>Account Information</h1>
        </header>
        
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/main">Main Menu</a></li>
                <li><a href="${pageContext.request.contextPath}/account">Account Info</a></li>
                <li><a href="${pageContext.request.contextPath}/logout">Sign Out</a></li>
            </ul>
        </nav>
        
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        
        <c:if test="${not empty sessionScope.user}">
            <div class="section">
                <h3>Personal Details</h3>
                <form method="post" action="${pageContext.request.contextPath}/account">
                    <input type="hidden" name="action" value="updateProfile">
                    <div class="form-group">
                        <label>Health ID:</label>
                        <input type="text" value="${sessionScope.user.healthId}" disabled>
                    </div>
                    <div class="form-group">
                        <label for="fullName">Full Name:</label>
                        <input type="text" id="fullName" name="fullName" value="${sessionScope.user.fullName}" required>
                    </div>
                    <div class="form-group">
                        <label for="accountStatus">Account Status:</label>
                        <select id="accountStatus" name="accountStatus">
                            <option value="Active" ${sessionScope.user.accountStatus == 'Active' ? 'selected' : ''}>Active</option>
                            <option value="Inactive" ${sessionScope.user.accountStatus == 'Inactive' ? 'selected' : ''}>Inactive</option>
                            <option value="Suspended" ${sessionScope.user.accountStatus == 'Suspended' ? 'selected' : ''}>Suspended</option>
                        </select>
                    </div>
                    <button type="submit">Update Profile</button>
                </form>
            </div>
            
            <div class="section">
                <h3>Email Addresses</h3>
                <c:forEach var="email" items="${sessionScope.user.emails}">
                    <div style="display: flex; justify-content: space-between; align-items: center; padding: 10px; background: white; margin-bottom: 10px; border-radius: 5px;">
                        <span>${email.email} 
                            <c:if test="${email.verified}">
                                <span style="color: green;">(Verified)</span>
                            </c:if>
                            <c:if test="${!email.verified}">
                                <span style="color: orange;">(Unverified)</span>
                            </c:if>
                        </span>
                        <form method="post" action="${pageContext.request.contextPath}/account" style="display: inline;">
                            <input type="hidden" name="action" value="deleteEmail">
                            <input type="hidden" name="email" value="${email.email}">
                            <button type="submit" class="btn btn-danger">Delete</button>
                        </form>
                    </div>
                </c:forEach>
                
                <form method="post" action="${pageContext.request.contextPath}/account" style="margin-top: 15px;">
                    <input type="hidden" name="action" value="addEmail">
                    <div class="form-group">
                        <input type="email" name="email" placeholder="Enter email address" required>
                    </div>
                    <button type="submit">Add Email</button>
                </form>
            </div>
            
            <div class="section">
                <h3>Phone Number</h3>
                <c:if test="${not empty sessionScope.user.phone}">
                    <div style="display: flex; justify-content: space-between; align-items: center; padding: 10px; background: white; margin-bottom: 10px; border-radius: 5px;">
                        <span>${sessionScope.user.phone.phoneNumber}
                            <c:if test="${sessionScope.user.phone.verified}">
                                <span style="color: green;">(Verified)</span>
                            </c:if>
                            <c:if test="${!sessionScope.user.phone.verified}">
                                <span style="color: orange;">(Unverified)</span>
                            </c:if>
                        </span>
                        <form method="post" action="${pageContext.request.contextPath}/account" style="display: inline;">
                            <input type="hidden" name="action" value="deletePhone">
                            <input type="hidden" name="phoneNumber" value="${sessionScope.user.phone.phoneNumber}">
                            <button type="submit" class="btn btn-danger">Delete</button>
                        </form>
                    </div>
                </c:if>
                <c:if test="${empty sessionScope.user.phone}">
                    <p style="margin-bottom: 10px;">No phone number registered</p>
                </c:if>
                
                <form method="post" action="${pageContext.request.contextPath}/account" style="margin-top: 15px;">
                    <input type="hidden" name="action" value="addPhone">
                    <div class="form-group">
                        <input type="tel" name="phoneNumber" placeholder="Enter phone number" required>
                    </div>
                    <button type="submit">Add Phone</button>
                </form>
            </div>
            
            <div class="section">
                <h3>Healthcare Providers</h3>
                <c:forEach var="userProvider" items="${userProviders}">
                    <div style="display: flex; justify-content: space-between; align-items: center; padding: 10px; background: white; margin-bottom: 10px; border-radius: 5px;">
                        <span>
                            ${userProvider.provider.providerName} (${userProvider.provider.licenseNo})
                            <c:if test="${userProvider.primary}">
                                <span style="color: green; font-weight: bold;">- Primary Provider</span>
                            </c:if>
                        </span>
                        <form method="post" action="${pageContext.request.contextPath}/account" style="display: inline;">
                            <input type="hidden" name="action" value="removeProvider">
                            <input type="hidden" name="userProviderId" value="${userProvider.userProviderId}">
                            <button type="submit" class="btn btn-danger">Remove</button>
                        </form>
                    </div>
                </c:forEach>
                
                <form method="post" action="${pageContext.request.contextPath}/account" style="margin-top: 15px;">
                    <input type="hidden" name="action" value="addProvider">
                    <div class="form-group">
                        <label for="providerId">Select Provider:</label>
                        <select id="providerId" name="providerId" required>
                            <option value="">-- Select Provider --</option>
                            <c:forEach var="provider" items="${allProviders}">
                                <option value="${provider.providerId}">${provider.providerName} (${provider.licenseNo})</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>
                            <input type="checkbox" name="isPrimary" value="true">
                            Set as Primary Provider
                        </label>
                    </div>
                    <button type="submit">Link Provider</button>
                </form>
            </div>
        </c:if>
    </div>
</body>
</html>

