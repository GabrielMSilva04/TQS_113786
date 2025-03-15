package deti.tqs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Library {
    private List<Book> store = new ArrayList<>();

    public void addBook(Book book) {
        store.add(book);
    }

    public List<Book> findBooksByAuthor(String author) {
        return store.stream()
                .filter(book -> book.getAuthor().equals(author))
                .collect(Collectors.toList());
    }

    public List<Book> findBooks(LocalDateTime from, LocalDateTime to) {
        return store.stream()
                .filter(book -> !book.getPublished().isBefore(from) && !book.getPublished().isAfter(to))
                .collect(Collectors.toList());
    }
}