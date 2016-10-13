package seedu.jimi.logic.commands;


/**
 * Lists all tasks in Jimi to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Listed all tasks";

    public ListCommand() {}

    @Override
    public CommandResult execute() {
        model.updateFilteredListToShowAll();
        return new CommandResult(MESSAGE_SUCCESS);
    }
    
    @Override
    public boolean isCommandWord(String commandWord) {
        for (int i = 1; i <= COMMAND_WORD.length(); i++) {
            if (commandWord.equals(COMMAND_WORD.substring(0, i))) {
                return true;
            }
        }
        return false;
    }
}
