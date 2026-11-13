package com.cdran.model.entity;

import com.cdran.model.enums.CaseStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * One row per status change of a case (the case "timeline").
 * Created only by DiseaseCase, never directly by services. The change time is createdAt.
 */
@Entity
@Table(name = "case_status_history")
public class CaseStatusHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id")
    private DiseaseCase diseaseCase;

    /** Null for the very first row (the case was just created). */
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 20)
    private CaseStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private CaseStatus toStatus;

    @Column(length = 255)
    private String note;

    protected CaseStatusHistory() {
    }

    CaseStatusHistory(DiseaseCase diseaseCase, CaseStatus fromStatus, CaseStatus toStatus, String note) {
        this.diseaseCase = diseaseCase;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.note = note;
    }

    public CaseStatus getFromStatus() {
        return fromStatus;
    }

    public CaseStatus getToStatus() {
        return toStatus;
    }

    public String getNote() {
        return note;
    }
}
