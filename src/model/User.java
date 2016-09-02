package model;
import java.util.ArrayList;
import java.util.Date;

public class User {
    public int id;
    public String ref;
    public String name;
    public String secondName;
    public Date birthday;
    public int friendsCount;
    public int followersCount;
    public String birthPlace;
    public String location;
    public String currentWork;
    public ArrayList<String> previousWorks = new ArrayList<>();
    public ArrayList<String> friendsList = new ArrayList<>();
    public ArrayList<String> education = new ArrayList<>();
    public ArrayList<String> userLikes = new ArrayList<>();
    public Date lastSearchDate;

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("--------------User-Info--------------\n")
                .append("ID: ").append(id).append("\n")
                .append("URL: ").append(ref).append("\n")
                .append("Name: ").append(name).append("\n")
                .append("Second Name: ").append(secondName).append("\n")
                .append("Birth Date: ").append(birthday).append("\n")
                .append("Count of Friends: ").append(friendsCount).append("\n")
                .append("Count of Followers: ").append(followersCount).append("\n")
                .append("Birth Place: ").append(birthPlace).append("\n")
                .append("Location: ").append(location).append("\n")
                .append("Current Work: ").append(currentWork).append("\n");
        sb.append("--------------Previous-Work---------------\n");
        for (String s : previousWorks) {
            sb.append(s).append("\n");
        }
        sb.append("--------------Education----------------\n");
        for (String s : education) {
            sb.append(s).append("\n");
        }
        sb.append("--------------Friend-List-[").append(friendsList.size()).append("]-----------\n");
        for (String s : friendsList) {
            sb.append(s).append("\n");
        }
        sb.append("--------------Liked-[").append(friendsList.size()).append("]-----------\n");
        for (String s : userLikes) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }
}
