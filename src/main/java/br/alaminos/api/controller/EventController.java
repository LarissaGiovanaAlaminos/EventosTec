package br.alaminos.api.controller;

import br.alaminos.api.domain.event.Event;
import br.alaminos.api.domain.event.dto.EventRequestDTO;
import br.alaminos.api.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/event")
public class EventController {

    @Autowired
    private EventService service;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Event> create(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("date") Long date,
            @RequestParam("city") String city,
            @RequestParam("state") String state,
            @RequestParam("remote") Boolean remote,
            @RequestParam("eventUrl") String eventUrl,
            @RequestParam(value = "image", required = false)MultipartFile image
            ){
        EventRequestDTO eventRequestDTO = new EventRequestDTO(title, description, date, city, state, remote, eventUrl, image);
        return ResponseEntity.ok(service.createEvent(eventRequestDTO));
    }
}
