
package Testing;

import Convex.LinearRn.ProhjectOntoAffine;
import Hilbert.HalfSpace;
import Matricies.Point;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * A series of projection tests.
 * @author Dov Neimand
 */
public class ProjectionTest extends Test<Point>{

    /**
     * A random non empty polyhedron that contains the given sphere centered at
     * the origin
     *
     * @param numConstraints the number of constraints
     * @param radius the distance of the half spaces from the origin
     * @param dim the number of dimensions the half spaces are in.
     * @return a set of random constraints.
     */
    public static List<HalfSpace<Point>> randomNonEmpty(int numConstraints, double radius, int dim) {
        Random rand = new Random(3);
        return IntStream.range(0, numConstraints).mapToObj(i -> {
            Point random = Point.uniformRandSphereSurface(dim, radius);
            return new HalfSpace<>(random, random);
        }).toList();
    }

    /**
     * Tests the algorithm on the projection function.
     * @param numTests the number of tests to be performed.
     * @param numDim the number of dimensions the tests are to take place in.
     * @param numConstraints the number of constraints for each polyhedron projected onto.
     * @param polyhedronRadius the distance of the constraints from the origin.
     * @param projectionPointRadius the distance of the randomly generated point to be projected from the origin.
     */
    public ProjectionTest(int numTests, int numDim, int numConstraints, int polyhedronRadius, int projectionPointRadius) {
        super(numTests, randomNonEmpty(numConstraints, polyhedronRadius, numDim), new ProhjectOntoAffine(Point.uniformRandSphereSurface(numDim, projectionPointRadius)));
    }
    
    
    
    private static final int DEFAULT_PROJ_R = 10, DEFAULT_POLY_R = 1;
    /**
     * Tests the algorithm on the projection function.
     * @param numTests the number of tests to be performed.
     * @param numDim the number of dimensions the tests are to take place in.
     * @param numConstraints the number of constraints for each polyhedron projected onto.
     * @param polyhedronRadius the distance of the constraints from the origin.
     * @param projectionPointRadius the distance of the randomly generated point to be projected from the origin.
     */
    public ProjectionTest(int numTests, int numDim, int numConstraints) {
        this(numTests, numDim, numConstraints, DEFAULT_POLY_R, DEFAULT_PROJ_R);
    }

}
