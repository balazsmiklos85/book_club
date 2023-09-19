package hu.bmiklos.bc.controller.mapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import hu.bmiklos.bc.controller.dto.HostData;
import hu.bmiklos.bc.controller.dto.ProfileInformation;
import hu.bmiklos.bc.service.dto.GetEventDto;
import hu.bmiklos.bc.service.dto.UserDto;

public class UserMapper {
    private UserMapper() {
    }

    public static ProfileInformation mapToProfileInformation(UserDto user) {
        return new ProfileInformation(user.getName(), user.getExternalId(), user.getEmails());
    }

    public static List<HostData> mapToHostData(List<UserDto> users) {
        return users.stream()
                .filter(user -> user.getId() != null)
                .map(user -> new HostData(getId(user), getName(user)))
                .toList();
    }

    public static HostData mapToHostData(GetEventDto event, UUID currentUser) {
        UserDto user = event.getHost();
        boolean userHosted = Optional.ofNullable(event.getHost())
                .map(UserDto::getId)
                .map(id -> id.equals(currentUser))
                .orElse(false);
        return new HostData(getId(user), getNameOrExternalId(event), userHosted);
    }

    private static String getNameOrExternalId(GetEventDto eventDto) {
        return Optional.ofNullable(eventDto.getHost())
            .map(UserDto::getName)
            .orElse(getHostExternalId(eventDto));
    }

    private static String getHostExternalId(GetEventDto eventDto) {
        String hostExternalId = Optional.ofNullable(eventDto.getHostExternalId())
            .map(Object::toString)
            .orElse("N/A");
        return "[" + hostExternalId + "]";
    }

    private static String getName(UserDto user) {
        return Optional.ofNullable(user)
                .map(UserDto::getName)
                .orElse("N/A");
    }

    private static String getId(UserDto user) {
        return Optional.ofNullable(user)
                .map(UserDto::getId)
                .map(UUID::toString)
                .orElse(null);
    }
}