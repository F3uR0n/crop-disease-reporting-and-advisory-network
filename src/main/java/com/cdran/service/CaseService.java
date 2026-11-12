package com.cdran.service;

import com.cdran.dto.CaseSubmissionForm;
import com.cdran.dto.ImageCheckResult;
import com.cdran.exception.BusinessRuleException;
import com.cdran.exception.ResourceNotFoundException;
import com.cdran.model.entity.CaseImage;
import com.cdran.model.entity.Crop;
import com.cdran.model.entity.DiseaseCase;
import com.cdran.model.entity.Farmer;
import com.cdran.model.entity.Symptom;
import com.cdran.model.enums.CaseStatus;
import com.cdran.model.enums.NotificationType;
import com.cdran.repository.CropRepository;
import com.cdran.repository.DiseaseCaseRepository;
import com.cdran.repository.SymptomRepository;
import com.cdran.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Business rules for submitting (Feature 1) and tracking (Feature 4) disease cases.
 * Farmers are identified by their login phone number (the Spring Security username).
 */
@Service
public class CaseService {

    static final int MAX_IMAGES = 5;

    private final DiseaseCaseRepository caseRepository;
    private final CropRepository cropRepository;
    private final SymptomRepository symptomRepository;
    private final UserRepository userRepository;
    private final ImageValidationService imageValidationService;
    private final ImageStorageService imageStorageService;
    private final NotificationService notificationService;

    public CaseService(DiseaseCaseRepository caseRepository, CropRepository cropRepository,
                       SymptomRepository symptomRepository, UserRepository userRepository,
                       ImageValidationService imageValidationService, ImageStorageService imageStorageService,
                       NotificationService notificationService) {
        this.caseRepository = caseRepository;
        this.cropRepository = cropRepository;
        this.symptomRepository = symptomRepository;
        this.userRepository = userRepository;
        this.imageValidationService = imageValidationService;
        this.imageStorageService = imageStorageService;
        this.notificationService = notificationService;
    }

    // ---------------------------------------------------------------- Feature 1: submission

    /** Values for the crop dropdown on the form. */
    @Transactional(readOnly = true)
    public List<Crop> allCrops() {
        return cropRepository.findAll();
    }

    /** Values for the symptom checklist on the form. */
    @Transactional(readOnly = true)
    public List<Symptom> allSymptoms() {
        return symptomRepository.findAll();
    }

    @Transactional
    public DiseaseCase submit(CaseSubmissionForm form, String farmerPhone) {
        Farmer farmer = findFarmer(farmerPhone);
        Crop crop = cropRepository.findById(form.getCropId())
                .orElseThrow(() -> new BusinessRuleException("cropId", "validation.required"));

        List<Symptom> symptoms = symptomRepository.findAllById(form.getSymptomIds());
        if (symptoms.isEmpty() && !StringUtils.hasText(form.getDescription())) {
            throw new BusinessRuleException("symptomIds", "case.error.noSymptoms");
        }

        // An empty file input still sends one empty file, so ignore empty ones.
        List<MultipartFile> files = form.getImages().stream().filter(f -> !f.isEmpty()).toList();
        if (files.isEmpty() || files.size() > MAX_IMAGES) {
            throw new BusinessRuleException("images", "case.error.imageCount");
        }

        // Check ALL images first, so nothing is saved to disk if one of them is bad.
        List<ImageCheckResult> checks = new ArrayList<>();
        for (MultipartFile file : files) {
            checks.add(imageValidationService.validate(file));
        }

        String description = StringUtils.hasText(form.getDescription()) ? form.getDescription().trim() : null;
        DiseaseCase diseaseCase = new DiseaseCase(farmer, crop, form.getGrowthStage(), description,
                form.getLatitude(), form.getLongitude(), new HashSet<>(symptoms));

        for (int i = 0; i < files.size(); i++) {
            String path = imageStorageService.store(files.get(i));
            ImageCheckResult check = checks.get(i);
            diseaseCase.addImage(new CaseImage(path, check.width(), check.height(), check.sharpness()));
        }

        caseRepository.save(diseaseCase);
        notifyFarmer(diseaseCase);
        return diseaseCase;
    }

    // ---------------------------------------------------------------- Feature 4: tracking

    /**
     * The single place where a case changes status. Writes the history row (inside the entity)
     * and notifies the farmer. Will be called by AI diagnosis (F2/F3) and expert review (F6).
     */
    @Transactional
    public void changeStatus(Long caseId, CaseStatus next, String note) {
        DiseaseCase diseaseCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", caseId));
        diseaseCase.transitionTo(next, note);
        notifyFarmer(diseaseCase);
    }

    @Transactional(readOnly = true)
    public List<DiseaseCase> listForFarmer(String farmerPhone) {
        return caseRepository.findByFarmerPhoneOrderByCreatedAtDesc(farmerPhone);
    }

    @Transactional(readOnly = true)
    public List<DiseaseCase> recentForFarmer(String farmerPhone) {
        return caseRepository.findTop5ByFarmerPhoneOrderByCreatedAtDesc(farmerPhone);
    }

    /** Number of the farmer's cases in each status, for the dashboard cards. */
    @Transactional(readOnly = true)
    public Map<CaseStatus, Long> statusCounts(String farmerPhone) {
        Map<CaseStatus, Long> counts = new EnumMap<>(CaseStatus.class);
        for (CaseStatus status : CaseStatus.values()) {
            counts.put(status, caseRepository.countByFarmerPhoneAndStatus(farmerPhone, status));
        }
        return counts;
    }

    /** A farmer may only open their own case; anyone else's case looks like "not found". */
    @Transactional(readOnly = true)
    public DiseaseCase getForFarmer(Long caseId, String farmerPhone) {
        DiseaseCase diseaseCase = caseRepository.findByIdAndFarmerPhone(caseId, farmerPhone)
                .orElseThrow(() -> new ResourceNotFoundException("Case", caseId));
        return loadDetails(diseaseCase);
    }

    // ---------------------------------------------------------------- Admin (read-only overview)

    @Transactional(readOnly = true)
    public List<DiseaseCase> listAll() {
        return caseRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public DiseaseCase getAny(Long caseId) {
        DiseaseCase diseaseCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", caseId));
        return loadDetails(diseaseCase);
    }

    // ---------------------------------------------------------------- helpers

    /**
     * Images, symptoms and history are loaded lazily. The page is rendered after this transaction ends,
     * so we load them now while the database session is still open.
     */
    private DiseaseCase loadDetails(DiseaseCase diseaseCase) {
        Hibernate.initialize(diseaseCase.getImages());
        Hibernate.initialize(diseaseCase.getSymptoms());
        Hibernate.initialize(diseaseCase.getHistory());
        return diseaseCase;
    }

    private void notifyFarmer(DiseaseCase diseaseCase) {
        String id = String.valueOf(diseaseCase.getId());
        notificationService.notify(diseaseCase.getFarmer(), NotificationType.CASE_UPDATE,
                "notification.case." + diseaseCase.getStatus(), id, "/farmer/cases/" + id);
    }

    private Farmer findFarmer(String phone) {
        if (userRepository.findByPhone(phone).orElse(null) instanceof Farmer farmer) {
            return farmer;
        }
        throw new ResourceNotFoundException("Farmer", phone);
    }
}
