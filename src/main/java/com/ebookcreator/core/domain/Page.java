package com.ebookcreator.core.domain;

import lombok.Data;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Nick Barban.
 */
@Data
public class Page {
    private final List<Line> lines;
    private final Integer pageNum;
    private final Float startX;
    private final Float endX;

    public Page(List<Line> lines, Integer pageNum) {

        this.lines = processLines(lines);
        this.pageNum = pageNum;
        this.startX = findStartX(this.lines);
        this.endX = findEndX(this.lines);
    }

    private Float findEndX(List<Line> lines) {
        return lines.stream()
                .map(line -> line.getLetters().get(0).getEndX())
                .max((Float::compareTo))
                .orElseThrow(() -> new RuntimeException("Can not find maximum X coordinate"));
    }

    private Float findStartX(List<Line> lines) {
        return lines.stream()
                .map(line -> line.getLetters().get(0).getStartX())
                .min((Float::compareTo))
                .orElseThrow(() -> new RuntimeException("Can not find minimal X coordinate"));
    }

    private List<Line> processLines(List<Line> lines) {
        Map<Float, List<Line>> map = lines.stream()
                .collect(Collectors.groupingBy(Line::getY));

        return map.keySet().stream()
                .map(k -> mergeLine(map.get(k)))
                .sorted(Comparator.comparing(Line::getY))
                .collect(Collectors.toList());
    }

    private Line mergeLine(List<Line> lines) {
        List<Letter> letters = lines.stream()
                .map(Line::getLetters)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return new Line(letters, lines.get(0).getY());
    }
}
