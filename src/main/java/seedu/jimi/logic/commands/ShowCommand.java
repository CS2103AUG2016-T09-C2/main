package seedu.jimi.logic.commands;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import seedu.jimi.model.FilteredListManager.ListId;
import seedu.jimi.model.ModelManager;
import seedu.jimi.model.datetime.DateTime;

/**
 * Shows certain sections of the task panel to the user.
 * @author zexuan
 *
 */
public class ShowCommand extends Command {

    public static final String COMMAND_WORD = "show";
    
    public static final List<String> VALID_KEYWORDS = Arrays.asList(
            "floating", 
            "incomplete", 
            "complete",
            "today", 
            "tomorrow", 
            "monday", 
            "tuesday", 
            "wednesday", 
            "thursday", 
            "friday", 
            "saturday", 
            "sunday" 
    );
    
    public static final String MESSAGE_USAGE = 
            COMMAND_WORD + ": Shows certain sections of the task panel in the agenda panel. \n"
            + "Parameters: NAME_OF_SECTION_TO_DISPLAY\n" 
            + "Example: " + COMMAND_WORD + " floating tasks\n"
            + "> Valid Keywords: Floating, Complete, Incompletes, Today, Tomorrow, {day of week displayed}";

    public static final String MESSAGE_SUCCESS = "Displayed tasks and events.";
    
    private final String userSelection; //section name from user input
    
    public ShowCommand() {
        this.userSelection = "";
    }
    
    public ShowCommand(String args) {
        this.userSelection = args.toLowerCase().trim();
    }
    
    /**
     * Updates the agenda lists with new relevant predicates to update lists show to user.
     */
    @Override
    public CommandResult execute() {
        ((ModelManager) model).showTaskPanelSection(userSelection);
        
        ListId sectionToShow = null;
        
        switch(userSelection) {
        case "floating":
            sectionToShow = ListId.FLOATING_TASKS;
            break;
        case "incomplete":
            sectionToShow = ListId.INCOMPLETE;
            break;
        case "complete":
            sectionToShow = ListId.COMPLETED;
            break;
        case "today":
            sectionToShow = ListId.DAY_AHEAD_0;
            break;
        case "tomorrow":
            sectionToShow = ListId.DAY_AHEAD_1;
            break;
        case "monday":
        case "tuesday": 
        case "wednesday":
        case "thursday":
        case "friday":
        case "saturday": 
        case "sunday":
            sectionToShow = findSectionToShow(userSelection);
            break;
        default:
                break;
        }
        
        model.updateFilteredAgendaTaskList(null, sectionToShow);
        model.updateFilteredAgendaEventList(null, sectionToShow);
        
        return new CommandResult(MESSAGE_SUCCESS);
    }

    private ListId findSectionToShow(String userSelection) {
        ListId sectionToShow = null;
        
        LocalDateTime dateTime = new DateTime().getLocalDateTime();
        
        for(int i=0; i<7; i++) {
            String dayOfWeek = dateTime.getDayOfWeek().plus(i).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            if(dayOfWeek.toLowerCase().contains(userSelection)) {
                sectionToShow = ListId.values()[i];
            }
        }
        
        return sectionToShow;
    }
    
    @Override
    public boolean isValidCommandWord(String commandWord) {
        for (int i = 1; i <= COMMAND_WORD.length(); i++) {
            if (commandWord.equals(COMMAND_WORD.substring(0, i))) {
                return true;
            }
        }
        return false;
    }

}
