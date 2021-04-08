package com.udacity.jdnd.course3.critter.user.employee;

import com.udacity.jdnd.course3.critter.exception.EmployeeNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee findById(long employeeId) throws EmployeeNotFoundException {
        return employeeRepository.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException("Employee with id " + employeeId + " was not found"));
    }

    public List<Employee> findEmployeesForService(EmployeeRequestDTO employeeDTO) {
        List<Employee> employees = employeeRepository.findAll();
        List<Employee> employeesAvailable = new ArrayList<>();
        for (Employee employee : employees) {
            if (employee.getSkills().containsAll(employeeDTO.getSkills()) && employee.getDaysAvailable().contains(employeeDTO.getDate().getDayOfWeek())) {
                employeesAvailable.add(employee);
            }
        }
        return employeesAvailable;
    }

    public List<Employee> findAllById(List<Long> employeeIds) {
        return employeeRepository.findAllById(employeeIds);
    }
}
