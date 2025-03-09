package hu.bmiklos.bc.controller.mapper;

import java.util.Optional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import hu.bmiklos.bc.service.dto.UserDto;
import hu.bmiklos.bc.controller.mapper.NormalizedHashGenerator;

public class VoterHasher implements Converter<UserDto, Optional<String>> {

    private final NormalizedHashGenerator hasher;

    public VoterHasher() {
        this.hasher = new NormalizedHashGenerator();
    }

    @Override
    @NonNull
    public Optional<String> convert(final UserDto source) {
        return source.getEmails()
            .stream()
            .map(hasher::apply)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    }
}
