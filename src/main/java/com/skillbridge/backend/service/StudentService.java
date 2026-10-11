
package com.skillbridge.backend.service;

import com.skillbridge.backend.model.Student;
import com.skillbridge.backend.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public StudentService(
            StudentRepository studentRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Student registerStudent(Student student) {

        if (student.getEmail() == null
                || student.getEmail().isBlank()) {
            throw new IllegalArgumentException(
                "Email is required."
            );
        }

        if (studentRepository.existsByEmail(
                student.getEmail().trim())) {
            throw new IllegalArgumentException(
                "This email is already registered."
            );
        }

        if (student.getPassword() == null
                || student.getPassword().length() < 8) {
            throw new IllegalArgumentException(
                "Password must contain at least 8 characters."
            );
        }

        if (student.getSkills() == null
                || student.getSkills().isEmpty()) {
            throw new IllegalArgumentException(
                "Please select at least one skill."
            );
        }

        student.setEmail(student.getEmail().trim());
        student.setPassword(
            passwordEncoder.encode(student.getPassword())
        );

        System.out.println(
            "Skills received during registration: "
            + student.getSkills()
        );

        Student saved = studentRepository.save(student);

        System.out.println(
            "Skills saved for student ID "
            + saved.getId() + ": " + saved.getSkills()
        );

        return saved;
    }

    public Student loginStudent(String email, String password) {

        Student student = studentRepository.findByEmail(email)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Invalid email or password."
                )
            );

        if (!passwordEncoder.matches(
                password, student.getPassword())) {
            throw new IllegalArgumentException(
                "Invalid email or password."
            );
        }

        return student;
    }
}
