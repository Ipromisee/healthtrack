package com.healthtrack.servlet;

import com.healthtrack.dao.ChallengeDAO;
import com.healthtrack.dao.UserDAO;
import com.healthtrack.model.Challenge;
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

@WebServlet("/challenge")
public class ChallengeServlet extends HttpServlet {
    private ChallengeDAO challengeDAO = new ChallengeDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        List<Challenge> challenges = challengeDAO.getChallengesByUserId(userId);
        
        request.setAttribute("challenges", challenges);
        request.getRequestDispatcher("/jsp/challenge.jsp").forward(request, response);
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
        
        if ("create".equals(action)) {
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
                    request.setAttribute("success", "Challenge created successfully");
                } else {
                    request.setAttribute("error", "Failed to create challenge");
                }
            } catch (Exception e) {
                request.setAttribute("error", "Error creating challenge: " + e.getMessage());
            }
        } else if ("addParticipant".equals(action)) {
            try {
                int actionId = Integer.parseInt(request.getParameter("actionId"));
                String healthId = request.getParameter("healthId");
                User participantUser = userDAO.getUserByHealthId(healthId);
                
                if (participantUser != null) {
                    if (challengeDAO.addParticipant(actionId, participantUser.getUserId())) {
                        request.setAttribute("success", "Participant added successfully");
                    } else {
                        request.setAttribute("error", "Failed to add participant");
                    }
                } else {
                    request.setAttribute("error", "User not found with Health ID: " + healthId);
                }
            } catch (Exception e) {
                request.setAttribute("error", "Error adding participant: " + e.getMessage());
            }
        }
        
        doGet(request, response);
    }
}

