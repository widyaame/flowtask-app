package service;

import model.Task;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TaskFileManager {
    private static final String TASKS_FILE = "tasks.csv";
    private List<Task> tasks;
    private int nextId;
    
    public TaskFileManager() {
        tasks = new ArrayList<>();
        loadTasks();
    }
    
    public void loadTasks() {
        tasks.clear();
        File file = new File(TASKS_FILE);
        if (!file.exists()) {
            initializeTasksFile();
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(TASKS_FILE))) {
            String line = reader.readLine();
            int maxId = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length == 6) {
                    int id = Integer.parseInt(parts[0]);
                    Task task = new Task(id, parts[1], parts[2], parts[3], parts[4], parts[5]);
                    tasks.add(task);
                    if (id > maxId) {
                        maxId = id;
                    }
                }
            }
            nextId = maxId + 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void initializeTasksFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TASKS_FILE))) {
            writer.println("id,title,description,status,assignedTo,createdDate");
        } catch (IOException e) {
            e.printStackTrace();
        }
        nextId = 1;
    }
    
    public void saveTasks() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TASKS_FILE))) {
            writer.println("id,title,description,status,assignedTo,createdDate");
            for (Task task : tasks) {
                writer.println(task.getId() + "," + 
                             task.getTitle() + "," + 
                             task.getDescription() + "," + 
                             task.getStatus() + "," + 
                             task.getAssignedTo() + "," + 
                             task.getCreatedDate());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean addTask(Task task) {
        if (!isValidStatus(task.getStatus())) {
            return false;
        }
        task.setId(nextId++);
        tasks.add(task);
        saveTasks();
        return true;
    }
    
    public boolean updateTask(Task updatedTask) {
        if (!isValidStatus(updatedTask.getStatus())) {
            return false;
        }
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == updatedTask.getId()) {
                tasks.set(i, updatedTask);
                saveTasks();
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteTask(int taskId) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == taskId) {
                tasks.remove(i);
                saveTasks();
                return true;
            }
        }
        return false;
    }
    
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }
    
    public Task getTaskById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }
    
    public List<Task> getTasksByStatus(String status) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getStatus().equals(status)) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }
    
    public List<Task> getTasksByUser(String username) {
        List<Task> userTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getAssignedTo().equals(username)) {
                userTasks.add(task);
            }
        }
        return userTasks;
    }
    
    private boolean isValidStatus(String status) {
        return status.equals("Pending") || status.equals("Progress") || status.equals("Done");
    }
    
    public int countByStatus(String status) {
        int count = 0;
        for (Task task : tasks) {
            if (task.getStatus().equals(status)) {
                count++;
            }
        }
        return count;
    }
}