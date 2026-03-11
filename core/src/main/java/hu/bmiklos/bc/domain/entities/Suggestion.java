package hu.bmiklos.bc.domain.entities;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@RequiredArgsConstructor
@Slf4j
public class Suggestion {
  private final UUID id;
  private final Instant creationDate;
  private final String description;
  private final ShallowUser suggester;
  private final Book book;

  public boolean isFromTheLastMonth() {
    log.trace("Suggestion[{}] `isFromTheLastMonth()`...", id);
    log.debug("Suggestion[{}] creation date: {}", id, creationDate);
    var result = creationDate.isAfter(Instant.now().minus(30l, ChronoUnit.DAYS));
    log.trace("Suggestion[{}] `isFromTheLastMonth()` result: {}", id, result);
    return result;
  }
}
