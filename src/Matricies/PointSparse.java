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
    
    /**
     * Creates a sparse point with a single nonzero value
     * @param dim the dimension of the point
     * @param i the index of the non zero value
     * @param d the non zero value
     */
  
    public PointSparse(int dim, int i, double d){
        this(dim);
        set(i, 0, d);
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

    public PointSparse(int dim, IntToDoubleFunction f) {
        super(dim, 1, (i, j) -> f.applyAsDouble(i));
         
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
