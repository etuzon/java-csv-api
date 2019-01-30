package org.eltn.java.csv.api;

import java.io.IOException;
import java.util.List;

import org.eltn.java.csv.api.CsvReaderApi.CellsSplitterEnum;
import org.eltn.projects.core.expections.InvalidValueException;
import org.eltn.projects.core.tests.asserts.SoftAssert;
import org.eltn.projects.core.tests.base.BaseTest;
import org.eltn.projects.core.tests.exceptions.AutomationTestException;
import org.eltn.projects.core.utils.ListUtil;
import org.eltn.projects.core.utils.StringUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CsvReaderApiTest extends BaseTest {
	private enum GetRowEnum {
		GET_ROW, GET_ROWS
	}

	public static final String DIR_PATH = "src/test/resources/csvFiles/";

	public static final String EMPTY_FILE_PATH = DIR_PATH + "emptyFile.csv";
	public static final String CSV_FILE_CONTAINS_ONLY_HEADERS_PATH = DIR_PATH + "containOnlyHeaders.csv";
	public static final String DEFAULT_CSV_FILE_PATH = DIR_PATH + "defaultFile.csv";
	public static final String COMPLEX_CSV_FILE_PATH = DIR_PATH + "complexFile.csv";
	public static final String TAB_SEPARETOR_CSV_FILE_PATH = DIR_PATH + "tabSeparetor.csv";

	public static final String HEADER_NOT_EXISTS = "Header Not Exists";
	public static final String CELL_NOT_EXISTS = "Cell Not Exists";
	public static final int OUT_OF_BOUND_ROW_INDEX = 99999;
	public static final int NEGATIVE_ROW_INDEX = -1;

	public static final String[] DEFAULT_CSV_HEADERS = { "header1", "header2", "header3" };

	public static final String[][] DEFAULT_CSV_BODY = { { "line11", "line12", "line13" },
			{ "line21", "line22", "line23" } };

	public static final String[] COMPLEX_CSV_HEADERS = { "header,comma", "header\n2 lines", "header \" invered comma",
			"header \"2 inverted commas\"" };

	public static final String[][] COMPLEX_CSV_BODY = {
			{ "line\n2 lines", "line end with inverted comma\"", "\"line start and end with inverted commas\"", "\n\"" },
			{ "", "test2", "test3 \"\nE1\"", "" } };

	public static final String CELL_LOCATION_STR_TEMPLATE_BASE = "ell in row [" + StringUtil.REPLACE_STR + "], column ["
			+ StringUtil.REPLACE_STR + "] in CSV file [" + StringUtil.REPLACE_STR + "]";

	public static final String CELL_LOCATION_START_WITH_CAPITAL_STR_TEMPLATE = "C" + CELL_LOCATION_STR_TEMPLATE_BASE;
	public static final String CELL_LOCATION_STR_TEMPLATE = "c" + CELL_LOCATION_STR_TEMPLATE_BASE;

	private CsvReaderApi defaultCsv = null;

	@BeforeClass
	public void beforeClass() throws AutomationTestException {
		defaultCsv = readCsvFile(DEFAULT_CSV_FILE_PATH);
	}

	@Test
	public void verify_empty_file_test() throws AutomationTestException {
		CsvReaderApi csv = readCsvFile(EMPTY_FILE_PATH);

		SoftAssert.assertTrue(csv.isCsvEmpty(),
				"CSV [" + EMPTY_FILE_PATH + "] isCsvEmpty() value is [false] and should be [true]");
		SoftAssert.assertTrue(csv.getHeaderList().size() == 0,
				"Empty CSV file [" + EMPTY_FILE_PATH + "] should not contains headers ["
						+ ListUtil.getMultilineStringFromList(csv.getHeaderList()) + "]",
				"Verify empty CSV file [" + EMPTY_FILE_PATH + "] not contains headers");
		SoftAssert.assertTrue(csv.getRowsAmount() == 0,
				"Empty CSV file [" + EMPTY_FILE_PATH + "] should not conrains rows",
				"Verify empty CSV file [" + EMPTY_FILE_PATH + "] not contains rows");

		SoftAssert.assertAll();
	}

	@Test
	public void verify_csv_file_contains_only_headers_test() throws AutomationTestException {
		CsvReaderApi csv = readCsvFile(CSV_FILE_CONTAINS_ONLY_HEADERS_PATH);
		verifyCsvHeaders(csv, DEFAULT_CSV_HEADERS);
	}

	@Test
	public void verify_cells_values_from_default_csv_file_test() throws AutomationTestException {
		verifyCsv(DEFAULT_CSV_FILE_PATH, DEFAULT_CSV_HEADERS, DEFAULT_CSV_BODY, GetRowEnum.GET_ROW);
	}

	@Test
	public void verify_cells_values_from_complex_csv_file_test() throws AutomationTestException {
		verifyCsv(COMPLEX_CSV_FILE_PATH, COMPLEX_CSV_HEADERS, COMPLEX_CSV_BODY, GetRowEnum.GET_ROWS);
	}

	@Test
	public void verify_cells_values_from_tab_separetor_csv_file_test() throws AutomationTestException {
		verifyCsv(TAB_SEPARETOR_CSV_FILE_PATH, DEFAULT_CSV_HEADERS, DEFAULT_CSV_BODY, CellsSplitterEnum.TAB,
				GetRowEnum.GET_ROW);
	}

	@Test
	public void verify_getColumn_on_default_csv_test() throws AutomationTestException {
		for (int i = 0; i < DEFAULT_CSV_HEADERS.length; i++) {
			verifyCsvColumn(defaultCsv, DEFAULT_CSV_HEADERS[i], DEFAULT_CSV_BODY, i);
		}

		SoftAssert.assertAll();
	}

	@Test
	public void verify_getColumnIndex_on_default_csv_test() throws AutomationTestException {
		for (int i = 0; i < DEFAULT_CSV_HEADERS.length; i++) {
			int currentIndex = defaultCsv.getColumnIndex(DEFAULT_CSV_HEADERS[i]);
			SoftAssert.assertTrue(currentIndex == i, "CSV [" + defaultCsv.getPath() + "] column ["
					+ DEFAULT_CSV_HEADERS[i] + "] index is [" + currentIndex + "] and should be [" + i + "]");
		}

		SoftAssert.assertAll();
	}

	@Test
	public void verify_getHeaderList_on_default_csv_test() throws AutomationTestException {
		List<String> currentHeaderList = defaultCsv.getHeaderList();

		SoftAssert.assertNotNullNow(currentHeaderList,
				"Header list for CSV [" + defaultCsv.getPath() + "] should not be null");

		if (SoftAssert.assertTrue(DEFAULT_CSV_HEADERS.length == currentHeaderList.size(),
				"Header list size of CSV [" + defaultCsv.getPath() + "] is [" + currentHeaderList.size()
						+ "] and should be [" + DEFAULT_CSV_HEADERS.length + "]",
				"Verify that header list size of CSV [" + defaultCsv.getPath() + "] should be ["
						+ DEFAULT_CSV_HEADERS.length + "]")) {
			for (int i = 0; i < DEFAULT_CSV_HEADERS.length; i++) {
				String currentHeader = currentHeaderList.get(i);
				String expectedHeader = DEFAULT_CSV_HEADERS[i];
				int columnNumber = i + 1;
				SoftAssert.assertTrue(expectedHeader.equals(currentHeader),
						"Header in CSV [" + defaultCsv.getPath() + "] in column number [" + columnNumber + "] is ["
								+ currentHeader + "] and should be [" + expectedHeader + "]",
						"Verify that header in CSV [" + defaultCsv.getPath() + "] in column number [" + columnNumber
								+ "] is [" + expectedHeader + "]");
			}
		}

		SoftAssert.assertAll();
	}

	@Test
	public void verify_getRow_on_default_csv_test() throws AutomationTestException {
		for (int i = 0; i < DEFAULT_CSV_BODY.length; i++) {
			verifyCsvRow(defaultCsv, DEFAULT_CSV_BODY, i);
		}

		SoftAssert.assertAll();
	}

	@Test
	public void verify_getRowIndex_on_default_csv_test() throws AutomationTestException {
		for (int i = 0; i < DEFAULT_CSV_BODY.length; i++) {
			for (int j = 0; j < DEFAULT_CSV_BODY[i].length; j++) {
				String headerName = DEFAULT_CSV_HEADERS[j];
				String cell = DEFAULT_CSV_BODY[i][j];
				int currentRowIndex = defaultCsv.getRowIndex(headerName, cell);
				SoftAssert.assertTrue(currentRowIndex == i,
						"Row index in CSV [" + defaultCsv.getPath() + "] for header [" + headerName
								+ "] and cell value [" + cell + "] is [" + currentRowIndex + "] and should be [" + i
								+ "]",
						"Verify that row index in CSV [" + defaultCsv.getPath() + "] for header [" + headerName
								+ "] and cell value [" + cell + "] is [" + i + "]");
			}
		}

		SoftAssert.assertAll();
	}

	@Test
	public void verify_getRowsAmmount_on_default_csv_test() throws AutomationTestException {
		SoftAssert.assertTrueNow(defaultCsv.getRowsAmount() == DEFAULT_CSV_BODY.length,
				"CSV [" + defaultCsv.getPath() + "] rows amount is [" + defaultCsv.getRowsAmount() + "] and should be ["
						+ DEFAULT_CSV_BODY.length + "]",
				"Verify that CSV [" + defaultCsv.getPath() + "] rows amount is [" + DEFAULT_CSV_BODY.length + "]");
	}

	@Test
	public void verify_getFieldValue_on_default_csv_test() throws AutomationTestException {
		for (int i = 0; i < DEFAULT_CSV_BODY.length; i++) {
			for (int j = 0; j < DEFAULT_CSV_BODY[i].length; j++) {
				String currentCell;
				try {
					currentCell = defaultCsv.getFieldValue(DEFAULT_CSV_HEADERS[j], i);
				} catch (Exception e) {
					throw new AutomationTestException(e);
				}
				String expectedCell = DEFAULT_CSV_BODY[i][j];

				SoftAssert.assertNotNullNow(currentCell,
						"CSV [" + defaultCsv.getPath() + "] getFieldValue value of header [" + DEFAULT_CSV_HEADERS[j]
								+ "] and row index [" + i + "] should not be null");

				SoftAssert.assertTrueNow(expectedCell.equals(currentCell),
						"CSV [" + defaultCsv.getPath() + "] getFieldValue value of header [" + DEFAULT_CSV_HEADERS[j]
								+ "] and row index [" + i + "] is [" + currentCell + "] and should be [" + expectedCell
								+ "]",
						"Verify that CSV [" + defaultCsv.getPath() + "] getFieldValue value of header ["
								+ DEFAULT_CSV_HEADERS[j] + "] and row index [" + i + "] is [" + expectedCell + "]");
			}
		}
	}

	@Test
	public void csv_file_not_found_negative_test() {
		final String path = "/dir/fileNotFound.csv";

		boolean isException = false;

		try {
			readCsvFile(path);
		} catch (AutomationTestException e) {
			isException = true;
		}

		SoftAssert.assertTrueNow(isException,
				"Exception should appear when trying to parse file that not exists in path [" + path + "]",
				"Verify that exception appear when trying to parse csv file that not exists in path [" + path + "]");
	}

	@Test
	public void getColumnIndex_with_header_not_exists_negative_test() throws AutomationTestException {
		int index = defaultCsv.getColumnIndex(HEADER_NOT_EXISTS);

		SoftAssert.assertTrueNow(index == -1,
				"Index for CSV [" + defaultCsv.getPath() + " with header that not exists as a parameter ["
						+ HEADER_NOT_EXISTS + "] value is [" + index + "] and should be [-1]",
				"Verify that index for CSV [" + defaultCsv.getPath() + " with header that not exists as a parameter ["
						+ HEADER_NOT_EXISTS + "] value is [-1]");
	}

	@Test
	public void getColumn_with_header_not_exists_negative_test() throws AutomationTestException {
		CsvReaderApi csv = readCsvFile(DEFAULT_CSV_FILE_PATH);
		List<String> columnList = csv.getColumn(HEADER_NOT_EXISTS);
		SoftAssert.assertTrueNow(columnList == null,
				"getColumn result on CSV [" + defaultCsv.getPath() + "] with header that not exists ["
						+ HEADER_NOT_EXISTS + "] should be null",
				"Verify that getColumn result on CSV [" + defaultCsv.getPath() + "] with header that not exists ["
						+ HEADER_NOT_EXISTS + "] is null");
	}

	@Test
	public void getFieldValue_with_out_of_bound_row_index_negative_test() throws AutomationTestException {
		boolean isException = false;
		try {
			defaultCsv.getFieldValue(DEFAULT_CSV_HEADERS[0], OUT_OF_BOUND_ROW_INDEX);
		} catch (IndexOutOfBoundsException e) {
			isException = true;
		} catch (InvalidValueException e) {
			throw new AutomationTestException(e);
		}

		SoftAssert.assertTrueNow(isException,
				"getFieldValue on CSV [" + defaultCsv.getPath()
						+ "] with row index out of bound value should throw IndexOutOfBoundsException",
				"Verify that getFieldValue on CSV [" + defaultCsv.getPath()
						+ "] with row index out of bound value throw IndexOutOfBoundsException");
	}

	@Test
	public void getFieldValue_with_negative_index_negative_test() throws AutomationTestException {
		boolean isException = false;
		try {
			defaultCsv.getFieldValue(DEFAULT_CSV_HEADERS[0], NEGATIVE_ROW_INDEX);
		} catch (IndexOutOfBoundsException e) {
			throw new AutomationTestException(e);
		} catch (InvalidValueException e) {
			isException = true;
		}

		SoftAssert.assertTrueNow(isException,
				"getFieldValue on CSV [" + defaultCsv.getPath()
						+ "] with negative row index should throw InvalidValueException",
				"Verify that getFieldValue on CSV [" + defaultCsv.getPath()
						+ "] with negative row index throw InvalidValueException");
	}

	@Test
	public void getFieldValue_with_header_not_exists_negative_test() throws InvalidValueException {
		String cell = defaultCsv.getFieldValue(HEADER_NOT_EXISTS, 0);

		SoftAssert.assertTrueNow(cell == null,
				"getFieldValue on CSV [" + defaultCsv.getPath()
						+ "] with header that not exists should be null but is [" + cell + "]",
				"Verify that getFieldValue on CSV [" + defaultCsv.getPath() + "] with header that not exists in null");
	}

	@Test
	public void getRow_with_negative_row_index_negative_test() throws AutomationTestException {
		boolean isException = false;

		try {
			defaultCsv.getRow(NEGATIVE_ROW_INDEX);
		} catch (InvalidValueException e) {
			isException = true;
		} catch (IndexOutOfBoundsException e) {
			throw new AutomationTestException(e);
		}

		SoftAssert.assertTrue(isException,
				"getRow on CSV [" + defaultCsv.getPath()
						+ "] with negative row index should throw InvalidValueException",
				"Verify that getRow on CSV [" + defaultCsv.getPath()
						+ "] with negative row index throw InvalidValueException");
	}

	@Test
	public void getRow_with_out_of_bounds_row_index_negative_test() throws AutomationTestException {
		boolean isException = false;

		try {
			defaultCsv.getRow(OUT_OF_BOUND_ROW_INDEX);
		} catch (InvalidValueException e) {
			throw new AutomationTestException(e);
		} catch (IndexOutOfBoundsException e) {
			isException = true;
		}

		SoftAssert.assertTrue(isException,
				"getRow on CSV [" + defaultCsv.getPath()
						+ "] with out of bounds row index should throw IndexOutOfBoundsException",
				"Verify that getRow on CSV [" + defaultCsv.getPath()
						+ "] with out of bounds row index throw IndexOutOfBoundsException");
	}

	@Test
	public void getRowIndex_with_header_not_exists_negative_test() throws AutomationTestException {
		int index = defaultCsv.getRowIndex(HEADER_NOT_EXISTS, DEFAULT_CSV_BODY[0][0]);
		SoftAssert.assertTrueNow(index == -1,
				"Row index in CSV [" + defaultCsv.getPath() + "] for header that not exists is [" + index
						+ "] and should be [-1]",
				"Verify that row index in CSV [" + defaultCsv.getPath() + "] for header that not exists is [-1]");
	}

	@Test
	public void getRowIndex_with_cell_not_exists_negative_test() throws AutomationTestException {
		int index = defaultCsv.getRowIndex(DEFAULT_CSV_HEADERS[0], CELL_NOT_EXISTS);
		SoftAssert.assertTrueNow(index == -1,
				"Row index in CSV [" + defaultCsv.getPath() + "] for header [" + DEFAULT_CSV_HEADERS[0]
						+ "] and cell that not exists [" + CELL_NOT_EXISTS + "] is [" + index + "] and should be [-1]",
				"Verify that row index in CSV [" + defaultCsv.getPath() + "] for header [" + DEFAULT_CSV_HEADERS[0]
						+ "] and cell that not exists [" + CELL_NOT_EXISTS + "] is [-1]");
	}

	private void verifyCsvRow(CsvReaderApi csv, String[][] expectedCsvBody, int rowIndex)
			throws AutomationTestException {
		try {
			List<String> row = csv.getRow(rowIndex);
			int rowNumber = rowIndex + 2;
			SoftAssert.assertNotNullNow(row,
					"Row in CSV [" + csv.getPath() + "] in row number [" + rowNumber + "] should not be null");
			if (SoftAssert.assertTrue(expectedCsvBody[rowIndex].length == row.size(),
					"Row number [" + rowNumber + "] length in CSV [" + csv.getPath() + "] is [" + row.size()
							+ "] and should be [" + expectedCsvBody[rowIndex].length + "]")) {
				for (int i = 0; i < row.size(); i++) {
					int columnNumber = i + 1;
					String currentCell = row.get(i);
					SoftAssert.assertTrue(expectedCsvBody[rowIndex][i].equals(currentCell),
							"Column in CSV [" + csv.getPath() + " in row number [" + rowNumber + "] in column number ["
									+ columnNumber + "] is [" + currentCell + "] and should be ["
									+ expectedCsvBody[rowIndex][i] + "]",
							"Verify that cell in CSV [" + csv.getPath() + " in row number [" + rowNumber
									+ "] in column number [" + columnNumber + "] is [" + expectedCsvBody[rowIndex][i]
									+ "]");
				}
			}
		} catch (InvalidValueException e) {
			throw new AutomationTestException(e);
		}
	}

	private void verifyCsvColumn(CsvReaderApi csv, String headerName, String[][] expectedCsvBody,
			int columnIndexInExpectedCsvBody) {
		List<String> column = csv.getColumn(headerName);

		SoftAssert.assertNotNullNow(column, "Column list of header [" + headerName + "] should not be null");

		if (SoftAssert.assertTrue(expectedCsvBody.length == column.size(), "CSV [" + csv.getPath() + "] column ["
				+ headerName + "] size is [" + column.size() + "] and should be [" + expectedCsvBody.length + "]")) {
			for (int i = 0; i < column.size(); i++) {
				String currentCell = column.get(i);
				String expectedCell = expectedCsvBody[i][columnIndexInExpectedCsvBody];
				SoftAssert.assertTrue(expectedCell.equals(currentCell),
						"Cell in CSV file [" + csv.getPath() + "] in column [" + headerName + "] is [" + currentCell
								+ "] and should be [" + expectedCell + "]",
						"Verify that cell in CSV file [" + csv.getPath() + "] in column [" + headerName + "] is ["
								+ expectedCell + "]");
			}
		}
	}

	private void verifyCsv(String csvPath, String[] expectedCsvHeaders, String[][] expectedCsvBody,
			CellsSplitterEnum cellSplitter, GetRowEnum getRowEnum) throws AutomationTestException {
		try {
			CsvReaderApi csv = readCsvFile(csvPath, cellSplitter);
			verifyCsvHeaders(csv, expectedCsvHeaders);
			verifyCsvBody(csv, expectedCsvBody, getRowEnum);
		} finally {
			SoftAssert.assertAll();
		}
	}

	private void verifyCsv(String csvPath, String[] expectedCsvHeaders, String[][] expectedCsvBody,
			GetRowEnum getRowEnum) throws AutomationTestException {
		try {
			CsvReaderApi csv = readCsvFile(csvPath);
			verifyCsvHeaders(csv, expectedCsvHeaders);
			verifyCsvBody(csv, expectedCsvBody, getRowEnum);
		} finally {
			SoftAssert.assertAll();
		}
	}

	private void verifyCsvBody(CsvReaderApi csv, String[][] expectedBody, GetRowEnum getRowEnum)
			throws AutomationTestException {
		try {
			for (int i = 0; i < expectedBody.length; i++) {
				List<String> currentRow = getRow(i, getRowEnum, csv);

				if (SoftAssert.assertTrue(expectedBody[i].length == currentRow.size(),
						"Row number [" + (i + 1) + "] size in csv file [" + csv.getPath() + "] is [" + currentRow.size()
								+ "] and should be [" + expectedBody[i].length + "]")) {
					verifyCsvLine(currentRow, ListUtil.asList(expectedBody[i]), i, csv.getPath());
				}
			}
		} catch (Exception e) {
			throw new AutomationTestException(e);
		}
	}

	private List<String> getRow(int index, GetRowEnum getRowEnum, CsvReaderApi csv)
			throws InvalidValueException, IndexOutOfBoundsException {
		if (getRowEnum == GetRowEnum.GET_ROW) {
			return csv.getRow(index);
		}

		return csv.getRows().get(index);
	}

	private void verifyCsvLine(List<String> currentRow, List<String> expectedRow, int rowIndex, String csvPath)
			throws Exception {
		int rowNumber = rowIndex + 2;
		if (SoftAssert.assertTrue(expectedRow.size() == currentRow.size(),
				"Row number [" + rowNumber + "] size in csv file [" + csvPath + "] is [" + currentRow.size()
						+ "] and should be [" + expectedRow.size() + "]")) {

			for (int i = 0; i < currentRow.size(); i++) {
				String currentCell = currentRow.get(i);
				String expectedCell = expectedRow.get(i);
				int columnNumber = i + 1;

				String cellLocationCapitalStr = StringUtil.replace(CELL_LOCATION_START_WITH_CAPITAL_STR_TEMPLATE,
						String.valueOf(rowNumber), String.valueOf(columnNumber), csvPath);
				String cellLocationStr = StringUtil.replace(CELL_LOCATION_STR_TEMPLATE, String.valueOf(rowNumber),
						String.valueOf(columnNumber), csvPath);

				if (SoftAssert.assertTrue(currentCell != null, cellLocationCapitalStr + " should not be null")) {
					SoftAssert.assertTrue(expectedCell.equals(currentCell),
							cellLocationStr + " value is [" + currentCell + "] and should be [" + expectedCell + "]",
							"Verify that " + cellLocationStr + " value is [" + expectedCell + "]");
				}
			}
		}
	}

	private void verifyCsvHeaders(CsvReaderApi csv, String[] expectedHeaders) {
		final int EXPECTED_HEADER_AMOUNT = expectedHeaders.length;
		SoftAssert.assertTrueNow(csv.getHeaderList().size() == EXPECTED_HEADER_AMOUNT,
				"Headers amount is [" + csv.getHeaderList().size() + "] and should be [" + EXPECTED_HEADER_AMOUNT
						+ "]. Headers [" + ListUtil.getMultilineStringFromList(csv.getHeaderList()) + "]",
				"Verify header amount is [" + EXPECTED_HEADER_AMOUNT + "]");

		for (int i = 0; i < expectedHeaders.length; i++) {
			SoftAssert.assertTrueNow(csv.getHeaderList().get(i).equals(expectedHeaders[i]),
					"Header in index [" + i + "] is [" + csv.getHeaderList().get(i) + "] and should be ["
							+ expectedHeaders[i] + "]",
					"Verify header in index [" + i + "] is [" + expectedHeaders[i] + "]");
		}

		SoftAssert.assertAll();
	}

	private CsvReaderApi readCsvFile(String csvPath) throws AutomationTestException {
		CsvReaderApi csv = null;

		try {
			csv = new CsvReaderApi(csvPath);
		} catch (IOException e) {
			throw new AutomationTestException(e);
		}

		return csv;

	}

	private CsvReaderApi readCsvFile(String csvPath, CellsSplitterEnum cellSpliter) throws AutomationTestException {
		CsvReaderApi csv = null;

		try {
			csv = new CsvReaderApi(csvPath, cellSpliter);
		} catch (IOException e) {
			throw new AutomationTestException(e);
		}

		return csv;
	}
}