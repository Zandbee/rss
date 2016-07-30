package org.strokova.rss.database;

import static org.strokova.rss.obj.FeedItem.FeedItemColumn;

import org.strokova.rss.obj.FeedItem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Veronika on 7/28/2016.
 */

public class FeedDAO {
    private static final Logger logger = Logger.getLogger(FeedDAO.class.getName());

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

    public ArrayList<FeedItem> getFeedItems() {
        String query = "select * from feed_item";
        try (Connection conn = FeedDbDataSource.getDataSource().getConnection()) {
            Statement stm = conn.createStatement();
            ResultSet resultSet = stm.executeQuery(query);

            ArrayList<FeedItem> feedItems = new ArrayList<>();

            while (resultSet.next()) {
                FeedItem feedItem = new FeedItem();
                feedItem.setGuid(resultSet.getString(FeedItemColumn.GUID.getColumnName()));
                feedItem.setTitle(resultSet.getString(FeedItemColumn.TITLE.getColumnName()));
                feedItem.setDescription(resultSet.getString(FeedItemColumn.DESCRIPTION.getColumnName()));
                feedItem.setLink(resultSet.getString(FeedItemColumn.LINK.getColumnName()));
                feedItem.setPubDate(resultSet.getDate(FeedItemColumn.PUB_DATE.getColumnName()));
                feedItem.setFeedId(resultSet.getString(FeedItemColumn.FEED_ID.getColumnName()));

                feedItems.add(feedItem);
            }
            return feedItems;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        }
        return null;
    }
}
