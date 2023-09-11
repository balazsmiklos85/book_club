package hu.bmiklos.bc.service.dto;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.springframework.lang.Nullable;

public class GetEventDto {
    private final Instant time;
    private final BookDto book;
    @Nullable
    private final UserDto host;
    @Nullable
    private final Integer hostExternalId;
    private final List<UserDto> participants;

    public GetEventDto(Instant time, BookDto book, UserDto host, Integer hostExternalId, List<UserDto> participants) {
        this.time = time;
        this.book = book;
        this.host = host;
        this.hostExternalId = hostExternalId;
        this.participants = participants;
    }

    public Instant getTime() {
        return time;
    }

    public BookDto getBook() {
        return book;
    }

    @Nullable
    public UserDto getHost() {
        return host;
    }

    public List<UserDto> getParticipants() {
        return participants;
    }

    @Nullable
    public Integer getHostExternalId() {
        return hostExternalId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, book, host, hostExternalId, participants);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof GetEventDto))
            return false;
        GetEventDto other = (GetEventDto) obj;
        return Objects.equals(time, other.time) && Objects.equals(book, other.book) && Objects.equals(host, other.host)
                && Objects.equals(hostExternalId, other.hostExternalId)
                && Objects.equals(participants, other.participants);
    }
}
