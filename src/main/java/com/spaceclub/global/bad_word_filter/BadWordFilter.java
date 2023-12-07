package com.spaceclub.global.bad_word_filter;

import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharSequenceNodeFactory;
import com.googlecode.concurrenttrees.radixinverted.ConcurrentInvertedRadixTree;
import com.googlecode.concurrenttrees.radixinverted.InvertedRadixTree;
import com.spaceclub.global.config.BadWordConfig;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

import static com.spaceclub.global.bad_word_filter.BadWordExceptionMessage.BAD_WORD_DETECTED;
import static com.spaceclub.global.bad_word_filter.BadWordExceptionMessage.FAIL_BAD_WORD_SETUP;

@Slf4j
@Component
@RequiredArgsConstructor
public class BadWordFilter implements ConstraintValidator<BadWord, String> {

    private final BadWordConfig config;

    private static final InvertedRadixTree<String> trie = new ConcurrentInvertedRadixTree<>(new DefaultCharSequenceNodeFactory());

    @PostConstruct
    private void setup() {
        try {
            List<String> badWords = Files.readAllLines(Paths.get(config.path()));
            badWords.forEach(badword -> trie.put(badword, badword));
        } catch (IOException e) {
            throw new IllegalStateException(FAIL_BAD_WORD_SETUP.toString());
        }
    }

    public static void filter(String textToFilter) {
        if (textToFilter == null) return;

        List<String> filteredWords = new ArrayList<>();
        trie.getValuesForKeysContainedIn(textToFilter)
                .forEach(filteredWords::add);

        if (!filteredWords.isEmpty()) {
            log.info("비속어 감지: {}", filteredWords);
            throw new IllegalArgumentException(BAD_WORD_DETECTED.toString());
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        // 성능 테스트 해보기 (parallelStream)
        Spliterator<String> searchedBadWords = trie.getValuesForKeysContainedIn(value).spliterator();
        List<String> filteredWords = StreamSupport.stream(searchedBadWords, true)
                .toList();

        if (!filteredWords.isEmpty()) {
            log.info("비속어 감지: {}", filteredWords);
            throw new IllegalArgumentException(BAD_WORD_DETECTED.toString());
        }
        return true;
    }

}
