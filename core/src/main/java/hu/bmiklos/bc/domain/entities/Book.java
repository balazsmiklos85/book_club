package hu.bmiklos.bc.domain.entities;

import java.util.UUID;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Book {
  private final UUID id;
  private final String author;
  private final String title;
  private final String url;
}

