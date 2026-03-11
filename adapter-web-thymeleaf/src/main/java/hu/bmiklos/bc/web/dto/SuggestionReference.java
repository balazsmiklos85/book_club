package hu.bmiklos.bc.web.dto;

import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@RequiredArgsConstructor
public class SuggestionReference {
  @Nullable private final UUID id;
  @Nullable private final String name;
  private final boolean isOfUser;
}
