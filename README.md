# Java-CSV-API
Java CSV Reader/writer API.
    
    API support multiline fields, and custom separators.
    
    Example of multiline CSV:
    
    header1,"header
    2",header3
    field1,field2,"field
    3"
    
    
    Code Example:
    
    //Read CSV file
    CsvReaderApi csvReader = new CsvReaderApi(CSV_PATH);
    
    List<String> headerList = csvReader.getHeaderList();
    List<List<String>> rowsList = csvReader.getRows();
    
    //Overwrite CSV file
    CsvWriterApi csvWriter = new CsvWriterApi(CSV_PATH);
    csvWriter.setHeaders(headerList);
    csvWriter.addRows(rowsList);
    //Add new Row that one of the fields is multiline field
    //ListUtil.asList is method in Java-Projects-Core
    List<String> row = ListUtil.asList("1", "2\n123", "3");
    csvWriter.addRow(row);
    //save to file
    csvWriter.save();
