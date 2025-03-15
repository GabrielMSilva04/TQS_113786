package deti.tqs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.assertj.core.api.Assertions.assertThat;

public class BookSearchSteps {
    Library library = new Library();
    List<Book> result = new ArrayList<>();

    @ParameterType("yyyy-MM-dd")
    public LocalDateTime iso8601Date(String date) {
        return LocalDateTime.parse(date + "T00:00:00");
    }

    @Given("a book with the title {string}, written by {string}, published in {iso8601Date}")
    public void addNewBook(final String title, final String author, final LocalDateTime published) {
        Book book = new Book(title, author, published);
        library.addBook(book);
    }

    @Given("the following books exist in the library")
    public void theFollowingBooksExistInTheLibrary(DataTable dataTable) {
        List<Map<String, String>> books = dataTable.asMaps(String.class, String.class);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Map<String, String> book : books) {
            String title = book.get("title");
            String author = book.get("author");
            LocalDateTime published = LocalDateTime.parse(book.get("published"), formatter);
            library.addBook(new Book(title, author, published));
        }
    }

    @When("the customer searches for books by author {string}")
    public void searchBooksByAuthor(final String author) {
        result = library.findBooksByAuthor(author);
    }

    @When("the customer searches for books published between {iso8601Date} and {iso8601Date}")
    public void setSearchParameters(final LocalDateTime from, final LocalDateTime to) {
        result = library.findBooks(from, to);
    }

    @Then("{int} books should have been found")
    public void verifyAmountOfBooksFound(final int booksFound) {
        assertThat(result.size()).isEqualTo(booksFound);
    }

    @Then("Book {int} should have the title {string}")
    public void verifyBookAtPosition(final int position, final String title) {
        assertThat(result.get(position - 1).getTitle()).isEqualTo(title);
    }
}