package main;

import Convex.LinearRn.RnAffineProjection;
import Convex.RnPolyhedron;
import Hilbert.HalfSpace;
import Hilbert.Optimization.CoupRliec;
import Matricies.Point;
import Matricies.PointD;
import java.io.IOException;
import java.util.ArrayList;
import tools.Table;
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
        PointD projecting = new PointD(.2, -3);
        System.out.println(square().projCoupRliecPointMethod(projecting));
    }

    public static void testCube() {
        System.out.println(Cube().projCoupRliecPointMethod(new PointD(-3, 7, .5)));
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
    private static double tableVal(int numTests, int numDim, int numFaces, boolean isTime) {
        double avg = 0;
        double r = 1;

        for (int i = 0; i < numTests; i++) {
            RnPolyhedron poly = RnPolyhedron.randomNonEmpty(numFaces, 1, numDim);
            PointD randP = PointD.uniformBoundedRand(new PointD(numDim), r * 10);
                    
            double startTime = System.currentTimeMillis();
            poly.projCoupRleic(randP);
            double time = System.currentTimeMillis() - startTime;
            
            avg += time / numTests;
        }
        double tableVal = avg / 1000.0;
//        System.out.println("dim " + numDim + " faces " + numFaces + " tableVal "
//                + tableVal);
        return tableVal;
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
        boolean isTime = false;

        int numDimChecks = 5, numFaceChecks = 10, faceIncrement = 3;

        double table[][] = new double[numFaceChecks][numDimChecks];

        for (int numFaces = 0; numFaces < numFaceChecks; numFaces++)
            for (int numDim = 0; numDim < numDimChecks; numDim++)
                table[numFaces][numDim] = tableVal(numTests, numDim + 2, (numFaces
                        + 1) * faceIncrement, isTime);

        new Table(15).print(table, buildHeaders(numDimChecks), buildRowNames(numFaceChecks, faceIncrement));
    }

    public static void main(String[] args) throws IOException {
        testProjection();
//        testCube();
//        testSquare();

    }

}
