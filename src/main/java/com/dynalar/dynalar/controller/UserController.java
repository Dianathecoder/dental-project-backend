
package com.dynalar.dynalar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dynalar.dynalar.model.user.User; 
import com.dynalar.dynalar.respository.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/all")
	public Iterable<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	@PostMapping("/login")
	public User login(@RequestBody User user) {	
		Optional<User> newUser = userRepository.findByEmail(user.getEmail());
		if (newUser.isPresent() && newUser.get().getPassword().equals(user.getPassword())) {
			User loggedInUser = newUser.get();
			loggedInUser.setPassword(null);
			return loggedInUser;
		}
		return null;
	}
	
	// Buscar un usuario específico por su ID
		@GetMapping("/{id}")
		public User getUserById(@PathVariable Long id) {
			Optional<User> user = userRepository.findById(id);
			
			if (user.isPresent()) {
				User foundUser = user.get();
				foundUser.setPassword(null); 
				return foundUser;
			}
			
			return null; 
		}
}