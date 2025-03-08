package hu.bmiklos.bc.domain.entities;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Suggestion {
    private final UUID id;
    private final Instant creationDate;
    private final String description;
    private final User suggester;
    private final Book book;
}
