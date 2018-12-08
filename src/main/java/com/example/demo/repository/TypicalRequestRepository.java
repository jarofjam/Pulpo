package com.example.demo.repository;

import com.example.demo.domain.Department;
import com.example.demo.domain.Status;
import com.example.demo.domain.TypicalRequest;
import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TypicalRequestRepository extends JpaRepository<TypicalRequest, Long> {
//Applicant
    List<TypicalRequest> findAllByTypicalRequestAuthor(User typicalRequestAuthor);
    List<TypicalRequest> findAllByTypicalRequestAuthorAndTypicalRequestStatus(User typicalRequestAuthor, Status typicalRequestStatus);

//Performer
    List<TypicalRequest> findAllByTypicalRequestPerformer(User typicalRequestPerformer);
    List<TypicalRequest> findAllByTypicalRequestPerformerAndTypicalRequestStatus(User typicalRequestPerformer, Status typicalRequestStatus);

//Moderator
    List<TypicalRequest> findAllByTypicalRequestStatus(Status typicalRequestStatus);
}
