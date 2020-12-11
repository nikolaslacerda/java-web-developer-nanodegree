package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.exception.CustomerNotFoundException;
import com.udacity.jdnd.course3.critter.exception.PetNotFoundException;
import com.udacity.jdnd.course3.critter.user.customer.Customer;
import com.udacity.jdnd.course3.critter.user.customer.CustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Pets.
 */
@RestController
@RequestMapping("/pet")
public class PetController {

    private final PetService petService;
    private final CustomerService customerService;

    public PetController(PetService petService, CustomerService customerService) {
        this.petService = petService;
        this.customerService = customerService;
    }

    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) throws CustomerNotFoundException {
        Customer owner = customerService.findById(petDTO.getOwnerId());
        Pet pet = convertPetDtoToPet(petDTO);
        pet.setOwner(owner);
        Pet savedPet = petService.save(pet);
        owner.getPets().add(savedPet);
        return convertPetToPetDto(savedPet);
    }

    @GetMapping("/{petId}")
    public PetDTO getPet(@PathVariable long petId) throws PetNotFoundException {
        Pet pet = petService.findById(petId);
        return convertPetToPetDto(pet);
    }

    @GetMapping
    public List<PetDTO> getPets() {
        return petService.findAll().stream().map(this::convertPetToPetDto).collect(Collectors.toList());
    }

    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) throws CustomerNotFoundException {
        return customerService.findById(ownerId).getPets().stream().map(this::convertPetToPetDto).collect(Collectors.toList());
    }

    private PetDTO convertPetToPetDto(Pet pet) {
        PetDTO petDto = new PetDTO();
        BeanUtils.copyProperties(pet, petDto);
        petDto.setOwnerId(pet.getOwner().getId());
        return petDto;
    }

    private Pet convertPetDtoToPet(PetDTO petDto) {
        Pet pet = new Pet();
        BeanUtils.copyProperties(petDto, pet);
        return pet;
    }
}
