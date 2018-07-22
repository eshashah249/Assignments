package photostore;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AlbumInformation {
	@JsonProperty("UserName")
	String UserName;
	@JsonProperty("AlbumNames")
	Set<String> AlbumNames;
}
