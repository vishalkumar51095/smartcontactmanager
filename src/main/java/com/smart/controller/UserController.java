package com.smart.controller;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    // Define a constant for the default image file name.
    private static final String DEFAULT_IMAGE_FILENAME = "Contact.png";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    //method for adding common data to response
    @ModelAttribute
    public void addCommonData(Model model,Principal principal){
        String username= principal.getName();
        System.out.println("USERNAME"+username);

        //get the user using username(Email)

        User user = userRepository.getUserByUserName(username);

        System.out.println("USER"+user);

        model.addAttribute("user",user);
    }

    //dashboard home
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal){

        model.addAttribute("title","User Dashboard");
        return "normal/user_dashboard";
    }

    //open add from handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model ){//, HttpRequest request, HttpResponse response, HttpSession session, ServletContext servletContext){

        model.addAttribute("title","Add Contact");
        model.addAttribute("contact",new Contact());

        return "normal/add_contact_form";
    }

    //processing add contact form
    @PostMapping("/process-contact")
    public String processContact(
            @ModelAttribute Contact contact,
            @RequestParam("profileImage") MultipartFile file,
            Principal principal,
            HttpSession session){

        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            contact.setUser(user);


            if (file.isEmpty()) {
                System.out.println("File is empty");
                contact.setImage(DEFAULT_IMAGE_FILENAME); // Set a default image.
            } else {
                String originalFilename = file.getOriginalFilename();

                try {
                    // Define the directory where you want to save the uploaded files.
                    String uploadDirectory = "static/img";

                    // Get the absolute path of the upload directory.
                    File saveDirectory = new File(uploadDirectory);
                    if (!saveDirectory.exists()) {
                        saveDirectory.mkdirs(); // Create the directory if it doesn't exist.
                    }

                    // Construct the full path to save the uploaded file.
                    Path path = Paths.get(saveDirectory.getAbsolutePath(), originalFilename);

                    // Copy the file to the specified location.
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                    System.out.println("Image is uploaded");

                    contact.setImage(originalFilename);
                } catch (IOException e) {
                    // Handle the exception, e.g., log an error or throw an exception.
                    System.err.println("Error uploading the file: " + e.getMessage());
                    // Set a default image in case of an error.
                    contact.setImage(DEFAULT_IMAGE_FILENAME);
                }
            }
            user.getContacts().add(contact);

            this.userRepository.save(user);

            System.out.println("DATA " + contact);

            System.out.println("Added to data base");

            //message success ........
            session.setAttribute("message",new Message("Your contact is added !! Add more..","alert-success"));

        }
        catch (Exception e){
            System.out.println("ERROR"+e.getMessage());
            e.printStackTrace();

            //message error
            session.setAttribute("message",new Message("Something went wrong !! Try again..","alert-danger"));
        }


        return "normal/add_contact_form";
    }

    //show contacts handler
    //per page = 5[n]
    //current page = 0[page]
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page,  Model m,Principal principal){

        m.addAttribute("title","Show User Contacts");

        //send contact list
        String username=principal.getName();

        User user=this.userRepository.getUserByUserName(username);

        Pageable pageable=PageRequest.of(page,5);

        Page<Contact> contacts=this.contactRepository.findContactsByUser(user.getId(),pageable);


        m.addAttribute("contacts",contacts);
        m.addAttribute("currentPage",page);
        m.addAttribute("totalPages",contacts.getTotalPages());


        return "normal/show_contacts";
    }

    @RequestMapping("/{cId}/contact")
    public String showContactDetails(@PathVariable("cId") Integer cId,Model model,Principal principal){
        model.addAttribute("title", "Contact Details");
        System.out.println("Cid : "+cId);
        Optional<Contact> optional = this.contactRepository.findById(cId);
        Contact contact = optional.get();
        //Contact contact = this.contactRepository.findById(cid).get();

        String name = principal.getName();
        User user = this.userRepository.getUserByUserName(name);
        if(user.getId()== contact.getUser().getId()) {
            model.addAttribute("contact", contact);
        }

        return "normal/contact_detail";
    }

    //delete contact handler
    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cId,
                                Model model,
                                HttpSession session,
                                Principal principal){

        Optional<Contact> contactOptional=this.contactRepository.findById(cId);
        Contact contact=contactOptional.get();

        //check.... Assignment
        System.out.println("Contact"+contact.getcId());

        User user=this.userRepository.getUserByUserName(principal.getName());

        user.getContacts().remove(contact);

        this.contactRepository.deleteById(cId);

        System.out.println("DELETED");
        session.setAttribute("message",new Message("Contact deleted successfully...","success"));

        return "redirect:/user/show-contacts/0";
    }

    // Open update form handler
    @PostMapping("/update-contact/{cId}")
    public String updateForm(@PathVariable("cId") Integer id, Model model) {
        model.addAttribute("title", "Update Contact");
        Optional<Contact> optional = this.contactRepository.findById(id);
        Contact contact = optional.get();
        model.addAttribute("contact", contact);
        return "normal/update_form";
    }

    // update contact handler
    @RequestMapping(value="/process-update",method = RequestMethod.POST)
    public String updateHandler(@ModelAttribute Contact contact,
                                @RequestParam("profileImage") MultipartFile file,
                                Model model,
                                HttpSession session,
                                Principal principal){

        try {

            //old contact details
            Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();

            //image..
            if(!file.isEmpty()){

                //file work...
                //rewrite

                //delete old photo

                File deleteFile = new ClassPathResource("static/img").getFile();
                File file1= new File(deleteFile,oldContactDetail.getImage());
                file1.delete();


                //update new photo

                File saveFile = new ClassPathResource("static/img").getFile();

                Path path =Paths.get(saveFile.getAbsolutePath() + File.separator +file.getOriginalFilename());

                Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);

                contact.setImage(file.getOriginalFilename());

            }
            else {
                contact.setImage(oldContactDetail.getImage());
            }

            User user=this.userRepository.getUserByUserName(principal.getName());

            contact.setUser(user);

            this.contactRepository.save(contact);

            session.setAttribute("message",new Message("Your contact is updated...","success"));

        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("CONTACT NAME"+contact.getName());
        System.out.println("CONTACT ID"+contact.getcId());

        return "redirect:/user/"+contact.getcId()+"/contact";
    }

    //Profile handler
    @GetMapping("/profile")
    public String profile(Model model,
                          @ModelAttribute User user){
        model.addAttribute("title","Profile Page");
        model.addAttribute("user",user);
        return "normal/profile";
    }

    //open settings handler
    @GetMapping("/settings")
    public String openSettings(){

        return "normal/settings";
    }

    // change password handler
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,Principal principal,HttpSession session ){

        User currentUser=this.userRepository.getUserByUserName(principal.getName());

        if(this.passwordEncoder.matches(oldPassword,currentUser.getPassword()))
        {
            //change password
            currentUser.setPassword(this.passwordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);
            session.setAttribute("message",new Message("Your password is successfully changed..","alert-success"));
        }
        else {
            session.setAttribute("message",new Message("Please enter correct old password!!","alert-danger"));
            return "redirect:/user/settings";

        }

        return "redirect:/user/index";
    }


}
