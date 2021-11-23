import java.io.*;
import java.util.*;
import org.apache.spark.sql.*;

public class SelectData {
	Scanner scanner;
	Dataset<Row> view;
	
	SelectData(String fileName) throws Exception {
		scanner = new Scanner(new File(fileName));
	}
	
	Dataset<Row> select() {
		SparkSession spark = SparkSession.builder()
			.getOrCreate();
		
		scanner.useDelimiter(";");
		if(scanner.hasNext()) {
			view = spark.sql(scanner.next());
		}
		
		scanner.close();
		
		return view;
	}
}