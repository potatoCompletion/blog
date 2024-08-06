package com.wondollar.api.controller;

import com.wondollar.api.request.PostCreate;
import com.wondollar.api.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping ("/posts")
    public Map<String, String> get(@RequestBody @Valid PostCreate request) {
        postService.write(request);
        return Map.of();
    }
}
