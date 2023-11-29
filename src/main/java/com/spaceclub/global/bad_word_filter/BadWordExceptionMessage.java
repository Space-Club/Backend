package com.spaceclub.global.bad_word_filter;

public enum BadWordExceptionMessage {

    FAIL_BAD_WORD_SETUP("비속어 목록 Trie 생성 실패"),
    BAD_WORD_DETECTED("비속어가 발견 되었습니다");

    BadWordExceptionMessage(String message) {
        this.message = message;
    }

    private final String message;
}
