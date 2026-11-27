package com.cdran.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cdran.dto.ContentFlagForm;
import com.cdran.dto.ForumAnswerForm;
import com.cdran.dto.ForumPostForm;
import com.cdran.exception.BusinessRuleException;
import com.cdran.model.entity.ContentFlag;
import com.cdran.model.entity.Expert;
import com.cdran.model.entity.Farmer;
import com.cdran.model.entity.ForumAnswer;
import com.cdran.model.entity.ForumPost;
import com.cdran.model.enums.FlagStatus;
import com.cdran.model.enums.FlagTarget;
import com.cdran.model.enums.NotificationType;
import com.cdran.model.enums.PostStatus;
import com.cdran.model.enums.Role;
import com.cdran.repository.ContentFlagRepository;
import com.cdran.repository.CropRepository;
import com.cdran.repository.ForumAnswerRepository;
import com.cdran.repository.ForumPostRepository;
import com.cdran.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ForumServiceTests {

    @Mock
    private ForumPostRepository postRepository;

    @Mock
    private ForumAnswerRepository answerRepository;

    @Mock
    private ContentFlagRepository flagRepository;

    @Mock
    private CropRepository cropRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    private ForumService forumService;
    private Farmer farmer;
    private Expert expert;

    @BeforeEach
    void setUp() {
        forumService = new ForumService(
                postRepository,
                answerRepository,
                flagRepository,
                cropRepository,
                userRepository,
                notificationService);

        farmer = mock(Farmer.class);
        expert = mock(Expert.class);
    }

    @Test
    void addAnswerMarksPostAnsweredAndNotifiesPostAuthor() {
        when(farmer.getPhone()).thenReturn("01711111111");
        when(expert.getPhone()).thenReturn("01822222222");

        ForumPost post = new ForumPost();
        post.setAuthor(farmer);
        post.setTitle("Leaf spots on paddy");
        post.setBody("Seeing brown spots near field edge.");

        ForumAnswerForm form = new ForumAnswerForm();
        form.setBody("Check moisture and apply recommended fungicide.");

        when(userRepository.findByPhone("01822222222")).thenReturn(Optional.of(expert));
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(ForumPost.class))).thenAnswer(inv -> inv.getArgument(0));

        ForumAnswer answer = forumService.addAnswer(10L, form, "01822222222");

        assertEquals(PostStatus.ANSWERED, post.getStatus());
        assertEquals("Check moisture and apply recommended fungicide.", answer.getBody());
        verify(notificationService).notify(
                eq(farmer),
                eq(NotificationType.FORUM_REPLY),
                eq("notification.forum.reply"),
                any(),
                any());
    }

    @Test
    void farmerCannotVerifyAnswerThrowsBusinessRuleException() {
        when(farmer.getRole()).thenReturn(Role.FARMER);
        when(userRepository.findByPhone("01711111111")).thenReturn(Optional.of(farmer));

        assertThrows(BusinessRuleException.class,
                () -> forumService.verifyAnswer(3L, "01711111111"));
    }

    @Test
    void expertCanVerifyAnswer() {
        when(expert.getRole()).thenReturn(Role.EXPERT);
        ForumAnswer answer = new ForumAnswer();
        when(userRepository.findByPhone("01822222222")).thenReturn(Optional.of(expert));
        when(answerRepository.findById(3L)).thenReturn(Optional.of(answer));
        when(answerRepository.save(any(ForumAnswer.class))).thenAnswer(inv -> inv.getArgument(0));

        forumService.verifyAnswer(3L, "01822222222");

        assertTrue(answer.isVerified());
    }

    @Test
    void flagContentCreatesOpenFlagForPost() {
        ForumPost post = new ForumPost();
        post.setAuthor(farmer);

        ContentFlagForm flagForm = new ContentFlagForm();
        flagForm.setTargetType(FlagTarget.FORUM_POST);
        flagForm.setTargetId(10L);
        flagForm.setReason("Off-topic spam");

        when(userRepository.findByPhone("01711111111")).thenReturn(Optional.of(farmer));
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(flagRepository.save(any(ContentFlag.class))).thenAnswer(inv -> inv.getArgument(0));

        ContentFlag saved = forumService.flagContent(flagForm, "01711111111");

        assertEquals(FlagStatus.OPEN, saved.getStatus());
        assertEquals("Off-topic spam", saved.getReason());
    }
}