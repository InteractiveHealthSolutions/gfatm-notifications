/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications.model;


/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class Location {

	Integer locationId;
	String name;
	Integer parentId;
	String uuid;
	Boolean fast;
	Boolean pet;
	Boolean childhoodTb;
	Boolean comorbidities;
	Boolean pmdt;
	Boolean aic;
	String primaryContact;
	String address1;
	String address2;
	String address3;
	String cityVillage;
	String stateProvince;
	String description;

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Boolean getFast() {
		return fast;
	}

	public void setFast(Boolean fast) {
		this.fast = fast;
	}

	public Boolean getPet() {
		return pet;
	}

	public void setPet(Boolean pet) {
		this.pet = pet;
	}

	public Boolean getChildhoodTb() {
		return childhoodTb;
	}

	public void setChildhoodTb(Boolean childhoodTb) {
		this.childhoodTb = childhoodTb;
	}

	public Boolean getComorbidities() {
		return comorbidities;
	}

	public void setComorbidities(Boolean comorbidities) {
		this.comorbidities = comorbidities;
	}

	public Boolean getPmdt() {
		return pmdt;
	}

	public void setPmdt(Boolean pmdt) {
		this.pmdt = pmdt;
	}

	public Boolean getAic() {
		return aic;
	}

	public void setAic(Boolean aic) {
		this.aic = aic;
	}

	public String getPrimaryContact() {
		return primaryContact;
	}

	public void setPrimaryContact(String primaryContact) {
		this.primaryContact = primaryContact;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getCityVillage() {
		return cityVillage;
	}

	public void setCityVillage(String cityVillage) {
		this.cityVillage = cityVillage;
	}

	public String getStateProvince() {
		return stateProvince;
	}

	public void setStateProvince(String stateProvince) {
		this.stateProvince = stateProvince;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
