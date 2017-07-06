/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
*/

package com.ihsinformatics.gfatmnotifications.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class MetadataUtil {

	public static List<EncounterType> getEncounterTypes() {
		Gson gson = new Gson();
		JsonReader reader;
		try {
			reader = new JsonReader(new FileReader("res/encounter_type.json"));
			List<EncounterType> encounterTypes = gson.fromJson(reader, List.class);
			return encounterTypes;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<Location> getLocations() {
		Gson gson = new Gson();
		JsonReader reader;
		try {
			reader = new JsonReader(new FileReader("res/locations.json"));
			List<Location> locations = gson.fromJson(reader, List.class);
			return locations;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

}
