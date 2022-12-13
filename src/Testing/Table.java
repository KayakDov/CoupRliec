package Testing;

import java.util.Arrays;

/**
 * This class is designed to print double arrays as nice tables.
 *
 * @author Dov Neimand
 */
public class Table {

    int totSpaces;

    /**
     * The spaces in the table
     * @param totSpaces 
     */
    public Table(int totSpaces) {
        this.totSpaces = totSpaces;
    }
    
    /**
     * Prints white spaces.
     * @param print
     * @return the string of white spaces.
     */
    private String wSpaces(String print){
        StringBuilder wSpaces = new StringBuilder(print);
        while(wSpaces.length() < totSpaces) wSpaces.append(' ');
        return wSpaces.toString();
    }
    
    /**
     * prints a string into the table.
     * @param string 
     */
    private void print(Object string){
        System.out.print("&" + wSpaces(string.toString()));
    }
    
    /**
     * prints a row of the table.
     * @param rowName the name of the row.
     * @param row the items in the row to be printed.
     */
    private void printRow(String rowName, Object[] row){
        print(rowName);
        for(Object st: row) print(st.toString());
        System.out.println("\\\\");
    }
    
    /**
     * prints a row.
     * @param rowName the name of the row.
     * @param row the items in the row to be printed.
     */
    private void printRow(String rowName, double[] row){
        printRow(rowName, Arrays.stream(row).mapToObj(i -> (float)i).toArray(Float[]::new));
    }
    
    /**
     * prints a table
     * @param table the items in the table to be printed.
     * @param headings the headings of each column
     * @param sideBar the headings of each row.
     */
    public void print(double[][] table, String[] headings, String[] sideBar) {
        printRow("", headings);
        for(int i = 0; i < sideBar.length; i++)
            printRow(sideBar[i], table[i]);
    }
    
}
