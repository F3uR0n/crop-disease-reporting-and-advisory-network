package com.cdran.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "farms")
public class Farm extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String farmName;

    @Column(nullable = false, length = 200)
    private String location;

    @Column(nullable = false)
    private Double totalAreaInAcres;

    @ManyToOne(optional = false)
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    @ManyToMany
    @JoinTable(
            name = "farm_crops",
            joinColumns = @JoinColumn(name = "farm_id"),
            inverseJoinColumns = @JoinColumn(name = "crop_id"))
    private Set<Crop> currentCrops = new HashSet<>();

    public Farm() {
    }

    public Farm(String farmName, String location, Double totalAreaInAcres, Farmer farmer) {
        this.farmName = farmName;
        this.location = location;
        this.totalAreaInAcres = totalAreaInAcres;
        this.farmer = farmer;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getTotalAreaInAcres() {
        return totalAreaInAcres;
    }

    public void setTotalAreaInAcres(Double totalAreaInAcres) {
        this.totalAreaInAcres = totalAreaInAcres;
    }

    public Farmer getFarmer() {
        return farmer;
    }

    public void setFarmer(Farmer farmer) {
        this.farmer = farmer;
    }

    public Set<Crop> getCurrentCrops() {
        return currentCrops;
    }

    public void setCurrentCrops(Set<Crop> currentCrops) {
        this.currentCrops = currentCrops == null ? new HashSet<>() : currentCrops;
    }
}
