package com.primix.tapestry.pageload;

import java.lang.reflect.*;
import com.primix.tapestry.event.*;
import com.primix.tapestry.spec.*;
import java.util.*;
import com.primix.tapestry.*;
import com.primix.tapestry.binding.*;

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
 *  Runs the process of building the component hierarchy for an entire page.
 *
 * @author Howard Ship
 * @version $Id$
 */


public class PageLoader
    implements IPageLoader
{
	private IApplication application;
    private IResourceResolver resolver;
	private ISpecificationSource specificationSource;

	/**
	*  Number of components instantiated, excluding the page itself.
	*
	*/

	private int count;

	/**
	*  The recursion depth.  A page with no components is zero.  A component on
	*  a page is one.
	*
	*/

	private int depth;

	/**
	*  The maximum depth reached while building the page.
	*
	*/

	private int maxDepth;

	/**
	*  Constructor.
	*
	*/

	public PageLoader(IApplication application)
	{
		specificationSource = application.getSpecificationSource();

		this.application = application;
        resolver = application.getResourceResolver();

	}

	/**
	*  Binds properties of the component as defined by the container's specification.
	*
	* <p>This implemenation is very simple, we will need a lot more
	*  sanity checking and error checking in the final version.
	*
	*  @param container The containing component.  For a dynamic 
	*  binding ({@link PropertyBinding}) the property name
	*  is evaluated with the container as the root. 
	*  @param component The contained component being bound.
	*  @param spec The specification of the contained component.
	* @param contained The contained component specification (from the container's 
	* {@link ComponentSpecification}).
	*
	*/

	protected void bind(IComponent container, IComponent component, ComponentSpecification spec, 
		ContainedComponent contained)
	throws PageLoaderException
	{
		Iterator i;
		String name;
		BindingSpecification bspec;
		IBinding binding;
		String bindingValue;
		BindingType type;
		boolean checkForFormal;
		ParameterSpecification parameterSpec;

		checkForFormal = !spec.getAllowInformalParameters();

		i = contained.getBindingNames().iterator();
		while (i.hasNext())
		{
			name = (String)i.next();

			// If not allowing informal parameters, check that each binding matches
			// a formal parameter.

			if (checkForFormal)
			{
				if (spec.getParameter(name) == null)
					throw new PageLoaderException(
						"Component " + component.getExtendedId() +
						" allows only formal paramters, binding " +
						name + " is not allowed.", 
						this, component, null);
			}

			bspec = contained.getBinding(name);

			// The type determines how to interpret the value:
			// As a simple static String
			// As a nested property name (relative to the component)
			// As the name of a binding inherited from the containing component.

			type = bspec.getType();
			bindingValue = bspec.getValue();

			if (type == BindingType.STATIC)
				binding = new StaticBinding(bindingValue);
			else if (type == BindingType.DYNAMIC)
				binding = new PropertyBinding(container, bindingValue);
			else
				binding = container.getBinding(bindingValue);		

			if (binding != null)
				component.setBinding(name, binding);
		}

		// Now, check that all required parameters are bound.

		i = spec.getParameterNames().iterator();
		while (i.hasNext())
		{
			name = (String)i.next();

			parameterSpec = spec.getParameter(name);

			if (parameterSpec.isRequired() &&
				component.getBinding(name) == null)
				throw new PageLoaderException(
					"Required parameter " + name + " of component " 
					+ component.getExtendedId() +
					" is not bound.",
					this, component, null);
		}

	}
    
	/**
	*  Sets up a component.  This involves:
	*  <ul>
	* <li>Instantiating any contained components.
	* <li>Add the contained components to the container.
	* <li>Setting up bindings between container and containees.
	* <li>Construct the containees recursively.
	* </ul>
	*
	* @param page The page on which the container exists.
	* @param container The component to be set up.
	* @param containerSpec The specification for the container.
	*
	*/

	protected void constructComponent(IPage page, IComponent container,
		ComponentSpecification containerSpec)
	throws PageLoaderException
	{
		IComponent component;
		ContainedComponent contained;
		ComponentSpecification  spec;
		String id;
		Iterator i;
		String type;

		depth++;
		if (depth > maxDepth)
			maxDepth = depth;

		i = containerSpec.getComponentIds().iterator();
		while (i.hasNext())
        {		
			id = (String)i.next();

			// Get the sub-component specification from the
			// container's specification.

			contained = containerSpec.getComponent(id);

			// Get the component specification for the contained
			// component.

			try
			{
				spec = specificationSource.getSpecification(contained.getType());
			}
			catch (ResourceUnavailableException re)
			{
				throw new PageLoaderException(re.getMessage(), this, container, re);
			}

			// Instantiate the contained component.

			component = instantiateComponent(page, container, id, spec);

			// Add it, by name, to the container.

			container.addComponent(id, component);

			// Bind its parameters.

			bind(container, component, spec, contained);

			// Add the new component to the queue of components to be
			// set up.

			constructComponent(page, component, spec);
		}

		if (container instanceof ILifecycle)
			page.addLifecycleComponent((ILifecycle)container);

		depth--;

	}

	/**
	*  Instantitates a component from its specification.  
	*  <p>Instantiating a page is a bit different that instantitating a component
	*  because the constructor is different.
	*
	* <p>A component requires a constructor that takes the following
	*  parameters:
	* <ul>
	* <li>IPage
	* <li>IComponent
	* <li>String
	* <li>ComponentSpecification
	* </li>
	*
	*  @see AbstractComponent
	*/

	protected IComponent instantiateComponent(IPage page, IComponent container, 
		String id, ComponentSpecification spec)
	throws PageLoaderException
	{
    	String className;
		Class componentClass;
		IComponent result = null;
		Constructor constructor;
		Class[] signature;
		Object[] parameters;

		className = spec.getComponentClassName();
		componentClass = resolver.findClass(className);

		signature = new Class[] { IPage.class, IComponent.class, String.class,
                                  ComponentSpecification.class };

		try
		{
			constructor = componentClass.getConstructor(signature);
		}
		catch (NoSuchMethodException e)
		{
			throw new PageLoaderException(
				"Class " + componentClass.getName() + " does not implement a constructor with " +
				"the (IPage, IComponent, String, ComponentSpecification) " +
				"signature.",
				this, container, e);
		}

		parameters = new Object[] { page, container, id, spec };

		try
		{
			result = (IComponent)constructor.newInstance(parameters);
		}
		catch (ClassCastException e)
		{
			throw new PageLoaderException(
				"Class " + componentClass.getName() + 
				" does not implement the com.primix.tapestry.IComponent interface.", 
				this, container, e);
		}
		catch (Exception e)
		{
			throw new PageLoaderException("Could not instantitate component.", 
				this, container, e);
		}

		count++;

		return result;
	}

	/**
	*  Instantitates a page from its specification.  
	*  <p>Instantiating a page is a bit different that instantitating a component
	*  because the constructor is different.
	*
	* <p>A page must implement the <code>IPage</code> interface and
	* requires a constructor that takes the following
	*  parameters:
	* <ul>
	* <li>IApplication
	* <li>ComponentSpecification
	* </li>
	*
	* @see IPage
	* @see IApplication
	* @see ChangeObserver
	*/

	protected IPage instantiatePage(String name, ComponentSpecification spec)
	throws PageLoaderException
	{
    	String className;
		Class pageClass;
		IPage result = null;
		Constructor constructor;
		Class[] signature;
		Object[] parameters;

		className = spec.getComponentClassName();
        
		pageClass = resolver.findClass(className);

		signature = new Class[] { IApplication.class, ComponentSpecification.class };

		try
		{
			constructor = pageClass.getConstructor(signature);
		}
		catch (NoSuchMethodException e)
		{
			throw new PageLoaderException(

				"Class " + pageClass.getName() + " does not implement a constructor with " +
				"the (IApplication, ComponentSpecification) signature.",
				this, name, e);
		}

		parameters = new Object[] { application, spec };

		try
		{
			result = (IPage)constructor.newInstance(parameters);
		}
		catch (ClassCastException e)
		{
			throw new PageLoaderException(
				"Class " + pageClass.getName() + 
				" does not implement the com.primix.tapestry.IPage interface.", 
				this, name, e);
		}
		catch (Exception e)
		{
			throw new PageLoaderException(e.getMessage(), this, name, e);
		}

		result.setName(name);

		return result;
	}

	public IPage loadPage(String name, String type)
	throws PageLoaderException
	{
		IPage page = null;
		ComponentSpecification specification;

		count = 0;
		depth = 0;
		maxDepth = 0;

		try
		{			
			specification = specificationSource.getSpecification(type);

			page = instantiatePage(name, specification);

			constructComponent(page, page, specification);
		}
		catch (ResourceUnavailableException e)
		{
			throw new PageLoaderException(e.getMessage(), this, name, e);
		}

		return page;
	}

	/**
	*  Updates this <code>PageLoader</code> to build pages for the specified
	*  application.  This updates the specificationSource for
	*  this page loader from the application.
	*
	*  @param application The application to load pages for, or null
	*  to release references to the application and specificationSource.
	*/

	public void setApplication(IApplication application)
	{
		this.application = application;

		if (application == null)
			specificationSource = null;
		else
			specificationSource = application.getSpecificationSource();
	}
}

