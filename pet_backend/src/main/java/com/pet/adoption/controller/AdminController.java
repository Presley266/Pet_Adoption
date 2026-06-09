package com.pet.adoption.controller;

import com.pet.adoption.entity.*;
import com.pet.adoption.repository.*;
import com.pet.adoption.service.AdoptionService;
import com.pet.adoption.service.PetService;
import com.pet.adoption.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private PetService petService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdoptionService adoptionService;

    // ========== 仪表盘统计 ==========
    @GetMapping("/dashboard/statistics")
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        result.put("userCount", userService.findAll().size());
        result.put("petCount", petService.findAll().size());
        result.put("pendingApplications", adoptionService.getPendingCount());
        result.put("adoptedCount", petService.findAll().stream()
                .filter(p -> "ADOPTED".equals(p.getStatus())).count());
        return result;
    }

    // ========== 宠物管理 ==========
    @GetMapping("/pets")
    public Map<String, Object> getAllPets() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("pets", petService.findAll());
        return result;
    }

    @PostMapping("/pets")
    public Map<String, Object> addPet(@RequestBody Pet pet) {
        pet.setCreateTime(new Date());
        pet.setStatus("AVAILABLE");
        Pet saved = petService.addPet(pet);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("pet", saved);
        return result;
    }

    @PutMapping("/pets/{id}")
    public Map<String, Object> updatePet(@PathVariable Integer id, @RequestBody Pet pet) {
        pet.setId(id);
        Pet saved = petService.updatePet(pet);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("pet", saved);
        return result;
    }

    @DeleteMapping("/pets/{id}")
    public Map<String, Object> deletePet(@PathVariable Integer id) {
        petService.deletePet(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    // ========== 用户管理 ==========
    @GetMapping("/users")
    public Map<String, Object> getAllUsers() {
        List<User> users = userService.findAll();
        users.forEach(u -> u.setPassword(null));
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("users", users);
        return result;
    }

    @PutMapping("/users/{id}/status")
    public Map<String, Object> updateUserStatus(@PathVariable Integer id,
                                                @RequestBody Map<String, String> params) {
        User user = userService.findById(id);
        if (user != null) {
            user.setStatus(params.get("status"));
            userService.update(user);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    @PutMapping("/users/{id}/role")
    public Map<String, Object> updateUserRole(@PathVariable Integer id,
                                              @RequestBody Map<String, String> params) {
        User user = userService.findById(id);
        if (user != null) {
            user.setRole(params.get("role"));
            userService.update(user);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    // ========== 申请管理 ==========
    @GetMapping("/applications")
    public Map<String, Object> getAllApplications(@RequestParam(required = false) String status,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        org.springframework.data.domain.Page<com.pet.adoption.entity.AdoptionApplication> apps = adoptionService.getAllApplications(status, page, size);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("applications", apps.getContent());
        result.put("total", apps.getTotalElements());
        return result;
    }

    @PutMapping("/applications/{id}/review")
    public Map<String, Object> reviewApplication(@PathVariable Integer id,
                                                 @RequestBody Map<String, String> params) {
        String status = params.get("status");
        String comment = params.get("reviewComment");
        adoptionService.reviewApplication(id, status, comment);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }
}