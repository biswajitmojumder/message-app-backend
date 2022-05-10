package com.messaging.messagingapp.web;

import com.messaging.messagingapp.data.models.bindingModel.RegisterUserBindingModel;
import com.messaging.messagingapp.services.implementations.UserServiceImplementation;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserServiceImplementation userService;

    public AdminController(UserServiceImplementation userService) {
        this.userService = userService;
    }

    @GetMapping("/panel")
    public String adminPanel(){
        return "adminPanel";
    }

    @GetMapping("/register")
    public String registerView(Model model){
        model.addAttribute("RegisterUserBindingModel", new RegisterUserBindingModel());
        if(!model.containsAttribute("errors"))
            model.addAttribute("errors", new ArrayList<>());
        else System.out.println("ima error");
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegisterUserBindingModel newUserModel, RedirectAttributes redirectAttributes){
        List<String> errors = new ArrayList<>();
        if(!newUserModel.getUsername().trim().isEmpty() &&
                !newUserModel.getEmail().trim().isEmpty() &&
                !newUserModel.getPublicName().trim().isEmpty() &&
                !newUserModel.getPassword().trim().isEmpty() &&
                !newUserModel.getConfirmPassword().trim().isEmpty()
        ){
            if(newUserModel.getPassword().equals(newUserModel.getConfirmPassword())) {
                try {
                    userService.registerUser(newUserModel);
                } catch (InvalidParameterException e) {
                    errors.add(e.getLocalizedMessage());
                }
            }
            else errors.add("Passwords don't match");
        }
        else errors.add("Fields cannot be empty");
        redirectAttributes.addFlashAttribute("errors", errors);
        return "redirect:register";
    }
}
