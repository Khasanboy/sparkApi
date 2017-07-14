package model;

public class SearchEngine {
	
	private String name;
	
	private String link;
	
	private String selector;

	public SearchEngine(String name, String link,  String selector) {
		super();
		this.name = name;
		this.link = link;
		this.selector = selector;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}
	
	

}
