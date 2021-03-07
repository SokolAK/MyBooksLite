package pl.sokolak.MyBooksLite.model.series;

import lombok.Getter;
import lombok.Setter;
import pl.sokolak.MyBooksLite.model.AbstractEntity;
import pl.sokolak.MyBooksLite.model.book.Book;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Series extends AbstractEntity {
    @Column(columnDefinition = "varchar(127) default ''")
    private String name = "";
    @OneToMany(mappedBy = "series", fetch = FetchType.EAGER)
    private Set<Book> books = new HashSet<>();
}