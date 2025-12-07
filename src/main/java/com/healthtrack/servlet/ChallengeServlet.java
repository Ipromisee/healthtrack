package com.healthtrack.servlet;

import com.healthtrack.dao.CaregiverPatientDAO;
import com.healthtrack.dao.ChallengeDAO;
import com.healthtrack.dao.UserDAO;
import com.healthtrack.model.CaregiverPatient;
import com.healthtrack.model.Challenge;
import com.healthtrack.model.ChallengeParticipant;
import com.healthtrack.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/challenge")
public class ChallengeServlet extends HttpServlet {
    private ChallengeDAO challengeDAO = new ChallengeDAO();
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
        String userRole = user.getUserRole();
        int userId = user.getUserId();

        request.setAttribute("userRole", userRole);
        // 同步状态，过期挑战自动标记
        challengeDAO.refreshExpiredChallenges();

        switch (userRole) {
            case "Provider":
                // 医疗提供者可以创建和管理挑战
                List<Challenge> createdChallenges = challengeDAO.getChallengesByUserId(userId);
                request.setAttribute("createdChallenges", createdChallenges);
                
                // 获取每个挑战的参与者
                for (Challenge challenge : createdChallenges) {
                    List<ChallengeParticipant> participants = challengeDAO.getParticipantsByChallengeId(challenge.getActionId());
                    challenge.setParticipants(participants);
                }
                break;
                
            case "Patient":
                // 患者和照顾者可以查看被邀请的挑战
                List<ChallengeParticipant> allInvitations = challengeDAO.getInvitedChallenges(userId);
                
                // 分类：待处理邀请、已加入、已拒绝
                List<ChallengeParticipant> pendingInvitations = allInvitations.stream()
                    .filter(cp -> "Invited".equals(cp.getParticipantStatus()))
                    .collect(Collectors.toList());
                List<ChallengeParticipant> joinedChallenges = allInvitations.stream()
                    .filter(cp -> "Joined".equals(cp.getParticipantStatus()))
                    .collect(Collectors.toList());
                
                request.setAttribute("pendingInvitations", pendingInvitations);
                request.setAttribute("joinedChallenges", joinedChallenges);
                // 可自由加入的挑战
                request.setAttribute("joinableChallenges", challengeDAO.getJoinableChallenges());
                break;

            case "Caregiver":
                // 显示关联患者的挑战列表
                List<CaregiverPatient> patients = caregiverPatientDAO.getPatientsByCaregiver(userId);
                Map<Integer, List<ChallengeParticipant>> patientChallenges = new HashMap<>();
                for (CaregiverPatient cp : patients) {
                    patientChallenges.put(cp.getPatientId(), challengeDAO.getInvitedChallenges(cp.getPatientId()));
                }
                request.setAttribute("patients", patients);
                request.setAttribute("patientChallenges", patientChallenges);
                break;
                
            case "Admin":
                // 管理员可以查看所有活跃挑战
                List<Challenge> allChallenges = challengeDAO.getAllActiveChallenges();
                request.setAttribute("allChallenges", allChallenges);
                
                // 获取热门挑战
                List<Challenge> popularChallenges = challengeDAO.getChallengesWithMostParticipants(5);
                request.setAttribute("popularChallenges", popularChallenges);
                break;
        }

        request.getRequestDispatcher("/jsp/challenge.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        int userId = user.getUserId();
        String userRole = user.getUserRole();
        String action = request.getParameter("action");
        
        switch (action) {
            case "create":
                // 只有医疗提供者可以创建挑战
                if (!"Provider".equals(userRole)) {
                    request.setAttribute("error", "只有医疗服务提供者才能创建健康挑战");
                    break;
                }
                handleCreateChallenge(request, userId);
                break;
                
            case "addParticipant":
                // 只有医疗提供者可以邀请患者
                if (!"Provider".equals(userRole)) {
                    request.setAttribute("error", "只有医疗服务提供者才能邀请患者参与挑战");
                    break;
                }
                handleAddParticipant(request);
                break;
                
            case "acceptInvitation":
                // 患者和照顾者可以接受邀请
                if (!"Patient".equals(userRole) && !"Caregiver".equals(userRole)) {
                    request.setAttribute("error", "权限不足");
                    break;
                }
                handleAcceptInvitation(request, userId);
                break;
                
            case "declineInvitation":
                // 患者和照顾者可以拒绝邀请
                if (!"Patient".equals(userRole) && !"Caregiver".equals(userRole)) {
                    request.setAttribute("error", "权限不足");
                    break;
                }
                handleDeclineInvitation(request, userId);
                break;
                
            case "updateProgress":
                // 患者可以更新进度
                handleUpdateProgress(request, userId);
                break;
            case "joinChallenge":
                if (!"Patient".equals(userRole)) {
                    request.setAttribute("error", "只有患者可以主动加入挑战");
                    break;
                }
                handleJoinChallenge(request, userId);
                break;
            case "markToday":
                handleMarkToday(request, userId);
                break;
            case "leaveChallenge":
                handleLeaveChallenge(request, userId);
                break;
                
            default:
                request.setAttribute("error", "未知操作");
        }
        
        doGet(request, response);
    }
    
    private void handleCreateChallenge(HttpServletRequest request, int userId) {
        try {
            String goal = request.getParameter("goal");
            Date startDate = Date.valueOf(request.getParameter("startDate"));
            Date endDate = Date.valueOf(request.getParameter("endDate"));
            String status = request.getParameter("status");
            
            Challenge challenge = new Challenge();
            challenge.setGoal(goal);
            challenge.setStartDate(startDate);
            challenge.setEndDate(endDate);
            challenge.setStatus(status);
            
            if (challengeDAO.createChallenge(challenge, userId)) {
                request.setAttribute("success", "健康挑战创建成功！您可以邀请患者参与此挑战。");
            } else {
                request.setAttribute("error", "挑战创建失败，请重试");
            }
        } catch (Exception e) {
            request.setAttribute("error", "创建挑战时发生错误: " + e.getMessage());
        }
    }
    
    private void handleAddParticipant(HttpServletRequest request) {
        try {
            int actionId = Integer.parseInt(request.getParameter("actionId"));
            String healthId = request.getParameter("healthId");
            User participantUser = userDAO.getUserByHealthId(healthId);
            
            if (participantUser == null) {
                request.setAttribute("error", "未找到健康ID为 " + healthId + " 的用户");
                return;
            }
            
            if (!"Patient".equals(participantUser.getUserRole())) {
                request.setAttribute("error", "只能邀请患者参与健康挑战");
                return;
            }
            
            if (challengeDAO.addParticipant(actionId, participantUser.getUserId())) {
                request.setAttribute("success", "已向 " + participantUser.getFullName() + " 发送挑战邀请");
            } else {
                request.setAttribute("error", "邀请发送失败");
            }
        } catch (Exception e) {
            request.setAttribute("error", "添加参与者时发生错误: " + e.getMessage());
        }
    }
    
    private void handleAcceptInvitation(HttpServletRequest request, int userId) {
        try {
            int challengeParticipantId = Integer.parseInt(request.getParameter("challengeParticipantId"));
            if (challengeDAO.acceptInvitation(challengeParticipantId, userId)) {
                request.setAttribute("success", "您已成功加入该健康挑战！");
            } else {
                request.setAttribute("error", "加入挑战失败");
            }
        } catch (Exception e) {
            request.setAttribute("error", "接受邀请时发生错误: " + e.getMessage());
        }
    }
    
    private void handleDeclineInvitation(HttpServletRequest request, int userId) {
        try {
            int challengeParticipantId = Integer.parseInt(request.getParameter("challengeParticipantId"));
            if (challengeDAO.declineInvitation(challengeParticipantId, userId)) {
                request.setAttribute("success", "已拒绝该挑战邀请");
            } else {
                request.setAttribute("error", "拒绝邀请失败");
            }
        } catch (Exception e) {
            request.setAttribute("error", "拒绝邀请时发生错误: " + e.getMessage());
        }
    }
    
    private void handleUpdateProgress(HttpServletRequest request, int userId) {
        try {
            int challengeParticipantId = Integer.parseInt(request.getParameter("challengeParticipantId"));
            double progressValue = Double.parseDouble(request.getParameter("progressValue"));
            String progressUnit = request.getParameter("progressUnit");
            
            if (challengeDAO.updateProgress(challengeParticipantId, userId, progressValue, progressUnit)) {
                request.setAttribute("success", "进度更新成功！");
            } else {
                request.setAttribute("error", "进度更新失败");
            }
        } catch (Exception e) {
            request.setAttribute("error", "更新进度时发生错误: " + e.getMessage());
        }
    }

    private void handleJoinChallenge(HttpServletRequest request, int userId) {
        try {
            int actionId = Integer.parseInt(request.getParameter("actionId"));
            if (challengeDAO.joinChallengeDirectly(actionId, userId)) {
                request.setAttribute("success", "已加入该健康挑战");
            } else {
                request.setAttribute("error", "加入挑战失败");
            }
        } catch (Exception e) {
            request.setAttribute("error", "加入挑战时发生错误: " + e.getMessage());
        }
    }

    private void handleMarkToday(HttpServletRequest request, int userId) {
        try {
            int challengeParticipantId = Integer.parseInt(request.getParameter("challengeParticipantId"));
            if (challengeDAO.markTodayComplete(challengeParticipantId, userId)) {
                request.setAttribute("success", "已记录今日完成");
            } else {
                request.setAttribute("error", "记录失败");
            }
        } catch (Exception e) {
            request.setAttribute("error", "更新状态时发生错误: " + e.getMessage());
        }
    }

    private void handleLeaveChallenge(HttpServletRequest request, int userId) {
        try {
            int challengeParticipantId = Integer.parseInt(request.getParameter("challengeParticipantId"));
            if (challengeDAO.leaveChallenge(challengeParticipantId, userId)) {
                request.setAttribute("success", "已退出该挑战");
            } else {
                request.setAttribute("error", "退出挑战失败");
            }
        } catch (Exception e) {
            request.setAttribute("error", "退出挑战时发生错误: " + e.getMessage());
        }
    }
}
