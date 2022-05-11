package com.messaging.messagingapp.web;

import com.messaging.messagingapp.data.models.bindingModel.RegisterUserBindingModel;
import com.messaging.messagingapp.data.models.viewModel.AdminSearchUserViewModel;
import com.messaging.messagingapp.exceptions.UserNotFoundException;
import com.messaging.messagingapp.services.implementations.AdminServiceImplementation;
import com.messaging.messagingapp.services.implementations.UserServiceImplementation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserServiceImplementation userService;
    private final AdminServiceImplementation adminServiceImplementation;

    public AdminController(UserServiceImplementation userService, AdminServiceImplementation adminServiceImplementation) {
        this.userService = userService;
        this.adminServiceImplementation = adminServiceImplementation;
    }

    @GetMapping("/panel")
    public String adminPanelView(){
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

    @GetMapping("/user-management")
    public String userManagementView(Model model){
        if(!model.containsAttribute("errors")){
            model.addAttribute("errors", new ArrayList<>());
        }
        return "userManagement";
    }

    @GetMapping("/list")
    public String usersList(Model model){
        if(!model.containsAttribute("users")){
            model.addAttribute("users", new ArrayList<>());
        }
        return "userList";
    }

    @GetMapping("/expand")
    public String expandUser(@RequestParam Long id, RedirectAttributes redirectAttributes){
        AdminSearchUserViewModel user;
        try {
            user = adminServiceImplementation.searchUserById(id);
        } catch (UserNotFoundException e) {
            return "redirect:search";
        }
        redirectAttributes.addFlashAttribute("user", user);
        return "redirect:user-details";
    }

    @GetMapping("/user-details")
    public String foundUserView(Model model){
        if (!model.containsAttribute("user")){
            model.addAttribute("user", new AdminSearchUserViewModel());
        }
        return "userDetails";
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

    @PostMapping("/search")
    public String search(@RequestParam String searchValue, @RequestParam String searchType, RedirectAttributes redirectAttributes){
        List<String> errors = new ArrayList<>();
        if (searchType.equals("id")){
            long id;
            try{
                id = Long.parseLong(searchValue);
                AdminSearchUserViewModel foundUser = adminServiceImplementation.searchUserById(id);
                redirectAttributes.addFlashAttribute("user", foundUser);
                return "redirect:user-details";
            }catch (NumberFormatException e){
                errors.add("Invalid search value");
            }catch (UserNotFoundException e) {
                errors.add(e.getLocalizedMessage());
            }
        }
        else {
            if(searchValue.trim().isEmpty()){
                errors.add("Invalid search value");
            }
            else {
                List<AdminSearchUserViewModel> foundUsers = adminServiceImplementation
                        .searchUsersByUsername(searchValue, 0);
                //TODO: make pageable work somehow
                redirectAttributes.addFlashAttribute("users", foundUsers);
                return "redirect:list";
            }
        }
        System.out.println(errors.size());
        redirectAttributes.addFlashAttribute("errors", errors);
        return "redirect:user-management";
    }
}
