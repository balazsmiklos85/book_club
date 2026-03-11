package hu.bmiklos.bc.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

public class CustomLocaleResolver extends AcceptHeaderLocaleResolver {
  private static final List<Locale> LOCALES = List.of(new Locale("hu"), new Locale("en"));

  @Override
  public Locale resolveLocale(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader("Accept-Language"))
        .map(String::trim)
        .filter(StringUtils::isNotBlank)
        .map(acceptLanguage -> Locale.lookup(Locale.LanguageRange.parse(acceptLanguage), LOCALES))
        .orElse(Locale.getDefault());
  }
}
