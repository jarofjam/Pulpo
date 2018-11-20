package com.example.demo.repository;

import com.example.demo.domain.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
//Client
//    List<Request> findAllByClient(String client);
//
//    @Query("SELECT r FROM Request r WHERE r.client=?1 AND r.status=?2")
//    List<Request> findAllByClientAndStatus(String client, String status);
//
////Performer
//    List<Request> findAllByPerformer(String performer);
//
//    @Query("SELECT r FROM Request r WHERE r.department=?1 AND r.performer=NULL")
//    List<Request> findAllByPerformerDepartment(String department);
}
