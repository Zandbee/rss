package org.strokova.rss.handler;

import static org.strokova.rss.util.RequestConstants.*;

import com.rometools.rome.io.FeedException;
import org.strokova.rss.database.FeedDAO;
import org.strokova.rss.exception.NewRssException;
import org.strokova.rss.exception.ValidationFailedException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author vstrokova, 03.08.2016.
 */
@WebServlet("/addRss")
public class AddRssServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(AddRssServlet.class.getName());

    private static final String NEW_RSS_EXCEPTION_MSG = "Could not add a new RSS";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(ENCODING_UTF_8);
        String rssLink = request.getParameter(PARAM_RSS_LINK);
        String rssName = request.getParameter(PARAM_RSS_NAME);

        try {
            FeedDAO.addRssForUser(rssLink, rssName, (int) request.getSession(false).getAttribute(SESSION_ATTR_USER_ID));
            response.sendRedirect("latest");
        } catch (SQLException | FeedException | ValidationFailedException e) {
            logger.log(Level.SEVERE, NEW_RSS_EXCEPTION_MSG, e);
            throw new NewRssException(NEW_RSS_EXCEPTION_MSG, e);
        }
    }
}
