package seedu.jimi.logic.commands;

import seedu.jimi.model.History;

public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";
    public static final String SHORT_COMMAND_WORD = "u";
    public static final String MESSAGE_USAGE = COMMAND_WORD 
            + ": Undoes the previous task.\n" + "To undo a task, type undo\n"
            + "> Tip: Typing 'u' instead of 'undo' works too.\n";
    public UndoCommand() { };

    @Override
    public CommandResult execute() {
        return History.getInstance().undo();
    }

    @Override
    public boolean isValidCommandWord(String commandWord) {
        String lowerStr = commandWord.toLowerCase();
        if(lowerStr == COMMAND_WORD.toLowerCase() 
                || lowerStr == SHORT_COMMAND_WORD.toLowerCase()) {
            return true;
        }
        
        return false;
    }

}
