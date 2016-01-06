package com.haalthy.service.persistence;

import java.util.List;

import com.haalthy.service.domain.ClinicData;
import com.haalthy.service.domain.ClinicReport;
import com.haalthy.service.domain.ClinicReportFormat;
import com.haalthy.service.domain.PatientStatus;
import com.haalthy.service.domain.PatientStatusFormat;
import com.haalthy.service.domain.Treatment;
import com.haalthy.service.domain.TreatmentFormat;

public interface PatientMapper {
	List<Treatment> getTreatmentsByUser(String username);
	
	List<PatientStatus> getPatientStatusByTreatment(int treatmentID);
	
	int insertTreatment(Treatment treatment);
	
	int insertPatientStatus(PatientStatus patientStatus);
	
	List<TreatmentFormat> getTreatmentFormat();
	
	List<PatientStatusFormat> getPatientStatusFormat();
	
	List<PatientStatus> getPatientStatusByUser(String username);
	
	List<ClinicReportFormat> getClinicReportFormat();
	
	int insertClinicReport(ClinicReport clinicReport);
	
	List<ClinicReport> getClinicReportByUser(String username);
	
	List<Treatment> getPostedTreatmentsByUser(String username);

	List<PatientStatus> getPostedPatientStatusByUser(String username);
	
	List<ClinicReport> getPostedClinicReportByUser(String username);
	
	int updateTreatmentById(Treatment treatment);
	
	int deleteTreatmentById(int treatmentId);
	
	int insertClinicData(List<ClinicData> clinicData);
	
	List<ClinicData> getClinicDataByUsername(String insertUsername);

	int updatePatientImg(PatientStatus patientStatus);
	int appendPatientImg(PatientStatus patientStatus);
}
