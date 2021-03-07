package pl.sokolak.MyBooksLite.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.data.domain.Pageable;
import pl.sokolak.MyBooksLite.model.Dto;
import pl.sokolak.MyBooksLite.model.publisher.PublisherService;
import pl.sokolak.MyBooksLite.security.Secured;
import pl.sokolak.MyBooksLite.ui.ExpandingTextField;
import pl.sokolak.MyBooksLite.ui.MainLayout;
import pl.sokolak.MyBooksLite.ui.form.AuthorForm;
import pl.sokolak.MyBooksLite.ui.form.DialogWindow;
import pl.sokolak.MyBooksLite.ui.form.Form;
import pl.sokolak.MyBooksLite.ui.form.PublisherForm;

import java.util.List;
import java.util.stream.Stream;

import static pl.sokolak.MyBooksLite.security.OperationType.ADD;
import static pl.sokolak.MyBooksLite.security.OperationType.EDIT;
import static pl.sokolak.MyBooksLite.utils.TextFormatter.header;

@Route(value = "publishers", layout = MainLayout.class)
@PageTitle("Publishers | MyBooks")
public class PublishersListView extends VerticalLayout {

    private final PublisherService publisherService;
    private final Grid<PublisherDto> grid = new Grid<>(PublisherDto.class);
    private final ExpandingTextField txtFilter = new ExpandingTextField();
    private VerticalLayout toolbar;
    private Button btnAddPublisher;
    private final ComboBox<Integer> comboBox = new ComboBox<>();
    private Button btnLeft = new Button();
    private Button btnRight = new Button();
    private final TextField txtPage = new TextField();
    private final ListViewUI listViewUI = new ListViewUI();
    private NavigationController nc;

    public PublishersListView(PublisherService publisherService) {
        this.publisherService = publisherService;
        addClassName("author-view");
        setSizeFull();

        configureGrid();

        configureUI();
    }

    private void configureUI() {
        nc = new NavigationController(this::getPublishers, publisherService::count, this::updateList, txtPage);
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

    private List<PublisherDto> getPublishers(Pageable pageable) {
        return publisherService.findAll(txtFilter.getValue(), pageable);
    }

    private void updateList(List<? extends Dto> publishers) {
        grid.setItems((List<PublisherDto>) publishers);
        updateGrid();
    }


    private void configureToolbar() {
        toolbar = new VerticalLayout();
        btnAddPublisher = new Button(new Icon(VaadinIcon.PLUS), click -> addPublisher());
        configureTxtFilter();
        //configureBtnPages();
        nc.configureBtnPages(btnLeft, btnRight);
        //configureCmbPage();
        nc.configureCmbPage(comboBox);
        reloadToolbar();
    }

    private void reloadToolbar() {
        ToolbarUtils.reloadToolbar(toolbar, listViewUI, btnAddPublisher, btnLeft, btnRight, txtFilter, txtPage, comboBox);
    }

    private void configureTxtFilter() {
        txtFilter.setPlaceholder(header("filterByPhrase"));
        txtFilter.addValueChangeListener(e -> {
            nc.getPage().no = 0;
            nc.update();
        });
    }

    private void configureGrid() {
        grid.addClassName("series-grid");
        grid.setSizeFull();
        grid.addItemDoubleClickListener(e -> {
            grid.select(e.getItem());
            editPublisher(e.getItem());
        });
        updateGrid();
    }

    private void addEntityColumns() {
        Stream.of("name")
                .map(grid::addColumn)
                .map(c -> c.setHeader(header(c.getKey())))
                .forEach(c -> c.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(publisherService.findAll(txtFilter.getValue()));
        updateGrid();
    }

    private void updateGrid() {
        grid.setColumns();
        addEntityColumns();
    }

    private void delete(AuthorForm.DeleteEvent e) {
        publisherService.delete(e.getDto(PublisherDto.class));
        updateList();
    }

    private void save(AuthorForm.SaveEvent e) {
        publisherService.save(e.getDto(PublisherDto.class));
        updateList();
    }

    @Secured(EDIT)
    private void editPublisher(PublisherDto publisherDto) {
        PublisherForm publisherForm = new PublisherForm(publisherDto, Form.FormMode.EDIT);
        publisherForm.setPublisher(publisherDto);
        publisherForm.addListener(PublisherForm.DeleteEvent.class, this::delete);
        publisherForm.addListener(PublisherForm.SaveEvent.class, this::save);
        new DialogWindow(publisherForm, header("editPublisher")).open();
    }

    @Secured(ADD)
    private void addPublisher() {
        grid.asSingleSelect().clear();
        PublisherForm publisherForm = new PublisherForm(Form.FormMode.ADD);
        publisherForm.setPublisher(new PublisherDto());
        publisherForm.addListener(AuthorForm.SaveEvent.class, this::save);
        new DialogWindow(publisherForm, header("addPublisher")).open();
    }
}