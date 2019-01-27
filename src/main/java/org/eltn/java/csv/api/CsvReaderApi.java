package org.eltn.java.csv.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.eltn.projects.core.expections.InvalidValueException;
import org.eltn.projects.core.utils.StringUtil;

public class CsvReaderApi {
	public enum CellsSplitterEnum {
		TAB('\t'), SPACE(' '), COMMA(',');

		private final char name;

		private CellsSplitterEnum(char name) {
			this.name = name;
		}

		public char getName() {
			return name;
		}
	}

	public static final char CELLS_DEFAULT_SPLITTER = ',';

	private final String path;
	private List<String> headerList = new ArrayList<String>();
	private final List<List<String>> csvRows = new ArrayList<List<String>>();;

	private String cell = "";
	private List<String> cellsInLine = new ArrayList<String>();
	private boolean isCellInInvertedComma = false;

	private final char cellsSplitter;

	public CsvReaderApi(String path) throws IOException {
		this(path, CELLS_DEFAULT_SPLITTER);
	}

	public CsvReaderApi(String path, CellsSplitterEnum cellsSplitter) throws IOException {
		this(path, cellsSplitter.getName());
	}

	public CsvReaderApi(String path, char cellsSplitter) throws IOException {
		this.path = path;
		this.cellsSplitter = cellsSplitter;

		parseCsv(path);
		setHeaderList();
	}

	public int getRowsAmount() {
		if (csvRows == null) {
			return -1;
		}

		return csvRows.size();
	}

	public boolean isCsvEmpty() {
		return getRowsAmount() < 1;
	}

	public List<String> getHeaderList() {
		return headerList;
	}

	public int getColumnIndex(String headerName) {
		for (int i=0; i < headerList.size(); i++) {
			if (headerList.get(i).equals(headerName)) {
				return i;
			}
		}

		return -1;
	}

	public int getRowIndex(String headerName, String value) {
		int columnIndex = getColumnIndex(headerName);

		if (columnIndex == -1) {
			return -1;
		}

		for (int i=0; i < csvRows.size(); i++) {
			List<String> row = csvRows.get(i);
			
			if (row.size() <= columnIndex) {
				return -1;
			}
			
			if (row.get(columnIndex).equals(value)) {
				return i;
			}
		}

		return -1;
	}

	public List<String> getColumn(String headerName) {
		List<String> columnValues = new ArrayList<String>();

		int index = getColumnIndex(headerName);

		if (index == -1) {
			return columnValues;
		}

		for (List<String> row : csvRows) {
			columnValues.add(row.get(index));
		}

		return columnValues;
	}

	public String getFieldValue(String headerName, int rowIndex) {
		List<String> columnFields = getColumn(headerName);

		if (rowIndex >= columnFields.size()) {
			return "";
		}

		return columnFields.get(rowIndex);
	}

	public List<List<String>> getRows() {
		return csvRows;
	}

	public List<String> getRow(int index) throws InvalidValueException {
		if (index < 0) {
			throw new InvalidValueException("Index value [" + index + "] should not be less than 0");
		}

		if (index < getRowsAmount()) {
			return csvRows.get(index);
		}

		throw new InvalidValueException(
				"Index [" + index + "] is out of bound. CSV rows amount [" + getRowsAmount() + "]");
	}

	public String getPath() {
		return path;
	}

	private void parseCsv(String path) throws IOException {
		BufferedReader buff = null;

		try {
			buff = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(path)), StandardCharsets.UTF_8));

			String row = "";

			while (row != null) {
				row = buff.readLine();

				if (row != null) {
					parsePreFormattedRow(row);

					if (isCellInInvertedComma == false) {
						csvRows.add(cellsInLine);
						cellsInLine = new ArrayList<String>();
					}
				}
			}
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("CSV file [" + path + "] was not found");
		} finally {
			if (buff != null) {
				buff.close();
			}
		}
	}

	private void parsePreFormattedRow(String row) {
		List<String> preFormattedCellList = StringUtil.split(row, cellsSplitter);

		for (int i=0; i < preFormattedCellList.size(); i++) {
			String preFormattedCell = preFormattedCellList.get(i);

			if (isCellInInvertedComma) {
				parsePreCellWhenItIsInInvertedComma(i, preFormattedCell);
			} else {
				parsePreCellWhenItIsNotInInvertedComma(preFormattedCell);
			}
		}
	}

	private void parsePreCellWhenItIsInInvertedComma(int preFormattedCellIndex, String preFormattedCellInRow) {
		// In case pre parsed cell already in inverted comma and this is first pre
		// parsed cell in row
		if (preFormattedCellIndex == 0) {
			cell += "\n" + preFormattedCellInRow;
		} else {
			cell += cellsSplitter + preFormattedCellInRow;
		}

		boolean isOddInvertedCommas = isOddInvertedCommasFromEnd(preFormattedCellInRow);

		if (isOddInvertedCommas) {
			isCellInInvertedComma = false;
			cell = removeCsvInvertedCommasInCell(cell);
			cellsInLine.add(cell);
			cell = "";
		}
	}

	private void parsePreCellWhenItIsNotInInvertedComma(String preFormattedCell) {
		if (preFormattedCell.isEmpty()) {
			cellsInLine.add("");
		} else {
			if (preFormattedCell.startsWith("\"")) {
				parseCellStartWithInvertedComma(preFormattedCell);
			} else {
				cellsInLine.add(preFormattedCell);
			}
		}
	}

	private void parseCellStartWithInvertedComma(String preFormattedCell) {
		boolean isOddInvertedCommas = isOddInvertedCommasFromBegin(preFormattedCell);

		if (isOddInvertedCommas) {
			if (isOddInvertedCommasFromEnd(preFormattedCell)) {
				String str = removeCsvInvertedCommasInCell(preFormattedCell);
				cellsInLine.add(str);
			} else {
				isCellInInvertedComma = true;
				cell = preFormattedCell;
			}
		} else {
			String str = removeCsvInvertedCommasInCell(preFormattedCell);
			cellsInLine.add(str);
		}
	}

	private boolean isOddInvertedCommasFromBegin(String field) {
		if (field == null) {
			return false;
		}

		if (field.startsWith("\"") == false) {
			return false;
		}

		int count = 0;

		for (int i=0; i < field.length(); i++) {
			char c = field.charAt(i);
			if (c == '"') {
				count++;
			} else {
				break;
			}
		}

		return Math.abs(count) % 2 == 1;
	}

	/*********************************************************
	 * 
	 * @param raw
	 * 
	 * @return
	 *********************************************************/
	private boolean isOddInvertedCommasFromEnd(String raw) {
		if (raw == null) {
			return false;
		}

		if (raw.endsWith("\"") == false) {
			return false;
		}

		int count = 0;

		for (int i=raw.length() - 1; i >= 0; i--) {
			char c = raw.charAt(i);
			if (c == '"') {
				count++;
			} else {
				break;
			}
		}

		return Math.abs(count) % 2 == 1;
	}

	/********************************************************************
	 * Remove the additional inverted commas that * the csv add to the fields
	 * 
	 * @param cell
	 *            *
	 * @return *
	 ********************************************************************/
	private String removeCsvInvertedCommasInCell(String cell) {
		if (cell == null) {
			return null;
		}

		if (cell.isEmpty()) {
			return "";
		}

		// remove the first and last "
		cell = cell.substring(1, cell.length() - 1);
		cell = cell.replaceAll("\"\"", "\"");

		return cell;
	}

	private void setHeaderList() {
		if (csvRows != null) {
			if (csvRows.size() > 0) {
				headerList = csvRows.get(0);
				csvRows.remove(0);
			}
		}
	}
}