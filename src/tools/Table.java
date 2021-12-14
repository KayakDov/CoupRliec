package tools;

import java.util.Arrays;

/**
 * This class is designed to print double arrays as nice tables.
 *
 * @author Dov Neimand
 */
public class Table {

    int totSpaces;

    public Table(int totSpaces) {
        this.totSpaces = totSpaces;
    }
    
    
    private String wSpaces(String print){
        StringBuilder wSpaces = new StringBuilder(print);
        while(wSpaces.length() < totSpaces) wSpaces.append(' ');
        return wSpaces.toString();
    }
    
    private void print(Object string){
        System.out.print("&" + wSpaces(string.toString()));
    }
    
    private void printRow(String rowName, Object[] row){
        print(rowName);
        for(Object st: row) print(st.toString());
        System.out.println("\\\\");
    }
    
    private void printRow(String rowName, double[] row){
        printRow(rowName, Arrays.stream(row).mapToObj(i -> (float)i).toArray(Float[]::new));
    }
    
    public void print(double[][] table, String[] headings, String[] sideBar) {
        printRow("", headings);
        for(int i = 0; i < sideBar.length; i++)
            printRow(sideBar[i], table[i]);
    }
    
    public static void main(String[] args){
        String[] top = new String[]{"left", "center", "right"};
        String[] side = new String[]{"row 1", "row 2", "row 3"};
        double[][] chart = new double[3][3];
        int k = 0;
        for(int i = 0; i < 3; i++) for(int j = 0; j < 3; j++) chart[i][j] = k++;
        new Table(8).print(chart, top, side);
    }
}
