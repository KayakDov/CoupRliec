/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Matricies;

import Convex.ConvexSet;
import Convex.LinearRn.RnPlane;
import java.util.Arrays;
import java.util.function.DoubleFunction;
import java.util.function.IntToDoubleFunction;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import org.ejml.data.DMatrixSparseCSC;
import org.ejml.data.DMatrixSparseTriplet;
import org.ejml.sparse.csc.CommonOps_DSCC;

/**
 *
 * @author dov
 */
public class PointSparse extends MatrixSparse implements Point{

    public PointSparse(int dim) {
        super(1, dim);
    }

    public PointSparse(Point p) {
        super(p.asSparse());
    }
    

    @Override
    public boolean above(RnPlane plane) {
        return plane.below(this);
    }

    @Override
    public PointSparse addToMe(Point p) {
        if(p.isDense()) setAll(i -> get(i) + p.get(i));
        else p.asSparse().nonZeroes().forEach(coord -> set(coord.row, get(coord.row) + coord.value));
        return this;
    }
    
    public PointSparse addToMe(PointD p) {
        return setAll(i -> get(i) + p.get(i));
    }
    
    public PointSparse addToMe(PointSparse p) {
        p.nonZeroes().forEach(coord -> set(coord.row, get(coord.row) + coord.value));
        return this;
    }

    @Override
    public double[] array() {
        return asDense().data;
    }

    @Override
    public boolean below(RnPlane plane) {
        return plane.above(this);
    }

    @Override
    public Point concat(Point p) {
        if(p.isDense()) return concat(p.asDense());
        else return concat(p.asSparse());
    }
    
    public PointSparse(DMatrixSparseTriplet trip){
        super(trip);
    }
    
    public PointSparse(DMatrixSparseCSC csc){
        super(csc);
    }
    
    public PointSparse concat(PointD pd){
        DMatrixSparseTriplet trip = new DMatrixSparseTriplet(rows() + pd.numRows, 1, ejmlSparse.getNonZeroLength() + pd.dim());
        nonZeroes().forEach(coord -> trip.set(coord.row, 0, coord.value));
        IntStream.range(0, pd.dim()).forEach(i -> trip.set(i + dim(), 0, pd.get(i)));
        return new PointSparse(trip);
    }
    
    public PointSparse concat(PointSparse ps){
        DMatrixSparseCSC concat = new DMatrixSparseCSC(dim(), ps.dim(), ejmlSparse.getNonZeroLength() + ps.ejmlSparse.getNonZeroLength());
        CommonOps_DSCC.concatRows(ejmlSparse, ps.ejmlSparse, concat);
        return new PointSparse(concat);
    }

    @Override
    public Point concat(double d) {
        
        return concat(PointD.oneD(d).asSparse());
    }

    @Override
    public double d(Point mp) {
        return Math.sqrt(distSq(mp));
    }

    @Override
    public int dim() {
        return ejmlSparse.numRows;
    }

    @Override
    public PointSparse dir() {
        return mult(1/magnitude());
    }

    @Override
    public double distSq(Point mp) {
        Point minus = minus(mp);
        return minus.dot(minus);
    }

    @Override
    public double dot(Point p) {
        return nonZeroes().mapToDouble(coord -> coord.value * get(coord.row)).sum();
    }

    @Override
    public PointSparse dot(Matrix m) {
       PointSparse ps = new PointSparse(m.cols());
        ps.ejmlSparse = mult(m).ejmlSparse;
        return ps;
    }

    @Override
    public boolean equals(Point p) {
        return super.equals(p);
    }
    

    @Override
    public boolean equals(Point p, double acc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double get(int i) {
        return ejmlSparse.get(i, 0);
    }

    @Override
    public boolean isReal() {
        return nonZeroes().allMatch(coord -> Double.isFinite(get(coord.row)));
    }

    @Override
    public double magnitude() {
        return d(this);
    }


    @Override
    public Point minus(Point p) {
        if(p.isDense()) return minus(p.asDense());
        else return minus(p.asSparse());
    }
    public PointSparse minus(PointSparse p){
        return new PointSparse(super.minus(p).ejmlSparse);
    }
    public PointD minus(PointD p){
        return new PointD(super.minus(p).data);
    }

    @Override
    public PointSparse mult(double k) {
        return new PointSparse(super.mult(k).ejmlSparse);
    }

    @Override
    public PointSparse multMe(double k) {
        nonZeroes().forEach(coord -> set(coord.row, coord.value*k));
        return this;
    }

    @Override
    public Point plus(Point p) {
        if(p.isDense()) return plus(p.asDense());
        else return plus(p.asSparse());
    }
    
    public PointSparse plus(PointSparse p){
        return new PointSparse(super.plus(p).ejmlSparse);
    }
    public PointD plus(PointD p){
        return new PointD(super.plus(p).data);
    }


    /**
     * Don't call this for repeated use.  It has to rebuild the entire matrix.
     * @param i
     * @param y
     * @return 
     */
    @Override
    public double set(int i, double y) {
        set(i, 0, y);
        return y;
    }

    @Override
    public PointSparse set(double[] x) {
        int numNonZeroes = (int)Arrays.stream(x).filter(s -> s != 0).count();
        DMatrixSparseTriplet trip = new DMatrixSparseTriplet(x.length, 1, numNonZeroes);
        IntStream.range(0, x.length).forEach(i->trip.set(i, 0, x[i]));
        setFromTrip(trip);
        return this;
    }

    @Override
    public PointSparse set(Point x) {
        ejmlSparse = new DMatrixSparseCSC(x.asSparse().ejmlSparse);
        return this;
    }

    @Override
    public PointSparse setAll(IntToDoubleFunction f) {
         setAll((i, j) -> f.applyAsDouble(i));
         return this;
    }


    @Override
    public DoubleStream stream() {
        return IntStream.range(0, dim()).mapToDouble(i -> get(i));
    }

    @Override
    public PointSparse mapToSparse(DoubleFunction<Double> f) {
        DMatrixSparseTriplet trip = new DMatrixSparseTriplet(rows(), cols(), ejmlSparse.getNonZeroLength());
        IntStream.range(0, dim()).forEach(i -> trip.set(i, 0, f.apply(get(i))));
        return new PointSparse(trip);
    }

    @Override
    public PointD mapToDense(DoubleFunction<Double> f) {
        PointD m = new PointD(dim());
        IntStream.range(0, dim()).forEach(i -> m.set(i, f.apply(get(i))));
        return m;
    }

    @Override
    public PointD asDense() {
        return mapToDense(i -> i);
    }

    @Override
    public PointSparse asSparse() {
        return this;
    }
    
    
    
}
