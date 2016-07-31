package org.strokova.rss.database;

import org.strokova.rss.obj.FeedItem;

/**
 * Created by Veronika on 7/28/2016.
 */
public class FeedManager {
    private FeedDbUtils database = null;

    public FeedManager() {}

    public void setDatabase(FeedDbUtils db) {
        this.database = db;
    }

    public FeedItem getFeed() {
        return database.getFeed();
    }
}
