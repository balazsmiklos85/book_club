package hu.bmiklos.bc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collector.Characteristics;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.model.Vote;
import hu.bmiklos.bc.service.dto.UserDto;

public class VoteByBookCollectorTest {

    private VoteByBookCollector collector;

    @BeforeEach
    void setup() {
        this.collector = new VoteByBookCollector();
    }

    @Test
    void supplierReturnsEmpty() {
        final Map<UUID, Collection<UserDto>> result = collector.supplier().get();

        assertTrue(result.isEmpty(), "The initial Map should be empty.");
    }

    @Test
    void accumulatorAddsUser() {
        Map<UUID, Collection<UserDto>> map = new HashMap<>(1);
        Vote vote = new Vote();
        var bookId = UUID.randomUUID();
        vote.setBookId(bookId);
        var user = new User();
        var userId = UUID.randomUUID();
        user.setId(userId);
        vote.setUserById(user);

        collector.accumulator().accept(map, vote);

        Optional<UserDto> resultUserId = map.get(bookId)
            .stream()
            .findFirst();
        assertTrue(resultUserId.isPresent(),
                "The voter should have been added to the book.");
        assertEquals(resultUserId.get().getId(), userId,
                "The voter should have the same ID.");
    }

    @Test
    void accumulatorDoesNotAddNull() {
        Map<UUID, Collection<UserDto>> map = new HashMap<>(1);
        Vote vote = new Vote();
        var bookId = UUID.randomUUID();
        vote.setBookId(bookId);

        collector.accumulator().accept(map, vote);

        assertTrue(CollectionUtils.isEmpty(map.get(bookId)),
                "Null voters should not be added.");
    }

    @Test
    void combinerMergesMaps() {
        Map<UUID, Collection<UserDto>> map1 = Map.of(UUID.randomUUID(),
                List.of());
        Map<UUID, Collection<UserDto>> map2 = Map.of(UUID.randomUUID(),
                List.of());

        Map<UUID, Collection<UserDto>> result = collector.combiner()
            .apply(map1, map2);

        assertEquals(2, result.size(),
                "The input maps should have been merged.");
    }

    @Test
    void combineriMergesLists() {
        var bookId = UUID.randomUUID();
        Map<UUID, Collection<UserDto>> map1 = Map.of(bookId,
                List.of(mock(UserDto.class)));
        Map<UUID, Collection<UserDto>> map2 = Map.of(bookId,
                List.of(mock(UserDto.class)));

        Map<UUID, Collection<UserDto>> result = collector.combiner()
            .apply(map1, map2);

        assertEquals(2, result.get(bookId).size(),
                "The voter collections for the same book should have been merged.");

    }

    @Test
    void finisherChangesNothing() {
        Map<UUID, Collection<UserDto>> map = Map.of();

        Map<UUID, Collection<UserDto>> result = collector.finisher().apply(map);

        assertEquals(map, result, "The finisher should not change anything.");
    }

    @Test
    void collectorHasNoCharacteristics() {
        Set<Characteristics> result = collector.characteristics();

        assertTrue(result.isEmpty(),
                "The collector should have no characteristics.");
    }
}

