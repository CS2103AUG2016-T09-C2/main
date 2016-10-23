package seedu.jimi.logic.commands;

import seedu.jimi.model.History;

public class RedoCommand extends Command{

    public static final String COMMAND_WORD = "redo";
    public static final String SHORT_COMMAND_WORD = "r";
    public static final String MESSAGE_USAGE = COMMAND_WORD 
            + ": Redoes the previous task.\n" + "To redo a task, type redo\n"
            + "> Tip: Typing 'r' instead of 'redo' works too.\n";

    public RedoCommand() { }

    @Override
    public CommandResult execute() {
        return History.getInstance().redo();
    }

    @Override
    public boolean isValidCommandWord(String commandWord) {
        String lowerStr = commandWord.toLowerCase();
        if(lowerStr == COMMAND_WORD.toLowerCase() 
                || lowerStr == SHORT_COMMAND_WORD.toLowerCase()) {
            return true;
        }
        
        return false;
    };

}
