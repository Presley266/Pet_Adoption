package com.pet.adoption.repository;

import com.pet.adoption.entity.AdoptionApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdoptionApplicationRepository extends JpaRepository<AdoptionApplication, Integer> {

    // 分页查询用户的申请
    Page<AdoptionApplication> findByUserId(Integer userId, Pageable pageable);

    // 查询用户的申请（不分页，按时间倒序）
    List<AdoptionApplication> findByUserIdOrderByCreateTimeDesc(Integer userId);

    // 检查用户是否已经申请过某只宠物（特定状态）
    boolean existsByUserIdAndPetIdAndStatusIn(Integer userId, Integer petId, List<String> statuses);

    // 按状态分页查询
    Page<AdoptionApplication> findByStatus(String status, Pageable pageable);

    // 统计某个状态的数量
    long countByStatus(String status);
}