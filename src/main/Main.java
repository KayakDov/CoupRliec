package main;

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
import tools.Table;

public class Main {

    /**
     * A random non empty polyhedron that contains the given sphere centered at
     * the origin
     *
     * @param numFaces
     * @param radius
     * @param dim
     * @return
     */
    public static List<HalfSpace<Point>> randomNonEmpty(int numFaces, double radius, int dim) {
        Random rand = new Random(3);

        return IntStream.range(0, numFaces).mapToObj(i -> {

            Point random = Point.uniformRandSphereSurface(dim, radius);

            return new HalfSpace<>(random, random);
        }).toList();

    }

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
     * This method should be called for each entry in the table being printed
     *
     * @param numTests how many tests do we wish to run for each entry
     * @param numDim the dimension of the space containing the polyhedrons we
     * wish to test
     * @param numFaces the number of half spaces intersecting to form the
     * polyhedron we wish to test.
     * @param isTime true if we want to know how much time this takes, false if
     * we want to know how many halfspaces are tested
     * @param timeOrFrac true if you want a table of time values, false if you
     * want the fraction of affine spaces computed.
     * @return the table value
     */
    private static double tableVal(int numTests, int numDim, int numFaces, boolean printResults, boolean timeVsFrac) {
        double avg = 0;
        double r = 1;

        for (int i = 0; i < numTests; i++) {
            List<HalfSpace<Point>> poly = randomNonEmpty(numFaces, 1, numDim);

            StrictlyConvexFunction proj = new ProhjectOntoAffine(Point.uniformRandSphereSurface(numDim, r
                    * 10));

            PolyhedralMin<Point> pm = new PolyhedralMin<>(proj, poly);

            if (timeVsFrac) {
                double startTime = System.currentTimeMillis();

                Point aMin = pm.argMin();

                double time = System.currentTimeMillis() - startTime;

                if (printResults) System.out.println("projection is " + aMin);

                avg += (double) time / numTests;

            } else
                avg += (double) pm.fracAffineSpacesChecked() / numTests;

        }
        if (printResults) {
            System.out.println("main.Main.tableVal()");
            System.out.println("time(numFaces, numDim) = (" + numFaces + ", "
                    + numDim + ")");

        }

        if (timeVsFrac) {
            double MILI_PER_SEC = 1000.0;
            avg /= MILI_PER_SEC;
        }

        //round to three significant figures
        BigDecimal bd = new BigDecimal(avg);
        bd = bd.round(new MathContext(3));
        return bd.floatValue();
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
     * @param numTests the number of tests for which each entry is the average 
     * value.  Larger values will take longer to produce more consistent results.
     * @param numDimChecks the total number of dimensions checked. The first 
     * dimension checked will always be 2.
     * @param numConstraintsChecks The number of constraints.  The lowest number of 
     * constraints begins with 1.
     * @param constraintIncrement The increment of the faces checked.  A value of 3
     * would, for example, check 1, 3, 6, 9 ... random linear constraints
     * @param dimIncrement The increment of the number of dimensions.
     * @param timeVsFrac true if you want to print a table of times, false
     * if you want to print a table of the fraction of affine spaces optimized
     * over.
     */
    public static void projectionDataTable(int numTests, int numDimChecks, int numConstraintsChecks, int constraintIncrement, int dimIncrement, boolean timeVsFrac) {
        
        double table[][] = new double[numConstraintsChecks][numDimChecks];

        for (int numFaces = 0; numFaces < numConstraintsChecks; numFaces++)
            for (int numDim = 0; numDim < numDimChecks; numDim++)
                table[numFaces][numDim] = tableVal(numTests, (numDim + 2)
                        * dimIncrement, (numFaces
                        + 1) * constraintIncrement, false, timeVsFrac);

        new Table(15).print(table, buildHeaders(numDimChecks), buildRowNames(numConstraintsChecks, constraintIncrement));
    }

    public static void main(String[] args) throws IOException {
        int numTests = 100, numDimChecks = 5, numConstraintsChecks = 10, 
                constraintIncrement = 5, dimIncrement = 1;
        
        projectionDataTable(numTests, numDimChecks, numConstraintsChecks, constraintIncrement, dimIncrement, true);
        projectionDataTable(numTests, numDimChecks, numConstraintsChecks, constraintIncrement, dimIncrement, false);

//        for(int i = 0; i < 200; i += 5)
//            System.out.println("(" + i + ", " + tableVal(1000, 3, i, false, true) + ")");
//        testCube();
    }

}
