package unit;

public class Task implements Comparable<Task>{
    private String url;
    private short priority;

    Task(String u, short p){
        this.url = u;
        this.priority = p;
    }

    public short getPriority() {
        return this.priority;
    }

    public void setPriority(short priority) {
        if (priority >= 0 && priority <=5) this.priority = priority;
        else throw new IllegalArgumentException();
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int compareTo(Task t) {
        return t.getPriority() - this.priority;
    }

    public void increasePriority(){
        switch (this.priority){
            case Priority.Priority_0: {
                this.priority = Priority.Priority_1;
                break;
            }
            case Priority.Priority_1: {
                this.priority = Priority.Priority_2;
                break;
            }
            case Priority.Priority_2: {
                this.priority = Priority.Priority_3;
                break;
            }
            case Priority.Priority_3: {
                this.priority = Priority.Priority_4;
                break;
            }
            case Priority.Priority_4: {
                this.priority = Priority.Priority_5;
                break;
            }
        }
    }

    class Priority {
        public static final short Priority_0 = 0;
        public static final short Priority_1 = 1;
        public static final short Priority_2 = 2;
        public static final short Priority_3 = 3;
        public static final short Priority_4 = 4;
        public static final short Priority_5 = 5;
    }
}
