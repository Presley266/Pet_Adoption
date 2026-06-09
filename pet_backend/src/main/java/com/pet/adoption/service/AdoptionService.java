package com.pet.adoption.service;

import com.pet.adoption.entity.AdoptionApplication;
import com.pet.adoption.entity.Pet;
import com.pet.adoption.repository.AdoptionApplicationRepository;
import com.pet.adoption.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class AdoptionService {

    @Autowired
    private AdoptionApplicationRepository applicationRepository;

    @Autowired
    private PetRepository petRepository;

    // 提交领养申请
    public AdoptionApplication submitApplication(AdoptionApplication application) {
        // 检查是否已经申请过（待审核或已通过状态）
        List<String> activeStatuses = Arrays.asList("PENDING", "APPROVED");
        boolean exists = applicationRepository.existsByUserIdAndPetIdAndStatusIn(
                application.getUserId(), application.getPetId(), activeStatuses);
        if (exists) {
            throw new RuntimeException("你已经申请过这只宠物了，请等待审核结果");
        }
        application.setStatus("PENDING");
        application.setCreateTime(new Date());
        return applicationRepository.save(application);
    }

    // 获取用户的申请列表
    public Page<AdoptionApplication> getUserApplications(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return applicationRepository.findByUserId(userId, pageable);
    }

    // 取消申请
    public void cancelApplication(Integer id, Integer userId) {
        AdoptionApplication app = applicationRepository.findById(id).orElse(null);
        if (app != null && app.getUserId().equals(userId) && "PENDING".equals(app.getStatus())) {
            app.setStatus("CANCELLED");
            applicationRepository.save(app);
        }
    }

    // 获取所有申请（管理员）
    public Page<AdoptionApplication> getAllApplications(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        if (status != null && !status.isEmpty()) {
            return applicationRepository.findByStatus(status, pageable);
        }
        return applicationRepository.findAll(pageable);
    }

    // 审核申请（管理员）
    public AdoptionApplication reviewApplication(Integer id, String status, String comment) {
        AdoptionApplication app = applicationRepository.findById(id).orElse(null);
        if (app != null) {
            app.setStatus(status);
            app.setReviewComment(comment);
            app.setReviewTime(new Date());
            applicationRepository.save(app);

            // 如果审核通过，把宠物状态改为已领养
            if ("APPROVED".equals(status)) {
                Pet pet = petRepository.findById(app.getPetId()).orElse(null);
                if (pet != null) {
                    pet.setStatus("ADOPTED");
                    petRepository.save(pet);
                }
            }
        }
        return app;
    }

    // 获取待审核数量
    public long getPendingCount() {
        return applicationRepository.countByStatus("PENDING");
    }
}