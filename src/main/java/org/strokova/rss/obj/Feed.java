package org.strokova.rss.obj;

/**
 * author: Veronika, 7/31/2016.
 */
public class Feed {
    private int id;
    private String feed_link;
    private String feed_name;

    public Feed() {}

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

    public String getFeed_name() {
        return feed_name;
    }

    public void setFeed_name(String feed_name) {
        this.feed_name = feed_name;
    }
}
