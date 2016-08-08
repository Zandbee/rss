package org.strokova.rss.database;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.strokova.rss.obj.FeedItem;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author vstrokova, 04.08.2016.
 */
public class FeedDAO {
    private static final Logger logger = Logger.getLogger(FeedDAO.class.getName());

    private static final int ITEMS_PER_PAGE= 20;

    public static void addRssForUser(String rssLink, String rssName, int userId) throws IOException, SQLException {
        Connection conn = FeedDbDataSource.getConnection();
        if (conn != null) {
            try {
                conn.setAutoCommit(false);

                //add to feed table if not exists
                int feedId = FeedDbUtils.insertRssIntoFeedTable(rssLink, conn);
                logger.info("new feed id = " + feedId);

                //add to subscription table for this user
                FeedDbUtils.insertIntoSubscriptionTable(userId, feedId, rssName, conn);

                //add to feed_item (bulk insert)
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(new URL(rssLink)));
                List<SyndEntry> feedItems = feed.getEntries();
                Object[][] itemsArray = new Object[feedItems.size()][6];
                int i = 0;
                for (SyndEntry item : feedItems) {
                    itemsArray[i][0] = item.getUri(); //guid
                    itemsArray[i][1] = item.getTitle(); //title
                    String description = item.getDescription().getValue();
                    if (description.length() > FeedItem.COL_DESCRIPTION_LENGTH) {
                        description = description.substring(0, FeedItem.COL_DESCRIPTION_LENGTH - 1);
                    }
                    itemsArray[i][2] = description; //description
                    itemsArray[i][3] = item.getLink(); //link
                    itemsArray[i][4] = item.getPublishedDate(); //pubDate
                    itemsArray[i][5] = feedId;
                    i++;
                }
                FeedDbUtils.insertIntoFeedItemTable(itemsArray, conn);

                //add read status?

                // commit transaction
                conn.commit();
            } catch (FeedException e) {
                conn.rollback();
                logger.log(Level.SEVERE, "Error processing feed", e);
            } catch (MalformedURLException e) {
                conn.rollback();
                logger.log(Level.SEVERE, "Malformed feed URL: " + rssLink, e);
            } finally {
                conn.close();
            }
        }
    }

    public static int getPageCountInLatest(int userId) {
        int feedItemsCount = FeedDbUtils.getUserFeedItemsCount(userId);
        int pageCount = feedItemsCount / ITEMS_PER_PAGE;
        if ((feedItemsCount % ITEMS_PER_PAGE) > 0) {
            pageCount++;
        }
        return pageCount;
    }

    public static int getPageCountByFeedLink(String feedLink) {
        int feedItemsCount = FeedDbUtils.getFeedItemsCountByFeedLink(feedLink);
        int pageCount = feedItemsCount / ITEMS_PER_PAGE;
        if ((feedItemsCount % ITEMS_PER_PAGE) > 0) {
            pageCount++;
        }
        return pageCount;
    }

    public static List<FeedItem> getUserFeedItemsLatestPage(int userId, int offset) {
        if (offset != 0) {
            offset--;
        }
        return FeedDbUtils.getUserFeedItemsLatest(userId, offset * ITEMS_PER_PAGE, ITEMS_PER_PAGE);
    }

    public static List<FeedItem> getFeedItemsByFeedLinkPage(String feedLink, int offset) {
        if (offset != 0) {
            offset--;
        }
        return FeedDbUtils.getFeedItemsByFeedLink(feedLink, offset * ITEMS_PER_PAGE, ITEMS_PER_PAGE);
    }
}
