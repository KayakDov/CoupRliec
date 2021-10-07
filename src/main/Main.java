package main;

import Convex.RnPolyhedron;
import Hilbert.HalfSpace;
import Hilbert.Polyhedron;
import Matricies.Point;
import Matricies.PointD;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {


    public static RnPolyhedron square(){
        ArrayList<HalfSpace<Point>> halfspaces = new ArrayList<>();
        halfspaces.add(new HalfSpace<>(new PointD(1,1), new PointD(1,0)));
        halfspaces.add(new HalfSpace<>(new PointD(1,1), new PointD(0,1)));
        halfspaces.add(new HalfSpace<>(new PointD(0,0), new PointD(0,-1)));
        halfspaces.add(new HalfSpace<>(new PointD(0,0), new PointD(-1,0)));
        return new RnPolyhedron(halfspaces);
    }
    

    public static void main(String[] args) throws IOException {
        
        System.out.println(square().proj(new PointD(4, 7)));
    }

}
