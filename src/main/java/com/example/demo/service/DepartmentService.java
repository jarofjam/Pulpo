package com.example.demo.service;

import com.example.demo.domain.Department;
import com.example.demo.dto.DepartmentDto;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Autowired
    DepartmentService(DepartmentRepository departmentRepository, UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    public List<DepartmentDto> findAll() {
        List<DepartmentDto> departmentDtos = new ArrayList<>();
        List<Department> departments = departmentRepository.findAll();

        for (Department department :departments) {
            DepartmentDto departmentDto = departmentToDepartmentDto(department);
            departmentDtos.add(departmentDto);
        }

        return departmentDtos;
    }

    public DepartmentDto create(DepartmentDto departmentDto) {
        Department department = departmentDtoToDepartment(departmentDto);
        department.setCreated(LocalDateTime.now());

        return departmentToDepartmentDto(departmentRepository.save(department));
    }

    public DepartmentDto read(Long id) {
        return departmentToDepartmentDto(findById(id));
    }

    public DepartmentDto update(Long id, DepartmentDto departmentDto) {
        Department department = findById(id);
        departmentDto.setId(id);

        if (departmentDto.getRemove()) {
            department.setRemoved(LocalDateTime.now());
            departmentDto.setRemoved(LocalDateTime.now());
        } else {
            department = departmentDtoToDepartment(departmentDto);
        }

        return departmentToDepartmentDto(departmentRepository.save(department));
    }

    public void delete(Long id) {
        departmentRepository.delete(findById(id));
    }

    private Department findById(Long id) {
        return departmentRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public static Department departmentDtoToDepartment(DepartmentDto departmentDto) {
        Department department = new Department();

        BeanUtils.copyProperties(departmentDto, department, "remove", "created", "removed");

        return department;
    }

    public static DepartmentDto departmentToDepartmentDto(Department department) {
        DepartmentDto departmentDto = new DepartmentDto();

        BeanUtils.copyProperties(department, departmentDto, "users", "managers", "requests");

        return departmentDto;
    }

}
