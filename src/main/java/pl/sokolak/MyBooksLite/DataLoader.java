package pl.sokolak.MyBooksLite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pl.sokolak.MyBooksLite.model.book.Book;
import pl.sokolak.MyBooksLite.model.book.BookMapper;
import pl.sokolak.MyBooksLite.model.book.BookService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Component
public class DataLoader implements ApplicationRunner {
    private final Logger log = Logger.getLogger(getClass().getName());

    @Value("${dataLoader.populate:false}")
    private boolean populate;

    @Value("${dataLoader.fromLine:0}")
    private long fromLine;

    @Value("${dataLoader.toLine:10000}")
    private long toLine;

    private final BookService bookService;
    private final BookMapper bookMapper;

    @Autowired
    public DataLoader(BookService bookService,
                      BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    public void run(ApplicationArguments args) {
        if (populate) {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("books_data.txt");
            Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines();

            lines.peek(s -> log.log(Level.INFO, "Reading line: {0}", s))
                    .map(s -> s.split("#"))
                    .filter(items -> items.length == 12)
                    .filter(items -> Long.parseLong(items[0].trim()) >= fromLine)
                    .filter(items -> Long.parseLong(items[0].trim()) <= toLine)
                    .peek(this::saveBook)
                    .forEach(s -> log.log(Level.INFO, "Saved"));
            lines.close();
        }
    }

    private void saveBook(String[] items) {
        Book book = new Book();

        book.setAuthor(items[1].trim());

        book.setTitle(items[2].trim());
        book.setSubtitle(items[3].trim());

        book.setPublisher(items[4].trim());

        book.setVolume(items[5].trim());
        book.setEdition(items[6].trim());
        book.setCity(items[7].trim());
        book.setYear(items[8].trim());

        book.setSeries(items[9].trim());

        book.setSeriesVolume(items[10].trim());
        book.setComment(items[11].trim());

        bookService.save(bookMapper.toDto(book));
    }
}
