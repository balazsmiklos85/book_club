package hu.bmiklos.bc.controller.mapper;

import hu.bmiklos.bc.controller.dto.ProfileInformation;
import hu.bmiklos.bc.service.dto.UserDto;

public class UserMapper {
    private UserMapper() {
    }

    public static ProfileInformation mapToProfileInformation(UserDto user) {
        return new ProfileInformation(user.getName(), user.getExternalId(), user.getEmails());
    }
}