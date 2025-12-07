package com.healthtrack.servlet;

import com.healthtrack.dao.ProviderDAO;
import com.healthtrack.dao.SearchDAO;
import com.healthtrack.dao.UserDAO;
import com.healthtrack.model.Appointment;
import com.healthtrack.model.Provider;
import com.healthtrack.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    private SearchDAO searchDAO = new SearchDAO();
    private ProviderDAO providerDAO = new ProviderDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        // 检查用户角色权限 - 只有Provider和Admin可以搜索
        if (!"Provider".equals(user.getUserRole()) && !"Admin".equals(user.getUserRole())) {
            request.setAttribute("error", "您没有访问此功能的权限");
            request.getRequestDispatcher("/jsp/main.jsp").forward(request, response);
            return;
        }

        if ("Provider".equals(user.getUserRole())) {
            Integer providerId = userDAO.getPrimaryProviderIdForUser(user.getUserId());
            if (providerId == null) {
                request.setAttribute("error", "未找到您的医生身份，请联系管理员绑定");
                request.getRequestDispatcher("/jsp/main.jsp").forward(request, response);
                return;
            }
            Provider self = providerDAO.getProviderById(providerId);
            request.setAttribute("providers", self != null ? Arrays.asList(self) : Collections.emptyList());
            request.setAttribute("lockedProviderId", providerId);
        } else {
            List<Provider> providers = providerDAO.getAllProviders();
            request.setAttribute("providers", providers);
        }
        request.setAttribute("userRole", user.getUserRole());
        request.getRequestDispatcher("/jsp/search.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String healthId = request.getParameter("healthId");
        String providerIdStr = request.getParameter("providerId");
        String consultationType = request.getParameter("consultationType");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        
        Integer providerId = null;
        if (providerIdStr != null && !providerIdStr.isEmpty()) {
            providerId = Integer.parseInt(providerIdStr);
        }

        // 医生只能搜索自己的患者
        if ("Provider".equals(user.getUserRole())) {
            Integer selfProviderId = userDAO.getPrimaryProviderIdForUser(user.getUserId());
            providerId = selfProviderId;
            providerIdStr = selfProviderId == null ? "" : String.valueOf(selfProviderId);
        }
        
        Date startDate = null;
        if (startDateStr != null && !startDateStr.isEmpty()) {
            startDate = Date.valueOf(startDateStr);
        }
        
        Date endDate = null;
        if (endDateStr != null && !endDateStr.isEmpty()) {
            endDate = Date.valueOf(endDateStr);
        }
        
        List<Appointment> appointments = searchDAO.searchAppointments(healthId, providerId, consultationType, startDate, endDate);
        
        if ("Provider".equals(user.getUserRole())) {
            Integer selfProviderId = userDAO.getPrimaryProviderIdForUser(user.getUserId());
            Provider self = selfProviderId == null ? null : providerDAO.getProviderById(selfProviderId);
            request.setAttribute("providers", self != null ? Arrays.asList(self) : Collections.emptyList());
            request.setAttribute("lockedProviderId", selfProviderId);
        } else {
            List<Provider> providers = providerDAO.getAllProviders();
            request.setAttribute("providers", providers);
        }
        request.setAttribute("appointments", appointments);
        request.setAttribute("searchHealthId", healthId);
        request.setAttribute("searchProviderId", providerIdStr);
        request.setAttribute("searchConsultationType", consultationType);
        request.setAttribute("searchStartDate", startDateStr);
        request.setAttribute("searchEndDate", endDateStr);
        
        request.getRequestDispatcher("/jsp/search.jsp").forward(request, response);
    }
}

