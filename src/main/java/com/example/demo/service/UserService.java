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

//Validate user??
    public User create(User user) {
        user.setCreated(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User read(String id) {
        return getFromDbByStringId(id);
    }

//Solve concurrent requests problem
//Validate user??
    public User update(String id, User user) {
        User userFromDb = getFromDbByStringId(id);

        BeanUtils.copyProperties(user, userFromDb, "id");

        return userRepository.save(userFromDb);
    }

    public void delete(String id) {
        userRepository.delete(getFromDbByStringId(id));
    }

    private User getFromDbByStringId(String stringId) {
        long id;
        try {
            id = Long.parseLong(stringId);
        } catch (NumberFormatException e) {
            throw new BadRequestException();
        }

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        else throw new NotFoundException();
    }
}
