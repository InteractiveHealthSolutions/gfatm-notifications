/* Copyright(C) 2017 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

package com.ihsinformatics.gfatmnotifications.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.InvalidPropertiesFormatException;
import java.util.MissingFormatArgumentException;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.json.JSONObject;

import com.ihsinformatics.gfatmnotifications.common.Context;
import com.ihsinformatics.gfatmnotifications.common.model.BaseEntity;
import com.ihsinformatics.gfatmnotifications.common.model.Encounter;
import com.ihsinformatics.gfatmnotifications.common.model.Location;
import com.ihsinformatics.gfatmnotifications.common.model.Observation;
import com.ihsinformatics.gfatmnotifications.common.model.Patient;
import com.ihsinformatics.gfatmnotifications.common.model.Rule;
import com.ihsinformatics.util.DatabaseUtil;
import com.ihsinformatics.util.JsonUtil;
import com.ihsinformatics.util.RegexUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class ValidationUtil {

	private static final Logger log = Logger.getLogger(Class.class.getName());
	public static final String PATIENT_ID_REGEX = "[0-9A-Za-z]{5}\\-[0-9]";
	public static final String LOCATION_ID_REGEX = "[A-Z\\-]+";
	public static final String USERNAME_REGEX = "[a-z]+\\.[a-z]+";
	public static final String VALIDATE_STRING = "validate";
	public static final String ENTITY_STRING = "entity";
	public static final String PROPERTY_STRING = "property";

	public static final String VALUE_STRING = "VALUE";
	public static final String NOTEQUALS_STRING = "NOTEQUALS";
	public static final String RANGE_STRING = "RANGE";
	public static final String REGEX_STRING = "REGEX";
	public static final String QUERY_STRING = "QUERY";
	public static final String LIST_STRING = "LIST";
	public static final String NOTNULL_STRING = "NOTNULL";
	public static final String PRESENT_STRING = "PRESENT";
	public static final String EXISTS_STRING = "EXISTS";

	private ValidationUtil() {
	}

	/**
	 * Checks whether given patient ID matches with the ID scheme or not.
	 * 
	 * @param patientId
	 * @return
	 */
	public static boolean isValidPatientId(String patientId) {
		return patientId.matches(PATIENT_ID_REGEX);
	}

	/**
	 * Checks whether given location ID matches with the ID scheme or not.
	 * 
	 * @param locationId
	 * @return
	 */
	public static boolean isValidLocationId(String locationId) {
		return locationId.matches(LOCATION_ID_REGEX);
	}

	/**
	 * Checks whether given location ID matches with the ID scheme or not.
	 * 
	 * @param username
	 * @return
	 */
	public static boolean isValidUsername(String username) {
		return username.matches(USERNAME_REGEX);
	}

	/**
	 * Checks whether given email address is valid or not.
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isValidEmailAddress(String email) {
		return RegexUtil.isEmailAddress(email);
	}

	/**
	 * Checks whether given contact number is legitimate or not. Invalid numbers
	 * contain same digits or digits in increasing or decreasing order
	 *
	 * @param number
	 * @return
	 */
	public static boolean isValidContactNumber(String number) {
		if (!RegexUtil.isContactNumber(number)) {
			return false;
		}
		if (number.length() < 9) {
			return false;
		}
		char[] array = number.toCharArray();
		int occurrances = 0;
		// Similarity check
		for (int i = 1; i < array.length; i++) {
			if (array[i] == array[i - 1]) {
				occurrances++;
			} else {
				occurrances = 0;
			}
			if (occurrances >= 5) {
				return false;
			}
		}
		// Series check
		for (int i = 1; i < array.length; i++) {
			if (Math.abs(array[i] - array[i - 1]) == 1) {
				occurrances++;
			} else {
				occurrances = 0;
			}
			if (occurrances >= 5) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Validates a value against given regular expression
	 * 
	 * @param regex
	 * @param value
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static boolean validateRegex(String regex, String value) throws PatternSyntaxException {
		try {
			Pattern.compile(regex);
		} catch (Exception e) {
			throw new PatternSyntaxException("Invalid regular expression provided for validation.", regex, -1);
		}
		return value.matches(regex);
	}

	/**
	 * Validates a value against given range (as a string)
	 * 
	 * @param range
	 * @param value
	 * @return
	 * @throws InvalidPropertiesFormatException
	 */
	public static boolean validateRange(String range, Double value) throws InvalidPropertiesFormatException {
		boolean valid = false;
		if (!range.matches("^[0-9.,-]+")) {
			throw new InvalidPropertiesFormatException(
					"Invalid format provided for validation range. Must be a list of hyphenated or comma-separated tuples of numbers (1-10; 2.2-3.0; 1,3,5; 1-5,7,9).");
		}
		// Break into tuples
		String[] tuples = range.split(",");
		for (String tuple : tuples) {
			if (tuple.contains("-")) {
				String[] parts = tuple.split("-");
				double min = Double.parseDouble(parts[0]);
				double max = Double.parseDouble(parts[1]);
				valid = (value >= min && value <= max);
			} else {
				valid = (Double.compare(value.doubleValue(), Double.parseDouble(tuple)) == 0);
			}
			if (valid) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Validates a value against given list of comma-separated values
	 * 
	 * @param list
	 * @param value
	 * @return
	 * @throws InvalidPropertiesFormatException
	 */
	public static boolean validateList(String list, String value) throws InvalidPropertiesFormatException {
		if (!list.matches("^[A-Za-z0-9,_\\-\\s]+")) {
			throw new InvalidPropertiesFormatException(
					"Invalid format provided for validation list. Must be a comma-separated list of alpha-numeric values (white space, hypen and underscore allowed).");
		}
		String[] values = list.split(",");
		for (int i = 0; i < values.length; i++) {
			if (value.trim().equalsIgnoreCase(values[i].trim()))
				return true;
		}
		return false;
	}

	/**
	 * Validates a value against given query
	 * 
	 * @param query
	 * @param value
	 * @return
	 * @throws SQLException
	 */
	public static boolean validateQuery(String query, String value) throws SQLException {
		Object[][] data = Context.getOpenmrsDb().getTableData(query);
		for (Object[] row : data) {
			for (Object obj : row) {
				if (Objects.equals(obj, value)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * MOAV (mother of all validations). This method first checks if the input value
	 * is of give dataType (String, Double, etc.), then matches regex. The regex
	 * must be in format: LHS=RHS. If LHS is "REGEX", then RHS is expected to be a
	 * valid regular expression to match value with; If LHS is "LIST", then RHS
	 * should be a comma-separated list of strings to lookup value in; If LHS is
	 * "RANGE", then RHS should be a set of range parts, like
	 * 1-10,2.2,3.2,5.5,17.1-18.9, etc. in which, the value will be checked; If LHS
	 * is "QUERY", then RHS is expected to be a SQL to lookup the value in database
	 * 
	 * @param regex
	 * @param dataType
	 * @param value
	 * @return
	 * @throws InvalidPropertiesFormatException
	 * @throws PatternSyntaxException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static boolean validateData(String regex, String dataType, String value)
			throws InvalidPropertiesFormatException, SQLException {
		boolean isValidDataType = false;
		boolean isValidValue = false;
		dataType = dataType.toLowerCase();
		DataType dataTypeEnum = DataType.getDataTypeByAlias(dataType);
		// Validate according to given data type
		switch (dataTypeEnum) {
		case BOOLEAN:
			isValidDataType = value.matches("Y|N|y|n|true|false|True|False|TRUE|FALSE|0|1");
			break;
		case CHARACTER:
			isValidDataType = value.length() == 1;
			break;
		case DATE:
			isValidDataType = value.matches(RegexUtil.SQL_DATE);
			break;
		case DATETIME:
			isValidDataType = value.matches(RegexUtil.SQL_DATETIME);
			break;
		case FLOAT:
			isValidDataType = value.matches(RegexUtil.DECIMAL);
			break;
		case INTEGER:
			isValidDataType = value.matches(RegexUtil.INTEGER);
			break;
		case STRING:
			isValidDataType = true;
			break;
		case TIME:
			isValidDataType = value.matches(RegexUtil.SQL_TIME);
			break;
		default:
			break;
		}
		// Check if validation regex is provided
		if (regex == null) {
			isValidValue = true;
		} else {
			String[] parts = regex.split("=");
			if (parts.length != 2) {
				throw new InvalidPropertiesFormatException(
						"Invalid value provided for validation regex. Must be in format LHS=RHS");
			}
			String type = parts[0];
			String validatorStr = parts[1];
			// Validate regular expression
			if (type.equalsIgnoreCase(REGEX_STRING)) {
				isValidValue = validateRegex(validatorStr, value);
			}
			// Validate range
			else if (type.equalsIgnoreCase(RANGE_STRING)) {
				try {
					double num = Double.parseDouble(value);
					isValidValue = validateRange(validatorStr, num);
				} catch (NumberFormatException e) {
					isValidValue = false;
				}
			}
			// Validate comma-separated list
			else if (type.equalsIgnoreCase(LIST_STRING)) {
				isValidValue = validateList(validatorStr, value);
			}
			// Validate using query
			else if (type.equalsIgnoreCase(QUERY_STRING)) {
				isValidValue = validateQuery(validatorStr, value);
			}
			// Validate matching single value
			if (type.equalsIgnoreCase(VALUE_STRING)) {
				isValidValue = validatorStr.equalsIgnoreCase(value);
			}
		}
		return (isValidDataType && isValidValue);
	}

	public static boolean validateRule(Rule rule, Patient patient, Location location, Encounter encounter,
			DatabaseUtil dbUtil) {
		return validateConditions(rule.getConditions(), patient, location, encounter, dbUtil)
				&& validateConditions(rule.getStopConditions(), patient, location, encounter, dbUtil);
	}

	/**
	 * Validates all conditions
	 * 
	 * @param patient
	 * @param location
	 * @param encounter
	 * @param conditions
	 * @param dbUtil
	 * @return
	 */
	public static boolean validateConditions(String conditions, Patient patient, Location location, Encounter encounter,
			DatabaseUtil dbUtil) {
		if ("".equals(conditions)) {
			return true;
		}
		String orPattern = "(.)+OR(.)+";
		String andPattern = "(.)+AND(.)+";
		if (conditions.matches(orPattern) && conditions.matches(andPattern)) {
			String[] orConditions = conditions.split("( )?OR( )?");
			for (String condition : orConditions) {
				// No need to proceed even if one condition is true
				if (condition.matches(andPattern)) {
					String[] andConditions = condition.split("( )?AND( )?");
					for (String nestedCondition : andConditions) {
						// No need to proceed even if one condition is false
						if (!validateSingleCondition(nestedCondition, patient, location, encounter, dbUtil)) {
							return false;
						}
					}
					return true;
				} else {
					return validateSingleCondition(condition, patient, location, encounter, dbUtil);
				}
			}
		}
		if (conditions.matches(orPattern)) {
			String[] orConditions = conditions.split("( )?OR( )?");
			for (String condition : orConditions) {
				// No need to proceed even if one condition is true

				if (validateSingleCondition(condition, patient, location, encounter, dbUtil)) {
					return true;
				}
			}
		} else if (conditions.matches(andPattern)) {
			String[] andConditions = conditions.split("( )?AND( )?");
			for (String condition : andConditions) {
				// No need to proceed even if one condition is false
				if (!validateSingleCondition(condition, patient, location, encounter, dbUtil)) {
					return false;
				}
			}
			return true;
		} else {
			return validateSingleCondition(conditions, patient, location, encounter, dbUtil);
		}
		return false;
	}

	/**
	 * This method validates a single condition token
	 * 
	 * @param condition
	 * @param patient
	 * @param location
	 * @param encounter
	 * @param dbUtil
	 * @return
	 */
	public static boolean validateSingleCondition(String condition, Patient patient, Location location,
			Encounter encounter, DatabaseUtil dbUtil) {
		JSONObject jsonObject = JsonUtil.getJSONObject(condition);
		// Trigger encounter cannot be NULL or invalid
		String triggerEncounterName = jsonObject.getString("encounter");
		if (triggerEncounterName == null) {
			throw new MissingFormatArgumentException(
					"Exact name of a trigger encounter must be provided in Encounter column for condition: "
							+ condition);
		}
		// Search in metadata
		if (!Context.getEncounterTypes().containsValue(triggerEncounterName)) {
			throw new MissingFormatArgumentException(
					"The encounter name provided as trigger does not match with any Encounter Type in metadata in condition: "
							+ condition);
		}
		// Prerequisites must be checked
		if (!(jsonObject.has(ENTITY_STRING) && jsonObject.has(PROPERTY_STRING) && jsonObject.has(VALIDATE_STRING))) {
			throw new MissingFormatArgumentException(
					"Condition must contain all required keys: entity, property and validate.");
		}
		// Clear to proceed
		String entity = jsonObject.getString(ENTITY_STRING);
		String validationType = jsonObject.getString(VALIDATE_STRING);
		String property = jsonObject.getString(PROPERTY_STRING);
		String expectedValue = jsonObject.getString(VALUE_STRING);
		String actualValue = null;
// {entity:encounter,encounter:Referral and Transfer,property:referral_site,validate:VALUE,value:OTHER}
		// In case of Encounter, search through observations
		if (entity.equalsIgnoreCase("encounter")) {
			if (encounter.getObservations() == null) {
				return false;
			}
			for (Observation obs : encounter.getObservations()) {
				// Search for the observation's concept name matching the variable name
				if (variableMatchesWithConcept(property, obs)) {
					if (validationType.equalsIgnoreCase(LIST_STRING)) {
						actualValue = obs.getValueCoded().toString();
					} else {
						actualValue = obs.getValue().toString();
					}
					try {
						return validateValue(validationType, expectedValue, actualValue);
					} catch (InvalidPropertiesFormatException | SQLException e) {
						log.warning(e.getMessage());
						return false;
					}
				}
			}
		}
		try {
			// In case of Patient or Location, search for the defined property
			if (entity.equals("Patient")) {
				actualValue = getEntityPropertyValue(patient, property);
			} else if (entity.equals("Location")) {
				actualValue = getEntityPropertyValue(location, property);
			}
			return validateValue(validationType, expectedValue, actualValue);
		} catch (Exception e) {
			log.warning(e.getMessage());
		}
		return false;
	}

	/**
	 * This method checks the type of validation and validates the value passed
	 * 
	 * @param validationType
	 * @param expectedValue
	 * @param actualValue
	 * @return
	 * @throws InvalidPropertiesFormatException
	 * @throws SQLException
	 */
	private static boolean validateValue(String validationType, String expectedValue, String actualValue)
			throws InvalidPropertiesFormatException, SQLException {
		switch (validationType) {
		case VALUE_STRING:
			return actualValue.equalsIgnoreCase(expectedValue);
		case NOTEQUALS_STRING:
			return !actualValue.equalsIgnoreCase(expectedValue);
		case RANGE_STRING:
			Double valueDouble = Double.parseDouble(actualValue);
			return ValidationUtil.validateRange(expectedValue, valueDouble);
		case REGEX_STRING:
			return ValidationUtil.validateRegex(expectedValue, actualValue);
		case QUERY_STRING:
			return ValidationUtil.validateQuery(expectedValue, actualValue);
		case LIST_STRING:
			return ValidationUtil.validateList(expectedValue, actualValue);
		case NOTNULL_STRING:
		case PRESENT_STRING:
		case EXISTS_STRING:
			return actualValue != null;
		default:
			return false;
		}
	}

	/**
	 * Checks the entity type of the object and looks for the value in given
	 * property using Reflection
	 * 
	 * @param object
	 * @param property could be a field name or a method name (strictly camel case)
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	public static String getEntityPropertyValue(Object object, String property)
			throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		if (!(object instanceof BaseEntity)) {
			throw new NoSuchFieldException("The object is not an instance of BaseEntity");
		}
		String actualValue;
		// Is it a field or method?
		if (property.matches("get[A-Z](.)+")) {
			Method method = object.getClass().getDeclaredMethod(property, new Class[] {});
			boolean flag = method.isAccessible();
			method.setAccessible(true);
			Object objectReturned = method.invoke(object, new Object[] {});
			actualValue = objectReturned == null ? null : objectReturned.toString();
			method.setAccessible(flag);
		} else {
			Field field = object.getClass().getDeclaredField(property);
			boolean flag = field.isAccessible();
			field.setAccessible(true);
			actualValue = field.get(object).toString();
			field.setAccessible(flag);
		}
		return actualValue;
	}

	/**
	 * This function only checks whether the variable is an Integer ID, or a concept
	 * name and matches with the concept in the observation
	 * 
	 * @param variable
	 * @param observation
	 * @return
	 */
	public static boolean variableMatchesWithConcept(String variable, Observation observation) {
		// Check if the variable is a concept ID
		if (RegexUtil.isNumeric(variable, false)) {
			return observation.getConceptId().equals(Integer.parseInt(variable));
		} else if (observation.getConceptName() != null && observation.getConceptName().equalsIgnoreCase(variable)) {
			return true;
		} else if (observation.getConceptShortName() != null
				&& observation.getConceptShortName().equalsIgnoreCase(variable)) {
			return true;
		}
		return false;
	}
}
