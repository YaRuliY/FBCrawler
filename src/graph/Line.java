package graph;

class Line {
    private String toPick;
    private int wt;

    Line(String lineTo, int WT) {
        this.toPick = lineTo;
        this.wt = WT;
    }

    public String getPick() {
        return this.toPick;
    }

    public int getWt() {
        return this.wt;
    }
}
