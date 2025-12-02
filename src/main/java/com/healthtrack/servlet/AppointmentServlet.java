package com.healthtrack.servlet;

import com.healthtrack.dao.AppointmentDAO;
import com.healthtrack.dao.ProviderDAO;
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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/appointment")
public class AppointmentServlet extends HttpServlet {
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private ProviderDAO providerDAO = new ProviderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        // 检查用户角色权限
        if (!"Patient".equals(user.getUserRole()) && !"Caregiver".equals(user.getUserRole()) && !"Provider".equals(user.getUserRole())) {
            request.setAttribute("error", "您没有访问此功能的权限");
            request.getRequestDispatcher("/jsp/main.jsp").forward(request, response);
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        List<Provider> providers = providerDAO.getAllProviders();
        List<Appointment> appointments = appointmentDAO.getAppointmentsByUserId(userId);

        request.setAttribute("providers", providers);
        request.setAttribute("appointments", appointments);
        request.setAttribute("userRole", user.getUserRole());
        request.getRequestDispatcher("/jsp/appointment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        String action = request.getParameter("action");
        
        if ("book".equals(action)) {
            try {
                int providerId = Integer.parseInt(request.getParameter("providerId"));
                String scheduledAtStr = request.getParameter("scheduledAt");
                String consultationType = request.getParameter("consultationType");
                String memo = request.getParameter("memo");
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                java.util.Date date = sdf.parse(scheduledAtStr);
                Timestamp scheduledAt = new Timestamp(date.getTime());
                
                Appointment appointment = new Appointment();
                appointment.setProviderId(providerId);
                appointment.setScheduledAt(scheduledAt);
                appointment.setConsultationType(consultationType);
                appointment.setMemo(memo);
                
                if (appointmentDAO.createAppointment(appointment, userId)) {
                    request.setAttribute("success", "预约成功");
                } else {
                    request.setAttribute("error", "预约失败");
                }
            } catch (ParseException e) {
                request.setAttribute("error", "日期格式无效");
            } catch (Exception e) {
                request.setAttribute("error", "预约时发生错误: " + e.getMessage());
            }
        } else if ("cancel".equals(action)) {
            try {
                int actionId = Integer.parseInt(request.getParameter("actionId"));
                String cancelReason = request.getParameter("cancelReason");
                
                if (appointmentDAO.cancelAppointment(actionId, cancelReason)) {
                    request.setAttribute("success", "预约取消成功");
                } else {
                    request.setAttribute("error", "取消预约失败。请确保在预约时间前至少24小时取消。");
                }
            } catch (Exception e) {
                request.setAttribute("error", "取消预约时发生错误: " + e.getMessage());
            }
        }
        
        doGet(request, response);
    }
}

