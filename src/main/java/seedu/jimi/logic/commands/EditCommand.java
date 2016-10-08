package seedu.jimi.logic.commands;

import seedu.jimi.model.task.FloatingTask;

/**
 * 
 * @author zexuan
 *
 * Edits an existing task/event in Jimi.
 */
public class EditCommand extends Command{

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits an existing task/event in Jimi. "
            + "Example: " + COMMAND_WORD
            + "2 by 10th July at 12 pm";

    public static final String MESSAGE_SUCCESS = "New task added: %1$s";
    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists in Jimi";

    private final FloatingTask toEdit;

    
    @Override
    public CommandResult execute() {
        // TODO Auto-generated method stub
        return null;
    }

}
