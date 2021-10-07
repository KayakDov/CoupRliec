package main;

import Convex.ASKeys.ASKey;
import Convex.ASKeys.ASKeyPCo;
import Convex.ASKeys.ASKeyPConeRI;
import Convex.LinearRn.RnAffineProjection;
import Convex.RnPolyhedron;
import Hilbert.HalfSpace;
import Hilbert.PCone;
import Hilbert.Polyhedron;
import Matricies.Point;
import Matricies.PointD;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Main {


    public static RnPolyhedron square(){
        ArrayList<HalfSpace<Point>> halfspaces = new ArrayList<>();
        halfspaces.add(new HalfSpace<>(new PointD(1,1), new PointD(1,0)));
        halfspaces.add(new HalfSpace<>(new PointD(1,1), new PointD(0,1)));
        halfspaces.add(new HalfSpace<>(new PointD(0,0), new PointD(0,-1)));
        halfspaces.add(new HalfSpace<>(new PointD(0,0), new PointD(-1,0)));
        return new RnPolyhedron(halfspaces);
    }
    

    public static void testSquare(){
        System.out.println(square().proj(new PointD(4, 7)));
    }
    
    public static void testAffineKeys(){
        HashMap<ASKey, PCone<Point>> a = new HashMap<>();
        ArrayList<HalfSpace<Point>> hsList = new ArrayList<>();
        PCone pc = new PCone(new RnAffineProjection(new PointD(0,0)), hsList, 0);
        ASKeyPCo goodKey = new ASKeyPCo(pc);
        a.put(goodKey, pc);
        System.out.println("Hashcode of good key is " + goodKey.hashCode());
        System.out.println(a.get(goodKey));
        
        HalfSpace<Point> hs = new HalfSpace<>(new PointD(0, 0), new PointD(1, 0));
        hsList = new ArrayList<>();
        hsList.add(hs);
        pc = new PCone(new RnAffineProjection(new PointD(0,0)), hsList, 0);
        
        ASKeyPCo problemKey = new ASKeyPConeRI(pc, 0);
        System.out.println("hashcode of problem key is " + problemKey.hashCode());
        
        System.out.println("good key equals problem key? " + goodKey.equals(problemKey) +" and "+ problemKey.equals(goodKey));
        
        System.out.println(a.get(problemKey));
        
        System.out.println(a);
    }
    
    public static void testProjectionFunction(){
        
    }
    
    public static void main(String[] args) throws IOException {
        
        testSquare();
    }

}
