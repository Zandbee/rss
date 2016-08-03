package org.strokova.rss.handler;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.strokova.rss.database.FeedDbUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vstrokova on 03.08.2016.
 */
@WebServlet
public class AddRssServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(AddRssServlet.class.getName());

    private static final String PARAM_RSS_LINK = "rss_link";
    private static final String PARAM_RSS_NAME = "rss_name";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String rssLink = request.getParameter(PARAM_RSS_LINK);
        String rssName = request.getParameter(PARAM_RSS_NAME);


        //add to feed_item (bulk insert)
        //add to subscription
        //add read status?
        //check if not xml on url
        try {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(new URL(rssLink)));
            List<SyndEntry> feedItems = feed.getEntries();

            //add to feed if not exists
            FeedDbUtils.insertRssIntoFeed(rssLink, rssName);

            /*for (SyndEntry item : feedItems) {
                logger.info(item.getUri()); //guid
                logger.info(item.getTitle()); //title
                logger.info(item.getDescription().getValue()); //description
                logger.info(item.getPublishedDate().toString()); //pubDate
                logger.info(item.getLink()); //link
            }*/

            response.sendRedirect("latest.jsp");
        } catch (FeedException e) {
            logger.log(Level.SEVERE, "Error processing feed", e);
        }

    }
}
