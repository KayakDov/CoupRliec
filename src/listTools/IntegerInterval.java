package listTools;

/**
 * A tool for handling recursive functions on arrays.
 * @author Dov Neimand
 */
public class IntegerInterval {

    public int start, end;

    public IntegerInterval(int start, int end) {
        this.start = start;
        this.end = end;
    }


    public int mid() {
        return (start + end) / 2;
    }

    public IntegerInterval leftHalf() {
        return new IntegerInterval(start, mid());
    }

    public IntegerInterval rightHalf() {
        return new IntegerInterval(mid(), end);
    }
}
