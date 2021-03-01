/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Convex.thesisProjectionIdeas.GradDescentFeasibility.Proj;

import Convex.Linear.AffineSpace;
import Matricies.Point;

/**
 *
 * @author dov
 */
public class ASProj {

        public Point proj;
        public AffineSpace as;

        public ASProj(Point proj, AffineSpace as) {
            this.proj = proj;
            this.as = as;
        }

        public ASProj(Point preProj, ASNode asn) {
            this.as = asn.as;
            proj = asn.getProj(preProj);
        }

    }
