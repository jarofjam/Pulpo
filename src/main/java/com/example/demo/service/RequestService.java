package com.example.demo.service;

import com.example.demo.domain.Department;
import com.example.demo.domain.Request;
import com.example.demo.domain.Status;
import com.example.demo.domain.User;
import com.example.demo.dto.RequestDto;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.RequestRepository;
import com.example.demo.repository.StatusRepository;
import com.example.demo.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final StatusRepository statusRepository;

    @Autowired
    RequestService(RequestRepository requestRepository, UserRepository userRepository, DepartmentRepository departmentRepository, StatusRepository statusRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.statusRepository = statusRepository;
    }

//Applicant
    public RequestDto create(RequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = findUserByUsername(currentUsername);

        //Check

        Request request = requestDtoToRequest(requestDto, new Request());

        request.setRequestAuthor(currentUser);
        request.setCreated(LocalDateTime.now());
        request.setRequestStatus(findStatusByName("New"));

        return requestToRequestDto(requestRepository.save(validate(request)));
    }

    public RequestDto updateByApplicant(Long id, RequestDto requestDto) {
        Request request = findRequestById(id);

        //Check permission
        if (request.getRemoved() != null) {
            throw new ForbiddenException();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = findUserByUsername(currentUsername);

        if (request.getRequestAuthor() != null && request.getRequestAuthor().getId() != currentUser.getId()) {
            throw new ForbiddenException();
        }

        //Update
        if (requestDto.getRemove()) {
            request.setRequestStatus(findStatusByName("Canceled"));
            request.setRemoved(LocalDateTime.now());
            request.setCancelInfo("by applicant");
        } else {
            Request requestFromApplicant = requestDtoToRequest(requestDto, new Request());
            if (requestFromApplicant.getDescription() != null) {
                request.setDescription(requestFromApplicant.getDescription());
            }
        }

        return requestToRequestDto(requestRepository.save(validate(request)));
    }

    public List<RequestDto> findAllByAuthorAndDepartmentAndStatus(String departmentName, String statusName) {
        List<Request> requests;
        Status status;
        Department department;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = findUserByUsername(currentUsername);

        if("All".equals(statusName)) {
            if ("All".equals(departmentName)) {
                requests = requestRepository.findAllByRequestAuthor(currentUser);
            } else {
                department = findDepartmentByName(departmentName);
                requests = requestRepository.findAllByRequestAuthorAndRequestDepartment(currentUser, department);
            }
        } else {
            if ("All".equals(departmentName)) {
                status = findStatusByName(statusName);
                requests = requestRepository.findAllByRequestAuthorAndRequestStatus(currentUser, status);
            } else {
                status = findStatusByName(statusName);
                department = findDepartmentByName(departmentName);
                requests = requestRepository.findAllByRequestAuthorAndRequestDepartmentAndRequestStatus(currentUser, department, status);
            }
        }

        return requestListToRequestDtoList(requests);
    }

//Performer
    public List<RequestDto> findAllByPerformerDepartment() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = findUserByUsername(currentUsername);

        return requestListToRequestDtoList(requestRepository.findAllByRequestDepartment(currentUser.getUserDepartment()));
    }

    public List<RequestDto> findAllByPerformerAndStatus(String statusName) {
        List<Request> requests;
        Status status;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = findUserByUsername(currentUsername);

        if ("All".equals(statusName)) {
            requests = requestRepository.findAllByRequestPerformer(currentUser);
        } else {
            status = findStatusByName(statusName);
            requests = requestRepository.findAllByRequestPerformerAndRequestStatus(currentUser, status);
        }

        return requestListToRequestDtoList(requests);
    }

    public RequestDto updateByPerformer(Long id, RequestDto requestDto) {
        Request request = findRequestById(id);

        //Check permission
        if (request.getRemoved() != null) {
            throw new ForbiddenException();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = findUserByUsername(currentUsername);

        if (request.getRequestPerformer() != null && request.getRequestPerformer().getId() != currentUser.getId()) {
            throw new ForbiddenException();
        }

        if (request.getRequestDepartment() != null && request.getRequestDepartment().getId() != currentUser.getUserDepartment().getId()) {
            throw new ForbiddenException();
        }

        //Update
        if (requestDto.getRemove()) {
            request.setRequestStatus(findStatusByName("Canceled"));
            request.setRemoved(LocalDateTime.now());
            request.setCancelInfo("by performer");
            request.setRequestPerformer(currentUser);
        } else {
            Request requestFromPerformer = requestDtoToRequest(requestDto, new Request());
            if (requestFromPerformer.getComment() != null) {
                request.setComment(requestFromPerformer.getComment());
            }
            //Update status
            if ("Invalid".equals(requestDto.getStatus())) {
                request.setRequestStatus(findStatusByName("Invalid"));
            }
            if ("Finished".equals(requestDto.getStatus())) {
                request.setRequestStatus(findStatusByName("Finished"));
            }
            if ("Ongoing".equals((requestDto.getStatus()))) {
                request.setRequestStatus(findStatusByName("Ongoing"));
            }
        }

        request.setRequestPerformer(currentUser);
        return requestToRequestDto(requestRepository.save(validate(request)));
    }

//Moderator
    public List<RequestDto> findAllByDepartmentAndStatus(String departmentName, String statusName) {
        List<Request> requests;
        Department department;
        Status status;

        if("All".equals(statusName)) {
            if ("All".equals(departmentName)) {
                requests = requestRepository.findAll();
            } else {
                department = findDepartmentByName(departmentName);
                requests = requestRepository.findAllByRequestDepartment(department);
            }
        } else {
            if ("All".equals(departmentName)) {
                status = findStatusByName(statusName);
                requests = requestRepository.findAllByRequestStatus(status);
            } else {
                status = findStatusByName(statusName);
                department = findDepartmentByName(departmentName);
                requests = requestRepository.findAllByRequestDepartmentAndRequestStatus(department, status);
            }
        }

        return requestListToRequestDtoList(requests);
    }

    public RequestDto updateByModerator(Long id, RequestDto requestDto) {
        Request request = findRequestById(id);

        //Check permission
        if (request.getRemoved() != null) {
            throw new ForbiddenException();
        }

        //Update
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = findUserByUsername(currentUsername);

        if (requestDto.getRemove()) {
            request.setRequestStatus(findStatusByName("Canceled"));
            request.setRemoved(LocalDateTime.now());
            request.setCancelInfo("by moderator");
            request.setRequestModerator(currentUser);
        } else {
            Request requestFromModerator = requestDtoToRequest(requestDto, new Request());

            if (requestFromModerator.getTopic() != null) {
                request.setTopic(requestFromModerator.getTopic());
            }
            if (requestFromModerator.getComment() != null) {
                request.setComment(requestFromModerator.getComment());
            }
            if (requestFromModerator.getDeadline() != null) {
                request.setDeadline(requestFromModerator.getDeadline());
            }
            //Update status
            if ("Checked".equals(requestDto.getStatus())) {
                request.setRequestStatus(findStatusByName("Checked"));
            }
            if ("Invalid".equals(requestDto.getStatus())) {
                request.setRequestStatus(findStatusByName("Invalid"));
            }
        }

        request.setRequestModerator(currentUser);
        return requestToRequestDto(requestRepository.save(validate(request)));
    }

    public void delete(Long id) {
        requestRepository.delete(findRequestById(id));
    }

//Additional
    private Department findDepartmentByName(String name) {
        return departmentRepository.findByName(name);
    }

    private User findUserByUsername(String userName) {
        return userRepository.findByUsername(userName);
    }

    private Request findRequestById(Long id) {
        return requestRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    private Status findStatusByName(String name) {
        return statusRepository.findByName(name);
    }

    private RequestDto requestToRequestDto(@NotNull Request request) {
        RequestDto requestDto = GeneralMethods.convert(request, new RequestDto(), Arrays.asList("requestDepartment", "requestAuthor", "requestPerformer", "requestModerator", "requestStatus"));

        if (request.getRequestDepartment() != null) {
            requestDto.setDepartment(request.getRequestDepartment().getName());
        }
        if (request.getRequestAuthor() != null) {
            requestDto.setAuthor(request.getRequestAuthor().getRealName());
        }
        if (request.getRequestPerformer() != null) {
            requestDto.setPerformer(request.getRequestPerformer().getRealName());
        }
        if (request.getRequestModerator() != null) {
            requestDto.setModerator(request.getRequestModerator().getRealName());
        }
        if (request.getRequestStatus() != null) {
            requestDto.setStatus(request.getRequestStatus().getName());
        }

        return requestDto;
    }

    private Request requestDtoToRequest(@NotNull RequestDto requestDto, Request request) {
        request = GeneralMethods.convert(requestDto, request, Arrays.asList("id", "created", "removed", "finished", "remove", "department", "status", "author", "performer", "moderator", "action"));

        Department department;
        Status status;

        String departmentName;
        String statusName;

        departmentName = requestDto.getDepartment();
        if (departmentName != null) {
            department = findDepartmentByName(departmentName);
            if (department == null || department.getRemoved() != null) {
                throw new BadRequestException();
            } else {
                request.setRequestDepartment(department);
            }
        }

        statusName = requestDto.getStatus();
        if (statusName != null) {
            status = statusRepository.findByName(statusName);
            if (status == null || status.getRemoved() != null) {
                throw new BadRequestException();
            } else {
                request.setRequestStatus(status);
            }
        }

        return request;
    }

    private List<RequestDto> requestListToRequestDtoList(List<Request> requests) {
        List<RequestDto> requestDtos = new ArrayList<>();

        if (requests == null) {
            return requestDtos;
        }

        for (Request request :requests) {
            requestDtos.add(requestToRequestDto(request));
        }

        return requestDtos;
    }

    private Request validate(@NotNull Request request) {
        if (request.getTopic() == null ||
            request.getDescription() == null ||
            request.getRequestAuthor() == null ||
            request.getRequestStatus() == null ||
            request.getRequestDepartment() == null
        ) {
            throw new BadRequestException();
        }

        return request;
    }
}
