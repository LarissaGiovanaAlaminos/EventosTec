package br.alaminos.api.domain.coupon.dto;

import br.alaminos.api.domain.event.Event;

import java.util.Date;

public record CouponRequestDTO(
        String code,
        Integer discount,
        Date valid
) {
}
