package model;

import java.util.Date;

public class Search {
	
	private Long id;
	
	private String query;
	
	private String status;
	
	private Date created;
	
	private Result result;
	
	private transient Long userId;
	
	public Search() {
		super();
	}

	

	public Search(Long id, String query, String status, Date created, Result result, Long userId) {
		super();
		this.id = id;
		this.query = query;
		this.status = status;
		this.created = created;
		this.result = result;
		this.userId = userId;
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}
