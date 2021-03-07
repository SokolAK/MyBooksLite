package pl.sokolak.MyBooksLite.model.book;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(columnDefinition = "varchar(255) default ''")
    private String author = "";
    @Column(columnDefinition = "varchar(255) default ''")
    private String title = "";
    @Column(columnDefinition = "varchar(511) default ''")
    private String subtitle = "";
    @Column(columnDefinition = "varchar(255) default ''")
    private String city = "";
    @Column(columnDefinition = "varchar(31) default ''")
    private String year = "";
    @Column(columnDefinition = "varchar(31) default ''")
    private String volume = "";
    @Column(columnDefinition = "varchar(127) default ''")
    private String edition = "";
    @Column(columnDefinition = "varchar(255) default ''")
    private String publisher = "";
    @Column(columnDefinition = "varchar(255) default ''")
    private String series = "";
    @Column(columnDefinition = "varchar(255) default ''", name = "series_volume")
    private String seriesVolume = "";
    @Column(columnDefinition = "varchar(511) default ''")
    private String comment = "";
}