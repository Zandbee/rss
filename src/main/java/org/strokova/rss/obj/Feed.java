package org.strokova.rss.obj;

/**
 * author: Veronika, 7/31/2016.
 */
public class Feed {
    public static final int COLUMN_FEED_LINK_LENGTH = 3000;

    private int id;
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
