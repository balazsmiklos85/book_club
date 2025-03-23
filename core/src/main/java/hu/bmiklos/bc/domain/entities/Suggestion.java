package hu.bmiklos.bc.domain.entities;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Suggestion {
    private final UUID id;
    private final Instant creationDate;
    private final String description;
    private final ShallowUser suggester;
    private final Book book;

    public boolean isFromTheLastMonth() {
	return creationDate.isAfter(Instant.now().minus(30l, ChronoUnit.DAYS));
    }
}
