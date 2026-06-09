package com.pet.adoption.controller;

import com.pet.adoption.entity.AdoptionApplication;
import com.pet.adoption.repository.AdoptionApplicationRepository;
import com.pet.adoption.repository.PetRepository;
import com.pet.adoption.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/adoptions")
@CrossOrigin(origins = "*")
public class AdoptionController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AdoptionApplicationRepository adoptionRepository;

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

    // 提交领养申请
    @PostMapping("/apply")
    public Map<String, Object> apply(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        Map<String, Object> result = new HashMap<>();

        if (userId == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        Integer petId = (Integer) params.get("petId");
        String applicantName = (String) params.get("applicantName");
        String applicantPhone = (String) params.get("applicantPhone");
        String applicantAddress = (String) params.get("applicantAddress");
        String applyReason = (String) params.get("applyReason");

        // 检查宠物是否存在且可领养
        com.pet.adoption.entity.Pet pet = petRepository.findById(petId).orElse(null);
        if (pet == null || !"AVAILABLE".equals(pet.getStatus())) {
            result.put("success", false);
            result.put("message", "该宠物暂不可领养");
            return result;
        }

        AdoptionApplication application = new AdoptionApplication();
        application.setUserId(userId);
        application.setPetId(petId);
        application.setApplicantName(applicantName);
        application.setApplicantPhone(applicantPhone);
        application.setApplicantAddress(applicantAddress);
        application.setApplyReason(applyReason);
        application.setStatus("PENDING");
        application.setCreateTime(new Date());

        adoptionRepository.save(application);

        result.put("success", true);
        result.put("message", "申请提交成功，请等待审核");
        return result;
    }

    // 获取我的申请列表
    @GetMapping("/my")
    public Map<String, Object> getMyApplications(HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        Map<String, Object> result = new HashMap<>();

        if (userId == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        List<AdoptionApplication> applications = adoptionRepository.findByUserIdOrderByCreateTimeDesc(userId);
        result.put("success", true);
        result.put("applications", applications);
        return result;
    }
}