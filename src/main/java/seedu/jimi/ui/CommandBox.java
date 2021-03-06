package seedu.jimi.ui;

import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import seedu.jimi.commons.core.LogsCenter;
import seedu.jimi.commons.core.Messages;
import seedu.jimi.commons.events.ui.IncorrectCommandAttemptedEvent;
import seedu.jimi.commons.util.CommandUtil;
import seedu.jimi.commons.util.FxViewUtil;
import seedu.jimi.commons.util.StringUtil;
import seedu.jimi.logic.Logic;
import seedu.jimi.logic.commands.CommandResult;

public class CommandBox extends UiPart {
    private final Logger logger = LogsCenter.getLogger(CommandBox.class);
    private static final String FXML = "CommandBox.fxml";

    private AnchorPane placeHolderPane;
    private AnchorPane commandPane;
    private ResultDisplay resultDisplay;
    String previousCommandText;

    private Logic logic;

    private Stack<String> previousInputs = new Stack<String>();
    private Stack<String> aheadInputs = new Stack<String>();
    
    public static final String MESSAGE_COMMAND_SUGGESTIONS = 
            "Are you looking for these commands? \n" 
            + "> %1$s";
    
    @FXML
    private TextField commandTextField;
    private CommandResult mostRecentResult;

    public static CommandBox load(Stage primaryStage, AnchorPane commandBoxPlaceholder,
            ResultDisplay resultDisplay, Logic logic) {
        CommandBox commandBox = UiPartLoader.loadUiPart(primaryStage, commandBoxPlaceholder, new CommandBox());
        commandBox.configure(resultDisplay, logic);
        commandBox.addToPlaceholder();
        return commandBox;
    }

    public void configure(ResultDisplay resultDisplay, Logic logic) {
        this.resultDisplay = resultDisplay;
        this.logic = logic;
        registerAsAnEventHandler(this);
    }

    private void addToPlaceholder() {
        SplitPane.setResizableWithParent(placeHolderPane, false);
        placeHolderPane.getChildren().add(commandTextField);
        FxViewUtil.applyAnchorBoundaryParameters(commandPane, 0.0, 0.0, 0.0, 0.0);
        FxViewUtil.applyAnchorBoundaryParameters(commandTextField, 0.0, 0.0, 0.0, 0.0);
    }

    @Override
    public void setNode(Node node) {
        commandPane = (AnchorPane) node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    @Override
    public void setPlaceholder(AnchorPane pane) {
        this.placeHolderPane = pane;
    }
    
    // @@author A0140133B
    @FXML
    private void handleTextFieldKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
        case UP :
            cyclePreviousInput();
            return;
        case DOWN :
            cycleAheadInput();
            return;
        case ENTER :
            return; // Do nothing since handleCommandInputChanged already handles this.
        default :
            handleTextFieldKeyTyped();
            return;
        }
    }
    
    /** Handles event when command input has changed */
    @FXML
    private void handleCommandInputChanged() {
        if (commandTextField.getText().trim().isEmpty()) {
            return; // Do nothing for empty input.
        }
        previousCommandText = commandTextField.getText().trim(); // Take a copy of the command text.
        resetInputHistoryToMostRecent();
        previousInputs.push(previousCommandText); // Updating history of inputs for up/down cycling.
        
        /* We assume the command is correct. If it is incorrect, the command box will be changed accordingly
         * in the event handling code {@link #handleIncorrectCommandAttempted}
         */
        setResultDisplayAndCmdBoxToDefault();
        executeCommand();
    }
    
    /** Parses and executes the command given by {@code previousCommandText} */
    private void executeCommand() {
        mostRecentResult = logic.execute(previousCommandText);
        resultDisplay.postMessage(mostRecentResult.feedbackToUser);
        logger.info("Result: " + mostRecentResult.feedbackToUser);
    }
    
    /** Handles the event when a key is typed in {@code commandTextField}. */
    private void handleTextFieldKeyTyped() {
        String currentText = commandTextField.getText().trim();
        
        logger.info("Text in text field: " + currentText);
        
        if (currentText.isEmpty()) {
            setResultDisplayAndCmdBoxToDefault();
        } else {
            postCommandSuggestions(currentText.toLowerCase());
        }
    }
    
    /** Posts suggestions for commands according to the first word of {@code currentText} */
    private void postCommandSuggestions(String currentText) {
        /* 
         * Only providing suggestions for first word, so once the full input text length 
         * exceeds the length of the first word, stop providing suggestions. 
         */
        String firstWordOfInput = StringUtil.getFirstWord(currentText);
        if (currentText.length() > firstWordOfInput.length()) { // Short-circuit here to improve performance.
            setResultDisplayToDefault();
            return; 
        }
        
        List<String> commandWordMatches = CommandUtil.getInstance().getCommandWordMatches(firstWordOfInput);
        
        logger.info("Suggestions: " + commandWordMatches);
        
        if (commandWordMatches.isEmpty()) {
            setResultDisplayToDefault();
        } else {
            resultDisplay.postMessage(
                    String.format(MESSAGE_COMMAND_SUGGESTIONS, String.join(", ", commandWordMatches)));
        }
    }
    
    /** Sets {@code commandTextField} and {@code resultDisplay} to their default styles/postings */
    private void setResultDisplayAndCmdBoxToDefault() {
        setCommandBoxtoDefault();
        setResultDisplayToDefault();
    }
    
    /** Sets {@code commandTextField} with input texts cycling forwards in input history. */
    private void cycleAheadInput() {
        if (aheadInputs.isEmpty()) {
            return;
        }
        
        commandTextField.setText(aheadInputs.peek());
        setCaretToRightEnd();
        
        // Last input text does not need to be popped so as to avoid double counting.
        if (aheadInputs.size() > 1) {
            previousInputs.push(aheadInputs.pop());
        }
    }
    
    /** Sets {@code commandTextField} with input texts cycling backwards in input history. */
    private void cyclePreviousInput() {
        if (previousInputs.isEmpty()) {
            return;
        }
        
        commandTextField.setText(previousInputs.peek());
        setCaretToRightEnd();
        
        // Last input text does not need to be popped so as to avoid double counting.
        if (previousInputs.size() > 1) {
            aheadInputs.push(previousInputs.pop());
        }
    }
    
    /** Pushes all input text in {@code aheadInputs} into {@code previousInputs}. */
    private void resetInputHistoryToMostRecent() {
        while (!aheadInputs.isEmpty()) {
            previousInputs.push(aheadInputs.pop());
        }
    }
    
    /** Shifts caret to the right end of {@code commandTextField} */
    private void setCaretToRightEnd() {
        commandTextField.positionCaret(commandTextField.getText().length());
    }
    
    /** Sets {@code resultDisplay} to its default posting */
    private void setResultDisplayToDefault() {
        // If most recent result does not exist, post welcome message instead.
        resultDisplay.postMessage(
                mostRecentResult == null ? Messages.MESSAGE_WELCOME_JIMI : mostRecentResult.feedbackToUser);
    }
    // @@author
    
    /**
     * Sets the command box style to indicate a correct command.
     */
    private void setCommandBoxtoDefault() {
        commandTextField.getStyleClass().remove("error");
        commandTextField.setText("");
    }

    @Subscribe
    private void handleIncorrectCommandAttempted(IncorrectCommandAttemptedEvent event){
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Invalid command: " + previousCommandText));
        setStyleToIndicateIncorrectCommand();
        restoreCommandText();
        setCaretToRightEnd();
    }

    /**
     * Restores the command box text to the previously entered command
     */
    private void restoreCommandText() {
        commandTextField.setText(previousCommandText);
    }

    /**
     * Sets the command box style to indicate an error
     */
    private void setStyleToIndicateIncorrectCommand() {
        commandTextField.getStyleClass().add("error");
    }

}
