package org.strokova.rss.database;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.strokova.rss.exception.ValidationFailedException;
import org.strokova.rss.obj.FeedItem;
import org.strokova.rss.obj.FeedItemWithReadStatus;
import org.strokova.rss.obj.SubscriptionWithFeed;

import java.io.IOException;
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

    public static void addRssForUser(String rssLink, String rssName, int userId)
            throws SQLException, IOException, FeedException, ValidationFailedException {
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
            } catch (Exception e) {
                conn.rollback();
                logger.log(Level.SEVERE, "Cannot add rss", e);
                throw e;
            } finally {
                if (!conn.isClosed()) {
                    conn.close();
                }
            }
        }
    }

    public static void deleteRssForUser(String feedLink, int userId) throws SQLException {
        Connection conn = FeedDbDataSource.getConnection();
        if (conn != null) {
            try {
                conn.setAutoCommit(false);

                FeedDbUtils.deleteFromSubscriptionTable(userId, feedLink, conn);
                FeedDbUtils.deleteFromItemReadStatusTable(userId, feedLink, conn);

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                logger.log(Level.SEVERE, "Cannot delete rss", e);
                throw e;
            }finally {
                if (!conn.isClosed()) {
                    conn.close();
                }
            }
        }
    }

    private static Object[][] getFeedItems(String rssLink, int feedId) throws IOException, FeedException {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(new URL(rssLink)));
        List<SyndEntry> feedItems = feed.getEntries();
        List<Object[]> feedItemsArray = validateFeedItems(feedItems, feedId);

        Object[][] itemsArray = new Object[feedItemsArray.size()][6]; // TODO enum for 6 - feed item fields num
        int i = 0;
        for (Object[] item : feedItemsArray) {
            itemsArray[i] = item;
            i++;
        }
        return itemsArray;
    }

    // check if all feed item fields comply with DB column length restrictions
    // @return a list of feed items as arrays of fields
    private static ArrayList<Object[]> validateFeedItems(List<SyndEntry> feedItems, int feedId) {
        ArrayList<Object[]> feedItemsArray = new ArrayList<>();
        for (SyndEntry item : feedItems) {
            String guid = item.getUri();
            if (guid.length() > FeedItem.COLUMN_GUID_LENGTH) {
                // don't add this item - guid too long
                continue;
            }
            String title = item.getTitle();
            if (title.length() > FeedItem.COLUMN_TITLE_LENGTH) {
                title = title.substring(0, FeedItem.COLUMN_TITLE_LENGTH - 1);
            }
            String description = item.getDescription().getValue();
            if (description.length() > FeedItem.COLUMN_DESCRIPTION_LENGTH) {
                description = description.substring(0, FeedItem.COLUMN_DESCRIPTION_LENGTH - 1);
            }
            String link = item.getLink();
            if (link.length() > FeedItem.COLUMN_LINK_LENGTH) {
                // don't add this item - link too long
                continue;
            }

            Object[] feedItem = new Object[6]; // TODO enum for 6 - feed item fields num
            feedItem[0] = guid;
            feedItem[1] = title;
            feedItem[2] = description;
            feedItem[3] = link;
            feedItem[4] = item.getPublishedDate();
            feedItem[5] = feedId;
            feedItemsArray.add(feedItem);
        }
        return feedItemsArray;
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

    public static void updateRssItemsForUser(int userId) throws SQLException, IOException, FeedException {
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

                    FeedDbUtils.insertIntoItemReadStatusTable(
                            getUserItemReadStatuses(getFeedItemsGuids(feedItems), userId), conn);
                }

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                logger.log(Level.SEVERE, "Cannot update rss", e);
                throw e;
            } finally {
                if (!conn.isClosed()) {
                    conn.close();
                }
            }
        }
    }

    public static int getPageCountInLatest(int userId) throws SQLException {
        int feedItemsCount = FeedDbUtils.getUserFeedItemsCount(userId);
        int pageCount = feedItemsCount / ITEMS_PER_PAGE;
        if ((feedItemsCount % ITEMS_PER_PAGE) > 0) {
            pageCount++;
        }
        return pageCount;
    }

    public static int getPageCountByFeedLink(String feedLink, int userId) throws SQLException {
        int feedItemsCount = FeedDbUtils.getFeedItemsCountByFeedLink(feedLink, userId);
        int pageCount = feedItemsCount / ITEMS_PER_PAGE;
        if ((feedItemsCount % ITEMS_PER_PAGE) > 0) {
            pageCount++;
        }
        return pageCount;
    }

    public static List<FeedItemWithReadStatus> getUserFeedItemsLatestPage(int userId, int offset, String order)
            throws SQLException {
        if (offset != 0) {
            offset--;
        }
        return FeedDbUtils.getUserFeedItemsWithReadStatusLatest(userId, offset * ITEMS_PER_PAGE, ITEMS_PER_PAGE, order);
    }

    public static List<FeedItemWithReadStatus> getFeedItemsByFeedLinkPage(int userId, String feedLink, int offset, String order)
            throws SQLException {
        if (offset != 0) {
            offset--;
        }
        return FeedDbUtils.getFeedItemsWithReadStatusByFeedLink(feedLink, offset * ITEMS_PER_PAGE, ITEMS_PER_PAGE, userId, order);
    }
}
