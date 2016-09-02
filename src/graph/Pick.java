package graph;

import java.util.ArrayList;

public class Pick {

    private String id;
    private ArrayList<Line> lines = new ArrayList<Line>();
    private int wt;

    public Pick (int wt, String id) {
        this.wt = wt;
        this.id = id;
    }

    public int getWt () {
        return this.wt;
    }

    public String getId () {
        return this.id;
    }

    public  void addLineTo(String Id) {
        this.lines.add(new Line(Id, this.wt));
    }

    public Line getLine(int number) {
        return lines.get(number);
    }

    public ArrayList<String> getLines () {
        ArrayList<String> list = new ArrayList<String>();

        int counter = 0;
        while (counter != lines.size()) {
            list.add(lines.get(counter).getPick());
            counter++;
        }

        return list;
    }

    public boolean haveLineTo(String Id) {
        boolean result = false;
        int couner = 0;
        while (couner != this.lines.size()) {
            if(
                this.lines.get(couner).getPick() == Id
                    ) {result = true;}
        }

        return result;
    }

    public void addLine(String id, int wt) {
        this.lines.add(new Line(id, wt));
    }

    public String findLineWith(int wt){
        String retStr = null;
        int counter = 0;
        while (counter != this.lines.size()) {
            if(this.lines.get(counter).getWt() == wt) {
                retStr = this.lines.get(counter).getPick();
            }
            counter++;
        }
        return retStr;
    }
}
