/**
 * 
 */
package seedu.jimi.model;

import java.util.Stack;

import seedu.jimi.logic.commands.Command;
import seedu.jimi.logic.commands.CommandResult;
import seedu.jimi.logic.commands.RedoCommand;
import seedu.jimi.logic.commands.UndoCommand;

/**
 * History of the operations
 * This is a singleton class
 */
public final class History {

    private static History instance = null;
    private final Stack<Context> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();
    
    
    public CommandResult undo() {
        if(!undoStack.isEmpty()) {
            Context previous = undoStack.pop();
            previous.cmd.undo();
            if (!(previous.cmd instanceof RedoCommand)) {
                redoStack.push(previous.cmd);
            }
            return previous.result;
        } 
        return new CommandResult("Already earlist operation!");
    }
    
    public CommandResult redo() {
        if(!redoStack.isEmpty()) {
            Command cmd = redoStack.pop();
            CommandResult result = cmd.execute();
            if (!(cmd instanceof UndoCommand)) {
                undoStack.push(new Context(cmd, result));
            }
            return result;
        }
        return new CommandResult("Already most recent operation!");
    }
    
    public void execute(final Command cmd, final CommandResult result) {
        if (!(cmd instanceof UndoCommand) 
                && !(cmd instanceof RedoCommand)) {
            undoStack.push(new Context(cmd, result));
            redoStack.clear();
        }
    }
    
    public static History getInstance() {
        if(History.instance == null) {
            History.instance = new History();
        }
        return History.instance;
    }

    public History() { };
    
    private class Context {
        private Command cmd;
        private CommandResult result;
        private Context(final Command cmd, final CommandResult result) {
            this.cmd = cmd;
            this.result = result;
        }
    }

}
