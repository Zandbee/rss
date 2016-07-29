package org.strokova.rss.database;

import org.strokova.rss.obj.Feed;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Veronika on 7/28/2016.
 */

public class FeedDatabase {

    public Feed getFeed() {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            DataSource ds = (DataSource) envCtx.lookup("jdbc/rss");

            String query = "select * from feed_item";
            try (Connection conn = ds.getConnection()) {
                Statement stm = conn.createStatement();
                ResultSet resultSet = stm.executeQuery(query);

                if (resultSet.next()) {
                    Feed feed = new Feed();
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    System.out.println("Title: " + title);
                    System.out.println("Title: " + description);
                    feed.setName(title);
                    return feed;
                }
            }
        } catch (NamingException | SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
