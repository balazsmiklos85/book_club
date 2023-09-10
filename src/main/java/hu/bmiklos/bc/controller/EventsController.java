package hu.bmiklos.bc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import hu.bmiklos.bc.controller.dto.EventData;
import hu.bmiklos.bc.controller.mapper.EventMapper;
import hu.bmiklos.bc.service.ActiveUserService;
import hu.bmiklos.bc.service.EventService;
import hu.bmiklos.bc.service.dto.GetEventDto;

@Controller
@RequestMapping("/events")
public class EventsController {
    @Autowired
    private ActiveUserService activeUserService;

    @Autowired
    private EventService eventService;

    // TODO add integration test
    @GetMapping
    public ModelAndView getEvents() {
        List<GetEventDto> eventDtos = eventService.getEvents();
        List<EventData> events = EventMapper.mapToEventData(eventDtos, activeUserService.getUserId());
        ModelAndView modelAndView = new ModelAndView("event/list");
        modelAndView.addObject("events", events);
        modelAndView.addObject("isAdmin", activeUserService.isAdmin());
        return modelAndView;
    }
}