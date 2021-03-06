//  Copyright 2004 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry.junit.mock.c17;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.html.BasePage;

/**
 *  Tests for the TextField component.
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 *  @since 3.0
 *
 **/

public abstract class Home extends BasePage
{
	public abstract String getNormal();
	public abstract void setNormal(String normal);
	
	public abstract String getDisabled();
	public abstract void setDisabled(String disabled);
	
	public abstract String getHidden();
	public abstract void setHidden(String hidden);
	
	protected void finishLoad()
	{
		setNormal("normal");
		setDisabled("disabled");
		setHidden("hidden");
	}
	
	public void formSubmit(IRequestCycle cycle)
	{
		Two page = (Two)cycle.getPage("Two");
		
		page.setNormal(getNormal());
		page.setDisabled(getDisabled());
		page.setHidden(getHidden());
		
		cycle.activate(page);
	}
}
