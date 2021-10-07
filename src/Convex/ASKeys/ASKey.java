package Convex.ASKeys;

/**
 * A key that can be generated from a half space or set of planes that calls a
 * specific affine space or polyhedral cone from hashsets
 *
 * @author Dov Neimand
 */
public abstract class ASKey {

    /**
     * The hash code.
     */
    int hashCode;

    /**
     * A constructor. This constructor should be called if you know the hash
     * code of the item you want to call.
     *
     * @param hash the hashcode of the affine space or cone sought.
     */
    public ASKey(int hash) {
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

        if (obj instanceof ASKeyPCo) return equals((ASKeyPCo) obj);

        return equals((ASKey) obj);
    }

    /**
     * The default equals operator.
     *
     * @param ask
     * @return
     */
    public boolean equals(ASKey ask) {
        return hashCode == ask.hashCode;
    }


    public abstract boolean equals(ASKeyPCo askaco);

    @Override
    public String toString() {
        return "" + hashCode;
    }
}
