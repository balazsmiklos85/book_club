package hu.bmiklos.bc.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import hu.bmiklos.bc.model.entity.BookEntity;
import hu.bmiklos.bc.model.entity.EventEntity;
import hu.bmiklos.bc.model.entity.ParticipantEntity;
import hu.bmiklos.bc.model.entity.UserEntity;
import hu.bmiklos.bc.model.entity.VoteEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@Transactional
public class BookJpaRepositoryTest {
  @Autowired private EntityManager entityManager;
  @Autowired private BookJpaRepository bookRepository;
  @Autowired private EventJpaRepository eventRepository;
  @Autowired private UserJpaRepository userRepository;

  @Test
  void testFindBookWeights() {
    UserEntity user1 = persistUser(-1);
    UserEntity user2 = persistUser(-2);
    BookEntity book1 = persistBook("Book 1", "Author 1", "Book URL 1");
    BookEntity book2 = persistBook("Book 2", "Author 2", "Book URL 2");
    BookEntity oldBook1 = persistBook("Old Book 1", "Author 1", "Old Book URL 1");
    BookEntity oldBook2 = persistBook("Old Book 2", "Author 2", "Old Book URL 2");
    EventEntity event1 = persistEvent(Instant.now(), oldBook1);
    EventEntity event2 = persistEvent(Instant.now().minus(30, ChronoUnit.DAYS), oldBook2);
    persistParticipant(user1, event1);
    persistParticipant(user1, event2);
    persistParticipant(user2, event2);
    persistVote(-1, book1);
    persistVote(-2, book1);
    persistVote(-1, book2);
    entityManager.flush();
    entityManager.clear();

    List<Object[]> result = bookRepository.findBookWeights();

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    Object[] firstResult = result.getFirst();
    UUID bookId = convertBytesToUUID((byte[]) firstResult[0]);
    Long userWeights = ((BigDecimal) firstResult[1]).longValue();
    assertThat(bookId).isEqualTo(book1.getId());
    assertThat(userWeights).isEqualTo(3L);
    Object[] secondResult = result.get(1);
    UUID secondBookId = convertBytesToUUID((byte[]) secondResult[0]);
    Long secondUserWeights = ((BigDecimal) secondResult[1]).longValue();
    assertThat(secondBookId).isEqualTo(book2.getId());
    assertThat(secondUserWeights).isEqualTo(2L);
  }

  public static UUID convertBytesToUUID(byte[] bytes) {
    var byteBuffer = ByteBuffer.wrap(bytes);
    return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
  }

  private void persistVote(int userExternalId, BookEntity book1) {
    var vote = new VoteEntity();
    vote.setUserExternalId(userExternalId);
    vote.setBookId(book1.getId());
    entityManager.merge(vote);
  }

  private void persistParticipant(UserEntity user, EventEntity event) {
    var participant = new ParticipantEntity();
    participant.setUser(user);
    participant.setParticipantExternalId(user.getExternalId());
    participant.setEventId(event.getId());
    entityManager.merge(participant);
  }

  private BookEntity persistBook(String title, String author, String url) {
    var result = new BookEntity();
    result.setId(UUID.randomUUID());
    result.setTitle(title);
    result.setAuthor(author);
    result.setUrl(url);
    return bookRepository.save(result);
  }

  private EventEntity persistEvent(Instant time, BookEntity oldBook) {
    var result = new EventEntity();
    result.setId(UUID.randomUUID());
    result.setTime(time);
    result.setBookId(oldBook.getId());
    return eventRepository.save(result);
  }

  private UserEntity persistUser(int externalId) {
    var result = new UserEntity();
    result.setExternalId(externalId);
    entityManager.persist(result);
    return result;
  }
}
