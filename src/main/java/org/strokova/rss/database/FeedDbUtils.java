package org.strokova.rss.database;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.strokova.rss.obj.FeedItem;
import org.strokova.rss.obj.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Veronika on 7/28/2016.
 */

public class FeedDbUtils {
    private static final Logger logger = Logger.getLogger(FeedDbUtils.class.getName());

    public FeedItem getFeed() {
        try {
            String query = "select * from feed_item";
            try (Connection conn = FeedDbDataSource.getDataSource().getConnection()) {
                Statement stm = conn.createStatement();
                ResultSet resultSet = stm.executeQuery(query);

                if (resultSet.next()) {
                    FeedItem feedItem = new FeedItem();
                    String title = resultSet.getString("title");
                    feedItem.setTitle(title);
                    return feedItem;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }

        return null;
    }

    public List<FeedItem> getFeedItems() {
        String query = "select * from feed_item";
        QueryRunner run = new QueryRunner(FeedDbDataSource.getDataSource());
        ResultSetHandler<List<FeedItem>> resultHandler = new BeanListHandler<>(FeedItem.class);
        List<FeedItem> feedItems = null;
        try {
            feedItems = run.query(query, resultHandler);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return feedItems;
    }

    public static boolean isValidUser(String userName, String userPassword) {
        String query = "select * from user where username = ? and password = ?";
        QueryRunner run = new QueryRunner((FeedDbDataSource.getDataSource()));
        ResultSetHandler<User> resultHandler = new BeanHandler(User.class);
        User user = null;
        try {
            user = run.query(query, resultHandler, userName, userPassword);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return (user != null);
    }
}
