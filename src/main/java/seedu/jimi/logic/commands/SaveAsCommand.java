package seedu.jimi.logic.commands;

import java.io.IOException;

import seedu.jimi.commons.core.Config;
import seedu.jimi.commons.exceptions.DataConversionException;
import seedu.jimi.commons.util.ConfigUtil;
import seedu.jimi.commons.util.FileUtil;

/**
 *  
 * @author Wei Yin 
 * 
 * Sets save directory for the tasks and events in Jimi.
 */
public class SaveAsCommand extends Command {

    public static final String COMMAND_WORD = "saveas";
    
    public static final String COMMAND_WORD_RESET = "reset";

    public static final String MESSAGE_USAGE = 
            COMMAND_WORD + ": Set a new save directory for all your tasks and events in Jimi.\n"
            + "Parameters: FILEPATH/FILENAME.xml or FILEPATH\\FILENAME.xml \n"
            + "Example: " + COMMAND_WORD + " C:/dropbox/taskbook.xml\n"
            + "> Tip: type \"saveas reset\" to reset Jimi's save directory back to its default.";

    public static final String MESSAGE_INVALID_PATH = "Specified directory is invalid!";
    public static final String MESSAGE_SUCCESS = "Save directory changed: %1$s";
    public static final String MESSAGE_CONFIG_FILE_NOT_FOUND = "Config file not found.";
    public static final String MESSAGE_DUPLICATE_SAVE_DIRECTORY =
            "New save directory is the same as the old save directory.";
    
    private static String configFilePath = Config.DEFAULT_CONFIG_FILE;
    
    private String taskBookFilePath;
    
    /**
     * Empty constructor for stub usage
     */
    public SaveAsCommand() {}
    
    /**
     * Convenience constructor using raw values.
     */
    public SaveAsCommand(String filePath) {
        this.taskBookFilePath = filePath;
    }
    
    public static void setConfigFilePath(String newConfigFilePath) {
        configFilePath = newConfigFilePath;
    }
    
    @Override
    public CommandResult execute() {
        if (!FileUtil.isValidPath(taskBookFilePath)) {
            return new CommandResult(MESSAGE_INVALID_PATH);
        }
        
        try {
            Config config = ConfigUtil.readConfig(configFilePath).orElse(new Config());
            
            String oldTaskBookFilePath = config.getTaskBookFilePath();
            if (oldTaskBookFilePath.equals(taskBookFilePath)) {
                indicateAttemptToExecuteIncorrectCommand();
                return new CommandResult(String.format(MESSAGE_DUPLICATE_SAVE_DIRECTORY));
            }
            config.setTaskBookFilePath(taskBookFilePath);
            
            ConfigUtil.saveConfig(config, configFilePath);
            
            indicateStoragePathChanged(oldTaskBookFilePath, taskBookFilePath);
            
            return new CommandResult(String.format(MESSAGE_SUCCESS, config.getTaskBookFilePath()));
        } catch (DataConversionException e) {
            return new CommandResult(MESSAGE_CONFIG_FILE_NOT_FOUND);
        } catch (IOException e) {
            return new CommandResult(e.getMessage());
        }
    }
    
    @Override
    public boolean isValidCommandWord(String commandWord) {
        return commandWord.equals(COMMAND_WORD);
    }
}