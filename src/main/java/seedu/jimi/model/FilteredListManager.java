package seedu.jimi.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.jimi.commons.core.LogsCenter;
import seedu.jimi.commons.core.UnmodifiableObservableList;
import seedu.jimi.commons.util.StringUtil;
import seedu.jimi.model.datetime.DateTime;
import seedu.jimi.model.event.Event;
import seedu.jimi.model.task.DeadlineTask;
import seedu.jimi.model.task.FloatingTask;
import seedu.jimi.model.task.ReadOnlyTask;

//@@author A0140133B
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
        EVENTS_AGENDA,
        OVERDUE
    }
    
    private final HashMap<ListId, FilteredList<ReadOnlyTask>> listMap =
            new HashMap<ListId, FilteredList<ReadOnlyTask>>();
    
    private final HashMap<ListId, SortedList<ReadOnlyTask>> sortedListMap =
            new HashMap<ListId, SortedList<ReadOnlyTask>>();
    
    private final HashMap<ListId, Expression> defaultExpressions = new HashMap<ListId, Expression>();
    
    
    public FilteredListManager(TaskBook taskBook) {
        initDefaultExpressions();
        initFilteredLists(taskBook);
        initSortedLists();
    }
    /*
     *  1. Initializing each list with taskBook's own internal list.
     *  2. Setting default filters for each list.
     *  
     *  Adds in CompletedQualifiers when initializing agenda lists.
     */
    private void initFilteredLists(TaskBook taskBook) {
        for (ListId id : ListId.values()) {
            listMap.put(id, new FilteredList<ReadOnlyTask>(taskBook.getTasks()));
            
            if (id.equals(ListId.TASKS_AGENDA)) {
                listMap.get(id).setPredicate(
                        new PredicateExpression(new TaskQualifier(true), new CompletedQualifier(false))::satisfies);
            } else if (id.equals(ListId.EVENTS_AGENDA)) {
                listMap.get(id).setPredicate(
                        new PredicateExpression(new EventQualifier(true), new CompletedQualifier(false))::satisfies);
            } else {
                listMap.get(id).setPredicate(defaultExpressions.get(id)::satisfies);
            }
        }
    }
    
    /**
     * @@author A0138915X
     * 
     * Wraps all the filteredLists with sortedLists.
     */
    private void initSortedLists() {
        for (ListId id : ListId.values()) {
            SortedList<ReadOnlyTask> sortedList = new SortedList<ReadOnlyTask>(listMap.get(id));
            sortedList.comparatorProperty().setValue((new Comparator<ReadOnlyTask>() {
                @Override
                public int compare(ReadOnlyTask arg0, ReadOnlyTask arg1) {
                    if(arg0 instanceof Event) {
                        if(arg1 instanceof Event) {
                            return ((Event) arg0).getStart().compareTo(((Event) arg1).getStart());
                        } else { //return 1, lowest natural ordering
                            return 1;
                        }
                    } else if(arg0 instanceof DeadlineTask) {
                        if(arg1 instanceof DeadlineTask) {
                            return ((DeadlineTask) arg0).getDeadline().compareTo(((DeadlineTask) arg1).getDeadline());
                        } else { //return -1, 2nd lowest natural ordering
                            return 1;
                        }
                    } else if(!(arg1 instanceof DeadlineTask) && !(arg1 instanceof Event)){
                        //compare names of floating tasks
                        return arg0.getName().fullName.compareToIgnoreCase(arg1.getName().fullName);
                    } 
                    
                    return -1;
                }
            }));
            sortedListMap.put(id, sortedList);
        }
    }
    //@@author
    
    // @@author A0140133B
    /**
     * Initializes default expressions used by all the filtered lists in {@code listMap}.
     */
    private void initDefaultExpressions() {
        // Expression matches if it's an incomplete floating task.
        defaultExpressions.put(ListId.FLOATING_TASKS,
                new PredicateExpression(new FloatingTaskQualifier(true), new CompletedQualifier(false)));
        
        // Expression matches if it's a completed non-event.
        defaultExpressions.put(ListId.COMPLETED,
                new PredicateExpression(new EventQualifier(false), new CompletedQualifier(true)));
        // Expression matches if it's an incomplete non-event.
        defaultExpressions.put(ListId.INCOMPLETE,
                new PredicateExpression(new EventQualifier(false), new CompletedQualifier(false)));
        
        // Expressions match if they match the current relative day and are incomplete.
        defaultExpressions.put(ListId.DAY_AHEAD_0,
                new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_0), new CompletedQualifier(false)));
        defaultExpressions.put(ListId.DAY_AHEAD_1,
                new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_1), new CompletedQualifier(false)));
        defaultExpressions.put(ListId.DAY_AHEAD_2,
                new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_2), new CompletedQualifier(false)));
        defaultExpressions.put(ListId.DAY_AHEAD_3,
                new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_3), new CompletedQualifier(false)));
        defaultExpressions.put(ListId.DAY_AHEAD_4,
                new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_4), new CompletedQualifier(false)));
        defaultExpressions.put(ListId.DAY_AHEAD_5,
                new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_5), new CompletedQualifier(false)));
        defaultExpressions.put(ListId.DAY_AHEAD_6,
                new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_6), new CompletedQualifier(false)));
        
        // Expression matches if it's a task.
        defaultExpressions.put(ListId.TASKS_AGENDA,
                new PredicateExpression(new TaskQualifier(true)));
        // Expression matches if it's an event.
        defaultExpressions.put(ListId.EVENTS_AGENDA,
                new PredicateExpression(new EventQualifier(true)));
        
        // Expression matches if task is overdue.
        defaultExpressions.put(ListId.OVERDUE,
                new PredicateExpression(new TaskQualifier(true), new OverdueQualifier()));
    }
    //@@author
    
    /*
     * ===========================================================
     *                  Getters for Sorted-Filtered Lists
     * ===========================================================
     */
    
    public UnmodifiableObservableList<ReadOnlyTask> getSortedFilteredList(ListId id) {
        return new UnmodifiableObservableList<ReadOnlyTask>(sortedListMap.get(id));
    }
    
    /*
     * ===========================================================
     *                  Updating Filtered Lists
     * ===========================================================
     */
    
    /** Updates filters of all filtered lists to default specified in {@code defaultExpressions} */
    public void updateFilteredListsToNormalListing() {
        for (ListId id : ListId.values()) {
            if (id.equals(ListId.TASKS_AGENDA)) {
                listMap.get(id).setPredicate(
                        new PredicateExpression(new TaskQualifier(true), new CompletedQualifier(false))::satisfies);
            } else if (id.equals(ListId.EVENTS_AGENDA)) {
                listMap.get(id).setPredicate(
                        new PredicateExpression(new EventQualifier(true), new CompletedQualifier(false))::satisfies);
            } else {
                listMap.get(id).setPredicate(defaultExpressions.get(id)::satisfies);
            }
        }
    }
    
    /** Updates filtered list identified by {@code id} with keyword filter along with its default filter. */
    public void updateFilteredList(ListId id, Set<String> keywords) {
        updateFilteredList(id, defaultExpressions.get(id), new PredicateExpression(new NameQualifier(keywords)));
    }
    
    // @@author A0138915X
    public void updateFilteredList(ListId id, DateTime fromDate, DateTime toDate) {
        if (toDate == null) {
            updateFilteredList(id, defaultExpressions.get(id), new PredicateExpression(new DateQualifier(fromDate)));
        } else {
            updateFilteredList(id, defaultExpressions.get(id),
                    new PredicateExpression(new DateQualifier(fromDate, toDate)));
        }
    }

    public void updateFilteredList(ListId id, Set<String> keywords, DateTime fromDate, DateTime toDate) {
        if (toDate == null) {
            updateFilteredList(id, defaultExpressions.get(id), new PredicateExpression(new NameQualifier(keywords),
                    new CompletedQualifier(false), new DateQualifier(fromDate)));
        } else {
            updateFilteredList(id, defaultExpressions.get(id), new PredicateExpression(new NameQualifier(keywords),
                    new CompletedQualifier(false), new DateQualifier(fromDate, toDate)));
        }
    }
    //@@author
    
    // @@author A0140133B
    /** 
     * Updates filtered list identified by {@code id} with the filter in {@code other}, along with the original 
     * default filter of list identified by {@code id}.  
     */
    public void updateFilteredList(ListId id, ListId other) {
        updateFilteredList(id, defaultExpressions.get(id), defaultExpressions.get(other));
        
    }
    
    /** 
     * Updates filtered list identified by {@code id} with a filter that matches all filters in {@code expressions}.
     */
    private void updateFilteredList(ListId id, Expression... expressions) {
        listMap.get(id).setPredicate(t -> Arrays.stream(expressions).allMatch(e -> e.satisfies(t)));
    }
    // @@author
    
    
    
    /*
     * ===========================================================
     *        Private qualifier classes used for filtering
     * ===========================================================
     */
    
    interface Expression {
        
        boolean satisfies(ReadOnlyTask task);
        
        String toString();
    }
    
    // @@author A0140133B
    /**
     * Represents a predicate expression that allows for multiple {@code Qualifier} instances.
     * 
     * For this PredicateExpression to satisfy, all qualifiers must pass.
     */
    private class PredicateExpression implements Expression {
        
        private final List<Qualifier> qualifiers;
        
        PredicateExpression(Qualifier... qualifiers) {
            this.qualifiers = Arrays.asList(qualifiers);
        }
        
        @Override
        public boolean satisfies(ReadOnlyTask task) {
            return qualifiers.stream().allMatch(q -> q.run(task));
        }
        
        @Override
        public String toString() {
            return qualifiers.toString();
        }
    }
    // @@author
    
    interface Qualifier {
        
        boolean run(ReadOnlyTask task);
        
        String toString();
    }
    
    private class NameQualifier implements Qualifier {
        private Set<String> nameKeyWords;
        
        public NameQualifier(Set<String> nameKeyWords) {
            this.nameKeyWords = nameKeyWords;
        }
        
        @Override
        public boolean run(ReadOnlyTask task) {
            String[] splitTaskName = task.getName().fullName.split("\\s+");
            return nameKeyWords.stream()
                    .filter(kw -> StringUtil.isNearMatch(kw, splitTaskName))
                    .findAny()
                    .isPresent();
        }
        
        @Override
        public String toString() {
            return "name=" + String.join(", ", nameKeyWords);
        }
    }
    
    /**
     * Checks for tasks/events that fall within a specific date or a range of dates.
     * @author zexuan
     *
     */
    //@@author A0138915X
    private class DateQualifier implements Qualifier {

        DateTime startDate;
        DateTime endDate;
        
        public DateQualifier(DateTime specificDate) {
            this.startDate = specificDate;
        }
        
        public DateQualifier(DateTime fromDate, DateTime toDate) {
            this.startDate = fromDate;
            this.endDate = toDate;
        }
        
        @Override
        public boolean run(ReadOnlyTask task) {
            logger.info(this.startDate.toString());
            
            if(endDate == null && startDate != null) { //if searching for a specific date
                if(task instanceof Event) {
                    return ((Event) task).getStart().getDifferenceInDays(startDate) >= 0
                            && ((Event) task).getEnd().getDifferenceInDays(startDate) <= 0;
                } else if(task instanceof DeadlineTask) {
                    return ((DeadlineTask) task).getDeadline().getDifferenceInDays(startDate) == 0;
                }
            } else if(endDate != null) { //if searching for a range of dates
                if(task instanceof Event) {
                    Event e = (Event) task;
                    return DateTime.isOverLapping(startDate, endDate, e.getStart(), e.getEnd());
                } else if(task instanceof DeadlineTask) {
                    return ((DeadlineTask) task).getDeadline().between(startDate, endDate);
                }
            }
            
            return false; //if floating task
        }
        
    }
    
    /**
     * Qualifier to check if task/event is overdue w.r.t. current time.
     * @author zexuan
     *
     */
    private class OverdueQualifier implements Qualifier {

        @Override
        public boolean run(ReadOnlyTask task) {
            if(task instanceof Event) { //needed?
            } else if(task instanceof DeadlineTask) {
                return LocalDateTime.now().compareTo(((DeadlineTask) task).getDeadline().getLocalDateTime()) >= 0;
            }
            
            return false;
        }
        
    }
    
    //@@author
    
    private class WeekQualifier implements Qualifier {
        private final ListId id;
        
        WeekQualifier(ListId i) {
            id = i;
        }
        
        @Override
        public boolean run(ReadOnlyTask task) {
            
            DayOfWeek currentDay = new DateTime().getLocalDateTime().getDayOfWeek();
            DayOfWeek dayOfWeek = null;
            
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
            
            if (daysDifference >= 0) {
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
    
    private class CompletedQualifier implements Qualifier {
        
        boolean isCheckCompleted;
        
        public CompletedQualifier(boolean isCheckCompleted) {
            this.isCheckCompleted = isCheckCompleted;
        }
        
        @Override
        public boolean run(ReadOnlyTask task) {
            return isCheckCompleted == task.isCompleted();
        }
    }
    
    // @@author A0138915X
    
    /**
     * Predicate for filtering events from the internal list.
     * 
     * @param isMatchingForEvent If true, matches events. Else matches anything that's not an event.
     */
    private class EventQualifier implements Qualifier {
        
        private final boolean isMatchingForEvent;
        
        public EventQualifier(boolean isMatchingForEvent) {
            this.isMatchingForEvent = isMatchingForEvent;
        }
        
        @Override
        public boolean run(ReadOnlyTask task) {
            return isMatchingForEvent == task instanceof Event;
        }
    }
    
    /**
     * Predicate for filtering floatingTasks from the internal list.
     * @param isMatchingForFloating If true, matches floating tasks. Else matches anything that's not a floating task.
     */
    private class FloatingTaskQualifier implements Qualifier {
        
        private final boolean isMatchingForFloating;
        
        public FloatingTaskQualifier(boolean isMatchingForFloating) {
            this.isMatchingForFloating = isMatchingForFloating;
        }
        
        @Override
        public boolean run(ReadOnlyTask task) {
            return isMatchingForFloating == !(task instanceof Event || task instanceof DeadlineTask);
        }
    }
    
    /**
     * Predicate for filtering tasks from the internal list.
     * @param isCheckCompleted If true, matches tasks. Else matches anything that's not a task.
     */
    private class TaskQualifier implements Qualifier {
        
        private final boolean isMatchingForTask;
        
        public TaskQualifier(boolean isMatchingForTask) {
            this.isMatchingForTask = isMatchingForTask;
        }
        
        @Override
        public boolean run(ReadOnlyTask task) {
            return isMatchingForTask == task instanceof FloatingTask;
        }
    }
    // @@author
}
