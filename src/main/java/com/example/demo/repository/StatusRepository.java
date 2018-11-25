package com.example.demo.repository;

import com.example.demo.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Long> {
    Status findByName(String name);
}
