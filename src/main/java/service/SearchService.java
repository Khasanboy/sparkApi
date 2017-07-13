package service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

import model.Result;
import model.Search;
import model.User;

public class SearchService {

	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/test";
	static final String USER = "sa";
	static final String PASS = "";

	Connection conn = null;
	Statement stmt = null;

	@SuppressWarnings("null")
	public List<Search> getAllSearch() {
		List<Search> list = null;

		try {

			Class.forName(JDBC_DRIVER);

			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			String sql = "Select * FROM searchAPI.searchResult";

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {

				Long id = (long) rs.getInt("searchResultId");
				String queryString = rs.getString("queryString");
				String statusString = rs.getString("statusString");
				Date createdDate = rs.getDate("createdDate");

				@SuppressWarnings("unchecked")
				List<Result> allResult = (List<Result>) rs.getArray("resultFromSearchId");

				Search newSearch = new Search(id, queryString, statusString, createdDate, allResult);

				list.add(newSearch);

			}

			System.out.println(list);
			stmt.close();
			conn.close();

		} catch (Exception se) {
			se.printStackTrace();
		} finally {

			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException se2) {
				se2.printStackTrace();
			}
		}
		return list;
	}
	
	

	public Search createSearch(String quesryString, String user) throws UnsupportedEncodingException, IOException {

		String google = "http://www.google.com/search?q=";
		String charset = "UTF-8";
		String userAgent = "ExampleBot 1.0 (+http://example.com/bot)"; 

		Elements links = Jsoup.connect(google + URLEncoder.encode(quesryString, charset)).userAgent(userAgent).get()
				.select(".g>.r>a");
		String googleTitle = null;

		for (Element link : links) {
			googleTitle = link.text();
			break;

		}
		
		System.out.println("google Title: " + googleTitle);	
		
		
		String yandex = "https://yandex.ru/search/?text=";

		Elements yandexlink = Jsoup.connect(yandex + URLEncoder.encode(quesryString, charset)).userAgent(userAgent).get()
				.select("a.serp-item__title-link");
		
		String yandexTitle = null;
				
				for (Element link : yandexlink) {
					yandexTitle = link.text();
					break;

				}
		
		System.out.println("yandex Title: " + yandexTitle);	
		
		

		try {

			Class.forName(JDBC_DRIVER);

			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			String sql;
			
			sql = "INSERT INTO searchAPI.resultFromSearch (google, yandex) VALUES ('"+googleTitle+"', '"+yandexTitle+"')";
			int resultId = stmt.executeUpdate(sql);
			
			 Date dNow = new Date( );
		     SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
			
			sql = "INSERT INTO searchAPI.searchResult (queryString, statusString, createdDate, resultFromSearchId) VALUES ('"+quesryString+"', 'COMPLETED', '"+ft.format(dNow)+"', "+resultId+")";
			int searchResultId = stmt.executeUpdate(sql);
			
			System.out.println(user);

			Gson g = new Gson();
			User newUser = g.fromJson(user, User.class);
			
			System.out.println(newUser.getId() + newUser.getUserName() + newUser.getPassword());
			
			
			sql = "Select * FROM searchAPI.userInfo where userInfoId = "+newUser.getId();
			ResultSet foundUserResult = stmt.executeQuery(sql);
			
			if(!foundUserResult.next()){
				System.out.println("Inside");
				int array[] = {searchResultId};
				sql = "INSERT INTO searchAPI.userInfo (username, password, searchResultId) VALUES ('"+newUser.getUserName()+"', '"+newUser.getPassword()+"', '"+ array+"')";
				int userone = stmt.executeUpdate(sql);
			}
			else{
				while (foundUserResult.next()) {
					/*
					int array[] = foundUserResult

					Long id = (long) rs.getInt("searchResultId");
					String queryString = rs.getString("queryString");
					String statusString = rs.getString("statusString");
					Date createdDate = rs.getDate("createdDate");

					@SuppressWarnings("unchecked")
					List<Result> allResult = (List<Result>) rs.getArray("resultFromSearchId");

					Search newSearch = new Search(id, queryString, statusString, createdDate, allResult);

					list.add(newSearch);
					*/

				}
				
			}
			
			//foundUserResult.next();
			//User foundUser = (User) foundUserResult.getObject(0);
			
			
			
			

			
			stmt.close();
			conn.close();

		} catch (Exception se) {
			se.printStackTrace();
		} finally {

			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException se2) {
				se2.printStackTrace();
			}
		}

		return null;

	}

}
