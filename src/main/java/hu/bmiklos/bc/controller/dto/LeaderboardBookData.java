package hu.bmiklos.bc.controller.dto;

import java.util.UUID;

public class LeaderboardBookData {
    private final String id;
    private final String author;
    private final String title;
    private final String url;
    private final String recommenderName;
    private final boolean userVoted;
    private boolean isNew = false;

    public LeaderboardBookData(UUID id, String author, String title, String url, String recommender, boolean userVoted) {
        this.id = id.toString();
        this.author = author;
        this.title = title;
        this.url = url;
        this.recommenderName = recommender;
        this.userVoted = userVoted;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getRecommenderName() {
        return recommenderName;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isUserVoted() {
        return userVoted;
    }
}
