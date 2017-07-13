package model;

public class Result {
	
	private Long id;
	
	private String google;
	
	private String yandex;

	public Result(Long id, String google, String yandex) {
		super();
		this.id = id;
		this.google = google;
		this.yandex = yandex;
	}
	
	

	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}


	public String getGoogle() {
		return google;
	}

	public void setGoogle(String google) {
		this.google = google;
	}

	public String getYandex() {
		return yandex;
	}

	public void setYandex(String yandex) {
		this.yandex = yandex;
	}
	
	

}
