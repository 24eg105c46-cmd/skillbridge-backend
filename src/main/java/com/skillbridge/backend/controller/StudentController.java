
package com.skillbridge.backend.controller;

import com.skillbridge.backend.model.Student;
import com.skillbridge.backend.repository.StudentRepository;
import com.skillbridge.backend.service.StudentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174"
})
public class StudentController {

    private final StudentService studentService;
    private final StudentRepository studentRepository;

    public StudentController(
            StudentService studentService,
            StudentRepository studentRepository) {
        this.studentService = studentService;
        this.studentRepository = studentRepository;
    }

    // Register a new student
    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(
            @RequestBody Student student) {
        try {
            Student saved =
                studentService.registerStudent(student);

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Registration successful!");
            result.put("id", saved.getId());
            result.put("fullName", saved.getFullName());
            result.put("email", saved.getEmail());

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                Map.of("message", e.getMessage())
            );
        }
    }

    // Student login
    @PostMapping("/login")
    public ResponseEntity<?> loginStudent(
            @RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");

            if (email == null || email.isBlank()
                    || password == null || password.isBlank()) {
                return ResponseEntity.badRequest().body(
                    Map.of(
                        "message",
                        "Please enter your email and password."
                    )
                );
            }

            Student student = studentService.loginStudent(
                email.trim(), password
            );

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Login successful!");
            result.put("id", student.getId());
            result.put("fullName", student.getFullName());
            result.put("email", student.getEmail());

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", e.getMessage()));
        }
    }

    // Get the complete student profile, including saved skills
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentProfile(
            @PathVariable Long id) {

        return studentRepository.findById(id)
            .<ResponseEntity<?>>map(student -> {
                Map<String, Object> profile = new HashMap<>();

                profile.put("id", student.getId());
                profile.put("fullName", student.getFullName());
                profile.put("email", student.getEmail());
                profile.put("department", student.getDepartment());
                profile.put("studyYear", student.getStudyYear());

                // Skills are loaded from the student_skills table
                profile.put("skills", student.getSkills());

                return ResponseEntity.ok(profile);
            })
            .orElseGet(() -> ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Student not found.")));
    }
}
