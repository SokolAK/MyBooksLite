package pl.sokolak.MyBooksLite.model.book;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepo bookRepo;
    private final BookMapper bookMapper;

    public BookService(BookRepo bookRepo, BookMapper bookMapper) {
        this.bookRepo = bookRepo;
        this.bookMapper = bookMapper;
    }

    public long count() {
        return bookRepo.count();
    }

    public List<BookDto> findAll() {
        return bookRepo.findAllByOrderById().stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> findAll(Pageable pageable) {
        return bookRepo.findAllByOrderById(pageable).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> findAll(String phrase, Map<String, Boolean> columnList, Pageable pageable) {
        if (phrase == null || phrase.isEmpty()) {
            return findAll(pageable);
        } else {
            return bookRepo.findAllContainingPhrase(phrase, columnList, pageable).stream()
                    .map(bookMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public BookDto save(BookDto bookDto) {
        return bookMapper.toDto(bookRepo.save(bookMapper.toEntity(bookDto)));
    }

    @Transactional
    public void delete(BookDto bookDto) {
        Optional<Book> book = bookRepo.findById(bookDto.getId());
        book.ifPresent(bookRepo::delete);
    }
}
