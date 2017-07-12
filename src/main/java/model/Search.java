package model;

import java.util.Date;
import java.util.List;

public class Search {
	
	private Long Id;
	
	private String query;
	
	private String status;
	
	private Date created;
	
	private List<Result> result;

	public Search(Long id, String query, String status, Date created, List<Result> result) {
		super();
		Id = id;
		this.query = query;
		this.status = status;
		this.created = created;
		this.result = result;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public List<Result> getResult() {
		return result;
	}

	public void setResult(List<Result> result) {
		this.result = result;
	}	
	
	

}
