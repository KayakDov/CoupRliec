package main;

import Convex.LinearRn.RnAffineSpace;
import Convex.PolyhedronRn;
import Matricies.PointD;
import Convex.HalfSpaceRn;
import Convex.LinearRn.RnLinearSpace;
import Convex.LinearRn.RnPlane;
import Convex.GradDescentFeasibility.FeasibilityGradDescent;
import Convex.GradDescentFeasibility.Proj.ASNode;
import Convex.GradDescentFeasibility.Proj.ProjPolytope;
import Matricies.Point;
import java.io.IOException;

public class Main {

    public static void polytopeFeasabilityTest(int dim, int numFaces, int numRuns, boolean empty, boolean saveToMemory) {

        double epsilon = 1e-7;

        ASNode.memoryAvailable = saveToMemory;

        for (int i = 0; i < numRuns; i++) {
//            if (i % 100 == 0)
                System.out.println("i = " + i);

            FeasibilityGradDescent poly = empty ? new FeasibilityGradDescent(PolyhedronRn.random(numFaces, 1, dim))
                    : new FeasibilityGradDescent(PolyhedronRn.randomNonEmpty(numFaces, 1, dim));

            poly.setEpsilon(epsilon);

            Point feas = poly.fesibility(PointD.uniformRand(new PointD(dim), 100));

            if (feas.isReal() && !poly.hasElement(feas))
                throw new RuntimeException("Point is not feasible.");
        }

    }

    public static void testPolytopeFesibilitySpecifi() {
        HalfSpaceRn[] hs = new HalfSpaceRn[3];
        hs[0] = new HalfSpaceRn(new PointD(2), new PointD(-1, 0));
        hs[1] = new HalfSpaceRn(new PointD(0, 1), new PointD(1, .1));
        hs[2] = new HalfSpaceRn(new PointD(2), new PointD(1, 1));

        PointD y = new PointD(0, 1);

        System.out.println(new FeasibilityGradDescent(new PolyhedronRn(hs)).fesibility(y));

    }

    public static void testProjIntersection() {

        RnPlane plane1 = new RnPlane(new PointD(3, 1, 6), new PointD(0, 1, 0));

        RnPlane plane2 = new RnPlane(new PointD(3, 1, 6), new PointD(0, 0, 1));

        RnAffineSpace as = plane1.intersection(plane2);

        PointD a = new PointD(5, 2, 9);

        System.out.println(as.proj(a));
    }

    public static void testProj() {
        PointD p1 = new PointD(-0.7809757720106206, -0.07239826327433142, 0.62035097727599);
        PointD p2 = new PointD(-0.4531759977988629, 0.45538980614703306, 0.7663234561700254);

        PointD inSpace = new PointD(3.0630593214837916, -3.307987161177735, 5.082094988792884);

        RnPlane pl1 = new RnPlane(inSpace, p1);
        RnPlane pl2 = new RnPlane(inSpace, p2);

        System.out.println(pl1);
        System.out.println(pl2);

        PointD pp = new PointD(2.2047195327221005, -3.0382123166886514, 5.51852864962741);

        System.out.println("pp = " + pp);

        RnLinearSpace ls = new RnLinearSpace(new Point[]{p1, p2});

        Point proj = ls.proj(pp);

        System.out.println("col\n" + ls.colSpaceMatrix());

        System.out.println(ls.hasElement(proj));
        System.out.println(proj);

    }

    public static void cubeTest() {
        Point a = new PointD(1, 1, 1);
        Point b = new PointD(2, 2, 2);
        PolyhedronRn cube = new PolyhedronRn(new HalfSpaceRn[]{
            new HalfSpaceRn(a, new PointD(-1, 0, 0)),
            new HalfSpaceRn(a, new PointD(0, -1, 0)),
            new HalfSpaceRn(a, new PointD(0, 0, -1)),
            new HalfSpaceRn(b, new PointD(1, 0, 0)),
            new HalfSpaceRn(b, new PointD(0, 1, 0)),
            new HalfSpaceRn(b, new PointD(0, 0, 1))
//            new HalfSpace(b, new PointD(0, 1)),
//            new HalfSpace(b, new PointD(1, 0)),
//            new HalfSpace(a, new PointD(0, -1)),
//            new HalfSpace(a, new PointD(-1, 0))
        });

        FeasibilityGradDescent fgd = new FeasibilityGradDescent(cube);

        ProjPolytope pp = new ProjPolytope(cube);

        System.out.println(pp.proj(new PointD(-6, 10, 10)));

        System.out.println(fgd.fesibility(new PointD(5, 0, 3)));
    }

    public static void counterExample() {

        HalfSpaceRn[] hs = new HalfSpaceRn[5];
        hs[0] = new HalfSpaceRn(new PointD(2), new PointD(0, -1));
        hs[1] = new HalfSpaceRn(new PointD(0, 1), new PointD(0, 1));
        hs[2] = new HalfSpaceRn(new PointD(2), new PointD(-1, -1));
        hs[3] = new HalfSpaceRn(new PointD(7, 0), new PointD(-1, .1));
        hs[4] = new HalfSpaceRn(new PointD(7, 0), new PointD(1, 1));
        PointD start = new PointD(-10, -1);

        Point fp = new FeasibilityGradDescent(new PolyhedronRn(hs)).fesibility(start);
        System.out.println(fp);

    }

    public static void main(String[] args) throws IOException {

//        counterExample()
        int i = 2;
        polytopeFeasabilityTest(3, 4, 1000000, false, false);

//        FeasibilityGradDescent.loadFromErrorFile();//don't forget to fix toe plane.tosting for dim 2 or 3.
//        cubeTest();
    }

}
