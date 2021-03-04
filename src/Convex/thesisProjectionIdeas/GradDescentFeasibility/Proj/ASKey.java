/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj;

import Convex.Linear.AffineSpace;
import Convex.Linear.Plane;

/**
 *
 * @author dov
 */
public class ASKey {

    int hashCode;

    public ASKey(Plane[] planes) {
        hashCode = 0;
        for (Plane p : planes) hashCode += p.hashCode();
    }
    public ASKey(AffineSpace as, int removeIndex){
        this.removeIndex = removeIndex;
        hashCode = 0;
        for(int i = 0; i < as.b.dim(); i++){
            if(i != removeIndex){
                hashCode += Double.hashCode(as.b.get(i));
                hashCode += as.linearSpace().normals[i].hashCode();
            }
        }
    }
    public int removeIndex;
    
    public ASKey(ASNode as){
        this(as.planeList);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        return hashCode == ((ASKey) obj).hashCode;
    }

    @Override
    public String toString() {
        return "" + hashCode;
    }
    
    

}
