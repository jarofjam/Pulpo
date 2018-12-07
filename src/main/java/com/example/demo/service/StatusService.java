package com.example.demo.service;

import com.example.demo.domain.Status;
import com.example.demo.dto.StatusDto;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.StatusRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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
        Status status = statusDtoToStatus(statusDto, new Status());

        status.setCreated(LocalDateTime.now());

        return statusToStatusDto(statusRepository.save(validate(status)));
    }

    public StatusDto read(Long id) {
        return statusToStatusDto(findStatusById(id));
    }

    public StatusDto update(Long id, StatusDto statusDto) {
        Status status = findStatusById(id);

        if (status.getRemoved() != null) {
            return statusToStatusDto(status);
        }

        if (statusDto.getRemove()) {
            status.setRemoved(LocalDateTime.now());
        } else {
            status = statusDtoToStatus(statusDto, status);
        }

        status.setId(id);
        return statusToStatusDto(statusRepository.save(validate(status)));
    }

    public void delete(Long id) {
        statusRepository.delete(findStatusById(id));
    }


    private Status findStatusById(Long id) {
        return statusRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    private Status findStatusByName(String name) {
        return statusRepository.findByName(name);
    }

    private Status statusDtoToStatus(@NotNull StatusDto statusDto, Status status) {
        return GeneralMethods.convert(statusDto, status, Arrays.asList("id", "created", "removed", "remove"));
    }

    private StatusDto statusToStatusDto(@NotNull Status status) {
        return GeneralMethods.convert(status, new StatusDto(), Arrays.asList("requests"));
    }

    private Status validate(@NotNull Status status) {

        if(status.getName() == null || status.getDescription() == null) {
            throw new BadRequestException();
        }

        Status statusFromDb = findStatusByName(status.getName());
        if (statusFromDb != null && statusFromDb.getId() != status.getId()) {
            throw new BadRequestException();
        }

        return status;
    }

}
