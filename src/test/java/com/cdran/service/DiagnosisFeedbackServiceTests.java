package com.cdran.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cdran.dto.DiagnosisFeedbackForm;
import com.cdran.exception.BusinessRuleException;
import com.cdran.model.entity.DiagnosisFeedback;
import com.cdran.model.entity.DiseaseCase;
import com.cdran.model.entity.Expert;
import com.cdran.model.entity.Farmer;
import com.cdran.model.enums.FollowUpStatus;
import com.cdran.model.enums.NotificationType;
import com.cdran.model.enums.RecoveryOutcome;
import com.cdran.model.enums.ReviewDecision;
import com.cdran.model.enums.Role;
import com.cdran.repository.DiagnosisFeedbackRepository;
import com.cdran.repository.DiseaseCaseRepository;
import com.cdran.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiagnosisFeedbackServiceTests {

    @Mock
    private DiagnosisFeedbackRepository feedbackRepository;

    @Mock
    private DiseaseCaseRepository diseaseCaseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    private DiagnosisFeedbackService service;
    private Farmer farmer;
    private Expert expert;

    @BeforeEach
    void setUp() {
        service = new DiagnosisFeedbackService(
                feedbackRepository, diseaseCaseRepository, userRepository, notificationService);
        farmer = mock(Farmer.class);
        expert = mock(Expert.class);
    }

    @Test
    void submitOrUpdateRecordsFeedbackAndNotifiesFarmerWhenReviewedByExpert() {
        when(farmer.getPhone()).thenReturn("01711111111");
        when(expert.getPhone()).thenReturn("01822222222");

        DiseaseCase diseaseCase = mock(DiseaseCase.class);
        when(diseaseCase.getId()).thenReturn(5L);
        when(diseaseCase.getFarmer()).thenReturn(farmer);

        DiagnosisFeedbackForm form = new DiagnosisFeedbackForm();
        form.setDecision(ReviewDecision.CONFIRMED);
        form.setRecoveryOutcome(RecoveryOutcome.RECOVERED);
        form.setFollowUpStatus(FollowUpStatus.ANSWERED);
        form.setAiLabel("Rice Blast");
        form.setExpertLabel("Rice Blast");
        form.setAiConfidence(0.88);
        form.setNotes("Fungicide worked within 5 days.");

        when(userRepository.findByPhone("01822222222")).thenReturn(Optional.of(expert));
        when(diseaseCaseRepository.findById(5L)).thenReturn(Optional.of(diseaseCase));
        when(feedbackRepository.findByDiseaseCaseId(5L)).thenReturn(Optional.empty());
        when(feedbackRepository.save(any(DiagnosisFeedback.class))).thenAnswer(inv -> inv.getArgument(0));

        DiagnosisFeedback saved = service.submitOrUpdate(5L, form, "01822222222");

        assertTrue(saved.isAiWasCorrect());
        assertEquals(RecoveryOutcome.RECOVERED, saved.getRecoveryOutcome());
        verify(notificationService).notify(
                eq(farmer),
                eq(NotificationType.FOLLOW_UP),
                eq("notification.feedback.updated"),
                any(),
                any());
    }

    @Test
    void correctedDecisionWithoutExpertLabelThrowsBusinessRuleException() {
        when(farmer.getRole()).thenReturn(Role.FARMER);
        when(farmer.getPhone()).thenReturn("01711111111");

        DiseaseCase diseaseCase = mock(DiseaseCase.class);
        when(diseaseCase.getFarmer()).thenReturn(farmer);

        DiagnosisFeedbackForm form = new DiagnosisFeedbackForm();
        form.setDecision(ReviewDecision.CORRECTED);
        form.setRecoveryOutcome(RecoveryOutcome.NO_CHANGE);
        form.setFollowUpStatus(FollowUpStatus.ANSWERED);
        form.setExpertLabel("   ");

        when(userRepository.findByPhone("01711111111")).thenReturn(Optional.of(farmer));
        when(diseaseCaseRepository.findById(5L)).thenReturn(Optional.of(diseaseCase));

        assertThrows(BusinessRuleException.class,
                () -> service.submitOrUpdate(5L, form, "01711111111"));
    }

    @Test
    void updatingExistingFeedbackOverwritesCleanly() {
        when(farmer.getRole()).thenReturn(Role.FARMER);
        when(farmer.getPhone()).thenReturn("01711111111");

        DiseaseCase diseaseCase = mock(DiseaseCase.class);
        when(diseaseCase.getId()).thenReturn(5L);
        when(diseaseCase.getFarmer()).thenReturn(farmer);

        DiagnosisFeedback existing = new DiagnosisFeedback();
        existing.setDiseaseCase(diseaseCase);

        DiagnosisFeedbackForm form = new DiagnosisFeedbackForm();
        form.setDecision(ReviewDecision.CORRECTED);
        form.setRecoveryOutcome(RecoveryOutcome.IMPROVING);
        form.setFollowUpStatus(FollowUpStatus.ANSWERED);
        form.setAiLabel("Leaf Spot");
        form.setExpertLabel("Bacterial Blight");
        form.setAiConfidence(0.61);

        when(userRepository.findByPhone("01711111111")).thenReturn(Optional.of(farmer));
        when(diseaseCaseRepository.findById(5L)).thenReturn(Optional.of(diseaseCase));
        when(feedbackRepository.findByDiseaseCaseId(5L)).thenReturn(Optional.of(existing));
        when(feedbackRepository.save(any(DiagnosisFeedback.class))).thenAnswer(inv -> inv.getArgument(0));

        DiagnosisFeedback updated = service.submitOrUpdate(5L, form, "01711111111");

        assertFalse(updated.isAiWasCorrect());
        assertEquals("Bacterial Blight", updated.getExpertLabel());
    }
}