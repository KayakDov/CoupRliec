/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASKeys;

import Convex.Linear.AffineSpace;
import Convex.Linear.Plane;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASFail;
import Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASNode;

/**
 *
 * @author dov
 */
public abstract class ASKey {

    int hashCode;

    public ASKey(int hash){
        this.hashCode = hash;
    }
      
    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof ASKey)) return false;

        return equals((ASKey) obj);
    }

    public boolean equals(ASKey ask){
        return hashCode == ask.hashCode;
    }
    
    public abstract boolean equals(ASKeyAS askas);
    public abstract boolean equals(ASKeyPlanes askas);
    public abstract boolean equals(ASKeyRI askas);
    
    @Override
    public String toString() {
        return "" + hashCode;
    }
    
    

}
