Jimi Manual Test Script

1. Loading Sample Data<br>

1.1 Overwriting default data file with Sample Data:<br>
1.1.1 Rename the SampleData.xml to jimi.xml<br>
1.1.2 Navigate to where Jimi.jar executable is.<br>
1.1.3 Create a folder named `data` if not created already.<br>
1.1.4 Copy the renamed jimi.xml into the data folder.<br>
1.1.5 Run Jimi.jar; All the sample tasks and events should be loaded.<br>

2. Add Command<br>

2.1 Adding a Floating Task:<br>
2.1.1 Type into the command box: `add "America tour"`<br>
2.1.2 Task should be added to agenda task panel in main window; Added task details should be shown in result display window; Task should be highlighted.<br>

2.2 Adding a Deadline Task:<br>
2.2.1 Type into the command box: `add "finish project" due next tuesday`<br>
2.2.2 Task should be added to agenda task panel in main window; Added task details should be shown in result display window; Task should be highlighted.<br>

2.3 Adding Events:<br>
2.3.1 Type into the command box: `add "visit grandma" from next monday to next tuesday`<br>
2.3.2 Event should be added to the agenda event panel in main window; Event should be highlighted;  Added event details should be shown in result display window;<br>
2.3.3 Type into the command box: `add "attend seminar" on today 4pm`<br>
2.3.4 Event should be added to the agenda event panel in main window; Event shoudld be highlighted;  Added event details should be shown in result display window; End date of event should be today 23:59.<br>

2.4 Adding Floating Tasks with tags:<br>
2.4.1 Type into the command box: `add "very important" t/Important`<br>
2.4.2 Task should be added to agenda task panel in main window, tagged; Added task details should be shown in result display window; Task should be highlighted.<br>

2.5 Adding Deadline Tasks with priority:<br>
2.5.1 Type into the command box: `add "high priority" due tomorrow p/HIGH`<br>
2.5.2 Task should be added to agenda task panel in main window in red; Added task details should be shown in result display window; Task should be highlighted.<br>

3. Delete Command<br>

3.1 Deleting a single task:<br>
3.1.1 Type into the command box: `delete t33`<br>
3.1.2 Task should be deleted from task panel in main window; Result message in result display window should show details of deleted task;<br>

3.2 Deleting a range of tasks:<br>
3.2.1 Type into the command box: `delete t34 to t36`<br>
3.2.2 Tasks should be deleted from task panel in main window; Result message in result display window should show details of all deleted tasks;<br>

3.3 Deleting a single event:<br>
3.3.1 Type into the command box: `delete e8`<br>
3.3.2 Event should be deleted from event panel in main window; Result message in result display window should show details of deleted event;<br>

3.4 Deleting a range of events:<br>
3.4.1 Type into the command box: `delete e8 to e11`<br>
3.4.2 Events should be deleted from task panel in main window; Result message in result display window should show details of all deleted events;<br>

4. Edit Command<br>

4.1 Editing task name only:<br>
4.1.1 Type into the command box: `edit t32 "this is updated"`<br>
4.1.2 Edited task should be highlighted; Changes to task name are reflected; Result display should show new updated task details;<br>

4.2 Editing event date only:<br>
4.2.1 Type into the command box: `edit e8 from today to next year`<br>
4.2.2 Edited event should be highlighted; Changes to event dates are reflected; Result display should show new updated event details;<br>

4.3 Editing task tags and priority:<br>
4.3.1 Type into the command box: `edit t31 t/CHANGED p/HIGH`<br>
4.3.2 Edited task should be highlighted; Changes to task tags and priority should be reflected; Result display should show new updated event details;<br>

4.4 Converting event to floating task:<br>
4.4.1 Type into the command box: `edit e9 dateless`<br>
4.4.2 Edited task should be highlighted; Task should now have no dates; Result display should show new updated task details;

4.5 Removing tags from task:<br>
4.5.1 Type into the command box: `edit t13 tagless`<br>
4.5.2 Edited task should be highlighted; Task should now have tags removed;
Result display should show new updated task details;<br>

4.6 Setting a task to no priority:<br>
4.6.1 Type into the command box: `edit t15 p/none`<br>
4.6.2 Edited task should be highlighted; Task should now be in white colour, indicating no priority; Result display should show new updated task details;<br>

4.7 Converting a floating task to deadline task:<br>\
4.7.1 Type into the command box: `edit t15 due next year`
4.7.2 Edited task should be highlighted; Task should now have a deadline set; Result display should show new updated task details;<br>

4.8 Converting a deadline task to event:<br>
4.8.1 Type into the command box: `edit t34 from today to tmr`<br>
4.8.2 Converted event should be highlighted; Event should be reflected in the events list; Result display should show new updated event details;<br>

4.9 Converting an event to deadline task:<br>
4.9.1 Type into the command box: `edit e10 due tomorrow`<br>
4.9.2 Converted task should be highlighted; Task should be reflected in task list; Result display should show new updated task details;<br>

5. Complete Command<br>

5.1 Completing a task:<br>
5.1.1 Type into the command box: `complete t30`<br>
5.1.2 Task should be completed; All completed tasks should be shown to user; Result display window should show completed task details;<br>

6. Show Command<br>

6.1 Showing all tasks and events:<br>
6.1.1 Type into the command box: `show all`<br>
6.1.2 All events and tasks should be displayed in the main window;<br>

6.2 Showing overdue tasks:<br>
6.2.1 Type into the command box: `show overdue`<br>
6.2.2 All overdue tasks should be displayed in the main window; Overdue tasks in summary panel should be expanded;<br>

6.3 Showing incomplete tasks:<br>
6.3.1 Type into the command box: `show incomplete`<br>
6.3.2 All incomplete tasks should be displayed in the main window; Incomplete tasks in summary panel should be expanded;<br>

6.4 Showing completed tasks:<br>
6.4.1 Type into the command box: `show completed`<br>
6.4.2 All completed tasks should be displayed in the main window; Completed tasks in summary panel should be expanded;<br>

6.5 Showing day of week tasks/events:<br>
6.5.1 Type into the command box: `show today`<br>
6.5.2 All tasks and events for today should be displayed to user; Today list in summary panel should be expanded;<br>
6.5.3 Type into the command box: `show tomorrow`<br>
6.5.4 All tasks and events for tomorrow should be displayed to user; Tomorrow list in summary panel should be expanded;<br>
6.5.5 Type into the command box: `show saturday`<br>
6.5.6 All tasks and events for saturday should be displayed to user; Saturday list in summary panel should be expanded;<br>
6.5.7 Type into the command box: `show sunday`<br>
6.5.8 All tasks and events for sunday should be displayed to user; Sunday list in summary panel should be expanded;<br>
6.5.9 Type into the command box: `show monday`<br>
6.5.10 All tasks and events for monday should be displayed to user; Monday list in summary panel should be expanded;<br>
6.5.11 Type into the command box: `show tuesday`<br>
6.5.12 All tasks and events for tuesday should be displayed to user; Tuesday list in summary panel should be expanded;<br>
6.5.13 Type into the command box: `show wednesday`<br>
6.5.14 All tasks and events for wednesday should be displayed to user; Wednesday list in summary panel should be expanded;<br>

7. Find Command<br>

7.1 Find by keyword only:<br>
7.1.1 Type into command box: `find "merica"`<br>
7.1.2 All tasks and events whose names nearly match the keyword will be displayed; Result display should show correct total number of tasks and events;<br>

7.2 Find by single date only:<br>
7.2.1 Type into the command box: `find on today`
7.2.2 All tasks and events whose dates matches today's date will be displayed; Result display should show correct total number of tasks and events;<br>

7.3 Find by range of dates:<br>
7.3.1 Type into the command box: `find from tomorrow to next week`
7.3.2 All tasks and events whose dates fall into the range of dates specified will be displayed; Result display should show correct total number of tasks and events;<br>

7.4 Find by keyword and range of dates:<br>
7.4.1 Type into the command box: `find "project" from today to next year`
7.4.2 All tasks and events whose name nearly matches the keyword and dates fall into the range of dates specified will be displayed; Result display should show correct total number of tasks and events;<br>

7.5 Find by keywords and single date:<br>
7.5.1 Type into the command box: `find "project" on next tuesday`
7.5.2 All tasks and events whose names nearly match the keyword and match the exact date will be displayed; Result display should show correct total number of tasks and events;<br>

8. Clear Command<br>

8.1 Clear all tasks and events:<br>
8.1.1 Type into the command box: `clear`<br>
8.1.2 All tasks and events should be removed from main window; Result display should show all tasks/events cleared message;<br>

9. Undo Command<br>

9.1 Undo-ing previous action (clear):<br>
9.1.1 Type into the command box: `undo`<br>
9.1.2 All cleared tasks and events should be returned to the main window; Result display should display undo successful message;<br>

10. Redo Command<br>

10.1 Redo-ing previous action (clear):<br>
10.1.1 Type into the command box: `redo`<br>
10.1.2 All tasks and events should be cleared from main window; Result display should display redo successful message;<br>
10.1.3 Type into the command box: `redo`<br>
10.1.4 Result window should display no-actions to be re-done message;<br>

11. Saveas Command<br>

11.1 Setting new save path location:<br>
11.1.1 Type into the command box: `saveas "dropbox/jimi.xml"`<br>
11.1.2 Result window should display successful message with new path location; Status bar footer should reflect new data save location;<br>

11.2 Resetting save path location:<br>
11.2.1 Type into the command box: `saveas reset`<br>
11.2.2 Result window should display successful message with default path location; Status bar footer should reflect default data save location;<br>

12. Help Command<br>

12.1 Opening help window:<br>
12.1.1 Type into the command box: `help`<br>
12.1.2 Help window should open up local copy of user guide.<br>

12.2 Showing help for specific command:<br>
12.2.1 Type into the command box: `help COMMAND` eg. help add<br>
12.2.2 Result window should display command format and usage instructions for that command.<br>

13. Exit Command<br>

13.1 Type into the command box: `exit`<br>
13.2 Jimi should close.<br>