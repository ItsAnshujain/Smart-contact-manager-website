package project.smartcontactmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.*;
import project.smartcontactmanager.dao.UserRepository;
import project.smartcontactmanager.entities.User;
import project.smartcontactmanager.helper.Message;

@Controller

public class HomeController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserRepository userRepository;

    @GetMapping("/home")
    public String home(Model m){
        m.addAttribute("title", "Home - Smart contact manager");
        return "home";
    }
    @GetMapping("/about")
    public String about(Model m){
        m.addAttribute("title", "About - Smart contact manager");
        return "about";
    }
    @GetMapping("/signup")
    public String signup(Model m){
        m.addAttribute("title", "Register - Smart contact manager");
        m.addAttribute("user", new User());
        return "signup";
    }
    @PostMapping(value = "/do_register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session){
        try {
            if(!agreement){
                System.out.println("You have not check terms and conditions");
                throw new Exception("You have not check terms and conditions");
            }
            if(result.hasErrors()){
                System.out.println("Error" + result);
                model.addAttribute("user", user);
                return "signup";
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("Default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            userRepository.save(user);
            model.addAttribute("user", new User());

            session.setAttribute("message", new Message("Successfully registered", "alert-success"));
            return "signup";
        } catch (Exception e) {
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong !! "+e.getMessage(), "alert-danger"));
            e.printStackTrace();
            return "signup";
        }
    }
    @GetMapping("/signin")
    public String CustomLogin(Model m){
        m.addAttribute("title", "Login - Smart contact manager");
        return "login";
    }
}
