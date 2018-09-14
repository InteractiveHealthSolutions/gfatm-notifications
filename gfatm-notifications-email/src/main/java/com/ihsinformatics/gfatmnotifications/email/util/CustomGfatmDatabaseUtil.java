/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications.email.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ihsinformatics.gfatmnotifications.common.Context;
import com.ihsinformatics.gfatmnotifications.common.model.ChilhoodFact;
import com.ihsinformatics.gfatmnotifications.common.model.Encounter;
import com.ihsinformatics.gfatmnotifications.common.model.FastFact;
import com.ihsinformatics.gfatmnotifications.common.model.Observation;
import com.ihsinformatics.gfatmnotifications.common.model.PatientScheduled;
import com.ihsinformatics.gfatmnotifications.common.model.PetFact;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class CustomGfatmDatabaseUtil {

	private static final Logger log = Logger.getLogger(Class.class.getName());
	private List<PatientScheduled> patientScheduledList;
	private List<FastFact> factsFast;
	private List<ChilhoodFact> factChildhood;
	private List<PetFact> factsPet;

	public CustomGfatmDatabaseUtil() {
	}

	/**
	 * not in use...
	 *
	 * @param enc
	 * @return
	 */
	public String checkReferelPresent(Encounter enc) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT encounter_id from encounter ");
		query.append(
				"where encounter_type = '28'  and patient_id = (select patient_id from patient_identifier where identifier = '"
						+ enc.getPatientId() + "') ");
		query.append("order by date_created desc ");
		query.append("limit 1");
		System.out.println(query);
		Object[][] data = Context.getLocalDb().getTableData(query.toString());
		String encID = "";
		if (data.length > 0) {
			for (Object[] row : data) {
				int k = 0;
				try {
					encID = Context.convertToString(row[k++]);
				} catch (Exception ex) {
					log.severe(ex.getMessage());
				}
			}
		}

		return encID;
	}

	/**
	 * With help of this method, we check the End of Follow-up form against the
	 * specific patient. this method returns true value when End of follow up form
	 * is not filed against the patient of this patient referral or transfer out
	 * from any location.
	 *
	 * @param encounter
	 * @return
	 */
	public Boolean isTransferOrReferel(Encounter encounter) {

		StringBuilder query = new StringBuilder();
		query.append("SELECT obs_id as obsId, value_coded as valueCoded FROM openmrs.obs where concept_id =159786");
		query.append(" and person_id = (select patient_id from patient_identifier where identifier = '"
				+ encounter.getIdentifier() + "')");

		String jsonString = Context.queryToJson(query.toString());
		List<Observation> codedValue = new ArrayList<Observation>();
		Type listType = new TypeToken<List<Observation>>() {
		}.getType();
		Gson gson = new Gson();
		codedValue = gson.fromJson(jsonString, listType);
		if (codedValue.isEmpty()) {
			return true;
		} else {
			for (Observation obs : codedValue) {
				if (obs.getValueCoded() == 159492 || obs.getValueCoded() == 1648) {
					return true;
				}
			}
		}
		return false;
	}

	public List<FastFact> getFactFast(String todayDate) {
		StringBuilder query = new StringBuilder();
		query.append(
				" select ff.location_id as locationId,l.name as locationName,l.description as locationDescription,dd.full_date as dateTime, ");
		query.append(
				" ff.Total_Screening as totalScreeingForm, ff.chest_xrays as chestXrays,ff.Verbal_Screen_Presumptives as verbalScreenPresumptives, ");
		query.append(
				" ff.Chest_XRay_Presumptives as chestXrayPresumptives,ff.Samples_Collected_Verbal_Screen_Presumptives as samplesCollectedVerbalScreenPresumptives , ");
		query.append(
				" ff.Samples_Collected_CXR_Presumptives as samplesCollectedCXRPresumptives,ff.GXP_Tests_Done as gxpTestsDone, ");
		query.append(
				" ff.Internal_Tests as internalTests, ff.External_Tests as externalTests,ff.MTBpve_Internal as mTBpveInternal ,ff.MTBpve_External as mTBpveExternal, ");
		query.append(
				" ff.MTBpve_RRpve_Internal as mTBpveRRpveInternal,ff.MTBpve_RRpve_External as mTBpveRRpveExternal, ");
		query.append(
				" ff.Error_No_result_Invalid as errorNoResultInvalid,ff.Clinically_Diagnosed as clinicallyDiagnosed,ff.Initiated_on_Antibiotic as initiatedOnAntibiotic, ff.Initiated_on_TBTx as initiatedOnTBTx ");
		query.append(" from fact_fast_dsss ff  ");
		query.append(" Inner join dim_datetime dd  on dd.datetime_id = ff.datetime_id ");
		query.append(" Inner join location l on l.location_id = ff.location_id ");
		query.append("where dd.full_date='" + todayDate + "';");
		// query.append("where dd.full_date='2017-12-11';");

		String jsonString = Context.queryToJson(query.toString());
		Type listType = new TypeToken<List<FastFact>>() {
		}.getType();
		Gson gson = new Gson();
		factsFast = gson.fromJson(jsonString, listType);
		return factsFast;

	}

	public List<ChilhoodFact> getFactChildhood(String todayDate) {
		StringBuilder query = new StringBuilder();
		query.append(
				" select dl.location_id as locationId,dl.location_name as locationName,dl.description as locationDescription , dd.full_date as dateTime, fc.Screened_nurse as screenedByNurse, fc.Presumptive_nurse as presumptiveByNurse , ");
		query.append(
				" fc.Presumptive_Case_Confirmed as presumptiveCaseConfirmedForms, fc.TB_Presumptive as tbPresumptiveConfirmed,fc.Test_indication as testIndication,fc.CBC_Indicated as cbcIndicated, fc.ESR_Indicated  as esrIndicated,fc.CXR_Indicated as cxrIndicated , ");
		query.append(
				" fc.MT_Indicated as mtIndicated,fc.Ultrasound_Indicated as ultrasoundIndicated,fc.HistopathologyFNAC_Indicated  as histopathologyFNACIndicated ,fc.CT_scan_Indicated as ctScanIndicated,fc.GXP_Indicated as gxpIndicated , fc.TB_Treatment_intiated as tbTreatmentIntiated, ");
		query.append(
				" fc.Antibiotic_trial_initiated as antibioticTrialInitiated,fc.IPT_treatment_initiated as iptTreatmentInitiated,fc.TB_Treatment_Follow_up as tbTreatmentFUP ,fc.Antibiotic_trial_Followup as antibioticTrialFUP,fc.IPT_follow_up as iptFUP,fc.End_of_followup as endOfFUP from fact_childtb_dsss fc  ");
		query.append(" inner join dim_location dl  on dl.location_id =fc.location_id ");
		query.append(" inner join dim_datetime dd on dd.datetime_id = fc.datetime_id ");
		query.append("where dd.full_date='" + todayDate + "';");
		// query.append("where dd.full_date ='2018-01-13';");

		String jsonString = Context.queryToJson(query.toString());
		Type listType = new TypeToken<List<ChilhoodFact>>() {
		}.getType();
		Gson gson = new Gson();
		factChildhood = gson.fromJson(jsonString, listType);
		return factChildhood;
	}

	public List<PetFact> getPetFact(String todayDate) {
		StringBuilder query = new StringBuilder();
		query.append(
				" SELECT dl.location_id as locationId,dl.location_name as locationName,dl.description as locationDescription,dd.full_date as dateTime,fp.No_Of_Index_Patients_Registered as noOfIndexPatientRegistered ,fp.No_Of_DSTB_Patients as noOfDSTBPatients, ");
		query.append(
				" fp.No_Of_DRTB_Patients as noOfDRTBPatients,fp.No_Of_Baseline_Screening as noOfBaselineScreening,fp.No_Of_Index_Patient_Agreed_For_Their_Contact_Screening as noOfIndexPatientsAgreed,fp.No_Of_Adult_Contacts as noOfAdultsContacts, ");
		query.append(
				" fp.No_Of_Peads_Contacts as noOfPeadsContacts ,fp.No_Of_Index_Not_Eligible_For_Study as noOfIndexNotEligibleStudy,fp.No_Of_Contact_Screening_Counseling_Done as noOfContactScreeningCounselingDone, fp.No_Of_Baseline_Counceling_Done as noOfBaselineCounselingDone, ");
		query.append(
				" fp.No_Of_Contacts_Investigated as noOfContactsInvestigated ,fp.No_Of_Contacts_Diagnosed_With_TB as noOfContactsDiagnosedTB,fp.No_Of_Contacts_Eligible_For_Pet as noOfContactsEligiblePET,fp.No_Of_Contacts_Agreed_For_Pet as noOfContactsAgreedPET , ");
		query.append(" fp.No_Of_Contacts_Completed_Treatment as noOfContactsCompletedTreatment FROM fact_Pet_DS fp ");
		query.append(" inner join dim_location dl on dl.location_id = fp.location_id ");
		query.append(" inner join dim_datetime dd on dd.datetime_id = fp.datetime_id ");
		query.append("where dd.full_date='" + todayDate + "';");
		String jsonString = Context.queryToJson(query.toString());
		Type listType = new TypeToken<List<PetFact>>() {
		}.getType();
		Gson gson = new Gson();
		factsPet = gson.fromJson(jsonString, listType);
		return factsPet;
	}

	public List<PatientScheduled> getPatientScheduledForVisit(String startDate, String endDate) {
		StringBuilder query = new StringBuilder();
		query.append(
				" SELECT distinct dp.external_id as externalId,GROUP_CONCAT(distinct p.name SEPARATOR ', ') as program ,dp.patient_identifier as patientIdentifier ,if(cctas.facility_scheduled is not null ,cctas.facility_scheduled,if(ccdtra.facility_scheduled is not null,ccdtra.facility_scheduled,ccifup.facility_scheduled) )  as facilityScheduled, ccifup.reason_for_call as reasonForCall,ccifup.facility_scheduled as fupFacilityScheduled ,date(ccifup.facility_visit_date) as fupFacilityVisitDate, ");
		query.append(
				" ccdtra.test_type as testType,ccdtra.facility_scheduled as raFacilityScheduled , date(ccdtra.facility_visit_date) as raFacilityVisitDate, ");
		query.append(
				" if(dp.health_center is not null,hdl.location_name,dl.location_name) as facilityName, date(mvf.return_visit_date)as mvfReturnVisitDate,cctas.facility_scheduled as taFacilityScheduled, date(cctas.facility_visit_date) as taFacilityVisitDate ");
		query.append(" FROM (   ");
		query.append(" select distinct ifup.patient_id from  enc_cc___tb_investigation_fup  ifup  ");
		query.append(" union ");
		query.append(" select distinct dtra.patient_id from enc_cc___diagnostic_test_result_available dtra  ");
		query.append(" union ");
		query.append(" select distinct mvf.patient_id  from enc_missed_visit_followup mvf   ");
		query.append(" ) basTable ");
		query.append(" inner join gfatm_dw.dim_patient dp on dp.patient_id = basTable.patient_id ");
		query.append(" inner join gfatm_dw.patient_identifier di on di.patient_id = basTable.patient_id  ");
		query.append(" inner join gfatm_dw.dim_location dl on dl.location_id = di.location_id ");
		query.append(" left join gfatm_dw.dim_location hdl on hdl.location_id =dp.health_center ");
		query.append(
				" left join gfatm_dw.enc_cc___tb_investigation_fup ccifup on ccifup.patient_id = basTable.patient_id and  ccifup.location_name='IBEX-KHI' and ccifup.encounter_id = (select encounter_id from enc_cc___tb_investigation_fup where patient_id =ccifup.patient_id order by date_entered DESC limit 1)   and date(ccifup.facility_visit_date) between '"
						+ startDate + "' and '" + endDate + "' ");
		query.append(" left join gfatm_dw.dim_user duFup on duFup.identifier= ccifup.provider  ");
		query.append(
				" left join gfatm_dw.enc_cc___diagnostic_test_result_available ccdtra on ccdtra.patient_id=basTable.patient_id and ccdtra.location_name='IBEX-KHI' and ccdtra.encounter_id = (select encounter_id from enc_cc___diagnostic_test_result_available where patient_id =ccdtra.patient_id order by date_entered DESC limit 1) and date(ccdtra.facility_visit_date) between '"
						+ startDate + "' and '" + endDate + "' ");
		query.append(" left join gfatm_dw.dim_user dudtra on dudtra.identifier= ccdtra.provider ");
		query.append(
				" left join gfatm_dw.enc_cc_treatment_adherence  cctas on cctas.patient_id =basTable.patient_id and cctas.location_name ='IBEX-KHI' and cctas.encounter_id =(select encounter_id from  gfatm_dw.enc_cc_treatment_adherence  where patient_id = cctas.patient_id order by date_entered DESC limit 1) and date(cctas.facility_visit_date) between '"
						+ startDate + "' and '" + endDate + "' ");
		query.append(" left join patient_program  pp on pp.patient_id = basTable.patient_id ");
		query.append(
				" left join gfatm_dw.enc_missed_visit_followup mvf on mvf.patient_id = basTable.patient_id and mvf.location_name ='IBEX-KHI' and mvf.encounter_id =(select encounter_id from gfatm_dw.enc_missed_visit_followup where patient_id =mvf.patient_id order by date_entered DESC limit 1) and date(mvf.return_visit_date) between '"
						+ startDate + "' and '" + endDate + "' ");
		query.append(" left join program p on p.program_id = pp.program_id ");
		query.append(
				" where ( (ccifup.facility_visit_date is not null) OR (ccdtra.facility_visit_date is not null )OR (cctas.facility_visit_date is not null) OR  (mvf.return_visit_date is not null) ) GROUP BY basTable.patient_id ; ");

		String jsonString = Context.queryToJson(query.toString());
		Type listType = new TypeToken<List<PatientScheduled>>() {
		}.getType();
		Gson gson = new Gson();
		patientScheduledList = gson.fromJson(jsonString, listType);
		return getPatientScheduledList();
	}

	public List<PatientScheduled> getPatientByScheduledFacilityName(String facilityName) {
		List<PatientScheduled> filteredList = new ArrayList<PatientScheduled>();
		if (getPatientScheduledList().isEmpty()) {
			return null;
		} else {
			try {
				System.out.println("Array Size : " + getPatientScheduledList().size());
				for (PatientScheduled patientScheduled : getPatientScheduledList()) {
					if (patientScheduled.getMvfReturnVisitDate() != null) {
						if (patientScheduled.getFacilityName().equals(facilityName)) {
							filteredList.add(patientScheduled);
						}
					} else {
						if (patientScheduled.getFacilityScheduled().equals(facilityName)) {
							filteredList.add(patientScheduled);
						}
					}
				}
			} catch (Exception e) {
				log.warning(e.getMessage());
			}
		}
		return filteredList;
	}

	/**
	 * @return the patientScheduledsList
	 */
	public List<PatientScheduled> getPatientScheduledList() {
		return patientScheduledList;
	}

	/**
	 * @param patientScheduledList
	 */
	public void setPatientScheduledList(List<PatientScheduled> patientScheduledList) {
		this.patientScheduledList = patientScheduledList;
	}

}
