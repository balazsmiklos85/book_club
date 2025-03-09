package hu.bmiklos.bc.service.dto;

import java.time.Instant;
import java.util.UUID;

import org.springframework.lang.Nullable;

public class EditEventDto {
    private final UUID id;
    private final UUID bookId;
    private final Instant time;
    @Nullable
    private final Integer hostExternalId;
    @Nullable
    private final UUID hostId;

    public EditEventDto(UUID id, UUID bookId, Instant time, UUID hostId) {
        this(id, bookId, time, null, hostId);
    }

    public EditEventDto(UUID id, UUID bookId, Instant time, Integer hostExternalId) {
        this(id, bookId, time, hostExternalId, null);
    }

    private EditEventDto(UUID id, UUID bookId, Instant time, @Nullable Integer hostExternalId, @Nullable UUID hostId) {
        this.id = id;
        this.bookId = bookId;
        this.time = time;
        this.hostExternalId = hostExternalId;
        this.hostId = hostId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBookId() {
        return bookId;
    }

    public Instant getTime() {
        return time;
    }

    @Nullable
    public Integer getHostExternalId() {
        return hostExternalId;
    }

    @Nullable
    public UUID getHostId() {
        return hostId;
    }
}