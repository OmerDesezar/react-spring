package com.omer.users.controller;

import com.omer.users.model.User;
import com.omer.users.repository.UserRepository;
import com.omer.users.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final String USER_KEY = "user";

    private UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    private boolean isLoggedIn(HttpServletRequest request) {
        return request.getSession().getAttribute(USER_KEY) != null;
    }

    @PostMapping("/register")
    public ResponseEntity<Response<User>> register(@RequestBody User user, HttpServletRequest request) {
        return repository
                .findByUsername(user.getUsername())
                .map(exists ->
                        ResponseEntity.ok(new Response<>(false, "A user with that username already exists", user)))
                .orElseGet(() -> {
                    User created = repository.save(user);
                    request.getSession().setAttribute(USER_KEY, created);
                    return ResponseEntity.ok(new Response<>(true, "Registered and logged in successfully", created));
                });
    }

    @PostMapping("/login")
    public ResponseEntity<Response<User>> logIn(@RequestBody User user, HttpServletRequest request) {
        return repository
                .findByUsernameAndPassword(user.getUsername(), user.getPassword())
                .map(withID -> {
                    request.getSession().setAttribute(USER_KEY, withID);
                    return ResponseEntity.ok(new Response<>(true, "Logged in successfully", withID));
                })
                .orElseGet(() -> ResponseEntity.ok(new Response<>(false, "Failed to log in", user)));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Response<User>> findById(@PathVariable("id") Long id, HttpServletRequest request) {
        return isLoggedIn(request) ? repository
                .findById(id)
                .map(user ->
                        ResponseEntity.ok(new Response<>(true, null, user)))
                .orElse(
                        ResponseEntity.ok(new Response<>(false, "No user with the ID: " + id, null)))
                : ResponseEntity.ok(new Response<>(false, "You need to log in to do that", null));
    }

    @GetMapping("/getall")
    public ResponseEntity<Response<List<User>>> findAll(HttpServletRequest request){
        return isLoggedIn(request) ?
                ResponseEntity.ok(new Response<>(true, null, (List) repository.findAll())) :
                ResponseEntity.ok(new Response<>(false, "You need to be logged in to do that", null));

    }
}
