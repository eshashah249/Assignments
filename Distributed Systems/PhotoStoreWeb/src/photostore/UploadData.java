package photostore;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.xml.sax.InputSource;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

public class UploadData extends HttpServlet {

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
				for(String filename : filenames) {
					InputStream is = new FileInputStream(filename);
					storageHandler.uploadPhoto(loginInfo, filename.substring(filename.lastIndexOf("\\")+1, filename.length()), is);
				}
				
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