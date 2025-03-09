package hu.bmiklos.bc.controller.dto;

import java.util.Collections;
import java.util.List;

public class EventData {
    private final String id;
    private final String date;
    private final String time;
    private final BookData book;
    private final HostData host;
    private final List<ParticipantData> participants;

    public EventData(String id, String date, String time, BookData book, HostData host, List<ParticipantData> participants) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.book = book;
        this.host = host;
        this.participants = Collections.unmodifiableList(participants);
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

    public boolean isUserHosted() {
        return host.isUserHosted();
    }

    public String getTime() {
        return time;
    }

    public List<ParticipantData> getParticipants() {
        return participants;
    }
}