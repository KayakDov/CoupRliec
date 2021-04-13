/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj.ASKeys;

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
        if(obj instanceof ASKeyAS) return equals((ASKeyAS)obj);
        if(obj instanceof ASKeyPlanes) return equals((ASKeyPlanes)obj);
        if(obj instanceof ASKeyRI) return equals((ASKeyRI)obj);

        return equals((ASKey) obj);
    }

    public boolean equals(ASKey ask){
        return hashCode == ask.hashCode;
    }
    
    public abstract boolean equals(ASKeyAS askas);
    public abstract boolean equals(ASKeyPlanes askp);
    public abstract boolean equals(ASKeyRI askri);
    
    @Override
    public String toString() {
        return "" + hashCode;
    }
    
    

}
