package Testing;

import Convex.LinearRn.ProhjectOntoAffine;
import Hilbert.HalfSpace;
import Hilbert.Optimization.PolyhedralMin;
import Hilbert.StrictlyConvexFunction;
import Matricies.Point;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import tools.Pair;

public class Main {

    public static List<HalfSpace<Point>> square() {
        ArrayList<HalfSpace<Point>> halfspaces = new ArrayList<>();
        halfspaces.add(new HalfSpace<>(new Point(1, 0), new Point(1, 1)));
        halfspaces.add(new HalfSpace<>(new Point(0, 1), new Point(1, 1)));
        halfspaces.add(new HalfSpace<>(new Point(0, -1), new Point(0, 0)));
        halfspaces.add(new HalfSpace<>(new Point(-1, 0), new Point(0, 0)));
        return halfspaces;
    }

    public static List<HalfSpace<Point>> cube() {
        Point ones = new Point(new double[]{1, 1, 1});
        Point origin = new Point(3);
        ArrayList<HalfSpace<Point>> halfspaces = new ArrayList<>();
        halfspaces.add(new HalfSpace<>(new Point(new double[]{1, 0, 0}), ones));
        halfspaces.add(new HalfSpace<>(new Point(new double[]{0, 1, 0}), ones));
        halfspaces.add(new HalfSpace<>(new Point(new double[]{0, 0, 1}), ones));
        halfspaces.add(new HalfSpace<>(new Point(new double[]{-1, 0, 0}), origin));
        halfspaces.add(new HalfSpace<>(new Point(new double[]{0, -1, 0}), origin));
        halfspaces.add(new HalfSpace<>(new Point(new double[]{0, 0, -1}), origin));

        return halfspaces;
    }

    public static void testSquare() {
        Point proj = new Point(6, -8);
        System.out.println(new PolyhedralMin<>(new ProhjectOntoAffine(proj), square()).argMin());
    }

    public static void testCube() {
        Point proj = new Point(new double[]{-6, .37, 12});
        System.out.println(new PolyhedralMin<>(new ProhjectOntoAffine(proj), cube()));
    }

    /**
     * Prints algorithm results, fractions and times.
     */
    public static void printTables() {
        int numTests, numDimChecks, numConstraintsChecks,
                constraintIncrement, dimIncrement;

        new ProjectionTables(
                numTests = 100,
                numDimChecks = 5,
                numConstraintsChecks = 10,
                constraintIncrement = 3,
                dimIncrement = 1
        ).print(true, false);
    }

    public static void main(String[] args) {

        printTables();
//        System.out.println(new ProjectionTest(1, 6, 30).getArgMins().toString());
//        testCube();
    }

}
