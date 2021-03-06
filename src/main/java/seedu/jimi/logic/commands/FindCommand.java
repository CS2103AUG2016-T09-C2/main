package seedu.jimi.logic.commands;

import java.util.Date;
import java.util.List;
import java.util.Set;

import seedu.jimi.model.datetime.DateTime;

/**
 * Finds and lists all tasks in Jimi whose name contains any of the argument keywords.
 * Keyword matching is case sensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    // That new string object infront of the second line denotes a repeated space character 
    // to align the messages according to the length of the command word.
    public static final String MESSAGE_USAGE = COMMAND_WORD 
            + ": Finds all tasks whose names nearly match any of the specified keywords (case-sensitive) and displays \n"
            + new String(new char[COMMAND_WORD.length() + 2]).replace("\0", " ") + "them as a list with index numbers.\n"
            + "> Shortcuts: f, fi, fin\n"
            + "Parameters: \"KEYWORD [MORE_KEYWORDS]...\"\n"
            + "Example: " + COMMAND_WORD + " \"add water\"\n"
            + "\n"
            + "To specify a date range for your search: \n"
            + "Parameters: [\"KEYWORD [MORE_KEYWORDS]...\"] on|from DATE_TIME [to DATE_TIME]\n"
            + "> Note the keywords are now optional.\n"
            + "Example: " + COMMAND_WORD + " \"meetings\" from 1 nov to 3 nov\n"
            + "Example: " + COMMAND_WORD + " on 9 nov";
            

    private final Set<String> keywords;
    private final DateTime fromDate;
    private final DateTime toDate;
    
    
    public FindCommand() {
        keywords = null;
        toDate = null;
        fromDate = null;
    }
    
    public FindCommand(Set<String> keywords, List<Date> fromDate, List<Date> toDate) {
        this.keywords = keywords;
        if(fromDate != null && fromDate.size() != 0) {
            this.fromDate = new DateTime(fromDate.get(0));
        } else {
            this.fromDate = null;
        }
            
        if(toDate != null && toDate.size() != 0) {
            this.toDate = new DateTime(toDate.get(0));
        } else {
            this.toDate = null;
        }
    }
    //@@author A0138915X

    @Override
    public CommandResult execute() {
        if (keywords != null && keywords.size() > 0) {
            if (fromDate != null) {
                model.updateFilteredAgendaTaskList(keywords, fromDate, toDate);
                model.updateFilteredAgendaEventList(keywords, fromDate, toDate);
            } else {
                model.updateFilteredAgendaTaskList(keywords);
                model.updateFilteredAgendaEventList(keywords);
            }
        } else {
            model.updateFilteredAgendaTaskList(fromDate, toDate);
            model.updateFilteredAgendaEventList(fromDate, toDate);
        }
        
        
        return new CommandResult(getMessageForTaskListShownSummary(model.getShownSize()));
    }
    
    @Override
    public boolean isValidCommandWord(String commandWord) {
        for (int i = 1; i <= COMMAND_WORD.length(); i++) {
            if (commandWord.equalsIgnoreCase(COMMAND_WORD.substring(0, i))) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String getMessageUsage() {
        return MESSAGE_USAGE;
    }
    
    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }
}
