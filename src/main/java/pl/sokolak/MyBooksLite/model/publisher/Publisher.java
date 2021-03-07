package pl.sokolak.MyBooksLite.model.publisher;

import lombok.Getter;
import lombok.Setter;
import pl.sokolak.MyBooksLite.model.AbstractEntity;
import pl.sokolak.MyBooksLite.model.book.Book;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Publisher extends AbstractEntity {
    @Column(columnDefinition = "varchar(255) default ''")
    private String name = "";
    @ManyToMany(mappedBy = "publishers", fetch = FetchType.EAGER)
    private Set<Book> books = new HashSet<>();
}
