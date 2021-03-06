package com.javacodingskill.spring.batch.example4.processor;

import java.util.Random;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.javacodingskill.spring.batch.example4.dto.EmployeeDTO;
import com.javacodingskill.spring.batch.example4.model.Employee;

@Component
public class EmployeeProcessor implements ItemProcessor<EmployeeDTO, Employee> {

	@Override
	public Employee process(EmployeeDTO employeeDTO) throws Exception {
		Employee employee = new Employee();
		employee.setEmployeeId(employeeDTO.getEmployeeId() + new Random().nextInt(10000000));
		employee.setFirstName(employeeDTO.getFirstName());
		employee.setLastName(employeeDTO.getLastName());
		employee.setEmail(employeeDTO.getEmail());
		employee.setAge(employeeDTO.getAge());
		System.out.println("Inside processor -" + employee.toString());
		return employee;
	}

}
