package com.pet.adoption.controller;

import com.pet.adoption.entity.Pet;
import com.pet.adoption.repository.FavoriteRepository;
import com.pet.adoption.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private FavoriteRepository favoriteRepository;

    // 宠物列表（分页+筛选+排序）
    @GetMapping
    public Map<String, Object> getPets(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {

        Page<Pet> petPage = petService.getPets(category, sortBy, page, size);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("pets", petPage.getContent());
        result.put("total", petPage.getTotalElements());
        result.put("totalPages", petPage.getTotalPages());
        result.put("currentPage", page);
        return result;
    }

    // 宠物详情
    @GetMapping("/{id}")
    public Map<String, Object> getPetDetail(@PathVariable Integer id) {
        Pet pet = petService.getPetDetail(id);
        Map<String, Object> result = new HashMap<>();
        if (pet != null) {
            result.put("success", true);
            result.put("pet", pet);
        } else {
            result.put("success", false);
            result.put("message", "宠物不存在");
        }
        return result;
    }

    // 获取所有种类
    @GetMapping("/categories")
    public Map<String, Object> getCategories() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("categories", new String[]{"DOG", "CAT", "RABBIT", "OTHER"});
        return result;
    }
}