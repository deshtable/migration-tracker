import org.apache.spark.sql.*;
import org.apache.spark.sql.functions.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.api.java.*;
import org.apache.spark.api.java.JavaSparkContext;

import com.mongodb.spark.MongoSpark;

import java.io.*;
import java.util.Scanner;

public class SimpleFramework {
	public static void main(String[] args) {
		SparkSession spark = SparkSession.builder()
			.appName("Simple Framework")
			.getOrCreate();
		
		JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
		
		// Postgres Connection
		Dataset<Row> source_df = spark.read()
			.format("jdbc")
			.option("url", "jdbc:postgresql://localhost:5432/bank")
			.option("dbtable", "bank_info")
			.option("user", "postgres")
			.option("password", "password")
			.option("driver", "org.postgresql.Driver")
			.load();
		
		// MongoDB Connection
		//spark.sparkContext().conf().set("spark.mongodb.input.uri", "mongodb://127.0.0.1/bank.bank_info");
		//spark.sparkContext().conf().set("spark.mongodb.output.uri", "mongodb://127.0.0.1/bank.bank_info");
		//Dataset<Row> source_df = MongoSpark.load(jsc).toDF();
		
		source_df.show(11, false);
		
		Dataset<Row> quality_df = validate(source_df);
		
		quality_df.show(11, false);
		
		spark.stop();
	}
	
	public static Dataset<Row> validate(Dataset<Row> source_df) {
		SparkSession spark = SparkSession.builder()
			.getOrCreate();
		
		try {
			Scanner scanner = new Scanner(new File("validations.sql"));
			scanner.useDelimiter(";");
			
			while(scanner.hasNext()) {
				source_df.createOrReplaceTempView("source");
				source_df = spark.sql(scanner.next());
				spark.sql("DROP VIEW " + "source");
			}
		}
		
		catch(Exception error) {
			error.printStackTrace();
		}
		
		return source_df;
	}
}