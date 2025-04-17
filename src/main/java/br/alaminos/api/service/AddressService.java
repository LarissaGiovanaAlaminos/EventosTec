package br.alaminos.api.service;

import br.alaminos.api.domain.address.Address;
import br.alaminos.api.domain.event.Event;
import br.alaminos.api.domain.event.dto.EventRequestDTO;
import br.alaminos.api.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    @Autowired
    private AddressRepository repository;

    public Address createAddress(EventRequestDTO dto, Event event){
        Address newAddress = new Address();
        newAddress.setCity(dto.city());
        newAddress.setUf(dto.state());
        newAddress.setEvent(event);
        return repository.save(newAddress);
    }
}
