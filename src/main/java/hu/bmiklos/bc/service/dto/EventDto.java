package hu.bmiklos.bc.service.dto;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class EventDto {
    private final UUID bookId;
    private final Instant time;
    private final Integer hostExternalId;
    private final UUID hostId;

    public EventDto(UUID bookId, Instant time, UUID hostId) {
        this(bookId, time, null, hostId);
    }

    public EventDto(UUID bookId, Instant time, Integer hostExternalId) {
        this(bookId, time, hostExternalId, null);
    }

    private EventDto(UUID bookId, Instant time, Integer hostExternalId, UUID hostId) {
        this.bookId = bookId;
        this.time = time;
        this.hostExternalId = hostExternalId;
        this.hostId = hostId;
    }

    public UUID getBookId() {
        return bookId;
    }

    public Instant getTime() {
        return time;
    }

    public Optional<Integer> getHostExternalId() {
        return Optional.ofNullable(hostExternalId);
    }

    public Optional<UUID> getHostId() {
        return Optional.ofNullable(hostId);
    }
}
