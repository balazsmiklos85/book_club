package hu.bmiklos.bc.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import hu.bmiklos.bc.model.Email;
import hu.bmiklos.bc.model.Participant;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.repository.EmailRepository;
import hu.bmiklos.bc.repository.ParticipantRepository;
import hu.bmiklos.bc.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class ParticipantService {
    private final EmailRepository emailRepository;

    private final ParticipantRepository participantRepository;

    private final UserRepository userRepository;

    public ParticipantService(EmailRepository emailRepository, ParticipantRepository participantRepository,
            UserRepository userRepository) {
        this.emailRepository = emailRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addParticipant(UUID eventUuid, String participantInfo) {
        int participantExtId = getParticipantExternalId(participantInfo);
        if (isNotExisting(eventUuid, participantExtId)) {
            Participant participation = new Participant(eventUuid, participantExtId);
            participantRepository.saveAndFlush(participation);
        }
    }

    private int getParticipantExternalId(String participantInfo) {
        try {
            switch (ParticipantInfoType.from(participantInfo)) {
                case EMAIL_ADDRESS:
                    Email participantEmail = emailRepository.findById(participantInfo)
                            .orElseThrow(
                                    () -> new IllegalArgumentException("User not found by email: " + participantInfo));
                    return participantEmail.getUser().getExternalId();
                case EXTERNAL_ID:
                    return Integer.parseInt(participantInfo);
                case FREE_TEXT:
                default:
                    List<User> usersByName = userRepository.findByNameLike('%' + participantInfo + '%');
                    if (usersByName.size() != 1) {
                        throw new IllegalArgumentException(
                                "User not found by name: " + participantInfo + " (found: " + usersByName.size() + ")");
                    }
                    User participant = usersByName.get(0);
                    return participant.getExternalId();
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid participant info: " + participantInfo);
        }
    }

    private boolean isNotExisting(UUID eventUuid, int participantExtId) {
        return participantRepository.findByEventIdAndParticipantExternalId(eventUuid, participantExtId)
                .isEmpty();
    }

}
