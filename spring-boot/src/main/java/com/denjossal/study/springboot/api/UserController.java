package com.denjossal.study.springboot.api;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller demonstrating proper API design:
 * - Resource-oriented endpoints
 * - Proper HTTP status codes (201 Created, 404 Not Found, etc.)
 * - Location header on creation
 * - Pagination placeholder
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final Map<String, UserDto> store = new ConcurrentHashMap<>();

    public record UserDto(String id, String name, String email, int age) {}

    public record CreateUserRequest(String name, String email, int age) {}

    public record UpdateUserRequest(String name, String email) {}

    @GetMapping
    public List<UserDto> list() {
        return List.copyOf(store.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> get(@PathVariable String id) {
        var user = store.get(id);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody CreateUserRequest request) {
        var id = UUID.randomUUID().toString();
        var user = new UserDto(id, request.name(), request.email(), request.age());
        store.put(id, user);
        return ResponseEntity.created(URI.create("/api/v1/users/" + id)).body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable String id, @RequestBody UpdateUserRequest request) {
        var existing = store.get(id);
        if (existing == null) return ResponseEntity.notFound().build();

        var updated = new UserDto(id, request.name(), request.email(), existing.age());
        store.put(id, updated);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (store.remove(id) == null) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
