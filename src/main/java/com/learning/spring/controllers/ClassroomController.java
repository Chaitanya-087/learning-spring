package com.learning.spring.controllers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.learning.spring.models.ClassroomService;
import com.learning.spring.models.Student;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/classroom")
public class ClassroomController {

    @Autowired
    private ClassroomService classroom;

    @GetMapping
    public String classroom(Model model) throws SQLException {
        model.addAttribute("students", classroom.getStudents());
        return "classroom";
    }

    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> add(@Valid @ModelAttribute Student student, BindingResult result,
            RedirectAttributes attr) {
        Map<String, Object> response = new HashMap<>();
        // Add your validation logic here if needed
        if (student == null || student.getName() == null) {
            response.put("success", false);
            response.put("message", "Invalid student data.");
            return response;
        }

        try {
            classroom.add(student);
            System.out.println(student);
            response.put("success", true);
            response.put("student", student);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error adding student.");
        }

        return response;
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable int id, @Valid @ModelAttribute Student student, BindingResult result,
            RedirectAttributes attr) {
        if (result.hasErrors()) {
            attr.addFlashAttribute("org.springframework.validation.BindingResult.student", result);
            attr.addFlashAttribute("student", student);
            return "redirect:/classroom";
        }
        classroom.replace(id, student);
        return "redirect:/classroom";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        classroom.remove(id);
        return "redirect:/classroom";
    }
}
