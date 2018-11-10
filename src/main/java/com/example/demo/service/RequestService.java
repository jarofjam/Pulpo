package com.example.demo.service;

import com.example.demo.domain.Request;
import com.example.demo.domain.User;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.RequestRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Autowired RequestService(RequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

//Client
    public Request create(Request request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername);

        request.setClient(String.valueOf(currentUser.getId()));
        request.setCreated(LocalDateTime.now());
        request.setStatus("NEW");

        return requestRepository.save(request);
    }

    public Request updateByClient(Long id, Request request) {

        Request requestFromDb = getFromDbById(id);

        if (requestFromDb.getRemoved() != null) {
            return requestFromDb;
        }

        if (request.getDescription() == null) {
            requestFromDb.setStatus("CANCELED");
            requestFromDb.setRemoved(LocalDateTime.now());
            requestFromDb.setCancelInfo("заказчиком");
        } else {
            requestFromDb.setDescription(request.getDescription());
        }

        return requestRepository.save(requestFromDb);
    }

    public List<Request> findAllByClientAndStatus(String status) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername);

        if("ALL".equals(status)) {
            return requestRepository.findAllByClient(String.valueOf(currentUser.getId()));
        } else {
            return requestRepository.findAllByClientAndStatus(String.valueOf(currentUser.getId()), status);
        }
    }

//Performer
    public List<Request> findAllByPerformerDepartment() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername);

        return requestRepository.findAllByPerformerDepartment(currentUser.getDepartment());
    }

    public List<Request> findAllByPerformer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername);

        return requestRepository.findAllByPerformer(String.valueOf(currentUser.getId()));
    }

    public Request updatePerformerStatusComment(Long id, Request request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername);

        Request requestFromDb = getFromDbById(id);

        requestFromDb.setPerformer(String.valueOf(currentUser.getId()));
        requestFromDb.setStatus(request.getStatus());
        requestFromDb.setComment(request.getComment());

        return requestRepository.save(requestFromDb);
    }

//Moderator
    public List<Request> getAll() {
    return requestRepository.findAll();
}

    public void delete(Long id) {
        requestRepository.delete(getFromDbById(id));
    }


//Additional
    private Request getFromDbById(Long id) {

        Request request = requestRepository.findById(id).orElseThrow(NotFoundException::new);

        return request;
    }

}
