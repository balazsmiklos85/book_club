package hu.bmiklos.bc.controller.mapper;

import java.util.Collection;

import org.springframework.core.convert.converter.Converter;

import hu.bmiklos.bc.controller.dto.BasicUser;
import hu.bmiklos.bc.service.dto.UserDto;

public class BasicUsersConverter
    implements Converter<Collection<UserDto>, Collection<BasicUser>> {

    @Override
    public Collection<BasicUser> convert(Collection<UserDto> source) {
        return source.stream()
            .map(new BasicUserConverter()::convert)
            .toList();
    }
}
