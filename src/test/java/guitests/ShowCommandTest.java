package guitests;


import org.junit.Test;

import seedu.jimi.commons.core.Messages;
import seedu.jimi.logic.commands.ShowCommand;
import seedu.jimi.model.task.ReadOnlyTask;
import seedu.jimi.testutil.TestDeadlineTask;
import seedu.jimi.testutil.TestEvent;
import seedu.jimi.testutil.TypicalTestDeadlineTasks;
import seedu.jimi.testutil.TypicalTestEvents;
//@@author A0138915X
public class ShowCommandTest extends AddressBookGuiTest {

    
    @Test
    public void show_floatingTasks() {
        ReadOnlyTask[] currentList = td.getTypicalTasks();
        int listSize = currentList.length;
        assertShowResult("show floating", listSize);
    }
    
    //tests dynamic day titles
    @Test
    public void show_tomorrow() {
        assertShowResult("show tomorrow", 
                         TypicalTestDeadlineTasks.ideas,
                         TypicalTestEvents.wedding);
        TestDeadlineTask taskToAdd = TypicalTestDeadlineTasks.homework;
        commandBox.runCommand(taskToAdd.getAddCommand());
        assertShowResult("show TOMORROW", //Test case-insensitivity in section names
                TypicalTestDeadlineTasks.ideas,
                TypicalTestEvents.wedding,
                taskToAdd);
    }
    
    @Test
    public void show_today() {
        assertShowResult("show today", 
                         TypicalTestDeadlineTasks.groceries,
                         TypicalTestEvents.tuition);
        TestEvent eventToAdd = TypicalTestEvents.nightClass;
        commandBox.runCommand(eventToAdd.getAddCommand());
        assertShowResult("show tOdAy", //Test case-insensitivity in section names
                TypicalTestDeadlineTasks.groceries,
                TypicalTestEvents.tuition,
                eventToAdd);
    }
    
    @Test
    public void show_invalidDay() {
        commandBox.runCommand("show floatingtasks");
        assertResultMessage(String.format(ShowCommand.MESSAGE_INVALID_SECTION));
    }

    private void assertShowResult(String command, int listSize) {
        commandBox.runCommand(command);
        assertResultMessage(String.format(Messages.MESSAGE_TASKS_LISTED_OVERVIEW, listSize));
    }
    
    private void assertShowResult(String command, ReadOnlyTask... expectedHits) {
        commandBox.runCommand(command);
        assertResultMessage(String.format(Messages.MESSAGE_TASKS_LISTED_OVERVIEW, expectedHits.length));
    }
}
