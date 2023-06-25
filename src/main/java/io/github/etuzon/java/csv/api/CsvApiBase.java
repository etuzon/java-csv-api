package io.github.etuzon.java.csv.api;

import java.util.ArrayList;
import java.util.List;

import io.github.etuzon.java.csv.enums.CellsSplitterEnum;
import io.github.etuzon.projects.core.base.ObjectBase;

/***********************************************
 * CSV API base class.
 * 
 * @author Eyal Tuzon
 */
public abstract class CsvApiBase extends ObjectBase {
    public static final char CELLS_DEFAULT_SPLITTER = CellsSplitterEnum.COMMA.getChar();

    protected final String filePath;
    protected List<String> headerList = new ArrayList<>();
    protected final List<List<String>> csvRows = new ArrayList<>();
    
    protected final char cellsSplitter;
    
    /***********************************************
     * Constructor.
     * 
     * @param filePath CSV file path.
     * @param cellsSplitter Cells Splitter char.
     */
    protected CsvApiBase(String filePath, char cellsSplitter) {
        this.filePath = filePath;
        this.cellsSplitter = cellsSplitter;
    }
}