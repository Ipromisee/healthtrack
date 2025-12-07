package com.healthtrack.servlet;

import com.healthtrack.dao.UserDAO;
import com.healthtrack.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String healthId = request.getParameter("healthId");
        
        if (healthId == null || healthId.trim().isEmpty()) {
            request.setAttribute("error", "请输入健康ID");
            request.getRequestDispatcher("/jsp/index.jsp").forward(request, response);
            return;
        }
        
        User user = userDAO.getUserByHealthId(healthId.trim());
        
        if (user != null) {
            if (!"Active".equals(user.getAccountStatus())) {
                request.setAttribute("error", "账户未激活，请等待管理员审核后再登录");
                request.getRequestDispatcher("/jsp/index.jsp").forward(request, response);
                return;
            }
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            response.sendRedirect(request.getContextPath() + "/main");
        } else {
            request.setAttribute("error", "无效的健康ID");
            request.getRequestDispatcher("/jsp/index.jsp").forward(request, response);
        }
    }
}

