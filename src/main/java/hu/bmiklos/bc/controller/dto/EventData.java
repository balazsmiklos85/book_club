package hu.bmiklos.bc.controller.dto;

import java.util.Objects;

public class EventData {
    private final String date;
    private final String author;
    private final String title;
    private final String hostName;
    private final boolean userAttended;
    private final boolean userHosted;

    public EventData(String date, String author, String title, String hostName, boolean userAttended, boolean userHosted) {
        this.date = date;
        this.author = author;
        this.title = title;
        this.hostName = hostName;
        this.userAttended = userAttended;
        this.userHosted = userHosted;
    }

    public String getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getHostName() {
        return hostName;
    }

    public boolean isUserAttended() {
        return userAttended;
    }

    public boolean isUserHosted() {
        return userHosted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, author, title, hostName, userAttended, userHosted);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof EventData))
            return false;
        EventData other = (EventData) obj;
        return Objects.equals(date, other.date) && Objects.equals(author, other.author)
                && Objects.equals(title, other.title) && Objects.equals(hostName, other.hostName)
                && userAttended == other.userAttended && userHosted == other.userHosted;
    }    
}