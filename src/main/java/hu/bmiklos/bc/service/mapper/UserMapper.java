package hu.bmiklos.bc.service.mapper;

import java.util.List;
import java.util.UUID;

import hu.bmiklos.bc.model.Email;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.service.dto.UserDto;

public class UserMapper {

    private UserMapper() {}

    public static UserDto mapToDto(User user) {
        UUID userId = user.getId();
        String userName = user.getName();
        Integer externalId = user.getExternalId();
        List<String> emails = user.getEmails()
            .stream()
            .map(Email::getEmailAddress)
            .toList();
        return new UserDto(userId, userName, externalId, emails);
    }    
}
