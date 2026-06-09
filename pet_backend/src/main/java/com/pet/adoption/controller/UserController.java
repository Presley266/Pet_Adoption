package com.pet.adoption.controller;

import com.pet.adoption.entity.*;
import com.pet.adoption.repository.*;
import com.pet.adoption.service.AdoptionService;
import com.pet.adoption.service.PetService;
import com.pet.adoption.service.UserService;
import com.pet.adoption.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private PetService petService;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private AdoptionService adoptionService;

    // 从token获取用户ID
    private Integer getUserIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        return null;
    }

    // 获取个人信息
    @GetMapping("/profile")
    public Map<String, Object> getProfile(@RequestHeader("Authorization") String token) {
        Integer userId = getUserIdFromToken(token);
        Map<String, Object> result = new HashMap<>();
        User user = userService.findById(userId);
        if (user != null) {
            user.setPassword(null); // 不返回密码
            result.put("success", true);
            result.put("user", user);
        } else {
            result.put("success", false);
        }
        return result;
    }

    // 更新个人信息
    @PutMapping("/profile")
    public Map<String, Object> updateProfile(@RequestHeader("Authorization") String token,
                                             @RequestBody User updateUser) {
        Integer userId = getUserIdFromToken(token);
        Map<String, Object> result = new HashMap<>();
        User user = userService.findById(userId);
        if (user != null) {
            if (updateUser.getEmail() != null) user.setEmail(updateUser.getEmail());
            if (updateUser.getPhone() != null) user.setPhone(updateUser.getPhone());
            userService.update(user);
            result.put("success", true);
            result.put("message", "更新成功");
        } else {
            result.put("success", false);
        }
        return result;
    }

    // ========== 收藏功能 ==========

    @PostMapping("/favorites")
    public Map<String, Object> addFavorite(@RequestHeader("Authorization") String token,
                                           @RequestBody Map<String, Integer> params) {
        Integer userId = getUserIdFromToken(token);
        Integer petId = params.get("petId");
        Map<String, Object> result = new HashMap<>();

        if (favoriteRepository.existsByUserIdAndPetId(userId, petId)) {
            result.put("success", false);
            result.put("message", "已经收藏过了");
            return result;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setPetId(petId);
        favorite.setCreateTime(new Date());
        favoriteRepository.save(favorite);

        result.put("success", true);
        result.put("message", "收藏成功");
        return result;
    }

    @DeleteMapping("/favorites/{petId}")
    public Map<String, Object> removeFavorite(@RequestHeader("Authorization") String token,
                                              @PathVariable Integer petId) {
        Integer userId = getUserIdFromToken(token);
        favoriteRepository.deleteByUserIdAndPetId(userId, petId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "取消收藏成功");
        return result;
    }

    @GetMapping("/favorites")
    public Map<String, Object> getFavorites(@RequestHeader("Authorization") String token,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "9") int size) {
        Integer userId = getUserIdFromToken(token);
        Page<Favorite> favorites = favoriteRepository.findByUserId(userId,
                org.springframework.data.domain.PageRequest.of(page, size,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createTime")));

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("favorites", favorites.getContent());
        result.put("total", favorites.getTotalElements());
        return result;
    }

    @GetMapping("/favorites/check/{petId}")
    public Map<String, Object> checkFavorite(@RequestHeader("Authorization") String token,
                                             @PathVariable Integer petId) {
        Integer userId = getUserIdFromToken(token);
        boolean isFavorited = favoriteRepository.existsByUserIdAndPetId(userId, petId);
        Map<String, Object> result = new HashMap<>();
        result.put("isFavorited", isFavorited);
        return result;
    }

    // ========== 领养申请功能 ==========

    @PostMapping("/applications")
    public Map<String, Object> submitApplication(@RequestHeader("Authorization") String token,
                                                 @RequestBody Map<String, Object> params) {
        Integer userId = getUserIdFromToken(token);
        Map<String, Object> result = new HashMap<>();
        try {
            AdoptionApplication application = new AdoptionApplication();
            application.setUserId(userId);
            application.setPetId((Integer) params.get("petId"));
            application.setApplicantName((String) params.get("applicantName"));
            application.setApplicantPhone((String) params.get("applicantPhone"));
            application.setApplicantAddress((String) params.get("applicantAddress"));
            application.setApplyReason((String) params.get("applyReason"));

            AdoptionApplication saved = adoptionService.submitApplication(application);
            result.put("success", true);
            result.put("applicationId", saved.getId());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/applications")
    public Map<String, Object> getApplications(@RequestHeader("Authorization") String token,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "9") int size) {
        Integer userId = getUserIdFromToken(token);
        Page<AdoptionApplication> apps = adoptionService.getUserApplications(userId, page, size);

        // 补充宠物信息
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("applications", apps.getContent());
        result.put("total", apps.getTotalElements());
        return result;
    }

    @PutMapping("/applications/{id}/cancel")
    public Map<String, Object> cancelApplication(@RequestHeader("Authorization") String token,
                                                 @PathVariable Integer id) {
        Integer userId = getUserIdFromToken(token);
        adoptionService.cancelApplication(id, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }
}