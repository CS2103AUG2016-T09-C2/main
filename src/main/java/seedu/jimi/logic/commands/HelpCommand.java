package seedu.jimi.logic.commands;

import java.util.List;
import java.util.Optional;

import seedu.jimi.commons.core.EventsCenter;
import seedu.jimi.commons.core.Messages;
import seedu.jimi.commons.events.ui.ShowHelpRequestEvent;
import seedu.jimi.commons.exceptions.IllegalValueException;
import seedu.jimi.commons.util.CommandUtil;

// @@author A0140133B
/**
 * Format full help instructions for every command for display.
 */
public class HelpCommand extends Command {
    
    public static final String COMMAND_WORD = "help";
    
    public static final String MESSAGE_USAGE = 
            COMMAND_WORD + ": Shows program usage instructions.\n"
            + "You can also get specific help for commands.\n"
            + "For example, getting help for add command: " + COMMAND_WORD + " add \n"
            + Messages.MESSAGE_ALL_AVAIL_CMD;

    public static final String SHOWING_HELP_MESSAGE = 
            "Opened help window.\n" 
            + "\n"
            + MESSAGE_USAGE;
    
    private Command toHelp;
    
    public HelpCommand() {}
    
    public HelpCommand(String cmdToShow) throws IllegalValueException {
        List<Command> cmdStubList = CommandUtil.getInstance().getCommandStubList();
        
        // Tries to find a match with all command words.
        Optional<Command> match = cmdStubList.stream()
                .filter(c -> c.isValidCommandWord(cmdToShow))
                .findFirst();
        
        if (match.isPresent()) {
            toHelp = match.get();
        } else { // User specified an unknown command.
            throw new IllegalValueException(String.format(Messages.MESSAGE_UNKNOWN_COMMAND, cmdToShow));
        }
    }
    
    @Override
    public CommandResult execute() {
        if (toHelp == null) {
            EventsCenter.getInstance().post(new ShowHelpRequestEvent());
            return new CommandResult(SHOWING_HELP_MESSAGE);
        } else { // User specified a command for help.
            return new CommandResult(toHelp.getMessageUsage());
        }
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
