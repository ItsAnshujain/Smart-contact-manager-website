package project.smartcontactmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Component;

import project.smartcontactmanager.dao.UserRepository;
import project.smartcontactmanager.entities.User;
@Component
public class UserDetailsServiceImpl implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByUsername(username);
        if(user==null){
            throw new UsernameNotFoundException("could not found user");
        }
        CostomUserDetail costomUserDetail = new CostomUserDetail(user);
        return costomUserDetail;
    }
    
}
