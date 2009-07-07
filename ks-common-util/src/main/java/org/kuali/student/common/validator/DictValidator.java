package org.kuali.student.common.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import org.kuali.student.common.util.MessageUtils;
import org.kuali.student.core.dictionary.newmodel.dto.CaseConstraint;
import org.kuali.student.core.dictionary.newmodel.dto.ConstraintDescriptor;
import org.kuali.student.core.dictionary.newmodel.dto.ConstraintSelector;
import org.kuali.student.core.dictionary.newmodel.dto.Field;
import org.kuali.student.core.dictionary.newmodel.dto.LookupConstraint;
import org.kuali.student.core.dictionary.newmodel.dto.ObjectStructure;
import org.kuali.student.core.dictionary.newmodel.dto.OccursConstraint;
import org.kuali.student.core.dictionary.newmodel.dto.RequireConstraint;
import org.kuali.student.core.dictionary.newmodel.dto.State;
import org.kuali.student.core.dictionary.newmodel.dto.Type;
import org.kuali.student.core.dictionary.newmodel.dto.TypeStateCaseConstraint;
import org.kuali.student.core.dictionary.newmodel.dto.ValidCharsConstraint;
import org.kuali.student.core.dictionary.newmodel.dto.WhenConstraint;
import org.kuali.student.core.messages.dto.Message;
import org.kuali.student.core.validation.dto.DictValidationResultContainer;
import org.springframework.util.StringUtils;

public class DictValidator {

	private static final String UNBOUNDED_CHECK = "unbounded";

	private Map<String, String> messages = new HashMap<String, String>();

	private Stack<String> elementStack = new Stack<String>();

	private DateParser dateParser = null;

	private ConstraintSetupFactory setupFactory = null;

	private boolean serverSide = true;

	public DictValidator(ConstraintSetupFactory setupFactory, boolean serverSide) {
		this.setupFactory = setupFactory;
		this.serverSide = serverSide;
	}

	/**
	 * @return the serverSide
	 */
	public boolean isServerSide() {
		return serverSide;
	}

	/**
	 * @param serverSide
	 *            the serverSide to set
	 */
	public void setServerSide(boolean serverSide) {
		this.serverSide = serverSide;
	}

	/**
	 * @return the dateParser
	 */
	public DateParser getDateParser() {
		return dateParser;
	}

	/**
	 * @param dateParser
	 *            the dateParser to set
	 */
	public void setDateParser(DateParser dateParser) {
		this.dateParser = dateParser;
	}

	public void addMessages(List<Message> list) {
		for (Message m : list) {
			messages.put(m.getId(), m.getValue());
		}
	}

	/**
	 * Validate Object and all its nested child objects for given type and state
	 * 
	 * @param data
	 * @param objStructure
	 * @return
	 */
	public List<DictValidationResultContainer> validateTypeStateObject(
			Object data, ObjectStructure objStructure) {
		List<DictValidationResultContainer> results = new ArrayList<DictValidationResultContainer>();

		ConstraintDataProvider dataProvider = setupFactory
				.getDataProvider(data);

		boolean isTypeStateObject = (dataProvider.hasField("type") && dataProvider
				.hasField("state"));

		// Push object structure to the top of the stack
		StringBuilder objXPathElement = new StringBuilder(objStructure.getKey());
		if (null != dataProvider.getObjectId()) {
			objXPathElement.append("[id='" + dataProvider.getObjectId() + "']");
		}
		elementStack.push(objXPathElement.toString());

		// We are making the assumption that all objects being validated has
		// Type and State
		if (!isTypeStateObject) {
			throw new IllegalArgumentException(
					"Non TypeState object being validated:"
							+ dataProvider.getObjectId());
		}

		// Validate with the matching Type/State
		List<Type> types = objStructure.getType();
		for (Type t : types) {
			if (t.getKey().equalsIgnoreCase(
					(String) dataProvider.getValue("type"))) {
				for (State s : t.getState()) {
					if (s.getKey().equalsIgnoreCase(
							(String) dataProvider.getValue("state"))) {
						for (Field f : s.getField()) {
							results.addAll(validateField(f, t, s, objStructure,
									dataProvider));
						}
						break;
					}
				}
				break;
			}
		}

		elementStack.pop();

		return results;
	}

	public List<DictValidationResultContainer> validateField(Field field,
			Type type, State state, ObjectStructure objStruct,
			ConstraintDataProvider dataProvider) {

		Object value = dataProvider.getValue(field.getKey());
		List<DictValidationResultContainer> results = new ArrayList<DictValidationResultContainer>();

		// Check to see if the Field is not a complex type
		if ("complex"
				.equalsIgnoreCase(field.getFieldDescriptor().getDataType())) {
			ObjectStructure nestedObjStruct = null;
			if (StringUtils.hasText(field.getFieldDescriptor()
					.getObjectStructureRef())) {
				nestedObjStruct = setupFactory.getObjectStructure(field
						.getFieldDescriptor().getObjectStructureRef());
			} else {
				nestedObjStruct = field.getFieldDescriptor()
						.getObjectStructure();
			}

			if (value instanceof Collection) {
				for (Object o : (Collection<?>) value) {
					processNestedObjectStructure(results, o, nestedObjStruct,
							field);
				}
			} else {
				processNestedObjectStructure(results, value, nestedObjStruct,
						field);
			}
		} else { // If non complex data type
			ConstraintDescriptor cd = field.getConstraintDescriptor();
			if (null != cd) {

				List<ConstraintSelector> constraints = cd.getConstraint();

				if (value instanceof Collection) {

					// TODO: Right now bcb is computed for each object. Change
					// this so that it is only computed
					// for the first object
					BaseConstraintBean bcb = new BaseConstraintBean();
					for (Object o : (Collection<?>) value) {
						String xPath = getElementXpath() + field.getKey()
								+ "[value='" + o.toString() + "']/";
						DictValidationResultContainer valResults = new DictValidationResultContainer(
								xPath);

						for (ConstraintSelector constraint : constraints) {
							processConstraint(valResults, constraint, field,
									type, state, objStruct, o, dataProvider,
									bcb);
						}
						processBaseConstraints(valResults, bcb, field, o);

						if (bcb.minOccurs > ((Collection<?>) value).size()) {
							valResults.addError(messages
									.get("validation.minOccurs"));
						}

						if (!UNBOUNDED_CHECK.equalsIgnoreCase(bcb.maxOccurs)
								&& Integer.parseInt(bcb.maxOccurs) < ((Collection<?>) value)
										.size()) {
							valResults.addError(messages
									.get("validation.maxOccurs"));
						}

						results.add(valResults);
					}
				} else {
					DictValidationResultContainer valResults = new DictValidationResultContainer(
							getElementXpath() + field.getKey() + "/");

					BaseConstraintBean bcb = new BaseConstraintBean();
					for (ConstraintSelector constraint : constraints) {
						processConstraint(valResults, constraint, field, type,
								state, objStruct, value, dataProvider, bcb);
					}
					processBaseConstraints(valResults, bcb, field, value);

					results.add(valResults);
				}
			}
		}
		return results;
	}

	private void processNestedObjectStructure(
			List<DictValidationResultContainer> results, Object value,
			ObjectStructure nestedObjStruct, Field field) {
		results.addAll(validateTypeStateObject(value, nestedObjStruct));

		// CD should have only one type state case constraint
		ConstraintDescriptor cd = field.getConstraintDescriptor();
		if (null != cd) {
			ConstraintSelector cs = cd.getConstraint().get(0);
			TypeStateCaseConstraint tscs = cs.getTypeStateCaseConstraint();
			if (null != tscs) {
				// processTypeStateCaseConstraint(valResults);
			}
		}
	}

	private void processConstraint(DictValidationResultContainer valResults,
			ConstraintSelector constraint, Field field, Type type, State state,
			ObjectStructure objStructure, Object value,
			ConstraintDataProvider dataProvider, BaseConstraintBean bcb) {

		// If constraint is only to be processed on server side
		if (StringUtils.hasText(constraint.getClassName())
				|| constraint.isServerSide() && !serverSide) {
			return;
		}

		if (null != constraint.getMinLength()) {
			bcb.minLength = (bcb.minLength > constraint.getMinLength()) ? bcb.minLength
					: constraint.getMinLength();
		}

		if (null != constraint.getMinOccurs()) {
			bcb.minOccurs = (bcb.minOccurs > constraint.getMinOccurs()) ? bcb.minOccurs
					: constraint.getMinOccurs();
		}

		if (null != constraint.getMinValue()) {
			bcb.minValue = (null == bcb.minValue || DictValidatorUtils
					.compareValues(bcb.minValue, constraint.getMinValue(),
							field.getFieldDescriptor().getDataType(),
							"GREATER_THAN", dateParser)) ? constraint
					.getMinValue() : bcb.minValue;
		}

		if (null != constraint.getMaxValue()) {
			bcb.maxValue = (null == bcb.maxValue || DictValidatorUtils
					.compareValues(bcb.maxValue, constraint.getMaxValue(),
							field.getFieldDescriptor().getDataType(),
							"LESS_THAN", dateParser)) ? constraint
					.getMaxValue() : bcb.maxValue;
		}

		if (StringUtils.hasText(constraint.getMaxLength())) {
			if (UNBOUNDED_CHECK.equalsIgnoreCase(bcb.maxLength)) {
				bcb.maxLength = constraint.getMaxLength();
			} else if (!UNBOUNDED_CHECK.equalsIgnoreCase(constraint
					.getMaxLength())) {
				if (Integer.parseInt(bcb.maxLength) > Integer
						.parseInt(constraint.getMaxLength())) {
					bcb.maxLength = constraint.getMaxLength();
				}
			}
		}

		if (StringUtils.hasText(constraint.getMaxOccurs())) {
			if (UNBOUNDED_CHECK.equalsIgnoreCase(bcb.maxOccurs)) {
				bcb.maxLength = constraint.getMaxOccurs();
			} else if (!UNBOUNDED_CHECK.equalsIgnoreCase(constraint
					.getMaxOccurs())) {
				if (Integer.parseInt(bcb.maxOccurs) > Integer
						.parseInt(constraint.getMaxOccurs())) {
					bcb.maxOccurs = constraint.getMaxOccurs();
				}
			}
		}

		// Process Valid Chars
		if (null != constraint.getValidChars()) {
			processValidCharConstraint(valResults, constraint.getValidChars(),
					dataProvider, value);
		}

		// Process Require Constraints (only if this field has value)
		if (value != null && !"".equals(value.toString().trim())) {
			if (null != constraint.getRequireConstraint()
					&& constraint.getRequireConstraint().size() > 0) {
				for (RequireConstraint rc : constraint.getRequireConstraint()) {
					processRequireConstraint(valResults, rc, field, objStructure,
							dataProvider);
				}
			}
		}

		// Process Occurs Constraint
		if (null != constraint.getOccursConstraint()
				&& constraint.getOccursConstraint().size() > 0) {
			for (OccursConstraint oc : constraint.getOccursConstraint()) {
				processOccursConstraint(valResults, oc, field, type, state,
						objStructure, dataProvider);
			}
		}

		// Process lookup Constraint
		// TODO: Implement lookup constraint
		if (null != constraint.getLookupConstraint()
				&& constraint.getLookupConstraint().size() > 0) {
			for (LookupConstraint lc : constraint.getLookupConstraint()) {
				processLookupConstraint(valResults);
			}
		}

		// Process Case Constraint
		if (null != constraint.getCaseConstraint()
				&& constraint.getCaseConstraint().size() > 0) {
			for (CaseConstraint cc : constraint.getCaseConstraint()) {
				processCaseConstraint(valResults, cc, field, type, state,
						objStructure, value, dataProvider, bcb);
			}
		}
	}

	private boolean processRequireConstraint(
			DictValidationResultContainer valResults,
			RequireConstraint constraint, Field field, ObjectStructure objStructure,
			ConstraintDataProvider dataProvider) {

		boolean result = false;

		String fieldName = constraint.getField();
		Object fieldValue = dataProvider.getValue(fieldName);

		if (fieldValue instanceof java.lang.String) {
			result = StringUtils.hasText((String) fieldValue);
		} else if (fieldValue instanceof java.util.Collection) {
			result = (((Collection<?>) fieldValue).size() > 0);
		} else {
			result = (null != fieldValue) ? true : false;
		}

		if (!result) {
			Map<String, Object> rMap = new HashMap<String, Object>();
			rMap.put("field1", field.getKey());
			rMap.put("field2", fieldName);
			valResults.addError(MessageUtils.interpolate(messages
					.get("validation.requiresField"), rMap));
		}

		return result;
	}

	/**
	 * Process caseConstraint tag and sets any of the base constraint items if
	 * any of the when condition matches
	 * 
	 * @param bcb
	 * @param caseConstraint
	 * @param field
	 */
	private void processCaseConstraint(
			DictValidationResultContainer valResults,
			CaseConstraint constraint, Field field, Type type, State state,
			ObjectStructure objStructure, Object value,
			ConstraintDataProvider dataProvider, BaseConstraintBean bcb) {

		String operator = (StringUtils.hasText(constraint.getOperator())) ? constraint
				.getOperator()
				: "EQUALS";
		Field caseField = (StringUtils.hasText(constraint.getField())) ? DictValidatorUtils
				.getField(constraint.getField(), objStructure, type.getKey(),
						state.getKey())
				: null;

		// TODO: What happens when the field is not in the dataProvider?
		Object fieldValue = (null != caseField) ? dataProvider
				.getValue(caseField.getKey()) : value;

		// Extract value for field Key
		for (WhenConstraint wc : constraint.getWhenConstraint()) {
			String whenValue = wc.getValue();

			if (DictValidatorUtils.compareValues(fieldValue, whenValue,
					caseField.getFieldDescriptor().getDataType(), operator,
					dateParser)) {
				processConstraint(valResults, wc.getConstraint(), field, type,
						state, objStructure, value, dataProvider, bcb);
			}
		}
	}

	private void processValidCharConstraint(
			DictValidationResultContainer valResults,
			ValidCharsConstraint vcConstraint,
			ConstraintDataProvider dataProvider, Object value) {

		StringBuilder fieldValue = new StringBuilder();
		String validChars = vcConstraint.getValue();
		String fields = vcConstraint.getFields();

		if (StringUtils.hasText(fields)) {
			String separator = vcConstraint.getSeparator();
			String[] fieldNameList = fields.split(",");

			int sz = fieldNameList.length;

			for (String fieldName : fieldNameList) {
				Object v = dataProvider.getValue(fieldName);
				fieldValue.append(DictValidatorUtils.getString(v));

				if (--sz > 0) {
					fieldValue.append(separator);
				}
			}
		} else {
			fieldValue.append(DictValidatorUtils.getString(value));
		}

		int typIdx = validChars.indexOf(":");
		String processorType = "regex";
		if (-1 == typIdx) {
			validChars = "[" + validChars + "]*";
		} else {
			processorType = validChars.substring(0, typIdx);
			validChars = validChars.substring(typIdx + 1);
		}

		// TODO: Allow different processing based on the label
		if ("regex".equalsIgnoreCase(processorType)) {
			if (!Pattern.matches(validChars, fieldValue.toString())) {
				valResults
						.addError(messages.get("validation.validCharsFailed"));
			}
		}
	}

	/**
	 * Computes if all the filed required in the occurs clause are between the
	 * min and max
	 * 
	 * @param valResults
	 * @param constraint
	 * @param field
	 * @param type
	 * @param state
	 * @param objStructure
	 * @param dataProvider
	 * @return
	 */
	private boolean processOccursConstraint(
			DictValidationResultContainer valResults,
			OccursConstraint constraint, Field field, Type type, State state,
			ObjectStructure objStructure, ConstraintDataProvider dataProvider) {

		boolean result = false;
		int trueCount = 0;

		DictValidationResultContainer tempC = new DictValidationResultContainer(
				null);

		for (RequireConstraint rc : constraint.getRequire()) {
			trueCount += (processRequireConstraint(tempC, rc, field, objStructure,
					dataProvider)) ? 1 : 0;
		}

		for (OccursConstraint oc : constraint.getOccurs()) {
			trueCount += (processOccursConstraint(tempC, oc, field, type,
					state, objStructure, dataProvider)) ? 1 : 0;
		}

		result = (trueCount >= constraint.getMin() && trueCount <= constraint
				.getMax()) ? true : false;

		if (!result) {
			valResults.addError(messages.get("validation.occurs"));
		}

		return result;
	}

	private void processLookupConstraint(
			DictValidationResultContainer valResults) {
	}

	private void processTypeStateCaseConstraint(
			DictValidationResultContainer valResults) {
	}

	private void processBaseConstraints(
			DictValidationResultContainer valResults, BaseConstraintBean bcb,
			Field field, Object value) {

		String dataType = field.getFieldDescriptor().getDataType();

		valResults.setDataType(dataType);
		valResults.setDerivedMinLength(bcb.minLength);
		valResults.setDerivedMaxLength(bcb.maxLength);
		valResults.setDerivedMinOccurs(bcb.minOccurs);
		valResults.setDerivedMaxOccurs(bcb.maxOccurs);

		if (value == null || "".equals(value.toString().trim())) {
			if (bcb.minOccurs != null && bcb.minOccurs > 0) {
				valResults.addError(messages.get("validation.required"));
				return;
			}
		}

		if ("string".equalsIgnoreCase(dataType)) {
			validateString(value, bcb, valResults);
		} else if ("integer".equalsIgnoreCase(dataType)) {
			validateInteger(value, bcb, valResults);
		} else if ("long".equalsIgnoreCase(dataType)) {
			validateLong(value, bcb, valResults);
		} else if ("double".equalsIgnoreCase(dataType)) {
			validateDouble(value, bcb, valResults);
		} else if ("float".equalsIgnoreCase(dataType)) {
			validateFloat(value, bcb, valResults);
		} else if ("boolean".equalsIgnoreCase(dataType)) {
			validateBoolean(value, bcb, valResults);
		} else if ("date".equalsIgnoreCase(dataType)) {
			validateDate(value, bcb, valResults, dateParser);
		}
	}

	private void validateBoolean(Object value, BaseConstraintBean bcb,
			DictValidationResultContainer result) {
		if (!(value instanceof Boolean)) {
			try {
				Boolean.valueOf(value.toString());
			} catch (Exception e) {
				result.addError(messages.get("validation.mustBeBoolean"));
			}
		}
	}

	private void validateDouble(Object value, BaseConstraintBean bcb,
			DictValidationResultContainer result) {
		Double v = null;
		if (value instanceof Number) {
			v = ((Number) value).doubleValue();
		} else {
			try {
				v = Double.valueOf(value.toString());
			} catch (Exception e) {
				result.addError(messages.get("validation.mustBeDouble"));
			}
		}

		if (result.isOk()) {
			Double maxValue = DictValidatorUtils.getDouble(bcb.maxValue);
			Double minValue = DictValidatorUtils.getDouble(bcb.minValue);

			if (maxValue != null && minValue != null) {
				// validate range
				if (v > maxValue || v < minValue) {
					result.addError(MessageUtils.interpolate(messages
							.get("validation.outOfRange"), bcb.toMap()));
				}
			} else if (maxValue != null) {
				if (v > maxValue) {
					result.addError(MessageUtils.interpolate(messages
							.get("validation.maxValueFailed"), bcb.toMap()));
				}
			} else if (minValue != null) {
				if (v < minValue) {
					result.addError(MessageUtils.interpolate(messages
							.get("validation.minValueFailed"), bcb.toMap()));
				}
			}
		}
	}

	private void validateFloat(Object value, BaseConstraintBean bcb,
			DictValidationResultContainer result) {
		Float v = null;

		if (value instanceof Number) {
			v = ((Number) value).floatValue();
		} else {
			try {
				v = Float.valueOf(value.toString());
			} catch (Exception e) {
				result.addError(messages.get("validation.mustBeFloat"));
			}
		}

		if (result.isOk()) {
			Float maxValue = DictValidatorUtils.getFloat(bcb.maxValue);
			Float minValue = DictValidatorUtils.getFloat(bcb.minValue);

			if (maxValue != null && minValue != null) {
				// validate range
				if (v > maxValue || v < minValue) {
					result.addError(MessageUtils.interpolate(messages
							.get("validation.outOfRange"), bcb.toMap()));
				}
			} else if (maxValue != null) {
				if (v > maxValue) {
					result.addError(MessageUtils.interpolate(messages
							.get("validation.maxValueFailed"), bcb.toMap()));
				}
			} else if (minValue != null) {
				if (v < minValue) {
					result.addError(MessageUtils.interpolate(messages
							.get("validation.minValueFailed"), bcb.toMap()));
				}
			}
		}
	}

	private void validateLong(Object value, BaseConstraintBean bcb,
			DictValidationResultContainer result) {
		Long v = null;

		if (value instanceof Number) {
			v = ((Number) value).longValue();
		} else {
			try {
				v = Long.valueOf(value.toString());
			} catch (Exception e) {
				result.addError(messages.get("validation.mustBeLong"));
			}
		}

		if (result.isOk()) {
			Long maxValue = DictValidatorUtils.getLong(bcb.maxValue);
			Long minValue = DictValidatorUtils.getLong(bcb.minValue);

			if (maxValue != null && minValue != null) {
				// validate range
				if (v > maxValue || v < minValue) {
					result.addError(MessageUtils.interpolate(messages
							.get("validation.outOfRange"), bcb.toMap()));
				}
			} else if (maxValue != null) {
				if (v > maxValue) {
					result.addError(MessageUtils.interpolate(messages
							.get("validation.maxValueFailed"), bcb.toMap()));
				}
			} else if (minValue != null) {
				if (v < minValue) {
					result.addError(MessageUtils.interpolate(messages
							.get("validation.minValueFailed"), bcb.toMap()));
				}
			}
		}

	}

	private void validateInteger(Object value, BaseConstraintBean bcb,
			DictValidationResultContainer result) {
		Integer v = null;

		if (value instanceof Number) {
			v = ((Number) value).intValue();
		} else {
			try {
				v = Integer.valueOf(value.toString());
			} catch (Exception e) {
				result.addError(messages.get("validation.mustBeInteger"));
			}
		}

		if (result.isOk()) {
			Integer maxValue = DictValidatorUtils.getInteger(bcb.maxValue);
			Integer minValue = DictValidatorUtils.getInteger(bcb.minValue);

			if (maxValue != null && minValue != null) {
				// validate range
				if (v > maxValue || v < minValue) {
					result.addError(MessageUtils.interpolate(messages
							.get("validation.outOfRange"), bcb.toMap()));
				}
			} else if (maxValue != null) {
				if (v > maxValue) {
					result.addError(MessageUtils.interpolate(messages
							.get("validation.maxValueFailed"), bcb.toMap()));
				}
			} else if (minValue != null) {
				if (v < minValue) {
					result.addError(MessageUtils.interpolate(messages
							.get("validation.minValueFailed"), bcb.toMap()));
				}
			}
		}

	}

	private void validateDate(Object value, BaseConstraintBean bcb,
			DictValidationResultContainer result, DateParser dateParser) {
		Date v = null;

		if (value instanceof Date) {
			v = (Date) value;
		} else {
			try {
				v = dateParser.parseDate(value.toString());
			} catch (Exception e) {
				result.addError(messages.get("validation.mustBeDate"));
			}
		}

		if (result.isOk()) {
			Date maxValue = DictValidatorUtils
					.getDate(bcb.maxValue, dateParser);
			Date minValue = DictValidatorUtils
					.getDate(bcb.minValue, dateParser);

			if (maxValue != null && minValue != null) {
				// validate range
				if (v.getTime() > maxValue.getTime()
						|| v.getTime() < minValue.getTime()) {
					result.addError(MessageUtils.interpolate(messages
							.get("validation.outOfRange"), bcb.toMap()));
				}
			} else if (maxValue != null) {
				if (v.getTime() > maxValue.getTime()) {
					result.addError(MessageUtils.interpolate(messages
							.get("validation.maxValueFailed"), bcb.toMap()));
				}
			} else if (minValue != null) {
				if (v.getTime() < minValue.getTime()) {
					result.addError(MessageUtils.interpolate(messages
							.get("validation.minValueFailed"), bcb.toMap()));
				}
			}
		}
	}

	private void validateString(Object value, BaseConstraintBean bcb,
			DictValidationResultContainer result) {
		String s = value.toString().trim();

		if (!UNBOUNDED_CHECK.equalsIgnoreCase(bcb.maxLength)
				&& bcb.minLength > 0) {
			if (s.length() > Integer.parseInt(bcb.maxLength)
					|| s.length() < bcb.minLength) {
				result.addError(MessageUtils.interpolate(messages
						.get("validation.lengthOutOfRange"), bcb.toMap()));
			}
		} else if (!UNBOUNDED_CHECK.equalsIgnoreCase(bcb.maxLength)) {
			if (s.length() > Integer.parseInt(bcb.maxLength)) {
				result.addError(MessageUtils.interpolate(messages
						.get("validation.maxLengthFailed"), bcb.toMap()));
			}
		} else if (bcb.minLength > 0) {
			if (s.length() < bcb.minLength) {
				result.addError(MessageUtils.interpolate(messages
						.get("validation.minLengthFailed"), bcb.toMap()));
			}
		}
	}

	private String getElementXpath() {
		StringBuilder xPath = new StringBuilder("/");

		Iterator<String> itr = elementStack.iterator();
		while (itr.hasNext()) {
			xPath.append(itr.next() + "/");
		}

		return xPath.toString();
	}

	private class BaseConstraintBean {
		public Integer minOccurs = 0;
		public String maxOccurs = UNBOUNDED_CHECK;
		public Integer minLength = 0;
		public String maxLength = UNBOUNDED_CHECK;
		public String dataType = null;
		public String minValue = null;
		public String maxValue = null;

		public Map<String, Object> toMap() {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("minOccurs", minOccurs);
			result.put("maxOccurs", maxOccurs);
			result.put("minLength", minLength);
			result.put("maxLength", maxLength);
			result.put("minValue", minValue);
			result.put("maxValue", maxValue);
			result.put("dataType", dataType);

			return result;
		}
	}
}
