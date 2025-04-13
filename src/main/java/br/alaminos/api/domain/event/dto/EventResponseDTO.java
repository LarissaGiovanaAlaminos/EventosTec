package br.alaminos.api.domain.event.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

public record EventResponseDTO(
        UUID id,
        String title,
        String description,
        Date date,
        String city,
        String state,
        Boolean remote,
        String eventUrl,
        String imgUrl
) {
}
