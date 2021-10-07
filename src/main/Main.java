package main;

import Convex.RnPolyhedron;
import Hilbert.HalfSpace;
import Matricies.Point;
import Matricies.PointD;
import java.io.IOException;
import java.util.ArrayList;

public class Main {



    public static void main(String[] args) throws IOException {
        ArrayList<HalfSpace<Point>> halfspaces = new ArrayList<>();
        halfspaces.add(new HalfSpace<>(new PointD(1,1), new PointD(1,0)));
        halfspaces.add(new HalfSpace<>(new PointD(1,1), new PointD(0,1)));
        halfspaces.add(new HalfSpace<>(new PointD(0,0), new PointD(0,-1)));
        halfspaces.add(new HalfSpace<>(new PointD(0,0), new PointD(-1,0)));
        RnPolyhedron poly = new RnPolyhedron(halfspaces);
        
        System.out.println(poly.proj(new PointD(4, 7)));
    }

}
