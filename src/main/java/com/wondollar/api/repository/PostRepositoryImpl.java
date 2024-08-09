package com.wondollar.api.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wondollar.api.domain.Post;
import com.wondollar.api.request.PostSearch;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.wondollar.api.domain.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getList(PostSearch postSearch) {
        return jpaQueryFactory.selectFrom(post)
                .limit(postSearch.getSize())
                .offset(postSearch.getOffset())
                .orderBy(post.id.desc())
                .fetch();
    }
}
