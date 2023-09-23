package hu.bmiklos.bc.service.dto;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.lang.Nullable;

public class CreateEventDto {
    private final UUID bookId;
    private final Instant time;
    @Nullable
    private final UUID hostId;

    public CreateEventDto(UUID bookId, Instant time, @Nullable UUID hostId) {
        this.bookId = bookId;
        this.time = time;
        this.hostId = hostId;
    }

    public UUID getBookId() {
        return bookId;
    }

    public Instant getTime() {
        return time;
    }

    public Optional<UUID> getHostId() {
        return Optional.ofNullable(hostId);
    }
}
