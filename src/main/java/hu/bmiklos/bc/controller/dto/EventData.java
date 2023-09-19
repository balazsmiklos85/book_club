package hu.bmiklos.bc.controller.dto;


public class EventData {
    private final String id;
    private final String date;
    private final String time;
    private final BookData book;
    private final HostData host;
    private final boolean userAttended;

    public EventData(String id, String date, String time, BookData book, HostData host, boolean userAttended) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.book = book;
        this.host = host;
        this.userAttended = userAttended;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public BookData getBook() {
        return book;
    }

    public HostData getHost() {
        return host;
    }

    public boolean isUserAttended() {
        return userAttended;
    }

    public boolean isUserHosted() {
        return host.isUserHosted();
    }

    public String getTime() {
        return time;
    }
}