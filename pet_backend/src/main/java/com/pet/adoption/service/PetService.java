package com.pet.adoption.service;

import com.pet.adoption.entity.Pet;
import com.pet.adoption.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    // 分页查询宠物（按状态筛选）
    public Page<Pet> getPets(String category, String sortBy, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        if ("price".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "price");
        } else if ("FavoriteCount".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "FavoriteCount");
        } else if ("age".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "age");
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        if (category != null && !category.isEmpty() && !"all".equals(category)) {
            return petRepository.findByCategoryAndStatus(category, "AVAILABLE", pageable);
        }
        return petRepository.findByStatus("AVAILABLE", pageable);
    }

    // 获取宠物详情
    public Pet getPetDetail(Integer id) {
        Optional<Pet> optional = petRepository.findById(id);
        return optional.orElse(null);
    }

    // 添加宠物
    public Pet addPet(Pet pet) {
        return petRepository.save(pet);
    }

    // 更新宠物
    public Pet updatePet(Pet pet) {
        return petRepository.save(pet);
    }

    // 删除/下架宠物
    public void deletePet(Integer id) {
        petRepository.deleteById(id);
    }

    // 获取所有宠物（管理员用）
    public List<Pet> findAll() {
        return petRepository.findAll();
    }

    // 根据ID获取
    public Pet findById(Integer id) {
        return petRepository.findById(id).orElse(null);
    }
}