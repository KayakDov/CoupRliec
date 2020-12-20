package Convex;

import Matricies.Matrix;
import RnSpace.points.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import listTools.Choose;

/**
 *
 * @author Dov Neimand
 */
public class Simplex extends Polytope {

    public Simplex(Matrix points) {
        List<ArrayList<Point>> pointSets = Arrays.asList(
                new Choose<>(points.rowList(), points.cols).chooseListsArray());
        
        pointSets.forEach(set -> {
            HalfSpace hs = new HalfSpace(Matrix.fromRows(set.toArray(Point[]::new)));
            Point other = points.rowStream().filter(p -> !set.contains(p)).findAny().get();
            if (!hs.hasElement(other)) hs = hs.complement();
            add(hs);
        });
    }

    public Simplex(Point[] points) {
        this(Matrix.fromRows(points));
    }

    
//    
//    @Override
//    public Simplex setVertices() {
//        super.setVertices(); 
//        return this;
//    }
    
    

}
