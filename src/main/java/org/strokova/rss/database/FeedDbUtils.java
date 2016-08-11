package org.strokova.rss.database;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.strokova.rss.obj.*;
import org.strokova.rss.util.FeedUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author: Veronika, 7/28/2016.
 */

public final class FeedDbUtils {
    private static final Logger logger = Logger.getLogger(FeedDbUtils.class.getName());
    private static final int NO_RESULT_ID = -1;
    private static final String ORDER_DESC = "desc";
    private static final String ORDER_ASC = "asc";
    private static final String USER_ORDER_ASC = "asc";

    // TODO: AsyncQueryRunner?

    // @return all user's feed items (articles) ordered by date in descending order
    public static List<FeedItem> getUserFeedItemsLatest(int userId) {
        String query =
                "select * from feed_item item\n" +
                        "join subscription sub\n" +
                        "on item.feed_id = sub.feed_id\n" +
                        "where sub.user_id = ?\n" +
                        "order by item.pub_date desc;";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<List<FeedItem>> resultHandler = new BeanListHandler<>(FeedItem.class);
        List<FeedItem> feedItems = null;
        try {
            feedItems = run.query(query, resultHandler, userId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return feedItems;
    }

    // @return a subset of <limit> user's feed items (articles) ordered by date in descending order with <offset>
    public static List<FeedItem> getUserFeedItemsLatest(int userId, int offset, int limit) {
        String query =
                "select * from feed_item item\n" +
                        "join subscription sub\n" +
                        "on item.feed_id = sub.feed_id\n" +
                        "where sub.user_id = ?\n" +
                        "order by item.pub_date desc\n" +
                        "limit ?, ?;";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<List<FeedItem>> resultHandler = new BeanListHandler<>(FeedItem.class);
        List<FeedItem> feedItems = null;
        try {
            feedItems = run.query(query, resultHandler, userId, offset, limit);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return feedItems;
    }

    // @return a subset of <limit> user's feed items (articles) with a read status ordered by date in descending order with <offset>
    public static List<FeedItemWithReadStatus> getUserFeedItemsWithReadStatusLatest(int userId, int offset, int limit, String userOrder) throws SQLException {
        String order;
        if (userOrder.equals(USER_ORDER_ASC)) {
            order = ORDER_ASC;
        } else {
            order = ORDER_DESC;
        }
        // TODO change + in strings into append or one line?
        String query =
                "select i.guid, i.title, i.description, i.link, i.pub_date, i.feed_id, r.is_read\n" +
                        "from feed_item i\n" +
                        "join item_read_status r\n" +
                        "on i.guid = r.item_guid\n" +
                        "where r.user_id = ?\n" +
                        "order by i.pub_date " + order + "\n" +
                        "limit ?, ?;";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<List<FeedItemWithReadStatus>> resultHandler = new BeanListHandler<>(FeedItemWithReadStatus.class);
        List<FeedItemWithReadStatus> feedItemsWithReadStatus = null;
        feedItemsWithReadStatus = run.query(query, resultHandler, userId, offset, limit);
        return feedItemsWithReadStatus;
    }

    public static List<FeedItem> getFeedItemsByFeedLink(String feedLink) {
        String query =
                "select * from feed_item\n" +
                        "join feed\n" +
                        "on feed_item.feed_id = feed.id\n" +
                        "where feed.feed_link = ?\n" +
                        "order by feed_item.pub_date desc;";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<List<FeedItem>> resultHandler = new BeanListHandler<>(FeedItem.class);
        List<FeedItem> feedItems = null;
        try {
            feedItems = run.query(query, resultHandler, feedLink);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return feedItems;
    }

    public static List<FeedItem> getFeedItemsByFeedLink(String feedLink, int offset, int limit) {
        String query =
                "select * from feed_item\n" +
                        "join feed\n" +
                        "on feed_item.feed_id = feed.id\n" +
                        "where feed.feed_link = ?\n" +
                        "order by feed_item.pub_date desc\n" +
                        "limit ?, ?;";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<List<FeedItem>> resultHandler = new BeanListHandler<>(FeedItem.class);
        List<FeedItem> feedItems = null;
        try {
            feedItems = run.query(query, resultHandler, feedLink, offset, limit);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return feedItems;
    }

    public static List<FeedItemWithReadStatus> getFeedItemsWithReadStatusByFeedLink(String feedLink, int offset, int limit, int userId, String userOrder) throws SQLException {
        String order;
        if (userOrder.equals(USER_ORDER_ASC)) {
            order = ORDER_ASC;
        } else {
            order = ORDER_DESC;
        }
        String query =
                "select i.guid, i.title, i.description, i.link, i.pub_date, i.feed_id, r.is_read\n" +
                        "from feed_item i\n" +
                        "left join feed\n" +
                        "on i.feed_id = feed.id\n" +
                        "left join item_read_status r\n" +
                        "on i.guid = r.item_guid\n" +
                        "where feed.feed_link = ? and r.user_id = ?\n" +
                        "order by i.pub_date " + order + "\n" +
                        "limit ?, ?;";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<List<FeedItemWithReadStatus>> resultHandler = new BeanListHandler<>(FeedItemWithReadStatus.class);
        return run.query(query, resultHandler, FeedUtils.decodeUrl(feedLink), userId, offset, limit);
    }

    // Get the number of feed items for a user. Returns 0 if no feed items found
    public static int getUserFeedItemsCount(int userId) {
        String query =
                "select count(*) as count from feed_item item\n" +
                        "join subscription sub\n" +
                        "on item.feed_id = sub.feed_id\n" +
                        "join item_read_status r\n" +
                        "on item.guid = r.item_guid\n" +
                        "where sub.user_id = ?;";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<RowCount> resultHandler = new BeanHandler<>(RowCount.class);
        try {
            RowCount rows = run.query(query, resultHandler, userId);
            return rows.getCount();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return 0;
    }

    // Get the number of feed items for a feed. Returns 0 if no feed items found
    public static int getFeedItemsCountByFeedLink(String feedLink, int userId) {
        String query =
                "select count(*) as count from feed_item\n" +
                        "join feed\n" +
                        "on feed_item.feed_id = feed.id\n" +
                        "join item_read_status r\n" +
                        "on feed_item.guid = r.item_guid\n" +
                        "where feed.feed_link = ? and r.user_id = ?;";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<RowCount> resultHandler = new BeanHandler<>(RowCount.class);
        try {
            return run.query(query, resultHandler, FeedUtils.decodeUrl(feedLink), userId).getCount();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return 0;
    }

    public static Feed getFeedByFeedLink(String feedLink) {
        String query = "select * from feed where feed_link = ?;";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<Feed> resultHandler = new BeanHandler<>(Feed.class);
        Feed feed = null;
        try {
            feed = run.query(query, resultHandler, feedLink);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return feed;
    }

    public static Feed getFeedByFeedLink(String feedLink, Connection conn) {
        String query = "select * from feed where feed_link = ?;";
        QueryRunner run = new QueryRunner();
        ResultSetHandler<Feed> resultHandler = new BeanHandler<>(Feed.class);
        Feed feed = null;
        try {
            feed = run.query(conn, query, resultHandler, feedLink);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return feed;
    }

    public static List<SubscriptionWithFeed> getUserSubscriptionsWithFeeds(int userId) {
        String query =
                "select s.user_id, s.feed_id, f.feed_link, s.feed_name\n" +
                        "from subscription s\n" +
                        "left join feed f\n" +
                        "on s.feed_id = f.id\n" +
                        "where s.user_id = ?;";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<List<SubscriptionWithFeed>> resultHandler = new BeanListHandler<>(SubscriptionWithFeed.class);
        List<SubscriptionWithFeed> subscriptions = null;
        try {
            subscriptions = run.query(query, resultHandler, userId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return subscriptions;
    }

    public static SubscriptionWithFeed getSubscriptionWithFeedByFeedLink(String feedLink) {
        String query =
                "select s.user_id, s.feed_id, f.feed_link, s.feed_name\n" +
                        "from subscription s\n" +
                        "left join feed f\n" +
                        "on s.feed_id = f.id\n" +
                        "where f.feed_link = ?;";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<SubscriptionWithFeed> resultHandler = new BeanHandler<>(SubscriptionWithFeed.class);
        SubscriptionWithFeed subscription = null;
        try {
            subscription = run.query(query, resultHandler, FeedUtils.decodeUrl(feedLink));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return subscription;
    }

    public static boolean isValidUser(String userName, String userPassword) {
        String query = "select * from user where username = ? and password = ?;";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<User> resultHandler = new BeanHandler<>(User.class);
        User user = null;
        try {
            user = run.query(query, resultHandler, userName, userPassword);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e); // TODO: create custom exceptions
        }
        return (user != null);
    }

    // @return -1 if user not found
    public static int getUserId(String userName) {
        String query = "select * from user where username = ?;";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<User> resultHandler = new BeanHandler<>(User.class);
        User user = null;
        try {
            user = run.query(query, resultHandler, userName);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }

        if (user != null) {
            return user.getId();
        } else {
            return NO_RESULT_ID; // TODO: handle 'nothing found' cases
        }
    }

    // insert new user into user table
    public static int insertIntoUserTable(String username, String password) {
        String query = "insert into user (username, password) values (?, ?);";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<User> resultHandler = new BeanHandler<>(User.class);
        try {
            run.insert(query, resultHandler, username, password);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return getUserId(username);
    }

    // insert new RSS feed into feed table
    // @return feed.id of inserted feed
    public static int insertRssIntoFeedTable(String feedLink, Connection conn) {
        String query = "insert into feed (feed_link) values (?)\n" +
                "on duplicate key update feed_link = values(feed_link);";
        QueryRunner run = new QueryRunner();
        ResultSetHandler<Feed> resultHandler = new BeanHandler<>(Feed.class);
        try {
            run.insert(conn, query, resultHandler, feedLink);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return getFeedByFeedLink(feedLink, conn).getId();
    }

    // insert new subscription for a user when they add new rss
    public static void insertIntoSubscriptionTable(int userId, int feedId, String feedName, Connection conn) {
        String query =
                "insert into subscription (user_id, feed_id, feed_name) values (?, ?, ?)\n" +
                        "on duplicate key update\n" +
                        "feed_name = values(feed_name);";
        QueryRunner run = new QueryRunner();
        ResultSetHandler<Subscription> resultHandler = new BeanHandler<>(Subscription.class);
        try {
            run.insert(conn, query, resultHandler, userId, feedId, feedName);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
    }

    public static void deleteFromSubscriptionTable(int userId, String feedLink, Connection conn) throws SQLException {
        String query =
                "delete subscription from subscription\n" +
                        "join feed\n" +
                        "on subscription.feed_id = feed.id\n" +
                        "where feed.feed_link = ?\n" +
                        "and subscription.user_id = ?;";
        QueryRunner run = new QueryRunner();
        run.update(conn, query, feedLink, userId);
    }

    public static void deleteFromItemReadStatusTable(int userId, String feedLink, Connection conn) throws SQLException {
        String query =
                "delete r from item_read_status as r\n" +
                        "join feed_item i\n" +
                        "on r.item_guid = i.guid\n" +
                        "join feed f\n" +
                        "on i.feed_id = f.id\n" +
                        "where f.feed_link = ?\n" +
                        "and r.user_id = ?;";
        QueryRunner run = new QueryRunner();
        run.update(conn, query, feedLink, userId);
    }

    // rename into ->update<-SubscriptionInSubscriptionTable
    public static void renameSubscriptionInSubscriptionTable(int userId, String feedLink, String feedName) {
        String query =
                "update subscription \n" +
                        "join feed on subscription.feed_id = feed.id\n" +
                        "set feed_name = ?\n" +
                        "where subscription.user_id = ? and feed.feed_link = ?;";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        try {
            run.update(query, feedName, userId, feedLink);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
    }

    // insert feed items when a new feed is added
    public static void insertIntoFeedItemTable(Object[][] items, Connection conn) throws SQLException {
        String query =
                "INSERT INTO feed_item (guid, title, description, link, pub_date, feed_id) \n" +
                        "VALUES (?, ?, ?, ?, ?, ?) \n" +
                        "ON DUPLICATE KEY UPDATE \n" +
                        "title = VALUES(title), \n" +
                        "description = VALUES(description),\n" +
                        "link = VALUES(link),\n" +
                        "pub_date = VALUES(pub_date),\n" +
                        "feed_id = VALUES(feed_id);";
        QueryRunner run = new QueryRunner();
        run.batch(conn, query, items);
    }

    public static void insertIntoItemReadStatusTable(Object[][] userItemReadStatuses, Connection conn) throws SQLException {
        String query =
                "insert into item_read_status (user_id, item_guid, is_read)\n" +
                        "values (?, ?, ?)\n" +
                        "on duplicate key update item_guid = item_guid;";
        QueryRunner run = new QueryRunner();
        run.batch(conn, query, userItemReadStatuses);
    }

    public static void updateItemReadStatus(int userId, String guid) throws SQLException {
        String query =
                "update item_read_status\n" +
                        "set is_read = not is_read\n" +
                        "where user_id = ? and item_guid = ?";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        run.update(query, userId, guid);
    }
}
