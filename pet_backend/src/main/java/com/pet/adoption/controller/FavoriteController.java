package com.pet.adoption.controller;

import com.pet.adoption.entity.Favorite;
import com.pet.adoption.repository.FavoriteRepository;
import com.pet.adoption.repository.PetRepository;
import com.pet.adoption.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private PetRepository petRepository;

    private Integer getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        return null;
    }

    @PostMapping("/{petId}")
    @Transactional
    public Map<String, Object> addFavorite(@PathVariable Integer petId, HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        Map<String, Object> result = new HashMap<>();

        if (userId == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        if (favoriteRepository.existsByUserIdAndPetId(userId, petId)) {
            result.put("success", false);
            result.put("message", "已经收藏过了");
            return result;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setPetId(petId);
        favoriteRepository.save(favorite);

        petRepository.findById(petId).ifPresent(pet -> {
            pet.setFavoriteCount(pet.getFavoriteCount() + 1);
            petRepository.save(pet);
        });

        result.put("success", true);
        result.put("message", "收藏成功");
        return result;
    }

    @DeleteMapping("/{petId}")
    @Transactional
    public Map<String, Object> removeFavorite(@PathVariable Integer petId, HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        Map<String, Object> result = new HashMap<>();

        if (userId == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        favoriteRepository.deleteByUserIdAndPetId(userId, petId);

        petRepository.findById(petId).ifPresent(pet -> {
            int newCount = Math.max(0, pet.getFavoriteCount() - 1);
            pet.setFavoriteCount(newCount);
            petRepository.save(pet);
        });

        result.put("success", true);
        result.put("message", "取消收藏成功");
        return result;
    }

    @GetMapping("/check/{petId}")
    public Map<String, Object> checkFavorite(@PathVariable Integer petId, HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        Map<String, Object> result = new HashMap<>();
        if (userId != null) {
            boolean isFavorited = favoriteRepository.existsByUserIdAndPetId(userId, petId);
            result.put("isFavorited", isFavorited);
        } else {
            result.put("isFavorited", false);
        }
        return result;
    }

    @GetMapping("/my")
    public Map<String, Object> getMyFavorites(HttpServletRequest request,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "9") int size) {
        Integer userId = getCurrentUserId(request);
        Map<String, Object> result = new HashMap<>();

        if (userId == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(page, size,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createTime"));
        org.springframework.data.domain.Page<Favorite> favorites = favoriteRepository.findByUserId(userId, pageable);

        result.put("success", true);
        result.put("favorites", favorites.getContent());
        result.put("total", favorites.getTotalElements());
        return result;
    }
}