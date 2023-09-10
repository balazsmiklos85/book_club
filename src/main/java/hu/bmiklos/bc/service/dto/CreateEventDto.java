package hu.bmiklos.bc.service.dto;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class CreateEventDto {
    private final UUID bookId;
    private final Instant time;
    private final Integer hostExternalId;
    private final UUID hostId;

    public CreateEventDto(UUID bookId, Instant time, UUID hostId) {
        this(bookId, time, null, hostId);
    }

    public CreateEventDto(UUID bookId, Instant time, Integer hostExternalId) {
        this(bookId, time, hostExternalId, null);
    }

    private CreateEventDto(UUID bookId, Instant time, Integer hostExternalId, UUID hostId) {
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

    @Override
    public int hashCode() {
        return Objects.hash(bookId, time, hostExternalId, hostId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof CreateEventDto))
            return false;
        CreateEventDto other = (CreateEventDto) obj;
        return Objects.equals(bookId, other.bookId) && Objects.equals(time, other.time)
                && Objects.equals(hostExternalId, other.hostExternalId) && Objects.equals(hostId, other.hostId);
    }    
}
