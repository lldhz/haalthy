package com.haalthy.service.controller.Interface;
import com.haalthy.service.controller.Interface.ContentIntEapsulate;

public class GetCountResponse {
	private int result;
	private String resultDesp;
	private ContentIntEapsulate content;
	
	public ContentIntEapsulate getContent() {
		return content;
	}
	public void setContent(ContentIntEapsulate content) {
		this.content = content;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getResultDesp() {
		return resultDesp;
	}
	public void setResultDesp(String resultDesp) {
		this.resultDesp = resultDesp;
	}
}
