/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications;

import java.text.ParseException;
import java.util.List;
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
		filter.append("where e.date_created between ");
		filter.append("timestamp('" + sqlFrom + "')");
		filter.append(" and ");
		filter.append("timestamp('" + sqlTo + "')");
		filter.append(" and ");
		filter.append("timestampdiff(HOUR, e.date_created, e.encounter_datetime) <= 48");
		if (type != null) {
			filter.append(" and e.encounter_type=" + type);
		}
		StringBuilder query = new StringBuilder();
		query.append("select e.encounter_id, et.name as encounter_type, pi.identifier, concat(pn.given_name, ' ', pn.family_name) as patient_name, e.encounter_datetime, l.name as encounter_location, pc.value as patient_contact, la.value_reference as location_contact, pr.identifier as provider, upc.value as provider_contact, u.username, e.date_created, e.uuid from encounter as e ");
		query.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		query.append("inner join patient as p on p.patient_id = e.patient_id ");
		query.append("inner join patient_identifier as pi on pi.patient_id = p.patient_id and pi.identifier_type = 3 ");
		query.append("inner join person_name as pn on pn.person_id = p.patient_id ");
		query.append("left outer join person_attribute as pc on pc.person_id = p.patient_id and pc.person_attribute_type_id = 8 ");
		query.append("left outer join location as l on l.location_id = e.location_id ");
		query.append("left outer join location_attribute as la on la.location_id = l.location_id and la.attribute_type_id = 2 ");
		query.append("left outer join encounter_provider as ep on ep.encounter_id = e.encounter_id ");
		query.append("left outer join provider as pr on pr.provider_id = ep.encounter_id ");
		query.append("left outer join users as u on u.system_id = pr.identifier ");
		query.append("where e.voided = false");
		Object[][] data = db.getTableData(query.toString());
		for (Object[] row : data) {
			int k = 0;
			try {
				int encounterId = Integer.parseInt(row[k++].toString());
				String encounterType = row[k++].toString();
				String patientId = row[k++].toString();
				String patientName = row[k++].toString();
				DateTime encounterDate = new DateTime(DateTimeUtil.getDateFromString(row[k++].toString(), DateTimeUtil.SQL_DATETIME));
				String locationName = row[k++].toString();
				String patientContact = row[k++].toString();
				String locationContact = row[k++].toString();
				String providerId = row[k++].toString();
				String providerContact = row[k++].toString();
				String user = row[k++].toString();
				DateTime dateCreated = new DateTime(DateTimeUtil.getDateFromString(row[k++].toString(), DateTimeUtil.SQL_DATETIME));
				String uuid = row[k++].toString();

				Encounter encounter = new Encounter(encounterId, encounterType, encounterDate, patientId, providerId, locationName, uuid); 
				
			} catch (ParseException ex) {
				log.severe(ex.getMessage());
			}
		}
		return null;
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

}
