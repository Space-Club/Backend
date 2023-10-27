package com.spaceclub.global.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T, E>(
        List<T> data,
        PageableResponse<E> pageData
) {

    public PageResponse(List<T> data, Page<E> page) {
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
