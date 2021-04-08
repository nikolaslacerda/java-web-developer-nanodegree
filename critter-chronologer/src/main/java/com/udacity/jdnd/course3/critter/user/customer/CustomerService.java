package com.udacity.jdnd.course3.critter.user.customer;

import com.udacity.jdnd.course3.critter.exception.CustomerNotFoundException;
import com.udacity.jdnd.course3.critter.exception.PetNotFoundException;
import com.udacity.jdnd.course3.critter.pet.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PetRepository petRepository;

    public CustomerService(CustomerRepository customerRepository, PetRepository petRepository) {
        this.customerRepository = customerRepository;
        this.petRepository = petRepository;
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer findById(Long customerId) throws CustomerNotFoundException {
        return customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer with id " + customerId + " was not found"));
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer findOwnerByPet(long petId) throws PetNotFoundException {
        return petRepository.findById(petId).orElseThrow(() -> new PetNotFoundException("Pet with id " + petId + " was not found")).getOwner();
    }
}
