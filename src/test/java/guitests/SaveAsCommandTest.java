package guitests;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import seedu.jimi.TestApp;
import seedu.jimi.commons.core.Config;
import seedu.jimi.commons.exceptions.DataConversionException;
import seedu.jimi.commons.util.ConfigUtil;
import seedu.jimi.logic.commands.SaveAsCommand;
import seedu.jimi.testutil.TestUtil;
//@@author A0143471L
public class SaveAsCommandTest extends AddressBookGuiTest{

    @Test
    public void saveAs() throws DataConversionException {
        Config currentConfig = ConfigUtil.readConfig(TestApp.DEFAULT_CONFIG_FILE_FOR_TESTING).orElse(new Config());
        SaveAsCommand.setConfigFilePath(TestApp.DEFAULT_CONFIG_FILE_FOR_TESTING);
        String originalTaskBookFilePath = TestUtil.getFilePathInSandboxFolder("sampleData.xml");
        
        // Taskbook directory to be changed to
        String newTaskBookFilePath = TestUtil.getFilePathInSandboxFolder("newSampleData.xml");
        
        // change storage directory
        assertSaveAsSuccess(currentConfig, newTaskBookFilePath);

        
        // save to duplicate storage directory
        commandBox.runCommand(getSaveAsCommand(newTaskBookFilePath));
        assertResultMessage(SaveAsCommand.MESSAGE_DUPLICATE_SAVE_DIRECTORY);


        // reset storage directory for next test run
        assertSaveAsSuccess(currentConfig, originalTaskBookFilePath);
        
    }

    /**
     * Runs the saveAs command on the newTaskBookFilePath and checks if the edited filepath matches with new filepath on the config file.
     * 
     * @param newFilePath newFilePath name to be used
     * @param currentConfig Config file to be edited.
     * @throws DataConversionException if Config file can't be read
     *
     */
    private void assertSaveAsSuccess(Config currentConfig, String newFilePath) throws DataConversionException {
        commandBox.runCommand(getSaveAsCommand(newFilePath));
        
        Config expectedConfig = new Config();
        expectedConfig.setTaskBookFilePath(newFilePath);
        
        currentConfig = ConfigUtil.readConfig(TestApp.DEFAULT_CONFIG_FILE_FOR_TESTING).orElse(new Config());    //Update currentConfig after command is executed
        assertEquals(newFilePath, currentConfig.getTaskBookFilePath());
        
    }
    
    public String getSaveAsCommand(String newFilePath) {
        StringBuilder sb = new StringBuilder();
        sb.append("saveas " + newFilePath + " ");
        return sb.toString();
    }
}
