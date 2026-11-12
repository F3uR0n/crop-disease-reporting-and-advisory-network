package com.cdran.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * One photo of a case. The image file itself is stored on disk (see ImageStorageService);
 * the database only keeps its relative path plus the quality numbers measured at upload.
 */
@Entity
@Table(name = "case_images")
public class CaseImage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id")
    private DiseaseCase diseaseCase;

    /** Path relative to the upload folder, e.g. "cases/3f2a...jpg". Shown at /uploads/{filePath}. */
    @Column(nullable = false)
    private String filePath;

    private int width;

    private int height;

    private double sharpnessScore;

    protected CaseImage() {
    }

    public CaseImage(String filePath, int width, int height, double sharpnessScore) {
        this.filePath = filePath;
        this.width = width;
        this.height = height;
        this.sharpnessScore = sharpnessScore;
    }

    void setDiseaseCase(DiseaseCase diseaseCase) {
        this.diseaseCase = diseaseCase;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getSharpnessScore() {
        return sharpnessScore;
    }
}
