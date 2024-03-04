package hu.bmiklos.bc.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.model.Vote;
import hu.bmiklos.bc.service.dto.UserDto;
import hu.bmiklos.bc.service.mapper.UserMapper;

public class VoteByBookCollector implements Collector<Vote, Map<UUID, Collection<UserDto>>, Map<UUID, Collection<UserDto>>> {

    @Override
    public Supplier<Map<UUID, Collection<UserDto>>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<UUID, Collection<UserDto>>, Vote> accumulator() {
        return (map, vote) -> {
            final User voter = Optional.ofNullable(vote.getUserById())
                .orElse(vote.getUserByExternalId());
            if (voter != null) {
                final UUID userId = vote.getBookId();
                final Collection<UserDto> userList = map.getOrDefault(userId, new ArrayList<>());
                userList.add(UserMapper.mapToDto(voter));
                map.put(userId, userList);
            }
        };
    }

    @Override
    public BinaryOperator<Map<UUID, Collection<UserDto>>> combiner() {
        return (map1, map2) -> {
            map1.putAll(map2);
            return map1;
        };
    }

    @Override
    public Function<Map<UUID, Collection<UserDto>>, Map<UUID, Collection<UserDto>>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

}

