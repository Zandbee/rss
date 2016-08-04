package org.strokova.rss.handler;

import org.strokova.rss.database.FeedDbUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author vstrokova, 04.08.2016.
 */
public class RegistrationServlet extends HttpServlet {
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_USER_PASSWORD = "userpass";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter(PARAM_USERNAME);
        String password = request.getParameter(PARAM_USER_PASSWORD);

        if (FeedDbUtils.getUserId(username) > -1) { // TODO how check not found?
            PrintWriter out = response.getWriter();
            out.print("<p style=\"color:red\">Name is already used. Please provide another name</p>");
            RequestDispatcher rd = request.getRequestDispatcher("registration.jsp");
            rd.forward(request, response);
            out.close();
        } else {

        }
    }
}
