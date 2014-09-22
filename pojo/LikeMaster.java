package com.pojo;

public class LikeMaster {

	private int likeID;
	private String likeName;
	private String likeType;
	private String application;
	private String refreshDateTime;

	public int getLikeID() {
		return likeID;
	}

	public void setLikeID(int likeID) {
		this.likeID = likeID;
	}

	public String getLikeName() {
		return likeName;
	}

	public void setLikeName(String likeName) {
		this.likeName = likeName;
	}

	public String getLikeType() {
		return likeType;
	}

	public void setLikeType(String likeType) {
		this.likeType = likeType;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getRefreshDateTime() {
		return refreshDateTime;
	}

	public void setRefreshDateTime(String refreshDateTime) {
		this.refreshDateTime = refreshDateTime;
	}
}
