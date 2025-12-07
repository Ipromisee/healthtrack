package com.healthtrack.servlet;

import com.healthtrack.dao.UserDAO;
import com.healthtrack.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String healthId = request.getParameter("healthId");
        String fullName = request.getParameter("fullName");
        String userRole = request.getParameter("userRole");

        if (healthId == null || healthId.trim().isEmpty() || fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("error", "请填写完整的健康ID和姓名");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        if (userDAO.getUserByHealthId(healthId.trim()) != null) {
            request.setAttribute("error", "该健康ID已存在，请直接登录或更换一个ID");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        User user = new User();
        user.setHealthId(healthId.trim());
        user.setFullName(fullName.trim());
        user.setAccountStatus("Inactive"); // 需管理员审核后启用
        user.setUserRole(userRole != null && !userRole.isEmpty() ? userRole : "Patient");

        if (userDAO.createUser(user)) {
            request.setAttribute("success", "注册申请已提交，请等待管理员审核后再登录");
        } else {
            request.setAttribute("error", "注册失败，请稍后重试");
        }

        request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
    }
}

