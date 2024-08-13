package com.wondollar.api.service;

import com.wondollar.api.domain.Post;
import com.wondollar.api.exception.PostNotFound;
import com.wondollar.api.repository.PostRepository;
import com.wondollar.api.request.PostCreate;
import com.wondollar.api.request.PostEdit;
import com.wondollar.api.request.PostSearch;
import com.wondollar.api.response.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    void writeTest() {
        //  given
        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        // when
        postService.write(postCreate);

        // then
        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().get(0);
        assertEquals("제목입니다.", post.getTitle());
        assertEquals("내용입니다.", post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void searchPostTest() {
        // given
        Post requestPost = postRepository.save(Post.builder()
                .title("foo")
                .content("bar")
                .build());

        // when
        PostResponse postResponse = postService.get(requestPost.getId());

        // then
        assertNotNull(postResponse);
        assertEquals("foo", postResponse.getTitle());
        assertEquals("bar", postResponse.getContent());
    }

    @Test
    @DisplayName("글 1페이지 조회")
    void searchPostListTest() {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                        .mapToObj(i -> Post.builder()
                                    .title("제목 - " + i)
                                    .content("내용 - " + i)
                                    .build()
                        )
                .toList();

        postRepository.saveAll(requestPosts);
        PostSearch postSearch = PostSearch.builder()
                .page(1)
                .size(10)
                .build();

        // when
        List<PostResponse> posts = postService.getList(postSearch);

        // then
        assertEquals(10, posts.size());
        assertEquals("제목 - 30", posts.get(0).getTitle());
        assertEquals("제목 - 21", posts.get(9).getTitle());
    }

    @Test
    @DisplayName("페이지를 0으로 요청해도 첫 페이지를 가져온다.")
    void get0PageTest() {
        // given
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("제목 - " + i)
                        .content("내용 - " + i)
                        .build()
                )
                .toList();

        postRepository.saveAll(requestPosts);
        PostSearch postSearch = PostSearch.builder()
                .page(0)
                .size(10)
                .build();

        // when
        List<PostResponse> posts = postService.getList(postSearch);

        // then
        assertEquals(10, posts.size());
        assertEquals("제목 - 30", posts.get(0).getTitle());
        assertEquals("제목 - 21", posts.get(9).getTitle());
    }

    @Test
    @DisplayName("글 제목 수정")
    void postUpdateTitleTest() {
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

        // when
        postService.edit(post.getId(), postEdit);

        //then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id = " + post.getId()));
        assertEquals("강원", changedPost.getTitle());
        assertEquals("백엔드", changedPost.getContent());
    }

    @Test
    @DisplayName("글 내용 수정")
    void postUpdateContentTest() {
        // given
        Post post = Post.builder()
                .title("김완수")
                .content("백엔드")
                .build();

        postRepository.save(post);

        // null 값은 무시하고 기존 값이 유지되어야 함
        PostEdit postEdit = PostEdit.builder()
                .title(null)
                .content("개발자")
                .build();

        // when
        postService.edit(post.getId(), postEdit);

        //then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id = " + post.getId()));
        assertEquals("김완수", changedPost.getTitle());
        assertEquals("개발자", changedPost.getContent());
    }

    @Test
    @DisplayName("게시글 삭제")
    void postDeleteTest() {
        // given
        Post post = Post.builder()
                .title("김완수")
                .content("백엔드")
                .build();

        postRepository.save(post);

        // when
        postService.delete(post.getId());

        // then
        assertEquals(0, postRepository.count());
    }

    @Test
    @DisplayName("글 1개 조회 - 존재하지 않는 글")
    void searchPostFailTest() {
        // given
        Post post = postRepository.save(Post.builder()
                .title("foo")
                .content("bar")
                .build());

        // when, then
        assertThrows(PostNotFound.class, () -> {
            postService.get(post.getId() + 1L);
        });
    }

    @Test
    @DisplayName("게시글 삭제 - 존재하지 않는 글")
    void postDeleteFailTest() {
        // given
        Post post = Post.builder()
                .title("김완수")
                .content("백엔드")
                .build();

        postRepository.save(post);

        // when, then
        assertThrows(PostNotFound.class, () -> {
            postService.delete(post.getId() + 1L);
        });
    }
}