package main;

import Convex.Linear.AffineSpace;
import Convex.Hull;
import Convex.Polytope;
import Matricies.Matrix;
import RnSpace.points.Point;
import Matricies.SymmetricMatrix;
import Convex.Cube;
import Convex.HalfSpace;
import Convex.Linear.LinearSpace;
import Convex.Linear.Plane;
import Convex.PolytopeCone;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.GradDescentFeasibility;
import Convex.thesisProjectionIdeas.RecursiveProjPolytopeCone;
import listTools.Pair1T;
import java.io.IOException;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.Arrays;
import listTools.Choose;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

public class Main {

    public static void testMatrixMult() {
        Matrix m1 = new Matrix(new double[][]{{0, 2}, {1, 1}});
        Matrix m2 = new Matrix(new double[][]{{1, 5}, {-1, 1}});
        System.out.println(m1);
        System.out.println(m2);
        System.out.println(m1.mult(m2));
    }

    public static void testSomeMatrixFunctions() {
        Matrix matrix = new Matrix(2, 2).setAll((n, m) -> pow(n + 2, m + 1));
        System.out.println(matrix);

        System.out.println(matrix.inverse());
        System.out.println(matrix.mult(matrix.inverse()));
//        System.out.println(matrix.solve(new MyPoint(10, 21)));

//        System.out.println(matrix.trace());
//        System.out.println(matrix.minor(0, 1));
//        System.out.println(matrix.mult(10));
//        Matrix m2 = new Matrix(4, 2).setAll((i, j) -> i+j);
//        System.out.println(m2);
//        System.out.println(matrix.mult(m2));
    }

    private static void testMatrixInverse() {
        Matrix m = new Matrix(new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 7}});
        Matrix inverse = m.inverse();
        System.out.println(m + "*\n" + inverse + " = " + m.mult(inverse));
    }

    public static void testSymmetricPossitiveDeffinite() {
        SymmetricMatrix sm = new SymmetricMatrix(new double[]{2, -1, 0,
            -1, 2, -1,
            0, -1, 2}, 3);
        System.out.println(sm.positiveDefinite());//should be true

        sm = new SymmetricMatrix(new double[]{1, 2, 3,
            2, 2, -1,
            3, -1, 2}, 3);
        System.out.println(sm.positiveDefinite());//should print false
    }

    public static void testQRDecomposition() {
        Matrix m = new Matrix(new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 7}});
        System.out.println(m);
        Pair1T<Matrix> pq = m.QRDecomposition();
        System.out.println(pq.l);
        System.out.println(pq.r);
        System.out.println(pq.l.mult(pq.r));

    }

    public static void testCuboidHalvs() {
        Cube c = new Cube(new Point(0, 0), new Point(1, 2));
        System.out.println(c.halves(0));
    }

    public static void testMatrixRemoveColRow() {
        Matrix m = new Matrix(3).setAll((i, j) -> i + j + 0.0);
        System.out.println(m);
        System.out.println(m.removeCol(1));
        System.out.println(m.removeRow(0));
    }

    public static void testPlanes() {
        Plane p = new Plane(new Point(0, 0), new Point(1, 0));
        System.out.println(p.below(new Point(5, 3)));
    }

    public static void testCrossProdcut() {
        Point p1 = new Point(1, 0, 0), p2 = new Point(0, 0, 1);
        Point[] p = new Point[]{p1, p2};
        System.out.println(p1.cross(p2));
        System.out.println(Point.cross(p));

    }

    public static void testPlane() {
        Point p1 = new Point(0, 0, 0), p2 = new Point(0, 1, 0), p3 = new Point(1, 0, 0);
        System.out.println(new Plane(new Point[]{p1, p2, p3}));
    }

    public static void testPolytope() {
        Polytope p = new Polytope();
        p.addFace(new HalfSpace(new Point(0, 1), new Point(0, 1)));
        p.addFace(new HalfSpace(new Point(0, -1), new Point(0, -1)));
        p.addFace(new HalfSpace(new Point(-1, 0), new Point(-1, 0)));
        p.addFace(new HalfSpace(new Point(1, 0), new Point(1, 0)));

        p.setEpsilon(1E-10);
        System.out.println(p.proj(new Point(-2, -2)));
        System.out.println(p.proj(new Point(-2, 2)));
        System.out.println(p.proj(new Point(2, .5)));
        System.out.println(p.proj(new Point(2, 2)));
        System.out.println(p.proj(new Point(2000000, 4000000)));

    }

    public static void testChoose() {
        int n = 4, k = 3;
        System.out.println(n + " choose " + k + " = " + Choose.choose(n, k));

        Character[] testSet = new Character[]{'a', 'b', 'c', 'd'};

        ArrayList<Character> list = new ArrayList<>();
        list.addAll(Arrays.asList(testSet));

        Choose<Character> ch = new Choose<>(list, k);

        ch.chooseStream().forEach(s -> {
            s.forEach(System.out::print);
            System.out.println("");
        });
    }

    public static void testPolytopeVertices() {

        Cube c = new Cube(new Point(1, 1, 1), new Point(2, 2, 2));
        System.out.println(c.getVertices());
    }

    public static void testPlaneNormalAvg() {
        Matrix m = new Matrix(3, 3);
        m.setRow(0, new Point(0, 0, 0));
        m.setRow(1, new Point(1, 0, -.001));
        m.setRow(0, new Point(0, 1, 0));
        m.setRow(0, new Point(1, 1, .001));
        System.out.println(new Plane(m));
    }

    public static void testMatrixSetRow() {
        Matrix m = new Matrix(3).setAll((i, j) -> i + j + 1.0);
        System.out.println(m);
        m.setRow(2, new Point(9, 9, 9));
        System.out.println(m);
    }

    public static void testLinearSpace() {
//
//        Matrix m = new Matrix(2, 4);
//        m.setRow(0, new Point(0, 1, 0, 0));
//        m.setRow(1, new Point(1, 0, 0, 0));
//        LinearSpace ls = LinearSpace.nullSpace(m);
//
//        System.out.println(ls.proj(new Point(1, -2, -3, -4)) + "\n");
//        System.out.println(ls.colSpaceMatrix());
//        System.out.println(ls.nullSpaceMatrix());

        LinearSpace line = LinearSpace.colSpace(new Point(1, 0, 0));
        System.out.println(line);
        System.out.println(line.hasElement(new Point(-1, 1, 0)));
    }

    public static void testAffineSpace() {
        Matrix m = new Matrix(3);
        m.setCol(0, new Point(0, 0, 1));
        m.setCol(1, new Point(1, 0, 0));
        LinearSpace ls = LinearSpace.colSpace(m);

        System.out.println(ls.nullSpaceMatrix());

        AffineSpace as = new AffineSpace(ls, new Point(7, 8, -3));

        System.out.println(as.proj(new Point(20, -17, 5)));
    }

    public static void testAffineSpaceIntersection() {

        AffineSpace as1 = new AffineSpace(LinearSpace.nullSpace(new Point(0, 0, 1).T()), new Point(0, 0, 1));
        AffineSpace as2 = new AffineSpace(LinearSpace.nullSpace(new Point(1, 0, 0).T()), new Point(0, 0, 1));
        AffineSpace as3 = new AffineSpace(LinearSpace.nullSpace(new Point(0, 1, 0).T()), new Point(0, 4, 0));
        //x = 0, z = 1, y is anything
        AffineSpace[] spaces = new AffineSpace[]{as1, as2, as3};

        AffineSpace as4 = AffineSpace.intersection(spaces);

        System.out.println(as4.linearSpace().proj(new Point(0, 5, 1)));

        System.out.println(as4);

        System.out.println(as4.hasElement(new Point(0, 7, 1)));
        System.out.println(as4.proj(new Point(-3, 5, 6)));
    }

    public static void testMyPolytopeProjection() {

        int dim = 3;

        Polytope standardPoly = new Hull(
                //                Matrix.randomColPoints(4, new Point(dim), 5)
                new Matrix(3, 4)
                        .setCol(0, new Point(0, 0, 0))
                        .setCol(1, new Point(0, 0, 1))
                        .setCol(2, new Point(0, 1, 0))
                        .setCol(3, new Point(1, 0, 0))
        //                        .setCol(4, new Point(3, .5))
        );

        GradDescentFeasibility mp = new GradDescentFeasibility(standardPoly);

        System.out.println(standardPoly);

//        Point testPoint = new Point(0, 0);
//        
//        System.out.println("fast projection " + mp.proj(testPoint));
//        
//        System.out.println("brute force " + mp.bruteForceProjection(testPoint));
        for (int i = 0; i < 100; i++) {

            Point y = Point.uniformRand(Point.Origin(dim), 100);

            Point myProj = mp.proj(y);

            if (!standardPoly.bruteForceProjection(y).equals(myProj, .000001)) {

                System.out.println("i = " + i);
                System.out.println("y = " + y);
                System.out.println("yk.bruteForceProjection(y) = "
                        + standardPoly.bruteForceProjection(y));
                System.out.println("mp.proj(y) = " + myProj);
                return;
            }

        }
        System.out.println("true");
    }

    public static void testAdjacentFacesOfAPolytope() {

        HalfSpace north = new HalfSpace(new Point(1, 1), new Point(0, 1)),
                south = new HalfSpace(new Point(0, 0), new Point(0, -1)),
                east = new HalfSpace(new Point(0, 0), new Point(-1, 0)),
                west = new HalfSpace(new Point(1, 1), new Point(1, 0));

        Polytope p = new Polytope(new HalfSpace[]{north, south, east, west});
//        System.out.println(p);
// 

//        HalfSpace up = new HalfSpace(new Point(1, 1, 1), new Point(0, 0, 1)),
//                down = new HalfSpace(new Point(0, 0, 0), new Point(0, 0, -1)),
//                north = new HalfSpace(new Point(1, 1, 1), new Point(0, 1, 0)),
//                south = new HalfSpace(new Point(0, 0, 0), new Point(0, -1, 0)),
//                east = new HalfSpace(new Point(0, 0, 0), new Point(-1, 0, 0)),
//                west = new HalfSpace(new Point(1, 1, 1), new Point(1, 0, 0));
//
//        Polytope p = new Polytope(new HalfSpace[]{up, down, north, south, east, west});
//
        System.out.println(p.adjacent(north, east));
    }

    public static void testMatrixIndependentRows() {

        Matrix m = new Matrix(3, 3);
        m.setRow(0, new Point(1, 1, 1));
        m.setRow(1, new Point(3, -4, 7));
        m.setRow(2, new Point(2, -5, 6));
        System.out.println(m);
        System.out.println(m.independentRows(.0000001));
    }

    public static void testConvexCombination() {

        Matrix points = new Matrix(3, 4)
                .setCol(0, new Point(0, 0, 0))
                .setCol(1, new Point(0, 1, 0))
                .setCol(2, new Point(1, 0, 0))
                .setCol(3, new Point(0, 0, 1));

//        Matrix points = Matrix.randomColPoints(3, new Point(5,5,5), 3);
        Point test = new Point(.1, .1, 0);

        Hull.ConvexCombination cc = new Hull.ConvexCombination(points);

        System.out.println("feasibility answer is " + cc.hasElement(test));

        System.out.println(new Hull(points));

    }

    public static void polytopeFeasabilityTest() {

        int dim = 174;
        int numFaces = 40;
        double epsilon = 1e-7;

        for (int i = 0; i < 10; i++) {
            System.out.println("i = " + i);
            GradDescentFeasibility poly = new GradDescentFeasibility(Polytope.randomNonEmpty(numFaces, 1, dim));
            poly.setEpsilon(epsilon);

            Point feas = poly.fesibility(Point.uniformRand(new Point(dim), 10));

//            System.out.println(feas);
        }

    }

    public static void testAffineSpaceContaining() {

        Matrix points = new Matrix(2, 3)
                .setRow(0, new Point(1, 0, 0))
                .setRow(1, new Point(0, 1, 0));

        System.out.println(points);

        AffineSpace as = AffineSpace.smallestContainingSubSpace(points, 1e-6);

        System.out.println(as.hasElement(new Point(3, 4, 5)));

        System.out.println(as.hasElement(points.row(1), 1e-4));

        System.out.println(as);

    }

    public static void testRecursivePolytopeCone() {

//        -0.4021674579995177*(x-0.0) + 0.3981497463145742*(y-0.0) + 0.824462318869605*(z-0.0)<= 0
//-0.5365748474367106*(x-0.0) + 0.5676777837620238*(y-0.0) + 0.624363169094164*(z-0.0)<= 0
//0.5279418008343463*(x-0.0) + -0.48336838058840675*(y-0.0) + 0.6983068548848197*(z-0.0)<= 0
        int dim = 3;
        Point p = new Point(dim).setAll(i -> 10);

        boolean equal = true;
        Point proj = null, bf = null;

        RecursiveProjPolytopeCone rprc = new RecursiveProjPolytopeCone(new Point(dim));
        rprc.addPlaneWithNormal(new Point(-0.4021674579995177, 0.3981497463145742, 0.824462318869605));
        rprc.addPlaneWithNormal(new Point(-0.5365748474367106, 0.5676777837620238, 0.624363169094164));
        rprc.addPlaneWithNormal(new Point(0.5279418008343463, -0.48336838058840675, 0.6983068548848197));

        proj = rprc.proj(p);
        bf = rprc.bruteForceProjection(p);

//        while (equal) {
//            System.out.println("\nstart:\n");
//            rprc = new RecursiveProjPolytopeCone(PolytopeCone.randomPolytopeCone(3, dim));
//            proj = rprc.proj(p);
//            bf = rprc.bruteForceProjection(p);
//            equal = proj.equals(bf, 1e-5);
//        }
        System.out.println(proj);
        System.out.println(bf);
        System.out.println(proj.equals(bf, 1e-5));
        System.out.println(rprc);

    }

    public static void testPolytopeConeProj() {

//        -0.4021674579995177*(x-0.0) + 0.3981497463145742*(y-0.0) + 0.824462318869605*(z-0.0)<= 0
//-0.5365748474367106*(x-0.0) + 0.5676777837620238*(y-0.0) + 0.624363169094164*(z-0.0)<= 0
//0.5279418008343463*(x-0.0) + -0.48336838058840675*(y-0.0) + 0.6983068548848197*(z-0.0)<= 0
        int dim = 3;
        Point p = new Point(dim).setAll(i -> 10);

        boolean equal = true;
        Point proj = null, bf = null;

        PolytopeCone rprc = new PolytopeCone(new Point(dim));
        rprc.addPlaneWithNormal(new Point(-0.4021674579995177, 0.3981497463145742, 0.824462318869605));
        rprc.addPlaneWithNormal(new Point(-0.5365748474367106, 0.5676777837620238, 0.624363169094164));
        rprc.addPlaneWithNormal(new Point(0.5279418008343463, -0.48336838058840675, 0.6983068548848197));

        proj = rprc.proj(p);
        bf = rprc.bruteForceProjection(p);

//        while (equal) {
//            System.out.println("\nstart:\n");
//            rprc = new RecursiveProjPolytopeCone(PolytopeCone.randomPolytopeCone(3, dim));
//            proj = rprc.proj(p);
//            bf = rprc.bruteForceProjection(p);
//            equal = proj.equals(bf, 1e-5);
//        }
        System.out.println(proj);
        System.out.println(bf);
        System.out.println(proj.equals(bf, 1e-5));
        System.out.println(rprc);

    }

    public static void testPolytopeFesibilitySpecifi() {
        HalfSpace[] hs = new HalfSpace[3];
        hs[0] = new HalfSpace(new Point(2), new Point(-1, 0));
        hs[1] = new HalfSpace(new Point(0, 1), new Point(1, .1));
        hs[2] = new HalfSpace(new Point(2), new Point(1, 1));

        Point y = new Point(0, 1);

        System.out.println(new GradDescentFeasibility(new Polytope(hs)).fesibility(y));

    }

    public static void main(String[] args) throws IOException {

//        polytopeFeasabilityTest();

//        GradDescentFeasibility.loadFromErrorFile();
    
        
        
         Matrix m = new Matrix(2);
         
    
    
    
    }

}
