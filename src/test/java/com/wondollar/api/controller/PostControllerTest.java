package com.wondollar.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wondollar.api.domain.Post;
import com.wondollar.api.repository.PostRepository;
import com.wondollar.api.request.PostCreate;
import com.wondollar.api.request.PostEdit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적인 post 요청 시 빈 객체를 반환한다.")
    void postRequestTest() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        // when, then
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(jsonRequest)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andDo(print());
    }

    @Test
    @DisplayName("post 요청 시 title 값이 비어있으면 오류를 반환한다.")
    void postWithTitleNullTest() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title(null)
                .content("내용입니다.")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        // when, then
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(jsonRequest)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation[0].errorMessage").value("타이틀을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("올바른 post 요청 후 DB에 정상적으로 값이 저장되어야 한다.")
    void DBSaveTestAfterPost() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(jsonRequest)
                )
                .andExpect(status().isOk())
                .andDo(print());

        //then
        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().get(0);
        assertEquals("제목입니다.", post.getTitle());
        assertEquals("내용입니다.", post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void getPostTest() throws Exception {
        // given
        Post post = Post.builder()
                .title("12345")
                .content("su")
                .build();

        postRepository.save(post);

        // when, then
        mockMvc.perform(get("/posts/{postId}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("12345"))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andDo(print());
    }

    @Test
    @DisplayName("글 여러개 조회")
    void getPostListTest() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("제목 - " + i)
                        .content("내용 - " + i)
                        .build()
                )
                .toList();

        postRepository.saveAll(requestPosts);

        // when, then
        mockMvc.perform(get("/posts?page=2&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(10)))
                .andExpect(jsonPath("$[0].title").value("제목 - 20"))
                .andExpect(jsonPath("$[9].content").value("내용 - 11"))
                .andDo(print());
    }

    @Test
    @DisplayName("0페이지를 조회해도 1페이지를 반환한다")
    void get0PageTest() throws Exception {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("제목 - " + i)
                        .content("내용 - " + i)
                        .build()
                )
                .toList();

        postRepository.saveAll(requestPosts);

        // when, then
        mockMvc.perform(get("/posts?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(10)))
                .andExpect(jsonPath("$[0].title").value("제목 - 30"))
                .andExpect(jsonPath("$[9].content").value("내용 - 21"))
                .andDo(print());
    }

    @Test
    @DisplayName("글 제목 수정")
    void postUpdateTitleTest() throws Exception {
        // given
        Post post = Post.builder()
                .title("김완수")
                .content("백엔드")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("강원")
                .content("백엔드")
                .build();

        // when, then
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 삭제")
    void postDeleteTest() throws Exception {
        // given
        Post post = Post.builder()
                .title("김완수")
                .content("백엔드")
                .build();

        postRepository.save(post);

        // when, then
        mockMvc.perform(delete("/posts/{postId}", post.getId())
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    void searchPostFailTest() throws Exception {
        mockMvc.perform(get("/posts/{postId}", 1L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}