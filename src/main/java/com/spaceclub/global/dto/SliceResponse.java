package com.spaceclub.global.dto;

import org.springframework.data.domain.Slice;

import java.util.List;

public record SliceResponse<T, E>(
        List<T> data,
        PageableResponse<E> sliceData
) {

    public SliceResponse(List<T> data, Slice<E> slice) {
        this(data, new PageableResponse<>(slice));
    }

    private record PageableResponse<E>(
            boolean first,
            boolean last,
            int number,
            int size,
            int numberOfElements
    ) {

        public PageableResponse(Slice<E> slice) {
            this(
                    slice.isFirst(),
                    slice.isLast(),
                    slice.getNumber(),
                    slice.getSize(),
                    slice.getNumberOfElements()
            );
        }

    }

}
