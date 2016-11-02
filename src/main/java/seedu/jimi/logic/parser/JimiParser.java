package seedu.jimi.logic.parser;

import static seedu.jimi.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.jimi.commons.core.Messages.MESSAGE_INVALID_DATE;
import static seedu.jimi.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import seedu.jimi.commons.core.Config;
import seedu.jimi.commons.core.LogsCenter;
import seedu.jimi.commons.exceptions.DateNotParsableException;
import seedu.jimi.commons.exceptions.IllegalValueException;
import seedu.jimi.commons.util.CommandUtil;
import seedu.jimi.commons.util.StringUtil;
import seedu.jimi.logic.commands.AddCommand;
import seedu.jimi.logic.commands.Command;
import seedu.jimi.logic.commands.CompleteCommand;
import seedu.jimi.logic.commands.DeleteCommand;
import seedu.jimi.logic.commands.EditCommand;
import seedu.jimi.logic.commands.FindCommand;
import seedu.jimi.logic.commands.HelpCommand;
import seedu.jimi.logic.commands.IncorrectCommand;
import seedu.jimi.logic.commands.SaveAsCommand;
import seedu.jimi.logic.commands.ShowCommand;
import seedu.jimi.model.tag.Priority;

/**
 * Parses user input.
 */
public class JimiParser {
    
    private static final Logger logger = LogsCenter.getLogger(JimiParser.class);

    // @@author A0140133B
    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    private static final Pattern TASK_INDEX_ARGS_FORMAT = Pattern.compile("[te](?<targetIndex>.+)");

    private static final Pattern KEYWORDS_WITH_DATES_ARGS_FORMAT =
            Pattern.compile("((\"(?<keywords>\\S+(?:\\s+\\S+)*)\")?(((on|from) (?<specificDateTime>.+))?)|(from (?<startDateTime>((?!to ).)*))?(to (?<endDateTime>.+))?)");
    
    private static final Pattern ADD_DATA_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("(?<ArgsDetails>[^/]+)(?<tagArguments>(?: t/[^/]+)?)(?<priorityArguments>(?: p/[^/]+)?)"); // zero or one tag only, zero or one priority    
    
    private static final Pattern EDIT_DATA_ARGS_FORMAT = // accepts index at beginning, follows task/event patterns after
            Pattern.compile("(?<targetIndex>[^\\s/]+) (?<editDetails>.+)");
    
    // all fields optional
    private static final Pattern EDIT_DETAILS_FORMAT = Pattern.compile(
            "(\"(?<taskDetails>.+)\"\\s*)?((due (?<deadline>[^/]+))|((on|from) (?<startDateTime>((?!to )[^/])*))?(to (?<endDateTime>[^/]+))?)?"
            + "(?<tagArguments>(?:\\s*t/[^/]+)?)(?<priorityArguments>(?:\\s*p/[^/]+)?)");
    
    private static final Pattern ADD_TASK_DATA_ARGS_FORMAT = 
            Pattern.compile("(\"(?<taskDetails>.+)\")( due (?<dateTime>.+))?");
    
    private static final Pattern ADD_EVENT_DATA_ARGS_FORMAT =
            Pattern.compile("(\"(?<taskDetails>.+)\") (on|from) (?<startDateTime>((?! to ).)*)( to (?<endDateTime>.+))?");
    
    private static final Pattern SHOW_COMMAND_ARGS_FORMAT = Pattern.compile("(?<sectionToShow>.+)");
    
    private static final Pattern SAVE_DIRECTORY_ARGS_FORMAT = Pattern.compile("(?<filePath>.+).xml");
    
    private static final Pattern SAVE_RESET_DIRECTORY_ARGS_FORMAT = Pattern.compile(SaveAsCommand.COMMAND_WORD_RESET);
    
    private static final String XML_FILE_EXTENSION = ".xml";
    
    private static final List<Command> COMMAND_STUB_LIST = CommandUtil.getInstance().getCommandStubList();

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
        final String arguments = matcher.group("arguments").trim();
        
        return prepareCommand(commandWord, arguments);
    }

    /**
     * Identifies which command to prepare according to raw command word.
     * 
     * @param commandWord command word from raw input
     * @param arguments arguments from raw input
     * @return correct Command corresponding to the command word if valid, else returns incorrect command.
     */
    private Command prepareCommand(String commandWord, String arguments) {
        Optional<Command> prepared = 
                COMMAND_STUB_LIST.stream().filter(c -> c.isValidCommandWord(commandWord)).map(c -> {
            if (c instanceof AddCommand) {
                return prepareAdd(arguments);
            } else if (c instanceof HelpCommand) {
                return prepareHelp(arguments);
            } else if (c instanceof EditCommand) {
                return prepareEdit(arguments);
            } else if (c instanceof CompleteCommand) {
                return prepareComplete(arguments);
            } else if (c instanceof ShowCommand) {
                return prepareShow(arguments);
            } else if (c instanceof DeleteCommand) {
                return prepareDelete(arguments);
            } else if (c instanceof FindCommand) {
                return prepareFind(arguments);
            } else if (c instanceof SaveAsCommand) {
                return prepareSaveAs(arguments);
            } else { // commands that do not require arguments e.g. exit
                return c;
            }
        }).findFirst();
        
        if (!prepared.isPresent()) {
            return new IncorrectCommand(String.format(MESSAGE_UNKNOWN_COMMAND, commandWord));
        }
        
        return prepared.get();
    }
    
    private Command prepareHelp(String args) {
        if (args.trim().isEmpty()) {
            return new HelpCommand();
        }
        
        try {
            return new HelpCommand(args.trim());
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Parses arguments in the context of the add task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareAdd(String args) {
        final Matcher detailsAndTagsMatcher = ADD_DATA_ARGS_FORMAT.matcher(args.trim());
        // Validate entire args string format
        if (!detailsAndTagsMatcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
        
        final Matcher taskDetailsMatcher =
                ADD_TASK_DATA_ARGS_FORMAT.matcher(detailsAndTagsMatcher.group("ArgsDetails").trim());
        final Matcher eventDetailsMatcher =
                ADD_EVENT_DATA_ARGS_FORMAT.matcher(detailsAndTagsMatcher.group("ArgsDetails").trim());
        
        if (taskDetailsMatcher.matches()) { // if user trying to add task 
            return generateAddCommandForTask(detailsAndTagsMatcher, taskDetailsMatcher);
        } else if (eventDetailsMatcher.matches()) { // if user trying to add event
            return generateAddCommandForEvent(detailsAndTagsMatcher, eventDetailsMatcher);
        }
        
        /* default return IncorrectCommand */
        return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }
    
    /**
     * Creates an AddCommand in the context of adding an event.
     * 
     * @return an AddCommand if raw args is valid, else IncorrectCommand
     */
    private Command generateAddCommandForEvent(final Matcher detailsAndTagsMatcher, final Matcher eventDetailsMatcher) {
        try {
            List<Date> startDates = parseStringToDate(eventDetailsMatcher.group("startDateTime"));
            List<Date> endDates = parseStringToDate(eventDetailsMatcher.group("endDateTime"));
            
            String priority = getPriorityFromArgs(detailsAndTagsMatcher.group("priorityArguments"));
            if (priority == null) {
                priority = Priority.PRIO_NONE;
            }
            
            return new AddCommand(
                    eventDetailsMatcher.group("taskDetails"),
                    startDates,
                    endDates,
                    getTagsFromArgs(detailsAndTagsMatcher.group("tagArguments")),
                    priority
            );
        } catch (DateNotParsableException e) {
            return new IncorrectCommand(e.getMessage());
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Creates an AddCommand in the context of adding an task.
     * 
     * @return an AddCommand if raw args is valid, else IncorrectCommand
     */
    private Command generateAddCommandForTask(final Matcher detailsAndTagsMatcher, final Matcher taskDetailsMatcher) {
        try {
            List<Date> dates = parseStringToDate(taskDetailsMatcher.group("dateTime"));
            
            String priority = getPriorityFromArgs(detailsAndTagsMatcher.group("priorityArguments"));
            if (priority == null) {
                priority = Priority.PRIO_NONE;
            }
            
            return new AddCommand(
                    taskDetailsMatcher.group("taskDetails"),
                    dates,
                    getTagsFromArgs(detailsAndTagsMatcher.group("tagArguments")),
                    priority
            );
        } catch (DateNotParsableException e) {
            return new IncorrectCommand(e.getMessage());
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }
    // @@author
    
    // @@author A0148040R
    private static List<Date> parseStringToDate(final String str) throws DateNotParsableException {
        if (str == null) {
            return new ArrayList<Date>();
        }
        
        final Parser dateParser = new Parser();
        final List<DateGroup> groups = dateParser.parse(str);
        
        if (!groups.isEmpty()) {
            return groups.get(0).getDates();
        } else {
            throw new DateNotParsableException(MESSAGE_INVALID_DATE);
        }
    }
    // @@author

    // @@author A0140133B
    /**
     * Parses arguments in context of the edit task command.
     * 
     * @param args Full user command input args
     * @return the prepared edit command
     */
    private Command prepareEdit(String args) {
        // Validate args in terms of <idx><details>
        final Matcher editArgsMatcher = EDIT_DATA_ARGS_FORMAT.matcher(args.trim());
        if (!editArgsMatcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }
        
        // User wishes to remove dates
        if (editArgsMatcher.group("editDetails").trim().equals(EditCommand.COMMAND_REMOVE_DATES)) {
            return new EditCommand(editArgsMatcher.group("targetIndex"));
        }
        
        // Validate details format
        final Matcher editDetailsMatcher = EDIT_DETAILS_FORMAT.matcher(editArgsMatcher.group("editDetails").trim());
        if (!editDetailsMatcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }
        
        try {
            return generateEditCommand(editArgsMatcher, editDetailsMatcher);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }
    
    /** Generates an edit command */
    private Command generateEditCommand(Matcher editArgsMatcher, Matcher editDetailsMatcher)
            throws IllegalValueException {
        
        List<Date> deadline = parseStringToDate(editDetailsMatcher.group("deadline"));
        List<Date> eventStart = parseStringToDate(editDetailsMatcher.group("startDateTime"));
        List<Date> eventEnd = parseStringToDate(editDetailsMatcher.group("endDateTime"));
        
        /* validating integer index */
        Optional<Integer> index = parseIndex(editArgsMatcher.group("targetIndex"));
        if (!index.isPresent()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }
        
        return new EditCommand(
                editDetailsMatcher.group("taskDetails"),
                getTagsFromArgs(editDetailsMatcher.group("tagArguments")),
                deadline,
                eventStart,
                eventEnd,
                editArgsMatcher.group("targetIndex"),
                getPriorityFromArgs(editDetailsMatcher.group("priorityArguments"))
        );
    }
    // @@author
    
    /**
     * Extracts the new task's tags from the add command's tag arguments string.
     * Merges duplicate tag strings.
     */
    private static Set<String> getTagsFromArgs(String tagArguments) throws IllegalValueException {
        // no tags
        if (tagArguments.trim().isEmpty()) {
            return Collections.emptySet();
        }
        // replace first delimiter prefix, then split
        final Collection<String> tagStrings = Arrays.asList(tagArguments.trim().replaceFirst("t/", "").split(" t/"));
        return new HashSet<>(tagStrings);
    }
    
    /**
     * Extracts the new task's priority from the add command's priority arguments string.
     */
    private static String getPriorityFromArgs(String priorityArguments) throws IllegalValueException {
        // no priority
        if (priorityArguments.trim().isEmpty()) {
            return null;   
        }
        // replace first delimiter prefix, then split
        final String priorityString = priorityArguments.trim().replaceFirst("p/", "");
        return priorityString;
    }

    /**
     * Parses arguments in the context of the complete task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareComplete(String args) {
        Optional<Integer> index = parseIndex(args); 
        if (!index.isPresent()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, CompleteCommand.MESSAGE_USAGE));
        }
        
        return new CompleteCommand(args.trim());
    }
    
    /**
     * Parses arguments in the context of the delete task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareDelete(String args) {
        Optional<Integer> index = parseIndex(args); 
        if (!index.isPresent()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }
        
        return new DeleteCommand(args.trim());
    }

    
    /**
     * Parses arguments to filter section of task panel to be displayed to user.
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareShow(String args) {
        final Matcher matcher = SHOW_COMMAND_ARGS_FORMAT.matcher(args.trim());

        
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ShowCommand.MESSAGE_USAGE));
        }
        
        final String sectionToShow = matcher.group("sectionToShow");
        
        try {
            return new ShowCommand(sectionToShow.toLowerCase().trim());
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
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
        if (!StringUtil.isUnsignedInteger(index)) {
            return Optional.empty();
        }
        return Optional.of(Integer.parseInt(index));
        
    }
    
    //@author A0138915X
    /**
     * Parses arguments in the context of the find task command.
     *
     * @param args full command args string
     * @return the prepared command
     *
     */
    private Command prepareFind(String args) {
        final Matcher matcher = KEYWORDS_WITH_DATES_ARGS_FORMAT.matcher(args.trim());
        if (args.trim().isEmpty() || !matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    FindCommand.MESSAGE_USAGE));
        }
        
        Optional<String> optKeywords = Optional.ofNullable(matcher.group("keywords"));
        Set<String> keywordSet = null;
        // keywords delimited by whitespace
        if(optKeywords.isPresent()) {
            final String[] keywords = optKeywords.get().split("\\s+");
            keywordSet = new HashSet<>(Arrays.asList(keywords));
        }
        
        List<Date> startDates = null;
        List<Date> endDates = null;
        List<Date> specificDates = null;
        try {
            specificDates = parseStringToDate(matcher.group("specificDateTime"));
            startDates = parseStringToDate(matcher.group("startDateTime"));
            endDates = parseStringToDate(matcher.group("endDateTime"));
        } catch (DateNotParsableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        logger.info(startDates.toString() + endDates + specificDates);
        
        if(!specificDates.isEmpty()) {
            startDates = specificDates;
        }
        Optional<List<Date>> optEndDate = Optional.ofNullable(endDates);
        
        return new FindCommand(keywordSet, startDates, optEndDate.orElse(null));
    }
    //@@author
    
    // @@author A0140133B
    /**
     * Parses arguments in the context of the save as command.
     * 
     * @param full command args string
     * @return the prepared command
     */
    private Command prepareSaveAs(String args) {
        try {
            final Matcher resetMatcher = SAVE_RESET_DIRECTORY_ARGS_FORMAT.matcher(args.trim());
            if (resetMatcher.matches()) {
                return new SaveAsCommand(Config.DEFAULT_XML_FILE_PATH);
            }
            
            final Matcher matcher = SAVE_DIRECTORY_ARGS_FORMAT.matcher(args.trim());
            if (!matcher.matches()) {
                return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SaveAsCommand.MESSAGE_USAGE));
            }
            
            return new SaveAsCommand(matcher.group("filePath") + XML_FILE_EXTENSION);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ive.getMessage()));
        }
    }

    // @@author
}