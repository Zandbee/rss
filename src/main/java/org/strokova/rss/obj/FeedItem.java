package org.strokova.rss.obj;

import java.util.Date;

/**
 * Created by Veronika on 7/28/2016.
 */
public class FeedItem {
    public enum FeedItemColumn {
        GUID("guid"),
        TITLE("title"),
        DESCRIPTION("description"),
        LINK("link"),
        PUB_DATE("pub_date"),
        FEED_ID("feed_id");

        private final String columnName;

        FeedItemColumn(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }
    }

    private String guid;
    private String title;
    private String description;
    private String link;
    private Date pubDate;
    private String feedId;

    public FeedItem() {
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }
}
