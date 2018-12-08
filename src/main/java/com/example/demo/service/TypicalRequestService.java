package com.example.demo.service;

import com.example.demo.domain.*;
import com.example.demo.dto.TypicalRequestDto;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

//Trashy trash
//Was written  on last week and in one night
//Need to rewrite a whole class, because it's terrible, but I have no time
@Service
public class TypicalRequestService {
    private final TypicalRequestRepository typicalRequestRepository;
    private final UserRepository userRepository;
    private final TemplateRepository templateRepository;
    private final ValueRepository valueRepository;
    private final AttributeRepository attributeRepository;
    private final StatusRepository statusRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    TypicalRequestService(
            TypicalRequestRepository typicalRequestRepository,
            UserRepository userRepository,
            TemplateRepository templateRepository,
            ValueRepository valueRepository,
            AttributeRepository attributeRepository,
            StatusRepository statusRepository,
            DepartmentRepository departmentRepository
    ) {
        this.typicalRequestRepository = typicalRequestRepository;
        this.userRepository = userRepository;
        this.templateRepository = templateRepository;
        this.valueRepository = valueRepository;
        this.attributeRepository = attributeRepository;
        this.statusRepository = statusRepository;
        this.departmentRepository = departmentRepository;
    }

//Applicant
    public void create(TypicalRequestDto typicalRequestDto) {
        TypicalRequest typicalRequest = new TypicalRequest();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = findUserByUsername(currentUsername);

        typicalRequest.setTypicalRequestAuthor(currentUser);
        typicalRequest.setCreated(LocalDateTime.now());
        typicalRequest.setTypicalRequestStatus(findStatusByName("New"));

        typicalRequestDtoToTypicalRequest(typicalRequestDto, typicalRequest);
    }

    public void updateByApplicant(Long id, TypicalRequestDto typicalRequestDto) {
        TypicalRequest typicalRequest = findTypicalRequestById(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = findUserByUsername(currentUsername);

        //Check permissions
        if (typicalRequest.getRemoved() != null) {
            throw new ForbiddenException();
        }
        if (typicalRequest.getTypicalRequestAuthor() != currentUser) {
            throw new ForbiddenException();
        }
        //Update
        if (typicalRequestDto.getRemove()) {
            typicalRequest.setRemoved(LocalDateTime.now());
            typicalRequest.setTypicalRequestStatus(findStatusByName("Canceled"));
            typicalRequest.setCancelInfo("by applicant");
            typicalRequestRepository.save(typicalRequest);
            return;
        }
        typicalRequestDtoToTypicalRequest(typicalRequestDto, typicalRequest);
    }

    public List<TypicalRequestDto> findAllByAuthorAndDepartmentAndStatus(String departmentName, String statusName) {
        List<TypicalRequest> typicalRequests = new ArrayList<>();
        Status status;
        Department department;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = findUserByUsername(currentUsername);

        if("All".equals(statusName)) {
            if ("All".equals(departmentName)) {
                typicalRequests = typicalRequestRepository.findAllByTypicalRequestAuthor(currentUser);
            } else {
                department = findDepartmentByName(departmentName);
                List<TypicalRequest> tempTypicalRequests = typicalRequestRepository.findAllByTypicalRequestAuthor(currentUser);
                for (TypicalRequest typicalRequest :tempTypicalRequests) {
                    if (typicalRequest.getRequestTemplate().getTemplateDepartment() == department) {
                        typicalRequests.add(typicalRequest);
                    }
                }
            }
        } else {
            if ("All".equals(departmentName)) {
                status = findStatusByName(statusName);
                typicalRequests = typicalRequestRepository.findAllByTypicalRequestAuthorAndTypicalRequestStatus(currentUser, status);
            } else {
                status = findStatusByName(statusName);
                department = findDepartmentByName(departmentName);
                List<TypicalRequest> tempTypicalRequests = typicalRequestRepository.findAllByTypicalRequestAuthorAndTypicalRequestStatus(currentUser, status);
                for (TypicalRequest typicalRequest :tempTypicalRequests) {
                    if (typicalRequest.getRequestTemplate().getTemplateDepartment() == department) {
                        typicalRequests.add(typicalRequest);
                    }
                }
            }
        }

        return typicalRequestListToTypicalRequestDtoList(typicalRequests);
    }
//Performer
    public List<TypicalRequestDto> findAllByPerformerDepartment() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = findUserByUsername(currentUsername);

        List<TypicalRequest> typicalRequests = new ArrayList<>();
        List<TypicalRequest> tempTypicalRequests = typicalRequestRepository.findAll();

        for (TypicalRequest typicalRequest :tempTypicalRequests) {
            if (typicalRequest.getRequestTemplate().getTemplateDepartment() == currentUser.getUserDepartment()) {
                typicalRequests.add(typicalRequest);
            }
        }

        return typicalRequestListToTypicalRequestDtoList(typicalRequests);
    }

    public List<TypicalRequestDto> findAllByPerformerAndStatus(String statusName) {
        List<TypicalRequest> typicalRequests;
        Status status;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = findUserByUsername(currentUsername);

        if ("All".equals(statusName)) {
            typicalRequests = typicalRequestRepository.findAllByTypicalRequestPerformer(currentUser);
        } else {
            status = findStatusByName(statusName);
            typicalRequests = typicalRequestRepository.findAllByTypicalRequestPerformerAndTypicalRequestStatus(currentUser, status);
        }

        return typicalRequestListToTypicalRequestDtoList(typicalRequests);
    }

    public void updateByPerformer(Long id, TypicalRequestDto typicalRequestDto) {
        TypicalRequest typicalRequest = findTypicalRequestById(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = findUserByUsername(currentUsername);

        //Check permissions
        if (typicalRequest.getRemoved() != null) {
            throw new ForbiddenException();
        }
        if (typicalRequest.getTypicalRequestPerformer() != null && typicalRequest.getTypicalRequestPerformer() != currentUser) {
            throw new ForbiddenException();
        }
        if (
            typicalRequest.getRequestTemplate() != null &&
            typicalRequest.getRequestTemplate().getTemplateDepartment() != currentUser.getUserDepartment()
        ) {
            throw new ForbiddenException();
        }

        typicalRequest.setTypicalRequestPerformer(currentUser);

        //Update
        if (typicalRequestDto.getRemove()) {
            typicalRequest.setRemoved(LocalDateTime.now());
            typicalRequest.setCancelInfo("by performer");
            typicalRequest.setTypicalRequestStatus(findStatusByName("Canceled"));
        } else {
            if ("Invalid".equals(typicalRequestDto.getStatus())) {
                typicalRequest.setTypicalRequestStatus(findStatusByName("Invalid"));
            }
            if ("Finished".equals((typicalRequestDto.getStatus()))) {
                typicalRequest.setTypicalRequestStatus(findStatusByName("Finished"));
            }
            if ("Ongoing".equals(typicalRequestDto.getStatus())) {
                typicalRequest.setTypicalRequestStatus(findStatusByName("Ongoing"));
            }
            if (typicalRequestDto.getComment() != null) {
                typicalRequest.setComment(typicalRequestDto.getComment());
            }
        }

        typicalRequestRepository.save(typicalRequest);
    }

//Moderator
    public List<TypicalRequestDto> findAllByDepartmentAndStatus(String departmentName, String statusName) {
        List<TypicalRequest> typicalRequests = new ArrayList<>();
        Status status;
        Department department;

        if("All".equals(statusName)) {
            if ("All".equals(departmentName)) {
                typicalRequests = typicalRequestRepository.findAll();
            } else {
                department = findDepartmentByName(departmentName);
                List<TypicalRequest> tempTypicalRequests = typicalRequestRepository.findAll();
                for (TypicalRequest typicalRequest :tempTypicalRequests) {
                    if (typicalRequest.getRequestTemplate().getTemplateDepartment() == department) {
                        typicalRequests.add(typicalRequest);
                    }
                }
            }
        } else {
            if ("All".equals(departmentName)) {
                status = findStatusByName(statusName);
                typicalRequests = typicalRequestRepository.findAllByTypicalRequestStatus(status);
            } else {
                status = findStatusByName(statusName);
                department = findDepartmentByName(departmentName);
                List<TypicalRequest> tempTypicalRequests = typicalRequestRepository.findAllByTypicalRequestStatus(status);
                for (TypicalRequest typicalRequest :tempTypicalRequests) {
                    if (typicalRequest.getRequestTemplate().getTemplateDepartment() == department) {
                        typicalRequests.add(typicalRequest);
                    }
                }
            }
        }
        return typicalRequestListToTypicalRequestDtoList(typicalRequests);
    }

    public void updateByModerator(Long id, TypicalRequestDto typicalRequestDto) {
        TypicalRequest typicalRequest = findTypicalRequestById(id);

        //Check permissions
        if (typicalRequest.getRemoved() != null) {
            throw new ForbiddenException();
        }

        //Update
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = findUserByUsername(currentUsername);

        typicalRequest.setTypicalRequestModerator(currentUser);

        if (typicalRequestDto.getRemove()) {
            typicalRequest.setRemoved(LocalDateTime.now());
            typicalRequest.setTypicalRequestStatus(findStatusByName("Canceled"));
            typicalRequest.setCancelInfo("by moderator");
        }
        //Update status
        if ("Checked".equals(typicalRequestDto.getStatus())) {
            typicalRequest.setTypicalRequestStatus(findStatusByName("Checked"));
        }
        if ("Invalid".equals(typicalRequestDto.getStatus())) {
            typicalRequest.setTypicalRequestStatus(findStatusByName("Invalid"));
        }
        if (typicalRequestDto.getComment() != null) {
            typicalRequest.setComment(typicalRequestDto.getComment());
        }
        typicalRequestRepository.save(typicalRequest);
    }

    public void delete(Long id) {
        TypicalRequest typicalRequest = findTypicalRequestById(id);

        for(Value value :typicalRequest.getValues()) {
            valueRepository.delete(value);
        }

        typicalRequestRepository.delete(typicalRequest);
    }

//Additional
    private User findUserByUsername(String userName) {
        return userRepository.findByUsername(userName);
    }

    private Template findTemplateById(Long id) {
        return templateRepository.findById(id).orElseThrow(BadRequestException::new);
    }

    private Attribute findAttributeById(Long id) {
        return attributeRepository.findById(id).orElseThrow(BadRequestException::new);
    }

    private TypicalRequest findTypicalRequestById(Long id) {
        return typicalRequestRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    private Status findStatusByName(String name) {
        return statusRepository.findByName(name);
    }

    private Department findDepartmentByName(String name) {
        return departmentRepository.findByName(name);
    }

    private TypicalRequest validate(@NotNull TypicalRequest typicalRequest) {
        if (
                typicalRequest.getTypicalRequestAuthor() == null ||
                typicalRequest.getRequestTemplate() == null
        ) {
            throw new BadRequestException();
        }

        return typicalRequest;
    }

    private TypicalRequest typicalRequestDtoToTypicalRequest(@NotNull TypicalRequestDto typicalRequestDto, TypicalRequest typicalRequest) {
        typicalRequest.setRequestTemplate(findTemplateById(typicalRequestDto.getTemplate()));

        List<Value> oldValues = typicalRequest.getValues();
        List<Value> newValues = new ArrayList<>();

        if (typicalRequestDto.getValues() != null) {
            if (
                    typicalRequestDto.getAttributes() == null ||
                    typicalRequestDto.getAttributes().size() != typicalRequestDto.getValues().size()
            ) {
                throw new BadRequestException();
            }
        }

        typicalRequestRepository.save(validate(typicalRequest));

        for (int i = 0; i < typicalRequestDto.getValues().size(); i++) {
            Value value = new Value();
            Attribute attribute = findAttributeById(Long.parseLong(typicalRequestDto.getAttributes().get(i).get("id")));

            value.setValue(typicalRequestDto.getValues().get(i).get("value"));
            value.setValueTypicalRequest(typicalRequest);
            value.setValueAttribute(attribute);

            newValues.add(value);
        }

        if (newValues.size() != 0) {
            if (oldValues != null) {
                for (Value value : oldValues) {
                    valueRepository.delete(value);
                }
            }
            for (Value value :newValues) {
                valueRepository.save(value);
            }
        }

        return typicalRequest;
    }

    private TypicalRequestDto typicalRequestToTypicalRequestDto(@NotNull TypicalRequest typicalRequest) {
        TypicalRequestDto typicalRequestDto = GeneralMethods.convert(typicalRequest, new TypicalRequestDto(), Arrays.asList("requestTemplate", "typicalRequestAuthor", "typicalRequestPerformer", "typicalRequestModerator", "typicalRequestStatus", "values"));

        if (typicalRequest.getRequestTemplate() != null) {
            typicalRequestDto.setTopic(typicalRequest.getRequestTemplate().getTopic());
            typicalRequestDto.setText(typicalRequest.getRequestTemplate().getText());
            typicalRequestDto.setDeadline(typicalRequest.getRequestTemplate().getDeadline());
            typicalRequestDto.setTemplate(typicalRequest.getRequestTemplate().getId());
        }

        if (typicalRequest.getTypicalRequestAuthor() != null) {
            typicalRequestDto.setAuthor(typicalRequest.getTypicalRequestAuthor().getRealName());
        }
        if (typicalRequest.getTypicalRequestPerformer() != null) {
            typicalRequestDto.setPerformer(typicalRequest.getTypicalRequestPerformer().getRealName());
        }
        if (typicalRequest.getTypicalRequestModerator() != null) {
            typicalRequestDto.setModerator(typicalRequest.getTypicalRequestModerator().getRealName());
        }

        if (typicalRequest.getTypicalRequestStatus() != null) {
            typicalRequestDto.setStatus(typicalRequest.getTypicalRequestStatus().getName());
        }

        List<Map<String, String>> values = new ArrayList<>();
        List<Map<String, String>> attributes = new ArrayList<>();

        for (Value value :typicalRequest.getValues()) {
            Attribute attribute = value.getValueAttribute();

            if (attribute == null) continue;

            values.add(
                new HashMap<String, String>() {{
                    put("id", String.valueOf(value.getId()));
                    put("value", value.getValue());
                }}
            );
            attributes.add(
                new HashMap<String, String>() {{
                    put("id", String.valueOf(attribute.getId()));
                    put("name", attribute.getName());
                    put("placeholder", attribute.getPlaceholder());
                }}
            );
        }

        typicalRequestDto.setValues(values);
        typicalRequestDto.setAttributes(attributes);

        return typicalRequestDto;
    }

    private List<TypicalRequestDto> typicalRequestListToTypicalRequestDtoList(@NotNull List<TypicalRequest> typicalRequests) {
        List<TypicalRequestDto> typicalRequestDtos = new ArrayList<>();

        for (TypicalRequest typicalRequest :typicalRequests) {
            typicalRequestDtos.add(typicalRequestToTypicalRequestDto(typicalRequest));
        }

        return typicalRequestDtos;
    }
}
