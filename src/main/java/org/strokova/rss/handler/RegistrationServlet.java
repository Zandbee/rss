package org.strokova.rss.handler;

import static org.strokova.rss.util.RequestConstants.*;

import org.strokova.rss.database.FeedDbUtils;
import org.strokova.rss.exception.RegistrationException;
import org.strokova.rss.exception.ValidationFailedException;
import org.strokova.rss.obj.User;
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
 * @author vstrokova, 04.08.2016.
 */
@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(RegistrationServlet.class.getName());

    private static final String REQ_ATTR_ERROR_NAME_EXISTS = "Name is already used. Please choose another name";
    private static final String REGISTRATION_EXCEPTION_MSG = "Registration failed";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute(REQ_ATTR_MAX_LENGTH_USERNAME, User.COLUMN_USERNAME_LENGTH);
        req.setAttribute(REQ_ATTR_MAX_LENGTH_PASSWORD, User.COLUMN_PASSWORD_LENGTH);

        req.getRequestDispatcher("/WEB-INF/jsp/registration.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(ENCODING_UTF_8);
        String username = request.getParameter(PARAM_USERNAME);
        String password = request.getParameter(PARAM_USER_PASSWORD);

        try {
            if (FeedDbUtils.getUserId(username) != null) {
                request.setAttribute(REQ_ATTR_ERROR, REQ_ATTR_ERROR_NAME_EXISTS);
                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/registration.jsp");
                rd.include(request, response);
            } else {
                HttpSession session = request.getSession(true);
                int userId = FeedDbUtils.insertIntoUserTable(username, FeedUtils.hashPassword(password));
                session.setAttribute(SESSION_ATTR_USER_ID, userId);
                response.sendRedirect("latest");
            }
        } catch (SQLException | ValidationFailedException e) {
            logger.log(Level.SEVERE, REGISTRATION_EXCEPTION_MSG, e);
            throw new RegistrationException(REGISTRATION_EXCEPTION_MSG, e);
        }
    }
}
