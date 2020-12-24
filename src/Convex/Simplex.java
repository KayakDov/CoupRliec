package Convex;

import Matricies.Matrix;
import Matricies.PointDense;
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
        List<ArrayList<PointDense>> pointSets = Arrays.asList(
                new Choose<>(points.rowList(), points.cols).chooseListsArray());
        
        pointSets.forEach(set -> {
            HalfSpace hs = new HalfSpace(Matrix.fromRows(set.toArray(PointDense[]::new)));
            PointDense other = points.rowStream().filter(p -> !set.contains(p)).findAny().get();
            if (!hs.hasElement(other)) hs = hs.complement();
            add(hs);
        });
    }

    public Simplex(PointDense[] points) {
        this(Matrix.fromRows(points));
    }

    
//    
//    @Override
//    public Simplex setVertices() {
//        super.setVertices(); 
//        return this;
//    }
    
    

}
