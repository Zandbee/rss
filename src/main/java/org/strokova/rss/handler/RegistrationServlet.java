package org.strokova.rss.handler;

import org.strokova.rss.database.FeedDbUtils;
import org.strokova.rss.util.FeedUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author vstrokova, 04.08.2016.
 */
@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_USER_PASSWORD = "userpass";
    private static final String SESSION_ATTRIBUTE_USER_ID = "userId";
    private static final String REQ_ATTR_ERROR = "error";
    private static final String REQ_ATTR_ERROR_NAME_EXISTS = "Name is already used. Please choose another name";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter(PARAM_USERNAME);
        String password = request.getParameter(PARAM_USER_PASSWORD);

        if (FeedDbUtils.getUserId(username) != null) {
            request.setAttribute(REQ_ATTR_ERROR, REQ_ATTR_ERROR_NAME_EXISTS);
            RequestDispatcher rd = request.getRequestDispatcher("registration.jsp");
            rd.include(request, response);
        } else {
            HttpSession session = request.getSession(true);
            int userId = FeedDbUtils.insertIntoUserTable(username, FeedUtils.hashPassword(password));
            session.setAttribute(SESSION_ATTRIBUTE_USER_ID, userId);
            response.sendRedirect("latest");
        }
    }
}
