package org.strokova.rss.database;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.strokova.rss.obj.FeedItem;
import org.strokova.rss.obj.FeedItemWithReadStatus;
import org.strokova.rss.obj.SubscriptionWithFeed;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author vstrokova, 04.08.2016.
 */
public class FeedDAO {
    private static final Logger logger = Logger.getLogger(FeedDAO.class.getName());

    private static final int ITEMS_PER_PAGE = 20;

    public static void addRssForUser(String rssLink, String rssName, int userId) throws SQLException {
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
                Object[][] feedItems = getFeedItems(rssLink, feedId);
                FeedDbUtils.insertIntoFeedItemTable(feedItems, conn);

                //add to item_read_status (bulk insert, false for new)
                FeedDbUtils.insertIntoItemReadStatusTable(getUserItemReadStatuses(
                        getFeedItemsGuids(feedItems), userId), conn);

                // commit transaction
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                logger.log(Level.SEVERE, "Error executing SQL", e);
            } finally {
                conn.close();
            }
        }
    }

    private static Object[][] getFeedItems(String rssLink, int feedId) {
        try {
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
            return itemsArray;
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "Malformed feed URL: " + rssLink, e);
        } catch (IOException | FeedException e) {
            logger.log(Level.SEVERE, "Error reading RSS", e);
        }

        return null;
    }

    private static ArrayList<String> getFeedItemsGuids(Object[][] feedItems) {
        int itemsCount = feedItems.length;
        ArrayList<String> guids = new ArrayList<>(itemsCount);
        for (Object[] feedItem : feedItems) {
            guids.add(feedItem[0].toString());
        }
        return guids;
    }

    private static Object[][] getUserItemReadStatuses(ArrayList<String> itemGuids, int userId) {
        Object[][] userItemReadStatuses = new Object[itemGuids.size()][3];
        int i = 0;
        for (String guid : itemGuids) {
            userItemReadStatuses[i][0] = userId;
            userItemReadStatuses[i][1] = guid;
            userItemReadStatuses[i][2] = Boolean.FALSE;
            i++;
        }
        return userItemReadStatuses;
    }

    public static void updateRssItemsForUser(int userId) throws SQLException {
        // get user's subscriptions
        List<SubscriptionWithFeed> subscriptions = FeedDbUtils.getUserSubscriptionsWithFeeds(userId);
        // load items from user's feed links
        Connection conn = FeedDbDataSource.getConnection();
        if (conn != null) {
            try {
                conn.setAutoCommit(false);

                for (SubscriptionWithFeed subscription : subscriptions) {
                    Object[][] feedItems = getFeedItems(subscription.getFeed_link(), subscription.getFeed_id());
                    FeedDbUtils.insertIntoFeedItemTable(feedItems, conn);

                    FeedDbUtils.insertIntoItemReadStatusTable(getUserItemReadStatuses(
                            getFeedItemsGuids(feedItems), userId), conn);
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                logger.log(Level.SEVERE, "Error executing SQL", e);
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

    public static List<FeedItemWithReadStatus> getUserFeedItemsLatestPage(int userId, int offset) {
        if (offset != 0) {
            offset--;
        }
        //return FeedDbUtils.getUserFeedItemsLatest(userId, offset * ITEMS_PER_PAGE, ITEMS_PER_PAGE);
        List<FeedItemWithReadStatus> items = null;
        try {
            items = FeedDbUtils.getUserFeedItemsWithReadStatusLatest(userId, offset * ITEMS_PER_PAGE, ITEMS_PER_PAGE);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return items;
    }

    public static List<FeedItemWithReadStatus> getFeedItemsByFeedLinkPage(int userId, String feedLink, int offset) {
        if (offset != 0) {
            offset--;
        }
        //return FeedDbUtils.getFeedItemsByFeedLink(feedLink, offset * ITEMS_PER_PAGE, ITEMS_PER_PAGE);
        List<FeedItemWithReadStatus> items = null;
        try {
            items = FeedDbUtils.getFeedItemsWithReadStatusByFeedLink(feedLink, offset * ITEMS_PER_PAGE, ITEMS_PER_PAGE, userId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return items;
    }
}
