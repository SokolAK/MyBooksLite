package pl.sokolak.MyBooksLite.ui.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import pl.sokolak.MyBooksLite.model.book.BookDto;
import pl.sokolak.MyBooksLite.security.Secured;

import java.util.Arrays;

import static pl.sokolak.MyBooksLite.security.OperationType.*;
import static pl.sokolak.MyBooksLite.utils.TextFormatter.header;
import static pl.sokolak.MyBooksLite.utils.TextFormatter.uppercase;

public class BookDetails extends Form {

    private BookDto bookDto;

    private final Binder<BookDto> binder = new BeanValidationBinder<>(BookDto.class);
    private final TextField title = new TextField();
    private final TextField subtitle = new TextField();
    private final TextField year = new TextField();
    private final TextField city = new TextField();
    private final TextField edition = new TextField();
    private final TextField seriesVolume = new TextField();
    private final TextField comment = new TextField();
    private final TextField volume = new TextField();
    private final TextField author = new TextField();
    private final TextField publisher = new TextField();
    private final TextField series = new TextField();

    private final DeleteButton btnDelete = new DeleteButton();


    public BookDetails(BookDto bookDto) {
        this.bookDto = BookDto.copy(bookDto);
        setClassName("book-details");

        binder.bindInstanceFields(this);
        title.setRequired(true);

        addListener(BookDetails.SaveEvent.class, e -> getParent().ifPresent(d -> ((Dialog)d).close()));
        addListener(BookDetails.DeleteEvent.class, e -> getParent().ifPresent(d -> ((Dialog)d).close()));

        updateDetails();
    }

    private void updateDetails() {
        removeAll();
        VerticalLayout verticalLayout = new VerticalLayout();

        configureTxtField(title, bookDto.getTitle());
        verticalLayout.add(createSection("title", getEditButton(title), title));

        configureTxtField(subtitle, bookDto.getSubtitle());
        verticalLayout.add(createSection("subtitle", getEditButton(subtitle), subtitle));

        configureTxtField(author, bookDto.getAuthor());
        verticalLayout.add(createSection("author", getEditButton(author), author));

        configureTxtField(year, bookDto.getYear());
        verticalLayout.add(createSection("year", getEditButton(year), year));

        configureTxtField(city, bookDto.getCity());
        verticalLayout.add(createSection("city", getEditButton(city), city));

        configureTxtField(volume, bookDto.getVolume());
        verticalLayout.add(createSection("volume", getEditButton(volume), volume));

        configureTxtField(edition, bookDto.getEdition());
        verticalLayout.add(createSection("edition", getEditButton(edition), edition));

        configureTxtField(publisher, bookDto.getPublisher());
        verticalLayout.add(createSection("publisher", getEditButton(publisher), publisher));

        configureTxtField(series, bookDto.getSeries());
        verticalLayout.add(createSection("series", getEditButton(series), series));

        configureTxtField(seriesVolume, bookDto.getSeriesVolume());
        verticalLayout.add(createSection("seriesVolume", getEditButton(seriesVolume), seriesVolume));

        configureTxtField(comment, bookDto.getComment());
        verticalLayout.add(createSection("comment", getEditButton(comment), comment));


        add(verticalLayout, createButtonsLayout());
    }

    private void configureTxtField(TextField textField, String value) {
        textField.setValue(value);
        textField.setReadOnly(true);
        textField.setWidthFull();
        textField.addBlurListener(e -> e.getSource().setReadOnly(true));
        textField.addValueChangeListener(e -> writeBook());
    }

    private Button getEditButton(TextField textField) {
        return new Button(new Icon(VaadinIcon.EDIT), click -> unlockTextField(textField));
    }

    @Secured(EDIT)
    private void unlockTextField(TextField textField) {
        textField.setReadOnly(false);
        textField.focus();
    }

    private VerticalLayout createSection(String title, Button btnEdit, Component... components) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setPadding(false);
        verticalLayout.setSpacing(false);

        Label lblTitle = new Label(uppercase(title));
        lblTitle.setClassName("section-header");
        btnEdit.setClassName("edit-button");
        btnEdit.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
        HorizontalLayout horizontalLayout = new HorizontalLayout(lblTitle, btnEdit);
        horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        horizontalLayout.setPadding(false);
        horizontalLayout.setSpacing(false);
        verticalLayout.add(horizontalLayout);

        if(components.length == 0) {
            TextField textField = new TextField();
            textField.setReadOnly(true);
            textField.setWidthFull();
            components = new Component[] {textField};
        }
        Arrays.stream(components).forEach(verticalLayout::add);

        return verticalLayout;
    }

    private HorizontalLayout createButtonsLayout() {
        HorizontalLayout buttonsLayout = new HorizontalLayout();

        Button btnSave = new Button(header("save"));
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSave.addClickShortcut(Key.ENTER);
        btnSave.addClickListener(e -> saveBook());
        buttonsLayout.addAndExpand(btnSave);

        btnDelete.addClickListener(e -> {
            if (((DeleteButton) e.getSource()).isReady())
                deleteBook();
        });
        buttonsLayout.addAndExpand(btnDelete);

        return buttonsLayout;
    }

    private boolean writeBook() {
        try {
            binder.writeBean(bookDto);
            return true;
        } catch (ValidationException e) {
            //e.printStackTrace();
            return false;
        }
    }

    @Secured(ADD)
    private void saveBook() {
        if(writeBook())
            fireEvent(new SaveEvent(this, bookDto));
    }

    @Secured(DELETE)
    private void deleteBook() {
        fireEvent(new DeleteEvent(this, bookDto));
    }
}
