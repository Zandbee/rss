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
                FeedDbUtils.insertIntoItemReadStatusTable(
                        getUserItemReadStatuses(getFeedItemsGuids(feedItems), userId), conn);

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
        List<Object[]> feedItemArrays = new ArrayList<>();

        for (SyndEntry entry : feedItems) {
            Object[] item = validateAndGetFeedItemAsArray(entry, feedId);
            if (item != null) {
                feedItemArrays.add(item);
            }
        }

        return putListOfArraysIntoArray(feedItemArrays);
    }

    // get all feed item fields and check if their values comply with DB column length restrictions
    // @return null if any of fields that cannot be cut exceeds the max length
    private static Object[] validateAndGetFeedItemAsArray(SyndEntry itemSource, int feedId) {
        String guid = itemSource.getUri();
        String link = itemSource.getLink();
        if (!validateCannotCut(guid, FeedItem.COLUMN_GUID_LENGTH) ||
                !validateCannotCut(link, FeedItem.COLUMN_LINK_LENGTH)) {
            return null;
        }

        List<Object> feedItemAsList = new ArrayList<>();
        // the order of adding is important
        feedItemAsList.add(guid);
        feedItemAsList.add(validateCanCut(itemSource.getTitle(), FeedItem.COLUMN_TITLE_LENGTH));
        feedItemAsList.add(validateCanCut(itemSource.getDescription().getValue(), FeedItem.COLUMN_DESCRIPTION_LENGTH));
        feedItemAsList.add(link);
        feedItemAsList.add(itemSource.getPublishedDate());
        feedItemAsList.add(feedId);

        return feedItemAsList.toArray();
    }

    private static String validateCanCut(String value, int maxLength) {
        return value.length() <= maxLength ? value : value.substring(0, maxLength - 1);
    }

    // @return true if value does not exceed the max length
    private static boolean validateCannotCut(String value, int maxLength) {
        return value.length() < maxLength;
    }

    private static Object[][] putListOfArraysIntoArray(List<Object[]> list) {
        Object[][] itemsArray = new Object[list.size()][];
        int i = 0;
        for (Object[] item : list) {
            itemsArray[i] = item;
            i++;
        }
        return itemsArray;
    }

    private static List<String> getFeedItemsGuids(Object[][] feedItems) {
        int itemsCount = feedItems.length;
        List<String> guids = new ArrayList<>(itemsCount);
        for (Object[] feedItem : feedItems) {
            guids.add(feedItem[0].toString());
        }
        return guids;
    }

    private static Object[][] getUserItemReadStatuses(List<String> itemGuids, int userId) {
        List<Object[]> itemReadStatuses =  new ArrayList<>(itemGuids.size());
        for (String guid : itemGuids) {
            List<Object> itemTeadStatus = new ArrayList<>();
            // the order of adding is important
            itemTeadStatus.add(userId);
            itemTeadStatus.add(guid);
            itemTeadStatus.add(Boolean.FALSE);
            itemReadStatuses.add(itemTeadStatus.toArray());
        }
        return putListOfArraysIntoArray(itemReadStatuses);
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
                    conn.commit();
                }

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

    private static int getOffset(int offset) {
        if (offset != 0) {
            offset--;
        }
        return offset * ITEMS_PER_PAGE;
    }

    public static List<FeedItemWithReadStatus> getUserFeedItemsLatestPage(int userId, int pageNum, String order)
            throws SQLException {
        return FeedDbUtils.getUserFeedItemsWithReadStatusLatest(userId, getOffset(pageNum) , ITEMS_PER_PAGE, order);
    }

    public static List<FeedItemWithReadStatus> getFeedItemsByFeedLinkPage(int userId, String feedLink, int pageNum, String order)
            throws SQLException {
        return FeedDbUtils.getFeedItemsWithReadStatusByFeedLink(feedLink, getOffset(pageNum), ITEMS_PER_PAGE, userId, order);
    }
}
