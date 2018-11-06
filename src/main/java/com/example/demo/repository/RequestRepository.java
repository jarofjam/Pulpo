package com.example.demo.repository;

import com.example.demo.domain.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByClient(String client);
    List<Request> findAllByDepartment(String department);
    List<Request> findAllByPerformer(String performer);
}
