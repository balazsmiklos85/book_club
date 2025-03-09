package hu.bmiklos.bc.controller.mapper;

import java.util.Collection;
import java.util.Optional;

import org.springframework.core.convert.converter.Converter;

import hu.bmiklos.bc.service.dto.UserDto;

public class VotersHasher implements Converter<Collection<UserDto>, Collection<String>> {

    private VoterHasher voterHasher;

    public VotersHasher() {
        this.voterHasher = new VoterHasher();
    }

    @Override
    public Collection<String> convert(Collection<UserDto> source) {
        return source.stream()
            .map(voterHasher::convert)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }
}
