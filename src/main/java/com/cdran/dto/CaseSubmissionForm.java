package com.cdran.dto;

import com.cdran.model.enums.GrowthStage;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Form-backing object for "Report a crop problem" (Feature 1).
 * Simple field checks are annotations here; rules that need the database or the image
 * contents (symptoms-or-description, image quality) are checked in CaseService.
 */
public class CaseSubmissionForm {

    @NotNull(message = "{validation.required}")
    private Long cropId;

    @NotNull(message = "{validation.required}")
    private GrowthStage growthStage;

    private List<Long> symptomIds = new ArrayList<>();

    @Size(max = 1000, message = "{validation.description.size}")
    private String description;

    /** GPS, limited to the bounding box of Bangladesh. */
    @NotNull(message = "{validation.gps.required}")
    @DecimalMin(value = "20.5", message = "{validation.gps.range}")
    @DecimalMax(value = "26.7", message = "{validation.gps.range}")
    private Double latitude;

    @NotNull(message = "{validation.gps.required}")
    @DecimalMin(value = "88.0", message = "{validation.gps.range}")
    @DecimalMax(value = "92.7", message = "{validation.gps.range}")
    private Double longitude;

    /** 1 to 5 photos, checked in CaseService. */
    private List<MultipartFile> images = new ArrayList<>();

    public Long getCropId() {
        return cropId;
    }

    public void setCropId(Long cropId) {
        this.cropId = cropId;
    }

    public GrowthStage getGrowthStage() {
        return growthStage;
    }

    public void setGrowthStage(GrowthStage growthStage) {
        this.growthStage = growthStage;
    }

    public List<Long> getSymptomIds() {
        return symptomIds;
    }

    public void setSymptomIds(List<Long> symptomIds) {
        this.symptomIds = symptomIds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<MultipartFile> getImages() {
        return images;
    }

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }
}
