package org.strokova.rss.database;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.strokova.rss.obj.Feed;
import org.strokova.rss.obj.FeedItem;
import org.strokova.rss.obj.User;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author: Veronika, 7/28/2016.
 */

public class FeedDbUtils {
    private static final Logger logger = Logger.getLogger(FeedDbUtils.class.getName());
    private static final int NO_RESULT_ID = -1;

    public static List<FeedItem> getUserFeedItemsLatest(int userId) {
        String query =
                "select * from feed_item item \n" +
                        "join subscription sub\n" +
                        "on item.feed_id = sub.feed_id\n" +
                        "where sub.user_id = ?";
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

    public static List<FeedItem> getFeedItemsByFeedLink(String feedLink) {
        String query =
                "select * from feed_item\n" +
                        "join feed\n" +
                        "on feed_item.feed_id = feed.id\n" +
                        "where feed.feed_link = ?"; //TODO: order by date
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

    public static String getFeedNameByFeedLink(String feedLink) {
        String query = "select * from feed where feed_link = ?";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<Feed> resultHandler = new BeanHandler<>(Feed.class);
        Feed feed = null;
        try {
            feed = run.query(query, resultHandler, feedLink);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return feed.getFeed_name(); //TODO
    }

    public static List<Feed> getUserFeeds(int userId) {
        String query =
                "select * from feed\n" +
                "join subscription sub\n" +
                "on feed.id = sub.feed_id\n" +
                "where sub.user_id = ?";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<List<Feed>> resultHandler = new BeanListHandler<>(Feed.class);
        List<Feed> feeds = null;
        try {
            feeds = run.query(query, resultHandler, userId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return feeds;
    }

    public static boolean isValidUser(String userName, String userPassword) {
        String query = "select * from user where username = ? and password = ?";
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

    public static int getUserId(String userName) {
        String query = "select * from user where username = ?";
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

    // insert new RSS feed into feed table
    public static void insertRssIntoFeed(String feedLink, String feedName) {
        String query = "insert into feed (feed_link, feed_name) values (?, ?)";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<Feed> resultHandler = new BeanHandler<>(Feed.class);
        try {
            run.insert(query, resultHandler, feedLink, feedName);
        } catch (SQLException e) {
            //TODO: can handle Duplicate entry 1062 error
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
    }
}
