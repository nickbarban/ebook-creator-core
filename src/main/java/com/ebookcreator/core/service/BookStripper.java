package com.ebookcreator.core.service;

import com.ebookcreator.core.domain.Letter;
import com.ebookcreator.core.domain.Line;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Nick Barban.
 */

public class BookStripper extends PDFTextStripper {

    private List<Line> lines = new ArrayList<>();

    /**
     * Instantiate a new PDFTextStripper object.
     *
     * @throws IOException If there is an error loading the properties.
     */
    public BookStripper() throws IOException {
        super();
    }

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) {

        List<Letter> letters = textPositions.stream()
                .map(this::createLetter)
                .collect(Collectors.toList());

        Line line = new Line(letters, textPositions.get(0).getY());
        lines.add(line);
        //        super.writeString(text, textPositions);
    }

    private Letter createLetter(TextPosition tp) {
        String character = tp.getUnicode();
        Float startX = tp.getX();
        Float endX = tp.getEndX();
        String fontName = tp.getFont().getName();
        Float fontSize = tp.getFontSize();
        Float whiteSpace = tp.getWidthOfSpace();
        return new Letter(character, startX, endX, fontName, fontSize, whiteSpace);
    }

    public List<Line> getLines() {
        return lines;
    }
}
