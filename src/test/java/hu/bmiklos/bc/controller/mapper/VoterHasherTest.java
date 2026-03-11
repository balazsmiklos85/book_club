package hu.bmiklos.bc.controller.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import hu.bmiklos.bc.domain.entities.Book;
import hu.bmiklos.bc.domain.entities.Email;
import hu.bmiklos.bc.domain.entities.ShallowUser;
import hu.bmiklos.bc.domain.entities.Vote;
import hu.bmiklos.bc.web.mapper.VoterHasher;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VoterHasherTest {
  @Test
  void hashesEmail() {
    var hasher = new VoterHasher();
    var email = new Email("email@test.hu");
    var user = new ShallowUser(null, null, null, null, List.of(email));
    var vote = new Vote(UUID.randomUUID(), mock(Book.class), user);

    Optional<String> result = hasher.convert(vote);

    assertTrue(result.isPresent(), "The e-mail address should have been hashed.");
    assertEquals(
        "467b20733af40a5920f52fd2958208646dc62a3eae25dae8bd48bac02f7429f7",
        result.get(),
        "The e-mail address should have been hashed.");
  }
}
