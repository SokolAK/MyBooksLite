package pl.sokolak.MyBooksLite.model.book;

import lombok.Getter;
import lombok.Setter;
import pl.sokolak.MyBooksLite.model.Dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BookDto implements Dto {
    private Long id;
    private String author = "";
    @NotNull
    @NotEmpty
    private String title = "";
    private String subtitle = "";
    private String city = "";
    private String year = "";
    private String volume = "";
    private String edition = "";
    private String publisher = "";
    private String series = "";
    private String seriesVolume = "";
    private String comment = "";

    public static BookDto copy(BookDto original) {
        BookDto copy = new BookDto();
        copy.setId(original.getId());
        copy.setAuthor(original.getAuthor());
        copy.setTitle(original.getTitle());
        copy.setSubtitle(original.getSubtitle());
        copy.setYear(original.getYear());
        copy.setCity(original.getCity());
        copy.setVolume(original.getVolume());
        copy.setEdition(original.getEdition());
        copy.setPublisher(original.getPublisher());
        copy.setSeries(original.getSeries());
        copy.setSeriesVolume(original.getSeriesVolume());
        copy.setComment(original.getComment());
        return copy;
    }

    @Override
    public String toString() {
        List<String> items = new ArrayList<>();
        items.add(id.toString());
        items.add(title);
        items.add(subtitle);
        return String.join(" # ", items);
    }
}