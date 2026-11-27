package com.cdran.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.cdran.model.enums.PostStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "forum_posts")
public class ForumPost extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id")
    private Crop cropTag;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 3000)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PostStatus status = PostStatus.OPEN;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<ForumAnswer> answers = new ArrayList<>();

    public void addAnswer(ForumAnswer answer) {
        answer.setPost(this);
        this.answers.add(answer);
        if (this.status == PostStatus.OPEN) {
            this.status = PostStatus.ANSWERED;
        }
    }

    public boolean hasVerifiedAnswer() {
        return answers.stream().anyMatch(ForumAnswer::isVerified);
    }

    public void hide() {
        this.status = PostStatus.HIDDEN;
    }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public Crop getCropTag() { return cropTag; }
    public void setCropTag(Crop cropTag) { this.cropTag = cropTag; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public PostStatus getStatus() { return status; }
    public void setStatus(PostStatus status) { this.status = status; }

    public List<ForumAnswer> getAnswers() { return answers; }
}