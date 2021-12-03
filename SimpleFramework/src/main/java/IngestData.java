import org.apache.spark.sql.*;
import org.apache.spark.api.java.JavaSparkContext;
import com.mongodb.spark.MongoSpark;

public class IngestData {
	String prefix, host, port, database, table, user, password, jdbcDriver;
	SparkSession spark;
	JavaSparkContext jsc;
	
	IngestData(String prefix, String host, String port, String database, String table, String user, String password, String jdbcDriver) {
		this.prefix = prefix;
		this.host = host;
		this.port = port;
		this.database = database;
		this.table = table;
		this.user = user;
		this.password = password;
		this.jdbcDriver = jdbcDriver;
		
		spark = SparkSession.builder().getOrCreate();
		jsc = new JavaSparkContext(spark.sparkContext());
	}
	
	void ingest(Dataset<Row> view) {
		if(prefix.equals("mongodb:"))
			ingestCollection(view);
		else
			ingestTable(view);
	}
	
	void ingestTable(Dataset<Row> view) {
		if(!(user.isEmpty()) && !(password.isEmpty())) {
			view.write().mode(SaveMode.Append)
				.format("jdbc")
				.option("url", prefix + "//" + host + ":" + port + "/" + database)
				.option("dbtable", table)
				.option("user", user)
				.option("password", password)
				.option("driver", jdbcDriver)
				.save();
		}
		else {
			view.write().mode(SaveMode.Append)
				.format("jdbc")
				.option("url", prefix + "//" + host + ":" + port + "/" + database)
				.option("dbtable", table)
				.option("driver", jdbcDriver)
				.save();
		}
	}
	
	void ingestCollection(Dataset<Row> view) {
		String connection;
		
		if(!(user.isEmpty()) && !(password.isEmpty()))
			connection = prefix + "//" + user + ":" + password + "@" + host + ":" + port + "/" + database + "." + table;
		else
			connection = prefix + "//" + host + ":" + port + "/" + database + "." + table;
		
		spark.sparkContext().conf().set("spark.mongodb.input.uri", connection);
		spark.sparkContext().conf().set("spark.mongodb.output.uri", connection);
		
		MongoSpark.write(view).option("collection", table).mode("append").save();
	}
}