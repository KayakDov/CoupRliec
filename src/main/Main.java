package main;


import Convex.LinearRn.RnAffineProjection;
import Hilbert.HalfSpace;
import Hilbert.Optimization.polyhedralMin;
import Hilbert.StrictlyConvexFunction;
import Matricies.Point;
import Matricies.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import tools.Table;

public class Main {
    
    
    /**
     * A random non empty polytope that contains the given sphere centered at the origin
     * @param numFaces
     * @param radius
     * @param dim
     * @return 
     */
    public static List<HalfSpace<Point>> randomNonEmpty(int numFaces, double radius, int dim) {
        Random rand = new Random(3);

        return IntStream.range(0, numFaces).mapToObj(i -> {

            Point random = Point.uniformBoundedRand(new Point(dim), radius);

            random = random.mult(radius * (rand.nextDouble() + 1) / random.magnitude());

            return new HalfSpace<>(random, random);
        }).toList();

    }


    public static List<HalfSpace<Point>> square() {
        ArrayList<HalfSpace<Point>> halfspaces = new ArrayList<>();
        halfspaces.add(new HalfSpace<>(new Point(1, 0), new Point(1, 1)).setName("right"));
        halfspaces.add(new HalfSpace<>(new Point(0, 1), new Point(1, 1)).setName("top"));
        halfspaces.add(new HalfSpace<>(new Point(0, -1), new Point(0, 0)).setName("bottom"));
        halfspaces.add(new HalfSpace<>(new Point(-1, 0), new Point(0, 0)).setName("left"));
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
        System.out.println(new polyhedralMin<>(new RnAffineProjection(proj), square()).argMin());
    }

    public static void testCube() {
        Point proj = new Point(new double[]{.5, .1, -9});
        System.out.println(new polyhedralMin<>(new RnAffineProjection(proj), cube()));
    }

    /**
     * This method should be called for each entry in the table being printed
     *
     * @param numTests how many tests do we wish to run for each entry
     * @param numDim the dimension of the space containing the polyhedrons we
     * wish to test
     * @param numFaces the number of half spaces intersecting to form the
     * polyhedron we wish to test.
     * @param isTime true if we want to know how much time this takes, false if
     * we want to know how many halfspaces are tested
     * @return the table value
     */
    private static double tableVal(int numTests, int numDim, int numFaces, boolean printResults) {
        double avg = 0;
        double r = 1;
        
        

        for (int i = 0; i < numTests; i++) {
            List<HalfSpace<Point>> poly = randomNonEmpty(numFaces, 1, numDim);
            
            StrictlyConvexFunction proj = new RnAffineProjection(Point.uniformBoundedRand(new Point(numDim), r * 10));

            double startTime = System.currentTimeMillis();

            if (printResults) System.out.println("projection is "
                        + new polyhedralMin<>(proj, poly));
            else new polyhedralMin<>(proj, poly);

            double time = System.currentTimeMillis() - startTime;

            avg += (double) time / numTests;
        }
        System.out.println("main.Main.tableVal()");
        System.out.println("time(numFaces, numDim) = (" + numFaces + ", "
                + numDim + ")");
        return avg / 1000.0;
    }

    private static String[] buildHeaders(int numCols) {
        String[] headers = new String[numCols];
        for (int i = 0; i < numCols; i++) {
            headers[i] = (i + 2) + " dim";
        }
        return headers;
    }

    private static String[] buildRowNames(int numRows, int increment) {
        String[] rowNames = new String[numRows];
        rowNames[0] = increment + " hs";
        for (int i = 1; i < numRows; i++)
            rowNames[i] = (i + 1) * increment + " hs";
        return rowNames;
    }

    /**
     * prints a table
     */
    public static void testProjection() {
        int numTests = 100;

        int numDimChecks = 5, numFaceChecks = 10, faceIncrement = 5, dimIncrement = 1;

        double table[][] = new double[numFaceChecks][numDimChecks];

        for (int numFaces = 0; numFaces < numFaceChecks; numFaces++)
            for (int numDim = 0; numDim < numDimChecks; numDim++)
                table[numFaces][numDim] = tableVal(numTests, (numDim + 2)
                        * dimIncrement, (numFaces
                        + 1) * faceIncrement, false);

        new Table(15).print(table, buildHeaders(numDimChecks), buildRowNames(numFaceChecks, faceIncrement));
    }

    public static void main(String[] args) throws IOException {
        testProjection();

//        System.out.println(tableVal(100, 6, 30, false, false));//(6,30) -> ~.07 without "acceleration"
//        testCube();
    }

}
