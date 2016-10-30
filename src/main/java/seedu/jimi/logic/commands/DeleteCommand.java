package seedu.jimi.logic.commands;

import java.util.Optional;

import seedu.jimi.commons.core.Messages;
import seedu.jimi.commons.core.UnmodifiableObservableList;
import seedu.jimi.model.task.ReadOnlyTask;
import seedu.jimi.model.task.UniqueTaskList.TaskNotFoundException;

/**
 * Deletes a task identified using it's last displayed index from Jimi.
 */
public class DeleteCommand extends Command implements TaskBookEditor {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the specified task/event from Jimi.\n"
            + "You can specify the task/event by entering its index number given in the last listing. \n"
            + "If you need to recover your deleted task, you can use the undo command. \n"
            + "Parameters: INDEX (must be t<positive integer> or e<positive integer>)\n"
            + "Example: " + COMMAND_WORD + " t1";

    public static final String MESSAGE_DELETE_TASK_SUCCESS = "Jimi has deleted this task: %1$s";

    public final String targetIndex;

    public DeleteCommand() {
        this(null);
    }
    
    public DeleteCommand(String targetIndex) {
        this.targetIndex = targetIndex;
    }

    // @@author A0140133B
    @Override 
    public CommandResult execute() {
        
        Optional<UnmodifiableObservableList<ReadOnlyTask>> optionalList = 
                determineListFromIndexPrefix(targetIndex);
        
        // actual integer index is everything after the first character prefix.
        int actualIdx = Integer.parseInt(targetIndex.substring(1).trim());
        if (!optionalList.isPresent() || optionalList.get().size() < actualIdx) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }
        
        UnmodifiableObservableList<ReadOnlyTask> lastShownList = optionalList.get();
        
        ReadOnlyTask taskToDelete = lastShownList.get(actualIdx - 1);        

        try {
            model.deleteTask(taskToDelete);
        } catch (TaskNotFoundException tnfe) {
            assert false : "The target task cannot be missing";
        }

        return new CommandResult(String.format(MESSAGE_DELETE_TASK_SUCCESS, taskToDelete));
    }
    
    @Override
    public boolean isValidCommandWord(String commandWord) {
        for (int i = 1; i <= COMMAND_WORD.length(); i++) {
            if (commandWord.toLowerCase().equals(COMMAND_WORD.substring(0, i))) {
                return true;
            }
        }
        return false;
    }
    // @@author
    
    @Override
    public String getMessageUsage() {
        return MESSAGE_USAGE;
    }
}
