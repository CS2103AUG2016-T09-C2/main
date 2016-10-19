package guitests;

import static org.junit.Assert.*;

import org.junit.Test;

import seedu.jimi.commons.core.Messages;
import seedu.jimi.logic.commands.ShowCommand;
import seedu.jimi.testutil.TestFloatingTask;

public class ShowCommandTest extends AddressBookGuiTest {

    @Test
    public void show_floatingTasks() {
        assertShowResult("show floating tasks");
    }
    
    //tests dynamic day titles
    @Test
    public void show_Tuesday() {
        assertShowResult("show Tuesday");
    }
    
    @Test
    public void show_invalidDay() {
        commandBox.runCommand("show floatingtasks");
        assertResultMessage(ShowCommand.MESSAGE_USAGE);
    }

    private void assertShowResult(String command) {
        commandBox.runCommand(command);
        assertResultMessage("Displayed tasks and events.");
    }
}
