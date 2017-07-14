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
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import com.google.gson.Gson;

import common.Status;
import model.Search;
import model.SearchEngine;
import model.User;

public class SearchService {

	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/test";
	static final String USER = "sa";
	static final String PASS = "";
	static final SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	static final Gson g = new Gson();
	static final String charset = "UTF-8";
	static final String userAgent = "User-Agent Mozilla/5.0";
	Connection conn = null;
	Statement stmt = null;

	HashMap<String, String> result;
	ArrayList<SearchEngine> engines = new ArrayList<>();

	public SearchService() {
		this.engines.add(new SearchEngine("google", "http://www.google.com/search?q=", ".g>.r>a"));
		this.engines.add(new SearchEngine("yandex", "https://yandex.ru/search/?text=", "b-serp-item__title-link"));

		for (int i = 0; i < this.engines.size(); i++) {

			try {

				Class.forName(JDBC_DRIVER);

				System.out.println("Connecting to database...");
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
				stmt = conn.createStatement();
				String sql;

				sql = "ALTER TABLE searchAPI.searchResult ADD COLUMN IF NOT EXISTS " + this.engines.get(i).getName()
						+ " VARCHAR(255)";
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

		}
	}

	public HashMap<String, String> createResult(int searchId, String queryString, ArrayList<SearchEngine> listEngines) {

		try {

			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			String sql = "UPDATE searchAPI.searchResult SET statusString= '" + Status.INPROCESS.getCurrent()
					+ "' WHERE searchResultId =" + searchId;
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

		HashMap<String, String> result = new HashMap<String, String>();

		Elements links = null;
		String title = "";

		for (int j = 0; j < listEngines.size(); j++) {
			System.out.println("How many times");
			System.out.println(listEngines.get(j).getSelector());
			try {
				links = Jsoup.connect(listEngines.get(j).getLink() + URLEncoder.encode(queryString, charset))
						.userAgent(userAgent).get().select(listEngines.get(j).getSelector());
				System.out.println(links.size());

			} catch (java.net.UnknownHostException ex) {
				System.out.println(listEngines.get(j).getLink() + "is unknown or not working");
			}

			catch (IOException e) {

				e.printStackTrace();

			}
			
			if(links.size()>0){
				title = links.get(0).text();
			}
			else{
				title="";
			}
			
			
			System.out.println(listEngines.get(j).getName() +" "+ title);
			result.put(listEngines.get(j).getName(), title);
			
			try {

				Class.forName(JDBC_DRIVER);
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
				stmt = conn.createStatement();
				System.out.println(title);
				String sql = "UPDATE searchAPI.searchResult SET " + listEngines.get(j).getName() + " = '" + title + "'";
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
		}

		return null;

	}

	public Search createSearch(String queryString, String user) throws UnsupportedEncodingException, IOException {
		int searchId = -1;

		try {

			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			Date dNow = new Date();
			String sql;

			User newUser = g.fromJson(user, User.class);

			System.out.println(newUser.getId() + newUser.getUsername() + newUser.getPassword());

			sql = "Select * FROM searchAPI.userInfo where userInfoId = " + newUser.getId();
			ResultSet foundUserResult = stmt.executeQuery(sql);

			if (!foundUserResult.next()) {
				System.out.println("Inside first if");
				int key = -1;
				sql = "INSERT INTO searchAPI.userInfo (username, password) VALUES ('" + newUser.getUsername() + "', '"
						+ newUser.getPassword() + "')";
				stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs != null && rs.next()) {
					System.out.println("Inside second if");
					key = rs.getInt(1);
					sql = "INSERT INTO searchAPI.searchResult (queryString, statusString, createdDate, userInfoId) VALUES ('"
							+ queryString + "', '" + Status.CREATED.getCurrent() + "', '" + ft.format(dNow) + "', "
							+ key + ")";
					stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
					ResultSet searchResult = stmt.getGeneratedKeys();
					if (searchResult != null && searchResult.next()) {
						searchId = searchResult.getInt(1);
						System.out.println("Inside third if");
					}
				}
			} else {
				System.out.println("Inside else");
				sql = "INSERT INTO searchAPI.searchResult (queryString, statusString, createdDate, userInfoId) VALUES ('"
						+ queryString + "', '" + Status.CREATED.getCurrent() + "', '" + ft.format(dNow) + "', "
						+ newUser.getId() + ")";
				stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
				ResultSet searchResultOne = stmt.getGeneratedKeys();
				if (searchResultOne != null && searchResultOne.next()) {
					searchId = searchResultOne.getInt(1);
					System.out.println("Inside else IF");
				}
			}

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

		createResult(searchId, queryString, this.engines);

		try {

			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			String sql = "UPDATE searchAPI.searchResult SET statusString= '" + Status.COMPLETED.getCurrent()
					+ "' WHERE searchResultId =" + searchId;
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

				HashMap<String, String> result = new HashMap<String, String>();
				for (int t = 0; t < this.engines.size(); t++) {
					result.put(this.engines.get(t).getName(), rs.getString(this.engines.get(t).getName()));
				}
				newSearch.setResult(result);
				newSearch.setUserId((long) rs.getInt("userInfoId"));
				list.add(newSearch);

			}

			stmt.close();
			conn.close();

		} catch (NullPointerException ex) {
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
		System.out.println(list);
		return list;
	}

	public ArrayList<Search> getAllSearchByUserName(String username) {
		ArrayList<Search> list = new ArrayList<>();

		try {

			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			String sql = "";

			sql = "Select * FROM searchAPI.userInfo where username = '" + username + "'";
			ResultSet foundUserResult = stmt.executeQuery(sql);

			if (foundUserResult.next()) {
				int userId = foundUserResult.getInt("userInfoId");

				sql = "Select * FROM searchAPI.searchResult where userInfoId = " + userId;
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
					HashMap<String, String> result = new HashMap<String, String>();
					for (int t = 0; t < this.engines.size(); t++) {
						result.put(this.engines.get(t).getName(),
								foundSearchResult.getString(this.engines.get(t).getName()));
					}
					newSearch.setResult(result);
					newSearch.setUserId((long) foundSearchResult.getInt("userInfoId"));

					list.add(newSearch);

				}

			}

			stmt.close();
			conn.close();

		} catch (NullPointerException ex) {
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
		System.out.println(list);
		return list;
	}

	public String deleteSearch(String id) {

		String deleted = "";

		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			String sql;

			sql = "SELECT * FROM searchAPI.searchResult WHERE searchResultId = " + new Integer(id);
			ResultSet found = stmt.executeQuery(sql);

			if (found.next()) {
				sql = "DELETE FROM searchAPI.searchResult WHERE searchResultId = " + new Integer(id);
				stmt.execute(sql);
				deleted = "Search with id = " + id + " is deleted";
			} else {
				deleted = "Search with id = " + id + " is not found";
			}

			stmt.close();
			conn.close();

		} catch (NullPointerException ex) {
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
