package seedu.jimi.logic.parser;

import static seedu.jimi.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.jimi.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.jimi.commons.exceptions.IllegalValueException;
import seedu.jimi.commons.util.StringUtil;
import seedu.jimi.logic.commands.*;

/**
 * Parses user input.
 */
public class JimiParser {
    
    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    private static final Pattern TASK_INDEX_ARGS_FORMAT = Pattern.compile("(?<targetIndex>.+)");

    private static final Pattern KEYWORDS_ARGS_FORMAT =
            Pattern.compile("(?<keywords>\\S+(?:\\s+\\S+)*)"); // one or more keywords separated by whitespace

    private static final Pattern TASK_DATA_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("(?<detailsArguments>[^/]+)(?<tagArguments>(?: t/[^/]+)?)"); // zero or one tag only

    private static final Pattern EDIT_DATA_ARGS_FORMAT = // accepts index at beginning, follows task/event patterns after
            Pattern.compile("(?<targetIndex>\\d+\\s)(?<name>[^/]+)(?<tagArguments>(?: t/[^/]+)?)");
    
    private static final Pattern DETAILS_ARGS_FORMAT = 
            Pattern.compile("(\"(?<taskDetails>.+)\")( by (?<dateTime>.+))?");
    
    public JimiParser() {}

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     */
    public Command parseCommand(String userInput) {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }
        
        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        
        if (AddCommand.isCommandWord(commandWord)) {
            return prepareAdd(arguments);
        } else if (EditCommand.isCommandWord(commandWord)) {
            return prepareEdit(arguments);
        } else if (SelectCommand.isCommandWord(commandWord)) {
            return prepareSelect(arguments);
        } else if (DeleteCommand.isCommandWord(commandWord)) {
            return prepareDelete(arguments);
        } else if (ClearCommand.isCommandWord(commandWord)) {
            return new ClearCommand();
        } else if (FindCommand.isCommandWord(commandWord)) {
            return prepareFind(arguments);
        } else if (ListCommand.isCommandWord(commandWord)) {     
            return new ListCommand();
        } else if (ExitCommand.COMMAND_WORD.equals(commandWord)) {        
            return new ExitCommand();
        } else if (HelpCommand.isCommandWord(commandWord)) {       
            return new HelpCommand();
        } else {        
            return new IncorrectCommand(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    /**
     * Parses arguments in the context of the add task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareAdd(String args){
        final Matcher detailsAndTagsMatcher = TASK_DATA_ARGS_FORMAT.matcher(args.trim());
        // Validate entire args string format
        if (!detailsAndTagsMatcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
        
        final Matcher detailsMatcher =
                DETAILS_ARGS_FORMAT.matcher(detailsAndTagsMatcher.group("detailsArguments").trim());
        // Validate details args format
        if (!detailsMatcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
        
        try {
            return new AddCommand(
                    detailsMatcher.group("taskDetails"),
                    detailsMatcher.group("dateTime"),
                    getTagsFromArgs(detailsAndTagsMatcher.group("tagArguments"))
            );
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Parses arguments in context of the edit task command.
     * 
     * @param args Full user command input args
     * @return  the prepared edit command
     */
    private Command prepareEdit(String args){
        final Matcher matcher = EDIT_DATA_ARGS_FORMAT.matcher(args.trim());
        
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }
        
        try {
            return new EditCommand(
                    matcher.group("name"),
                    getTagsFromArgs(matcher.group("tagArguments")),
                    Integer.parseInt(matcher.group("targetIndex").trim())
                    );
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
        
    }
    
    /**
     * Extracts the new task's tags from the add command's tag arguments string.
     * Merges duplicate tag strings.
     */
    private static Set<String> getTagsFromArgs(String tagArguments) throws IllegalValueException {
        // no tags
        if (tagArguments.isEmpty()) {
            return Collections.emptySet();
        }
        // replace first delimiter prefix, then split
        final Collection<String> tagStrings = Arrays.asList(tagArguments.replaceFirst(" t/", "").split(" t/"));
        return new HashSet<>(tagStrings);
    }

    /**
     * Parses arguments in the context of the delete task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareDelete(String args) {
        Optional<Integer> index = parseIndex(args);
        if(!index.isPresent()){
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }

        return new DeleteCommand(index.get());
    }

    /**
     * Parses arguments in the context of the select task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareSelect(String args) {
        Optional<Integer> index = parseIndex(args);
        if(!index.isPresent()){
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectCommand.MESSAGE_USAGE));
        }

        return new SelectCommand(index.get());
    }

    /**
     * Returns the specified index in the {@code command} IF a positive unsigned integer is given as the index.
     *   Returns an {@code Optional.empty()} otherwise.
     */
    private Optional<Integer> parseIndex(String command) {
        final Matcher matcher = TASK_INDEX_ARGS_FORMAT.matcher(command.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String index = matcher.group("targetIndex");
        if(!StringUtil.isUnsignedInteger(index)){
            return Optional.empty();
        }
        return Optional.of(Integer.parseInt(index));

    }

    /**
     * Parses arguments in the context of the find task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareFind(String args) {
        final Matcher matcher = KEYWORDS_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    FindCommand.MESSAGE_USAGE));
        }

        // keywords delimited by whitespace
        final String[] keywords = matcher.group("keywords").split("\\s+");
        final Set<String> keywordSet = new HashSet<>(Arrays.asList(keywords));
        return new FindCommand(keywordSet);
    }

}