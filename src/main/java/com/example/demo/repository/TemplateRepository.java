package com.example.demo.repository;

import com.example.demo.domain.Department;
import com.example.demo.domain.Template;
import com.example.demo.dto.TemplateDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    List<Template> findAllByTemplateDepartment(Department templateDepartment);
}
