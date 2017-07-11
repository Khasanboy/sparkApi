package model;

import java.util.Date;

public class Search {
	
	private Long Id;
	
	private String query;
	
	private String status;
	
	private Date created;
	
	private Result result;

	public Search() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Search(Long id, String query, String status, Date created, Result result) {
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

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}
	
	
	
	

}
