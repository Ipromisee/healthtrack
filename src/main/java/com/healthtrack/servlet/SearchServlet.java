package com.healthtrack.servlet;

import com.healthtrack.dao.ProviderDAO;
import com.healthtrack.dao.SearchDAO;
import com.healthtrack.model.Appointment;
import com.healthtrack.model.Provider;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    private SearchDAO searchDAO = new SearchDAO();
    private ProviderDAO providerDAO = new ProviderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        List<Provider> providers = providerDAO.getAllProviders();
        request.setAttribute("providers", providers);
        request.getRequestDispatcher("/jsp/search.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String healthId = request.getParameter("healthId");
        String providerIdStr = request.getParameter("providerId");
        String consultationType = request.getParameter("consultationType");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        
        Integer providerId = null;
        if (providerIdStr != null && !providerIdStr.isEmpty()) {
            providerId = Integer.parseInt(providerIdStr);
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
        
        List<Provider> providers = providerDAO.getAllProviders();
        request.setAttribute("providers", providers);
        request.setAttribute("appointments", appointments);
        request.setAttribute("searchHealthId", healthId);
        request.setAttribute("searchProviderId", providerIdStr);
        request.setAttribute("searchConsultationType", consultationType);
        request.setAttribute("searchStartDate", startDateStr);
        request.setAttribute("searchEndDate", endDateStr);
        
        request.getRequestDispatcher("/jsp/search.jsp").forward(request, response);
    }
}

