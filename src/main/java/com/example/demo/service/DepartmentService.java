package com.example.demo.service;

import com.example.demo.domain.Department;
import com.example.demo.dto.DepartmentDto;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Autowired
    DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
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
        Department department = departmentDtoToDepartment(departmentDto, new Department());

        department.setCreated(LocalDateTime.now());

        return departmentToDepartmentDto(departmentRepository.save(validate(department)));
    }

    public DepartmentDto read(Long id) {
        return departmentToDepartmentDto(findDepartmentById(id));
    }

    public DepartmentDto update(Long id, DepartmentDto departmentDto) {
        Department department = findDepartmentById(id);

        if (department.getRemoved() != null) {
            return departmentToDepartmentDto(department);
        }

        if (departmentDto.getRemove()) {
            department.setRemoved(LocalDateTime.now());
            departmentDto.setRemoved(LocalDateTime.now());
        } else {
            department = departmentDtoToDepartment(departmentDto, department);
        }

        return departmentToDepartmentDto(departmentRepository.save(validate(department)));
    }

    public void delete(Long id) {
        departmentRepository.delete(findDepartmentById(id));
    }

//Additional
    private Department findDepartmentById(Long id) {
        return departmentRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    private Department findDepartmentByName(String name) {
        return departmentRepository.findByName(name);
    }

    private Department departmentDtoToDepartment(@NotNull DepartmentDto departmentDto, Department department) {
        return GeneralMethods.convert(departmentDto, department, Arrays.asList("id", "created", "removed", "remove"));
    }

    private DepartmentDto departmentToDepartmentDto(@NotNull Department department) {
        return GeneralMethods.convert(department, new DepartmentDto(), Arrays.asList("users", "managers", "requests"));
    }

    private Department validate(@NotNull Department department) {

        if (department.getName() == null || department.getDescription() == null) {
            throw new BadRequestException();
        }

        Department departmentFromDb = findDepartmentByName(department.getName());

        if (departmentFromDb != null && departmentFromDb.getId() != department.getId()) {
            throw new BadRequestException();
        }

        return department;
    }

}
