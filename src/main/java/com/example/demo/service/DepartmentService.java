package com.example.demo.service;

import com.example.demo.entity.Department;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    public Department create(Department department) {
        department.setCreated(LocalDateTime.now());
        return departmentRepository.save(department);
    }

    public Department read(Long id) {
        return getFromDbById(id);
    }

    public Department update(Long id, Department department) {
        Department departmentFromDb = getFromDbById(id);

        BeanUtils.copyProperties(department, departmentFromDb, "id", "created", "removed");

        return departmentRepository.save(departmentFromDb);
    }

    public void delete(Long id) {
        departmentRepository.delete(getFromDbById(id));
    }

    private Department getFromDbById(Long id) {
        Department department = departmentRepository.findById(id).orElseThrow(NotFoundException::new);
        return department;
    }
}
