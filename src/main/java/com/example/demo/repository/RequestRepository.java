package com.example.demo.repository;

import com.example.demo.domain.Department;
import com.example.demo.domain.Request;
import com.example.demo.domain.Status;
import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
//Client
    List<Request> findAllByRequestAuthor(User requestAuthor);
//    @Query("SELECT r FROM Request r WHERE r.client=?1 AND r.status=?2")
    List<Request> findAllByRequestAuthorAndRequestStatus(User requestAuthor, Status requestStatus);
    List<Request> findAllByRequestAuthorAndRequestDepartment(User requestAuthor, Department requestDepartment);
    List<Request> findAllByRequestAuthorAndRequestDepartmentAndRequestStatus(User requestAuthor, Department requestDepartment, Status requestStatus);

//Performer
    List<Request> findAllByRequestPerformer(User requestPerformer);
    List<Request> findAllByRequestPerformerAndRequestStatus(User requestPerformer, Status requestStatus);
//    @Query("SELECT r FROM Request r WHERE r.department=?1 AND r.performer=NULL")
    List<Request> findAllByRequestDepartment(Department requestDepartment);

//Moderator
    List<Request> findAllByRequestStatus(Status requestStatus);
    List<Request> findAllByRequestDepartmentAndRequestStatus(Department requestDepartment, Status requestStatus);
}
