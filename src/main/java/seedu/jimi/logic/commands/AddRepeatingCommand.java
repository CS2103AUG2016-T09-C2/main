package seedu.jimi.logic.commands;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.jimi.commons.exceptions.IllegalValueException;
import seedu.jimi.logic.parser.Frequency;
import seedu.jimi.model.tag.Tag;

public class AddRepeatingCommand extends Command implements TaskBookEditor {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = 
            COMMAND_WORD + ": Adds a task or event to Jimi with one optional tag.\n"
            + "\n"
            + "To add a task:\n"
            + "Parameters: \"TASK_DETAILS\" [due DATE_TIME] [t/TAG]\n"
            + "Example: " + COMMAND_WORD + " \"do dishes\" t/important\n"
            + "\n"
            + "To add an event:\n"
            + "Parameters: \"TASK_DETAILS\" on|from START_DATE_TIME [to END_DATE_TIME] [t/TAG]\n"
            + "Example: " + COMMAND_WORD + " \"linkin park concert\" on sunday 2pm t/fun\n"
            + "\n"
            + "> Tip: Typing 'a' or 'ad' instead of 'add' works too.\n";

    public static final String MESSAGE_SUCCESS = "New task added: %1$s";
    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists in Jimi";
    public static final String MESSAGE_WRONG_FREQUENCY = "Wrong frequency!\n" + MESSAGE_USAGE;
    
    private final List<AddCommand> addCommands;

    
    public AddRepeatingCommand(String name, List<Date> dates, int freqQuantifier, String freqWord, int numberOfTimes,
            Set<String> tags, String priority) 
                    throws IllegalValueException {
        addCommands = new ArrayList<>();
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(new Tag(tagName));
        }
        Date first = getFirstOccurrence(dates, freqWord);
        for(int i = 0; i < numberOfTimes; i ++ ) {
            List<Date> newDates = 
                    getDateListFromDate(Frequency.getNextDate(first, i * freqQuantifier, freqWord));
            if (dates.isEmpty()) { // originally a floating
                addCommands.add(new AddCommand(name, newDates, new ArrayList<Date>(), tags, priority));
            } else { // originally a deadline
                addCommands.add(new AddCommand(name, newDates, tags, priority)); 
            }
        }
    }
    
    public AddRepeatingCommand(String name, List<Date> startDates, List<Date> endDates,
            int freqQuantifier, String freqWord, int numberOfTimes, Set<String> tags, String priority) 
                    throws IllegalValueException {
        addCommands = new ArrayList<>();
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(new Tag(tagName));
        } 
        Date firstStart = getFirstOccurrence(startDates, freqWord);
        Date firstEnd;
        if(endDates.isEmpty()) {
            firstEnd = null;
        } else {
            firstEnd = getFirstOccurrence(endDates, freqWord);
            if(firstStart.compareTo(firstEnd) > 0) {
                LocalDateTime ldt = 
                    LocalDateTime.ofInstant(firstEnd.toInstant(), ZoneId.systemDefault()).plusWeeks(1);
                firstEnd = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
            }
        }
        for(int i = 0; i < numberOfTimes; i ++ ) {
            if(firstEnd == null) {
                List<Date> newStarts = 
                        getDateListFromDate(Frequency.getNextDate(firstStart, i * freqQuantifier, freqWord));
                addCommands.add(new AddCommand(name, newStarts, new ArrayList<Date>(), tags, priority));
            } else {
                List<Date> newStarts =
                        getDateListFromDate(Frequency.getNextDate(firstStart, i * freqQuantifier, freqWord));
                List<Date> newEnds = 
                        getDateListFromDate(Frequency.getNextDate(firstEnd, i * freqQuantifier, freqWord));
                addCommands.add(new AddCommand(name, newStarts, newEnds, tags, priority));
                
            }
        }
    }
    
 

    private Date getFirstOccurrence(final List<Date> dates, final String freqWord) 
            throws IllegalValueException {               
        for(Date dt : dates) System.out.println(dt.toString() + "\n");
        LocalDateTime first;
        if(Frequency.isDayOfWeek(freqWord)) {
            DayOfWeek dow = DayOfWeek.valueOf(Frequency.getDayOfWeekFull(freqWord));
            LocalDateTime ldt;
            if(dates.isEmpty()) {
                ldt = LocalDateTime.now();
            } else {
                ldt = LocalDateTime.ofInstant(dates.get(0).toInstant(), ZoneId.systemDefault());
            }
            if(ldt.getDayOfWeek().compareTo(dow) <= 0) {
                first = ldt.with(dow);
            } else {
                first = ldt.plusWeeks(1).with(dow);
            }
            return Date.from(first.atZone(ZoneId.systemDefault()).toInstant());
        } else {
            if(dates.isEmpty())
                return new Date();
            else
                return dates.get(0);
        }
    }
    
    private List<Date> getDateListFromDate(final Date date) {
        ArrayList<Date> dates = new ArrayList<>();
        dates.add(date);
        return dates;
    }
    
    @Override
    public CommandResult execute() {
        StringBuilder result = new StringBuilder("The following task(s) added: \n");
        for(AddCommand ac : addCommands) {
            ac.setData(model);
            result.append(ac.execute().feedbackToUser + "\n");
        }
        return new CommandResult(result.toString());
    }

    @Override
    public boolean isValidCommandWord(String commandWord) {
        return new AddCommand().isValidCommandWord(commandWord);
    }

}
