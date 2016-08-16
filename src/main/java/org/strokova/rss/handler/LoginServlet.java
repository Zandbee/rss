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
import java.io.PrintWriter;

/**
 * author: Veronika, 7/31/2016.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    public static final String SESSION_ATTR_USER_ID = "userId";

    private static final String REQ_ATTR_ERROR = "error";
    private static final String REQ_ATTR_ERROR_INCORRECT_CREDENTIALS = "Username or password is not correct";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String userName = request.getParameter("username"); //TODO: extract String const
        String userPassword = request.getParameter("userpass");

        if (FeedDbUtils.isValidUser(userName, FeedUtils.hashPassword(userPassword))) {
            HttpSession session = request.getSession(true);
            int userId = FeedDbUtils.getUserId(userName);
            session.setAttribute(SESSION_ATTR_USER_ID, userId);
            response.sendRedirect("latest");
        } else {
            request.setAttribute(REQ_ATTR_ERROR, REQ_ATTR_ERROR_INCORRECT_CREDENTIALS);
            RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
            rd.include(request, response); //TODO forward?
        }
    }
}
