package org.strokova.rss.obj;

/**
 * author: Veronika, 7/31/2016.
 */
public class Feed {
    public static final int COLUMN_FEED_LINK_LENGTH = 3000;

    private int id;
    // the fields in obj classes have the same names as in database for convenient work with Apache DbUtils
    // just sorry for underscores
    private String feed_link;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFeed_link() {
        return feed_link;
    }

    public void setFeed_link(String feed_link) {
        this.feed_link = feed_link;
    }

}
