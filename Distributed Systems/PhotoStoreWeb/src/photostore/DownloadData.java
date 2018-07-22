package photostore;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DownloadData extends HttpServlet {

	// @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// Set the response MIME type of the response message
		response.setContentType("text/html; charset=UTF-8");

		// Allocate a output writer to write the response message into the network
		// socket
		PrintWriter out = response.getWriter();

		// Write the response message, in an HTML page
		try {

			String UserName = URLDecoder.decode(request.getParameter("username"), "UTF-8");
			String inputFileNames = URLDecoder.decode(request.getParameter("filenames"), "UTF-8");
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			List<String> filenames = new ArrayList<String>();
			filenames = mapper.readValue(inputFileNames, SelectedContent.class).getFileNames();
			LoginInformation loginInfo = new LoginInformation();
			loginInfo.UserName = UserName;

			boolean bExist = true; // Always return true for Download
			StorageHandler storageHandler = new StorageHandler();

			if (filenames != null && filenames.size() > 0) {
				storageHandler.massDownloadPhoto(loginInfo, StoreRegion.US, filenames);
			}
			
			String json = "";

			if (bExist) {
				json += "{\"results\":{\"result\":[";
				json += "{\"username\":\"" + UserName + "\",";
				json += "\"status\":\"" + "true" + "\"}";
				json += "]}}";
			} else {
				json += "{\"results\":{\"result\":[]} }";
			}

			out.println(json);
		} catch (Exception e) {
			out.println(e.toString());
		} finally {
			out.close(); // Always close the output writer
		}
	}

	// @Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doGet(request, response);
	}

}