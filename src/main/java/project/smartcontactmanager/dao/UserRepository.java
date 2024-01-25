package project.smartcontactmanager.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import project.smartcontactmanager.entities.User;

public interface UserRepository extends CrudRepository<User, Integer>{
    @Query("select u from User u where u.email = :email")
    public User getUserByUsername(@Param("email") String email);
    
} 
