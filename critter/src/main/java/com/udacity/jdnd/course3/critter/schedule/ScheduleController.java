package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.exception.CustomerNotFoundException;
import com.udacity.jdnd.course3.critter.exception.EmployeeNotFoundException;
import com.udacity.jdnd.course3.critter.exception.PetNotFoundException;
import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.PetService;
import com.udacity.jdnd.course3.critter.user.customer.CustomerService;
import com.udacity.jdnd.course3.critter.user.employee.Employee;
import com.udacity.jdnd.course3.critter.user.employee.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Schedules.
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final EmployeeService employeeService;
    private final CustomerService customerService;
    private final PetService petService;

    public ScheduleController(ScheduleService scheduleService, EmployeeService employeeService, CustomerService customerService, PetService petService) {
        this.scheduleService = scheduleService;
        this.employeeService = employeeService;
        this.petService = petService;
        this.customerService = customerService;
    }

    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDto) {
        List<Employee> employees = employeeService.findAllById(scheduleDto.getEmployeeIds());
        List<Pet> pets = petService.findAllById(scheduleDto.getPetIds());

        Schedule schedule = convertScheduleDtoToSchedule(scheduleDto);
        schedule.setEmployees(employees);
        schedule.setPets(pets);

        Schedule savedSchedule = scheduleService.save(schedule);

        employees.forEach(employee -> {
            employee.getSchedules().add(savedSchedule);
        });

        pets.forEach(pet -> {
            pet.getSchedules().add(savedSchedule);
        });

        return scheduleDto;
    }

    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        List<Schedule> schedules = scheduleService.findAll();
        return schedules.stream().map(this::convertScheduleToScheduleDto).collect(Collectors.toList());
    }

    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) throws PetNotFoundException {
        List<Schedule> schedules = petService.findById(petId).getSchedules();
        return schedules.stream().map(this::convertScheduleToScheduleDto).collect(Collectors.toList());
    }

    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) throws EmployeeNotFoundException {
        Employee employee = employeeService.findById(employeeId);
        if (employee.getSchedules() == null)
            return null;
        List<Schedule> schedules = employee.getSchedules();
        return schedules.stream().map(this::convertScheduleToScheduleDto).collect(Collectors.toList());
    }

    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) throws CustomerNotFoundException {
        List<Pet> pets = customerService.findById(customerId).getPets();
        HashMap<Long, Schedule> map = new HashMap<>();

        pets.forEach(pet -> {
            pet.getSchedules().forEach(schedule -> {
                map.put(schedule.getId(), schedule);
            });
        });

        List<Schedule> schedules = new ArrayList<>(map.values());
        return schedules.stream().map(this::convertScheduleToScheduleDto).collect(Collectors.toList());
    }

    private ScheduleDTO convertScheduleToScheduleDto(Schedule schedule) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        BeanUtils.copyProperties(schedule, scheduleDTO);
        scheduleDTO.setEmployeeIds(schedule.getEmployees().stream().map(Employee::getId).collect(Collectors.toList()));
        scheduleDTO.setPetIds(schedule.getPets().stream().map(Pet::getId).collect(Collectors.toList()));
        return scheduleDTO;
    }

    private Schedule convertScheduleDtoToSchedule(ScheduleDTO scheduleDto) {
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleDto, schedule);
        return schedule;
    }
}
