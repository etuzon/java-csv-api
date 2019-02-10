package org.eltn.java.csv.api;

import java.util.ArrayList;
import java.util.List;

import org.eltn.java.csv.enums.CellsSplitterEnum;
import org.eltn.projects.core.base.ObjectBase;

/***********************************************
 * Base class to CSV API.
 * 
 * @author Eyal Tuzon
 *
 */
public abstract class CsvApiBase extends ObjectBase {
    public static final char CELLS_DEFAULT_SPLITTER = CellsSplitterEnum.COMMA.getChar();

    protected final String path;
    protected List<String> headerList = new ArrayList<String>();
    protected final List<List<String>> csvRows = new ArrayList<List<String>>();
    
    protected final char cellsSplitter;
    
    /***********************************************
     * Constructor.
     * 
     * @param path CSV file path.
     * @param cellsSplitter Cells Splitter char.
     */
    protected CsvApiBase(String path, char cellsSplitter) {
        this.path = path;
        this.cellsSplitter = cellsSplitter;
    }
}
