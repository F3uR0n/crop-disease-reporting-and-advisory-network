package com.cdran.model.entity;

import com.cdran.exception.BusinessRuleException;
import com.cdran.model.enums.CaseStatus;
import com.cdran.model.enums.GrowthStage;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A crop disease case reported by a farmer (Feature 1) and tracked through its statuses (Feature 4).
 * Status changes only happen through transitionTo(), which checks the rule and writes the history row.
 */
@Entity
@Table(name = "disease_cases")
public class DiseaseCase extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "crop_id")
    private Crop crop;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private GrowthStage growthStage;

    @Column(length = 1000)
    private String description;

    /** GPS position where the photos were taken. */
    private double latitude;

    private double longitude;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private CaseStatus status;

    @ManyToMany
    @JoinTable(name = "case_symptoms",
            joinColumns = @JoinColumn(name = "case_id"),
            inverseJoinColumns = @JoinColumn(name = "symptom_id"))
    private Set<Symptom> symptoms = new HashSet<>();

    @OneToMany(mappedBy = "diseaseCase", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<CaseImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "diseaseCase", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<CaseStatusHistory> history = new ArrayList<>();

    protected DiseaseCase() {
    }

    /** A new case always starts as SUBMITTED, with the first history row. */
    public DiseaseCase(Farmer farmer, Crop crop, GrowthStage growthStage, String description,
                       double latitude, double longitude, Set<Symptom> symptoms) {
        this.farmer = farmer;
        this.crop = crop;
        this.growthStage = growthStage;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.symptoms.addAll(symptoms);
        this.status = CaseStatus.SUBMITTED;
        this.history.add(new CaseStatusHistory(this, null, CaseStatus.SUBMITTED, null));
    }

    /**
     * Move the case to a new status. Illegal moves (e.g. RESOLVED -> SUBMITTED) are refused.
     * Every successful move is recorded in the history.
     */
    public void transitionTo(CaseStatus next, String note) {
        if (!status.canMoveTo(next)) {
            throw new BusinessRuleException(null, "case.error.illegalTransition");
        }
        history.add(new CaseStatusHistory(this, status, next, note));
        this.status = next;
    }

    public void addImage(CaseImage image) {
        image.setDiseaseCase(this);
        images.add(image);
    }

    /** Used by the progress bar on the case page: has the case ever been in this status? */
    public boolean hasReached(CaseStatus s) {
        return history.stream().anyMatch(h -> h.getToStatus() == s);
    }

    public Farmer getFarmer() {
        return farmer;
    }

    public Crop getCrop() {
        return crop;
    }

    public GrowthStage getGrowthStage() {
        return growthStage;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public Set<Symptom> getSymptoms() {
        return symptoms;
    }

    public List<CaseImage> getImages() {
        return images;
    }

    public List<CaseStatusHistory> getHistory() {
        return history;
    }
}
