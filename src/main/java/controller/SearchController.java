package controller;

import static spark.Spark.*;
import service.SearchService;


public class SearchController {
	
	
	public SearchController(final SearchService searchService){
		
		get("/searches", (req, res) -> searchService.getAllSearchByUserName(req.queryParams("q")), common.JsonUtil.json());
		
		get("/searches", (req, res) -> searchService.getAllSearch(), common.JsonUtil.json());
		
		post("/search", (req, res) -> searchService.createSearch(
				req.queryParams("q"),
				req.queryParams("user")
		), common.JsonUtil.json());
		
	
		
		delete("/search", (req, res) -> searchService.deleteSearch(req.queryParams("id")));
	}

}
