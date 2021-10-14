package main;

import Convex.RnPolyhedron;
import Hilbert.HalfSpace;
import Matricies.Point;
import Matricies.PointD;
import java.io.IOException;
import java.util.ArrayList;
import tools.Time;

public class Main {

    public static RnPolyhedron square() {
        ArrayList<HalfSpace<Point>> halfspaces = new ArrayList<>();
        halfspaces.add(new HalfSpace<>(new PointD(1, 1), new PointD(1, 0)).setName("right"));
        halfspaces.add(new HalfSpace<>(new PointD(1, 1), new PointD(0, 1)).setName("top"));
        halfspaces.add(new HalfSpace<>(new PointD(0, 0), new PointD(0, -1)).setName("bottom"));
        halfspaces.add(new HalfSpace<>(new PointD(0, 0), new PointD(-1, 0)).setName("left"));
        return new RnPolyhedron(halfspaces);
    }

    public static RnPolyhedron Cube() {
        PointD ones = new PointD(1, 1, 1);
        PointD origin = new PointD(3);
        ArrayList<HalfSpace<Point>> halfspaces = new ArrayList<>();
        halfspaces.add(new HalfSpace<>(ones, new PointD(1, 0, 0)));
        halfspaces.add(new HalfSpace<>(ones, new PointD(0, 1, 0)));
        halfspaces.add(new HalfSpace<>(ones, new PointD(0, 0, 1)));
        halfspaces.add(new HalfSpace<>(origin, new PointD(-1, 0, 0)));
        halfspaces.add(new HalfSpace<>(origin, new PointD(0, -1, 0)));
        halfspaces.add(new HalfSpace<>(origin, new PointD(0, 0, -1)));
        return new RnPolyhedron(halfspaces);
    }

    public static void testSquare() {
        PointD projecting = new PointD(6, -8);
        System.out.println(square().proj(projecting));
    }

    public static void testCube() {
        System.out.println(Cube().proj(new PointD(3, 7, 9)));
    }

    public static void testProjection() {
        int numFaces = 300, numDim = 2, r = 1, numTests = 100;
        
        double totTime = 0;

        for (int i = 0; i < numTests; i++) {
            RnPolyhedron poly = RnPolyhedron.randomNonEmpty(numFaces, 1, numDim);
            PointD randP = PointD.uniformBoundedRand(new PointD(numDim), r*10);
            long time = Time.inMilli(() -> poly.projCoupRliecVar(randP));
            totTime += time / numTests;
        }

        System.out.println(totTime / 1000.0);
    }

    public static void main(String[] args) throws IOException {
//        testCube();
//        testSquare();
        testProjection();

    }

}
