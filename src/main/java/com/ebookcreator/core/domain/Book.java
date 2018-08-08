package com.ebookcreator.core.domain;

import lombok.Data;

import java.util.List;

/**
 * @author Nick Barban.
 */
@Data
public class Book {
    private final String title;
    private final String author;
    private final List<Page> pages;
    private final List<Bookmark> bookmarks;
}
