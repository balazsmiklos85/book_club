package hu.bmiklos.bc.web.mapper;

import hu.bmiklos.bc.domain.entities.ShallowUser;
import hu.bmiklos.bc.domain.entities.Suggestion;
import hu.bmiklos.bc.domain.entities.User;
import hu.bmiklos.bc.web.dto.SuggestionReference;
import java.text.MessageFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;

@RequiredArgsConstructor
public class SuggestionReferenceConverter implements Converter<Suggestion, SuggestionReference> {
  private final User caller;

  @Override
  public SuggestionReference convert(Suggestion source) {
    ShallowUser suggester = source.getSuggester();
    String suggesterName;
    if (suggester.name() != null) {
      suggesterName = suggester.name();
    } else if (suggester.externalId() != null) {
      suggesterName = MessageFormat.format("[{0}]", suggester.externalId());
    } else {
      suggesterName = "[N/A]";
    }
    return new SuggestionReference(source.getId(), suggesterName, suggester.isSameAs(caller));
  }
}
