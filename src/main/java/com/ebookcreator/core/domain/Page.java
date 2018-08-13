package com.ebookcreator.core.domain;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(Page.class);
    private final List<Line> lines;
    private final Integer pageNum;
    private final Float minX;
    private final Float maxX;

    public Page(List<Line> lines, Integer pageNum) {

        this.lines = processLines(lines);
        this.pageNum = pageNum;
        this.minX = findStartX(this.lines);
        this.maxX = findEndX(this.lines);
    }

    private Float findEndX(List<Line> lines) {

        if (CollectionUtils.isEmpty(lines)) {
            LOGGER.warn("Page {} has no line", this.pageNum);
            return -1.0f;
        }

        return lines.stream()
                .map(line -> line.getLetters().get(line.getLetters().size() - 1).getEndX())
                .max((Float::compareTo))
                .orElseThrow(() -> new RuntimeException("Can not find maximum X coordinate"));
    }

    private Float findStartX(List<Line> lines) {

        if (CollectionUtils.isEmpty(lines)) {
            LOGGER.warn("Page {} has no line", this.pageNum);
            return -1.0f;
        }

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
