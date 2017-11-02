/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.GsonBuilder;
import com.ihsinformatics.gfatmnotifications.model.DateDeserializer;
import com.ihsinformatics.gfatmnotifications.model.DateSerializer;
import com.ihsinformatics.gfatmnotifications.model.Encounter;
import com.ihsinformatics.gfatmnotifications.model.Location;
import com.ihsinformatics.gfatmnotifications.model.Patient;
import com.ihsinformatics.gfatmnotifications.model.User;
import com.ihsinformatics.util.DatabaseUtil;
import com.ihsinformatics.util.DateTimeUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class OpenMrsUtil {

	private static final Logger log = Logger.getLogger(Class.class.getName());
	private static List<User> users;
	private static List<Location> locations;
	private static Map<Integer, String> encounterTypes;
	private DatabaseUtil db;

	public OpenMrsUtil(DatabaseUtil db) {
		this.setDb(db);
	}

	public DatabaseUtil getDb() {
		return db;
	}

	public void setDb(DatabaseUtil db) {
		this.db = db;
	}

	/**
	 * Execultes query and converts result set into JSON string
	 * 
	 * @param query
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public String queryToJson(String query) {
		List<Map<String, Object>> listOfMaps = null;
		QueryRunner queryRunner = new QueryRunner();
		try {
			listOfMaps = queryRunner.query(db.getConnection(), query,
					new MapListHandler());
		} catch (Exception e) {
			e.printStackTrace();
		}
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Date.class, new DateDeserializer());
		builder.registerTypeAdapter(Date.class, new DateSerializer());
		builder.setPrettyPrinting();
		String json = builder.create().toJson(listOfMaps);
		System.out.println(json);
		return json;
	}

	public String convertToString(Object obj) {
		return obj == null ? null : obj.toString();
	}

	/**
	 * @return the users
	 */
	public static List<User> getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            the users to set
	 */
	public static void setUsers(List<User> users) {
		OpenMrsUtil.users = users;
	}

	/**
	 * @return the locations
	 */
	public static List<Location> getLocations() {
		return locations;
	}

	/**
	 * @param locations
	 *            the locations to set
	 */
	public static void setLocations(List<Location> locations) {
		OpenMrsUtil.locations = locations;
	}

	/**
	 * @return the encounterTypes
	 */
	public static Map<Integer, String> getEncounterTypes() {
		return encounterTypes;
	}

	/**
	 * @param encounterTypes
	 *            the encounterTypes to set
	 */
	public static void setEncounterTypes(Map<Integer, String> encounterTypes) {
		OpenMrsUtil.encounterTypes = encounterTypes;
	}

	public void loadEncounterTypes() {
		setEncounterTypes(new HashMap<Integer, String>());
		StringBuilder query = new StringBuilder(
				"SELECT encounter_type_id, name FROM encounter_type where retired = 0");
		Object[][] data = db.getTableData(query.toString());
		if (data == null) {
			return;
		}
		for (int i = 0; i < data.length; i++) {
			getEncounterTypes().put(Integer.parseInt(data[i][0].toString()),
					data[i][1].toString());
		}
	}

	/**
	 * Fetch all locations from DB and store into locations
	 */
	public void loadLocations() {
		setLocations(new ArrayList<Location>());
		StringBuilder query = new StringBuilder();
		query.append("select l.location_id as locationId, l.name, l.parent_location as parentId, l.uuid, (case ifnull(ltfast.location_id, 0) when 0 then 0 else 1 end) as fast, (case ifnull(ltpet.location_id, 0) when 0 then 0 else 1 end) as pet, (case ifnull(ltpmdt.location_id, 0) when 0 then 0 else 1 end) as pmdt, (case ifnull(ltctb.location_id, 0) when 0 then 0 else 1 end) as childhood_tb, (case ifnull(ltcomorb.location_id, 0) when 0 then 0 else 1 end) as comorbidities, ");
		query.append("pcontact.value_reference as primaryContact, pcontact_nm.value_reference as primaryContactName, scontact.value_reference as secondaryContact, scontact_nm.value_reference as secondaryContactName, ");
		query.append("ltype.value_reference as locationType, l.address1, l.address2, l.address3, l.city_village as cityVillage, l.state_province as stateProvince, l.description, l.date_created as dateCreated, st.value_reference as status from location as l ");
		query.append("left outer join location_tag_map as ltfast on ltfast.location_id = l.location_id and ltfast.location_tag_id = 6 ");
		query.append("left outer join location_tag_map as ltpet on ltpet.location_id = l.location_id and ltpet.location_tag_id = 7 ");
		query.append("left outer join location_tag_map as ltpmdt on ltpmdt.location_id = l.location_id and ltpmdt.location_tag_id = 8 ");
		query.append("left outer join location_tag_map as ltctb on ltctb.location_id = l.location_id and ltctb.location_tag_id = 9 ");
		query.append("left outer join location_tag_map as ltcomorb on ltcomorb.location_id = l.location_id and ltcomorb.location_tag_id = 10 ");
		query.append("left outer join location_attribute as pcontact on pcontact.location_id = l.location_id and pcontact.attribute_type_id = 2 and pcontact.voided = 0 ");
		query.append("left outer join location_attribute as pcontact_nm on pcontact_nm.location_id = l.location_id and pcontact_nm.attribute_type_id = 14 and pcontact_nm.voided = 0 ");
		query.append("left outer join location_attribute as scontact on scontact.location_id = l.location_id and scontact.attribute_type_id = 10 and scontact.voided = 0 ");
		query.append("left outer join location_attribute as scontact_nm on scontact_nm.location_id = l.location_id and scontact_nm.attribute_type_id = 15 and scontact_nm.voided = 0 ");
		query.append("left outer join location_attribute as ltype on ltype.location_id = l.location_id and ltype.attribute_type_id = 9 and ltype.voided = 0 ");
		query.append("left outer join location_attribute as st on st.location_id = l.location_id and st.attribute_type_id = 13 and st.voided = 0 ");
		query.append("where l.retired = 0");
		Field[] fields = Location.class.getDeclaredFields();
		JSONArray array = new JSONArray(queryToJson(query.toString()));
		for (int i = 0; i < array.length(); i++) {
			JSONObject json = array.getJSONObject(i);
			Location location = new Location();
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = null;
				try {
					value = json.get(field.getName());
					field.set(location, value);
				} catch (JSONException e) {
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
			locations.add(location);
		}
	}

	/**
	 * Fetch all users from DB and store into users
	 */
	public void loadUsers() {
		setUsers(new ArrayList<User>());
		StringBuilder query = new StringBuilder();
		query.append("select u.user_id as userId, u.person_id as personId, u.system_id as systemId, u.username, pn.given_name as givenName, pn.family_name as lastName, p.gender, pcontact.value as primaryContact, scontact.value as secondaryContact, hd.value as healthDistrict, hc.value as healthCenter, ");
		query.append("edu.value as educationLevel, emp.value as employmentStatus, occu.value as occupation, lang.value as motherTongue, nic.value as nationalId, pa.address1, pa.address2, pa.county_district as district, pa.city_village as cityVillage, pa.country, pa.address3 as landmark, inter.value_reference as intervention, u.date_created as dateCreated, u.uuid from users as u ");
		query.append("inner join person as p on p.person_id = u.person_id ");
		query.append("inner join person_name as pn on pn.person_id = p.person_id ");
		query.append("inner join provider as pr on pr.person_id = p.person_id and pr.identifier = u.system_id ");
		query.append("left outer join provider_attribute as inter on inter.provider_id = pr.provider_id and inter.attribute_type_id = 1 and inter.voided = 0 ");
		query.append("left outer join person_attribute as hd on hd.person_id = p.person_id and hd.person_attribute_type_id = 6 and hd.voided = 0 ");
		query.append("left outer join person_attribute as hc on hc.person_id = p.person_id and hc.person_attribute_type_id = 7 and hc.voided = 0 ");
		query.append("left outer join person_attribute as pcontact on pcontact.person_id = p.person_id and pcontact.person_attribute_type_id = 8 and pcontact.voided = 0 ");
		query.append("left outer join person_attribute as scontact on scontact.person_id = p.person_id and scontact.person_attribute_type_id = 12 and scontact.voided = 0 ");
		query.append("left outer join person_attribute as edu on edu.person_id = p.person_id and edu.person_attribute_type_id = 15 and edu.voided = 0 ");
		query.append("left outer join person_attribute as emp on emp.person_id = p.person_id and emp.person_attribute_type_id = 16 and emp.voided = 0 ");
		query.append("left outer join person_attribute as occu on occu.person_id = p.person_id and occu.person_attribute_type_id = 17 and occu.voided = 0 ");
		query.append("left outer join person_attribute as lang on lang.person_id = p.person_id and lang.person_attribute_type_id = 18 and lang.voided = 0 ");
		query.append("left outer join person_attribute as nic on nic.person_id = p.person_id and nic.person_attribute_type_id = 20 and nic.voided = 0 ");
		query.append("left outer join person_address as pa on pa.person_id = p.person_id and pa.voided = 0 and pa.preferred = 1 ");
		query.append("where u.retired = 0");
		Field[] fields = User.class.getDeclaredFields();
		JSONArray array = new JSONArray(queryToJson(query.toString()));
		for (int i = 0; i < array.length(); i++) {
			JSONObject json = array.getJSONObject(i);
			User user = new User();
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = null;
				try {
					value = json.get(field.getName());
					field.set(user, value);
				} catch (JSONException e) {
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
			users.add(user);
		}
	}

	/**
	 * Fetch Encounter object by encounter ID
	 * 
	 * @param encounterId
	 * @param encounterTypeId
	 * @return
	 */
	public Encounter getEncounter(int encounterId, int encounterTypeId) {
		StringBuilder query = new StringBuilder();
		query.append("select e.encounter_id, et.name as encounter_type, pi.identifier, concat(pn.given_name, ' ', pn.family_name) as patient_name, e.encounter_datetime, l.description as encounter_location, pc.value as patient_contact, lc.value_reference as location_contact, pr.identifier as provider, upc.value as provider_contact, u.username, e.date_created, e.uuid from encounter as e ");
		query.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		query.append("inner join patient as p on p.patient_id = e.patient_id ");
		query.append("inner join patient_identifier as pi on pi.patient_id = p.patient_id and pi.identifier_type = 3 ");
		query.append("inner join person_name as pn on pn.person_id = p.patient_id and pn.preferred = 1 ");
		query.append("inner join person_attribute as pc on pc.person_id = p.patient_id and pc.person_attribute_type_id = 8 and pc.voided = 0 ");
		query.append("left outer join location as l on l.location_id = e.location_id ");
		query.append("left outer join location_attribute as lc on lc.location_id = l.location_id and lc.attribute_type_id = 2 ");
		query.append("left outer join encounter_provider as ep on ep.encounter_id = e.encounter_id ");
		query.append("left outer join provider as pr on pr.provider_id = ep.encounter_id ");
		query.append("left outer join person_attribute as upc on upc.person_id = pr.person_id and upc.person_attribute_type_id = 8 ");
		query.append("left outer join users as u on u.system_id = pr.identifier ");
		query.append("where e.encounter_id = " + encounterId);
		StringBuilder filter = new StringBuilder();
		filter.append("where e.voided = 0 and e.date_created >= date_sub(NOW(), interval 5 minute) ");
		query.append(filter);
		query.append("where e.encounter_id = " + encounterId
				+ " and e.encounter_type = " + encounterTypeId);
		System.out.println(query);
		Object[][] data = db.getTableData(query.toString());
		List<Encounter> encounters = new ArrayList<Encounter>();
		for (Object[] row : data) {
			int k = 0;
			try {
				encounterId = Integer.parseInt(row[k++].toString());
				System.out.println(encounterId);
				String encounterType = convertToString(row[k++]);
				String patientId = convertToString(row[k++]);
				String patientName = convertToString(row[k++]);
				DateTime encounterDate = new DateTime(
						DateTimeUtil.getDateFromString(row[k++].toString(),
								DateTimeUtil.SQL_DATETIME));
				String locationName = convertToString(row[k++]);
				String patientContact = convertToString(row[k++]);
				String locationContact = convertToString(row[k++]);
				String providerId = convertToString(row[k++]);
				String providerContact = convertToString(row[k++]);
				String user = convertToString(row[k++]);
				DateTime dateCreated = new DateTime(
						DateTimeUtil.getDateFromString(row[k++].toString(),
								DateTimeUtil.SQL_DATETIME));
				String uuid = convertToString(row[k++]);
				System.out.println(locationName);
				Encounter encounter = new Encounter(encounterId, encounterType,
						encounterDate, patientId, providerId, locationName,
						uuid);
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
	public List<Encounter> getEncounters(DateTime from, DateTime to,
			Integer type) {
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
		filter.append("timestampdiff(HOUR, e.date_created, e.encounter_datetime) <= 24");
		if (type != null) {
			filter.append(" and e.encounter_type=" + type);
		}
		StringBuilder query = new StringBuilder();
		query.append("select e.encounter_id, et.name as encounter_type, pi.identifier, concat(pn.given_name, ' ', pn.family_name) as patient_name, e.encounter_datetime, l.description as encounter_location, pc.value as patient_contact, lc.value_reference as location_contact, pr.identifier as provider, upc.value as provider_contact, u.username, e.date_created, e.uuid from encounter as e ");
		query.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		query.append("inner join patient as p on p.patient_id = e.patient_id ");
		query.append("inner join patient_identifier as pi on pi.patient_id = p.patient_id and pi.identifier_type = 3 ");
		query.append("inner join person_name as pn on pn.person_id = p.patient_id and pn.preferred = 1 ");
		query.append("inner join person_attribute as pc on pc.person_id = p.patient_id and pc.person_attribute_type_id = 8 and pc.voided = 0 ");
		query.append("left outer join location as l on l.location_id = e.location_id ");
		query.append("left outer join location_attribute as lc on lc.location_id = l.location_id and lc.attribute_type_id = 2 ");
		query.append("left outer join encounter_provider as ep on ep.encounter_id = e.encounter_id ");
		query.append("left outer join provider as pr on pr.provider_id = ep.encounter_id ");
		query.append("left outer join person_attribute as upc on upc.person_id = pr.person_id and upc.person_attribute_type_id = 8 ");
		query.append("left outer join users as u on u.system_id = pr.identifier ");
		query.append(filter);
		System.out.println(query);
		Object[][] data = db.getTableData(query.toString());
		List<Encounter> encounters = new ArrayList<Encounter>();
		for (Object[] row : data) {
			int k = 0;
			try {
				int encounterId = Integer.parseInt(row[k++].toString());
				String encounterType = convertToString(row[k++]);
				String patientId = convertToString(row[k++]);
				String patientName = convertToString(row[k++]);
				DateTime encounterDate = new DateTime(
						DateTimeUtil.getDateFromString(row[k++].toString(),
								DateTimeUtil.SQL_DATETIME));
				String locationName = convertToString(row[k++]);
				String patientContact = convertToString(row[k++]);
				String locationContact = convertToString(row[k++]);
				String providerId = convertToString(row[k++]);
				String providerContact = convertToString(row[k++]);
				String user = convertToString(row[k++]);
				DateTime dateCreated = new DateTime(
						DateTimeUtil.getDateFromString(row[k++].toString(),
								DateTimeUtil.SQL_DATETIME));
				String uuid = convertToString(row[k++]);

				Encounter encounter = new Encounter(encounterId, encounterType,
						encounterDate, patientId, providerId, locationName,
						uuid);
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

	public ArrayList<String> getSiteSupervisorContact(String referralSite) {
		StringBuilder query = new StringBuilder();
		query.append("select pa.value  from person ps ");
		query.append("inner join person_attribute pa on pa.person_id=ps.person_id and pa.person_attribute_type_id='8' ");
		query.append("inner join person_attribute pa2 on pa2.person_id=ps.person_id and pa2.person_attribute_type_id='7' ");
		query.append("inner join users u on u.person_id = pa.person_id ");
		query.append("inner join user_role ur on ur.user_id = u.user_id ");
		query.append("where ur.role = 'Field Supervisor' and (pa2.value = '"
				+ referralSite + "' or pa2.value = '" + referralSite + "')");
		System.out.println(query);
		Object[][] data = db.getTableData(query.toString());
		ArrayList<String> contact = new ArrayList<String>();
		for (Object[] row : data) {
			int k = 0;
			try {
				contact.add(convertToString(row[k++]));
			} catch (Exception ex) {
				log.severe(ex.getMessage());
			}

		}
		return contact;
	}

	public Boolean getfilter(Encounter encounter, String encounterType) {

		StringBuilder query = new StringBuilder();
		// query.append("select patient_id from patient_identifier where identifier='"+encounter.getPatientId()+"'");
		query.append("select e.encounter_type, et.name from encounter e ");
		query.append("inner join encounter_type et on e.encounter_type = et.encounter_type_id and et.name = '"
				+ encounterType + "'");
		query.append(" and e.patient_id = (select patient_id from patient_identifier where identifier = '"
				+ encounter.getPatientId() + "')");
		// query.append("and e.patient_id= (select patient_id from patient_identifier where identifier='YXAIA-1')");

		Object[][] data = db.getTableData(query.toString());

		// System.out.println(data.length);
		if (data.length > 0)
			return false;

		return true;
	}

	public Map<String, Object> getEncounterObservations(Encounter encounter) {
		Map<String, Object> observations;

		StringBuilder query = new StringBuilder();
		query.append("select q.name as obs, concat(ifnull(a.name, ''), ifnull(o.value_boolean, ''), ifnull(o.value_datetime, ''), ifnull(o.value_text, ''), ifnull(o.value_numeric, '')) as value from obs as o ");
		query.append("left outer join concept_name as q on q.concept_id = o.concept_id and q.locale = 'en' and q.concept_name_type = 'SHORT' and q.voided = 0 ");
		query.append("left outer join concept_name as a on a.concept_id = o.value_coded and a.locale = 'en' and a.locale_preferred = 1 and a.voided = 0 ");
		query.append("where o.voided = 0 and o.encounter_id = "
				+ encounter.getEncounterId());
		// System.out.println(query);
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

	public Location getLocationById(Integer id) {
		// TODO: to be completed
		return null;
	}
	
	public Location getLocationByShortCode(String code) {
		// TODO: to be completed
		return null;
	}
	
	public User getUserById(Integer id) {
		// TODO: to be completed
		return null;
	}

	public String[] getUserRolesByUser(User user) {
		// TODO: to be completed
		return null;
	}
	
	public Patient getPatientByIdentifier(String patientIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	public String checkReferelPresent(Encounter enc) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT encounter_id from encounter ");
		query.append("where encounter_type = '28'  and patient_id = (select patient_id from patient_identifier where identifier = '"
				+ enc.getPatientId() + "') ");
		query.append("order by date_created desc ");
		query.append("limit 1");
		System.out.println(query);
		Object[][] data = db.getTableData(query.toString());
		String encID = "";
		if (data.length > 0)
			for (Object[] row : data) {
				int k = 0;
				try {
					encID = convertToString(row[k++]);

				} catch (Exception ex) {
					log.severe(ex.getMessage());
				}
			}

		return encID;
	}
}
