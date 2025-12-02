package com.healthtrack.servlet;

import com.healthtrack.dao.AppointmentDAO;
import com.healthtrack.dao.ChallengeDAO;
import com.healthtrack.dao.SearchDAO;
import com.healthtrack.dao.SummaryDAO;
import com.healthtrack.model.Challenge;
import com.healthtrack.model.MonthlySummary;
import com.healthtrack.model.User;
import javax.servlet.http.HttpSession;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/summary")
public class SummaryServlet extends HttpServlet {
    private SummaryDAO summaryDAO = new SummaryDAO();
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private ChallengeDAO challengeDAO = new ChallengeDAO();
    private SearchDAO searchDAO = new SearchDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        // 检查用户角色权限
        if (!"Patient".equals(user.getUserRole()) && !"Caregiver".equals(user.getUserRole()) && !"Admin".equals(user.getUserRole())) {
            request.setAttribute("error", "您没有访问此功能的权限");
            request.getRequestDispatcher("/jsp/main.jsp").forward(request, response);
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        String yearParam = request.getParameter("year");
        String monthParam = request.getParameter("month");

        int year = yearParam != null ? Integer.parseInt(yearParam) : 2024;
        int month = monthParam != null ? Integer.parseInt(monthParam) : 3;
        
        MonthlySummary summary = summaryDAO.getMonthlySummary(userId, year, month);
        List<MonthlySummary> allSummaries = summaryDAO.getMonthlySummariesByUserId(userId);
        
        // Statistics
        double avgSteps = summaryDAO.getAverageStepsPerMonth(userId, year);
        int minSteps = summaryDAO.getMinStepsPerMonth(userId, year);
        int maxSteps = summaryDAO.getMaxStepsPerMonth(userId, year);
        
        // Date range for appointments
        Date startDate = Date.valueOf(year + "-" + String.format("%02d", month) + "-01");
        Date endDate = Date.valueOf(year + "-" + String.format("%02d", month) + "-31");
        int totalAppointments = appointmentDAO.getTotalAppointmentsInRange(userId, startDate, endDate);
        
        // Top challenges
        List<Challenge> topChallenges = challengeDAO.getChallengesWithMostParticipants(5);
        
        // Most active users
        List<User> mostActiveUsers = searchDAO.getMostActiveUsers(10);
        
        request.setAttribute("summary", summary);
        request.setAttribute("allSummaries", allSummaries);
        request.setAttribute("avgSteps", avgSteps);
        request.setAttribute("minSteps", minSteps);
        request.setAttribute("maxSteps", maxSteps);
        request.setAttribute("totalAppointments", totalAppointments);
        request.setAttribute("topChallenges", topChallenges);
        request.setAttribute("mostActiveUsers", mostActiveUsers);
        request.setAttribute("selectedYear", year);
        request.setAttribute("selectedMonth", month);
        
        request.getRequestDispatcher("/jsp/summary.jsp").forward(request, response);
    }
}

