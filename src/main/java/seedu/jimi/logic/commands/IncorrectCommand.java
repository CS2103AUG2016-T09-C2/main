package seedu.jimi.logic.commands;

/**
 * Represents an incorrect command. Upon execution, produces some feedback to the user.
 */
public class IncorrectCommand extends Command {
    
    public final String feedbackToUser;
    
    public IncorrectCommand(String feedbackToUser) {
        this.feedbackToUser = feedbackToUser;
    }
    
    @Override
    public CommandResult execute() {
        indicateAttemptToExecuteIncorrectCommand();
        return new CommandResult(feedbackToUser);
    }
    
    @Override
    public boolean isValidCommandWord(String commandWord) {
        return false;
    }
    
    @Override
    public String getMessageUsage() {
        return "";
    }
    
    @Override
    public String getCommandWord() {
        return "";
    }
}
