package hu.bmiklos.bc.domain.entities;

import java.util.UUID;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Vote {
    private final UUID id;
    private final Book book;
    private final User user;
}
