package com.pet.adoption.service;

import com.pet.adoption.entity.User;
import com.pet.adoption.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 注册
    public User register(String username, String password, String email, String phone) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setPhone(phone);
        return userRepository.save(user);
    }

    // 登录验证
    public User login(String username, String rawPassword) {
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isPresent()) {
            User user = optional.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    // 根据用户名查用户
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // 根据ID查用户
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    // 更新用户信息
    public User update(User user) {
        return userRepository.save(user);
    }

    // 获取所有用户（管理员用）
    public java.util.List<User> findAll() {
        return userRepository.findAll();
    }
}