package br.alaminos.api.service;

import br.alaminos.api.domain.event.Event;
import br.alaminos.api.domain.event.dto.EventRequestDTO;
import br.alaminos.api.domain.event.dto.EventResponseDTO;
import br.alaminos.api.repositories.EventRepository;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class EventService {

    @Autowired
    private AmazonS3 s3Client;

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Autowired
    private EventRepository repository;

    public Event createEvent(EventRequestDTO dto) {
        String imgUrl = null;

        if (dto.image() != null) {
            imgUrl = this.uploadImg(dto.image());
        }

        Event newEvent = new Event();
        newEvent.setTitle(dto.title());
        newEvent.setDescription(dto.description());
        newEvent.setEventUrl(dto.eventUrl());
        newEvent.setDate(new Date(dto.date()));
        newEvent.setRemote(dto.remote());
        newEvent.setImgUrl(imgUrl);

        repository.save(newEvent);
        return newEvent;
    }

    private String uploadImg(MultipartFile image) {
        String filename = UUID.randomUUID() + image.getOriginalFilename();

        try {
            File file = this.convertMultipartToFile(image);
            s3Client.putObject(bucketName, filename, file);
            file.delete();
            return s3Client.getUrl(bucketName, filename).toString();
        } catch (Exception e) {
            System.out.println("Erro ao subir o arquivo");
        }
        return "";
    }

    private File convertMultipartToFile(MultipartFile image) throws IOException {
        File convFile = new File(Objects.requireNonNull(image.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(image.getBytes());
        fos.close();
        return convFile;
    }

    public Event getEvent(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
    }

    public List<EventResponseDTO> getEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPage = this.repository.findUpcomingEvents(new Date(), pageable);
        return eventsPage.map(event -> new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                "",
                "",
                event.getRemote(),
                event.getEventUrl(),
                event.getImgUrl()
        )
        ).stream().toList();
    }
}
