package com.healthtrack.servlet;

import com.healthtrack.dao.ProviderDAO;
import com.healthtrack.dao.UserDAO;
import com.healthtrack.model.Email;
import com.healthtrack.model.Phone;
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
import java.util.List;

@WebServlet("/account")
public class AccountServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();
    private ProviderDAO providerDAO = new ProviderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        user = userDAO.getUserById(user.getUserId());
        session.setAttribute("user", user);
        
        List<Provider> allProviders = providerDAO.getAllProviders();
        List<UserProvider> userProviders = userDAO.getUserProviders(user.getUserId());
        
        request.setAttribute("allProviders", allProviders);
        request.setAttribute("userProviders", userProviders);
        request.getRequestDispatcher("/jsp/account.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");
        
        if ("updateProfile".equals(action)) {
            String fullName = request.getParameter("fullName");
            String accountStatus = request.getParameter("accountStatus");
            user.setFullName(fullName);
            user.setAccountStatus(accountStatus);
            if (userDAO.updateUser(user)) {
                session.setAttribute("user", user);
                request.setAttribute("success", "Profile updated successfully");
            } else {
                request.setAttribute("error", "Failed to update profile");
            }
        } else if ("addEmail".equals(action)) {
            String email = request.getParameter("email");
            Email emailObj = new Email();
            emailObj.setEmail(email);
            emailObj.setUserId(user.getUserId());
            emailObj.setVerified(false);
            if (userDAO.addEmail(emailObj)) {
                request.setAttribute("success", "Email added successfully");
            } else {
                request.setAttribute("error", "Failed to add email");
            }
        } else if ("deleteEmail".equals(action)) {
            String email = request.getParameter("email");
            if (userDAO.deleteEmail(email)) {
                request.setAttribute("success", "Email deleted successfully");
            } else {
                request.setAttribute("error", "Failed to delete email");
            }
        } else if ("addPhone".equals(action)) {
            String phoneNumber = request.getParameter("phoneNumber");
            Phone phone = new Phone();
            phone.setPhoneNumber(phoneNumber);
            phone.setUserId(user.getUserId());
            phone.setVerified(false);
            if (userDAO.addPhone(phone)) {
                request.setAttribute("success", "Phone added successfully");
            } else {
                request.setAttribute("error", "Failed to add phone");
            }
        } else if ("deletePhone".equals(action)) {
            String phoneNumber = request.getParameter("phoneNumber");
            if (userDAO.deletePhone(phoneNumber)) {
                request.setAttribute("success", "Phone deleted successfully");
            } else {
                request.setAttribute("error", "Failed to delete phone");
            }
        } else if ("addProvider".equals(action)) {
            int providerId = Integer.parseInt(request.getParameter("providerId"));
            boolean isPrimary = "true".equals(request.getParameter("isPrimary"));
            UserProvider userProvider = new UserProvider();
            userProvider.setUserId(user.getUserId());
            userProvider.setProviderId(providerId);
            userProvider.setPrimary(isPrimary);
            userProvider.setLinkStatus("Active");
            if (userDAO.addUserProvider(userProvider)) {
                request.setAttribute("success", "Provider linked successfully");
            } else {
                request.setAttribute("error", "Failed to link provider");
            }
        } else if ("removeProvider".equals(action)) {
            int userProviderId = Integer.parseInt(request.getParameter("userProviderId"));
            if (userDAO.removeUserProvider(userProviderId)) {
                request.setAttribute("success", "Provider unlinked successfully");
            } else {
                request.setAttribute("error", "Failed to unlink provider");
            }
        }
        
        doGet(request, response);
    }
}

