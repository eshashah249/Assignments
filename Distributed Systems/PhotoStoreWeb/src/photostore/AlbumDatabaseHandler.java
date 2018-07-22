package photostore;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AlbumDatabaseHandler {

	String tableName = "AlbumNames";
	AmazonDynamoDB amazonDynamoDB;
	DynamoDB dynamoDB;
	Table table;
	ObjectMapper mapper;

	public AlbumDatabaseHandler() {
		AWSCredentials credentials = new BasicAWSCredentials("AKIAJNWENM3ZC7GSQULQ",
				"w9AcS9cdimk6agSxwegvcajugzHCDWZWLNvvH0og");
		amazonDynamoDB = new AmazonDynamoDBClient(credentials);
		amazonDynamoDB.setRegion(Region.getRegion(Regions.US_EAST_2));
		dynamoDB = new DynamoDB(amazonDynamoDB);
		table = dynamoDB.getTable(tableName);
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
	}

	public boolean createAlbum(String userName, String albumName)
			throws JsonParseException, JsonMappingException, IOException {
		AlbumInformation albumInformation = getAlbums(userName);
		Set<String> albumNames = new HashSet<String>();
		;
		if (albumInformation != null) {
			albumNames.addAll(albumInformation.AlbumNames);
		}
		albumNames.add(albumName);
		Item item = new Item().withPrimaryKey("UserName", userName).withJSON("AlbumNames",
				mapper.writeValueAsString(albumNames));
		table.putItem(item);
		return true;
	}

	public AlbumInformation getAlbums(String userName) throws JsonParseException, JsonMappingException, IOException {
		Item item = table.getItem("UserName", userName);
		if (item != null) {
			// System.out.println(item.toJSONPretty());
			return mapper.readValue(item.toJSON(), AlbumInformation.class);
		}

		return null;
	}

	public static void main(String[] args) throws IOException {
		AlbumDatabaseHandler databaseHandler = new AlbumDatabaseHandler();
		AlbumInformation album = new AlbumInformation();
		album.UserName = "jilayshah";
		System.out.println("Exist : " + databaseHandler.getAlbums(album.UserName));
		System.out.println("Insert : " + databaseHandler.createAlbum(album.UserName, "MyAlbum1"));
		System.out.println("Exist : " + databaseHandler.getAlbums(album.UserName).AlbumNames.size());
		System.out.println("Insert : " + databaseHandler.createAlbum(album.UserName, "MyAlbum2"));
		System.out.println("Insert : " + databaseHandler.createAlbum(album.UserName, "MyAlbum3"));
		System.out.println("Exist : " + databaseHandler.getAlbums(album.UserName).AlbumNames.size());

	}

}
