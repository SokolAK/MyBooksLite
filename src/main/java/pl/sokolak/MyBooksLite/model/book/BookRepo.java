package pl.sokolak.MyBooksLite.model.book;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepo extends JpaRepository<Book,Long>, BookFilter {
    List<Book> findAllByOrderById();
    List<Book> findAllByOrderById(Pageable pageable);
}
