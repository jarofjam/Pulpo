package com.example.demo.service;

import com.example.demo.domain.Status;
import com.example.demo.dto.StatusDto;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.StatusRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatusService {

    private final StatusRepository statusRepository;

    @Autowired
    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public List<StatusDto> findAll() {
        List<Status> statuses = statusRepository.findAll();
        List<StatusDto> statusDtos = new ArrayList<>();

        for (Status status :statuses) {
            statusDtos.add(statusToStatusDto(status));
        }

        return statusDtos;
    }

    public StatusDto create(StatusDto statusDto) {
        Status status = statusDtoToStatus(statusDto);

        status.setCreated(LocalDateTime.now());

        return statusToStatusDto(statusRepository.save(status));
    }

    public StatusDto read(String id) {
        return statusToStatusDto(findById(id));
    }

    public StatusDto update(String id, StatusDto statusDto) {
        Status status = findById(id);

        if (statusDto.getRemove()) {
            status.setRemoved(LocalDateTime.now());
        } else {
            status = statusDtoToStatus(statusDto);
        }

        return statusToStatusDto(statusRepository.save(status));
    }

    public void delete(String id) {
        statusRepository.delete(findById(id));
    }


    private Status findById(String id) {
        return statusRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    private Status statusDtoToStatus(StatusDto statusDto) {
        Status status = new Status();

        BeanUtils.copyProperties(statusDto, status, "remove");

        return status;
    }

    private StatusDto statusToStatusDto(Status status) {
        StatusDto statusDto = new StatusDto();

        BeanUtils.copyProperties(status, statusDto, "requests");

        return statusDto;
    }
}
