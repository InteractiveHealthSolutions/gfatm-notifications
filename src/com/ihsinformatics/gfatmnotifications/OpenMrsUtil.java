/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import com.ihsinformatics.gfatmnotifications.model.Encounter;
import com.ihsinformatics.util.DatabaseUtil;
import com.ihsinformatics.util.DateTimeUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class OpenMrsUtil {

	private static final Logger log = Logger.getLogger(Class.class.getName());
	private DatabaseUtil db;

	public OpenMrsUtil(DatabaseUtil db) {
		this.setDb(db);
	}
	
	public Encounter getEncounter(int encounterId, int encounterTypeId){
		
		/*StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM encounter ");
		query.append("where encounter_id = "+encounterId+" and encounter_type = " +encounterTypeId );*/
		StringBuilder query = new StringBuilder();
		query.append("select e.encounter_id, et.name as encounter_type, pi.identifier, concat(pn.given_name, ' ', pn.family_name) as patient_name, e.encounter_datetime, l.description as encounter_location, pc.value as patient_contact, lc.value_reference as location_contact, pr.identifier as provider, upc.value as provider_contact, u.username, e.date_created, e.uuid from encounter as e ");
		query.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		query.append("inner join patient as p on p.patient_id = e.patient_id ");
		query.append("inner join patient_identifier as pi on pi.patient_id = p.patient_id and pi.identifier_type = 3 ");
		query.append("inner join person_name as pn on pn.person_id = p.patient_id and pn.preferred = 1 ");
		query.append("inner join person_attribute as pc on pc.person_id = p.patient_id and pc.person_attribute_type_id = 8 ");
		query.append("left outer join location as l on l.location_id = e.location_id ");
		query.append("left outer join location_attribute as lc on lc.location_id = l.location_id and lc.attribute_type_id = 2 ");
		query.append("left outer join encounter_provider as ep on ep.encounter_id = e.encounter_id ");
		query.append("left outer join provider as pr on pr.provider_id = ep.encounter_id ");
		query.append("left outer join person_attribute as upc on upc.person_id = pr.person_id and upc.person_attribute_type_id = 8 ");
		query.append("left outer join users as u on u.system_id = pr.identifier ");
		query.append("where e.encounter_id = "+encounterId+" and e.encounter_type = " +encounterTypeId );
		
		Object[][] data = db.getTableData(query.toString());
		List<Encounter> encounters = new ArrayList<Encounter>();
		for (Object[] row : data) {
			int k = 0;
			try {
				encounterId = Integer.parseInt(row[k++].toString());
				String encounterType = convertToString(row[k++]);
				String patientId = convertToString(row[k++]);
				String patientName = convertToString(row[k++]);
				DateTime encounterDate = new DateTime(DateTimeUtil.getDateFromString(row[k++].toString(), DateTimeUtil.SQL_DATETIME));
				String locationName = convertToString(row[k++]);
				String patientContact = convertToString(row[k++]);
				String locationContact = convertToString(row[k++]);
				String providerId = convertToString(row[k++]);
				String providerContact = convertToString(row[k++]);
				String user = convertToString(row[k++]);
				DateTime dateCreated = new DateTime(DateTimeUtil.getDateFromString(row[k++].toString(), DateTimeUtil.SQL_DATETIME));
				String uuid = convertToString(row[k++]);

				Encounter encounter = new Encounter(encounterId, encounterType, encounterDate, patientId, providerId, locationName, uuid); 
				encounter.setPatientName(patientName);
				encounter.setPatientContact(patientContact);
				encounter.setLocationContact(locationContact);
				encounter.setProviderContact(providerContact);
				encounter.setDateCreated(dateCreated);
				encounter.setUsername(user);
				encounters.add(encounter);
			} catch (Exception ex) {
				log.severe(ex.getMessage());
			}
		}
		return encounters.get(0);
		
	}


	/**
	 * Fetch encounters by date range and type (optional)
	 * 
	 * @param from
	 * @param to
	 * @param type
	 * @return
	 */
	
		public List<Encounter> getEncounters(DateTime from, DateTime to, Integer type) {
		if (from == null || to == null) {
			return null;
		}
		String sqlFrom = DateTimeUtil.getSqlDateTime(from.toDate());
		String sqlTo = DateTimeUtil.getSqlDateTime(to.toDate());
		StringBuilder filter = new StringBuilder();
		filter.append("where e.voided = 0 and e.date_created between ");
		filter.append("timestamp('" + sqlFrom + "')");
		filter.append(" and ");
		filter.append("timestamp('" + sqlTo + "')");
		filter.append(" and ");
		filter.append("timestampdiff(HOUR, e.date_created, e.encounter_datetime) <= 48");
		if (type != null) {
			filter.append(" and e.encounter_type=" + type);
		}
		StringBuilder query = new StringBuilder();
		query.append("select e.encounter_id, et.name as encounter_type, pi.identifier, concat(pn.given_name, ' ', pn.family_name) as patient_name, e.encounter_datetime, l.description as encounter_location, pc.value as patient_contact, lc.value_reference as location_contact, pr.identifier as provider, upc.value as provider_contact, u.username, e.date_created, e.uuid from encounter as e ");
		query.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		query.append("inner join patient as p on p.patient_id = e.patient_id ");
		query.append("inner join patient_identifier as pi on pi.patient_id = p.patient_id and pi.identifier_type = 3 ");
		query.append("inner join person_name as pn on pn.person_id = p.patient_id and pn.preferred = 1 ");
		query.append("inner join person_attribute as pc on pc.person_id = p.patient_id and pc.person_attribute_type_id = 8 ");
		query.append("left outer join location as l on l.location_id = e.location_id ");
		query.append("left outer join location_attribute as lc on lc.location_id = l.location_id and lc.attribute_type_id = 2 ");
		query.append("left outer join encounter_provider as ep on ep.encounter_id = e.encounter_id ");
		query.append("left outer join provider as pr on pr.provider_id = ep.encounter_id ");
		query.append("left outer join person_attribute as upc on upc.person_id = pr.person_id and upc.person_attribute_type_id = 8 ");
		query.append("left outer join users as u on u.system_id = pr.identifier ");
		query.append(filter);
		Object[][] data = db.getTableData(query.toString());
		List<Encounter> encounters = new ArrayList<Encounter>();
		for (Object[] row : data) {
			int k = 0;
			try {
				int encounterId = Integer.parseInt(row[k++].toString());
				String encounterType = convertToString(row[k++]);
				String patientId = convertToString(row[k++]);
				String patientName = convertToString(row[k++]);
				DateTime encounterDate = new DateTime(DateTimeUtil.getDateFromString(row[k++].toString(), DateTimeUtil.SQL_DATETIME));
				String locationName = convertToString(row[k++]);
				String patientContact = convertToString(row[k++]);
				String locationContact = convertToString(row[k++]);
				String providerId = convertToString(row[k++]);
				String providerContact = convertToString(row[k++]);
				String user = convertToString(row[k++]);
				DateTime dateCreated = new DateTime(DateTimeUtil.getDateFromString(row[k++].toString(), DateTimeUtil.SQL_DATETIME));
				String uuid = convertToString(row[k++]);

				Encounter encounter = new Encounter(encounterId, encounterType, encounterDate, patientId, providerId, locationName, uuid); 
				encounter.setPatientName(patientName);
				encounter.setPatientContact(patientContact);
				encounter.setLocationContact(locationContact);
				encounter.setProviderContact(providerContact);
				encounter.setDateCreated(dateCreated);
				encounter.setUsername(user);
				encounters.add(encounter);
			} catch (Exception ex) {
				log.severe(ex.getMessage());
			}
		}
		return encounters;
	}

	public String getSiteSupervisorContact(String referralSite){
		
		StringBuilder query = new StringBuilder();
		query.append("select pa.value from person_attribute pa ");
		query.append("inner join users u on u.person_id = pa.person_id and person_attribute_type_id = '7' ");
		query.append("inner join user_role ur on ur.user_id=u.user_id and ur.role = 'field supervisor' ");
		query.append("inner join location l on l.location_id = pa.value and l.name = '"+referralSite+"'");
		System.out.println(query);
		Object[][] data =  db.getTableData(query.toString());
	
		String contact="";
		for (Object[] row : data) {
			int k = 0;
			try {
				contact = convertToString(row[k++]);
			} catch (Exception ex) {
				log.severe(ex.getMessage());
			}
		}
		System.out.println(contact);
		return contact;
		
		/*	StringBuilder query = new StringBuilder();
			query.append("select value_text from obs ");
			query.append("where encounter_id ="+encounter.getEncounterId()+" and concept_id = " +conceptId);
			Object[][] data =  db.getTableData(query.toString());

		
			for (Object[] row : data) {
				int k = 0;
				try {
					String locName = convertToString(row[k++]);
					
					System.out.println(locName);
					
				} catch (Exception ex) {
					log.severe(ex.getMessage());
				}
			}
	*/
		
	}
	
	public void check(){
		System.out.println("helloWorld");
	}

	public Boolean getfilter(Encounter encounter, String encounterType) {
		
		StringBuilder query = new StringBuilder();
	//	query.append("select patient_id from patient_identifier where identifier='"+encounter.getPatientId()+"'");
		query.append("select e.encounter_type, et.name from encounter e ");
		query.append("inner join encounter_type et on e.encounter_type = et.encounter_type_id and et.name = '" +encounterType+"'");
		query.append(" and e.patient_id = (select patient_id from patient_identifier where identifier = '"+encounter.getPatientId()+"')");
	//	query.append("and e.patient_id= (select patient_id from patient_identifier where identifier='YXAIA-1')");
	
	
		Object[][] data =  db.getTableData(query.toString());
	
		System.out.println(data.length);
		if(data.length >0)
			return false;
		
		return true;
	}
	
	
	
	public Map<String, Object> getEncounterObservations(Encounter encounter) {
		Map<String, Object> observations;

		StringBuilder query = new StringBuilder();
		query.append("select q.name as obs, concat(ifnull(a.name, ''), ifnull(o.value_boolean, ''), ifnull(o.value_datetime, ''), ifnull(o.value_text, ''), ifnull(o.value_numeric, '')) as value from obs as o ");
		query.append("left outer join concept_name as q on q.concept_id = o.concept_id and q.locale = 'en' and q.concept_name_type = 'SHORT' and q.voided = 0 ");
		query.append("left outer join concept_name as a on a.concept_id = o.value_coded and a.locale = 'en' and a.locale_preferred = 1 and a.voided = 0 ");
		query.append("where o.voided = 0 and o.encounter_id = " + encounter.getEncounterId());
		Object[][] data = db.getTableData(query.toString());
		observations = new HashMap<String, Object>();
		for (Object[] row : data) {
			int k = 0;
			try {
				String observation = convertToString(row[k++]);
				String value = convertToString(row[k++]);
				
				observations.put(observation, value);
			} catch (Exception ex) {
				log.severe(ex.getMessage());
			}
		}
		return observations;
	}
	
	/**
	 * Fetch full name of a person by Id
	 * 
	 * @param personId
	 * @return
	 */
	public String getPersonNameById(Integer personId) {
		Object[] names = db.getRecord("person_name", "given_name,family_name",
				"person_id=" + personId);
		String name = names[0].toString();
		if (!"".equals(names[1])) {
			name += names[1].toString();
		}
		return name;
	}

	public DatabaseUtil getDb() {
		return db;
	}

	public void setDb(DatabaseUtil db) {
		this.db = db;
	}

	public String convertToString(Object obj) {
		return obj == null ? null : obj.toString();
	}
}
