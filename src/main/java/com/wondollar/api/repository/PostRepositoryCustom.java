package com.wondollar.api.repository;

import com.wondollar.api.domain.Post;
import com.wondollar.api.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}
