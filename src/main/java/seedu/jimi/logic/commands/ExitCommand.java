package seedu.jimi.logic.commands;

import seedu.jimi.commons.core.EventsCenter;
import seedu.jimi.commons.events.ui.ExitAppRequestEvent;

/**
 * Terminates the program.
 */
public class ExitCommand extends Command {

    public static final String COMMAND_WORD = "exit";

    public static final String MESSAGE_EXIT_ACKNOWLEDGEMENT = "Exiting Jimi as requested ...";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Closes Jimi.";
    
    public ExitCommand() {}

    @Override
    public CommandResult execute() {
        EventsCenter.getInstance().post(new ExitAppRequestEvent());
        return new CommandResult(MESSAGE_EXIT_ACKNOWLEDGEMENT);
    }

    // @@author A0140133B
    @Override
    public boolean isValidCommandWord(String commandWord) {
        return commandWord.toLowerCase().equals(COMMAND_WORD);
    }
    // @@author
    
    @Override
    public String getMessageUsage() {
        return MESSAGE_USAGE;
    }
}
