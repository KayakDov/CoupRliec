package main;

import Convex.Linear.AffineSpace;
import Convex.Polytope;
import Matricies.PointDense;
import Convex.HalfSpace;
import Convex.Linear.LinearSpace;
import Convex.Linear.Plane;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.GradDescentFeasibility;
import Matricies.Matrix;
import Matricies.MatrixDense;
import Matricies.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import listTools.Choose;

public class Main {

    public static void testPlanes() {
        Plane p = new Plane(new PointDense(0, 0), new PointDense(1, 0));
        System.out.println(p.below(new PointDense(5, 3)));
    }

    public static void testPolytope() {
        Polytope p = new Polytope();
        p.addFace(new HalfSpace(new PointDense(0, 1), new PointDense(0, 1)));
        p.addFace(new HalfSpace(new PointDense(0, -1), new PointDense(0, -1)));
        p.addFace(new HalfSpace(new PointDense(-1, 0), new PointDense(-1, 0)));
        p.addFace(new HalfSpace(new PointDense(1, 0), new PointDense(1, 0)));

        p.setEpsilon(1E-10);
        System.out.println(p.proj(new PointDense(-2, -2)));
        System.out.println(p.proj(new PointDense(-2, 2)));
        System.out.println(p.proj(new PointDense(2, .5)));
        System.out.println(p.proj(new PointDense(2, 2)));
        System.out.println(p.proj(new PointDense(2000000, 4000000)));

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

        LinearSpace line = LinearSpace.colSpace(new PointDense(1, 0, 0));
        System.out.println(line);
        System.out.println(line.hasElement(new PointDense(-1, 1, 0)));
    }

    public static void testAffineSpaceIntersection() {

        AffineSpace as1 = new AffineSpace(LinearSpace.nullSpace(new PointDense(0, 0, 1).T()), new PointDense(0, 0, 1));
        AffineSpace as2 = new AffineSpace(LinearSpace.nullSpace(new PointDense(1, 0, 0).T()), new PointDense(0, 0, 1));
        AffineSpace as3 = new AffineSpace(LinearSpace.nullSpace(new PointDense(0, 1, 0).T()), new PointDense(0, 4, 0));
        //x = 0, z = 1, y is anything
        AffineSpace[] spaces = new AffineSpace[]{as1, as2, as3};

        AffineSpace as4 = AffineSpace.intersection(spaces);

        System.out.println(as4.linearSpace().proj(new PointDense(0, 5, 1)));

        System.out.println(as4);

        System.out.println(as4.hasElement(new PointDense(0, 7, 1)));
        System.out.println(as4.proj(new PointDense(-3, 5, 6)));
    }

    public static void testAdjacentFacesOfAPolytope() {

        HalfSpace north = new HalfSpace(new PointDense(1, 1), new PointDense(0, 1)),
                south = new HalfSpace(new PointDense(0, 0), new PointDense(0, -1)),
                east = new HalfSpace(new PointDense(0, 0), new PointDense(-1, 0)),
                west = new HalfSpace(new PointDense(1, 1), new PointDense(1, 0));

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

    public static void polytopeFeasabilityTest() {

        int dim = 5;
        int numFaces = 10000;
        double epsilon = 1e-7;

        for (int i = 0; i < 10; i++) {
            System.out.println("i = " + i);
            GradDescentFeasibility poly = new GradDescentFeasibility(Polytope.randomNonEmpty(numFaces, 1, dim));
            poly.setEpsilon(epsilon);

            Point feas = poly.fesibility(PointDense.uniformRand(new PointDense(dim), 10));

            System.out.println(feas);
        }

    }

    public static void testPolytopeFesibilitySpecifi() {
        HalfSpace[] hs = new HalfSpace[3];
        hs[0] = new HalfSpace(new PointDense(2), new PointDense(-1, 0));
        hs[1] = new HalfSpace(new PointDense(0, 1), new PointDense(1, .1));
        hs[2] = new HalfSpace(new PointDense(2), new PointDense(1, 1));

        PointDense y = new PointDense(0, 1);

        System.out.println(new GradDescentFeasibility(new Polytope(hs)).fesibility(y));

    }

    public static void main(String[] args) throws IOException {

//        polytopeFeasabilityTest();

//            System.out.println(Memory.remaining());
//        GradDescentFeasibility.loadFromErrorFile();
        MatrixDense mp1 = new PointDense(1, 2, 3).T();
        
        System.out.println(mp1.cols);
        
        
        
        PointDense p2 = new PointDense(4, 5, 6);
        
        System.out.println(p2.rows());
        
        
        
        MatrixDense mp2 = new MatrixDense(p2);
        System.out.println(mp1);
        
        
        System.out.println(mp2.ejmlDense());
        
        
        
        System.out.println(mp1.T().mult(mp2));

    }

}
