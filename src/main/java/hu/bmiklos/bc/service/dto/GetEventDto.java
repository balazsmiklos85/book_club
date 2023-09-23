package hu.bmiklos.bc.service.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.lang.Nullable;

public class GetEventDto {
    private final UUID id;
    private final Instant time;
    private final BookDto book;
    @Nullable
    private final UserDto host;
    @Nullable
    private final Integer hostExternalId;
    private final List<UserDto> participants;

    public GetEventDto(UUID id, Instant time, BookDto book, UserDto host, Integer hostExternalId, List<UserDto> participants) {
        this.id = id;
        this.time = time;
        this.book = book;
        this.host = host;
        this.hostExternalId = hostExternalId;
        this.participants = participants;
    }

    public UUID getId() {
        return id;
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
}
