package hu.bmiklos.bc.business.repository;

import hu.bmiklos.bc.business.mapper.EmailEntityMapper;
import hu.bmiklos.bc.domain.entities.Email;
import hu.bmiklos.bc.model.repository.EmailJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailBusinessRepository implements EmailRepository {

  private final EmailJpaRepository emailJpaRepository;

  @Override
  public Optional<Email> findById(String username) {
    return emailJpaRepository.findById(username).map(email -> new EmailEntityMapper().convert(email));
  }
}
