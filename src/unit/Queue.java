package unit;
import model.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Queue {
    private List<Task> tasks = new ArrayList<>();

    public void addTask(String url){
        this.addTask(url, Task.Priority.Priority_1);
    }

    public void addTask(String url, short priority){
        if (this.containsTask(url)) this.getTaskByURL(url).increasePriority();
        else this.tasks.add(new Task(url, priority));
        Collections.sort(this.tasks);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    private boolean containsTask(String url){
        for (Task task : this.tasks) {
            if (task.getUrl().equals(url)) return true;
        }
        return false;
    }

    private Task getTaskByURL(String url){
        for (Task task : this.tasks) {
            if (task.getUrl().equals(url)) return task;
        }
        return null;
    }

    public ArrayList<User> execute(){
        ArrayList<User> users = new ArrayList<>(this.tasks.size());
        Crawler facebook = new Crawler();
        users.addAll(this.tasks.stream().map(t -> facebook.getInfo(t.getUrl())).collect(Collectors.toList()));
        return users;
    }
}