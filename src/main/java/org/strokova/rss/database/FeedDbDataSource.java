package org.strokova.rss.database;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Veronika on 7/29/2016.
 */
public final class FeedDbDataSource {
    private static final Logger logger = Logger.getLogger(FeedDbDataSource.class.getName());

    private static final String NAMESPACE = "java:comp/env";
    private static final String JDBC_RESOURCE_NAME = "jdbc/rss";

    private static DataSource dataSource = null;

    public static DataSource getDataSource() {
        if (dataSource == null) {
            try {
                Context initCtx = new InitialContext();
                Context envCtx = (Context) initCtx.lookup(NAMESPACE);
                dataSource = (DataSource) envCtx.lookup(JDBC_RESOURCE_NAME);
            } catch (NamingException e) {
                logger.log(Level.SEVERE, "Error getting DB data source", e);
            }
        }
        return dataSource;
    }
}
