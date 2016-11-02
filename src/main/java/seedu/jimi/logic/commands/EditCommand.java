package seedu.jimi.logic.commands;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.jimi.commons.core.Messages;
import seedu.jimi.commons.core.UnmodifiableObservableList;
import seedu.jimi.commons.exceptions.IllegalValueException;
import seedu.jimi.model.datetime.DateTime;
import seedu.jimi.model.event.Event;
import seedu.jimi.model.tag.Priority;
import seedu.jimi.model.tag.Tag;
import seedu.jimi.model.tag.UniqueTagList;
import seedu.jimi.model.task.DeadlineTask;
import seedu.jimi.model.task.FloatingTask;
import seedu.jimi.model.task.Name;
import seedu.jimi.model.task.ReadOnlyTask;
import seedu.jimi.model.task.UniqueTaskList.DuplicateTaskException;

// @@author A0140133B
/**
 * Edits an existing task/event in Jimi.
 */
public class EditCommand extends Command implements TaskBookEditor {
    
    public static final String COMMAND_WORD = "edit";
    public static final String COMMAND_REMOVE_DATES = "float";
    
    public static final String INDEX_TASK_PREFIX = "t";
    public static final String INDEX_EVENT_PREFIX = "e";
    
    public static final String MESSAGE_USAGE = 
            COMMAND_WORD + ": Edits an existing task/event in Jimi. \n"
            + "Parameters: INDEX(must be t<positive integer> or e<positive integer>) EDITS_TO_MAKE\n" 
            + "You can edit everything from the task name to its tags. \n"
            + "You can leave out fields that you do not wish to edit too. \n"
            + "\n"
            + "For instance, if t2. already has a deadline but you only wish to edit its name: \n"
            + "Example: " + COMMAND_WORD + " t2 \"clear trash\"\n"
            + "\n"
            + "You can also convert between task types too. \n"
            + "Here's an example of converting an event to a task: \n"
            + "Example: " + COMMAND_WORD + " e2 due sunday\n"
            + "\n"
            + "If you wish to remove all dates from an existing task i.e. make it floating: \n"
            + "Example: " + COMMAND_WORD + " e1 float \n"
            + "\n"
            + "> Tip: Typing 'e', 'ed', 'edi' instead of 'edit' works too.";
    
    public static final String MESSAGE_EDIT_SUCCESS = "Updated details: %1$s";
    private static final String MESSAGE_DUPLICATE_TASK = 
            "Your new edits seem to overlap with an already existing task. Please check and try again!";
    
    private final String taskIndex; //index of task/event to be edited
    private UniqueTagList newTagList;
    private Name newName;
    private Priority newPriority;
    
    private DateTime deadline;
    private DateTime eventStart;
    private DateTime eventEnd;
    
    private enum EditType {
        REMOVE_DATES,
        TO_SAME_TYPE,
        TO_EVENT,
        TO_DEADLINE,
        ONLY_PRIORITY
    }
    
    private EditType editType;
    
    /** Empty constructor for stub usage */
    public EditCommand() {
        this(null);
    }
    
    /** Constructor nullifying everything except {@code taskIndex}. */
    public EditCommand(String taskIndex) {
        this.taskIndex = taskIndex;
        editType = EditType.REMOVE_DATES;
    }
    
    public EditCommand(String name, Set<String> tags, List<Date> deadline, List<Date> eventStart, List<Date> eventEnd,
            String taskIndex, String priority) throws IllegalValueException {
        
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(new Tag(tagName));
        }
        
        this.taskIndex = taskIndex;
        
        if (name != null) {
            this.newName = new Name(name);
        }
        
        if (!tagSet.isEmpty()) {
            this.newTagList = new UniqueTagList(tagSet);
        }
        
        if (priority != null && !priority.isEmpty())   {
            this.newPriority = new Priority(priority);
        }
        
        this.deadline = (deadline.size() != 0) ? new DateTime(deadline.get(0)) : null;
        this.eventStart = (eventStart.size() != 0) ? new DateTime(eventStart.get(0)) : null;
        this.eventEnd = (eventEnd.size() != 0) ? new DateTime(eventEnd.get(0)) : null;
        
        determineEditType();
    }
    
    @Override
    public CommandResult execute() {
        Optional<UnmodifiableObservableList<ReadOnlyTask>> optionalList = 
                determineListFromIndexPrefix(taskIndex);
        
        // actual index is everything after the 1 character prefix.
        int actualIdx = Integer.parseInt(taskIndex.substring(1).trim());
        if (!optionalList.isPresent() || optionalList.get().size() < actualIdx) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }
        
        UnmodifiableObservableList<ReadOnlyTask> lastShownList = optionalList.get();
        
        ReadOnlyTask oldTask = lastShownList.get(actualIdx - 1);        
        Optional<ReadOnlyTask> newTask;
        try {
            newTask = determineNewTask(oldTask);
        } catch (IllegalValueException e) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(e.getMessage());
        }
        if (!newTask.isPresent()) {
            new CommandResult(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }
        
        try {
            model.replaceTask(oldTask, newTask.get());
            return new CommandResult(String.format(MESSAGE_EDIT_SUCCESS, newTask.get()));
        } catch (DuplicateTaskException dte) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(MESSAGE_DUPLICATE_TASK);
        }
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
    
    @Override
    public String getMessageUsage() {
        return MESSAGE_USAGE;
    }
    
    @Override
    public String getCommandWord() {
        return COMMAND_WORD;
    }
    
    /*
     * ====================================================================
     *                          Helper Methods
     * ====================================================================
     */
    
    /** Determines the type of edit based on user input. */
    private void determineEditType() {
        if ((newName != null || newTagList != null || newPriority != null) && deadline == null && eventStart == null
                && eventEnd == null) {
            this.editType = EditType.TO_SAME_TYPE;
        } else if (eventStart == null && eventEnd == null && deadline != null) {
            this.editType = EditType.TO_DEADLINE;
        } else if (deadline == null && (eventStart != null || eventEnd != null)) {
            this.editType = EditType.TO_EVENT;
        }
    }
    
    /** Generates the new task to replace the current task 
     * @throws IllegalValueException */
    private Optional<ReadOnlyTask> determineNewTask(ReadOnlyTask oldTask) throws IllegalValueException {
        switch (editType) {
        case REMOVE_DATES :
            return Optional.of(toFloatingTypeWithChanges(oldTask));
        case TO_SAME_TYPE :
            return Optional.of(toSameTaskTypeWithChanges(oldTask));
        case TO_EVENT :
            return Optional.of(toEventTypeWithChanges(oldTask));
        case TO_DEADLINE :
            return Optional.of(toDeadlineTaskTypeWithChanges(oldTask));
        default :
            return Optional.empty();
        }
    }

    /** Generates a floating task with changes */
    private ReadOnlyTask toFloatingTypeWithChanges(ReadOnlyTask t) {
        return new FloatingTask(
                newName == null ? t.getName() : newName, 
                newTagList == null ? t.getTags() : newTagList, 
                t.isCompleted(),
                newPriority == null ? t.getPriority() : newPriority);
    }

    /** Generates a deadline task with changes. */
    private ReadOnlyTask toDeadlineTaskTypeWithChanges(ReadOnlyTask t) {
        return new DeadlineTask(
                newName == null ? t.getName() : newName, 
                t instanceof DeadlineTask && deadline == null ? ((DeadlineTask) t).getDeadline() : deadline, 
                newTagList == null ? t.getTags() : newTagList, 
                newPriority == null ? t.getPriority() : newPriority);
    }

    /** Generates an Event with changes. 
     * @throws IllegalValueException */
    private ReadOnlyTask toEventTypeWithChanges(ReadOnlyTask t) throws IllegalValueException {
        DateTime newStart = t instanceof Event && eventStart == null ? 
                ((Event) t).getStart() : eventStart;
        DateTime newEnd =  t instanceof Event && eventEnd == null ? 
                ((Event) t).getEnd() : eventEnd;
        if (newStart.compareTo(newEnd) <= 0) {
            return new Event(newName == null ? t.getName() : newName, newStart, newEnd,
                    newTagList == null ? t.getTags() : newTagList, t.isCompleted(),
					newPriority == null ? t.getPriority() : newPriority);
        } else {
            throw new IllegalValueException(Messages.MESSAGE_START_END_CONSTRAINT);
        }
    }

    /** Generates a task with changes while maintaining it's task type. 
     * @throws IllegalValueException */
    private ReadOnlyTask toSameTaskTypeWithChanges(ReadOnlyTask t) throws IllegalValueException {
        if (t instanceof Event) {
            DateTime newStart = eventStart == null ? ((Event) t).getStart() : eventStart;
            DateTime newEnd = eventEnd == null ? ((Event) t).getEnd() : eventEnd;
            if (newStart.compareTo(newEnd) <= 0) {
                return new Event(newName == null ? t.getName() : newName, newStart, newEnd,
                        newTagList == null ? t.getTags() : newTagList, t.isCompleted(), 
						newPriority == null ? t.getPriority() : newPriority);
            } else {
                throw new IllegalValueException(Messages.MESSAGE_START_END_CONSTRAINT);
            }
        } else if (t instanceof DeadlineTask) {
            return new DeadlineTask(
                    newName == null ? t.getName() : newName, 
                    deadline == null ? ((DeadlineTask) t).getDeadline() : deadline, 
                    newTagList == null ? t.getTags() : newTagList, 
                    t.isCompleted(),
                    newPriority == null ? t.getPriority() : newPriority);
        } else { // floating task
            return new FloatingTask( 
                    newName == null ? t.getName() : newName, 
                    newTagList == null ? t.getTags() : newTagList, 
                    t.isCompleted(),
                    newPriority == null ? t.getPriority() : newPriority);
        }
    }
}
