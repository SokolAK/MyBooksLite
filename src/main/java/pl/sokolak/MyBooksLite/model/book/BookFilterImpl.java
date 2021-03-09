package pl.sokolak.MyBooksLite.model.book;

import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

import static pl.sokolak.MyBooksLite.utils.StringUtils.convertPhraseToSubPhrases;

public class BookFilterImpl implements BookFilter {

    @PersistenceContext
    private EntityManager em;



    @Override
    public List<Book> findAllContainingPhrase(String phrase, Map<String, Boolean> columnList, Pageable pageable) {
        return findAllContainingPhraseSQL(phrase, columnList, pageable);
    }

    //Awful but works - NEW
    @SuppressWarnings("unchecked")
    private List<Book> findAllContainingPhraseSQL(String phrase, Map<String, Boolean> columnList, Pageable pageable) {
        Set<String> subPhrases = convertPhraseToSubPhrases(phrase);
        StringJoiner columns = new StringJoiner(", ';' ,");

        if (columnList.getOrDefault("title", false)) {
            columns.add("LOWER(b.title)");
        }
        if (columnList.getOrDefault("subtitle", false)) {
            columns.add("LOWER(b.subtitle)");
        }
        if (columnList.getOrDefault("seriesVolume", false)) {
            columns.add("LOWER(b.series_volume)");
        }
        if (columnList.getOrDefault("city", false)) {
            columns.add("LOWER(b.city)");
        }
        if (columnList.getOrDefault("year", false)) {
            columns.add("b.year");
        }
        if (columnList.getOrDefault("volume", false)) {
            columns.add("b.volume");
        }
        if (columnList.getOrDefault("edition", false)) {
            columns.add("LOWER(b.edition)");
        }
        if (columnList.getOrDefault("comment", false)) {
            columns.add("LOWER(b.comment)");
        }
        if (columnList.getOrDefault("author", false)) {
            columns.add("LOWER(b.author)");
        }
        if (columnList.getOrDefault("publisher", false)) {
            columns.add("LOWER(b.publisher)");
        }
        if (columnList.getOrDefault("series", false)) {
            columns.add("LOWER(b.series)");
        }

        String row = "CONCAT(" + columns + ")";
        String conditions = subPhrases.stream()
                .map(s -> row + " LIKE '%" + s + "%'")
                .collect(Collectors.joining(" AND "));

        String subSql = "SELECT b.id FROM Book b " +
                "GROUP BY b.id " +
                "HAVING " +
                conditions;
        String sql = "SELECT * FROM Book b " +
                "WHERE b.id IN (" + subSql + ") " +
                "ORDER BY b.id " +
                "LIMIT " + pageable.getPageSize() +
                " OFFSET " + pageable.getPageSize() * pageable.getPageNumber();

        Query query = em.createNativeQuery(sql, Book.class);
        return new ArrayList<Book>(query.getResultList());
    }
}
