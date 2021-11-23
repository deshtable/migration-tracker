import org.apache.spark.sql.*;
import org.apache.spark.api.java.JavaSparkContext;
import com.mongodb.spark.MongoSpark;

public class GetData {
	String prefix, host, port, database, table, user, password, jdbcDriver;
	Dataset<Row> view;
	SparkSession spark;
	JavaSparkContext jsc;
	
	GetData(String prefix, String host, String port, String database, String table, String user, String password, String jdbcDriver) {
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
	
	Dataset<Row> get(String fields) {
		if(prefix.equals("mongodb:"))
			getCollection();
		else
			getTable();
			
		if(!(fields.isEmpty()))
		{
			view.createOrReplaceTempView("temp");
			view = spark.sql("select " + fields + " from temp");
			spark.sql("drop view temp");
		}
		
		return view;
	}
	
	void getTable() {
		if(!(user.isEmpty()) && !(password.isEmpty())) {
			view = spark.read()
				.format("jdbc")
				.option("url", prefix + "//" + host + ":" + port + "/" + database)
				.option("dbtable", table)
				.option("user", user)
				.option("password", password)
				.option("driver", jdbcDriver)
				.load();
		}
		else {
			view = spark.read()
				.format("jdbc")
				.option("url", prefix + "//" + host + ":" + port + "/" + database)
				.option("dbtable", table)
				.option("driver", jdbcDriver)
				.load();
		}
	}
	
	void getCollection() {
		String connection;
		
		if(!(user.isEmpty()) && !(password.isEmpty()))
			connection = prefix + "//" + user + ":" + password + "@" + host + ":" + port + "/" + database + "." + table;
		else
			connection = prefix + "//" + host + ":" + port + "/" + database + "." + table;
		
		spark.sparkContext().conf().set("spark.mongodb.input.uri", connection);
		spark.sparkContext().conf().set("spark.mongodb.output.uri", connection);
		
		view = MongoSpark.load(jsc).toDF();
	}
}