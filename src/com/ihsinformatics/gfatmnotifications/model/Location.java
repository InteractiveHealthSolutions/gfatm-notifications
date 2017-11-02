/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class Location implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4675815525012531145L;
	Integer locationId;
	String name;
	Integer parentId;
	String uuid;
	Boolean fast;
	Boolean pet;
	Boolean childhoodTb;
	Boolean comorbidities;
	Boolean pmdt;
	String primaryContact;
	String primaryContactName;
	String secondaryContact;
	String secondaryContactName;
	String locationType;
	String address1;
	String address2;
	String address3;
	String cityVillage;
	String stateProvince;
	String description;
	private Date dateCreated;
	Boolean status;

	/**
	 * @return the locationId
	 */
	public Integer getLocationId() {
		return locationId;
	}

	/**
	 * @param locationId
	 *            the locationId to set
	 */
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the parentId
	 */
	public Integer getParentId() {
		return parentId;
	}

	/**
	 * @param parentId
	 *            the parentId to set
	 */
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid
	 *            the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the fast
	 */
	public Boolean getFast() {
		return fast;
	}

	/**
	 * @param fast
	 *            the fast to set
	 */
	public void setFast(Boolean fast) {
		this.fast = fast;
	}

	/**
	 * @return the pet
	 */
	public Boolean getPet() {
		return pet;
	}

	/**
	 * @param pet
	 *            the pet to set
	 */
	public void setPet(Boolean pet) {
		this.pet = pet;
	}

	/**
	 * @return the childhoodTb
	 */
	public Boolean getChildhoodTb() {
		return childhoodTb;
	}

	/**
	 * @param childhoodTb
	 *            the childhoodTb to set
	 */
	public void setChildhoodTb(Boolean childhoodTb) {
		this.childhoodTb = childhoodTb;
	}

	/**
	 * @return the comorbidities
	 */
	public Boolean getComorbidities() {
		return comorbidities;
	}

	/**
	 * @param comorbidities
	 *            the comorbidities to set
	 */
	public void setComorbidities(Boolean comorbidities) {
		this.comorbidities = comorbidities;
	}

	/**
	 * @return the pmdt
	 */
	public Boolean getPmdt() {
		return pmdt;
	}

	/**
	 * @param pmdt
	 *            the pmdt to set
	 */
	public void setPmdt(Boolean pmdt) {
		this.pmdt = pmdt;
	}

	/**
	 * @return the primaryContact
	 */
	public String getPrimaryContact() {
		return primaryContact;
	}

	/**
	 * @param primaryContact
	 *            the primaryContact to set
	 */
	public void setPrimaryContact(String primaryContact) {
		this.primaryContact = primaryContact;
	}

	/**
	 * @return the primaryContactName
	 */
	public String getPrimaryContactName() {
		return primaryContactName;
	}

	/**
	 * @param primaryContactName
	 *            the primaryContactName to set
	 */
	public void setPrimaryContactName(String primaryContactName) {
		this.primaryContactName = primaryContactName;
	}

	/**
	 * @return the secondaryContact
	 */
	public String getSecondaryContact() {
		return secondaryContact;
	}

	/**
	 * @param secondaryContact
	 *            the secondaryContact to set
	 */
	public void setSecondaryContact(String secondaryContact) {
		this.secondaryContact = secondaryContact;
	}

	/**
	 * @return the secondaryContactName
	 */
	public String getSecondaryContactName() {
		return secondaryContactName;
	}

	/**
	 * @param secondaryContactName
	 *            the secondaryContactName to set
	 */
	public void setSecondaryContactName(String secondaryContactName) {
		this.secondaryContactName = secondaryContactName;
	}

	/**
	 * @return the locationType
	 */
	public String getLocationType() {
		return locationType;
	}

	/**
	 * @param locationType
	 *            the locationType to set
	 */
	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	/**
	 * @return the address1
	 */
	public String getAddress1() {
		return address1;
	}

	/**
	 * @param address1
	 *            the address1 to set
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	/**
	 * @return the address2
	 */
	public String getAddress2() {
		return address2;
	}

	/**
	 * @param address2
	 *            the address2 to set
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	/**
	 * @return the address3
	 */
	public String getAddress3() {
		return address3;
	}

	/**
	 * @param address3
	 *            the address3 to set
	 */
	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	/**
	 * @return the cityVillage
	 */
	public String getCityVillage() {
		return cityVillage;
	}

	/**
	 * @param cityVillage
	 *            the cityVillage to set
	 */
	public void setCityVillage(String cityVillage) {
		this.cityVillage = cityVillage;
	}

	/**
	 * @return the stateProvince
	 */
	public String getStateProvince() {
		return stateProvince;
	}

	/**
	 * @param stateProvince
	 *            the stateProvince to set
	 */
	public void setStateProvince(String stateProvince) {
		this.stateProvince = stateProvince;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the dateCreated
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated
	 *            the dateCreated to set
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * @return the status
	 */
	public Boolean getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Boolean status) {
		this.status = status;
	}
}
