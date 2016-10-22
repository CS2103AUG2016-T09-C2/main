package seedu.jimi.model;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import javafx.collections.transformation.FilteredList;
import seedu.jimi.commons.core.LogsCenter;
import seedu.jimi.commons.core.UnmodifiableObservableList;
import seedu.jimi.commons.util.StringUtil;
import seedu.jimi.model.datetime.DateTime;
import seedu.jimi.model.event.Event;
import seedu.jimi.model.task.DeadlineTask;
import seedu.jimi.model.task.FloatingTask;
import seedu.jimi.model.task.ReadOnlyTask;

/**
 * Represents a manager for filtered lists used in the UI component.
 * Respective UI components should already be listeners to each of the lists in {@code listMap}.
 */
public class FilteredListManager {
    private static final Logger logger = LogsCenter.getLogger(FilteredListManager.class);
    
    public enum ListId {
        DAY_AHEAD_0, 
        DAY_AHEAD_1, 
        DAY_AHEAD_2, 
        DAY_AHEAD_3, 
        DAY_AHEAD_4, 
        DAY_AHEAD_5, 
        DAY_AHEAD_6, 
        FLOATING_TASKS, 
        COMPLETED, 
        INCOMPLETE, 
        TASKS_AGENDA, 
        EVENTS_AGENDA
    }
    
    private final HashMap<ListId, FilteredList<ReadOnlyTask>> listMap =
            new HashMap<ListId, FilteredList<ReadOnlyTask>>();
    
    private final HashMap<ListId, Expression> defaultExpressions = new HashMap<ListId, Expression>();
    
    public FilteredListManager(TaskBook taskBook) {
        initDefaultExpressions();
        
        /*
         *  Initializing each list with taskBook's own internal list.
         *  Initializing default filters for each list.
         */
        for (ListId id : ListId.values()) {
            listMap.put(id, new FilteredList<ReadOnlyTask>(taskBook.getTasks()));
            listMap.get(id).setPredicate(defaultExpressions.get(id)::satisfies);
        }
    }
    
    private void initDefaultExpressions() {
        defaultExpressions.put(
                ListId.FLOATING_TASKS, new PredicateExpression(new FloatingTaskQualifier(false)));
        defaultExpressions.put(
                ListId.COMPLETED, new PredicateExpression(new CompletedTaskQualifier(true)));
        defaultExpressions.put(
                ListId.INCOMPLETE, new PredicateExpression(new CompletedTaskQualifier(false)));
        
        defaultExpressions.put(
                ListId.DAY_AHEAD_0, new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_0)));
        defaultExpressions.put(
                ListId.DAY_AHEAD_1, new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_1)));
        defaultExpressions.put(
                ListId.DAY_AHEAD_2, new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_2)));
        defaultExpressions.put(
                ListId.DAY_AHEAD_3, new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_3)));
        defaultExpressions.put(
                ListId.DAY_AHEAD_4, new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_4)));
        defaultExpressions.put(
                ListId.DAY_AHEAD_5, new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_5)));
        defaultExpressions.put(
                ListId.DAY_AHEAD_6, new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_6)));
        
        defaultExpressions.put(
                ListId.TASKS_AGENDA, new PredicateExpression(new TaskQualifier(false)));
        defaultExpressions.put(
                ListId.EVENTS_AGENDA, new PredicateExpression(new EventQualifier(false)));
    }
    
    /*
     * ===========================================================
     *                  Getters for Filtered Lists
     * ===========================================================
     */
    
    public UnmodifiableObservableList<ReadOnlyTask> getFilteredList(ListId id) {
        return new UnmodifiableObservableList<ReadOnlyTask>(listMap.get(id));
    }
    
    
    /*
     * ===========================================================
     *                  Updating Filtered Lists
     * ===========================================================
     */
    
    public void updateFilteredListToDefault() {
        listMap.get(ListId.TASKS_AGENDA)
                .setPredicate(defaultExpressions.get(ListId.TASKS_AGENDA)::satisfies);
        listMap.get(ListId.EVENTS_AGENDA)
                .setPredicate(defaultExpressions.get(ListId.EVENTS_AGENDA)::satisfies);
    }
    
    public void updateFilteredList(ListId id, Set<String> keywords, ListId sectionToShow) {
        PredicateExpression nameQualifier = new PredicateExpression(new NameQualifier(keywords));
        
        if(keywords != null && !keywords.isEmpty()) {
            updateFilteredList(id, nameQualifier, 
                    defaultExpressions.get(sectionToShow));
        }
        
        updateFilteredList(id, defaultExpressions.get(sectionToShow));
    }
    
    /** 
     * Updates filtered list with a filter that matches the default filter 
     * along with all predicate expressions in {@code expressions}.
     * @author Clarence 
     */
    private void updateFilteredList(ListId id, Expression... expressions) {
        Expression defaultExp = defaultExpressions.get(id);
        listMap.get(id).setPredicate(
                t -> defaultExp.satisfies(t) && Arrays.stream(expressions).allMatch(e -> e.satisfies(t)));
    }
    
    /*
     * ===========================================================
     *        Private qualifier classes used for filtering
     * ===========================================================
     */
    
    interface Expression {
        
        boolean satisfies(ReadOnlyTask task);
        
        String toString();
        
    }
    
    private class PredicateExpression implements Expression {
        
        private final Qualifier qualifier;
        
        PredicateExpression(Qualifier qualifier) {
            this.qualifier = qualifier;
        }
        
        @Override
        public boolean satisfies(ReadOnlyTask task) {
            return qualifier.run(task);
        }
        
        @Override
        public String toString() {
            return qualifier.toString();
        }
    }
    
    interface Qualifier {
        boolean run(ReadOnlyTask task);
        
        String toString();
    }
    
    private class NameQualifier implements Qualifier {
        private Set<String> nameKeyWords;
        
        NameQualifier(Set<String> nameKeyWords) {
            this.nameKeyWords = nameKeyWords;
        }
        
        @Override
        public boolean run(ReadOnlyTask task) {
            return nameKeyWords.stream()
                    .filter(keyword -> StringUtil.containsIgnoreCase(task.getName().fullName, keyword))
                    .findAny()
                    .isPresent();
        }
        
        @Override
        public String toString() {
            return "name=" + String.join(", ", nameKeyWords);
        }
    }
    
    private class WeekQualifier implements Qualifier {
        private ListId id;
        private DayOfWeek dayOfWeek;
        private DayOfWeek currentDay;
        
        WeekQualifier(ListId i) {
            id = i;
        }
        
        @Override
        public boolean run(ReadOnlyTask task) {
            if (task.isCompleted()) {
                return false;
            }
            
            currentDay = new DateTime().getLocalDateTime().getDayOfWeek();
            
            // dynamically set the day that each list corresponds to
            switch (id) {
            case DAY_AHEAD_0 :
                dayOfWeek = currentDay;
                break;
            case DAY_AHEAD_1 :
                dayOfWeek = currentDay.plus(1);
                break;
            case DAY_AHEAD_2 :
                dayOfWeek = currentDay.plus(2);
                break;
            case DAY_AHEAD_3 :
                dayOfWeek = currentDay.plus(3);
                break;
            case DAY_AHEAD_4 :
                dayOfWeek = currentDay.plus(4);
                break;
            case DAY_AHEAD_5 :
                dayOfWeek = currentDay.plus(5);
                break;
            case DAY_AHEAD_6 :
                dayOfWeek = currentDay.plus(6);
                break;
            default :
                break;
            }
            
            if (task instanceof DeadlineTask) {
                return isTaskSameWeekDate((DeadlineTask) task, dayOfWeek);
            } else if (task instanceof Event) {
                return isEventSameWeekDate((Event) task, dayOfWeek);
            } else {
                return false;
            }
        }
        
        /**
         * Checks if the task is at most 1 week ahead of current time.
         */
        private boolean isTaskSameWeekDate(DeadlineTask task, DayOfWeek day) {
            long daysDifference = new DateTime().getDifferenceInDays(task.getDeadline());
            
            if (daysDifference > 0) {
                return task.getDeadline().getLocalDateTime().getDayOfWeek().getValue() == day.getValue(); // check if day of the week
            }
            
            return false;
        }
        
        /**
         * Checks if the event is at most 1 week ahead of current time or is
         * occuring now.
         */
        private boolean isEventSameWeekDate(Event event, DayOfWeek day) {
            long daysDifference = new DateTime().getDifferenceInDays(event.getStart());
            
            // checks if event is not within only at most a week ahead
            if (!(daysDifference >= 0 && daysDifference <= 7)) {
                return false;
            }
            
            int eventStartDay = event.getStart().getLocalDateTime().getDayOfWeek().getValue();
            DateTime eventEndDate = event.getEnd();
            Optional<DateTime> ed = Optional.ofNullable(eventEndDate);
            
            logger.info("Checking event: " + day + " " + daysDifference);
            
            if (ed.isPresent()) {
                Integer eventEndDay = ed.get().getLocalDateTime().getDayOfWeek().getValue();
                
                Optional<Integer> endDay = Optional.ofNullable(eventEndDay);
                
                return day.getValue() >= eventStartDay && day.getValue() <= endDay.orElse(0);
            } else {
                return day.getValue() == eventStartDay;
            }
        }
    }
    
    private class CompletedTaskQualifier implements Qualifier {
        
        boolean isCheckCompleted;
        
        public CompletedTaskQualifier(boolean isCheckCompleted) {
            this.isCheckCompleted = isCheckCompleted;
        }
        
        @Override
        public boolean run(ReadOnlyTask task) {
            if (task instanceof Event) {
                return false;
            }
            return isCheckCompleted == task.isCompleted();
        }
    }
    
    /**
     * Predicate for filtering events from the internal list.
     * @author zexuan
     * @param isCheckCompleted If true, checks for event completion as well.
     */
    private class EventQualifier implements Qualifier {
        
        boolean isCheckCompleted;
        
        public EventQualifier(boolean isCheckCompleted) {
            this.isCheckCompleted = isCheckCompleted;
        }
        
        @Override
        public boolean run(ReadOnlyTask task) {
            if (!(task instanceof Event)) {
                return false;
            }
            return isCheckCompleted == task.isCompleted();
        }
    }
    
    /**
     * Predicate for filtering floatingTasks from the internal list.
     * @author zexuan
     * @param isCheckCompleted If true, checks for task completion as well.
     */
    private class FloatingTaskQualifier implements Qualifier {
        
        boolean isCheckCompleted;
        
        public FloatingTaskQualifier(boolean isCheckCompleted) {
            this.isCheckCompleted = isCheckCompleted;
        }
        
        @Override
        public boolean run(ReadOnlyTask task) {
            if (task instanceof Event || task instanceof DeadlineTask) {
                return false;
            }
            return isCheckCompleted == task.isCompleted();
        }
    }
    
    /**
     * Predicate for filtering tasks from the internal list.
     * @author zexuan
     * @param isCheckCompleted If true, checks for task completion as well.
     */
    private class TaskQualifier implements Qualifier {
        
        boolean isCheckCompleted;
        
        public TaskQualifier(boolean isCheckCompleted) {
            this.isCheckCompleted = isCheckCompleted;
        }
        
        @Override
        public boolean run(ReadOnlyTask task) {
            if (!(task instanceof FloatingTask)) {
                return false;
            }
            return isCheckCompleted == task.isCompleted();
        }
    }
}
