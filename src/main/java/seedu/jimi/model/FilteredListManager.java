package seedu.jimi.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
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
    
    public FilteredListManager(TaskBook taskBook) {
        // Initializing each list with taskBook's own internal list
        for (ListId id : ListId.values()) {
            listMap.put(id, new FilteredList<ReadOnlyTask>(taskBook.getTasks()));
        }
        
        updateFilteredListToDefault();
    }
    
    public void updateFilteredListToDefault() {
        listMap.get(ListId.FLOATING_TASKS)
                .setPredicate(new PredicateExpression(new FloatingTaskQualifier(false))::satisfies);
        
        listMap.get(ListId.COMPLETED)
                .setPredicate(new PredicateExpression(new CompletedTaskQualifier(true))::satisfies);
        listMap.get(ListId.INCOMPLETE)
                .setPredicate(new PredicateExpression(new CompletedTaskQualifier(false))::satisfies);
        
        listMap.get(ListId.DAY_AHEAD_0)
                .setPredicate(new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_0))::satisfies);
        listMap.get(ListId.DAY_AHEAD_1)
                .setPredicate(new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_1))::satisfies);
        listMap.get(ListId.DAY_AHEAD_2)
                .setPredicate(new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_2))::satisfies);
        listMap.get(ListId.DAY_AHEAD_3)
                .setPredicate(new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_3))::satisfies);
        listMap.get(ListId.DAY_AHEAD_4)
                .setPredicate(new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_4))::satisfies);
        listMap.get(ListId.DAY_AHEAD_5)
                .setPredicate(new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_5))::satisfies);
        listMap.get(ListId.DAY_AHEAD_6)
                .setPredicate(new PredicateExpression(new WeekQualifier(ListId.DAY_AHEAD_6))::satisfies);
        
        listMap.get(ListId.TASKS_AGENDA)
                .setPredicate(new PredicateExpression(new TaskQualifier(true))::satisfies);
        listMap.get(ListId.EVENTS_AGENDA)
                .setPredicate(new PredicateExpression(new EventQualifier(true))::satisfies);
    }
    
    public UnmodifiableObservableList<ReadOnlyTask> getRequiredFilteredTaskList(ListId id) {
        return new UnmodifiableObservableList<>(listMap.get(id));
    }
    
    public void updateRequiredFilteredTaskList(ListId id, Set<String> keywords, String sectionToShow) {
        ArrayList<Qualifier> qualifiersList = new ArrayList<>();
        
        //add the must-have qualifiers for the respective filteredList
        switch(id) {
        case DAY_AHEAD_0:
            qualifiersList.add(new WeekQualifier(ListId.DAY_AHEAD_0));
            break;
        case DAY_AHEAD_1:
            qualifiersList.add(new WeekQualifier(ListId.DAY_AHEAD_1));
            break;
        case DAY_AHEAD_2:
            qualifiersList.add(new WeekQualifier(ListId.DAY_AHEAD_2));
            break;
        case DAY_AHEAD_3:
            qualifiersList.add(new WeekQualifier(ListId.DAY_AHEAD_3));
            break;
        case DAY_AHEAD_4:
            qualifiersList.add(new WeekQualifier(ListId.DAY_AHEAD_4));
            break;
        case DAY_AHEAD_5:
            qualifiersList.add(new WeekQualifier(ListId.DAY_AHEAD_5));
            break;
        case DAY_AHEAD_6:
            qualifiersList.add(new WeekQualifier(ListId.DAY_AHEAD_6));
            break;
        case FLOATING_TASKS:
            qualifiersList.add(new FloatingTaskQualifier(false));
            break;
        case COMPLETED:
            qualifiersList.add(new CompletedTaskQualifier(true));
            break;
        case INCOMPLETE:
            qualifiersList.add(new CompletedTaskQualifier(false));
            break;
        case TASKS_AGENDA:
            qualifiersList.add(new TaskQualifier(true));
            break;
        case EVENTS_AGENDA:
            qualifiersList.add(new EventQualifier(true));
            break;
        }
        
        //if nameQualifer is required
        if(keywords != null && !keywords.isEmpty()) {
            qualifiersList.add(new NameQualifier(keywords));
        }
        
        //if need to show certain list respective to task list panel
        if(sectionToShow != null) {
            switch(sectionToShow.toLowerCase().trim()) {
            case "floating tasks":
                qualifiersList.add(new FloatingTaskQualifier(false));
                break;
            case "completed tasks":
                qualifiersList.add(new CompletedTaskQualifier(true));
                break;
            case "incomplete tasks":
                qualifiersList.add(new CompletedTaskQualifier(false));
                break;
            case "today":
                qualifiersList.add(new WeekQualifier(ListId.DAY_AHEAD_0));
                break;
            case "tomorrow":
                qualifiersList.add(new WeekQualifier(ListId.DAY_AHEAD_1));
                break;
           default: //assume sectionToShow is a day of week
                qualifiersList.add(getRequiredWeekQualifier(sectionToShow));
                break;
            }
        }

        updateFilteredTaskList(id, composePredicates(qualifiersList, 0));
    }

    /**
     * Finds the day to be displayed and calls its respective method to load its
     * respective list.
     * 
     * @param sectionToDisplay
     */
    private WeekQualifier getRequiredWeekQualifier(String sectionToDisplay) {
        DateTime dayNow = new DateTime();
        LocalDateTime dayRequired = dayNow.getLocalDateTime();
        int differenceInDays = 0;
        // find required datetime
        for (int i = 0; i < 7; i++) {
            if (dayRequired.getDayOfWeek().plus(i).toString().toLowerCase().contains(sectionToDisplay)) {
                differenceInDays = i;
            }
        }
        
        ListId li = ListId.DAY_AHEAD_0;
        
        switch(differenceInDays) {
        case 2:
            li = ListId.DAY_AHEAD_2;
            break;
        case 3:
            li = ListId.DAY_AHEAD_3;
            break;
        case 4:
            li = ListId.DAY_AHEAD_4;
            break;
        case 5:
            li = ListId.DAY_AHEAD_5;
            break;
        case 6:
            li = ListId.DAY_AHEAD_6;
            break;
        }
        
        return new WeekQualifier(li);
    }

    private void updateFilteredTaskList(ListId id, Predicate<ReadOnlyTask> expression) {
        listMap.get(id).setPredicate(expression);
    }

    /*
     *  ============================================
     *  Private qualifier classes used for filtering
     *  ============================================
     */
    
    /**
     * Recursively composes predicates and returns the aggregated predicate expression.
     * @param expressions List of constructed Qualifiers to be composed
     * @return Composed predicate expression
     */
    private Predicate<ReadOnlyTask> composePredicates(List<Qualifier> qualifiers, int index) {
        if (index >= qualifiers.size() - 1) {
            Predicate<ReadOnlyTask> p = (new PredicateExpression(qualifiers.get(index)))::satisfies;
            return p;
        } else {
            return PredicateExpressionChainer.and(new PredicateExpression(qualifiers.get(index)),
                    composePredicates(qualifiers, index + 1));
        }
    }

    interface Expression {
        boolean satisfies(ReadOnlyTask task);

        String toString();
    }
    
    /**
     * Composes different predicate expressions together into 1 single expression.
     * @author zexuan
     *
     * @param <T>
     */
    public static class PredicateExpressionChainer {
        public static Predicate<ReadOnlyTask> and(PredicateExpression pe, Predicate<ReadOnlyTask> ex) {
            Predicate<ReadOnlyTask> p = pe::satisfies;
            return p.and(ex);
        }
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
            
            if (isCheckCompleted) {
                return task.isCompleted();
            } else {
                return !task.isCompleted();
            }
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
            if (isCheckCompleted && task.isCompleted()) {
                return false;
            }
            return task instanceof Event;
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
            if (isCheckCompleted && task.isCompleted()) {
                return false;
            }
            
            return !(task instanceof DeadlineTask) 
                    && !(task instanceof Event) 
                    && task instanceof FloatingTask;
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
            if (isCheckCompleted && task.isCompleted()) {
                return false;
            }
            return task instanceof FloatingTask;
        }
    }
}
