package main;

import Convex.RnPolyhedron;
import Hilbert.HalfSpace;
import Matricies.Point;
import Matricies.Point;
import java.io.IOException;
import java.util.ArrayList;
import tools.Table;

public class Main {

    public static RnPolyhedron square() {
        ArrayList<HalfSpace<Point>> halfspaces = new ArrayList<>();
        halfspaces.add(new HalfSpace<>(new Point(1, 0), new Point(1, 1)).setName("right"));
        halfspaces.add(new HalfSpace<>(new Point(0, 1), new Point(1, 1)).setName("top"));
        halfspaces.add(new HalfSpace<>(new Point(0, -1), new Point(0, 0)).setName("bottom"));
        halfspaces.add(new HalfSpace<>(new Point(-1, 0), new Point(0, 0)).setName("left"));
        return new RnPolyhedron(halfspaces);
    }

    public static RnPolyhedron Cube() {
        Point ones = new Point(new double[]{1, 1, 1});
        Point origin = new Point(3);
        ArrayList<HalfSpace<Point>> halfspaces = new ArrayList<>();
        halfspaces.add(new HalfSpace<>(new Point(new double[]{1, 0, 0}), ones));
        halfspaces.add(new HalfSpace<>(new Point(new double[]{0, 1, 0}), ones));
        halfspaces.add(new HalfSpace<>(new Point(new double[]{0, 0, 1}), ones));
        halfspaces.add(new HalfSpace<>(new Point(new double[]{-1, 0, 0}), origin));
        halfspaces.add(new HalfSpace<>(new Point(new double[]{0, -1, 0}), origin));
        halfspaces.add(new HalfSpace<>(new Point(new double[]{0, 0, -1}), origin));
        return new RnPolyhedron(halfspaces);
    }

    public static void testSquare() {
        Point projecting = new Point(6, -8);
        System.out.println(square().proj(projecting));
    }

    public static void testCube() {
        System.out.println(Cube().proj(new Point(new double[]{.5, 7, 9})));
    }

    /**
     * This method should be called for each entry in the table being printed
     * @param numTests how many tests do we wish to run for each entry
     * @param numDim the dimension of the space containing the polyhedrons we wish to test
     * @param numFaces the number of half spaces intersecting to form the polyhedron we wish to test.
     * @param isTime true if we want to know how much time this takes, false if we want to know how many halfspaces are tested
     * @return the table value
     */
    private static double tableVal(int numTests, int numDim, int numFaces, boolean ordered) {
        double avg = 0;
        double r = 1;

        for (int i = 0; i < numTests; i++) {
            RnPolyhedron poly = RnPolyhedron.randomNonEmpty(numFaces, 1, numDim);
            Point randP = Point.uniformBoundedRand(new Point(numDim), r * 10);
            
            double startTime = System.currentTimeMillis();
            if(ordered)poly.projCoupRliecOrderedHalfSpaces(randP);
            else poly.proj(randP);
            double time = System.currentTimeMillis() - startTime;
            
            
            avg += (double)time / numTests;
        }
        return avg/1000.0;
    }

    private static String[] buildHeaders(int numCols){
        String[] headers = new String[numCols];
        for(int i = 0; i < numCols; i++){
            headers[i] = (i + 2) + " dim";
        }
        return headers;
    }
    
    private static String[] buildRowNames(int numRows, int increment){
        String[] rowNames = new String[numRows];
        rowNames[0] = increment + " hs";
        for(int i = 1; i < numRows; i++)
            rowNames[i] = (i + 1)*increment + " hs";
        return rowNames;
    }
    
    /**
     * prints a table
     */
    public static void testProjection() {
        int numTests = 3;
                
        int numDimChecks = 5, numFaceChecks = 5, faceIncrement = 3;

        double table[][] = new double[numFaceChecks][numDimChecks];
    
        for (int numFaces = 0; numFaces < numFaceChecks; numFaces++) 
            for (int numDim = 0; numDim < numDimChecks; numDim++)
                table[numFaces][numDim] = tableVal(numTests, numDim + 2, (numFaces
                        + 1) * faceIncrement, false);
        
        new Table(15).print(table, buildHeaders(numDimChecks), buildRowNames(numFaceChecks, faceIncrement));
    }

    public static void main(String[] args) throws IOException {
//        testProjection();

//        tableVal(1, 10, 20, false);
        
        testCube();

    }

}
