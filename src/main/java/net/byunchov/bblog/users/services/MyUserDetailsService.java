package net.byunchov.bblog.users.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import net.byunchov.bblog.users.models.*;
import net.byunchov.bblog.users.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Component("userDetailsService")
// @Service
public class MyUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<UserDao> userOptional = userRepo.findByUsername(username);
		if (userOptional.isEmpty()) {
			throw new UsernameNotFoundException(String.format("User with {username} was not found", username));
		}
		UserDao user = userOptional.get();
		List<GrantedAuthority> grantedAuthorities = user
				.getAuthorities()
				.stream()
				.map(authority -> new SimpleGrantedAuthority(authority.getName()))
				.collect(Collectors.toList());
		return new User(user.getUsername(), user.getPassword(), grantedAuthorities);
	}
}