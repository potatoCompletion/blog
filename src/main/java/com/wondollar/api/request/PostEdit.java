package com.wondollar.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostEdit {

    @NotBlank(message = "타이틀을 입력해주세요.")
    private final String title;
    @NotBlank(message = "컨텐트를 입력해주세요.")
    private final String content;

    @Builder
    public PostEdit(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
