/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj;

import Convex.Linear.Plane;

/**
 *
 * @author dov
 */
public class ASNKey {

    Plane[] planes;
    int hashCode;

    public ASNKey(Plane[] planes) {
        this.planes = planes;
        hashCode = 0;
        for (Plane p : planes) hashCode += p.hashCode();
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

        return hashCode == ((ASNKey) obj).hashCode;
    }

}
