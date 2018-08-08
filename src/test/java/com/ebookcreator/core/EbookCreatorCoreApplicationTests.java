package com.ebookcreator.core;

import com.ebookcreator.core.domain.Book;
import com.ebookcreator.core.domain.Bookmark;
import com.ebookcreator.core.domain.Line;
import com.ebookcreator.core.domain.Page;
import com.ebookcreator.core.service.BookStripper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EbookCreatorCoreApplicationTests {


    @Test
    public void contextLoads() {
    }

    @Test
    public void shouldGetAllLettersFromPage() throws IOException {
        List<String> content = new ArrayList<String>() {{
            add("Foreword");
            add("IF a colleague were to say to you, “Spouse of me this night today manufactures the");
            add("unusual meal in a home. You will join?” three things would likely cross your mind:");
            add("third, that you had been invited to dinner; second, that English was not your col-");
            add("league’s first language; and first, a good deal of puzzlement.");
            add("If you have ever studied a second language yourself and then tried to use it");
            add("outside the classroom, you know that there are three things you must master: how");
            add("the language is structured (grammar), how to name things you want to talk about");
            add("(vocabulary), and the customary and effective ways to say everyday things");
            add("(usage). Too often only the first two are covered in the classroom, and you find");
            add("native speakers constantly suppressing their laughter as you try to make yourself");
            add("understood.");
            add("It is much the same with a programming language. You need to understand the");
            add("core language: is it algorithmic, functional, object-oriented? You need to know the");
            add("vocabulary: what data structures, operations, and facilities are provided by the");
            add("standard libraries? And you need to be familiar with the customary and effective");
            add("ways to structure your code. Books about programming languages often cover");
            add("only the first two, or discuss usage only spottily. Maybe that’s because the first");
            add("two are in some ways easier to write about. Grammar and vocabulary are proper-");
            add("ties of the language alone, but usage is characteristic of a community that uses it.");
            add("The Java programming language, for example, is object-oriented with single");
            add("inheritance and supports an imperative (statement-oriented) coding style within");
            add("each method. The libraries address graphic display support, networking, distrib-");
            add("uted computing, and security. But how is the language best put to use in practice?");
            add("There is another point. Programs, unlike spoken sentences and unlike most");
            add("books and magazines, are likely to be changed over time. It’s typically not enough");
            add("to produce code that operates effectively and is readily understood by other per-");
            add("sons; one must also organize the code so that it is easy to modify. There may be");
            add("ten ways to write code for some task T. Of those ten ways, seven will be awkward,");
            add("inefficient, or puzzling. Of the other three, which is most likely to be similar to the");
            add("code needed for the task T' in next year’s software release?");
            add("xi");
        }};
        Map<Integer, String> givenBookmarks = new LinkedHashMap<Integer, String>() {{
            put(1, "Cover");
            put(4, "Title Page");
            put(5, "Copyright Page");
            put(8, "Contents");
            put(12, "Foreword");
            put(14, "Preface");
            put(18, "Acknowledgments");
            put(22, "5 Generics");
            put(62, "Index");
        }};
        String givenAuthor = "Joshua Bloch";
        String givenTitle = "Effective Java Third Edition";
        String filename = "../books/Effective Java 3d edition.pdf";

        byte[] pdf = Files.readAllBytes(Paths.get(filename));
        PDDocument document = PDDocument.load(pdf);
        Book book = getBook(document);

        Assertions.assertThat(book)
                .isNotNull();
        Assertions.assertThat(book.getAuthor())
                .isNotNull()
                .isEqualTo(givenAuthor);
        Assertions.assertThat(book.getTitle())
                .isNotNull()
                .isEqualTo(givenTitle);
        Assertions.assertThat(book.getPages())
                .isNotEmpty()
                .hasSameSizeAs(givenBookmarks.keySet().size());
        Assertions.assertThat(book.getBookmarks())
                .isNotEmpty()
                .doesNotContainNull()
                .hasSameSizeAs(givenBookmarks.keySet())
                .extracting(Bookmark::getTitle)
                .containsExactlyElementsOf(givenBookmarks.values());
        Assertions.assertThat(book.getBookmarks())
                .isNotEmpty()
                .doesNotContainNull()
                .hasSameSizeAs(givenBookmarks.keySet())
                .extracting(Bookmark::getPageNum)
                .containsExactlyElementsOf(givenBookmarks.keySet());
    }

    private Book getBook(PDDocument document) {
        List<Bookmark> bookmarks = getBookmarks(document);
        String author = document.getDocumentInformation().getAuthor();
        String title = document.getDocumentInformation().getTitle();
        List<Page> pages = bookmarks.stream()
                .map(b -> getPage(document, b.getPageNum(), b.getNextPageNum() - 1))
                .collect(Collectors.toList());

        return new Book(title, author, pages, bookmarks);
    }

    private Page getPage(PDDocument document, Integer startPageNum, Integer endPageNum) {
        startPageNum = startPageNum == null ? 1 : startPageNum;
        endPageNum = endPageNum == null ? document.getDocumentCatalog().getPages().getCount() : endPageNum;

        try {
            BookStripper stripper = new BookStripper();
            stripper.setStartPage(startPageNum);
            stripper.setEndPage(endPageNum);
            List<Line> lines = stripper.getLines();
            return new Page(lines, startPageNum);
        } catch (IOException e) {
            String message = String.format("Can not read document %s", document.getDocumentInformation().getTitle());
            throw new RuntimeException(message);
        }
    }

    private List<Bookmark> getBookmarks(PDDocument document) {
        List<Bookmark> bookmarks = new ArrayList<>();
        PDDocumentOutline documentOutline = document.getDocumentCatalog().getDocumentOutline();
        PDOutlineItem current = documentOutline.getFirstChild();

        while (current != null) {
            String title = current.getTitle();

            Integer pageNum;
            Integer nextPageNum = null;
            try {
                PDPage currentPage = current.findDestinationPage(document);
                pageNum = document.getDocumentCatalog().getPages().indexOf(currentPage) + 1;

                current = current.getNextSibling();

                if (current != null) {
                    PDPage nextPage = current.findDestinationPage(document);
                    nextPageNum = document.getDocumentCatalog().getPages().indexOf(nextPage) + 1;
                }

            } catch (IOException e) {
                String message = String.format("Document %s does not contain page %s",
                        document.getDocumentInformation().getTitle(), current.getTitle());
                throw new RuntimeException(message);
            }

            Bookmark bookmark = new Bookmark(title, pageNum, nextPageNum);
            bookmarks.add(bookmark);
        }

        return bookmarks;
    }
}
