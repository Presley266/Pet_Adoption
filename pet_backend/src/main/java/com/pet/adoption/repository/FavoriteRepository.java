package com.pet.adoption.repository;

import com.pet.adoption.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    @Transactional
    void deleteByUserIdAndPetId(Integer userId, Integer petId);

    Page<Favorite> findByUserId(Integer userId, Pageable pageable);

    boolean existsByUserIdAndPetId(Integer userId, Integer petId);
}