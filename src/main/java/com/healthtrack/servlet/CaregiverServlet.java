package com.healthtrack.servlet;

import com.healthtrack.dao.CaregiverPatientDAO;
import com.healthtrack.dao.AppointmentDAO;
import com.healthtrack.dao.ChallengeDAO;
import com.healthtrack.dao.UserDAO;
import com.healthtrack.dao.SummaryDAO;
import com.healthtrack.model.CaregiverPatient;
import com.healthtrack.model.Appointment;
import com.healthtrack.model.ChallengeParticipant;
import com.healthtrack.model.MonthlySummary;
import com.healthtrack.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/caregiver")
public class CaregiverServlet extends HttpServlet {
    private CaregiverPatientDAO caregiverPatientDAO = new CaregiverPatientDAO();
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private ChallengeDAO challengeDAO = new ChallengeDAO();
    private SummaryDAO summaryDAO = new SummaryDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        
        // 只有照顾者可以访问此页面
        if (!"Caregiver".equals(user.getUserRole())) {
            request.setAttribute("error", "只有照顾者才能访问此功能");
            request.getRequestDispatcher("/jsp/main.jsp").forward(request, response);
            return;
        }
        
        int caregiverId = user.getUserId();
        
        // 获取关联的患者列表
        List<CaregiverPatient> patients = caregiverPatientDAO.getPatientsByCaregiver(caregiverId);
        request.setAttribute("patients", patients);
        
        // 为每个患者获取健康数据
        Map<Integer, List<Appointment>> patientAppointments = new HashMap<>();
        Map<Integer, List<ChallengeParticipant>> patientChallenges = new HashMap<>();
        Map<Integer, List<MonthlySummary>> patientSummaries = new HashMap<>();
        
        for (CaregiverPatient cp : patients) {
            int patientId = cp.getPatientId();
            
            // 获取患者的预约
            List<Appointment> appointments = appointmentDAO.getAppointmentsByUserId(patientId);
            patientAppointments.put(patientId, appointments);
            
            // 获取患者的挑战参与情况
            List<ChallengeParticipant> challenges = challengeDAO.getInvitedChallenges(patientId);
            patientChallenges.put(patientId, challenges);
            
            // 获取患者的月度汇总
            List<MonthlySummary> summaries = summaryDAO.getMonthlySummariesByUserId(patientId);
            patientSummaries.put(patientId, summaries);
        }
        
        request.setAttribute("patientAppointments", patientAppointments);
        request.setAttribute("patientChallenges", patientChallenges);
        request.setAttribute("patientSummaries", patientSummaries);
        
        request.getRequestDispatcher("/jsp/caregiver.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        
        if (!"Caregiver".equals(user.getUserRole())) {
            request.setAttribute("error", "只有照顾者才能执行此操作");
            doGet(request, response);
            return;
        }
        
        String action = request.getParameter("action");
        int caregiverId = user.getUserId();
        
        switch (action) {
            case "requestLink":
                handleRequestLink(request, caregiverId);
                break;
            case "terminateLink":
                handleTerminateLink(request, caregiverId);
                break;
            default:
                request.setAttribute("error", "未知操作");
        }
        
        doGet(request, response);
    }
    
    private void handleRequestLink(HttpServletRequest request, int caregiverId) {
        try {
            String patientHealthId = request.getParameter("patientHealthId");
            String relationship = request.getParameter("relationship");
            String notes = request.getParameter("notes");
            
            User patient = userDAO.getUserByHealthId(patientHealthId);
            
            if (patient == null) {
                request.setAttribute("error", "未找到健康ID为 " + patientHealthId + " 的用户");
                return;
            }
            
            if (!"Patient".equals(patient.getUserRole())) {
                request.setAttribute("error", "只能关联患者用户");
                return;
            }
            
            if (caregiverPatientDAO.hasActiveRelationship(caregiverId, patient.getUserId())) {
                request.setAttribute("error", "您已经与该患者建立了照顾关系");
                return;
            }
            
            if (caregiverPatientDAO.createCaregiverRequest(caregiverId, patient.getUserId(), relationship, notes)) {
                request.setAttribute("success", "照顾请求已发送，等待患者确认");
            } else {
                request.setAttribute("error", "发送请求失败");
            }
        } catch (Exception e) {
            request.setAttribute("error", "操作失败: " + e.getMessage());
        }
    }
    
    private void handleTerminateLink(HttpServletRequest request, int caregiverId) {
        try {
            int caregiverPatientId = Integer.parseInt(request.getParameter("caregiverPatientId"));
            
            if (caregiverPatientDAO.terminateRelationship(caregiverPatientId, caregiverId)) {
                request.setAttribute("success", "已解除照顾关系");
            } else {
                request.setAttribute("error", "解除关系失败");
            }
        } catch (Exception e) {
            request.setAttribute("error", "操作失败: " + e.getMessage());
        }
    }
}

