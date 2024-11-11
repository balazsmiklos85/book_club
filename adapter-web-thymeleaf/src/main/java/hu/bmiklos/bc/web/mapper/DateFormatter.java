package hu.bmiklos.bc.web.mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.core.convert.converter.Converter;

public class DateFormatter implements Converter<Instant, String> {
  @Override
  public String convert(Instant source) {
    return LocalDateTime.ofInstant(source, ZoneId.systemDefault()).toLocalDate().toString();
  }
}
