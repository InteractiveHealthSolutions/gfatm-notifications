/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications.model;

import java.util.Map;

import org.joda.time.DateTime;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class Encounter {
	Integer encounterId;
	String encounterType;
	DateTime encounterDate;
	String patientId;
	String patientName;
	String patientContact;
	String location;
	String locationContact;
	String provider;
	String providerContact;
	String username;
	DateTime dateCreated;
	String uuid;
	Map<String, Object> observations;

	public Encounter() {
	}

	public Encounter(Integer encounterId, String encounterType,
			DateTime encounterDate, String patientId, String provider,
			String location, String uuid) {
		super();
		this.encounterId = encounterId;
		this.encounterType = encounterType;
		this.encounterDate = encounterDate;
		this.patientId = patientId;
		this.provider = provider;
		this.location = location;
		this.uuid = uuid;
	}

	public Integer getEncounterId() {
		return encounterId;
	}

	public void setEncounterId(Integer encounterId) {
		this.encounterId = encounterId;
	}

	public String getEncounterType() {
		return encounterType;
	}

	public void setEncounterType(String encounterType) {
		this.encounterType = encounterType;
	}

	public DateTime getEncounterDate() {
		return encounterDate;
	}

	public void setEncounterDate(DateTime encounterDate) {
		this.encounterDate = encounterDate;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientContact() {
		return patientContact;
	}

	public void setPatientContact(String patientContact) {
		this.patientContact = patientContact;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocationContact() {
		return locationContact;
	}

	public void setLocationContact(String locationContact) {
		this.locationContact = locationContact;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProviderContact() {
		return providerContact;
	}

	public void setProviderContact(String providerContact) {
		this.providerContact = providerContact;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public DateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Map<String, Object> getObservations() {
		return observations;
	}

	public void setObservations(Map<String, Object> observations) {
		this.observations = observations;
	}
}