package com.healthtrack.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import com.healthtrack.dao.CaregiverPatientDAO;
import com.healthtrack.model.CaregiverPatient;
import com.healthtrack.model.User;
import java.util.List;

@WebServlet("/main")
public class MainServlet extends HttpServlet {
    private final CaregiverPatientDAO caregiverPatientDAO = new CaregiverPatientDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        User user = (User) session.getAttribute("user");

        // 若为患者，加载待处理的照顾者请求，方便主页直接处理
        if ("Patient".equals(user.getUserRole())) {
            List<CaregiverPatient> pendingCaregivers = caregiverPatientDAO.getPendingRequestsForPatient(user.getUserId());
            request.setAttribute("pendingCaregivers", pendingCaregivers);
        }

        request.getRequestDispatcher("/jsp/main.jsp").forward(request, response);
    }
}

