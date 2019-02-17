package io.github.tuzon.java.csv.api;

public interface TestParameters {
    public static final String[] DEFAULT_CSV_HEADERS = { "header1", "header2", "header3" };

    public static final String[][] DEFAULT_CSV_BODY = { { "line11", "line12", "line13" },
            { "line21", "line22", "line23" } };
    
    public static final String[] COMPLEX_CSV_HEADERS = { "header,comma", "header\n2 lines", "header \" invered comma",
            "header \"2 inverted commas\"" };

    public static final String[][] COMPLEX_CSV_BODY = { { "line\n2 lines", "line end with inverted comma\"",
            "\"line start and end with inverted commas\"", "\n\"" }, { "", "test2", "test3 \"\nE1\"", "" } };
}
