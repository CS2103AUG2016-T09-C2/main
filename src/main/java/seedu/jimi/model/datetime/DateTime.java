package seedu.jimi.model.datetime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

import seedu.jimi.commons.core.Messages;
import seedu.jimi.commons.exceptions.IllegalValueException;

public class DateTime implements Comparable<DateTime> {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATETIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;
    
    private LocalDateTime dtInstance;
    
    public DateTime() {
        dtInstance = LocalDateTime.now();
    }
    
    public DateTime(String dateStr) throws IllegalValueException {
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
        try {
            dtInstance = LocalDateTime.parse(dateStr, dtFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalValueException(Messages.MESSAGE_INVALID_DATE);
        }
    }
    
    public DateTime(Date date) {
        dtInstance = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public String getDate() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return dtInstance.format(dateFormatter).toString();
    }
    
    public String getTime() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        return dtInstance.format(timeFormatter).toString();
    }
    
    private LocalDateTime getLocalDateTime() {
        return dtInstance;
    }
    
    public long getDifferenceInHours(DateTime other) {
        return ChronoUnit.HOURS.between(dtInstance, other.getLocalDateTime());
    }
    
    public long getDifferencetInDays(DateTime other) {
        return ChronoUnit.DAYS.between(dtInstance, other.getLocalDateTime());
    }
    
    public long getDifferenceInMonths(DateTime other) {
        return ChronoUnit.MONTHS.between(dtInstance, other.getLocalDateTime());
    }
    
    @Override
    public int compareTo(DateTime dt) {
        return dtInstance.compareTo(dt.getLocalDateTime());
    }
    
    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DateTime// instanceof handles nulls
                && this.dtInstance.equals(((DateTime) other).getLocalDateTime()));
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(dtInstance);
    }
    
    @Override
    public String toString() {
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dtInstance.format(dtFormatter);
    }
}
