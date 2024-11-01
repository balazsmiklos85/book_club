package hu.bmiklos.bc.domain.entities;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Event {
  private UUID id;
  private UUID bookId;
  private Instant time;
  private User host;
  private Book book;
  private List<User> participants;
}

