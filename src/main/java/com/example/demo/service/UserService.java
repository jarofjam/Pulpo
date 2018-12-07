package com.example.demo.service;

import com.example.demo.domain.Department;
import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.dto.UserDto;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

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
            throw new ForbiddenException();
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
        user = GeneralMethods.convert(userDto, user, Arrays.asList("id", "removed", "created", "department", "managerOfDepartment", "remove", "roles"));

        Department department;
        String departmentName;

//Set user department
        departmentName = userDto.getDepartment();
        if (departmentName != null) {
            department = departmentRepository.findByName(departmentName);
            if (department == null || department.getRemoved() != null) {
                throw new BadRequestException();
            } else {
                user.setUserDepartment(department);
            }
        }
//Set manager of department
        departmentName = userDto.getManagerOfDepartment();
        if (departmentName != null) {
            department = departmentRepository.findByName(departmentName);
            if (department == null || department.getRemoved() != null) {
                throw new BadRequestException();
            } else {
                user.setManagerOfDepartment(department);
            }
        }
//Set roles
        if (userDto.getRoles() != null) {
            Set<Role> oldRoles = user.getRoles();
            Set<Role> newRoles = new HashSet<>();

            Set<String> allowedRoles = Arrays.stream(Role.values())
                    .map(Role::name)
                    .collect(Collectors.toSet());

            for (String role :userDto.getRoles()) {
                if (allowedRoles.contains(role)) {
                    newRoles.add(Role.valueOf(role));
                }
            }

            if (newRoles.size() != 0) {
                user.setRoles(newRoles);
            } else {
                user.setRoles(oldRoles);
            }
        }

        return user;
    }

    private UserDto userToUserDto(@NotNull User user) {

        UserDto userDto = GeneralMethods.convert(user, new UserDto(), Arrays.asList("userDepartment", "managerOfDepartment", "active", "requestsAuthor", "templatesAuthor", "requestsPerformer", "requestsModerator", "roles"));

//Set user department
        if (user.getUserDepartment() != null) {
            userDto.setDepartment(user.getUserDepartment().getName());
        }
//Set manager of department
        if (user.getManagerOfDepartment() != null) {
            userDto.setManagerOfDepartment(user.getManagerOfDepartment().getName());
        }
//Set roles
        List<String> listRoles = new ArrayList<>();

        for (Role role :user.getRoles()) {
            listRoles.add(role.name());
        }
        userDto.setRoles(listRoles);

        return userDto;
    }

    private User validate(User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            throw new BadRequestException();
        }

        if (user.getRoles() == null || user.getRoles().size() == 0) {
            throw new BadRequestException();
        }

        User userFromDb = findUserByUsername(user.getUsername());

        if (userFromDb != null && userFromDb.getId() != user.getId()) {
            throw new BadRequestException();
        }

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }
}
