package com.cdran.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ForumPostForm {

    @NotBlank(message = "{forum.validation.title.required}")
    @Size(max = 200, message = "{forum.validation.title.size}")
    private String title;

    @NotBlank(message = "{forum.validation.body.required}")
    @Size(max = 3000, message = "{forum.validation.body.size}")
    private String body;

    private Long cropId;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public Long getCropId() { return cropId; }
    public void setCropId(Long cropId) { this.cropId = cropId; }
}