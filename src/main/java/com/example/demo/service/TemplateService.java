package com.example.demo.service;

import com.example.demo.domain.Attribute;
import com.example.demo.domain.Department;
import com.example.demo.domain.Template;
import com.example.demo.domain.User;
import com.example.demo.dto.TemplateDto;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AttributeRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.TemplateRepository;
import com.example.demo.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TemplateService {
    private final TemplateRepository templateRepository;
    private final AttributeRepository attributeRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public TemplateService(TemplateRepository templateRepository, AttributeRepository attributeRepository, UserRepository userRepository, DepartmentRepository departmentRepository) {
        this.templateRepository = templateRepository;
        this.attributeRepository = attributeRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    public void create(TemplateDto templateDto) {
        Template template = new Template();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = findUserByUsername(currentUsername);

        template.setTemplateAuthor(currentUser);
        template.setCreated(LocalDateTime.now());

        templateDtoToTemplate(templateDto, template);
    }

    public List<TemplateDto> findAllByDepartment(String departmentName) {
        List<TemplateDto> templateDtos = new ArrayList<>();
        List<Template> templates = new ArrayList<>();

        if ("All".equals(departmentName)) {
            templates = templateRepository.findAll();
        } else {
            Department department = findDepartmentByName(departmentName);
            templates = templateRepository.findAllByTemplateDepartment(department);
        }

        for (Template template :templates) {
            templateDtos.add(templateToTemplateDto(template));
        }

        return templateDtos;
    }

    public void update(Long id, TemplateDto templateDto) {
        Template template = findTemplateById(id);

        if (template.getRemoved() != null) {
            throw new ForbiddenException();
        }

        if (templateDto.getRemove()) {
            template.setRemoved(LocalDateTime.now());
            templateRepository.save(template);
        }
    }

    public void delete(Long id) {
        Template template = findTemplateById(id);
        for (Attribute attribute :template.getAttributes()) {
            attributeRepository.delete(attribute);
        }

        templateRepository.delete(template);
    }

    private TemplateDto templateToTemplateDto(@NotNull Template template) {
        TemplateDto templateDto = GeneralMethods.convert(template, new TemplateDto(), Arrays.asList("attributes", "author", "department", "requests"));

        List<Map<String, String>> attributes = new ArrayList<>();

        if (template.getTemplateAuthor() != null) {
            templateDto.setAuthor(template.getTemplateAuthor().getRealName());
        }
        if (template.getTemplateDepartment() != null) {
            templateDto.setDepartment(template.getTemplateDepartment().getName());
        }

        for (Attribute attribute :template.getAttributes()) {
            Map<String, String> attr = new HashMap<>();
            attr.put("name", attribute.getName());
            attr.put("placeholder", attribute.getPlaceholder());
            attr.put("id", String.valueOf(attribute.getId()));
            attributes.add(attr);
        }
        templateDto.setAttributes(attributes);

        return templateDto;
    }

    private Template templateDtoToTemplate(@NotNull TemplateDto templateDto, @NotNull Template template) {
        template = GeneralMethods.convert(templateDto, template, Arrays.asList("id", "created", "removed", "attributes", "author", "department"));

        List<Attribute> attributes = new ArrayList<>();

        for (Map<String, String> attribute :templateDto.getAttributes()) {
            Attribute attr = new Attribute();
            attr.setName(attribute.get("name"));
            attr.setPlaceholder(attribute.get("placeholder"));
            if (attr.getName() == null || attr.getPlaceholder() == null) {
                throw new BadRequestException();
            }
            attributes.add(attr);
        }

        String departmentName = templateDto.getDepartment();
        if (departmentName != null) {
            Department department = findDepartmentByName(departmentName);
            if (department != null) {
                template.setTemplateDepartment(department);
            }
        }

        templateRepository.save(validate(template));

        for (Attribute attribute :attributes) {
            attribute.setAttributeTemplate(template);
            attributeRepository.save(attribute);
        }

        return template;
    }

    private Template validate(@NotNull Template template) {
        if (
            template.getTopic() == null ||
            template.getText() == null ||
            template.getTemplateAuthor() == null ||
            template.getTemplateDepartment() == null
        ) {
            throw new BadRequestException();
        }
        return template;
    }

    private Template findTemplateById(Long id) {
        return templateRepository.findById(id).orElseThrow(NotFoundException::new);
    }
    private User findUserByUsername(String userName) {
        return userRepository.findByUsername(userName);
    }
    private Department findDepartmentByName(String name) {
        return departmentRepository.findByName(name);
    }
}
