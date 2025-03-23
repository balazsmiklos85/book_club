package hu.bmiklos.bc.web.mapper;

import hu.bmiklos.bc.domain.entities.Suggestion;
import hu.bmiklos.bc.domain.entities.User;
import hu.bmiklos.bc.web.dto.SuggestionReference;
import java.text.MessageFormat;
import org.springframework.core.convert.converter.Converter;

public class SuggestionReferenceConverter implements Converter<Suggestion, SuggestionReference> {

  @Override
  public SuggestionReference convert(Suggestion source) {
    User suggester = source.getSuggester();
    String suggesterName;
    if (suggester.getName() != null) {
      suggesterName = suggester.getName();
    } else if (suggester.getExternalId() != null) {
      suggesterName = MessageFormat.format("[{0}]", suggester.getExternalId());
    } else {
      suggesterName = "[N/A]";
    }
    return new SuggestionReference(source.getId(), suggesterName);
  }
}
