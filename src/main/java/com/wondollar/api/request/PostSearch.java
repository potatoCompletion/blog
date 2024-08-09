package com.wondollar.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Getter
@Setter
public class PostSearch {

    private static final int MAX_SIZE = 2000;

    private int page = 1;
    private int size = 10;

    @Builder
    public PostSearch(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public long getOffset() {
        return (long) (max(1, page) - 1) * min(size, MAX_SIZE);
    }
}
