package com.healthtrack.servlet;

import com.healthtrack.dao.CaregiverPatientDAO;
import com.healthtrack.dao.ProviderDAO;
import com.healthtrack.dao.UserDAO;
import com.healthtrack.model.CaregiverPatient;
import com.healthtrack.model.Email;
import com.healthtrack.model.Phone;
import com.healthtrack.model.Provider;
import com.healthtrack.model.User;
import com.healthtrack.model.UserProvider;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/account")
public class AccountServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();
    private ProviderDAO providerDAO = new ProviderDAO();
    private CaregiverPatientDAO caregiverPatientDAO = new CaregiverPatientDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        user = userDAO.getUserById(user.getUserId());
        session.setAttribute("user", user);
        
        List<Provider> allProviders = providerDAO.getAllProviders();
        List<UserProvider> userProviders = userDAO.getUserProviders(user.getUserId());
        
        request.setAttribute("allProviders", allProviders);
        request.setAttribute("userProviders", userProviders);
        
        // 如果是患者，获取照顾者相关信息
        if ("Patient".equals(user.getUserRole())) {
            List<CaregiverPatient> allCaregivers = caregiverPatientDAO.getCaregiversByPatient(user.getUserId());
            List<CaregiverPatient> pendingCaregivers = allCaregivers.stream()
                .filter(cp -> "Pending".equals(cp.getStatus()))
                .collect(Collectors.toList());
            List<CaregiverPatient> activeCaregivers = allCaregivers.stream()
                .filter(cp -> "Active".equals(cp.getStatus()))
                .collect(Collectors.toList());
            
            request.setAttribute("pendingCaregivers", pendingCaregivers);
            request.setAttribute("activeCaregivers", activeCaregivers);
        }
        
        request.getRequestDispatcher("/jsp/account.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");
        
        switch (action) {
            case "updateProfile":
                handleUpdateProfile(request, session, user);
                break;
            case "addEmail":
                handleAddEmail(request, user);
                break;
            case "deleteEmail":
                handleDeleteEmail(request);
                break;
            case "addPhone":
                handleAddPhone(request, user);
                break;
            case "deletePhone":
                handleDeletePhone(request);
                break;
            case "addProvider":
                handleAddProvider(request, user);
                break;
            case "removeProvider":
                handleRemoveProvider(request);
                break;
            case "approveCaregiverRequest":
                handleApproveCaregiverRequest(request, user);
                break;
            case "rejectCaregiverRequest":
                handleRejectCaregiverRequest(request, user);
                break;
            case "terminateCaregiverRelation":
                handleTerminateCaregiverRelation(request, user);
                break;
            default:
                request.setAttribute("error", "未知操作");
        }
        
        doGet(request, response);
    }
    
    private void handleUpdateProfile(HttpServletRequest request, HttpSession session, User user) {
        String fullName = request.getParameter("fullName");
        user.setFullName(fullName);
        
        // 只有管理员可以修改账户状态和用户角色
        if ("Admin".equals(user.getUserRole())) {
            String accountStatus = request.getParameter("accountStatus");
            String userRole = request.getParameter("userRole");
            if (accountStatus != null) {
                user.setAccountStatus(accountStatus);
            }
            if (userRole != null) {
                user.setUserRole(userRole);
            }
        }
        
        if (userDAO.updateUser(user)) {
            session.setAttribute("user", user);
            request.setAttribute("success", "资料更新成功！");
        } else {
            request.setAttribute("error", "资料更新失败，请稍后重试");
        }
    }
    
    private void handleAddEmail(HttpServletRequest request, User user) {
        String email = request.getParameter("email");
        Email emailObj = new Email();
        emailObj.setEmail(email);
        emailObj.setUserId(user.getUserId());
        emailObj.setVerified(false);
        if (userDAO.addEmail(emailObj)) {
            request.setAttribute("success", "邮箱添加成功！");
        } else {
            request.setAttribute("error", "邮箱添加失败，可能已存在该邮箱");
        }
    }
    
    private void handleDeleteEmail(HttpServletRequest request) {
        String email = request.getParameter("email");
        if (userDAO.deleteEmail(email)) {
            request.setAttribute("success", "邮箱删除成功！");
        } else {
            request.setAttribute("error", "邮箱删除失败");
        }
    }
    
    private void handleAddPhone(HttpServletRequest request, User user) {
        String phoneNumber = request.getParameter("phoneNumber");
        Phone phone = new Phone();
        phone.setPhoneNumber(phoneNumber);
        phone.setUserId(user.getUserId());
        phone.setVerified(false);
        if (userDAO.addPhone(phone)) {
            request.setAttribute("success", "手机号添加成功！");
        } else {
            request.setAttribute("error", "手机号添加失败");
        }
    }
    
    private void handleDeletePhone(HttpServletRequest request) {
        String phoneNumber = request.getParameter("phoneNumber");
        if (userDAO.deletePhone(phoneNumber)) {
            request.setAttribute("success", "手机号删除成功！");
        } else {
            request.setAttribute("error", "手机号删除失败");
        }
    }
    
    private void handleAddProvider(HttpServletRequest request, User user) {
        int providerId = Integer.parseInt(request.getParameter("providerId"));
        boolean isPrimary = "true".equals(request.getParameter("isPrimary"));
        UserProvider userProvider = new UserProvider();
        userProvider.setUserId(user.getUserId());
        userProvider.setProviderId(providerId);
        userProvider.setPrimary(isPrimary);
        userProvider.setLinkStatus("Active");
        if (userDAO.addUserProvider(userProvider)) {
            request.setAttribute("success", "医生关联成功！");
        } else {
            request.setAttribute("error", "医生关联失败");
        }
    }
    
    private void handleRemoveProvider(HttpServletRequest request) {
        int userProviderId = Integer.parseInt(request.getParameter("userProviderId"));
        if (userDAO.removeUserProvider(userProviderId)) {
            request.setAttribute("success", "已解除医生关联");
        } else {
            request.setAttribute("error", "解除关联失败");
        }
    }
    
    private void handleApproveCaregiverRequest(HttpServletRequest request, User user) {
        int caregiverPatientId = Integer.parseInt(request.getParameter("caregiverPatientId"));
        if (caregiverPatientDAO.approveRequest(caregiverPatientId, user.getUserId())) {
            request.setAttribute("success", "已批准照顾者请求！");
        } else {
            request.setAttribute("error", "批准请求失败");
        }
    }
    
    private void handleRejectCaregiverRequest(HttpServletRequest request, User user) {
        int caregiverPatientId = Integer.parseInt(request.getParameter("caregiverPatientId"));
        if (caregiverPatientDAO.rejectRequest(caregiverPatientId, user.getUserId())) {
            request.setAttribute("success", "已拒绝照顾者请求");
        } else {
            request.setAttribute("error", "拒绝请求失败");
        }
    }
    
    private void handleTerminateCaregiverRelation(HttpServletRequest request, User user) {
        int caregiverPatientId = Integer.parseInt(request.getParameter("caregiverPatientId"));
        if (caregiverPatientDAO.terminateRelationship(caregiverPatientId, user.getUserId())) {
            request.setAttribute("success", "已解除照顾关系");
        } else {
            request.setAttribute("error", "解除关系失败");
        }
    }
}
