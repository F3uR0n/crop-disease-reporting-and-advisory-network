package com.cdran.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdran.model.entity.Farm;
import com.cdran.model.entity.Farmer;
import com.cdran.repository.FarmRepository;

@Service
@Transactional
public class FarmService {

    private final FarmRepository farmRepository;

    public FarmService(FarmRepository farmRepository) {
        this.farmRepository = farmRepository;
    }

    @Transactional(readOnly = true)
    public List<Farm> getFarmsByFarmer(Farmer farmer) {
        return farmRepository.findByFarmer(farmer);
    }

    public Farm saveFarm(Farm farm, Farmer farmer) {
        farm.setFarmer(farmer);
        return farmRepository.save(farm);
    }

    public void deleteFarm(Long farmId) {
        farmRepository.deleteById(farmId);
    }
}