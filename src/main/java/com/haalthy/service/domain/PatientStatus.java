package com.haalthy.service.domain;

import java.sql.Timestamp;
import java.io.Serializable;

public class PatientStatus implements Serializable{
	private static final long serialVersionUID = 8751282105532159742L;

	private int statusID;
	private String username;
	private String statusDesc;
	private String scanData;
	private Timestamp insertedDate;
	private int isPosted;
	private String imageURL;
	private int hasImage;
	
	public int getHasImage() {
		return hasImage;
	}
	public void setHasImage(int hasImage) {
		this.hasImage = hasImage;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public String getScanData() {
		return scanData;
	}
	public void setScanData(String scanData) {
		this.scanData = scanData;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public int getIsPosted() {
		return isPosted;
	}
	public void setIsPosted(int isPosted) {
		this.isPosted = isPosted;
	}
	public int getStatusID() {
		return statusID;
	}
	public void setStatusID(int statusID) {
		this.statusID = statusID;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Timestamp getInsertedDate() {
		return insertedDate;
	}
	public void setInsertedDate(Timestamp insertedDate) {
		this.insertedDate = insertedDate;
	}
}
