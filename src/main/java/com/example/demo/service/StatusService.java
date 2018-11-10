package com.example.demo.service;

import com.example.demo.domain.Status;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.StatusRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatusService {

    private final StatusRepository statusRepository;

    @Autowired
    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public List<Status> findAll() {
        return statusRepository.findAll();
    }

    public Status create(Status status) {
        status.setCreated(LocalDateTime.now());
        return statusRepository.save(status);
    }

    public Status update(String id, Status status) {
        Status statusFromDb = getFromDbById(id);

        BeanUtils.copyProperties(status, statusFromDb, "id", "created", "removed");

        return statusRepository.save(statusFromDb);
    }

    public void delete(String id) {
        statusRepository.delete(getFromDbById(id));
    }


    private Status getFromDbById(String id) {

        Status status = statusRepository.findById(id).orElseThrow(NotFoundException::new);

        return status;
    }
}
