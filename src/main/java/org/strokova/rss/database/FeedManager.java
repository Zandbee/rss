package org.strokova.rss.database;

import org.strokova.rss.obj.Feed;

/**
 * Created by Veronika on 7/28/2016.
 */
public class FeedManager {
    private FeedDatabase database = null;

    public FeedManager() {}

    public void setDatabase(FeedDatabase db) {
        this.database = db;
    }

    public Feed getFeed() {
        return database.getFeed();
    }
}
