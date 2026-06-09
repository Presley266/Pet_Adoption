package com.pet.adoption.repository;

import com.pet.adoption.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, Integer> {
    Page<Pet> findByStatus(String status, Pageable pageable);
    Page<Pet> findByCategoryAndStatus(String category, String status, Pageable pageable);
    Page<Pet> findByStatusAndCategoryIn(String status, java.util.List<String> categories, Pageable pageable);
}