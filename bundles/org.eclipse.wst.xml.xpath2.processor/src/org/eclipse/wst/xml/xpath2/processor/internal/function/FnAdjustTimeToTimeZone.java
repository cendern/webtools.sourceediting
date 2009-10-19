/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - bug 280547 - initial API and implementation. 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Adjusts an xs:dateTime value to a specific timezone, or to no timezone at
 * all. If <code>$timezone</code> is the empty sequence, returns an
 * <code>xs:dateTime</code> without timezone. Otherwise, returns an
 * <code>xs:dateTime</code> with a timezone.
 */
public class FnAdjustTimeToTimeZone extends Function {
	private static Collection _expected_args = null;
	private static final XSDayTimeDuration minDuration = new XSDayTimeDuration(
			0, 14, 0, 0, true);
	private static final XSDayTimeDuration maxDuration = new XSDayTimeDuration(
			0, 14, 0, 0, false);

	/**
	 * Constructor for FnDateTime.
	 */
	public FnAdjustTimeToTimeZone() {
		super(new QName("adjust-time-to-timezone"), 1, 2);
	}

	/**
	 * Evaluate arguments.
	 * 
	 * @param args
	 *            argument expressions.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of evaluation.
	 */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return adjustTime(args, dynamic_context());
	}

	/**
	 * Evaluate the function using the arguments passed.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @param sc
	 *            Result of static context operation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of the fn:dateTime operation.
	 */
	public static ResultSequence adjustTime(Collection args,
			DynamicContext dc) throws DynamicError {

		XSDuration impTimeZone = dc.tz();

		Collection cargs = Function.convert_arguments(args, expectedArgs());

		ResultSequence rs = ResultSequenceFactory.create_new();

		// get args
		Iterator argiter = cargs.iterator();
		ResultSequence arg1 = (ResultSequence) argiter.next();
		if (arg1.empty()) {
			return rs;
		}
		ResultSequence arg2 = ResultSequenceFactory.create_new();
		if (argiter.hasNext()) {
			arg2 = (ResultSequence) argiter.next();
		}
		XSTime time = (XSTime) arg1.first();
		XSDayTimeDuration timezone = null;
		
		if (arg2.empty()) {
			if (time.timezoned()) {
				XSTime localized = new XSTime(time.calendar(), null);
				rs.add(localized);
				return rs;
			} else {
				return arg1;
			}
		}


		XMLGregorianCalendar xmlCalendar = null;
		
		try {
			if (time.tz() != null) {
				xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar)time.normalizeCalendar(time.calendar(), time.tz()));
			} else {
				xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendarTime(time.hour(), time.minute(), (int)time.second(), 0);
			}
	
			timezone = (XSDayTimeDuration) arg2.first();
			if (timezone.lt(minDuration, dc) || timezone.gt(maxDuration, dc)) {
				throw DynamicError.invalidTimezone();
			}
			
			if (time.tz() == null) {
				rs.add(new XSTime(time.calendar(), timezone));
				return rs;
			}
			
			if (time.tz().hours() == 0 && time.tz().minutes() == 0) {
				Duration duration = DatatypeFactory.newInstance().newDuration(timezone.string_value());
				xmlCalendar.add(duration);
			} else { 
				Duration duration = DatatypeFactory.newInstance().newDuration(timezone.string_value());
				xmlCalendar.add(duration);
			}
	
			rs.add(new XSTime(xmlCalendar.toGregorianCalendar(), timezone));
		} catch (DatatypeConfigurationException ex) {
			throw DynamicError.invalidTimezone();
		}

		return rs;
	}

	/**
	 * Obtain a list of expected arguments.
	 * 
	 * @return Result of operation.
	 */
	public static Collection expectedArgs() {
		if (_expected_args == null) {
			_expected_args = new ArrayList();
			_expected_args
					.add(new SeqType(new XSTime(), SeqType.OCC_QMARK));
			_expected_args.add(new SeqType(new XSDayTimeDuration(),
					SeqType.OCC_QMARK));
		}

		return _expected_args;
	}
}
