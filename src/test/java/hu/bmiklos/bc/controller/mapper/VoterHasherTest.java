package hu.bmiklos.bc.controller.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import hu.bmiklos.bc.service.dto.UserDto;

class VoterHasherTest {
    @Test
    void hashesEmail() {
        var hasher = new VoterHasher();
        var email = "email@test.hu";
        var user = new UserDto(UUID.randomUUID(), "Test User", -1,
                List.of(email));

        Optional<String> result = hasher.convert(user);

        assertEquals(
                "467b20733af40a5920f52fd2958208646dc62a3eae25dae8bd48bac02f7429f7",
                result.get(), "The e-mail address should have been hashed.");
    }
}
