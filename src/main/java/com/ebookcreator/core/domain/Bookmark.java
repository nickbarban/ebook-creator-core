package com.ebookcreator.core.domain;

import lombok.Data;

/**
 * @author Nick Barban.
 */
@Data
public class Bookmark {
    private final String title;
    private final Integer pageNum;
    private final Integer nextPageNum;
}
