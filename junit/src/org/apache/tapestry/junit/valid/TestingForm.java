/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation", "Tapestry" 
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache" 
 *    or "Tapestry", nor may "Apache" or "Tapestry" appear in their 
 *    name, without prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE TAPESTRY CONTRIBUTOR COMMUNITY
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.tapestry.junit.valid;

import java.util.Collection;
import java.util.Map;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IBinding;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IComponentStrings;
import org.apache.tapestry.IForm;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.INamespace;
import org.apache.tapestry.IPage;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.engine.IPageLoader;
import org.apache.tapestry.form.FormEventType;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.spec.BaseLocatable;
import org.apache.tapestry.spec.IComponentSpecification;
import org.apache.tapestry.valid.IValidationDelegate;

/**
 *  Simulates an {@link IForm} for the test suite.
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 *  @since 2.1
 *
 **/

public class TestingForm extends BaseLocatable implements IForm
{
    private String _name;

    public TestingForm()
    {
        this("DefaultFormName");
    }

    public TestingForm(String name)
    {
        _name = name;
    }

    public void rewind(IMarkupWriter writer, IRequestCycle cycle)
    {
    }

    public void addEventHandler(FormEventType type, String functionName)
    {
    }

    public String getElementId(IFormComponent component)
    {
        return null;
    }

    public String getElementId(IFormComponent component, String baseId)
    {
        return null;
    }

    public String getName()
    {
        return _name;
    }

    public boolean isRewinding()
    {
        return false;
    }

    public IValidationDelegate getDelegate()
    {
        return null;
    }

    public boolean getRequiresSession()
    {
        return false;
    }

    public void addAsset(String name, IAsset asset)
    {
    }

    public void addComponent(IComponent component)
    {
    }

    public void addWrapped(IRender element)
    {
    }

    public Map getAssets()
    {
        return null;
    }

    public IAsset getAsset(String name)
    {
        return null;
    }

    public IBinding getBinding(String name)
    {
        return null;
    }

    public Collection getBindingNames()
    {
        return null;
    }

    public Map getBindings()
    {
        return null;
    }

    public IComponent getComponent(String id)
    {
        return null;
    }

    public IComponent getContainer()
    {
        return null;
    }

    public void setContainer(IComponent value)
    {
    }

    public String getExtendedId()
    {
        return null;
    }

    public String getId()
    {
        return null;
    }

    public void setId(String value)
    {
    }

    public String getIdPath()
    {
        return null;
    }

    public IPage getPage()
    {
        return null;
    }

    public void setPage(IPage value)
    {
    }

    public IComponentSpecification getSpecification()
    {
        return null;
    }

    public void setSpecification(IComponentSpecification value)
    {
    }

    public void renderWrapped(IMarkupWriter writer, IRequestCycle cycle)
    {
    }

    public void setBinding(String name, IBinding binding)
    {
    }

    public Map getComponents()
    {
        return null;
    }

    public void finishLoad(IRequestCycle cycle, IPageLoader loader, IComponentSpecification specification)
    {
    }

    public String getString(String key)
    {
        return null;
    }

    public void render(IMarkupWriter writer, IRequestCycle cycle)
    {
    }

    public INamespace getNamespace()
    {
        return null;
    }

    public void setNamespace(INamespace namespace)
    {
    }

    public void addBody(IRender element)
    {
    }

    public void renderBody(IMarkupWriter writer, IRequestCycle cycle)
    {
    }

    public IComponentStrings getStrings()
    {
        return null;
    }

    public void setEncodingType(String encodingType)
    {
    }

    /** @since 3.0 */
    public void addHiddenValue(String name, String value)
    {

    }

}
