package photostore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import photostore.LoginInformation;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;

public class StorageHandler {

	// private TransferManager tx;
	private String bucketNameUS;
	private String bucketNameEU;
	private String bucketNameIN;
	private AmazonS3 s3US;
	private AmazonS3 s3EU;
	private AmazonS3 s3IN;
	private String prefixUS;
	private String prefixEU;
	private String prefixIN;

	public StorageHandler() {
		AWSCredentials credentials = new BasicAWSCredentials("AKIAJNWENM3ZC7GSQULQ",
				"w9AcS9cdimk6agSxwegvcajugzHCDWZWLNvvH0og");
		s3US = new AmazonS3Client(credentials);
		Region usEast2 = Region.getRegion(Regions.US_EAST_2);
		s3US.setRegion(usEast2);
		s3EU = new AmazonS3Client(credentials);
		Region eu = Region.getRegion(Regions.EU_WEST_2);
		s3EU.setRegion(eu);
		s3IN = new AmazonS3Client(credentials);
		Region in = Region.getRegion(Regions.AP_SOUTH_1);
		s3IN.setRegion(in);
		// tx = new TransferManager(s3);

		bucketNameUS = "photostoragedistributedsystems-us";
		bucketNameEU = "photostoragedistributedsystems-eu";
		bucketNameIN = "photostoragedistributedsystems-in";
		
		prefixUS = "https://s3.us-east-2.amazonaws.com/photostoragedistributedsystems-us/";
		prefixEU = "https://s3.eu-west-2.amazonaws.com/photostoragedistributedsystems-eu/";
		prefixIN = "https://s3.ap-south-1.amazonaws.com/photostoragedistributedsystems-in/";

	}

	public void uploadPhoto(LoginInformation login, String photoName, File photo) {
		String key = login.UserName + "/" + photoName;
		s3US.putObject(new PutObjectRequest(bucketNameUS, key, photo));
		s3EU.putObject(new PutObjectRequest(bucketNameEU, key, photo));
		s3IN.putObject(new PutObjectRequest(bucketNameIN, key, photo));
	}

	public void massUploadPhoto(LoginInformation login, List<String> photoNames, File photo) {
		for (String photoName : photoNames) {
			String key = login.UserName + "/" + photoName;
			s3US.putObject(new PutObjectRequest(bucketNameUS, key, photo));
			s3EU.putObject(new PutObjectRequest(bucketNameEU, key, photo));
			s3IN.putObject(new PutObjectRequest(bucketNameIN, key, photo));
		}

	}
	
	public void uploadPhoto(LoginInformation login, String photoName, InputStream stream) throws IOException {
		try {
			String key = login.UserName + "/" + photoName;
			s3US.putObject(new PutObjectRequest(bucketNameUS, key, stream, null));
			s3EU.putObject(new PutObjectRequest(bucketNameEU, key, stream, null));
			s3IN.putObject(new PutObjectRequest(bucketNameIN, key, stream, null));
		
		}
		catch(Exception e) {
			
		}
	}
	
	public void uploadPhoto(LoginInformation login, String photoName) {
		String key = login.UserName + "/" + photoName;
		com.sun.security.auth.module.NTSystem NTSystem = new com.sun.security.auth.module.NTSystem();
		//System.out.println(NTSystem.getName());
		String location = "C:\\Users\\" + NTSystem.getName() + "\\Downloads\\"+ photoName;
		s3US.putObject(new PutObjectRequest(bucketNameUS, key, new File(location)));
		s3EU.putObject(new PutObjectRequest(bucketNameEU, key, new File(location)));
		s3IN.putObject(new PutObjectRequest(bucketNameIN, key, new File(location)));
	}
	
	public void massUploadPhoto(LoginInformation login, List<String> photoNames) {
		for(String photoName : photoNames) {
			uploadPhoto(login, photoName);
		}
		
	}

	public void deletePhoto(LoginInformation login, String photoName) {
		String key = login.UserName + "/" + photoName;
		s3US.deleteObject(bucketNameUS, key);
		s3EU.deleteObject(bucketNameEU, key);
		s3IN.deleteObject(bucketNameIN, key);
	}

	public void massDeletePhoto(LoginInformation login, List<String> photoNames) {
		for (String photoName : photoNames) {
			String key = login.UserName + "/" + photoName;
			s3US.deleteObject(bucketNameUS, key);
			s3EU.deleteObject(bucketNameEU, key);
			s3IN.deleteObject(bucketNameIN, key);
		}

	}

	public void downloadPhoto(LoginInformation login, StoreRegion region, String photoName) throws IOException {
		String bucketName;
		AmazonS3 s3;
		if (region.equals(StoreRegion.US)) {
			bucketName = bucketNameUS;
			s3 = s3US;
		} else if (region.equals(StoreRegion.EU)) {
			bucketName = bucketNameEU;
			s3 = s3EU;
		} else {
			bucketName = bucketNameIN;
			s3 = s3IN;
		}

		GetObjectRequest request = null;

		String key = login.UserName + "/" + photoName;
		request = new GetObjectRequest(bucketName, key);
		S3Object object = s3.getObject(request);
		com.sun.security.auth.module.NTSystem NTSystem = new com.sun.security.auth.module.NTSystem();
		//System.out.println(NTSystem.getName());
		String fileName = "C:\\Users\\" + NTSystem.getName() + "\\Downloads\\" + "PhotoStore" + "-"
				+ photoName.substring(photoName.lastIndexOf("/") + 1);

		saveFile(object.getObjectContent(), fileName);

		return;
	}

	public void massDownloadPhoto(LoginInformation login, StoreRegion region, List<String> photoNames)
			throws IOException {
		for (String photoName : photoNames) {
			downloadPhoto(login, region, photoName);
		}

	}

	public List<String> listPhotosUris(LoginInformation login, StoreRegion region) {
		String bucketName;
		AmazonS3 s3;
		if (region.equals(StoreRegion.US)) {
			bucketName = bucketNameUS;
			s3 = s3US;
		} else if (region.equals(StoreRegion.EU)) {
			bucketName = bucketNameEU;
			s3 = s3EU;
		} else {
			bucketName = bucketNameIN;
			s3 = s3IN;
		}
		List<String> photoNames = new ArrayList<String>();
		ObjectListing objectListing = s3
				.listObjects(new ListObjectsRequest().withBucketName(bucketName).withPrefix(login.UserName + "/"));
		for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
			photoNames.add(objectSummary.getKey());
			System.out.println(" - " + objectSummary.getKey() + "  " + "(size = " + objectSummary.getSize() + ")");
		}

		return photoNames;
	}

	public List<PhotoInfo> listPhotosNames(LoginInformation login, StoreRegion region) {
		List<String> photoUris = listPhotosUris(login, region);
		List<String> photoNames = new ArrayList<String>();
		List<PhotoInfo> photoInfos = new ArrayList<PhotoInfo>();
		String prefix = null;
		if(region.equals(StoreRegion.US)) {
			prefix = prefixUS;
		}else if(region.equals(StoreRegion.EU)) {
			prefix = prefixEU;
		}else {
			prefix = prefixIN;
		}
		if(photoUris != null && photoUris.size() > 0) {
			for(String photoUri : photoUris) {
				PhotoInfo photoInfo = new PhotoInfo(photoUri.substring(photoUri.lastIndexOf("/")+1), prefix+photoUri);
				photoInfos.add(photoInfo);
			}
		}
		
		return photoInfos;
	}

	private static void saveFile(InputStream input, String fileName) throws IOException {
		Files.copy(input, new File(fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	public static void main(String[] args) throws IOException {
		StorageHandler handler = new StorageHandler();
		LoginInformation login = new LoginInformation();
		login.UserName = "jilayshah";
		File photo = createSampleFile();
		//com.sun.security.auth.module.NTSystem NTSystem = new com.sun.security.auth.module.NTSystem();
		//System.out.println(NTSystem.getName());
		handler.uploadPhoto(login, "ajax-loader.gif", new FileInputStream(photo));
		//handler.downloadPhoto(login, StoreRegion.US, "Pic1");
		System.out.println("Photonames");

		//for (String photoName : handler.listPhotosNames(login, StoreRegion.US)) {
		//	System.out.println(photoName);
		//}
	}

	private static File createSampleFile() throws IOException {
		File file = File.createTempFile("aws-java-sdk-", ".txt");
		file.deleteOnExit();

		Writer writer = new OutputStreamWriter(new FileOutputStream(file));
		writer.write("abcdefghijklmnopqrstuvwxyz\n");
		writer.write("01234567890112345678901234\n");
		writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
		writer.write("01234567890112345678901234\n");
		writer.write("abcdefghijklmnopqrstuvwxyz\n");
		writer.close();

		return file;
	}
}
