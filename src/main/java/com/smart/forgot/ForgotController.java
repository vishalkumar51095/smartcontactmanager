package com.smart.forgot;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class ForgotController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    Random random =new Random(1000);

    //email id from open handler
    @RequestMapping("/forgot")
    public String openEmailFrom(){
        return "forgot_email_form";
    }


    //email id from open handler
    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session){

        //generate 4 digit random otp

        int otp=random.nextInt(999999);

        //write code for send otp to email...

        String subject="OTP from SCM";
        String message=""
                + "<div style='border:1px solid #e2e2e2; padding:20px'>"
                + "<h1>"
                + "OTP is "
                + "<b>" + otp
                + "</n>"
                + "</h1>"
                + "</div>";

        String to=email;

       boolean flag= this.emailService.sendEmail(subject,message,to);

       if(flag){
           session.setAttribute("myotp",otp);
           session.setAttribute("email",email);
           return "verify_otp";
       }
        else {
            session.setAttribute("message","Check your email id !!");
           return "forgot_email_form";
       }
    }

    //verify otp
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") int otp,HttpSession session){

        int myotp=(int)session.getAttribute("myotp");
        String email=(String) session.getAttribute("email");
        if(otp == myotp){

            //change password
            User user=this.userRepository.getUserByUserName(email);

            if(user==null){
                session.setAttribute("message","User does not exist with this email!!");
                return "forgot_email_form";
            }
            else {

            }

            return "password_change_form";
        }
        else {
            session.setAttribute("message","You have entered wrong otp !!");
            return "verify_otp";
        }
    }

    //change password
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newPassword") String newPassword,HttpSession session){
        String email=(String) session.getAttribute("email");
        User user=this.userRepository.getUserByUserName(email);
        user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
        this.userRepository.save(user);

        return "redirect:/signin?change=password changed successfully";
    }
}
