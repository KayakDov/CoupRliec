package Testing;

/**
 *
 * @author Dov Neimand
 */
public class ProjectionTables {

    /**
     * builds the column names for the default table
     *
     * @param numCols
     * @return
     */
    static String[] buildHeaders(int numCols) {
        String[] headers = new String[numCols];
        for (int i = 0; i < numCols; i++) {
            headers[i] = (i + 2) + " dim";
        }
        return headers;
    }

    /**
     * builds the row names for the default table.
     *
     * @param numRows
     * @param increment
     * @return
     */
    static String[] buildRowNames(int numRows, int increment) {
        String[] rowNames = new String[numRows];
        rowNames[0] = increment + " hs";
        for (int i = 1; i < numRows; i++)
            rowNames[i] = (i + 1) * increment + " hs";
        return rowNames;
    }

    /**
     * prints a table
     *
     * @param numTests the number of tests for which each entry is the average
     * value. Larger values will take longer to produce more consistent results.
     * @param numDimChecks the total number of dimensions checked. The first
     * dimension checked will always be 2.
     * @param numConstraintsChecks The number of constraints. The lowest number
     * of constraints begins with 1.
     * @param constraintIncrement The increment of the faces checked. A value of
     * 3 would, for example, check 1, 3, 6, 9 ... random linear constraints
     * @param dimIncrement The increment of the number of dimensions.
     */
    public ProjectionTables(int numTests, int numDimChecks, int numConstraintsChecks, int constraintIncrement, int dimIncrement) {
        headers = ProjectionTables.buildHeaders(numDimChecks);
        rowNames = ProjectionTables.buildRowNames(numConstraintsChecks, constraintIncrement);
        timeTable = new double[numConstraintsChecks][numDimChecks];
        fracTable = new double[numConstraintsChecks][numDimChecks];

        for (int numConstraints = 0; numConstraints < numConstraintsChecks; numConstraints++)
            for (int numDim = 0; numDim < numDimChecks; numDim++) {

                ProjectionTest test = new ProjectionTest(numTests, (numDim + 2)
                        * dimIncrement, (numConstraints + 1)
                        * constraintIncrement);

                timeTable[numConstraints][numDim] = test.averageTimes();
                fracTable[numConstraints][numDim] = test.averagFrac();
            }

    }

    private final double[][] timeTable, fracTable;
    private final String[] headers, rowNames;
    private final int whiteSpace = 15;

    /**
     * A table of fractions, the number of cones in which the algorithm called
     * the black box method over the total number of cones. A row with index i
     * contains fractions with dimensions (i + 2)* dimIncrement and a column
     * with index j contains fractions for tests with (j +
     * 1)*constraintIncrement constraints.
     *
     * @return the table
     */
    public double[][] getFracTable() {
        return fracTable;
    }

    /**
     * A table of times that each test takes. A row with index i contains
     * fractions with dimensions (i + 2)* dimIncrement and a column with index j
     * contains fractions for tests with (j + 1)*constraintIncrement
     * constraints.
     *
     * @return the table
     */
    public double[][] getTimeTable() {
        return timeTable;
    }

    /**
     * * Prints a time table and a fraction table.
     * @param includeTimeTable should the time table be printed, true for yes
     * @param incldueFracTable should the frac table be printed, true for yes
     */
    public void print(boolean includeTimeTable, boolean incldueFracTable) {
        Table table = new Table(whiteSpace);
        if (includeTimeTable) {
            table.print(timeTable, headers, rowNames);
            if (incldueFracTable) System.out.println("");
        }
        if (incldueFracTable)
            table.print(fracTable, headers, rowNames);
    }

}
