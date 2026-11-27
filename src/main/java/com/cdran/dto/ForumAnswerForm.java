package com.cdran.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ForumAnswerForm {

    @NotBlank(message = "{forum.validation.answer.required}")
    @Size(max = 2500, message = "{forum.validation.answer.size}")
    private String body;

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}