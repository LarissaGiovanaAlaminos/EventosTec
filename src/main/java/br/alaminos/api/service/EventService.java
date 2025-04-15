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
import java.time.Year;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.Date;

@Service
public class EventService {

    @Autowired
    private AmazonS3 s3Client;

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Autowired
    private EventRepository repository;

    @Autowired
    private AddressService addressService;

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

        if (Boolean.FALSE.equals(dto.remote())) {
            this.addressService.createAddress(dto, newEvent);
        }

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

    public List<EventResponseDTO> getUpcomingEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPage = this.repository.findUpcomingEvents(new Date(), pageable);
        return eventsPage.map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getAddress() != null ? event.getAddress().getCity() : "",
                        event.getAddress() != null ? event.getAddress().getUf() : "",
                        event.getRemote(),
                        event.getEventUrl(),
                        event.getImgUrl()
                )
        ).stream().toList();
    }

    public List<EventResponseDTO> getFilteredEvents(int page, int size, String title, String city, String uf, Date startDate, Date endDate) {

        Calendar cal = Calendar.getInstance();
        cal.set(Year.now().getValue() + 70, Calendar.JANUARY, 1);

        title = (title != null)? title:"";
        city = (city != null)? city:"";
        uf = (uf != null)? uf:"";
        startDate = (startDate != null)? startDate: new Date();
        endDate = (endDate != null)? endDate :cal.getTime();

        Pageable pageable = PageRequest.of(page, size);

        Page<Event> eventsPage = this.repository.findFilteredEvents(title, city, uf, startDate, endDate, pageable);
        return eventsPage.map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getAddress() != null ? event.getAddress().getCity() : "",
                        event.getAddress() != null ? event.getAddress().getUf() : "",
                        event.getRemote(),
                        event.getEventUrl(),
                        event.getImgUrl()
                )
        ).stream().toList();
    }
}
