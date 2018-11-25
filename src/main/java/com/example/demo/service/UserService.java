package com.example.demo.service;

import com.example.demo.domain.Department;
import com.example.demo.domain.User;
import com.example.demo.dto.UserDto;
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
        User user = userDtoToUser(userDto, new User());

        user.setCreated(LocalDateTime.now());
        user.setActive(true);

        return userToUserDto(userRepository.save(validate(user)));
    }

    public UserDto read(Long id) {
        return userToUserDto(findUserById(id));
    }

    public UserDto update(Long id, UserDto userDto) {
        User user = findUserById(id);
//Check
        if (user.getRemoved() != null) {
            return userToUserDto(user);
        }

//Update
        if (userDto.getRemove()) {
            user.setRemoved(LocalDateTime.now());
            user.setActive(false);
        } else {
            user = userDtoToUser(userDto, user);
            user.setActive(true);
        }

        return userToUserDto(userRepository.save(validate(user)));
    }

    public void delete(Long id) {
        userRepository.delete(findUserById(id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private User userDtoToUser(@NotNull UserDto userDto, User user) {
        user = GeneralMethods.convert(userDto, user, Arrays.asList("id", "removed", "created", "department", "managerOfDepartment", "remove"));

        Department department;
        String departmentName;

        departmentName = userDto.getDepartment();
        if (departmentName != null) {
            department = departmentRepository.findByName(departmentName);
            if (department == null || department.getRemoved() != null) {
                throw new BadRequestException();
            } else {
                user.setUserDepartment(department);
            }
        }

        departmentName = userDto.getManagerOfDepartment();
        if (departmentName != null) {
            department = departmentRepository.findByName(departmentName);
            if (department == null || department.getRemoved() != null) {
                throw new BadRequestException();
            } else {
                user.setManagerOfDepartment(department);
            }
        }

        return user;
    }

    private UserDto userToUserDto(@NotNull User user) {

        UserDto userDto = GeneralMethods.convert(user, new UserDto(), Arrays.asList("userDepartment", "managerOfDepartment", "active", "a", "requestsAuthor", "requestsPerformer", "requestsModerator"));

        if (user.getUserDepartment() != null) {
            userDto.setDepartment(user.getUserDepartment().getName());
        }
        if (user.getManagerOfDepartment() != null) {
            userDto.setManagerOfDepartment(user.getManagerOfDepartment().getName());
        }

        return userDto;
    }

    private User validate(User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            throw new BadRequestException();
        }

        User userFromDb = findUserByUsername(user.getUsername());

        if (userFromDb != null && userFromDb.getId() != user.getId()) {
            throw new BadRequestException();
        }

        return user;
    }
}
