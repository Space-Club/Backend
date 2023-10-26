package com.spaceclub.user.controller.dto;

import org.springframework.data.domain.Page;

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
            int totalPages,
            long totalElements
    ) {

        public PageableResponse(Page<E> page) {
            this(
                    page.isFirst(),
                    page.isLast(),
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalPages(),
                    page.getTotalElements()
            );
        }

    }

}
