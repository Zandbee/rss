package org.strokova.rss.handler;

import static org.strokova.rss.util.RequestConstants.*;

import org.strokova.rss.database.FeedDbUtils;
import org.strokova.rss.exception.LoginRuntimeException;
import org.strokova.rss.util.FeedUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author: Veronika, 7/31/2016.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());

    private static final String REQ_ATTR_ERROR_INCORRECT_CREDENTIALS = "Username or password is not correct";
    private static final String LOGIN_EXCEPTION_MSG = "Login failed";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String userName = request.getParameter(PARAM_USERNAME);
        String userPassword = request.getParameter(PARAM_USER_PASSWORD);

        try {
            if (FeedDbUtils.isValidUser(userName, FeedUtils.hashPassword(userPassword))) {
                HttpSession session = request.getSession(true);
                Integer id = FeedDbUtils.getUserId(userName);
                if (id != null) {
                    int userId = id;
                    session.setAttribute(SESSION_ATTR_USER_ID, userId);
                    response.sendRedirect("latest");
                }
            } else {
                request.setAttribute(REQ_ATTR_ERROR, REQ_ATTR_ERROR_INCORRECT_CREDENTIALS);
                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/login.jsp");
                rd.include(request, response); //TODO forward?
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, LOGIN_EXCEPTION_MSG, e);
            throw new LoginRuntimeException(LOGIN_EXCEPTION_MSG, e);
        }
    }
}
