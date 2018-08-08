package com.ebookcreator.core.domain;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Nick Barban.
 */
@Data
public class Line {
    private final List<Letter> letters;
    private final Float y;
    private boolean startParagraph;
    private boolean endParagraph;

    public String toText() {
        return this.letters.stream()
                .map(Letter::getCharacter)
                .collect(Collectors.joining(""));
    }
}
