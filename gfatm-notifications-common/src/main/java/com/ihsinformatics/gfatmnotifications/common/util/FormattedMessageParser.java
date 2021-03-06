/* Copyright(C) 2018 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
*/
package com.ihsinformatics.gfatmnotifications.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ihsinformatics.gfatmnotifications.common.Context;
import com.ihsinformatics.gfatmnotifications.common.model.BaseEntity;
import com.ihsinformatics.gfatmnotifications.common.model.Encounter;
import com.ihsinformatics.gfatmnotifications.common.service.SearchService;
import com.ihsinformatics.util.ClassLoaderUtil;
import com.ihsinformatics.util.CommandType;
import com.ihsinformatics.util.DateTimeUtil;

/**
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class FormattedMessageParser {

	private Decision onNullDecision;

	public FormattedMessageParser(Decision onNull) {
		this.setOnNullDecision(onNull);
	}

	public Decision getOnNullDecision() {
		return onNullDecision;
	}

	public void setOnNullDecision(Decision onNullDecision) {
		this.onNullDecision = onNullDecision;
	}

	public String parseFormattedMessage(String message, Object... objects) throws ParseException {
		if (message == null) {
			throw new ParseException("Parse what? Null!", -1);
		}
		if (!areParenthesesBalanced(message)) {
			throw new ParseException("Parantheses are not balanced in the message.", 0);
		}
		// Tokenize the message
		List<String> tokens = tokenizeMessage(message);
		StringBuilder output = new StringBuilder();
		for (String token : tokens) {
			// Detect the entity.property tokens
			if (isEntityValuePair(token)) {
				String[] pair = token.split("\\.");
				String entityName = pair[0];
				String propertyName = pair[1];
				String values = "";
				String resultPropertyVal = "";
				Object object = null;
				try {
					// Precaution! Turn first character into capital
					entityName = String.valueOf(entityName.charAt(0)).toUpperCase()
							+ entityName.substring(1, entityName.length());
					object = getMatchingClassObject(entityName, objects);
					// Handle Encounter differently
					if (object instanceof Encounter) {
						SearchService searchService = new SearchService(Context.getOpenmrsDb());
						resultPropertyVal = searchService.searchValueFromEncounter((Encounter) object, propertyName);
					} else {
						resultPropertyVal = getPropertyValue(object, propertyName).toString();
					}
				} catch (NullPointerException e) {
					resultPropertyVal = "";
				} catch (ReflectiveOperationException e) {
					e.printStackTrace();
				}
				// Change the date to a more human readable format
				if (resultPropertyVal.matches(DateTimeUtil.SQL_DATE)
						|| resultPropertyVal.matches(DateTimeUtil.SQL_DATETIME)) {
					resultPropertyVal = getReadableDate(DateTimeUtil.fromSqlDateString(resultPropertyVal));
				}
				values = resultPropertyVal;
				output.append(values);
			} else {
				output.append(token);
			}
		}
		// Detect SQL queries and replace result inside the text
		String result = parseSqlQueries(output.toString());
		return result;
	}

	@SuppressWarnings("deprecation")
	/**
	 * Converts date (passed as string) into a more readable string
	 * 
	 * @param resultPropertyVal
	 * @return
	 */
	public String getReadableDate(Date date) {
		String dayName = new DateFormatSymbols().getWeekdays()[new Date().getDay() + 1];
		String dateString = DateTimeUtil.toString(date, DateTimeUtil.STANDARD_DATE_HYPHENATED);
		DaysInUrdu urduName = DaysInUrdu.valueOf(dayName);
		return dateString + ", " + dayName + "(" + urduName + ")";
	}

	/**
	 * This piece of intelligent code detects all SQL queries quoted within $ sign
	 * inside the given parameter text and replaces them with query results. This
	 * method does not throw any exception, if a query fails or retrieves no result,
	 * then a placeholder <MISSING TEXT> is replaced with the query instead
	 * 
	 * @param text
	 * @return
	 */
	public String parseSqlQueries(String text) {
		List<String> queries = new ArrayList<>();
		Pattern p = Pattern.compile("\\$(.*?)\\$");
		Matcher m = p.matcher(text.toString());
		// First, detect all queries
		while (m.find()) {
			queries.add(m.group(1));
		}
		// Set a placeholder for results
		text = m.replaceAll("<RESULT>");
		// Execute queries and replace the first occurrence of placeholder with result
		for (String query : queries) {
			Object result = Context.getOpenmrsDb().runCommand(CommandType.SELECT, query);
			if (result == null) {
				result = "<MISSING TEXT>";
			}
			text = text.replaceFirst("<RESULT>", result.toString());
		}
		return text;
	}

	/**
	 * Searches for class by name and returns the first object which belongs to
	 * found class. Returns null if no object is instance of found class
	 * 
	 * @param className
	 * @param objects
	 * @return
	 * @throws ClassNotFoundException
	 */
	public Object getMatchingClassObject(String className, Object[] objects) throws ClassNotFoundException {
		Class<?> clazz = ClassLoaderUtil.loadClass(className, BaseEntity.class.getPackage().getName(), this.getClass());
		for (Object object : objects) {
			if (clazz.isInstance(object)) {
				return object;
			}
		}
		return null;
	}

	/**
	 * Searches for field or method in given object and returns the corresponding
	 * value of that field in given object
	 * 
	 * @param object
	 * @param fieldOrMethod
	 * @return
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 */
	public Object getPropertyValue(Object object, String fieldOrMethod) throws ReflectiveOperationException, NullPointerException {
		Object value = "";
		try {
			Field field = object.getClass().getDeclaredField(fieldOrMethod);
			boolean accessible = field.isAccessible();
			field.setAccessible(true);
			value = field.get(object);
			field.setAccessible(accessible);
		} catch (NoSuchFieldException e) {
			// In case the field doesn't exist, try to find method
			Class<?> params[] = new Class[0];
			Method method = object.getClass().getDeclaredMethod(fieldOrMethod, params);
			boolean accessible = method.isAccessible();
			method.setAccessible(true);
			value = method.invoke(object);
			method.setAccessible(accessible);
		}
		return value;
	}

	/**
	 * Returns true of the parameter token is in entity.value format (e.g.
	 * patient.lastName)
	 * 
	 * @param token
	 * @return
	 */
	public boolean isEntityValuePair(String token) {
		return token.matches("^[\\w]+\\.[\\w]+$");
	}

	/**
	 * Detect opening and closing parenthesis and tokenize the given string
	 * 
	 * @param string
	 * @return
	 */
	public List<String> tokenizeMessage(String string) {
		List<String> tokens = new ArrayList<>();
		char[] charArray = string.toCharArray();
		StringBuilder token = new StringBuilder();
		for (char c : charArray) {
			if (isTokenizerParenthesis(c)) {
				tokens.add(token.toString());
				token = new StringBuilder();
			} else {
				token.append(c);
			}
		}
		tokens.add(token.toString());
		return tokens;
	}

	private boolean isTokenizerParenthesis(char c) {
		return c == '{' || c == '[' || c == ']' || c == '}';
	}

	/**
	 * This function checks whether the number of parentheses are balanced (equally
	 * opened and closed) in given message or not
	 * 
	 * @param message
	 * @return
	 */
	public boolean areParenthesesBalanced(String message) {
		Map<Character, Character> parentheses = new HashMap<>();
		parentheses.put('[', ']');
		parentheses.put('{', '}');
		parentheses.put('(', ')');
		// Leave nothing except parentheses in the message
		String clean = message.replaceAll("[^\\Q{[()]}\\E]", "");
		// Odd number would always result in false
		if ((clean.length() % 2) != 0) {
			return false;
		}
		Stack<Character> stack = new Stack<>();
		for (int i = 0; i < clean.length(); i++) {
			if (parentheses.containsKey(clean.charAt(i))) {
				stack.push(clean.charAt(i));
			} else if (stack.empty() || (clean.charAt(i) != parentheses.get(stack.pop()))) {
				return false;
			}
		}
		return true;
	}
}
