package com.example.demo.service;

import com.example.demo.domain.Role;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

//Validate role
    public Role create(Role role) {
        role.setCreated(LocalDateTime.now());
        return roleRepository.save(role);
    }

//Solve concurrent requests problem
//Validate user
    public Role update(Long id, Role role) {
        Role roleFromDb = getFromDbById(id);

        BeanUtils.copyProperties(role, roleFromDb, "id", "created", "removed");

        return roleRepository.save(roleFromDb);
    }

    public Role read(Long id) {
        return getFromDbById(id);
    }

    public void delete(Long id) {
        roleRepository.delete(getFromDbById(id));
    }

    private Role getFromDbById(Long id) {
        Role user = roleRepository.findById(id).orElseThrow(NotFoundException::new);
        return user;
    }
}
