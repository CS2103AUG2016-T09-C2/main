package seedu.jimi.logic.parser;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import seedu.jimi.commons.exceptions.IllegalValueException;

public class Frequency {
    private static final String MESSAGE_INVALID_FREQUENCY = "Invalid frequency!";
    private static final HashSet<String> minuteEquivalents = new HashSet<>(Arrays.asList(
            "minute", "minutes", "min", "mins", "m"
    ));
    private static final HashSet<String> hourEquivalents = new HashSet<>(Arrays.asList(
            "hour", "hours", "hr", "hrs", "h"
    ));
    private static final HashSet<String> dayEquivalents = new HashSet<>(Arrays.asList(
            "day", "days", "d"
    ));
    private static final HashSet<String> weekEquivalents = new HashSet<>(Arrays.asList(
            "week", "weeks", "w", "monday", "mon", "tuesday", "tu", "tue", "tues", 
            "wednesday", "wed", "thursday", "thu", "thur", "thurs", "friday", "fri",
            "saturday", "sat", "sunday", "sun"
    ));
    private static final HashSet<String> monthEquivalents = new HashSet<>(Arrays.asList(
            "month", "months", "january", "jan", "feburuary", "feb", "march", "mar", "april", "apr",
            "may", "june", "jun", "july", "jul", "august", "aug", "september", "sep", "october", "oct",
            "november", "nov", "december", "dec"
    ));
    private static final HashSet<String> yearEquivalents = new HashSet<>(Arrays.asList(
            "year", "years", "y"
    ));
    
    public static boolean isMinute(final String str) {
        return minuteEquivalents.contains(str);
    }
    
    public static boolean isHour(final String str) {
        return hourEquivalents.contains(str);
    }
    
    public static boolean isDay(final String str) {
        return dayEquivalents.contains(str);
    }
    
    public static boolean isWeek(final String str) {
        return weekEquivalents.contains(str);
    }
    
    public static boolean isMonth(final String str) {
        return monthEquivalents.contains(str);
    }
    
    public static boolean isYear(final String str) {
        return yearEquivalents.contains(str);
    }
    
    public static Date getNextDate(final Date date, final int quantifier, final String freq) 
            throws IllegalValueException {
        LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        if(isMinute(freq))     ldt = ldt.plusMinutes(quantifier);
        else if(isHour(freq))  ldt = ldt.plusHours(quantifier);
        else if(isDay(freq))   ldt = ldt.plusDays(quantifier);
        else if(isWeek(freq))  ldt = ldt.plusWeeks(quantifier);
        else if(isMonth(freq)) ldt = ldt.plusMonths(quantifier);
        else if(isYear(freq))  ldt = ldt.plusYears(quantifier);
        else throw new IllegalValueException(MESSAGE_INVALID_FREQUENCY);
        
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

}
