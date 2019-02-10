package org.eltn.java.csv.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.eltn.java.csv.exceptions.CsvOperationException;
import org.eltn.projects.core.expections.InvalidValueException;
import org.eltn.projects.core.tests.asserts.SoftAssert;
import org.eltn.projects.core.tests.exceptions.AutomationTestException;
import org.eltn.projects.core.utils.ListUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CscWriterApiTest extends CsvApiTestBase implements TestParameters {
    public static final String DIR_PATH = "src/test/resources/createCsv/";

    public static final String CSV_PATH = DIR_PATH + "tempCsv.csv";

    CsvWriterApi csvApi = null;

    @BeforeClass
    public void beforeClass() {
    }

    @BeforeMethod
    public void beforeMethod() {
        deleteFileIfExists(CSV_PATH);
    }

    @Test
    public void create_default_csv_set_headers_and_add_rows_test() throws AutomationTestException {
        createDefaultCsvAddRowsCsv();
        verifyDefaultCsv();
    }

    @Test
    public void create_default_csv_set_headers_and_add_row_in_loop_test() throws AutomationTestException {
        createDefaultCsvAddRowInLoopCsv();
        verifyDefaultCsv();
    }

    @Test
    public void create_new_csv_set_headers_and_add_rows_without_save_negative_test() throws AutomationTestException {
        createDefaultCsvAddRowsCsvWithoutSave();
        verifyFileNotExists(CSV_PATH);
        csvApi = null;
    }

    @Test
    public void create_complex_csv_set_headers_and_add_rows_test() throws AutomationTestException {
        createComplexCsvAddRowsCsv();
        verifyComplexCsv();
    }

    @Test
    public void create_complex_csv_set_headers_and_add_row_in_loop_test() throws AutomationTestException {
        createComplexCsvAddRowInLoopCsv();
        verifyComplexCsv();
    }

    @Test
    public void set_headers_twice_negative_test() throws AutomationTestException {
        csvApi = new CsvWriterApi(CSV_PATH);
        setHeaders(COMPLEX_CSV_HEADERS);

        boolean isCsvOperationException = false;

        try {
            try {
                csvApi.setHeaders(ListUtil.asList(COMPLEX_CSV_HEADERS));
            } catch (InvalidValueException e) {
                throw new AutomationTestException(e);
            }
        } catch (CsvOperationException e) {
            isCsvOperationException = true;
        }

        SoftAssert.assertTrueNow(isCsvOperationException,
                "setHeaders did not throwed CsvOperationException after called the method twice",
                "Verify that setHeaders throwed CsvOperationException after called the method twice");
    }

    @Test
    public void remove_row_from_complex_csv_test()
            throws AutomationTestException, InvalidValueException, IndexOutOfBoundsException, CsvOperationException {
        createComplexCsvAddRowsCsvWithoutSave();
        csvApi.removeRow(0);
        saveCsv();
        verifyCsv(CSV_PATH, COMPLEX_CSV_HEADERS, new String[][] { COMPLEX_CSV_BODY[1] }, GetRowEnum.GET_ROWS);
    }

    @Test
    public void create_default_csv_set_headers_and_add_row_in_index_0_test() throws AutomationTestException {
        csvApi = new CsvWriterApi(CSV_PATH);
        setHeaders(DEFAULT_CSV_HEADERS);
        addRow(DEFAULT_CSV_BODY[1]);
        addRow(DEFAULT_CSV_BODY[0], 0);
        saveCsv();

        verifyDefaultCsv();
    }

    @AfterClass
    public void afterClass() {
        deleteFileIfExists(CSV_PATH);
    }

    private void deleteFileIfExists(String filePath) {
        File file = new File(filePath);
        file.delete();
    }

    private void verifyFileNotExists(String filePath) throws AutomationTestException {
        File file = new File(CSV_PATH);

        if (SoftAssert.assertTrue(file.exists() == false,
                "CSV file [" + filePath + " should be be exists because save() method was not executed",
                "Verify that CSV file [" + filePath + " not exists") == false) {
            deleteFile(CSV_PATH);
        }

        SoftAssert.assertAll();
    }

    private void deleteFile(String filePath) throws AutomationTestException {
        File file = new File(CSV_PATH);
        try {
            if (Files.deleteIfExists(file.toPath()) == false) {
                throw new AutomationTestException("Cannot delete file [" + CSV_PATH + "] because it is not exists");
            }
        } catch (IOException e) {
            throw new AutomationTestException(e);
        }
    }

    private void verifyComplexCsv() throws AutomationTestException {
        verifyCsv(CSV_PATH, COMPLEX_CSV_HEADERS, COMPLEX_CSV_BODY, GetRowEnum.GET_ROWS);
    }

    private void verifyDefaultCsv() throws AutomationTestException {
        verifyCsv(CSV_PATH, DEFAULT_CSV_HEADERS, DEFAULT_CSV_BODY, GetRowEnum.GET_ROWS);
    }

    private void createComplexCsvAddRowInLoopCsv() throws AutomationTestException {
        csvApi = new CsvWriterApi(CSV_PATH);
        setHeaders(COMPLEX_CSV_HEADERS);
        addRowsViaCsvApiAddRowInLoop(COMPLEX_CSV_BODY);
        saveCsv();
    }

    private void createDefaultCsvAddRowInLoopCsv() throws AutomationTestException {
        csvApi = new CsvWriterApi(CSV_PATH);
        setHeaders(DEFAULT_CSV_HEADERS);
        addRowsViaCsvApiAddRowInLoop(DEFAULT_CSV_BODY);
        saveCsv();
    }

    private void createComplexCsvAddRowsCsvWithoutSave() throws AutomationTestException {
        csvApi = new CsvWriterApi(CSV_PATH);
        setHeaders(COMPLEX_CSV_HEADERS);
        addRowsViaCsvApiAddRows(COMPLEX_CSV_BODY);
    }

    private void createDefaultCsvAddRowsCsvWithoutSave() throws AutomationTestException {
        csvApi = new CsvWriterApi(CSV_PATH);
        setHeaders(DEFAULT_CSV_HEADERS);
        addRowsViaCsvApiAddRows(DEFAULT_CSV_BODY);
    }

    private void createComplexCsvAddRowsCsv() throws AutomationTestException {
        createComplexCsvAddRowsCsvWithoutSave();
        saveCsv();
    }

    private void createDefaultCsvAddRowsCsv() throws AutomationTestException {
        createDefaultCsvAddRowsCsvWithoutSave();
        saveCsv();
    }

    private void saveCsv() throws AutomationTestException {
        try {
            csvApi.save();
        } catch (IOException | CsvOperationException e) {
            throw new AutomationTestException(e);
        }
    }

    private void setHeaders(String[] headersArr) throws AutomationTestException {
        try {
            csvApi.setHeaders(ListUtil.asList(headersArr));
        } catch (CsvOperationException | InvalidValueException e) {
            throw new AutomationTestException(e);
        }
    }

    private void addRow(String[] row, int index) throws AutomationTestException {
        try {
            csvApi.addRow(ListUtil.asList(row), index);
        } catch (InvalidValueException | CsvOperationException e) {
            throw new AutomationTestException(e);
        }
    }
    
    private void addRow(String[] row) throws AutomationTestException {
        try {
            csvApi.addRow(ListUtil.asList(row));
        } catch (InvalidValueException | CsvOperationException e) {
            throw new AutomationTestException(e);
        }
    }

    private void addRowsViaCsvApiAddRowInLoop(String[][] rows) throws AutomationTestException {
        for (String[] row : rows) {
            addRow(row);
        }
    }

    private void addRowsViaCsvApiAddRows(String[][] rows) throws AutomationTestException {
        List<List<String>> rowsList = new ArrayList<List<String>>();

        for (String[] row : rows) {
            rowsList.add(ListUtil.asList(row));
        }

        try {
            csvApi.addRows(rowsList);
        } catch (InvalidValueException | CsvOperationException e) {
            throw new AutomationTestException(e);
        }
    }
}