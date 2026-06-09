package com.dynalar.dynalar.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.dynalar.dynalar.model.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {	
	Optional<User> findByEmail(String email);
}	