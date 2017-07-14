package service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
	static final SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
	static final Gson g = new Gson();
	Connection conn = null;
	Statement stmt = null;

	public ArrayList<Search> getAllSearch() {
		ArrayList<Search> list = new ArrayList<>();

		try {

			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			String sql = "Select * FROM searchAPI.searchResult";

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {

				Search newSearch = new Search();
				
				newSearch.setId((long) rs.getInt("searchResultId"));
				newSearch.setQuery(rs.getString("queryString"));
				newSearch.setStatus(rs.getString("statusString"));
				String createdDateString = rs.getString("createdDate");
				Date createdDate = null; 
				try {
					createdDate = ft.parse(createdDateString);
				} catch (ParseException e) {
					System.out.println("Parse error");
				    e.printStackTrace();
				}
				
				newSearch.setCreated(createdDate);
				newSearch.setResult(new Result(rs.getString("google"), rs.getString("yandex")));
				newSearch.setUserId((long)rs.getInt("userInfoId"));
				
				list.add(newSearch);

			}

			stmt.close();
			conn.close();

		}catch(NullPointerException ex){
			System.out.println(ex);
		}
		
		catch (Exception se) {
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
	
	public ArrayList<Search> getAllSearchByUserName(String username) {
		ArrayList<Search> list = new ArrayList<>();

		try {

			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			String sql="";
			
			sql = "Select * FROM searchAPI.userInfo where username = '"+username+"'";
			ResultSet foundUserResult = stmt.executeQuery(sql);
			
			if(foundUserResult.next()){
				int userId = foundUserResult.getInt("userInfoId");
				
				sql = "Select * FROM searchAPI.searchResult where userInfoId = "+userId;
				ResultSet foundSearchResult = stmt.executeQuery(sql);
				
				while (foundSearchResult.next()) {

					Search newSearch = new Search();
					
					newSearch.setId((long) foundSearchResult.getInt("searchResultId"));
					newSearch.setQuery(foundSearchResult.getString("queryString"));
					newSearch.setStatus(foundSearchResult.getString("statusString"));
					String createdDateString = foundSearchResult.getString("createdDate");
					Date createdDate = null; 
					try {
						createdDate = ft.parse(createdDateString);
					} catch (ParseException e) {
						System.out.println("Parse error");
					    e.printStackTrace();
					}
					
					newSearch.setCreated(createdDate);
					newSearch.setResult(new Result(foundSearchResult.getString("google"), foundSearchResult.getString("yandex")));
					newSearch.setUserId((long)foundSearchResult.getInt("userInfoId"));
					
					if(newSearch!= null)
						list.add(newSearch);

				}
				
			}

			stmt.close();
			conn.close();

		}catch(NullPointerException ex){
			System.out.println(ex);
		}
		
		catch (Exception se) {
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
			
			System.out.println(user);
			
			User newUser = g.fromJson(user, User.class);
			
			System.out.println(newUser.getId() + newUser.getUsername() + newUser.getPassword());
			
			
			sql = "Select * FROM searchAPI.userInfo where userInfoId = "+newUser.getId();
			ResultSet foundUserResult = stmt.executeQuery(sql);
			
			if(!foundUserResult.next()){
				sql = "INSERT INTO searchAPI.userInfo (username, password) VALUES ('"+newUser.getUsername()+"', '"+newUser.getPassword()+"')";
				stmt.executeUpdate(sql);
			}
			
			 Date dNow = new Date( );
		    
			
			sql = "INSERT INTO searchAPI.searchResult (queryString, statusString, createdDate, google, yandex, userInfoId) VALUES ('"+quesryString+"', 'COMPLETED', '"+ft.format(dNow)+"', '"+googleTitle+"','"+yandexTitle+"', "+newUser.getId()+")";
			stmt.executeUpdate(sql);
			
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
	
	public String deleteSearch(String id) {
		
		String deleted ="";
		
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			String sql;
			
			sql = "SELECT * FROM searchAPI.searchResult WHERE searchResultId = " + new Integer(id);
			ResultSet found = stmt.executeQuery(sql);
			
			if(found.next()){
				sql= "DELETE FROM searchAPI.searchResult WHERE searchResultId = " + new Integer(id);
				stmt.execute(sql);
				deleted = "Search with id = "+id+" is deleted";
			}
			else{
				deleted = "Search with id = "+id+" is not found";
			}
			
			stmt.close();
			conn.close();

		}catch(NullPointerException ex){
			System.out.println(ex);
		}
		
		catch (Exception se) {
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
		return deleted;
	}
	

}
