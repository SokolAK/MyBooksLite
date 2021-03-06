package pl.sokolak.MyBooksLite.utils;

import com.vaadin.flow.component.Text;

import static pl.sokolak.MyBooksLite.utils.StringUtils.lowerAndCapitalizeFirst;


public class TextFormatter {

    private static final Text component = new Text("");

    public static String header(String text) {
        return lowerAndCapitalizeFirst(component.getTranslation(text));
    }

    public static String label(String text) {
        return component.getTranslation(text);
    }

    public static String uppercase(String text) {
        return component.getTranslation(text).toUpperCase();
    }
}

