package com.primix.tapestry.components;

import com.primix.tapestry.*;
import com.primix.tapestry.spec.*;

/*
 * Tapestry Web Application Framework
 * Copyright (c) 2000 by Howard Ship and Primix Solutions
 *
 * Primix Solutions
 * One Arsenal Marketplace
 * Watertown, MA 02472
 * http://www.primix.com
 * mailto:hship@primix.com
 * 
 * This library is free software.
 * 
 * You may redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation.
 *
 * Version 2.1 of the license should be included with this distribution in
 * the file LICENSE, as well as License.html. If the license is not
 * included with this distribution, you may find a copy at the FSF web
 * site at 'www.gnu.org' or 'www.fsf.org', or you may write to the
 * Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139 USA.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 */

/**
 *  A base class for building components that correspond to HTML form elements.
 *  All such components must be wrapped (directly or indirectly) by
 *  a {@link Form} component, and may have an 
 *  {@link IActionListener action listener}.
 *
 *  @version $Id$
 *  @author Howard Ship
 *
 */

public abstract class AbstractFormComponent
    extends AbstractComponent
{
	private IBinding listenerBinding;

	public AbstractFormComponent(IPage page, IComponent container, 
		String id, ComponentSpecification specification)
	{
		super(page, container, id, specification);
	}

	public void setListenerBinding(IBinding value)
	{
		listenerBinding = value;
	}

	public IBinding getListenerBinding()
	{
		return listenerBinding;
	}

	public IActionListener getListener(IRequestCycle cycle)
	throws RequestCycleException
	{
		if (listenerBinding == null)
			return null;

		try
		{
			return (IActionListener)listenerBinding.getValue();
		}
		catch (ClassCastException e)
		{
			throw new RequestCycleException(
				"Parameter listener is not type IActionListener.",
				this, cycle, e);
		}

	}

	public Form getForm(IRequestCycle cycle)
	throws RequestCycleException
	{
		Form result;

		result = Form.get(cycle);

		if (result == null)
			throw new RequestCycleException(
				"This component must be contained within a Form.",
				this, cycle);

		return result;
	}
}


