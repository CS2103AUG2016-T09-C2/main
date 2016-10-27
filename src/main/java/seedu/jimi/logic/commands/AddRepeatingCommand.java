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
import seedu.jimi.logic.parser.StringToInt;
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
    
    public AddRepeatingCommand(String name, List<Date> dates, 
            Set<String> tags, String priority, String frequency, int times) throws IllegalValueException {
        assert frequency != null;
        addCommands = new ArrayList<>(times);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(new Tag(tagName));
        }
        
        String str = frequency.trim().toLowerCase();
        String[] freqStrs = str.split("[\\W || \\s]+");
        String freqWord;
        int freqQuantifier;
        if(freqStrs.length == 1) {
            freqQuantifier = 1;
            freqWord = freqStrs[0];
        }
        else if(freqStrs.length == 2) {
            freqQuantifier = StringToInt.parse(freqStrs[0]);
            freqWord = freqStrs[1];
        }
        else throw new IllegalValueException(MESSAGE_WRONG_FREQUENCY);
        Date first = getFirstOccurrence(dates, freqWord);
        for(int i = 0; i < times; i ++ ) {
            List<Date> newDates = 
                    getDateListFromDate(Frequency.getNextDate(first, i * freqQuantifier, freqWord));
            addCommands.add(new AddCommand(name, newDates, tags, priority));
        }
    }

    private Date getFirstOccurrence(final List<Date> dates, final String freqWord) 
            throws IllegalValueException {               
        LocalDateTime first;
        if(Frequency.isDayOfWeek(freqWord)) {
            if(dates.isEmpty())
               first = LocalDateTime.now().with(DayOfWeek.valueOf(Frequency.getDayOfWeekFull(freqWord)));
            else {
                LocalDateTime ldt = LocalDateTime.ofInstant(
                        dates.get(0).toInstant(), ZoneId.systemDefault());
                first = ldt.with(DayOfWeek.valueOf(Frequency.getDayOfWeekFull(freqWord)));
            }
        } else {
            if(dates.isEmpty())
                return new Date();
            else
                return dates.get(0);
        }
        return Date.from(first.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    private List<Date> getDateListFromDate(final Date date) {
        ArrayList<Date> dates = new ArrayList<>();
        dates.add(date);
        return dates;
    }
    
    @Override
    public CommandResult execute() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isValidCommandWord(String commandWord) {
        // TODO Auto-generated method stub
        return false;
    }

}
