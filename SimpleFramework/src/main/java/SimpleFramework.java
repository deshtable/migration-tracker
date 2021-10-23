import org.apache.spark.sql.*;
import org.apache.spark.sql.functions.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.api.java.*;
import org.apache.spark.api.java.JavaSparkContext;

import com.mongodb.spark.MongoSpark;

public class SimpleFramework {
	
	public static void main(String[] args) {
		SparkSession spark = SparkSession.builder()
			.appName("Simple Framework")
			.config("spark.mongodb.input.uri", "mongodb://127.0.0.1/bank.bank_info")	// MongoDB Connection Configuration
			.config("spark.mongodb.output.uri", "mongodb://127.0.0.1/bank.bank_info")	// MongoDB Connection Configuration
			.getOrCreate();
		
		JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
		
		/* Postgres Connection
		Dataset<Row> source_df = spark.read()
			.format("jdbc")
			.option("url", "jdbc:postgresql://localhost:5432/bank")
			.option("dbtable", "bank_info")
			.option("user", "postgres")
			.option("password", "password")
			.option("driver", "org.postgresql.Driver")
			.load();
		*/
		
		// MongoDB Connection
		Dataset<Row> source_df = MongoSpark.load(jsc).toDF();
		
		source_df.createOrReplaceTempView("source");
		
		source_df.printSchema();
		source_df.show(10, false);
		
		spark.stop();
	}
}
