package main;

import Convex.Linear.AffineSpace;
import Convex.Polytope;
import Matricies.PointD;
import Convex.HalfSpace;
import Convex.Linear.LinearSpace;
import Convex.Linear.Plane;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.FeasibilityGradDescent;
import Matricies.Point;
import java.io.IOException;
import java.util.HashMap;

public class Main {

    public static void polytopeFeasabilityTest() {

        int dim = 26;
        int numFaces = 100;
        double epsilon = 1e-7;

        for (int i = 0; i < 1; i++) {
            System.out.println("i = " + i);
            FeasibilityGradDescent poly = new FeasibilityGradDescent(Polytope.randomNonEmpty(numFaces, 1, dim));
            poly.setEpsilon(epsilon);

            Point feas = poly.fesibility(PointD.uniformRand(new PointD(dim), 100));

//            System.out.println(feas);
        }

    }

    public static void testPolytopeFesibilitySpecifi() {
        HalfSpace[] hs = new HalfSpace[3];
        hs[0] = new HalfSpace(new PointD(2), new PointD(-1, 0));
        hs[1] = new HalfSpace(new PointD(0, 1), new PointD(1, .1));
        hs[2] = new HalfSpace(new PointD(2), new PointD(1, 1));

        PointD y = new PointD(0, 1);

        System.out.println(new FeasibilityGradDescent(new Polytope(hs)).fesibility(y));

    }

    public static void testProjIntersection() {

        Plane plane1 = new Plane(new PointD(3, 1, 6), new PointD(0, 1, 0));

        Plane plane2 = new Plane(new PointD(3, 1, 6), new PointD(0, 0, 1));

        AffineSpace as = plane1.intersection(plane2);

        PointD a = new PointD(5, 2, 9);

        System.out.println(as.proj(a));
    }

    public static void testProj() {
        PointD p1 = new PointD(-0.7809757720106206, -0.07239826327433142, 0.62035097727599);
        PointD p2 = new PointD(-0.4531759977988629, 0.45538980614703306, 0.7663234561700254);

        PointD inSpace = new PointD(3.0630593214837916, -3.307987161177735, 5.082094988792884);

        Plane pl1 = new Plane(inSpace, p1);
        Plane pl2 = new Plane(inSpace, p2);

        System.out.println(pl1);
        System.out.println(pl2);

        PointD pp = new PointD(2.2047195327221005, -3.0382123166886514, 5.51852864962741);

        System.out.println("pp = " + pp);

        LinearSpace ls = new LinearSpace(new Point[]{p1, p2});

        Point proj = ls.proj(pp);

        System.out.println("col\n" + ls.colSpaceMatrix());

        System.out.println(ls.hasElement(proj));
        System.out.println(proj);

    }

    public static void main(String[] args) throws IOException {
        
        polytopeFeasabilityTest();
//        FeasibilityGradDescent.loadFromErrorFile();//don't forget to fix toe plan.tosting for dim 2 or 3.
    }

}
