package Convex;

import Convex.Linear.Plane;
import Convex.Linear.AffineSpace;
import Matricies.Matrix;
import RnSpace.points.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;
import listTools.Choose;

/**
 *
 * @author Dov Neimand
 */
public class Hull extends Polytope {

    public static class ConvexCombination {

        private Matrix sum1;
        private Polytope lambdaPositive;

        public ConvexCombination(Matrix points) {

            sum1 = points.rowConcat(new Point(points.cols).setAll(i -> 1));

            lambdaPositive = new Polytope();
            IntStream.range(0, points.cols).mapToObj(i -> new HalfSpace(Point.Origin(points.cols), new Point(points.cols).set(i, -1)))
                    .forEach(hs -> lambdaPositive.addFace(hs));
        }

        /**
         * is the element p a convex combination of the given points?
         *
         * @param b
         * @return true if yes, false if no.
         */
        public boolean hasElement(Point b) {
            AffineSpace as = new AffineSpace(sum1.rowArray(), b.concat(1));

            try {
                return lambdaPositive.hasNonEmptyIntersection(as);
            } catch (NoSuchElementException ex) {
                return false;
            }
        }
    }

    private Matrix points;

    /**
     * A matrix whose columns are the points that form the basis of the convex
     * combination
     *
     * @param pointCols
     */
    public Hull(Matrix pointCols) {
        this.points = pointCols;
        removeNonCriticalPoints();
        buildHalfSpaces();

    }

    /**
     * Removes all points from the list of points that are not critical points.
     */
    private void removeNonCriticalPoints() {

        for (int i = 0; i < points.cols; i++)
            if (new ConvexCombination(points.removeCol(i)).hasElement(points.col(i))) {
                points = points.removeCol(i);
                i--;
            }

        setVertices();
    }

    /**
     * Takes in a potential half space, and returns it if it's part of this
     * polytope. If the complement of it is part of this polytope, then that's
     * returned. Otheriwse, returns null;
     *
     * @param plane
     * @return
     */
    private List<HalfSpace> goodHalfSpace(Plane plane) {
        List<HalfSpace> goodHalfSpace = new ArrayList<>(2);

        if (points.colStream().allMatch(p -> plane.above(p) || plane.hasElement(p, epsilon)))
            goodHalfSpace.add(new HalfSpace(plane));
        if (points.colStream().allMatch(p -> plane.below(p) || plane.hasElement(p, epsilon)))
            goodHalfSpace.add(new HalfSpace(plane.flipNormal()));

        return goodHalfSpace;
    }

    /**
     * Note, this does not work for line segments in two dimensional space
     * because it only generates 2 half spaces to describe the length of the
     * line. I do not know if this problem manifests itself with other shapes in
     * other dimensions (planes in 4 dimensional space?) creates a set of half
     * spaces to define this polytope from the convex combination.
     */
    private void buildHalfSpaces() {

        AffineSpace containingAS = AffineSpace.smallestContainingSubSpace(points.T(), epsilon);
        
        System.out.println("Convex.Hull.buildHalfSpaces()");
        System.out.println("dim = " + containingAS.subSpaceDim());

        addFaces(new Polytope(
                new Choose<>(points.colList(), (int) containingAS.subSpaceDim()).chooseStream(false)
                        .map(pointList -> goodHalfSpace(new Plane(HSSurfacePoints(pointList))))
                        .flatMap(hsList -> hsList.stream())
                        .filter(hs -> hs != null)
        ));
        if (containingAS.subSpaceDim() < dim())
            addFaces(containingAS.asPolytope());

    }

    /**
     * This function chooses extra suitable points when the number provided is
     * insufficient to form a hyperplane.
     *
     * @param pointList
     * @return
     */
    private Matrix HSSurfacePoints(List<Point> pointList) {

        Matrix pointsPlus = new Matrix(points.T());
        AffineSpace containingAS = AffineSpace.smallestContainingSubSpace(pointsPlus, epsilon);

        for (int i = pointList.size(); i < dim(); i++) {

            Point notInSpace = containingAS.notInSpace();

            pointList.add(notInSpace);
            pointsPlus = pointsPlus.rowConcat(notInSpace);
            containingAS = AffineSpace.smallestContainingSubSpace(pointsPlus, epsilon);
        }

        return Matrix.fromRows(pointList);
    }

    @Override
    public int dim() {
        return points.rows;
    }

    @Override
    protected Hull setVertices() {
        this.vertices = points.T();
        return this;
    }

}
