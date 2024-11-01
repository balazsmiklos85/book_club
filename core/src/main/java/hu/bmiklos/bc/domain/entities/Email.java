package hu.bmiklos.bc.domain.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Email {
    private final String emailAddress;
    private User user;
}
