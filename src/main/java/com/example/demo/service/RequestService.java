package com.example.demo.service;

import com.example.demo.domain.Department;
import com.example.demo.domain.Request;
import com.example.demo.domain.Status;
import com.example.demo.domain.User;
import com.example.demo.dto.RequestDto;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.RequestRepository;
import com.example.demo.repository.StatusRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final StatusRepository statusRepository;

    @Autowired RequestService(RequestRepository requestRepository, UserRepository userRepository, DepartmentRepository departmentRepository, StatusRepository statusRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.statusRepository = statusRepository;
    }

//Customer
    public RequestDto create(RequestDto requestDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername);

        Request request = requestDtoToRequest(requestDto);

        request.setRequestAuthor(currentUser);
        request.setCreated(LocalDateTime.now());
        request.setRequestStatus(statusRepository.findById("CHECKED").orElseThrow(NotFoundException::new));

        return requestToRequestDto(requestRepository.save(request));
    }

//    public Request updateByClient(Long id, Request request) {
//
//        Request requestFromDb = findInDbById(id);
//
//        if (requestFromDb.getRemoved() != null) {
//            return requestFromDb;
//        }
//
//        if (request.getDescription() == null) {
//            requestFromDb.setStatus("CANCELED");
//            requestFromDb.setRemoved(LocalDateTime.now());
//            requestFromDb.setCancelInfo("заказчиком");
//        } else {
//            requestFromDb.setDescription(request.getDescription());
//        }
//
//        return requestRepository.save(requestFromDb);
//    }

//    public List<Request> findAllByAuthorAndStatus(String status) {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUsername = authentication.getName();
//        User currentUser = userRepository.findByUsername(currentUsername);
//
//        if("ALL".equals(status)) {
//            return requestRepository.findAllByClient(String.valueOf(currentUser.getId()));
//        } else {
//            return requestRepository.findAllByClientAndStatus(String.valueOf(currentUser.getId()), status);
//        }
//    }

//Performer
//    public List<Request> findAllByPerformerDepartment() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUsername = authentication.getName();
//        User currentUser = userRepository.findByUsername(currentUsername);
//
//        return requestRepository.findAllByPerformerDepartment(currentUser.getDepartment());
//    }

//    public List<Request> findAllByPerformer() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUsername = authentication.getName();
//        User currentUser = userRepository.findByUsername(currentUsername);
//
//        return requestRepository.findAllByPerformer(String.valueOf(currentUser.getId()));
//    }

//    public Request updatePerformerStatusComment(Long id, Request request) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUsername = authentication.getName();
//        User currentUser = userRepository.findByUsername(currentUsername);
//
//        Request requestFromDb = findInDbById(id);
//
//        requestFromDb.setPerformer(String.valueOf(currentUser.getId()));
//        requestFromDb.setStatus(request.getStatus());
//        requestFromDb.setComment(request.getComment());
//
//        return requestRepository.save(requestFromDb);
//    }

//Moderator
    public List<RequestDto> findAll() {
        List<Request> requests = requestRepository.findAll();
        List<RequestDto> requestDtos = new ArrayList<>();

        for (Request request :requests) {
            requestDtos.add(requestToRequestDto(request));
        }

        return requestDtos;
}

    public void delete(Long id) {
        requestRepository.delete(findById(id));
    }


//Additional
    private Request findById(Long id) {
        return requestRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    private RequestDto requestToRequestDto(Request request) {
        RequestDto requestDto = new RequestDto();

        BeanUtils.copyProperties(request, requestDto, "requestDepartment", "requestAuthor", "requestPerformer", "requestStatus");
        if (request.getRequestDepartment() != null) {
            requestDto.setDepartment(request.getRequestDepartment().getName());
        }
        if (request.getRequestAuthor() != null) {
            requestDto.setAuthor(request.getRequestAuthor().getRealName());
        }
        if (request.getRequestPerformer() != null) {
            requestDto.setPerformer(request.getRequestPerformer().getRealName());
        }
        if (request.getRequestStatus() != null) {
            requestDto.setStatus(request.getRequestStatus().getName());
        }

        return requestDto;
    }

    private Request requestDtoToRequest(RequestDto requestDto) {
        Request request = new Request();

        BeanUtils.copyProperties(requestDto, request, "remove", "created", "removed", "department", "author", "performer", "status");

        List<Department> departments = departmentRepository.findByName(requestDto.getDepartment());
        if (departments.size() != 0) {
            request.setRequestDepartment(departments.get(0));
        }
        List<User> users = userRepository.findByRealName(requestDto.getAuthor());
        if (users.size() != 0) {
            request.setRequestAuthor(users.get(0));
        }
        users = userRepository.findByRealName(requestDto.getPerformer());
        if (users.size() != 0) {
            request.setRequestPerformer(users.get(0));
        }
        List<Status> statuses = statusRepository.findAllByName(requestDto.getStatus());
        if (statuses.size() != 0) {
            request.setRequestStatus(statuses.get(0));
        }

        return request;
    }

}
