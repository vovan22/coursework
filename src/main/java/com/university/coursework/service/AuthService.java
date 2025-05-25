package com.university.coursework.service;

import com.university.coursework.domain.LoginDTO;
import com.university.coursework.domain.RegisterDTO;
import com.university.coursework.domain.UserDTO;

public interface AuthService {
    UserDTO loginUser(LoginDTO request);

    UserDTO registerUser(RegisterDTO request);
    String generateToken(UserDTO user);
}
