package hu.bmiklos.bc.controller.dto;

import java.util.UUID;

import hu.bmiklos.bc.service.dto.UserDto;

public class LeaderboardBookData {
    private final String id;
    private final String author;
    private final String title;
    private final String url;
    private final String recommenderName;
    private final boolean userVoted;

    public LeaderboardBookData(UUID id, String author, String title, String url, UserDto recommender, boolean userVoted) {
        this.id = id.toString();
        this.author = author;
        this.title = title;
        this.url = url;
        this.recommenderName = recommender.getName();
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

    public boolean isUserVoted() {
        return userVoted;
    }    
}
