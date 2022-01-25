package main;

import Convex.RnPolyhedron;
import Hilbert.HalfSpace;
import Matricies.Point;
import Matricies.PointD;
import java.io.IOException;
import java.util.ArrayList;
import tools.Table;

public class Main {

    public static RnPolyhedron square() {
        ArrayList<HalfSpace<Point>> halfspaces = new ArrayList<>();
        halfspaces.add(new HalfSpace<>(new PointD(1, 0), new PointD(1, 1)).setName("right"));
        halfspaces.add(new HalfSpace<>(new PointD(0, 1), new PointD(1, 1)).setName("top"));
        halfspaces.add(new HalfSpace<>(new PointD(0, -1), new PointD(0, 0)).setName("bottom"));
        halfspaces.add(new HalfSpace<>(new PointD(-1, 0), new PointD(0, 0)).setName("left"));
        return new RnPolyhedron(halfspaces);
    }

    public static RnPolyhedron Cube() {
        PointD ones = new PointD(1, 1, 1);
        PointD origin = new PointD(3);
        ArrayList<HalfSpace<Point>> halfspaces = new ArrayList<>();
        halfspaces.add(new HalfSpace<>(new PointD(1, 0, 0), ones));
        halfspaces.add(new HalfSpace<>(new PointD(0, 1, 0), ones));
        halfspaces.add(new HalfSpace<>(new PointD(0, 0, 1), ones));
        halfspaces.add(new HalfSpace<>(new PointD(-1, 0, 0), origin));
        halfspaces.add(new HalfSpace<>(new PointD(0, -1, 0), origin));
        halfspaces.add(new HalfSpace<>(new PointD(0, 0, -1), origin));
        return new RnPolyhedron(halfspaces);
    }

    public static void testSquare() {
        PointD projecting = new PointD(6, -8);
        System.out.println(square().proj(projecting));
    }

    public static void testCube() {
        System.out.println(Cube().proj(new PointD(3, 7, 9)));
    }

    /**
     * This method should be called for each entry in the table being printed
     * @param numTests how many tests do we wish to run for each entry
     * @param numDim the dimension of the space containing the polyhedrons we wish to test
     * @param numFaces the number of half spaces intersecting to form the polyhedron we wish to test.
     * @param isTime true if we want to know how much time this takes, false if we want to know how many halfspaces are tested
     * @return the table value
     */
    private static double tableVal(int numTests, int numDim, int numFaces) {
        double avg = 0;
        double r = 1;

        for (int i = 0; i < numTests; i++) {
            RnPolyhedron poly = RnPolyhedron.randomNonEmpty(numFaces, 1, numDim);
            PointD randP = PointD.uniformBoundedRand(new PointD(numDim), r * 10);
            
            double startTime = System.currentTimeMillis();
            poly.projCoupRliecOrderedHalfSpaces(randP);
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
                        + 1) * faceIncrement);
        
        new Table(15).print(table, buildHeaders(numDimChecks), buildRowNames(numFaceChecks, faceIncrement));
    }

    public static void main(String[] args) throws IOException {
        testProjection();



    }

}
