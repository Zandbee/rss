package org.strokova.rss.obj;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Veronika on 7/28/2016.
 */
public class FeedItem {
    public static final int COL_DESCRIPTION_LENGTH = 3000;

    private String guid;
    private String title;
    private String description;
    private String link;
    private Date pub_date;
    private String feed_id;

    public FeedItem() {
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(String feed_id) {
        this.feed_id = feed_id;
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

    public Date getPub_date() {
        return pub_date;
    }

    public void setPub_date(Date pub_date) {
        this.pub_date = pub_date;
    }

    public String getFormattedDate() {
        String datePattern = "EEE, MMM d, h:mm a";
        SimpleDateFormat formatter = new SimpleDateFormat(datePattern, Locale.getDefault());
        return formatter.format(pub_date);
    }
}
