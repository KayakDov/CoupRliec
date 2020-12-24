package Convex;

import listTools.Pair1T;
import Matricies.PointDense;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author Kayak
 */
public class Cube extends Polytope {

    private final PointDense a, b;

    public Cube(PointDense a, PointDense b) {
                
        checkInputQuality(a, b);
        this.a = a;
        this.b = b;
        
        addAll(IntStream.range(0, a.dim()).mapToObj(i -> 
                    new HalfSpace(a, new PointDense(a.dim()).set(i, -1))
            ).collect(Collectors.toList())
        );
        
        addAll(IntStream.range(0, b.dim()).mapToObj(i -> 
                    new HalfSpace(b, new PointDense(b.dim()).set(i, 1)))
            .collect(Collectors.toList())
        );
    }

    private void checkInputQuality(PointDense a, PointDense b) {
        if (IntStream.range(0, a.dim()).anyMatch(i -> a.get(i) > b.get(i)))
            throw new RuntimeException("this cuboid " + toString() + "is inside out.");
        if (a.dim() != b.dim())
            throw new RuntimeException("Dimensions don't match");
    }

    public Cube(Cube c) {
        this.a = new PointDense(c.a);
        this.b = new PointDense(c.b);
    }


    public PointDense getA() {
        return new PointDense(a);
    }

    public PointDense getB() {
        return (b);
    }



    /**
     * the number of iteration/intervals in one of the dimensions;
     *
     * @param dim
     * @param dx
     * @return
     */
    public double dimIterations(int dim, double dx) {
        return (b.get(dim) - a.get(dim)) / dx;
    }

    private boolean inCube(double x, int index) {
        double a = getA().get(index),
                b = getB().get(index);
        return a < b ? a <= x && x <= b : b <= x && x <= a;
    }

    public double volume() {
        PointDense dif = b.minus(a);
        return IntStream.range(0, dim()).mapToDouble(i -> dif.get(i)).reduce((d1, d2) -> d1*d2).getAsDouble();
    }

    /**
     * Returns to halves of this cube
     *
     * @param cutIndex the dimension along which the cube is to be cut
     * @return two halves of the cube
     */
    public Pair1T<Cube> halves(int cutIndex) {
        double mid = (a.get(cutIndex) + b.get(cutIndex)) / 2;
        return new Pair1T<>(
                new Cube(a, new PointDense(b).set(cutIndex, mid)),
                new Cube(new PointDense(a).set(cutIndex, mid), b)
        );
    }


    public PointDense mid() {
        return a.plus(b).mult(.5);
    }

    public Cube intersection(Cube c) {
        return new Cube(
                new PointDense(dim()).setAll(i -> Math.max(getA().get(i), c.getA().get(i))),
                new PointDense(dim()).setAll(i -> Math.min(getB().get(i), c.getB().get(i))));
    }

    public Interval getInterval(int dim) {
        return new Interval(getA().get(0), getB().get(0));
    }

    @Override
    public boolean hasElement(PointDense p) {
        return IntStream.range(0, dim()).allMatch(i -> inCube(p.get(i), i));
    }

    @Override
    public PointDense proj(PointDense x) {
        if (hasElement(x)) return new PointDense(x);
        return new PointDense(x).setAll(i
                -> getInterval(i).onInterval(x.get(i)));
    }

}
