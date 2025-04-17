package br.alaminos.api.controllers;

import br.alaminos.api.domain.coupon.Coupon;
import br.alaminos.api.domain.coupon.dto.CouponRequestDTO;
import br.alaminos.api.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    @Autowired
    CouponService service;

    @PostMapping("/event/{eventId}")
    public ResponseEntity<Coupon> addCouponToEvent (@PathVariable UUID eventId, @RequestBody CouponRequestDTO dto){
        return ResponseEntity.ok(service.addCouponToEvent(eventId, dto));
    }
}
