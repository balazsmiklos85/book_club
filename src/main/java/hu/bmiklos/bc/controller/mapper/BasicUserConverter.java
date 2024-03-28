package hu.bmiklos.bc.controller.mapper;

import java.util.Optional;

import org.springframework.core.convert.converter.Converter;

import hu.bmiklos.bc.controller.dto.BasicUser;
import hu.bmiklos.bc.service.dto.UserDto;

public class BasicUserConverter implements Converter<UserDto, BasicUser> {

    @Override
    public BasicUser convert(UserDto source) {
        Optional<String> email = source.getEmails().stream().findFirst();
        return new BasicUser(source.getName(), email.orElse("[no e-mail set]"));
    }
}

