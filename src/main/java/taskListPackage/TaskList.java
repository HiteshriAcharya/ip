package taskListPackage;

import taskPackage.Deadlines;
import taskPackage.Events;
import taskPackage.Task;
import taskPackage.ToDos;
import ui.Ui;
import edithExceptionPackage.ChatBotExceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the list of tasks and performs
 * operations like adding, deleting, and
 * marking tasks as done.
 */
public class TaskList {
    private final List<Task> tasks;
    private final Ui ui;

    /**
     * Constructs a TaskList object with the given list of tasks.
     *
     * @param list The initial list of tasks.
     */
    public TaskList(List<Task> list) {
        tasks = list;
        ui = new Ui();
    }

    /**
     * Retrieves the current list of tasks.
     *
     * @return The list of tasks.
     */
    public List<Task> getList() {
        return tasks;
    }

    /**
     * Deletes the task with the specified task number.
     *
     * @param taskNumber The task number to delete.
     * @throws ChatBotExceptions If there are no tasks to delete
     * or if an invalid task number is provided.
     */
    public void deleteTask(String taskNumber) throws ChatBotExceptions {
        if (tasks.isEmpty()) {
            throw new ChatBotExceptions("No tasks to delete. Task list is empty.");
        }

        try {
            int index = Integer.parseInt(taskNumber) - 1;
            if (index >= 0 && index < tasks.size()) {
                Task removedTask = tasks.remove(index);
                System.out.println("Noted. I've removed this task:");
                System.out.println("   " + removedTask.getStatusIcon() +
                        " " + removedTask.getDescription());
                ui.printFormattedMessage(" Now you have " + tasks.size() + " tasks in the list.");
            } else {
                ui.printFormattedMessage("Invalid task number. Please enter a valid task number.");
            }
        } catch (NumberFormatException e) {
            ui.printFormattedMessage("Invalid command. Please enter a valid task number to delete.");
        }
    }

    /**
     * Adds a new task based on the task description and type.
     *
     * @param taskDescription The description of the task.
     * @param taskType        The type of the task (todo, deadline, event).
     * @throws ChatBotExceptions If the task description or type is invalid.
     */
    public void addTask(String taskDescription, String taskType) throws ChatBotExceptions {
        Task newTask;

        switch (taskType) {
        case "todo":
            newTask = new ToDos(taskDescription, false);
            break;
        case "deadline": {
            String[] parts = taskDescription.split(" by ");
            if (parts.length < 2) {
                throw new ChatBotExceptions("Invalid deadline format. " +
                        "Please use 'deadline <description> by <date>'.");
            }
            String description = parts[0];
            String byDate = parts[1];
            newTask = new Deadlines(description, byDate, false);
            break;
        }
        case "event": {
            String[] parts = taskDescription.split(" from | to ");
            if (parts.length < 3) {
                throw new ChatBotExceptions("Invalid event format. " +
                        "Please use 'event <description> from <start> to <end>'.");
            }
            String description = parts[0];
            String fromDate = parts[1];
            String toDate = parts[2];
            newTask = new Events(description, fromDate, toDate, false);
            break;
        }
        default:
            throw new ChatBotExceptions("Invalid command.");
        }

        tasks.add(newTask);
        System.out.println("Got it. I've added this task:");
        if (newTask instanceof Deadlines) {
            Deadlines deadline = (Deadlines) newTask;
            System.out.println("   " + newTask.getStatusIcon()
                    + " " + deadline.getDescription());
        } else if (newTask instanceof Events) {
            Events event = (Events) newTask;
            System.out.println("   " + newTask.getStatusIcon()
                    + " " + event.getDescription());
        } else {
            System.out.println("   " + newTask.getStatusIcon()
                    + " " + newTask.getDescription());
        }
        ui.printFormattedMessage(" Now you have " + tasks.size() + " tasks in the list.");
    }

    /**
     * Marks the task with the specified task number as done.
     *
     * @param taskNumber The task number to mark as done.
     */
    public void markTaskAsDone(String taskNumber) {
        try {
            int index = Integer.parseInt(taskNumber) - 1;
            if (index >= 0 && index < tasks.size()) {
                Task task = tasks.get(index);
                task.markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                ui.printFormattedMessage("   " + task.getStatusIcon()
                        + " " + task.getDescription());
            } else {
                ui.printFormattedMessage("Invalid task number." +
                        " Please enter a valid task number.");
            }
        } catch (NumberFormatException e) {
            ui.printFormattedMessage("Invalid command." +
                    " Please enter a valid task number to mark as done.");
        }
    }

    /**
     * Marks the task with the specified task number as not done yet.
     *
     * @param taskNumber The task number to mark as not done.
     */
    public void unmarkTaskAsDone(String taskNumber) {
        try {
            int index = Integer.parseInt(taskNumber) - 1;
            if (index >= 0 && index < tasks.size()) {
                Task task = tasks.get(index);
                task.markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
                ui.printFormattedMessage("   " + task.getStatusIcon()
                        + " " + task.getDescription());
            } else {
                ui.printFormattedMessage("Invalid task number. " +
                        "Please enter a valid task number.");
            }
        } catch (NumberFormatException e) {
            ui.printFormattedMessage("Invalid command. " +
                    "Please enter a valid task number to unmark.");
        }
    }

    /**
     * Finds tasks containing the specified keyword and prints them.
     *
     * @param keyword The keyword to search for.
     */
    public void findTasksByKeyword(String keyword) {
        List<Task> matchingTasks = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getDescription().contains(keyword)) {
                matchingTasks.add(task);
            }
        }

        int numberOfMatchingTasks = matchingTasks.size();

        if (numberOfMatchingTasks == 0) {
            ui.printFormattedMessage("No matching tasks found.");
        } else {
            System.out.println("Here are the matching tasks in your list:");
            for (int i = 0; i < matchingTasks.size(); i++) {
                Task task = matchingTasks.get(i);
                int originalIndex = tasks.indexOf(task) + 1;
                System.out.println(originalIndex + ". " + task.toString());
            }
            ui.printFormattedMessage("There are "
                    + numberOfMatchingTasks + " number of tasks found");
        }
    }
}
