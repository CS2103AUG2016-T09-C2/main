package guitests;

import static org.junit.Assert.*;
import static seedu.jimi.logic.commands.EditCommand.MESSAGE_EDIT_SUCCESS;

import java.util.Comparator;

import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Arrays;
import guitests.guihandles.FloatingTaskCardHandle;
import seedu.jimi.commons.core.Messages;
import seedu.jimi.commons.exceptions.IllegalValueException;
import seedu.jimi.logic.commands.EditCommand;
import seedu.jimi.model.tag.Priority;
import seedu.jimi.model.tag.Tag;
import seedu.jimi.model.tag.UniqueTagList;
import seedu.jimi.model.task.Name;
import seedu.jimi.model.task.ReadOnlyTask;
import seedu.jimi.testutil.TestFloatingTask;
import seedu.jimi.testutil.TestUtil;
import seedu.jimi.testutil.TypicalTestFloatingTasks;

// @@author A0143471L
public class EditCommandTest extends AddressBookGuiTest{
    
    @Test
    public void edit() throws IllegalValueException {
        // Set up the initialised lists
        ReadOnlyTask[] currentList = td.getTypicalTasks();
        Arrays.sort(currentList, 0, currentList.length, NameComparator);
        
        // edit the first floating task in list with only name changes
        int targetIndex = 1;
        String newName = "Get rich or die coding";
        TestFloatingTask expectedTask = TypicalTestFloatingTasks.water;
        expectedTask.setName(new Name(newName));
        assertEditSuccess(currentList, expectedTask, targetIndex, newName, null, null);
        Arrays.sort(currentList, 0, currentList.length, NameComparator);
        
        // edit task with same changes
        commandBox.runCommand("edit t6 \"" + newName + "\"");
        assertResultMessage(EditCommand.MESSAGE_DUPLICATE_TASK);
        
        // edit the second floating task in list with tag and priority changes
        targetIndex = 1;
        String newTag = "presentation";
        String newPriority = "med";
        expectedTask = TypicalTestFloatingTasks.ideas;
        expectedTask.setTags(new UniqueTagList(new Tag(newTag)));
        expectedTask.setPriority(new Priority(newPriority));
        assertEditSuccess(currentList, expectedTask, targetIndex, null, newTag, newPriority);
        Arrays.sort(currentList, 0, currentList.length, NameComparator);
        
        // edit task with invalid index
        commandBox.runCommand("edit t" + currentList.length + 1 + " \"Change invalid stuff\"");
        assertResultMessage(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX); 
        
        // edit task with wrong command message
        commandBox.runCommand("edit t1");
        assertResultMessage(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        
        // invalid command
        commandBox.runCommand("edits");
        assertResultMessage(String.format(Messages.MESSAGE_UNKNOWN_COMMAND, "edits"));
    }
    
    /**
     * Runs the edit command on the target floating task and checks if the edited task matches with changedTask
     * 
     * @param targetIndexIndex Index of target task to be edited
     * @param currentList List of tasks to be edited.
     * @throws IllegalValueException
     **/
    private void assertEditSuccess(ReadOnlyTask[] currentList, ReadOnlyTask expectedTask, int targetIndex,
                                   String newName, String newTag, String newPriority) throws IllegalValueException {
        // Generate command to edit the task
        String commandMessage = "edit " + "t" + targetIndex;
        
        if (newName != null) 
            commandMessage = commandMessage + " \"" + newName + "\"";
        if (newTag != null) 
            commandMessage = commandMessage + " t/" + newTag;
        if (newPriority != null)
            commandMessage = commandMessage + " p/" + newPriority;
        
        // Run the command to edit the task
        commandBox.runCommand(commandMessage);
            
        // Confirm that edited card contains the right data
        FloatingTaskCardHandle addedCard = taskListPanel.navigateToTask(expectedTask.getName().fullName);
        assertMatching(expectedTask, addedCard);
        
        //confirm the list now contains all previous tasks plus the edited task
        ReadOnlyTask[] expectedList = TestUtil.replaceTaskFromList(currentList, expectedTask, targetIndex - 1);
        Arrays.sort(expectedList, 0, expectedList.length, NameComparator);
        assertTrue(taskListPanel.isListMatching(expectedList));
        
        //confirm the result message is correct
        assertResultMessage(String.format(MESSAGE_EDIT_SUCCESS, expectedTask));
    }

    /**
     * Sorts expected floatingTaskListPanel in alphabetical order
     */
    public static Comparator<ReadOnlyTask> NameComparator = new Comparator<ReadOnlyTask>() {

        public int compare(ReadOnlyTask task1, ReadOnlyTask task2) {

            String taskName1 = task1.getName().toString().toUpperCase();
            String taskName2 = task2.getName().toString().toUpperCase();

            // ascending order
            return taskName1.compareTo(taskName2);
        }
    };
    // @@author
}
