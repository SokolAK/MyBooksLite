package pl.sokolak.MyBooksLite.model.book;

import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    public BookDto toDto(Book book) {
        BookDto bookDto = new BookDto();

        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setSubtitle(book.getSubtitle());
        bookDto.setYear(book.getYear());
        bookDto.setCity(book.getCity());
        bookDto.setVolume(book.getVolume());
        bookDto.setEdition(book.getEdition());
        bookDto.setSeriesVolume(book.getSeriesVolume());
        bookDto.setComment(book.getComment());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setPublisher(book.getPublisher());
        bookDto.setSeries(book.getSeries());

        return bookDto;
    }

    public Book toEntity(BookDto bookDto) {
        Book book = new Book();
        book.setId(bookDto.getId());
        book.setTitle(bookDto.getTitle());
        book.setSubtitle(bookDto.getSubtitle());
        book.setYear(bookDto.getYear());
        book.setCity(bookDto.getCity());
        book.setVolume(bookDto.getVolume());
        book.setEdition(bookDto.getEdition());
        book.setSeriesVolume(bookDto.getSeriesVolume());
        book.setComment(bookDto.getComment());
        book.setAuthor(bookDto.getAuthor());
        book.setPublisher(bookDto.getPublisher());
        book.setSeries(bookDto.getSeries());

        return book;
    }
}
