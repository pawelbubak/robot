package pl.symulacja.robota.dane;

import pl.symulacja.robota.dane.Point;

public class Wektor {
    private Point start;
    private Point end; //strzałka

    public Wektor() {
    }

    public Wektor(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Wektor{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (((Wektor)obj).getStart() != null && ((Wektor)obj).getEnd() != null)
            return start.equals(((Wektor)obj).getStart()) && end.equals(((Wektor)obj).getEnd());
        if (((Wektor)obj).getStart() == null )
            return end.equals(((Wektor)obj).getEnd());
        if (((Wektor)obj).getEnd() == null)
            return start.equals(((Wektor)obj).getStart());
        return false;
    }
}
