package pl.sokolak.MyBooksLite.model.book;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepo extends JpaRepository<Book,Long>, BookFilter {
}
