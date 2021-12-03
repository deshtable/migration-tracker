import java.io.*;
import java.util.*;
import java.nio.file.Paths;
import org.json.simple.*;
import org.json.simple.parser.*;
import org.apache.spark.sql.*;

public class SimpleFramework {
	public static void main(String[] args) throws Exception {
		SparkSession spark = SparkSession.builder()
			.appName("Simple Framework")
			.getOrCreate();
		
		if(args.length == 0)
		{
			System.out.println("No configuration given as argument.");
			return;
		}
		
		String filename = args[0];
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader(filename));
		JSONObject jsonObject = (JSONObject)obj;
		JSONObject workflow = (JSONObject)jsonObject.get("workflow");
		Object[] arr = workflow.keySet().toArray();
		Arrays.sort(arr);
		
		for(int i = 0; i < arr.length; i++) {
			System.out.println((String)((JSONObject) workflow.get(arr[i])).get("component"));
			JSONObject componentParameters = (JSONObject) ((JSONObject) workflow.get(arr[i])).get("componentParameters");
			
			if(((String) ((JSONObject) workflow.get(arr[i])).get("component")).contains("GET_DATA")) {
				JSONObject connectionDetails = (JSONObject) ((JSONObject) componentParameters.get("connectionConfig")).get("connectionDetails");
				String outputTableName = (String) componentParameters.get("outputTable");
				String prefix = (String) connectionDetails.get("connectionUriPrefix");
				String host = (String) connectionDetails.get("host");
				String port = Long.toString((Long) connectionDetails.get("port"));
				String jdbcdriver = (String)((JSONObject) componentParameters.get("connectionConfig")).get("jdbcDriverClass");
				String database = (String) connectionDetails.get("database");
				String userName = (String) connectionDetails.get("userName");
				String password = (String) connectionDetails.get("password");
				String jdbcTable = (String) componentParameters.get("jdbcTable");
				JSONArray cols = (JSONArray) componentParameters.get("jdbcSelectFields");
				String fields = "";
				
				Iterator<String> iterator = cols.iterator();
				while(iterator.hasNext()) {
					fields += iterator.next();
					if(iterator.hasNext())
						fields += ", ";
				}
				
				GetData get_data = new GetData(prefix, host, port, database, jdbcTable, userName, password, jdbcdriver);
				get_data.get(fields).createOrReplaceTempView(outputTableName);
				}
			else if(((String) ((JSONObject) workflow.get(arr[i])).get("component")).contains("SELECT_DATA")) {
				String outputTableName = (String) componentParameters.get("outputTable");
				String queryFileName = (String) componentParameters.get("queryFileURL");
				
				SelectData select_data = new SelectData(queryFileName);
				select_data.select().createOrReplaceTempView(outputTableName);
			}
			else if(((String) ((JSONObject) workflow.get(arr[i])).get("component")).contains("INGEST_DATA")) {
				JSONObject connectionDetails = (JSONObject) ((JSONObject) componentParameters.get("connectionConfig")).get("connectionDetails");
				String inputTableName = (String) componentParameters.get("inputTable");
				String prefix = (String) connectionDetails.get("connectionUriPrefix");
				String host = (String) connectionDetails.get("host");
				String port = Long.toString((Long) connectionDetails.get("port"));
				String jdbcdriver = (String)((JSONObject) componentParameters.get("connectionConfig")).get("jdbcDriverClass");
				String database = (String) connectionDetails.get("database");
				String userName = (String) connectionDetails.get("userName");
				String password = (String) connectionDetails.get("password");
				String jdbcTable = (String) componentParameters.get("jdbcTable");
				JSONArray cols = (JSONArray) componentParameters.get("jdbcSelectFields");
				String fields = "";
				
				Iterator<String> iterator = cols.iterator();
				while(iterator.hasNext()) {
					fields += iterator.next();
					if(iterator.hasNext())
						fields += ", ";
				}
				
				IngestData ingest_data = new IngestData(prefix, host, port, database, jdbcTable, userName, password, jdbcdriver);
				
				if(!(fields.isEmpty()))
					ingest_data.ingest(spark.sql("select " + fields + " from " + inputTableName));
				else
					ingest_data.ingest(spark.sql("select * from " + inputTableName));
			}
		}
		
		spark.stop();
	}
}