package com.cdran.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdran.dto.ContentFlagForm;
import com.cdran.dto.ForumAnswerForm;
import com.cdran.dto.ForumPostForm;
import com.cdran.exception.BusinessRuleException;
import com.cdran.exception.ResourceNotFoundException;
import com.cdran.model.entity.ContentFlag;
import com.cdran.model.entity.Crop;
import com.cdran.model.entity.ForumAnswer;
import com.cdran.model.entity.ForumPost;
import com.cdran.model.entity.User;
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

@Service
public class ForumService {

    private final ForumPostRepository postRepository;
    private final ForumAnswerRepository answerRepository;
    private final ContentFlagRepository flagRepository;
    private final CropRepository cropRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ForumService(
            ForumPostRepository postRepository,
            ForumAnswerRepository answerRepository,
            ContentFlagRepository flagRepository,
            CropRepository cropRepository,
            UserRepository userRepository,
            NotificationService notificationService) {
        this.postRepository = postRepository;
        this.answerRepository = answerRepository;
        this.flagRepository = flagRepository;
        this.cropRepository = cropRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public List<ForumPost> searchPosts(Long cropId, String keyword) {
        String normalizedKeyword = keyword != null ? keyword.trim() : "";
        return postRepository.searchVisible(PostStatus.HIDDEN, cropId, normalizedKeyword);
    }

    @Transactional(readOnly = true)
    public ForumPost getVisiblePost(Long postId) {
        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum post not found", postId));
        if (post.getStatus() == PostStatus.HIDDEN) {
            throw new ResourceNotFoundException("Forum post not found", postId);
        }
        return post;
    }

    @Transactional
    public ForumPost createPost(ForumPostForm form, String authorPhone) {
        User author = requireUser(authorPhone);
        Crop cropTag = null;
        if (form.getCropId() != null) {
            cropTag = cropRepository.findById(form.getCropId())
                    .orElseThrow(() -> new ResourceNotFoundException("Crop not found", form.getCropId()));
        }

        ForumPost post = new ForumPost();
        post.setAuthor(author);
        post.setCropTag(cropTag);
        post.setTitle(form.getTitle().trim());
        post.setBody(form.getBody().trim());
        post.setStatus(PostStatus.OPEN);
        return postRepository.save(post);
    }

    @Transactional
    public ForumAnswer addAnswer(Long postId, ForumAnswerForm form, String authorPhone) {
        User author = requireUser(authorPhone);
        ForumPost post = getVisiblePost(postId);

        ForumAnswer answer = new ForumAnswer();
        answer.setAuthor(author);
        answer.setBody(form.getBody().trim());
        post.addAnswer(answer);

        ForumPost savedPost = postRepository.save(post);
        ForumAnswer savedAnswer = savedPost.getAnswers().get(savedPost.getAnswers().size() - 1);

        User postAuthor = savedPost.getAuthor();
        if (postAuthor != null && !postAuthor.getPhone().equals(author.getPhone())) {
            notificationService.notify(
                    postAuthor,
                    NotificationType.FORUM_REPLY,
                    "notification.forum.reply",
                    String.valueOf(savedPost.getId()),
                    "/forum/posts/" + savedPost.getId());
        }
        return savedAnswer;
    }

    @Transactional
    public void verifyAnswer(Long answerId, String verifierPhone) {
        User verifier = requireUser(verifierPhone);
        if (verifier.getRole() != Role.EXPERT
                && verifier.getRole() != Role.FIELD_OFFICER
                && verifier.getRole() != Role.ADMIN) {
            throw new BusinessRuleException("role", "forum.error.verifyForbidden");
        }

        ForumAnswer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum answer not found", answerId));
        answer.verify(verifier, LocalDateTime.now());
        answerRepository.save(answer);
    }

    @Transactional
    public void hidePost(Long postId, String actorPhone) {
        User actor = requireUser(actorPhone);
        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum post not found", postId));

        boolean isAdmin = actor.getRole() == Role.ADMIN;
        boolean isAuthor = post.getAuthor() != null && post.getAuthor().getPhone().equals(actor.getPhone());
        if (!isAdmin && !isAuthor) {
            throw new BusinessRuleException("role", "forum.error.hideForbidden");
        }

        post.hide();
        postRepository.save(post);
    }

    @Transactional
    public ContentFlag flagContent(ContentFlagForm form, String reporterPhone) {
        User reporter = requireUser(reporterPhone);
        if (form.getTargetType() == FlagTarget.FORUM_POST) {
            postRepository.findById(form.getTargetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Forum post not found", form.getTargetId()));
        } else if (form.getTargetType() == FlagTarget.FORUM_ANSWER) {
            answerRepository.findById(form.getTargetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Forum answer not found", form.getTargetId()));
        } else {
            throw new BusinessRuleException("targetType", "forum.error.invalidFlagTarget");
        }

        ContentFlag flag = new ContentFlag();
        flag.setReporter(reporter);
        flag.setTargetType(form.getTargetType());
        flag.setTargetId(form.getTargetId());
        flag.setReason(form.getReason().trim());
        flag.setStatus(FlagStatus.OPEN);
        return flagRepository.save(flag);
    }

    private User requireUser(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", phone));
    }
}