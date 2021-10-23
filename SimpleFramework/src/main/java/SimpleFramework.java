import org.apache.spark.sql.*;
import org.apache.spark.sql.functions.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.api.java.*;

public class SimpleFramework {
	
	public static void main(String[] args) {
		SparkSession spark = SparkSession.builder().appName("Simple Framework").getOrCreate();
		
		Dataset<Row> source_df = spark.read()
			.format("jdbc")
			.option("url", "jdbc:postgresql://localhost:5432/bank")
			.option("dbtable", "bank_info")
			.option("user", "postgres")
			.option("password", "password")
			.option("driver", "org.postgresql.Driver")
			.load();
		
		source_df.createOrReplaceTempView("source");
		
		source_df.printSchema();
		source_df.show(10, false);
		
		spark.stop();
	}
}