package project.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.*;
import java.security.Principal;
import java.util.*;

import com.razorpay.*;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import project.smartcontactmanager.dao.*;
import project.smartcontactmanager.entities.*;
import project.smartcontactmanager.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private MyOrdersRepository myOrdersRepository;
    
    //it will automatically in all
    @ModelAttribute
    public void addCommonAttribute(Model model, Principal principal){
        String userName=principal.getName();
        User user = userRepository.getUserByUsername(userName);
        model.addAttribute("user", user);
    }

    @RequestMapping("/index")
    public String userDashBoard(Model model){
        model.addAttribute("title", "User Dashboard - Smart contact manager");
        return "normal/user_dashboard";
    }
    @RequestMapping("/add-contact")
    public String addContactForm(Model model){
        model.addAttribute("title", "Add contact - Smart contact manager");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }
    @PostMapping("/process-contact")
    public String processContact(@Valid @ModelAttribute Contact contact, BindingResult bindingResult, @RequestParam("image") MultipartFile file, Principal principal, HttpSession session){
        try{
            String name=principal.getName();
            User user=userRepository.getUserByUsername(name);

            if(file.isEmpty()){
                contact.setImage("default.png");
            }
            else{
                contact.setImage(file.getOriginalFilename());
                File saveFile= new ClassPathResource("static/img").getFile();
                Path path= Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            contact.setUser(user);
            user.getContacts().add(contact);

            this.userRepository.save(user);

            session.setAttribute("message", new Message("Your contact added successfully !! add more..", "success"));
        
        }catch(Exception e){
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong !! try again letter..", "danger"));
        }
        return "normal/add_contact_form";
    }

    // pagination : - one page- 5 contacts
    @GetMapping("/show-contacts/{page}")
    public String showContact(@PathVariable("page") Integer page ,Model model, Principal principal){
        model.addAttribute("title", "Your Contacts - Smart contact manager");
        
        String userName = principal.getName();
        User user=this.userRepository.getUserByUsername(userName);
        
        Pageable pageable = PageRequest.of(page, 5);
        Page<Contact> contacts = this.contactRepository.findContactByuser(user.getId(), pageable);
        
        model.addAttribute("contact", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        return "normal/show_contacts";
    }
    @RequestMapping("/contact/{cid}")
    public String contactDetail(@PathVariable("cid") Integer cid, Model m, Principal principal){
        Optional<Contact> contactOptional = this.contactRepository.findById(cid);
        Contact contact = contactOptional.get();

        String userName = principal.getName();
        User user=this.userRepository.getUserByUsername(userName);

        if(user.getId()==contact.getUser().getId()){
            m.addAttribute("contact", contact);
            m.addAttribute("title", contact.getName());
        }

        return "normal/contact_detail";
    }
    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cid, Model model, Principal principal, HttpSession session){
        Contact contact = this.contactRepository.findById(cid).get();
        contact.setUser(null);

        this.contactRepository.delete(contact);
        session.setAttribute("message", new Message("Contact deleted successfully", "success"));
        
        return "redirect:/user/show-contacts/0";
    }
    @PostMapping("/update-contact/{cid}")
    public String updateContact(@PathVariable("cid") Integer cid, Model model){
        model.addAttribute("title", "Update Contact - Smart contact manager");
        Contact contact = this.contactRepository.findById(cid).get();
        model.addAttribute("contact", contact);
        return "normal/update_form";
    }
    @PostMapping("/process-update")
    public String updateHandler(@ModelAttribute Contact contact, BindingResult bindingResult, @RequestParam("image") MultipartFile file, Principal principal, HttpSession session){
        try {
            Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();
            if(!file.isEmpty()){
                // delete img
                File deleteFile= new ClassPathResource("static/img").getFile();
                File file1=new File(deleteFile, oldContactDetail.getImage());
                file1.delete();
                
                // update new image
                File saveFile= new ClassPathResource("static/img").getFile();
                Path path= Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());
            }
            else{
                contact.setImage(oldContactDetail.getImage());
            }

            String userName = principal.getName();
            User user=this.userRepository.getUserByUsername(userName);
            contact.setUser(user);
            this.contactRepository.save(contact);
             session.setAttribute("message", new Message("Contact Updated successfully", "success"));
        

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/user/contact/"+contact.getcId();
    }

    @GetMapping("/profile")
    public String yourProfile(Model m){
        m.addAttribute("title", "Your Profile - Smart contact manager");
        return "normal/profile";
    }

    @GetMapping("/settings")
    public String settings(Model m){
        m.addAttribute("title", "Settings - Smart contact manager");
        return "normal/settings";
    }
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session){
        String userName = principal.getName();
        User currentUser = this.userRepository.getUserByUsername(userName);

        if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())){
            currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword)); 
            this.userRepository.save(currentUser);
            session.setAttribute("message", new Message("Password changed successfully", "success"));
        }
        else{
            session.setAttribute("message", new Message("Please enter correct old password", "danger"));
            return "redirect:/user/settings";
        }
        return "redirect:/user/index";
    }
    
    // create order using razorpaty
    @PostMapping("/create_order")
    @ResponseBody
    public String createOrder(@RequestBody Map<String, Object> data, Principal principal) throws RazorpayException{
        int amt=Integer.parseInt(data.get("amount").toString());
        var client=new RazorpayClient("rzp_test_sd5Mg54qetJVZR", "PtTZjfIxRTlZcQyUEh4fp3Ki");
        
        JSONObject options = new JSONObject();
        options.put("amount", amt*100);
        options.put("currency", "INR");
        options.put("receipt", "txn_123456");
        Order order = client.orders.create(options);

        // save order into database
        MyOrders myOrders=new MyOrders();
        myOrders.setAmount(order.get("amount"));
        myOrders.setOrderId(order.get("id"));
        myOrders.setPaymentId(null);
        myOrders.setStatus("created");
        myOrders.setUser(this.userRepository.getUserByUsername(principal.getName()));
        myOrders.setReceipt(order.get("receipt"));
        this.myOrdersRepository.save(myOrders);
        
        return order.toString();
    }

    @PostMapping("/update_order")
    @ResponseBody
    public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data){

        MyOrders myOrders=this.myOrdersRepository.findByOrderId(data.get("order_id").toString());
        myOrders.setPaymentId(data.get("payment_id").toString());
        myOrders.setStatus(data.get("status").toString());
        this.myOrdersRepository.save(myOrders);

        return ResponseEntity.ok(Map.of("msg", "updated"));
    }
}
