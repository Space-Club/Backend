package com.spaceclub.user.controller.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public record EventPageResponse<T, E>(
        List<T> data,
        PageableResponse<E> pageData
) {

    public EventPageResponse(List<T> data, Page<E> page) {
        this(data, new PageableResponse<>(page));
    }

    private record PageableResponse<E>(
            boolean first,
            boolean last,
            int pageNumber,
            int size,
            SortResponse sort,
            int totalPages,
            long totalElements
    ) {

        public PageableResponse(Page<E> page) {
            this(
                    page.isFirst(),
                    page.isLast(),
                    page.getNumber(),
                    page.getSize(),
                    new SortResponse(page.getSort()),
                    page.getTotalPages(),
                    page.getTotalElements()
            );
        }

    }

    private record SortResponse(
            boolean empty,
            boolean sorted
    ) {

        public SortResponse(Sort sort) {
            this(
                    sort.isEmpty(),
                    sort.isSorted()
            );
        }

    }

}
