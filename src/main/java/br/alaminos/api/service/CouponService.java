package br.alaminos.api.service;

import br.alaminos.api.domain.coupon.Coupon;
import br.alaminos.api.domain.coupon.dto.CouponRequestDTO;
import br.alaminos.api.repositories.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CouponService {

    @Autowired
    CouponRepository repository;

    @Autowired
    EventService eventService;

    public Coupon addCouponToEvent(UUID eventId, CouponRequestDTO dto){

        Coupon newCoupon = new Coupon();
        newCoupon.setCode(dto.code());
        newCoupon.setDiscount(dto.discount());
        newCoupon.setValid(dto.valid());
        newCoupon.setEvent(eventService.getEvent(eventId));

        return repository.save(newCoupon);
    }
}
