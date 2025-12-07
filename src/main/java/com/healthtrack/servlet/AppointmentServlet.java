package com.healthtrack.servlet;

import com.healthtrack.dao.AppointmentDAO;
import com.healthtrack.dao.CaregiverPatientDAO;
import com.healthtrack.dao.ProviderDAO;
import com.healthtrack.dao.UserDAO;
import com.healthtrack.model.Appointment;
import com.healthtrack.model.CaregiverPatient;
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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/appointment")
public class AppointmentServlet extends HttpServlet {
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private ProviderDAO providerDAO = new ProviderDAO();
    private UserDAO userDAO = new UserDAO();
    private CaregiverPatientDAO caregiverPatientDAO = new CaregiverPatientDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String role = user.getUserRole();

        // 检查用户角色权限
        if (!"Patient".equals(role) && !"Caregiver".equals(role) && !"Provider".equals(role)) {
            request.setAttribute("error", "您没有访问此功能的权限");
            request.getRequestDispatcher("/jsp/main.jsp").forward(request, response);
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        request.setAttribute("userRole", role);

        // 患者：只能选择自己的医生
        if ("Patient".equals(role)) {
            List<UserProvider> providers = userDAO.getUserProviders(userId);
            request.setAttribute("providers", providers);
            request.setAttribute("appointments", appointmentDAO.getAppointmentsByUserId(userId));
        }

        // 照顾者：选择要为之预约的患者，再选择该患者的医生
        if ("Caregiver".equals(role)) {
            List<CaregiverPatient> patients = caregiverPatientDAO.getPatientsByCaregiver(userId);
            request.setAttribute("patients", patients);

            Integer selectedPatientId = null;
            String pidParam = request.getParameter("patientId");
            if (pidParam != null && !pidParam.isEmpty()) {
                selectedPatientId = Integer.parseInt(pidParam);
            } else if (!patients.isEmpty()) {
                selectedPatientId = patients.get(0).getPatientId();
            }
            request.setAttribute("selectedPatientId", selectedPatientId);

            List<UserProvider> providers = selectedPatientId != null
                ? userDAO.getUserProviders(selectedPatientId)
                : new ArrayList<>();
            request.setAttribute("providers", providers);

            List<Appointment> appointments = selectedPatientId != null
                ? appointmentDAO.getAppointmentsByUserId(selectedPatientId)
                : new ArrayList<>();
            // 标记患者信息，方便前端展示
            final Integer selectedIdFinal = selectedPatientId;
            CaregiverPatient selected = patients.stream()
                .filter(cp -> cp.getPatientId() == selectedIdFinal)
                .findFirst().orElse(null);
            if (selected != null && selected.getPatient() != null) {
                for (Appointment ap : appointments) {
                    ap.setUser(selected.getPatient());
                }
            }
            request.setAttribute("appointments", appointments);
        }

        // 医生：只能查看/管理自己的预约，不允许新建
        if ("Provider".equals(role)) {
            Integer providerId = userDAO.getPrimaryProviderIdForUser(userId);
            if (providerId == null) {
                request.setAttribute("error", "未找到您的医生身份，请联系管理员绑定医生信息");
                request.getRequestDispatcher("/jsp/main.jsp").forward(request, response);
                return;
            }
            request.setAttribute("providerId", providerId);

            // 简单过滤：健康ID关键词、就诊类型
            String healthIdFilter = request.getParameter("healthIdFilter");
            String consultationFilter = request.getParameter("consultationType");

            List<Appointment> all = appointmentDAO.getAppointmentsByProvider(providerId);
            List<Appointment> filtered = all.stream().filter(ap -> {
                boolean ok = true;
                if (healthIdFilter != null && !healthIdFilter.isEmpty()) {
                    ok &= ap.getUser() != null && ap.getUser().getHealthId() != null &&
                          ap.getUser().getHealthId().contains(healthIdFilter);
                }
                if (consultationFilter != null && !consultationFilter.isEmpty()) {
                    ok &= consultationFilter.equals(ap.getConsultationType());
                }
                return ok;
            }).collect(Collectors.toList());

            request.setAttribute("appointments", filtered);
            request.setAttribute("healthIdFilter", healthIdFilter);
            request.setAttribute("consultationFilter", consultationFilter);
        }

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
        String role = ((User) session.getAttribute("user")).getUserRole();
        String action = request.getParameter("action");
        
        if ("book".equals(action)) {
            try {
                if ("Provider".equals(role)) {
                    request.setAttribute("error", "医生账号不能发起预约");
                } else {
                    int providerId = Integer.parseInt(request.getParameter("providerId"));
                    String scheduledAtStr = request.getParameter("scheduledAt");
                    String consultationType = request.getParameter("consultationType");
                    String memo = request.getParameter("memo");

                    int targetUserId = userId; // 默认患者自己
                    if ("Caregiver".equals(role)) {
                        targetUserId = Integer.parseInt(request.getParameter("patientId"));
                        if (!caregiverPatientDAO.hasActiveRelationship(userId, targetUserId)) {
                            request.setAttribute("error", "只能为已关联的患者预约");
                            doGet(request, response);
                            return;
                        }
                    }

                    // 验证医生是否是该患者的关联医生
                    boolean providerLinked = userDAO.getUserProviders(targetUserId).stream()
                        .anyMatch(up -> up.getProviderId() == providerId && "Active".equals(up.getLinkStatus()));
                    if (!providerLinked) {
                        request.setAttribute("error", "只能选择与该患者已关联的医生");
                        doGet(request, response);
                        return;
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                    java.util.Date date = sdf.parse(scheduledAtStr);
                    Timestamp scheduledAt = new Timestamp(date.getTime());
                    
                    Appointment appointment = new Appointment();
                    appointment.setProviderId(providerId);
                    appointment.setScheduledAt(scheduledAt);
                    appointment.setConsultationType(consultationType);
                    appointment.setMemo(memo);
                    
                    if (appointmentDAO.createAppointment(appointment, targetUserId)) {
                        request.setAttribute("success", "预约成功");
                    } else {
                        request.setAttribute("error", "预约失败");
                    }
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

                Appointment appointment = appointmentDAO.getAppointmentWithUser(actionId);
                if (appointment == null) {
                    request.setAttribute("error", "未找到该预约");
                } else if ("Patient".equals(role) && appointment.getUser() != null && appointment.getUser().getUserId() == userId) {
                    handleCancel(request, appointment, cancelReason);
                } else if ("Caregiver".equals(role) && appointment.getUser() != null &&
                        caregiverPatientDAO.hasActiveRelationship(userId, appointment.getUser().getUserId())) {
                    handleCancel(request, appointment, cancelReason);
                } else if ("Provider".equals(role)) {
                    Integer providerId = userDAO.getPrimaryProviderIdForUser(userId);
                    if (providerId != null && providerId == appointment.getProviderId()) {
                        handleCancel(request, appointment, cancelReason);
                    } else {
                        request.setAttribute("error", "只能处理属于自己的预约");
                    }
                } else {
                    request.setAttribute("error", "无权取消该预约");
                }
            } catch (Exception e) {
                request.setAttribute("error", "取消预约时发生错误: " + e.getMessage());
            }
        }
        
        doGet(request, response);
    }

    private void handleCancel(HttpServletRequest request, Appointment appointment, String cancelReason) {
        if (!"Scheduled".equals(appointment.getStatus())) {
            request.setAttribute("error", "该预约已无法取消");
            return;
        }
        if (appointmentDAO.cancelAppointment(appointment.getActionId(), cancelReason)) {
            request.setAttribute("success", "预约取消成功");
        } else {
            request.setAttribute("error", "取消预约失败。请确保在预约时间前至少24小时取消。");
        }
    }
}

