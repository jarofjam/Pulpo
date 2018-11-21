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

//Applicant
    public RequestDto create(RequestDto requestDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername);

        Request request = requestDtoToRequest(requestDto);

        request.setRequestAuthor(currentUser);
        request.setCreated(LocalDateTime.now());
        request.setRequestStatus(findStatusById("CHECKED"));

        return requestToRequestDto(requestRepository.save(request));
    }

    public RequestDto updateByApplicant(Long id, RequestDto requestDto) {

        Request request = findRequestById(id);

        if (requestDto.getRemove()) {
            request.setRequestStatus(findStatusById("CANCELED"));
            request.setRemoved(LocalDateTime.now());
            request.setCancelInfo("by applicant");
        } else {
            Request requestFromApplicant = requestDtoToRequest(requestDto);
            if (requestFromApplicant.getDescription() != null) {
                request.setDescription(requestFromApplicant.getDescription());
            }
        }

        return requestToRequestDto(requestRepository.save(request));
    }

    public List<RequestDto> findAllByAuthorAndDepartmentAndStatus(String departmentName, String statusName) {
        List<Request> requests;
        Status status;
        Department department;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername);

        if("ALL".equals(statusName)) {
            if ("NONE".equals(departmentName)) {
                requests = requestRepository.findAllByRequestAuthor(currentUser);
            } else {
                department = findDepartmentByName(departmentName);
                requests = requestRepository.findAllByRequestAuthorAndRequestDepartment(currentUser, department);
            }
        } else {
            if ("NONE".equals(departmentName)) {
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
        User currentUser = userRepository.findByUsername(currentUsername);

        return requestListToRequestDtoList(requestRepository.findAllByRequestDepartment(currentUser.getUserDepartment()));
    }

    public List<RequestDto> findAllByPerformerAndStatus(String statusName) {
        List<Request> requests;
        Status status;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername);

        if ("NONE".equals(statusName)) {
            requests = requestRepository.findAllByRequestPerformer(currentUser);
        } else {
            status = findStatusByName(statusName);
            requests = requestRepository.findAllByRequestPerformerAndRequestStatus(currentUser, status);
        }

        return requestListToRequestDtoList(requests);
    }

    public RequestDto updateByPerformer(Long id, RequestDto requestDto) {
        Request request = findRequestById(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername);


        if (requestDto.getRemove()) {
            request.setRequestStatus(findStatusById("CANCELED"));
            request.setRemoved(LocalDateTime.now());
            request.setCancelInfo("by performer");
        } else if ("signUp".equals(requestDto.getPerformer())) {
            request.setRequestPerformer(currentUser);
        } else {
            Request requestFromPerformer = requestDtoToRequest(requestDto);
            if (requestFromPerformer.getComment() != null) {
                request.setComment(requestFromPerformer.getComment());
            }
        }

        return requestToRequestDto(requestRepository.save(request));
    }

//Moderator
    public List<RequestDto> findAllByDepartmentAndStatus(String departmentName, String statusName) {
        List<Request> requests;
        Department department;
        Status status;

        if("ALL".equals(statusName)) {
            if ("NONE".equals(departmentName)) {
                requests = requestRepository.findAll();
            } else {
                department = findDepartmentByName(departmentName);
                requests = requestRepository.findAllByRequestDepartment(department);
            }
        } else {
            if ("NONE".equals(departmentName)) {
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

        if (requestDto.getRemove()) {
            request.setRequestStatus(findStatusById("CANCELED"));
            request.setRemoved(LocalDateTime.now());
            request.setCancelInfo("by moderator");
        } else {
            Request requestFromModerator = requestDtoToRequest(requestDto);

            if (requestFromModerator.getTopic() != null) {
                request.setTopic(requestFromModerator.getTopic());
            }
            if (requestFromModerator.getDescription() != null) {
                request.setDescription(requestFromModerator.getDescription());
            }
            if (requestFromModerator.getComment() != null) {
                request.setComment(requestFromModerator.getComment());
            }
            if (requestFromModerator.getDeadline() != null) {
                request.setDeadline(requestFromModerator.getDeadline());
            }
        }

        return requestToRequestDto(requestRepository.save(request));
    }

    public void delete(Long id) {
        requestRepository.delete(findRequestById(id));
    }


//Additional
    private Department findDepartmentByName(String name) {
        List<Department> departments = departmentRepository.findAllByName(name);

        if (departments.size() == 0) {
            throw new NotFoundException();
        }

        return  departments.get(0);
    }

    private Request findRequestById(Long id) {
        return requestRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    private Status findStatusById(String id) {
        return statusRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    private Status findStatusByName(String name) {
        List<Status> statuses = statusRepository.findAllByName(name);

        if (statuses.size() == 0) {
            throw new NotFoundException();
        }

        return  statuses.get(0);
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

        List<Department> departments = departmentRepository.findAllByName(requestDto.getDepartment());
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
}
