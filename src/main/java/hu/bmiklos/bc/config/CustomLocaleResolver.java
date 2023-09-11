package hu.bmiklos.bc.config;

import java.util.List;
import java.util.Locale;

import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import jakarta.servlet.http.HttpServletRequest;

public class CustomLocaleResolver extends AcceptHeaderLocaleResolver {
    private static final List<Locale> LOCALES = List.of(new Locale("hu"), new Locale("en"));

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String acceptLanguage = request.getHeader("Accept-Language");
        if (acceptLanguage == null || acceptLanguage.trim().isEmpty()) {
            return Locale.getDefault();
        }

        List<Locale.LanguageRange> list = Locale.LanguageRange.parse(acceptLanguage);
        return Locale.lookup(list, LOCALES);
    }
}