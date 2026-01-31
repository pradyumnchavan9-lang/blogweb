package com.prady.blogWeb.service;


import com.prady.blogWeb.dto.request.AuthRequest;
import com.prady.blogWeb.dto.request.CreateUser;
import com.prady.blogWeb.dto.response.AuthResponse;
import com.prady.blogWeb.entity.User;
import com.prady.blogWeb.jwt.JwtUtility;
import com.prady.blogWeb.mapper.UserMapper;
import com.prady.blogWeb.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserRepository userRepo;
    private final AuthenticationManager authenticationManager ;
    private final JwtUtility jwtUtility;


    public UserService(PasswordEncoder passwordEncoder, UserMapper userMapper, UserRepository userRepo,
                       AuthenticationManager authenticationManager,JwtUtility jwtUtility) {
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.userRepo = userRepo;
        this.authenticationManager = authenticationManager;
        this.jwtUtility = jwtUtility;
    }

    public void register(CreateUser createUser){

        User user  = userMapper.createUserToUser(createUser);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        userRepo.save(user);
    }

    public AuthResponse login(AuthRequest request){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String token = jwtUtility.generateToken(request.getUsername());
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        return authResponse;
    }
}
