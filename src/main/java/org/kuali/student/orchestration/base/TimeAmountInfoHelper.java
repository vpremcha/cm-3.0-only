/*
 * Copyright 2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may	obtain a copy of the License at
 *
 * 	http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.student.orchestration.base;


import org.kuali.student.common.assembly.client.Data;
import org.kuali.student.lum.lu.assembly.data.client.PropertyEnum;


public class TimeAmountInfoHelper
{
	private static final long serialVersionUID = 1;
	
	public enum Properties implements PropertyEnum
	{
		ATP_DURATION_TYPE_KEY ("atpDurationTypeKey"),
		TIME_QUANTITY ("timeQuantity");
		
		private final String key;
		
		private Properties (final String key)
		{
			this.key = key;
		}
		
		@Override
		public String getKey ()
		{
			return this.key;
		}
	}
	private Data data;
	
	private TimeAmountInfoHelper (Data data)
	{
		this.data = data;
	}
	
	public static TimeAmountInfoHelper wrap (Data data)
	{
		if (data == null)
		{
			 return null;
		}
		return new TimeAmountInfoHelper (data);
	}
	
	public Data getData ()
	{
		return data;
	}
	
	
	public void setAtpDurationTypeKey (String value)
	{
		data.set (Properties.ATP_DURATION_TYPE_KEY.getKey (), value);
	}
	
	
	public String getAtpDurationTypeKey ()
	{
		return (String) data.get (Properties.ATP_DURATION_TYPE_KEY.getKey ());
	}
	
	
	public void setTimeQuantity (Integer value)
	{
		data.set (Properties.TIME_QUANTITY.getKey (), value);
	}
	
	
	public Integer getTimeQuantity ()
	{
		return (Integer) data.get (Properties.TIME_QUANTITY.getKey ());
	}
	
}

