package seedu.jimi.model.task;

import seedu.jimi.model.tag.UniqueTagList;

public class DeadlineTask extends FloatingTask implements ReadOnlyTask {

    private DateTime deadline;
    
    public DeadlineTask(Name name, DateTime deadline, UniqueTagList tags) {
        super(name, tags);
        assert deadline != null;
        this.deadline = deadline;
    }
    
    public DateTime getDeadline() {
        return deadline;
    }

    @Override
    public String getAsText() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
               .append(getDeadline())
               .append(" Tags: ");
        getTags().forEach(builder::append);
        return builder.toString();
    }
    
    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ReadOnlyTask // instanceof handles nulls
                && this.isSameStateAs((ReadOnlyTask) other));
    }

    @Override
    public String toString() {
        return getAsText();
    }


}