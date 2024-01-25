package project.smartcontactmanager.controller;

import java.security.Principal;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import project.smartcontactmanager.dao.UserRepository;
import project.smartcontactmanager.entities.User;
import project.smartcontactmanager.services.EmailService;


@Controller
public class forgotController {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    Random random=new Random(1000);

    @RequestMapping("/forgot")
    public String openEmailForm(){
        return "/forgot_email_form";
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam("email") String email, HttpSession session){
        int otp = random.nextInt(999999);
        String subject="OTP from SCM";
        String message=""
        + "<div style='border:1px solid #e2e2e2; padding:20px'>"
        + "<h1>"
        + "OTP is "
        + "<b>"+otp
        + "</b>"
        + "</h1>"
        + "</div>";


        String to=email;
        boolean result = this.emailService.sendEmail(to, subject, message);
        if(result){
            session.setAttribute("myotp", otp);
            session.setAttribute("email", email);
            return "/verify_otp";
        }else{
            session.setAttribute("message", "Check your email id..");
            return "/forgot_email_form";
        }
    }
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") int otp, HttpSession session){
        int myOtp=(int)session.getAttribute("myotp");
        String email=(String)session.getAttribute("email");
        if(myOtp==otp){
            User user = this.userRepository.getUserByUsername(email);
            if(user==null){
                session.setAttribute("message", "User doesn't exist with this email");
                return "/forgot_email_form";
            }else{
                return "/password_change_form";
            }
        }else{
            session.setAttribute("message", "You have entered wrong otp");
            return "/verify_otp";
        }
    }
    @PostMapping("/change-oldpassword")
    public String changePassword(@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session){
        String email=(String)session.getAttribute("email");
        User user = this.userRepository.getUserByUsername(email);
        user.setPassword(passwordEncoder.encode(newPassword));
        this.userRepository.save(user);
        return "redirect:/signin?change=Password changed successfully..";
    }
}
