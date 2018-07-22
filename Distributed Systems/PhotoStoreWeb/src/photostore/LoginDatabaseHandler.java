package photostore;

import java.io.IOException;
import java.util.HashMap;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LoginDatabaseHandler {

	String tableName = "NewLogin";
	AmazonDynamoDB amazonDynamoDB;
	DynamoDB dynamoDB;
	Table table;
	ObjectMapper mapper;

	public LoginDatabaseHandler() {
		AWSCredentials credentials = new BasicAWSCredentials("AKIAJNWENM3ZC7GSQULQ",
				"w9AcS9cdimk6agSxwegvcajugzHCDWZWLNvvH0og");
		amazonDynamoDB = new AmazonDynamoDBClient(credentials);
		amazonDynamoDB.setRegion(Region.getRegion(Regions.US_EAST_2));
		dynamoDB = new DynamoDB(amazonDynamoDB);
		table = dynamoDB.getTable(tableName);
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
	}

	public boolean insertData(LoginInformation loginInformation) {
		if (doesExist(loginInformation)) {
			return false;
		}
		HashMap<String, AttributeValue> item_values = new HashMap<String, AttributeValue>();
		item_values.put("UserName", new AttributeValue(loginInformation.UserName));
		item_values.put("FirstName", new AttributeValue(loginInformation.FirstName));
		item_values.put("LastName", new AttributeValue(loginInformation.LastName));
		item_values.put("Password", new AttributeValue(loginInformation.Password));
		item_values.put("EmailId", new AttributeValue(loginInformation.EmailId));
		amazonDynamoDB.putItem(tableName, item_values);
		return true;
	}

	public boolean doesExist(LoginInformation loginInformation) {
		Item item = table.getItem("UserName", loginInformation.UserName);
		if (item != null) {
			// System.out.println(item.toJSONPretty());

			return true;
		}

		return false;
	}

	public boolean isValidLoginInformation(LoginInformation loginInformation)
			throws JsonParseException, JsonMappingException, IOException {
		Item item = table.getItem("UserName", loginInformation.UserName);
		if (item != null) {
			LoginInformation loginInfo = mapper.readValue(item.toJSON(), LoginInformation.class);
			if (loginInfo != null && loginInfo.UserName.equals(loginInformation.UserName)
					&& loginInfo.Password.equals(loginInformation.Password)) {
				return true;
			}
		}

		return false;
	}

	public static void main(String[] args) throws IOException {
		LoginDatabaseHandler databaseHandler = new LoginDatabaseHandler();
		LoginInformation login = new LoginInformation();
		login.EmailId = "EshaShah@gmail.com";
		login.UserName = "Eshashah";
		login.FirstName = "Esha";
		login.LastName = "Shah";
		login.Password = "password";
		System.out.println("Exist : " + databaseHandler.doesExist(login));
		System.out.println("Insert : " + databaseHandler.insertData(login));
		System.out.println("Exist : " + databaseHandler.doesExist(login));
	}

}
