package easv.event.gui.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateFormatter {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d. MMMM");

    public static String format(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public static String createDateTimeString(LocalDate date, String time) {
        return format(date) + " " + time;
    }

    public static SimpleStringProperty createDateStringProperty(LocalDate date) {
        return new SimpleStringProperty(format(date));
    }

    public static SimpleStringProperty createDateTimeStringProperty(LocalDate date, String time) {
        return new SimpleStringProperty(format(date) + " " + time);
    }


    public static StringConverter<LocalDate> createLocalDateConverter(DateTimeFormatter formatter) {
        return new StringConverter<>() {
            @Override
            public String toString(LocalDate localDate) {
                return (localDate == null) ? "" : formatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.isBlank()) {
                    return null;
                }
                try {
                    return LocalDate.parse(string, formatter);
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        };
    }

}
