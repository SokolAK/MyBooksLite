package pl.sokolak.MyBooksLite.model.author;

import lombok.Getter;
import lombok.Setter;
import pl.sokolak.MyBooksLite.model.book.Book;
import pl.sokolak.MyBooksLite.model.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Author extends AbstractEntity {
    @Column(columnDefinition = "varchar(63) default ''")
    private String prefix = "";
    @Column(columnDefinition = "varchar(63) default ''", name = "first_name")
    private String firstName = "";
    @Column(columnDefinition = "varchar(63) default ''", name = "middle_name")
    private String middleName = "";
    @NotNull
    @Column(columnDefinition = "varchar(63) default ''", name = "last_name")
    private String lastName = "";

    @ManyToMany(mappedBy = "authors", fetch = FetchType.EAGER)
    private Set<Book> books = new HashSet<>();
}
