package hu.bmiklos.bc.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.controller.dto.EditEventRequest;
import hu.bmiklos.bc.controller.dto.EventData;
import hu.bmiklos.bc.controller.dto.HostData;
import hu.bmiklos.bc.controller.dto.ParticipantData;
import hu.bmiklos.bc.controller.mapper.EventMapper;
import hu.bmiklos.bc.controller.mapper.UserMapper;
import hu.bmiklos.bc.service.ActiveUserService;
import hu.bmiklos.bc.service.BookService;
import hu.bmiklos.bc.service.EventService;
import hu.bmiklos.bc.service.ParticipantService;
import hu.bmiklos.bc.service.UserService;
import hu.bmiklos.bc.service.dto.CreateEventDto;
import hu.bmiklos.bc.service.dto.GetEventDto;
import hu.bmiklos.bc.service.dto.UserDto;
import hu.bmiklos.bc.web.controller.EventController;

/**
 * @deprecated Use {@link EventController} instead and move functionality there.
 */ 
@Controller
@RequestMapping("/event")
@Deprecated
public class DeprecatedEventController {
    @Autowired
    private ActiveUserService activeUserService;

    @Autowired
    private EventService eventService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private UserService userService;

    @GetMapping("/{eventId}")
    public ModelAndView showEvent(@PathVariable String eventId) {
        UUID eventUuid = UUID.fromString(eventId);
        GetEventDto event = eventService.getEvent(eventUuid);
        EventData eventData = EventMapper.mapToEventData(event, activeUserService.getUserId());
        List<UserDto> users = userService.getUsers();
        List<HostData> hosts = UserMapper.mapToHostData(users);
        List<ParticipantData> participants = UserMapper.mapToParticipantData(event.getParticipants());

        ModelAndView modelAndView = new ModelAndView("event/edit");
        modelAndView.addObject("event", eventData);
        modelAndView.addObject("users", hosts);
        modelAndView.addObject("isAdmin", activeUserService.isAdmin());
        return modelAndView;
    }

    @PostMapping
    public ModelAndView editEvent(@ModelAttribute EditEventRequest event) {
        // TODO: non-admin users should be able to schedule events
        if (event.getId() == null) {
            CreateEventDto eventDto = EventMapper.mapToDto(event);
            UUID eventId = eventService.createEvent(eventDto);
            return new ModelAndView("redirect:/event/" + eventId);
        } else {
            if (activeUserService.isAdmin()) {
                eventService.editEvent(EventMapper.mapToEditDto(event));
                return new ModelAndView("redirect:/event/" + event.getId());
            }
        }
        return new ModelAndView("redirect:/");
    }

    @PostMapping("/{eventId}/participant/")
    public ModelAndView addParticipant(@PathVariable String eventId, @RequestParam String participant) {
        if (activeUserService.isAdmin()) {
            UUID eventUuid = UUID.fromString(eventId);
            participantService.addParticipant(eventUuid, participant);
        }
        return new ModelAndView("redirect:/event/" + eventId);
    }
    

}
