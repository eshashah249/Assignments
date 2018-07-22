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
import org.xml.sax.InputSource;
import java.net.URLDecoder;
import java.net.MalformedURLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

public class RegisterUser extends HttpServlet {

	// @Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// Set the response MIME type of the response message
		response.setContentType("text/html; charset=UTF-8");

		// Allocate a output writer to write the response message into the network
		// socket
		PrintWriter out = response.getWriter();

		// Write the response message, in an HTML page
		try {

			String FirstName = URLDecoder.decode(request.getParameter("firstname"), "UTF-8");
			String LastName = URLDecoder.decode(request.getParameter("lastname"), "UTF-8");
			String UserName = URLDecoder.decode(request.getParameter("username"), "UTF-8");
			String EmailId = URLDecoder.decode(request.getParameter("email"), "UTF-8");
			String Password = URLDecoder.decode(request.getParameter("password"), "UTF-8");

			LoginInformation loginInfo = new LoginInformation();
			loginInfo.FirstName = FirstName;
			loginInfo.LastName = LastName;
			loginInfo.UserName = UserName;
			loginInfo.EmailId = EmailId;
			loginInfo.Password = Password;

			LoginDatabaseHandler loginHandler = new LoginDatabaseHandler();
			boolean bExist = loginHandler.insertData(loginInfo);

			String json = "";

			if (bExist) {
				json += "{\"results\":{\"result\":[";
				json += "{\"username\":\"" + UserName + "\",";
				json += "\"exist\":\"" + "true" + "\"}";
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
