package main;

import Convex.Linear.AffineSpace;
import Convex.Hull;
import Convex.Polytope;
import FuncInterfaces.RToR;
import RnSpace.curves.Curve;
import Matricies.Matrix;
import RnSpace.points.Point;
import RnSpace.rntor.RnToRFunc;
import RnSpace.curves.Spline;
import RnSpcae.RnToRmFunc.VectorField;
import RnSpcae.RnToRmFunc.VectorField2d;
import RnSpace.rntor.Gradient;
import RnSpace.rntor.Hessian;
import RnSpace.Optimization.Min;
import Matricies.SymmetricMatrix;
import RnSpace.FunctionPrinter;
import Convex.Cube;
import Convex.HalfSpace;
import Convex.indicatorProjection.ConvexSetIndicator;
import RnSpace.rntor.polynomial.Laurent;
import RnSpace.rntor.polynomial.Polynomial;
import FuncInterfaces.RnToR;
import FuncInterfaces.RnToRnToR;
import Convex.Interval;
import Convex.Linear.LinearSpace;
import Convex.Linear.Plane;
import Convex.PolytopeCone;
import Convex.Sphere;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.GradDescentFeasibility;
import Convex.thesisProjectionIdeas.RecursiveProjPolytopeCone;
import RnSpace.rntor.GraphX;
import DiscreteMath.Choose;
import FuncInterfaces.Indicator;
import listTools.Pair1T;
import FuncInterfaces.RnToRn;
import RnSpace.curves.JoinedLines;
import graph.JChart;
import graph.Processing;
import java.io.IOException;
import realFunction.Point2d;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import listTools.InsertionSort;
import realFunction.RToRFunc;
import FuncInterfaces.RnToRpxq;
import RnSpace.SequenceTI;

public class Main {

    public static RnToRFunc f = RnToRFunc.st(x -> (x[0] - 1) * (x[0] - 1)
            + (x[1] - 3) * (x[1] - 3), 2);

    public static void testNewtonDescent() {

        int n = 4;
        RnToRFunc f = RnToRFunc.st(x -> {

            return IntStream.range(0, x.length - 1).mapToDouble(i -> 100
                    * squared(x[i + 1] - x[i] * x[i]) + squared(1 - x[i])).sum();

        }, n);

        Point init = new Point(n).setAll(i -> .8);

        double end = 1e-4, dt = 1e-8;

        Min ndm = Min.newtonsDescentMethod(f, init, end, dt);
        System.out.println("f(" + ndm + ") = " + f.of(ndm));

        Min gdm = Min.gradDescentNoLineSearch(f, init, end, dt);
        System.out.println("f(" + gdm + ") = " + f.of(gdm));

    }

    public static void testNewtonRaphsonMethod() {
        double dt = 1e-10;
        RToR f = ((RToR) (x -> sin(x))).d(dt);
        System.out.println(f.newtonMethodMin(0, dt, .01));
        System.out.println(f.min(0, dt, 2));
    }

    public static void testPenaltyMethods() {
        RnToRFunc[] g = new RnToRFunc[]{RnToRFunc.st(x -> x[0] * x[0] + x[1]
        * x[1] - 4.0, 2)};
        Min min = Min.interiorPenaltyFunctionMethod(f, g, new GraphX(f, Point.Origin(2)), 1E-6, 1E-4);
        System.out.println(min);
        min = Min.exteriorPenaltyFunctionMethod(f, g, new GraphX(f, Point.Origin(2)), 1E-6, 1E-4);
        System.out.println(min);
    }

    public static void printVectorFieldDemo() {
        Processing.setDim(-10, 10, -10, 10);

        VectorField2d vf = new VectorField2d("-6xy", "3y^2-3x^2");

        //Processing.addCurve(circle);
        int numCurves = 9;
//        for (int i = 1; i < numCurves; i++)
//            for (int j = -7; j < 8; j++) {
//                Curve2dAprx curve = new Curve2dAprx("t", "0 + " + j, -7, 7, 1);
//                Processing.addCurve(curve);
//                Processing.addCurve(vf.getCurves(curve, numCurves, 7));
//            }

        System.out.println(vf.of(new Point2d(4, 4)));
        for (int i = 0; i < 40; i++) {
            Processing.addCurve(vf.getCurve(new Point2d(-9 - i / 10.0, 9 - i
                    / 10.0), 5));
            Processing.addCurve(vf.getCurve(new Point2d(9 + i / 10.0, 9 - i
                    / 10.0), 5));
            Processing.addCurve(vf.getCurve(new Point2d(-3 + i / 7.0, -11), 5));
        }
        Processing.openWindow();

    }

    public static void workIntegral() {
        VectorField vf = new VectorField(0) {
            @Override
            public Point of(double[] x) {
                double[] of = new double[2];
                of[0] = 0;
                of[1] = 1;
                return new Point(of);
            }
        };

        Curve c = new Curve(0, 1) {
            @Override
            public Point of(double t) {
                return new Point(0, t);
            }
        };

        System.out.println(c.work(vf));

    }

    public static void Spline1(int n) {
        Processing.setDim(10);

        Random r = new Random();

        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            points[i] = new Point(r.nextDouble() * 8 - 4, r.nextDouble() * 8
                    - 4);
        }

        Spline spline = new Spline(points);

        Processing.addCurve(spline, 100 * n);
        for (Point p : points) {
            Processing.circleAt(p, .1);
        }

        Processing.openWindow();

    }

    public static void testNelderMead() {
        Processing.setDim(10);
        Processing.openWindow();
        Processing.drawAxes(5);

        RnToRFunc f = RnToRFunc.st(x -> {
            Processing.circleAt(new Point(x), .2);
            try {
                TimeUnit.SECONDS.sleep(0);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            return (cos(x[0]) + cos(x[1]));
        }, 2);

        System.out.println(Min.nelderMead(f, new Point(2, 2), 1, .00001));
    }

    public static void derivitiveTest() {
        RnToR cos = RnToRFunc.st(x -> cos(x[0]), 2),
                negSinTest = cos.d(0, E - 3);

        RToRFunc fsin = RToRFunc.st(x -> -negSinTest.of(new double[]{x, 0}), -PI
                * 1.5, PI * 1.5);
        RToRFunc sin = RToRFunc.st(x -> Math.sin(x), -5, 5);

        Processing.setDim(15);
        Processing.drawAxes(5);
        Processing.addCurve(sin, 100);
        Processing.addCurve(fsin, 100);
        Processing.openWindow();
    }

    public static void testHessian() {
        RnToRFunc f = RnToRFunc.st(x -> x[0] * x[0] * x[0] * x[1], 2);
        RnToRn df = new Gradient(f, .00001);
        RnToRpxq hes = new Hessian(f, .00001);
        System.out.println(hes.of(new double[]{2, 2}));
    }

    public static void testGradientDescent() {

        RnToRFunc f = RnToRFunc.st(x -> (x[0] - 1) * (x[0] - 1) + (x[1] - 3)
                * (x[1] - 3), 2);
        System.out.println(Min.gradientDescentLineSearch(f, new GraphX(f, Point.Origin(2)), .001, .00001));

    }

    public static void testMatrixMult() {
        Matrix m1 = new Matrix(new double[][]{{0, 2}, {1, 1}});
        Matrix m2 = new Matrix(new double[][]{{1, 5}, {-1, 1}});
        System.out.println(m1);
        System.out.println(m2);
        System.out.println(m1.mult(m2));
    }

    private static double cube(double x) {
        return x * x * x;
    }

    private static double squared(double x) {
        return x * x;
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

    private static void testDerrivitive() {
        RnToRFunc f = RnToRFunc.st(t -> t[0] * t[0], 1);
        System.out.println(f.d(0, .0001).of(Point.oneD(3)));
    }

    private static void testGradient() {
        RnToRFunc f = RnToRFunc.st(x -> sin(x[0]) + cos(x[1]), 2);

        System.out.println(new Gradient(f, .00001).of(new Point(0, 0)));
    }

    private static void testBisectionMethodZero() {
        RToRFunc f = RToRFunc.st(t -> t + 3, -10, 10);
        System.out.println(f.bisectionMethodZero(.00001, .0001));
    }

    private static void testSecantMethod() {
        RToRFunc f = RToRFunc.st(t -> (t - 3) * (t - 3) - 1, -10, 10);
        System.out.println(f.secantMethodMin(0, .0001));

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

    public static void testSetSomeInMatrix() {
        Matrix m = new Matrix(new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 7}});
        System.out.println(m.setSome((i, j) -> i <= j, (i, j) -> m.get(j, i)));
    }

    public static void testFunctionPrinter() {
        RnToR f = t -> t[0] * t[1];
        Cube c = new Cube(new Point(-1, -1), new Point(1, 1));
        FunctionPrinter.printFunc("xSquared.plt", f, c, .05);
    }

    public static void polyTest() {
        Polynomial p = new Polynomial(new double[]{1, 1, 1, 1, 1, 1}, 2, 2);
        System.out.println(p);
        System.out.println(p.of(new double[]{0, 0}));
        p = new Polynomial(new double[]{1, 1, 1, 1, 1, 1, 1, 1, 1}, 3, 2);
        System.out.println(p);
        JChart chart = new JChart(new Interval(-5, 5));
        RnToRFunc f = new Polynomial(new double[]{1, -1, 1}, 2, 1).setDomain(new Interval(-5, 5)).setName("poly");
        System.out.println(f);
        chart.addFunction(f, .01);
    }

    public static void LaurentTest() {
        Laurent p = new Laurent(new double[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, 2, 2);
        System.out.println(p);
        System.out.println(p.of(new double[]{0, 0}));
        p = new Laurent(new double[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, 3, 2);
        System.out.println(p);
        p = new Laurent(new double[]{0, 1, -2}, 1, 1);
        System.out.println(p);
        JChart chart = new JChart(new Interval(-6, 6));
        chart.addFunction(p.setDomain(new Interval(-3, 3)), .01);
    }

    public static void integralTest() {
        RnToRFunc f = RnToRFunc.st(x -> 2 * x[0], new Cube(Point.oneD(0), Point.oneD(1)));
        System.out.println(f.integral(.0000001));
    }

    public static void testCuboidHalvs() {
        Cube c = new Cube(new Point(0, 0), new Point(1, 2));
        System.out.println(c.halves(0));
    }

    public static void testCuboidStream() {
        new Cube(new Point(1, 1, 1), new Point(2, 2, 2)).stream(.5).forEach(System.out::println);
    }

    public static void jchartTest() {
        JChart chart = new JChart();
        chart.addFunction(RToRFunc.st(x -> x * x - 2, "a", -1, 1), .001);
        chart.addFunction(RToRFunc.st(x -> x * x, "b", -1, 1), .001);
    }

    public static Point findLaurent(int deg, RnToRFunc target, Interval interval, double end) {

        RToRFunc abs = RToRFunc.st((RToR) (x -> Math.abs(x)), interval);

        int numCoef = Laurent.numCoeficients(deg, 1);

        RnToRnToR laurent = x -> new Laurent(x, deg, 1);

        RnToRFunc f = RnToRFunc.st(x
                -> abs.of(laurent.of(x).minus(target))
                        .integral(interval, .1), numCoef);

        Point initPoint = new Point(numCoef).setAll(i -> 0);

        return Min.nelderMead(f, initPoint, 50 * deg, end);
//        return Min.newtonsDescentMethod(f, initPoint, .1, .00001);

    }

    public static void fOfXTesting() {
        RnToR f = x -> {
            System.out.println("boo");
            return x[0];
        };

        GraphX p1 = new GraphX(f, new Point(0, 0));

        System.out.println(p1.y());
        System.out.println(p1.y());
    }

    public static void InsertionSortTest() {
        ArrayList<Integer> list = new ArrayList<>();
        list.addAll(Arrays.asList(new Integer[]{1, 2, -1, 99, -52, -99}));
        InsertionSort is = new InsertionSort(list, (Comparator<Integer>) (a, b) -> (int) Math.signum(a
                - b));
        System.out.println(list);
    }

//    public static void testSubGradient() {
//        Point p = new Point(0, 1);
//        double dt = 1e-5;
//        RnToRFunc f = RnToRFunc.st(x -> x[0]*x[1], 2);
//        System.out.println(new Gradient(f, dt).of(p));
//        System.out.println(new SubGradient(f, dt, .00001).of(p));
//        
//    }
    public static void testSphere() {
        Sphere s = new Sphere(2);
        Curve c = Curve.st(t -> s.surface(Point.oneD(t)), new Interval(0, 4));
        new JChart().addFunction(c, .001);

    }

    public static void testMatrixRemoveColRow() {
        Matrix m = new Matrix(3).setAll((i, j) -> i + j);
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

    public static void testConvexSetIndicator() {
        double dt = 1e-8;
        double end = 1e-3;
        int numPoints = 8;

        Indicator id = x -> new Sphere(3).hasElement(new Point(x));
        ConvexSetIndicator set = ConvexSetIndicator.st(dt, end, new Point(3).set(0, .9), id, numPoints);
        System.out.println(set.proj(new Point(0, -50, 0)));

        id = x -> x.get(2) > x.get(0) * x.get(0) + x.get(1) * x.get(1) - 3;
        set = ConvexSetIndicator.st(dt, end, new Point(0, 5, 25.1), id, numPoints);
        System.out.println(set.proj(new Point(0, 0, -50)));

        for (int i = 0; i < 50; i++) {
            Cube cuboid = new Cube(new Point(1, 1, 1), new Point(2, 2, 2));
            id = x -> cuboid.hasElement(new Point(x));
            set = ConvexSetIndicator.st(dt, end, new Point(1.01, 1.9, 1.5), id, numPoints);
            System.out.println(set.proj(new Point(0, 0, 0)));
        }
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
        Matrix m = new Matrix(3).setAll((i, j) -> i + j + 1);
        System.out.println(m);
        m.setRow(2, new Point(9, 9, 9));
        System.out.println(m);
    }

    public static void cauchyTest() {

        Point lim = ((SequenceTI) x -> x.mult(.5)).cauchyLimit(new Point(1, 5), 1e-10);
        System.out.println(lim);
    }

    public static void testJoinedLines() {
        JoinedLines lines = new JoinedLines(Matrix.fromRows(new Point[]{
            new Point(0, 0),
            new Point(1, 1),
            new Point(5, 6),
            new Point(100, 200)
        }));

        System.out.println(lines.ofB());
        System.out.println(lines.of(1));
    }

    public static void testSystemOfEquations() {
//        Matrix m = new Matrix(3);
//        Point p1 = new Point(1, 1, 0);
//        Point p2 = new Point(1, 0, 2);
//        Point p3 = new Point(1, 1, 1);
//        m.setRow(0, p1).setRow(1, p2).setRow(2, p3);
//
//        Point b = new Point(1, 2, 3);
//        Point x = new SystemOfEquations(m, b).compute();
//
//        System.out.println("should read 123: " + m.mult(x));
//
//        m = new Matrix(3, 2)
//                .setRow(0, new Point(0, 4))
//                .setRow(1, new Point(1, 5))
//                .setRow(2, new Point(1, 1));
//
//        System.out.println(m);
//
//        b = new Point(2, 7, 5);
//
//        x = new SystemOfEquations(m, b).compute();
//
//        System.out.println(x);
//
//        System.out.println("should read " + b + ": " + m.mult(x));
//
//        System.out.println(m);
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

        int dim = 10;
        int numFaces = 1000;
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


    public static void testPolytopeFesibilitySpecifi(){
        HalfSpace[] hs = new HalfSpace[3];
        hs[0] = new HalfSpace(new Point(2), new Point(-1, 0));
        hs[1] = new HalfSpace(new Point(0, 1), new Point(1, .1));
        hs[2] = new HalfSpace(new Point(2), new Point(1,1));
        
        Point y = new Point(0, 1);
        
        System.out.println(new GradDescentFeasibility(new Polytope(hs)).fesibility(y));
        
    }
    
    public static void main(String[] args) throws IOException {

        polytopeFeasabilityTest();
//        GradDescentFeasibility.loadFromErrorFile();
    }

}
