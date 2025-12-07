package com.healthtrack.servlet;

import com.healthtrack.dao.UserDAO;
import com.healthtrack.dao.ProviderDAO;
import com.healthtrack.dao.AppointmentDAO;
import com.healthtrack.dao.ChallengeDAO;
import com.healthtrack.dao.SummaryDAO;
import com.healthtrack.model.User;
import com.healthtrack.model.Provider;
import com.healthtrack.model.Challenge;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();
    private ProviderDAO providerDAO = new ProviderDAO();
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private ChallengeDAO challengeDAO = new ChallengeDAO();
    private SummaryDAO summaryDAO = new SummaryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        
        // 只有管理员可以访问此页面
        if (!"Admin".equals(user.getUserRole())) {
            request.setAttribute("error", "只有管理员才能访问此功能");
            request.getRequestDispatcher("/jsp/main.jsp").forward(request, response);
            return;
        }
        
        // 获取系统统计信息
        Map<String, Object> stats = new HashMap<>();
        
        // 用户统计
        List<User> allUsers = userDAO.getAllUsers();
        int patientCount = 0, providerCount = 0, caregiverCount = 0, adminCount = 0;
        int activeCount = 0, inactiveCount = 0, suspendedCount = 0;
        
        for (User u : allUsers) {
            switch (u.getUserRole()) {
                case "Patient": patientCount++; break;
                case "Provider": providerCount++; break;
                case "Caregiver": caregiverCount++; break;
                case "Admin": adminCount++; break;
            }
            switch (u.getAccountStatus()) {
                case "Active": activeCount++; break;
                case "Inactive": inactiveCount++; break;
                case "Suspended": suspendedCount++; break;
            }
        }
        
        stats.put("totalUsers", allUsers.size());
        stats.put("patientCount", patientCount);
        stats.put("providerCount", providerCount);
        stats.put("caregiverCount", caregiverCount);
        stats.put("adminCount", adminCount);
        stats.put("activeCount", activeCount);
        stats.put("inactiveCount", inactiveCount);
        stats.put("suspendedCount", suspendedCount);
        
        // 医疗提供者统计
        List<Provider> allProviders = providerDAO.getAllProviders();
        int verifiedProviders = 0;
        for (Provider p : allProviders) {
            if (p.isVerified()) verifiedProviders++;
        }
        stats.put("totalProviders", allProviders.size());
        stats.put("verifiedProviders", verifiedProviders);
        
        // 挑战统计
        List<Challenge> activeChallenges = challengeDAO.getAllActiveChallenges();
        stats.put("activeChallenges", activeChallenges.size());
        
        request.setAttribute("stats", stats);
        request.setAttribute("allUsers", allUsers);
        request.setAttribute("allProviders", allProviders);
        
        request.getRequestDispatcher("/jsp/admin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        if (!"Admin".equals(currentUser.getUserRole())) {
            request.setAttribute("error", "只有管理员才能执行此操作");
            doGet(request, response);
            return;
        }
        
        String action = request.getParameter("action");
        
        switch (action) {
            case "updateUserStatus":
                handleUpdateUserStatus(request);
                break;
            case "updateUserRole":
                handleUpdateUserRole(request);
                break;
            case "verifyProvider":
                handleVerifyProvider(request);
                break;
            default:
                request.setAttribute("error", "未知操作");
        }
        
        doGet(request, response);
    }
    
    private void handleUpdateUserStatus(HttpServletRequest request) {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            String newStatus = request.getParameter("newStatus");
            
            User user = userDAO.getUserById(userId);
            if (user != null) {
                user.setAccountStatus(newStatus);
                if (userDAO.updateUser(user)) {
                    request.setAttribute("success", "用户 " + user.getFullName() + " 的状态已更新为 " + newStatus);
                } else {
                    request.setAttribute("error", "状态更新失败");
                }
            } else {
                request.setAttribute("error", "未找到该用户");
            }
        } catch (Exception e) {
            request.setAttribute("error", "操作失败: " + e.getMessage());
        }
    }
    
    private void handleUpdateUserRole(HttpServletRequest request) {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            String newRole = request.getParameter("newRole");
            
            User user = userDAO.getUserById(userId);
            if (user != null) {
                user.setUserRole(newRole);
                if (userDAO.updateUser(user)) {
                    request.setAttribute("success", "用户 " + user.getFullName() + " 的角色已更新为 " + newRole);
                } else {
                    request.setAttribute("error", "角色更新失败");
                }
            } else {
                request.setAttribute("error", "未找到该用户");
            }
        } catch (Exception e) {
            request.setAttribute("error", "操作失败: " + e.getMessage());
        }
    }
    
    private void handleVerifyProvider(HttpServletRequest request) {
        try {
            int providerId = Integer.parseInt(request.getParameter("providerId"));
            
            if (providerDAO.verifyProvider(providerId)) {
                request.setAttribute("success", "医疗提供者已验证");
            } else {
                request.setAttribute("error", "验证失败");
            }
        } catch (Exception e) {
            request.setAttribute("error", "操作失败: " + e.getMessage());
        }
    }
}





