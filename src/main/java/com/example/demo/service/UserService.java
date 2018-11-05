package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

//Validate user
    public User create(User user) {
        user.setCreated(LocalDateTime.now());
        user.setActive(true);
        return userRepository.save(user);
    }

    public User read(Long id) {
        return getFromDbById(id);
    }

//Solve concurrent requests problem
//Validate user
    public User update(Long id, User user) {
        User userFromDb = getFromDbById(id);

        BeanUtils.copyProperties(user, userFromDb, "id", "created", "removed");
        userFromDb.setActive(true);

        return userRepository.save(userFromDb);
    }

    public void delete(Long id) {
        userRepository.delete(getFromDbById(id));
    }

    private User getFromDbById(Long id) {
        User user = userRepository.findById(id).orElseThrow(NotFoundException::new);
        return user;
    }
}
