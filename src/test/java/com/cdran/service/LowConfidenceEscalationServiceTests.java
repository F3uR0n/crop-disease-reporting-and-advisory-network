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

import com.cdran.dto.EscalationForm;
import com.cdran.exception.BusinessRuleException;
import com.cdran.model.entity.CaseEscalation;
import com.cdran.model.entity.DiseaseCase;
import com.cdran.model.entity.Expert;
import com.cdran.model.entity.Farmer;
import com.cdran.model.enums.AccountStatus;
import com.cdran.model.enums.CaseStatus;
import com.cdran.model.enums.NotificationType;
import com.cdran.model.enums.Role;
import com.cdran.repository.CaseEscalationRepository;
import com.cdran.repository.DiseaseCaseRepository;
import com.cdran.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LowConfidenceEscalationServiceTests {

    @Mock
    private CaseEscalationRepository escalationRepository;

    @Mock
    private DiseaseCaseRepository diseaseCaseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    private LowConfidenceEscalationService service;
    private Farmer farmer;
    private Expert activeExpert;

    @BeforeEach
    void setUp() {
        service = new LowConfidenceEscalationService(
                escalationRepository, diseaseCaseRepository, userRepository, notificationService, 0.75);
        farmer = mock(Farmer.class);
        activeExpert = mock(Expert.class);
    }

    @Test
    void evaluateAndEscalateAutomaticallyTransitionsCaseAndNotifiesFarmerAndExpert() {
        DiseaseCase diseaseCase = mock(DiseaseCase.class);
        when(diseaseCase.getId()).thenReturn(12L);
        when(diseaseCase.getStatus()).thenReturn(CaseStatus.AI_ANALYZED);
        when(diseaseCase.getFarmer()).thenReturn(farmer);
        when(activeExpert.getRole()).thenReturn(Role.EXPERT);

        when(diseaseCaseRepository.findById(12L)).thenReturn(Optional.of(diseaseCase));
        when(escalationRepository.existsByDiseaseCaseIdAndResolvedFalse(12L)).thenReturn(false);
        when(escalationRepository.save(any(CaseEscalation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.findByStatus(AccountStatus.ACTIVE)).thenReturn(List.of(activeExpert));

        Optional<CaseEscalation> created = service.evaluateAndEscalateAutomatically(
                12L, "Brown Spot", 0.42, false);

        assertTrue(created.isPresent());
        assertEquals(0.42, created.get().getConfidence());
        assertEquals(0.75, created.get().getThreshold());
        verify(diseaseCase).transitionTo(eq(CaseStatus.UNDER_REVIEW), any());
        verify(notificationService).notify(
                eq(farmer),
                eq(NotificationType.CASE_UPDATE),
                eq("notification.escalation.created"),
                eq("12"),
                eq("/farmer/cases/12"));
        verify(notificationService).notify(
                eq(activeExpert),
                eq(NotificationType.CASE_UPDATE),
                eq("notification.escalation.reviewer"),
                eq("12"),
                eq("/cases/12/escalations"));
    }

    @Test
    void highConfidenceWithKnownLabelDoesNotEscalateAutomatically() {
        assertFalse(service.shouldEscalate("Blast", 0.85, false));
    }

    @Test
    void escalateExplicitlyRejectsAboveThresholdWhenAiAvailable() {
        DiseaseCase diseaseCase = mock(DiseaseCase.class);
        when(diseaseCase.getFarmer()).thenReturn(farmer);
        when(farmer.getPhone()).thenReturn("01711111111");
        when(farmer.getRole()).thenReturn(Role.FARMER);

        EscalationForm form = new EscalationForm();
        form.setPredictedLabel("Blast");
        form.setConfidence(0.90);
        form.setAiUnavailable(false);
        form.setReason("Manual check");

        when(userRepository.findByPhone("01711111111")).thenReturn(Optional.of(farmer));
        when(diseaseCaseRepository.findById(12L)).thenReturn(Optional.of(diseaseCase));

        assertThrows(BusinessRuleException.class,
                () -> service.escalateExplicitly(12L, form, "01711111111"));
    }
}