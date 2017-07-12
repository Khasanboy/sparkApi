package service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import model.Result;
import model.Search;

public class SearchService {
	 
	   static final String JDBC_DRIVER = "org.h2.Driver";   
	   static final String DB_URL = "jdbc:h2:~/test";  
	   static final String USER = "sa"; 
	   static final String PASS = ""; 
	   
	   Connection conn = null; 
	   Statement stmt = null; 
	   
	
	@SuppressWarnings("null")
	public List<Search> getAllSearch(){
		List<Search> list = null;
		
		 try { 
			 
	         Class.forName(JDBC_DRIVER); 
	             
	         System.out.println("Connecting to database..."); 
	         conn = DriverManager.getConnection(DB_URL,USER,PASS);  
	         stmt = conn.createStatement();
	         
	        // String insert = "Insert into searchAPI.searchResult ('lalala', 'Completed', 12.07.2017)";
	         //stmt.executeQuery(insert);
	          
	         String sql = "Select * FROM searchAPI.searchResult"; 
	         
	         ResultSet rs = stmt.executeQuery(sql); 
	         while(rs.next()){
	        	 
	        	 Long id  = (long) rs.getInt("searchResultId"); 
	             String queryString = rs.getString("queryString"); 
	             String statusString = rs.getString("statusString"); 
	             Date createdDate = rs.getDate("createdDate");
	             
	             @SuppressWarnings("unchecked")
				List<Result>allResult = (List<Result>) rs.getArray("resultFromSearchId"); 
	             
	             Search newSearch = new Search(id, queryString, statusString, createdDate, allResult);
	             
	             list.add(newSearch);
	        	 
	         }
	         
	         System.out.println(list);
	         stmt.close(); 
	         conn.close(); 
	        
	      } catch(SQLException se) { 
	          
	         se.printStackTrace(); 
	      } catch(Exception e) { 
	          
	         e.printStackTrace(); 
	      } finally { 
	          
	         try{ 
	            if(stmt!=null) stmt.close(); 
	         } catch(SQLException se2) { 
	         }  
	         try { 
	            if(conn!=null) conn.close(); 
	         } catch(SQLException se){ 
	            se.printStackTrace(); 
	         } 
	      } 
		 return list;
	}
	
	
	public Search createSearch(String quesryString, String user) throws UnsupportedEncodingException, IOException{
		
		System.out.println("I am here");
		
		String google = "http://www.google.com/search?q=";
		String charset = "UTF-8";
		String userAgent = "ExampleBot 1.0 (+http://example.com/bot)"; // Change this to your company's name and bot homepage!

		Elements links = Jsoup.connect(google + URLEncoder.encode(quesryString, charset)).userAgent(userAgent).get().select(".g>.r>a");
		String title = null;
		
		for (Element link : links) {
		    title = link.text();
		    break;
		    
		    
		}
		System.out.println("Title: " + title);
		
		
		return null;
		
		
		
		
		
	}

}
