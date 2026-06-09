package com.pet.adoption.controller;

import com.pet.adoption.entity.User;
import com.pet.adoption.service.UserService;
import com.pet.adoption.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // 注册
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = params.get("username");
            String password = params.get("password");
            String email = params.get("email");
            String phone = params.get("phone");
            User user = userService.register(username, password, email, phone);
            result.put("success", true);
            result.put("message", "注册成功");
            result.put("userId", user.getId());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    // 登录
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        String username = params.get("username");
        String password = params.get("password");

        User user = userService.login(username, password);
        if (user != null) {
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            result.put("success", true);
            result.put("token", token);
            result.put("userId", user.getId());
            result.put("username", user.getUsername());
            result.put("role", user.getRole());
        } else {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
        }
        return result;
    }
}