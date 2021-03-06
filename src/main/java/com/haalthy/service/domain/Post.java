package com.haalthy.service.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.haalthy.service.configuration.*;

/**
 * @author lily
 *
 */
public class Post implements Serializable{
	private static final long serialVersionUID = 8751282105532159742L;
	private int postID;
	private String insertUsername;
	private String body;
	private String tags;
	private int countComments;
	private int countBookmarks;
	private int countViews;
	private int closed;
	private int isBroadcast;
	private Timestamp dateInserted;
	private Timestamp dateUpdated;
	private int isActive;
//	private byte[] image;
	private int type;
	private int treatmentID;
	private int patientStatusID;
	private String gender;
	private String pathological;
	private int age;
	private String cancerType;
	private String metastasis;
	private String stage;
	private int hasImage;
	private String displayname;
	private String highlight;
	private String clinicReport;
	private String imageURL;
	private String portraitURL;
	private String geneticMutation;
	
	public String getGeneticMutation() {
		return geneticMutation;
	}
	public void setGeneticMutation(String geneticMutation) {
		this.geneticMutation = geneticMutation;
	}
	public String getPortraitURL() {
		return portraitURL;
	}
	public void setPortraitURL(String portraitURL) {
		this.portraitURL = portraitURL;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public String getClinicReport() {
		return clinicReport;
	}
	public void setClinicReport(String clinicReport) {
		this.clinicReport = clinicReport;
	}
	public String getHighlight() {
		return highlight;
	}
	public void setHighlight(String highlight) {
		this.highlight = highlight;
	}
	public String getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	public int getHasImage() {
		return hasImage;
	}
	public void setHasImage(int hasImage) {
		this.hasImage = hasImage;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPathological() {
		return pathological;
	}
	public void setPathological(String pathological) {
		this.pathological = pathological;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getCancerType() {
		return cancerType;
	}
	public void setCancerType(String cancerType) {
		this.cancerType = cancerType;
	}
	public String getMetastasis() {
		return metastasis;
	}
	public void setMetastasis(String metastasis) {
		this.metastasis = metastasis;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getTreatmentID() {
		return treatmentID;
	}
	public void setTreatmentID(int treatmentID) {
		this.treatmentID = treatmentID;
	}
	public int getPatientStatusID() {
		return patientStatusID;
	}
	public void setPatientStatusID(int patientStatusID) {
		this.patientStatusID = patientStatusID;
	}
	public String getInsertUsername() {
		return insertUsername;
	}
	public void setInsertUsername(String insertUsername) {
		this.insertUsername = insertUsername;
	}
	public Timestamp getDateUpdated() {
		return dateUpdated;
	}
	public void setDateUpdated(Timestamp dateUpdated) {
		this.dateUpdated = dateUpdated;
	}
	public Timestamp getDateInserted() {
		return dateInserted;
	}
	public void setDateInserted(Timestamp dateInserted) {
		this.dateInserted = dateInserted;
	}
//	public byte[] getImage() {
//		return image;
//	}
//	public void setImage(byte[] image) {
//		this.image = image;
//	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public int getPostID() {
		return postID;
	}
	public void setPostID(int postID) {
		this.postID = postID;
	}
	public String getBody() {
		return this.body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getTags() {
		return this.tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public int getCountComments() {
		return this.countComments;
	}
	public void setCountComments(int countComments) {
		this.countComments = countComments;
	}
	public int getCountBookmarks() {
		return this.countBookmarks;
	}
	public void setCountBookmarks(int countBookmarks) {
		this.countBookmarks = countBookmarks;
	}
	public int getCountViews() {
		return this.countViews;
	}
	public void setCountViews(int countViews) {
		this.countViews = countViews;
	}
	public int getClosed() {
		return this.closed;
	}
	public void setClosed(int closed) {
		this.closed = closed;
	}
	public int getIsBroadcast() {
		return this.isBroadcast;
	}
	public void setIsBroadcast(int isBroadcast) {
		this.isBroadcast = isBroadcast;
	}
}
