package hu.bmiklos.bc.controller;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
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

import hu.bmiklos.bc.controller.dto.CreateEventRequest;
import hu.bmiklos.bc.controller.dto.EventData;
import hu.bmiklos.bc.controller.dto.HostData;
import hu.bmiklos.bc.controller.mapper.DateTimeMapper;
import hu.bmiklos.bc.controller.mapper.EventMapper;
import hu.bmiklos.bc.controller.mapper.UserMapper;
import hu.bmiklos.bc.service.ActiveUserService;
import hu.bmiklos.bc.service.BookService;
import hu.bmiklos.bc.service.EventService;
import hu.bmiklos.bc.service.UserService;
import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.dto.CreateEventDto;
import hu.bmiklos.bc.service.dto.GetEventDto;
import hu.bmiklos.bc.service.dto.UserDto;

@Controller
@RequestMapping("/event")
public class EventController {
    @Autowired
    private ActiveUserService activeUserService;

    @Autowired
    private BookService bookService;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @GetMapping("/new")
    public ModelAndView newBookForm(@RequestParam String bookId) {
        BookDto book = bookService.getBookById(bookId);
        Optional<Instant> proposedDateTime = eventService.proposeNewTime();
        List<UserDto> users = userService.getUsers();

        ModelAndView modelAndView = new ModelAndView("event/new");
        modelAndView.addObject("author", book.getAuthor());
        modelAndView.addObject("title", book.getTitle());
        modelAndView.addObject("bookId", bookId);
        if (proposedDateTime.isPresent()) {
            modelAndView.addObject("proposedDate", DateTimeMapper.toLocalDateString(proposedDateTime.get()));
            modelAndView.addObject("proposedTime", DateTimeMapper.toLocalTimeString(proposedDateTime.get()));
        }
        modelAndView.addObject("host", book.getRecommender().getId());
        modelAndView.addObject("users", users);
        return modelAndView;
    }

    @GetMapping("/{eventId}")
    public ModelAndView showEvent(@PathVariable String eventId) {
        UUID eventUuid = UUID.fromString(eventId);
        GetEventDto event = eventService.getEvent(eventUuid);
        EventData eventData = EventMapper.mapToEventData(event, activeUserService.getUserId());
        List<UserDto> users = userService.getUsers();
        List<HostData> hosts = UserMapper.mapToHostData(users);

        ModelAndView modelAndView = new ModelAndView("event/edit");
        modelAndView.addObject("event", eventData);
        modelAndView.addObject("users", hosts);
        modelAndView.addObject("isAdmin", activeUserService.isAdmin());
        return modelAndView;
    }

    @PostMapping
    public ModelAndView createBook(@ModelAttribute CreateEventRequest event) {
        CreateEventDto eventDto = EventMapper.mapToDto(event);
        eventService.createEvent(eventDto);
        return new ModelAndView("redirect:/");
    }    
}
