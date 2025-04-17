package br.alaminos.api.service;

import br.alaminos.api.domain.coupon.Coupon;
import br.alaminos.api.domain.coupon.dto.CouponRequestDTO;
import br.alaminos.api.domain.event.Event;
import br.alaminos.api.repositories.CouponRepository;
import br.alaminos.api.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CouponService {

    @Autowired
    private CouponRepository repository;

    @Autowired
    private EventRepository eventRepository;

    public Coupon addCouponToEvent(UUID eventId, CouponRequestDTO couponData) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Coupon coupon = new Coupon();
        coupon.setCode(couponData.code());
        coupon.setDiscount(couponData.discount());
        coupon.setValid(new Date(couponData.valid()));
        coupon.setEvent(event);

        return repository.save(coupon);
    }

    public List<Coupon> consultCoupon(UUID id, Date date) {
        return repository.findByEventIdAndValidAfter(id, date);
    }
}
