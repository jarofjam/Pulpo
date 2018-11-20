package com.example.demo.service;

import com.example.demo.domain.Department;
import com.example.demo.domain.User;
import com.example.demo.dto.UserDto;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public UserService(UserRepository userRepository, DepartmentRepository departmentRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();

        for (User user :users) {
            userDtos.add(userToUserDto(user));
        }

        return userDtos;
    }

    public UserDto create(UserDto userDto) {
        User user = userDtoToUser(userDto);

        user.setCreated(LocalDateTime.now());
        user.setActive(true);

        return userToUserDto(userRepository.save(user));
    }

    public UserDto read(Long id) {
        return userToUserDto(findById(id));
    }

    public UserDto update(Long id, UserDto userDto) {
        User user = findById(id);
        userDto.setId(id);

        if (userDto.getRemove()) {
            user.setRemoved(LocalDateTime.now());
            userDto.setRemoved(LocalDateTime.now());
        } else {
            user = userDtoToUser(userDto);
        }

        return userToUserDto(userRepository.save(user));
    }

    public void delete(Long id) {
        userRepository.delete(findById(id));
    }

    private User findById(Long id) {
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    private User userDtoToUser(UserDto userDto) {
        User user = new User();

        BeanUtils.copyProperties(userDto, user, "removed", "created", "department", "managerOfDepartment", "remove");
        List<Department> departments = departmentRepository.findByName(userDto.getDepartment());
        if (departments.size() != 0) {
            user.setUserDepartment(departments.get(0));
        }

        departments = departmentRepository.findByName(userDto.getManagerOfDepartment());
        if (departments.size() != 0) {
            user.setManagerOfDepartment((departments.get(0)));
        }

        return user;
    }

    private UserDto userToUserDto(User user) {
        UserDto userDto = new UserDto();

        BeanUtils.copyProperties(user, userDto, "userDepartment", "managerOfDepartment", "active", "a", "requestsAuthor", "requestsPerformer");
        if (user.getUserDepartment() != null) {
            userDto.setDepartment(user.getUserDepartment().getName());
        }
        if (user.getManagerOfDepartment() != null) {
            userDto.setManagerOfDepartment(user.getManagerOfDepartment().getName());
        }

        return userDto;
    }
}
