package application;

import controller.SearchController;
import service.SearchService;

public class App {
	
	public static void  main(String[] args) {
		new SearchController(new SearchService());
	}

}
