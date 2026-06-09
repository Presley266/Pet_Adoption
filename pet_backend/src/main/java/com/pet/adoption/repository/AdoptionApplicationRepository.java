package com.pet.adoption.repository;

import com.pet.adoption.entity.AdoptionApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdoptionApplicationRepository extends JpaRepository<AdoptionApplication, Integer> {
    Page<AdoptionApplication> findByUserId(Integer userId, Pageable pageable);
    Page<AdoptionApplication> findByStatus(String status, Pageable pageable);
    boolean existsByUserIdAndPetIdAndStatusIn(Integer userId, Integer petId, java.util.List<String> statuses);
    long countByStatus(String status);
}