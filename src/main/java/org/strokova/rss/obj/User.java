package org.strokova.rss.obj;

/**
 * Created by Veronika on 7/31/2016.
 */
public class User {
    public static final int COLUMN_USERNAME_LENGTH = 45;
    public static final int COLUMN_PASSWORD_LENGTH = 45;

    private int id;
    private String username;
    private String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
