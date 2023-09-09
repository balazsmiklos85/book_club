package hu.bmiklos.bc.service.mapper;

import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.service.dto.UserDto;

public class UserMapper {

    private UserMapper() {}

    public static UserDto mapToDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getExternalId());
    }    
}
