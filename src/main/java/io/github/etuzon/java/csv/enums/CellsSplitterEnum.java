package io.github.etuzon.java.csv.enums;

/*********************************
 * Cells splitter enum.
 *
 * @author Eyal Tuzon
 * 
 */
public enum CellsSplitterEnum {
    TAB('\t'), SPACE(' '), COMMA(',');

    private final char chr;

    /*********************************
     * Constructor.
     *
     * @param value Enum char.
     */
    private CellsSplitterEnum(char value) {
        chr = value;
    }

    /*********************************
     * Get cells splitter char.
     *
     * @return cells splitter char.
     */
    public char getChar() {
        return chr;
    }
}