package project.smartcontactmanager.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import project.smartcontactmanager.dao.ContactRepository;
import project.smartcontactmanager.dao.UserRepository;
import project.smartcontactmanager.entities.Contact;
import project.smartcontactmanager.entities.User;

@RestController
public class SearchController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){
        User user= this.userRepository.getUserByUsername(principal.getName());
        List<Contact> contacts = contactRepository.findByNameContainingAndUser(query, user);
        return ResponseEntity.ok(contacts);
    }
    
}
