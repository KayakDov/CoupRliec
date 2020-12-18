package RnSpace;

import Convex.Cube;
import RnSpace.points.Point;
import RnSpace.rntor.RnToRFunc;
import FuncInterfaces.RnToR;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Kayak
 */
public class FunctionPrinter {

    private MyWriter writer;

    public FunctionPrinter() {
    }

    /**
     * 
     * @param writeTo the file you want to write to
     * @param f the function you want to print the values of
     * @param cube the domain of the function
     * @param dx the increment you want to print at.
     */
    private FunctionPrinter(String writeTo, RnToR f, Cube cube, double dx) {
        print(writeTo, f, cube, dx);
    }
     
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void printFunc(String writeTo, RnToR f, Cube cube, double dx){
        new FunctionPrinter(writeTo, f, cube, dx);
    }
    /**
     *
     * @param writeTo the name of the file to write to.
     * @param f the function to be printed
     * @param cube the cube on which the function is to be printed
     * @param dx the interval the function is to be printed at
     */
    public void print(String writeTo, RnToR f, Cube cube, double dx) {
        Stream<Point> xStream = cube.stream(dx);
        
        writer = MyWriter.build(writeTo);
        xStream.forEach(x -> writer.writeFofX(x, f));
                
        writer.close();
    }


}

class MyWriter extends BufferedWriter {

    public MyWriter(File file) throws IOException {
        super(new FileWriter(file));
    }

    public static MyWriter build(File name) {
        try {
            return new MyWriter(name);
        } catch (IOException ex) {
            Logger.getLogger(MyWriter.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    public static MyWriter build(String name) {
        return build(new File(name));
    }

    @Override
    public void write(String str) {
        try {
            super.write(str); //To change body of generated methods, choose Tools | Templates.
        } catch (IOException ex) {
            Logger.getLogger(FunctionPrinter.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    public void writeFofX(Point vector, RnToR f) {
        StringBuilder line = new StringBuilder();
        vector.stream().forEachOrdered(d -> line.append(d).append(" "));
        line.append(f.of(vector.array())).append("\n");
        write(line.toString());
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (IOException ex) {
            Logger.getLogger(MyWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
