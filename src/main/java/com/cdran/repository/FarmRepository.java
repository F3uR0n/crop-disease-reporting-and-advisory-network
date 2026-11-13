package com.cdran.repository;

import com.cdran.model.entity.Farm;
import com.cdran.model.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmRepository extends JpaRepository<Farm, Long> {

    List<Farm> findByFarmer(Farmer farmer);
}
