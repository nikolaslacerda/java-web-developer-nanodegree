package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.exception.PetNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet save(Pet pet) {
        return petRepository.save(pet);
    }

    public Pet findById(long petId) throws PetNotFoundException {
        return petRepository.findById(petId).orElseThrow(() -> new PetNotFoundException("Pet with id " + petId + " was not found"));
    }

    public List<Pet> findAll() {
        return petRepository.findAll();
    }

    public List<Pet> findPetsByOwner(long ownerId) {
        return petRepository.findByOwnerId(ownerId);
    }

    public List<Pet> findAllById(List<Long> petIds) {
        return petRepository.findAllById(petIds);
    }
}
