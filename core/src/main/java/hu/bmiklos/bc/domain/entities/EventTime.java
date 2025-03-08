package hu.bmiklos.bc.domain.entities;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.Instant;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EventTime {
  private static final long FOUR_WEEKS_AS_DAYS = 4 * 7l;
  private final Instant time;

  /**
   * Adds 4 weeks to the current time.
   *
   * @return the proposed new time, which is 4 weeks from the current time.
   */
  public Instant proposeNewDate() {
    /*
     * Adds the 4 weeks as days, because otherwise we would get a {@link java.time.temporal.UnsupportedTemporalTypeException}: "Unsupported unit: Weeks".
     */
    return time.plus(FOUR_WEEKS_AS_DAYS, DAYS);
  }
}
