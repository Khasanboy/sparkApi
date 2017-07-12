package controller;

import static spark.Spark.*;
import service.SearchService;


public class SearchController {
	
	
	public SearchController(final SearchService searchService){
		get("/search", (req, res) -> searchService.getAllSearch(), common.JsonUtil.json());
		
		post("/search", (req, res) -> searchService.createSearch(
				req.queryParams("q"),
				req.queryParams("user")
		), common.JsonUtil.json());
	}

}
