package graph;
import graph.vocabularies.EFC;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import java.util.*;

public class Graph {
    private Map<String, Pick> picks = new HashMap< String, Pick >();
    private ArrayList<String> lastResults;

    public void addPick(String id, int wt){
        picks.put(id, new Pick(wt, id));
    }

    public void addFriendsToPick(Model model, String id) {
        Resource person = model.getResource(id);
        StmtIterator iterator = person.listProperties(EFC.friend);
        while (iterator.hasNext()) {
            this.picks.get(id).addLineTo(iterator.next().getString());
        }
    }

    public void createPicksfromLines(String id) {
        Pick pick = this.picks.get(id);
        ArrayList<String> lines = pick.getLines();
        int counter = 0;
        while (counter != lines.size()) {
            if(!(this.picks.containsKey(
                    pick.getLine(counter).getPick())
            )) {
                this.addPick( pick.getLine(counter).getPick(), (pick.getWt() + 1));
                this.picks.get(pick.getLine(counter).getPick()).addLine(id, 0);
            }
            counter++;
        }
    }

    public ArrayList<String> getFriendsRelation(String id1, String id2, Model model) {
        ArrayList<String> results = new ArrayList<>();
        int wt = 1;
        results.add(id1);
        int counter = 0;

        this.addPick(id1, wt);
        this.addFriendsToPick(model, id1);
        this.createPicksfromLines(id1);

        ArrayList<String> recursionList;
        while (checkRecursion(id2) || wt == Integer.MAX_VALUE) {
            counter = 0;
            wt++;
            recursionList = this.GraphRecursion(wt);
            while (recursionList.size() != counter) {
                this.addFriendsToPick(model, recursionList.get(counter));
                this.createPicksfromLines(recursionList.get(counter));
                counter++;
            }
        }

        String lastId = id2;
        while (!lastId.equals(id1)) {
            lastId = this.picks.get(lastId).findLineWith(0);
            if(!lastId.equals(id2)) {
                results.add(lastId);
            }
        }
        results.set(0, id2);

        this.lastResults = results;
        return results;
    }

    private ArrayList<String> GraphRecursion (int wt){
        ArrayList<String> picks = new ArrayList<String>();
        Collection<Pick> list = this.picks.values();
        Iterator<Pick> iterator = list.iterator();
        Pick buff;
        int counter = 0;

        while (iterator.hasNext()) {
            buff = iterator.next();
            if(buff.getWt() == wt) {
                picks.add(buff.getId());
            }
        }
        return picks;

    }

    private boolean checkRecursion (String id2) {
        return !(this.picks.containsKey(id2));
    }

    public void printLastResults() {
        int counter = this.lastResults.size() - 1;
        while (counter != -1) {
            System.out.println(
                    this.lastResults.get(counter));
            counter--;
        }
    }


}
