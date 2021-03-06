package pl.sokolak.MyBooksLite.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.data.domain.Pageable;
import pl.sokolak.MyBooksLite.model.Dto;
import pl.sokolak.MyBooksLite.model.book.BookDto;
import pl.sokolak.MyBooksLite.model.book.BookService;
import pl.sokolak.MyBooksLite.security.Secured;
import pl.sokolak.MyBooksLite.ui.ExpandingTextField;
import pl.sokolak.MyBooksLite.ui.MainLayout;
import pl.sokolak.MyBooksLite.ui.form.BookDetails;
import pl.sokolak.MyBooksLite.ui.form.DialogWindow;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static pl.sokolak.MyBooksLite.security.OperationType.ADD;
import static pl.sokolak.MyBooksLite.utils.TextFormatter.header;


@Route(value = "", layout = MainLayout.class)
@PageTitle("Books | MyBooksLite")
public class BooksListView extends VerticalLayout {
    private final BookService bookService;
    private final Grid<BookDto> grid = new Grid<>(BookDto.class);
    private final ExpandingTextField txtFilter = new ExpandingTextField();
    private final MenuBar mnuColumns = new MenuBar();
    private final TextField txtPage = new TextField();
    private final ComboBox<Integer> comboBox = new ComboBox<>();
    private final Map<String, Boolean> columnList = new LinkedHashMap<>();
    private final Button btnLeft = new Button();
    private final Button btnRight = new Button();
    private Button btnShortNotation;
    private Button btnAddBook;
    private boolean shortNotation = false;
    private VerticalLayout toolbar;
    private final ListViewUI listViewUI = new ListViewUI();
    private NavigationController nc;


    public BooksListView(BookService bookService) {
        this.bookService = bookService;
        addClassName("list-view");
        setSizeFull();

        //initFullColumnList();
        initShortColumnList();
        columnList.put("id",true);
        //configureChkShortNotation();
        configureGrid();

        configureUI();
    }

    private void configureUI() {
        nc = new NavigationController(this::getBooks, bookService::count, this::updateList, txtPage);
        nc.configureTxtPage(txtPage);
        listViewUI.configure(this::onInit, this::onReload);
    }

    private void onInit() {
        configureToolbar();
        nc.update();
        add(toolbar, grid);
    }

    private void onReload() {
        reloadToolbar();
    }

    private List<BookDto> getBooks(Pageable pageable) {
        return bookService.findAll(txtFilter.getValue(), columnList, pageable);
    }

    private void updateList(List<? extends Dto> books) {
        grid.setItems((List<BookDto>) books);
        updateGrid();
    }

    private Icon setBtnShortNotationIcon(boolean shortNotation) {
        if (shortNotation)
            return new Icon(VaadinIcon.EXPAND);
        else
            return new Icon(VaadinIcon.COMPRESS);
    }

    private void configureGrid() {
        grid.addClassName("book-grid");
        grid.setSizeFull();
        grid.setColumns();

        updateGrid();

        grid.addItemDoubleClickListener(e -> {
            grid.select(e.getItem());
            openBookDetails(e.getItem());
        });
    }

    //@Secured(EDIT)
    private void openBookDetails(BookDto bookDto) {
        BookDetails bookDetails = new BookDetails(BookDto.copy(bookDto));
        //authorForm.setAuthor(e);
        //authorForm.addListener(AuthorForm.DeleteEvent.class, this::deleteAuthor);
        //authorForm.addListener(AuthorForm.SaveEvent.class, this::saveAuthor);
        bookDetails.addListener(BookDetails.SaveEvent.class, this::saveBook);
        bookDetails.addListener(BookDetails.DeleteEvent.class, this::deleteBook);
        new DialogWindow(bookDetails, header("bookDetails")).open();
    }

    private void saveBook(BookDetails.SaveEvent e) {
        bookService.save(e.getDto(BookDto.class));
        nc.update();
    }

    private void deleteBook(BookDetails.DeleteEvent e) {
        bookService.delete(e.getDto(BookDto.class));
        nc.update();
    }

    private void configureToolbar() {
        toolbar = new VerticalLayout();
        btnAddBook = new Button(new Icon(VaadinIcon.PLUS), click -> addBook());
        configureTxtFilter();
        configureBtnShortNotation();
        configureMnuColumns();
        //configureBtnPages();
        nc.configureBtnPages(btnLeft, btnRight);
        //configureCmbPage();
        nc.configureCmbPage(comboBox);
        reloadToolbar();
    }

    private void reloadToolbar() {
        ToolbarUtils.reloadToolbar(toolbar, listViewUI, btnAddBook, btnLeft, btnRight, txtFilter, txtPage, comboBox, mnuColumns, btnShortNotation);
    }

    private void configureBtnExport() {
        //Anchor export = new Anchor(new StreamResource("filename.txt", () -> new DataExporter(bookService).exportFile()), "");
        //export.getElement().setAttribute("download", true);
        //export.add(new Button(new Icon(VaadinIcon.DOWNLOAD_ALT)));
    }

    private void configureBtnShortNotation() {
        btnShortNotation = new Button(setBtnShortNotationIcon(shortNotation), e -> {
            shortNotation = !shortNotation;
            if (shortNotation)
                initShortColumnList();
            else
                initFullColumnList();
            updateMnuColumns();
            updateGrid();
            e.getSource().setIcon(setBtnShortNotationIcon(shortNotation));
        });
    }

    private void configureTxtFilter() {
        txtFilter.setPlaceholder(header("filterByPhrase"));
        txtFilter.addValueChangeListener(e -> {
            nc.getPage().no = 0;
            nc.update();
        });
    }

    @Secured(ADD)
    private void addBook() {
        grid.asSingleSelect().clear();
        openBookDetails(new BookDto());
    }

    private Grid.Column addGridColumn(String key) {
        Grid.Column<BookDto> column;
        column = grid.addColumn(key);
        column.setHeader(header(key));
        return column;
    }

    private void addEntityColumns() {
        columnList.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(e -> addGridColumn(e.getKey()))
                .forEach(c -> c.setAutoWidth(true));
    }

    private void updateGrid() {
        grid.setColumns();
        addEntityColumns();
    }

    private void initShortColumnList() {
        columnList.put("id", false);
        columnList.put("title", true);
        columnList.put("subtitle", false);
        columnList.put("author", true);
        columnList.put("publisher", true);
        columnList.put("year", true);
        columnList.put("city", false);
        columnList.put("edition", true);
        columnList.put("volume", true);
        columnList.put("series", false);
        columnList.put("seriesVolume", false);
        columnList.put("comment", false);
    }

    private void initFullColumnList() {
        columnList.put("id", false);
        columnList.put("title", true);
        columnList.put("subtitle", true);
        columnList.put("author", true);
        columnList.put("publisher", true);
        columnList.put("year", true);
        columnList.put("city", true);
        columnList.put("edition", true);
        columnList.put("volume", true);
        columnList.put("series", true);
        columnList.put("seriesVolume", true);
        columnList.put("comment", true);
    }

    private void configureMnuColumns() {
        MenuItem columns = mnuColumns.addItem(new Icon(VaadinIcon.TABLE));
        columnList.forEach((columnName, isChecked) -> {
                    MenuItem item = columns
                            .getSubMenu()
                            .addItem(header(columnName));
                    item.setId(columnName);
                    item.setCheckable(true);
                    item.setChecked(isChecked);
                    item.addClickListener(e -> {
                                boolean currentValue = columnList.get(columnName);
                                columnList.put(columnName, !currentValue);
                                updateGrid();
                                nc.update();
                            }
                    );
                }
        );
        mnuColumns.addThemeVariants(MenuBarVariant.LUMO_ICON);
    }

    private void updateMnuColumns() {
        MenuItem columns = mnuColumns.getItems().get(0);
        columns.getSubMenu().getItems().forEach(it -> it.setChecked(columnList.get(it.getId().get())));
    }
}