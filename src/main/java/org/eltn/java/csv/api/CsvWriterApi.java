package org.eltn.java.csv.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import org.eltn.java.csv.enums.CellsSplitterEnum;
import org.eltn.java.csv.exceptions.CsvOperationException;
import org.eltn.projects.core.expections.InvalidValueException;
import org.eltn.projects.core.utils.StringUtil;

/*********************************************
 * CSV writer API.
 * 
 * Create CSV file.
 * API create new CSV.
 * In case the file already exists than the file will be overwritten.
 * 
 * The file will be created only after execute {@link CsvWriterApi#save()} method.
 * 
 * CSV writer API can use {@link CsvWriterApi#save()} only one time.
 * 
 * @author Eyal Tuzon
 *
 */
public class CsvWriterApi extends CsvApiBase {
    private int rowSize = -1;

    private boolean isCsvSaved = false;

    private boolean isSetHeaders = false;

    private Writer writer = null;
    private PrintWriter printWriter = null;

    /*********************************************
     * Constructor.
     * 
     * @param filePath CSV file path.
     */
    public CsvWriterApi(String filePath) {
        this(filePath, CELLS_DEFAULT_SPLITTER);
    }

    /*********************************************
     * Constructor.
     * 
     * @param filePath CSV file path.
     * @param cellsSplitter Cells splitter enum.
     */
    public CsvWriterApi(String filePath, CellsSplitterEnum cellsSplitter) {
        this(filePath, cellsSplitter.getChar());
    }

    /*********************************************
     * Constructor.
     * 
     * @param filePath CSV file path.
     * @param cellsSplitter Cells splitter char.
     */
    public CsvWriterApi(String filePath, char cellsSplitter) {
        super(filePath, cellsSplitter);
    }

    /*********************************************
     * Set CSV headers.
     * Headers can be set only one time.
     * 
     * @param headerList Header list.
     * @throws CsvOperationException in case user call the method more than one time, 
     *                               or run {@link #setHeaders(List)} after running {@link #save()} method.
     * @throws InvalidValueException in case header list is null or headers amount is different from row size.
     */
    public void setHeaders(final List<String> headerList) throws CsvOperationException, InvalidValueException {
        if (isSetHeaders) {
            throw new CsvOperationException("Headers already set and cannot set more than one time");
        }

        if (isCsvSaved()) {
            throw new CsvOperationException("Headers cannot be set after save CSV file");
        }

        validateNotNull(headerList);

        isSetHeaders = true;

        super.headerList = headerList;

        updateRowSizeAfterUpdateHeaderList();
    }

    /*********************************************
     * Add a row to CSV body.
     * 
     * @param row A row of cells.
     * @throws InvalidValueException in case row is null or row size is different 
     *         from previous headers amount or rows size.
     * @throws CsvOperationException in case run adding a row after CSV file already been saved.
     */
    public void addRow(List<String> row) throws InvalidValueException, CsvOperationException {
        if (isCsvSaved()) {
            throw new CsvOperationException("Row cannot be added because CSV file [" + filePath + "] already saved");
        }

        validateNotNull(row);
        updateRowSizeBeforeUpdateRow(row);
        csvRows.add(row);
    }

    /*********************************************
     * Add a row to CSV body in index.
     * CSV body not include headers row.
     * 
     * @param row A row of cells.
     * @param index Index of row in CSV body.
     * @throws InvalidValueException in case row is null, or index is negative.
     * @throws IndexOutOfBoundsException in case index is out of row bounds.
     * @throws CsvOperationException in case run adding a row after CSV file already been saved.
     */
    public void addRow(List<String> row, int index)
            throws InvalidValueException, IndexOutOfBoundsException, CsvOperationException {
        if (isCsvSaved()) {
            throw new CsvOperationException("Row cannot be added because CSV file [" + filePath + "] already saved");
        }

        validateNotNull(row);
        validateNotNegative(index);

        if (row.size() <= index) {
            throw new IndexOutOfBoundsException(
                    "Index [" + index + "] cannot be out of bound from row size [" + row.size() + "]");
        }

        updateRowSizeBeforeUpdateRow(row);

        csvRows.add(index, row);
    }

    /*********************************************
     * Add a row to CSV body.
     * 
     * @param rowList List of rows.
     * @throws InvalidValueException in case rowList is null.
     * @throws CsvOperationException in case run adding rows after CSV file already been saved.
     */
    public void addRows(List<List<String>> rowList) throws InvalidValueException, CsvOperationException {
        validateNotNull(rowList);

        for (List<String> row : rowList) {
            addRow(row);
        }
    }

    /*********************************************
     * Remove a row from CSV body in index place.
     * 
     * CSV body not include headers row.
     * 
     * @param index Index of the row to be removed.
     * @throws InvalidValueException in case index is negative number.
     * @throws IndexOutOfBoundsException in case index is out of CSV rows bounds.
     * @throws CsvOperationException in case remove a row after CSV file already been saved.
     */
    public void removeRow(int index) throws InvalidValueException, IndexOutOfBoundsException, CsvOperationException {
        if (isCsvSaved()) {
            throw new CsvOperationException("Row cannot be removed because CSV file [" + filePath + "] already saved");
        }

        validateNotNegative(index);

        if (csvRows.size() <= index) {
            throw new IndexOutOfBoundsException(
                    "Index [" + index + "] is out of bound from CSV rows amount [" + csvRows + "]");
        }

        csvRows.remove(index);
    }

    /*********************************************
     * Save CSV to file.
     * The file will be overwritten in case it is already exists.
     * 
     * The file can be saved only one time.
     * 
     * @throws IOException in case fail to save CSV file.
     * @throws CsvOperationException in case CSV file already been saved. 
     */
    public void save() throws IOException, CsvOperationException {
        if (isCsvSaved()) {
            throw new CsvOperationException("CSV file [" + filePath + "] already been saved");
        }

        try {
            openFileAndSetPrintWriter();
            writeCsvHeadersRowToFile();
            writeCsvRowsToFile();
        } finally {
            isCsvSaved = true;
            closeFile();
        }
    }

    /*********************************************
     * Is {@link #save()} was executed.
     * 
     * @return true in case CSV file was saved, else return false.
     */
    public boolean isCsvSaved() {
        return isCsvSaved;
    }

    private void closeFile() {
        try {
            if (printWriter != null) {
                printWriter.close();
            }

            if (writer != null) {
                writer.close();
            }
        } catch (Exception e) {
        } finally {
            writer = null;
            printWriter = null;
        }
    }

    private void writeCsvRowsToFile() {
        for (List<String> row : csvRows) {
            printWriter.println(convertCsvRowToFileRow(row));
        }
    }

    private void writeCsvHeadersRowToFile() {
        if (headerList.isEmpty() == false) {
            printWriter.println(convertCsvRowToFileRow(headerList));
        }
    }

    private void openFileAndSetPrintWriter() throws IOException {
        File file = new File(filePath);
        writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        printWriter = new PrintWriter(writer);
    }

    private void updateRowSizeBeforeUpdateRow(List<String> row) throws InvalidValueException {
        if (rowSize != -1) {
            if (rowSize != row.size()) {
                throw new InvalidValueException("Current row size [" + row.size()
                        + "] is different from CSV previous updated row size [" + rowSize + "]");
            }
        } else {
            rowSize = row.size();
        }
    }

    private void updateRowSizeAfterUpdateHeaderList() throws InvalidValueException {
        if (rowSize != -1) {
            if (rowSize != headerList.size()) {
                throw new InvalidValueException("Headers amount [" + headerList.size()
                        + "] is different from CSV previous updated rows size [" + rowSize + "]");
            }
        } else {
            rowSize = headerList.size();
        }
    }

    private String convertCsvRowToFileRow(List<String> csvRow) {
        StringBuffer rowToFile = new StringBuffer();

        for (String cell : csvRow) {
            cell = updateCellIfContainInvertedCommans(cell);
            cell = delimitWithInvertedCommasIfComplexCell(cell);
            rowToFile.append(cell).append(cellsSplitter);
        }

        return StringUtil.removeLastChar(rowToFile.toString());
    }

    private String updateCellIfContainInvertedCommans(String cell) {
        return cell.replace("\"", "\"\"");
    }

    private String delimitWithInvertedCommasIfComplexCell(String cell) {
        if ((cell.contains("\n")) || (cell.contains("\"")) || (cell.indexOf(cellsSplitter) >= 0)) {
            cell = "\"" + cell + "\"";
        }

        return cell;
    }
}