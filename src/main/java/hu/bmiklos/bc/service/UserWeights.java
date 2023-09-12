package hu.bmiklos.bc.service;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import hu.bmiklos.bc.model.Event;
import hu.bmiklos.bc.model.Participant;
import hu.bmiklos.bc.model.User;

public class UserWeights {
    private static final int LAST_CONSIDERED_EVENTS = 12; // TODO this value should come from some configuration

    private final Map<User, Long> weights;
    private final Map<Integer, Long> externalWeights;

    public UserWeights(List<Event> events) {
        Stream<Event> lastFewEvents = events.stream()
                .filter(this::isPastEvent)
                .sorted((event1, event2) -> event2.getTime().compareTo(event1.getTime()))
                .limit(LAST_CONSIDERED_EVENTS);
        List<Participant> lastParticipants = lastFewEvents.map(Event::getParticipants)
                .flatMap(Collection::stream)
                .toList();
        Stream<User> lastUsers = lastParticipants.stream()
                .map(Participant::getUser)
                .filter(Objects::nonNull);
        this.weights = lastUsers
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        Stream<Integer> externalUsers = lastParticipants.stream()
                .filter(participant -> !Objects.nonNull(participant.getUser()))
                .map(Participant::getParticipantExternalId);

        this.externalWeights = externalUsers.collect(Collectors.groupingBy(e -> e, Collectors.counting()));
    }

    public long getWeight(Integer integer) {
        return externalWeights.getOrDefault(integer, 0L);
    }

    public long getWeight(User userById) {
        return weights.getOrDefault(userById, 0L);
    }

    private boolean isPastEvent(Event event) {
        return event.getTime().isBefore(Instant.now());
    }
}
