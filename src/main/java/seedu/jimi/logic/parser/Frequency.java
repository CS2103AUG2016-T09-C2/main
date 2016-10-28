package seedu.jimi.logic.parser;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import seedu.jimi.commons.exceptions.IllegalValueException;

public class Frequency {
    private static final String MESSAGE_INVALID_FREQUENCY_NAME = "Invalid frequency name!";
    private static final HashSet<String> minuteEquivalents = new HashSet<>(Arrays.asList(
            "minute", "minutes", "min", "mins", "m"
    ));
    private static final HashSet<String> hourEquivalents = new HashSet<>(Arrays.asList(
            "hour", "hours", "hr", "hrs", "h"
    ));
    private static final HashSet<String> dayEquivalents = new HashSet<>(Arrays.asList(
            "day", "days", "d"
    ));
    private static final HashSet<String> mondayEquilavents = new HashSet<>(Arrays.asList(
            "monday", "mon"
    ));
    private static final HashSet<String> tuesdayEquivalent = new HashSet<>(Arrays.asList(
            "tuesday", "tu", "tue", "tues"
    ));
    private static final HashSet<String> wednesdayEquivalent = new HashSet<>(Arrays.asList(
            "wednesday", "wed"
    ));
    private static final HashSet<String> thursdayEquivalent = new HashSet<>(Arrays.asList(
            "thursday", "thu", "thur", "thurs" 
    ));
    private static final HashSet<String> fridayEquivalent = new HashSet<>(Arrays.asList(
            "friday", "fri" 
    ));
    private static final HashSet<String> saturdayEquivalent = new HashSet<>(Arrays.asList(
            "saturday", "sat"
    ));
    private static final HashSet<String> sundayEquivalent = new HashSet<>(Arrays.asList(
            "sunday", "sun"
    ));
    private static final HashSet<String> weekendEquivalents = new HashSet<>(Arrays.asList(
            "weekend", "weekends"
    ));
    private static final HashSet<String> dayOfWeekEquivalents = new HashSet<>(Arrays.asList(
            "monday", "mon", "tuesday", "tu", "tue", "tues", "wednesday", "wed", 
            "thursday", "thu", "thur", "thurs", "friday", "fri",
            "saturday", "sat", "sunday", "sun", "weekend", "weekends"
    ));
    private static final HashSet<String> weekEquivalents = new HashSet<>(Arrays.asList(
            "week", "weeks", "w", "monday", "mon", "tuesday", "tu", "tue", "tues", 
            "wednesday", "wed", "thursday", "thu", "thur", "thurs", "friday", "fri",
            "saturday", "sat", "sunday", "sun", "weekend", "weekends"
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
    
    public static boolean isDayOfWeek(final String str) {
        return dayOfWeekEquivalents.contains(str);
    }
    
    public static String getDayOfWeekFull(final String str) throws IllegalValueException {
        String lowerStr = str.trim().toLowerCase();
        if(mondayEquilavents.contains(lowerStr))        return "MONDAY";
        else if(tuesdayEquivalent.contains(lowerStr))   return "TUESDAY";
        else if(wednesdayEquivalent.contains(lowerStr)) return "WEDNESDAY";
        else if(thursdayEquivalent.contains(lowerStr))  return "THURSDAY";
        else if(fridayEquivalent.contains(lowerStr))    return "FRIDAY";
        else if(saturdayEquivalent.contains(lowerStr))  return "SATURDAY";
        else if(sundayEquivalent.contains(lowerStr))    return "SUNDAY";
        else if(weekendEquivalents.contains(lowerStr))  return "SATURDAY";
        else throw new IllegalValueException("Wrong week of day input!");
    }
    
    public static boolean isWeek(final String str) {
        return weekEquivalents.contains(str.toLowerCase());
    }
    
    public static boolean isMonth(final String str) {
        return monthEquivalents.contains(str.toLowerCase());
    }
    
    public static boolean isYear(final String str) {
        return yearEquivalents.contains(str.toLowerCase());
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
        else throw new IllegalValueException(MESSAGE_INVALID_FREQUENCY_NAME);
        
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

}
