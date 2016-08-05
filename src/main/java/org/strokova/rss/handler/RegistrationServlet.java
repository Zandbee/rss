package org.strokova.rss.handler;

import org.strokova.rss.database.FeedDbUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author vstrokova, 04.08.2016.
 */
public class RegistrationServlet extends HttpServlet {
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_USER_PASSWORD = "userpass";
    private static final String SESSION_ATTRIBUTE_USER_ID = "userId";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter(PARAM_USERNAME);
        String password = request.getParameter(PARAM_USER_PASSWORD);

        if (FeedDbUtils.getUserId(username) > -1) { // TODO how check user found?
            PrintWriter out = response.getWriter();
            out.print("<p style=\"color:red\">Name is already used. Please choose another name</p>");
            RequestDispatcher rd = request.getRequestDispatcher("registration.jsp");
            rd.include(request, response);
            out.close();
        } else {
            HttpSession session = request.getSession(true);
            int userId = FeedDbUtils.insertIntoUserTable(username, password);
            session.setAttribute(SESSION_ATTRIBUTE_USER_ID, userId);
            response.sendRedirect("latest.jsp");
        }
    }
}
