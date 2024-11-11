package hu.bmiklos.bc.domain.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Participant {
  private final Event event;
  private final int participantExternalId;
  private final User user;
}
