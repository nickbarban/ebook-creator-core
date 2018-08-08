package com.ebookcreator.core.domain;

import lombok.Data;

/**
 * @author Nick Barban.
 */
@Data
public class Letter {
    private final String character;
    private final Float startX;
    private final Float endX;
    private final String fontName;
    private final Float fontSize;
    private final Float whiteSpace;
}
