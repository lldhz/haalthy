package com.haalthy.service.controller.Interface.patient;

import java.util.List;

import com.haalthy.service.domain.PatientStatusFormat;

public class GetPatientstatusFormatResponse {
	private int result;
	private String resultDesp;
	private List<PatientStatusFormat> content;
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
	public List<PatientStatusFormat> getContent() {
		return content;
	}
	public void setContent(List<PatientStatusFormat> content) {
		this.content = content;
	}
}
