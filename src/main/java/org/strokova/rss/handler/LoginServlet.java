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
 * author: Veronika, 7/31/2016.
 */
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String userName = request.getParameter("username");
        String userPassword = request.getParameter("userpass");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (FeedDbUtils.isValidUser(userName, userPassword)) {
            RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
            HttpSession session = request.getSession(true);
            session.setAttribute("user", userName);
            response.sendRedirect("index.jsp");
            //rd.forward(req, resp);
        } else {
            out.print("<p style=\"color:red\">Username or password is not correct</p>");
            RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
            rd.include(request, response);
        }

        out.close();
    }
}
