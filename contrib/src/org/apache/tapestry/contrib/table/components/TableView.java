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

package org.apache.tapestry.contrib.table.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry.ApplicationRuntimeException;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.IResourceResolver;
import org.apache.tapestry.contrib.table.model.IBasicTableModel;
import org.apache.tapestry.contrib.table.model.ITableColumn;
import org.apache.tapestry.contrib.table.model.ITableColumnModel;
import org.apache.tapestry.contrib.table.model.ITableDataModel;
import org.apache.tapestry.contrib.table.model.ITableModel;
import org.apache.tapestry.contrib.table.model.ITableModelSource;
import org.apache.tapestry.contrib.table.model.ITableSessionStateManager;
import org.apache.tapestry.contrib.table.model.ITableSessionStoreManager;
import org.apache.tapestry.contrib.table.model.common.BasicTableModelWrap;
import org.apache.tapestry.contrib.table.model.ognl.ExpressionTableColumn;
import org.apache.tapestry.contrib.table.model.simple.SimpleListTableDataModel;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumn;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableColumnModel;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableModel;
import org.apache.tapestry.contrib.table.model.simple.SimpleTableState;
import org.apache.tapestry.event.PageDetachListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.event.PageRenderListener;
import org.apache.tapestry.util.prop.OgnlUtils;

/**
 * A low level Table component that wraps all other low level Table components.
 * This component carries the {@link org.apache.tapestry.contrib.table.model.ITableModel}
 * that is used by the other Table components. Please see the documentation of
 * {@link org.apache.tapestry.contrib.table.model.ITableModel} if you need to know more
 * about how a table is represented.
 * <p>
 * This component also handles the saving of the state of the model using an 
 * {@link org.apache.tapestry.contrib.table.model.ITableSessionStateManager}
 * to determine what part of the model is to be saved and an 
 * {@link  org.apache.tapestry.contrib.table.model.ITableSessionStoreManager}
 * to determine how to save it.
 * <p>
 * Upon the beginning of a new request cycle when the table model is first needed,
 * the model is obtained using the following process:
 * <ul>
 * <li>The persistent state of the table is loaded.
 * If the tableSessionStoreManager binding has not been bound, the state is loaded 
 * from a persistent property within the component (it is null at the beginning). 
 * Otherwise the supplied
 * {@link  org.apache.tapestry.contrib.table.model.ITableSessionStoreManager} is used
 * to load the persistent state.
 * <li>The table model is recreated using the 
 * {@link org.apache.tapestry.contrib.table.model.ITableSessionStateManager} that
 * could be supplied using the tableSessionStateManager binding 
 * (but has a default value and is therefore not required).
 * <li>If the {@link org.apache.tapestry.contrib.table.model.ITableSessionStateManager}
 * returns null, then a table model is taken from the tableModel binding. Thus, if
 * the {@link org.apache.tapestry.contrib.table.model.common.NullTableSessionStateManager}
 * is used, the table model would be taken from the tableModel binding every time.
 * </ul>
 * Just before the rendering phase the persistent state of the model is saved in
 * the session. This process occurs in reverse:
 * <ul>
 * <li>The persistent state of the model is taken via the 
 * {@link org.apache.tapestry.contrib.table.model.ITableSessionStateManager}.
 * <li>If the tableSessionStoreManager binding has not been bound, the persistent
 * state is saved as a persistent page property. Otherwise the supplied
 * {@link  org.apache.tapestry.contrib.table.model.ITableSessionStoreManager} is used
 * to save the persistent state. Use of the 
 * {@link  org.apache.tapestry.contrib.table.model.ITableSessionStoreManager} 
 * is usually necessary when tables with the same model have to be used across 
 * multiple pages, and hence the state has to be saved in the Visit, rather than
 * in a persistent component property.
 * </ul>
 * <p>
 * 
 * <table border=1 align="center">
 * <tr>
 *    <th>Parameter</th>
 *    <th>Type</th>
 *    <th>Direction </th>
 *    <th>Required</th>
 *    <th>Default</th>
 *    <th>Description</th>
 * </tr>
 *
 * <tr>
 *  <td>tableModel</td>
 *  <td>{@link org.apache.tapestry.contrib.table.model.ITableModel}</td>
 *  <td>in</td>
 *  <td>yes</td>
 *  <td>&nbsp;</td>
 *  <td align="left">The TableModel to be used to render the table. 
 *      This binding is typically used only once at the beginning and then the 
 *      component stores the model in the session state. 
 *      <p>If you want the Table to read the model every time you can use
 *      a session state manager such as 
 *      {@link org.apache.tapestry.contrib.table.model.common.NullTableSessionStateManager}
 *      that will force it to get the TableModel from this binding every time.
 *      If you do this, however, you will be responsible for saving the state of 
 *      the table yourself.
 *      <p> You can also call the reset() method to force the Table to abandon
 *      its old model and reload a new one.
 *  </td> 
 * </tr>
 *
 * <tr>
 *  <td>tableSessionStateManager</td>
 *  <td>{@link org.apache.tapestry.contrib.table.model.ITableSessionStateManager}</td>
 *  <td>in</td>
 *  <td>no</td>
 *  <td>{@link org.apache.tapestry.contrib.table.model.common.FullTableSessionStateManager}</td>
 *  <td align="left">This is the session state manager that will control what part of the 
 *      table model will be saved in the session state. 
 *      It is then used to recreate the table model from
 *      using what was saved in the session. By default, the 
 *      {@link org.apache.tapestry.contrib.table.model.common.FullTableSessionStateManager}
 *      is used, which just saves the entire model into the session.
 *      This behaviour may not be appropriate when the data is a lot or it is not
 *      {@link java.io.Serializable}.
 *      <p> You can use one of the stock implementations of  
 *      {@link org.apache.tapestry.contrib.table.model.ITableSessionStateManager}
 *      to determine the session state behaviour, or you can just define your own.
 *  </td> 
 * </tr>
 *
 * <tr>
 *  <td>tableSessionStoreManager</td>
 *  <td>{@link org.apache.tapestry.contrib.table.model.ITableSessionStoreManager}</td>
 *  <td>in</td>
 *  <td>no</td>
 *  <td>null</td>
 *  <td align="left">Determines how the session state (returned by the session state manager)
 *      will be saved in the session. If this parameter is null, then the state
 *      will be saved as a persistent property. If it is not null, then the methods
 *      of the interface will be used to save and load the state.
 *  </td> 
 * </tr>
 *
 * <tr>
 *  <td>element</td>
 *  <td>String</td>
 *  <td>in</td>
 *  <td>no</td>
 *  <td>"table"</td>
 *  <td align="left">The tag that will be used to wrap the inner components.
 *      If no binding is given, the tag that will be generated is 'table'. If you 
 *      would like to place the bounds of the table elsewhere, you can make the
 *      element 'span' or another neutral tag and manually define the table.
 *  </td> 
 * </tr>
 *
 * </table> 
 * 
 * 
 * @author mindbridge
 * @version $Id$
 */
public abstract class TableView
    extends BaseComponent
    implements PageDetachListener, PageRenderListener, ITableModelSource
{
    // Component properties
    private ITableSessionStateManager m_objDefaultSessionStateManager = null;

    // Persistent properties
    private Serializable m_objSessionState;

    // Transient objects
    private ITableModel m_objTableModel;
    private ITableModel m_objCachedTableModelValue;

    // enhanced parameter methods
    public abstract ITableModel getTableModelValue();
    public abstract Object getSource();
    public abstract Object getColumns();
    public abstract ITableSessionStateManager getTableSessionStateManager();
    public abstract ITableSessionStoreManager getTableSessionStoreManager();
    public abstract IComponent getColumnSettingsContainer();

    // enhanced property methods
    public abstract Serializable getSessionState();
    public abstract void setSessionState(Serializable sessionState);

    /**
     *  The component constructor. Invokes the component member initializations. 
     */
    public TableView()
    {
        initialize();
    }

    /**
     *  Invokes the component member initializations.
     *  
     *  @see org.apache.tapestry.event.PageDetachListener#pageDetached(PageEvent)
     */
    public void pageDetached(PageEvent objEvent)
    {
        initialize();
    }

    /**
     *  Initialize the component member variables.
     */
    private void initialize()
    {
        m_objSessionState = null;
        m_objTableModel = null;
        m_objCachedTableModelValue = null;
    }

    /**
     *  Resets the table by removing any stored table state. 
     *  This means that the current column to sort on and the current page will be
     *  forgotten and all data will be reloaded.
     */
    public void reset()
    {
        m_objTableModel = null;
        storeSessionState(null);
    }

    public ITableModel getCachedTableModelValue()
    {
        if (m_objCachedTableModelValue == null)
            m_objCachedTableModelValue = getTableModelValue();
        return m_objCachedTableModelValue;
    }

    /**
     *  Returns the tableModel.
     * 
     *  @return ITableModel the table model used by the table components
     */
    public ITableModel getTableModel()
    {
        // if null, first try to recreate the model from the session state
        if (m_objTableModel == null)
        {
            Serializable objState = loadSessionState();
            m_objTableModel = getTableSessionStateManager().recreateTableModel(objState);
        }

        // if the session state does not help, get the model from the binding
        if (m_objTableModel == null)
            m_objTableModel = getCachedTableModelValue();

        // if the model from the binding is null, build a model from source and columns
        if (m_objTableModel == null)
            m_objTableModel = generateTableModel(null);

        if (m_objTableModel == null)
            throw new ApplicationRuntimeException(format("missing-table-model", this));

        return m_objTableModel;
    }

    /**
     *  Generate a table model using the 'source' and 'columns' parameters.
     * 
     *  @return the newly generated table model
     */
    protected ITableModel generateTableModel(SimpleTableState objState)
    {
        // get the column model. if not possible, return null.
        ITableColumnModel objColumnModel = getTableColumnModel();
        if (objColumnModel == null)
            return null;

        Object objSourceValue = getSource();

        // if the source parameter is of type {@link IBasicTableModel}, 
        // create and return an appropriate wrapper
        if (objSourceValue instanceof IBasicTableModel)
            return new BasicTableModelWrap((IBasicTableModel) objSourceValue, objColumnModel);

        // otherwise, the source parameter must contain the data to be displayed
        ITableDataModel objDataModel = null;
        if (objSourceValue instanceof Object[])
            objDataModel = new SimpleListTableDataModel((Object[]) objSourceValue);
        else if (objSourceValue instanceof List)
            objDataModel = new SimpleListTableDataModel((List) objSourceValue);
        else if (objSourceValue instanceof Collection)
            objDataModel = new SimpleListTableDataModel((Collection) objSourceValue);
        else if (objSourceValue instanceof Iterator)
            objDataModel = new SimpleListTableDataModel((Iterator) objSourceValue);

        if (objDataModel == null)
            return null;

        if (objState == null)
            objState = new SimpleTableState();

        return new SimpleTableModel(objDataModel, objColumnModel, objState);
    }

    /**
     *  Returns the table column model as specified by the 'columns' binding.
     *  If the value of the 'columns' binding is of a type different than
     *  ITableColumnModel, this method makes the appropriate conversion. 
     * 
     *  @return The table column model as specified by the 'columns' binding
     */
    protected ITableColumnModel getTableColumnModel()
    {
        Object objColumns = getColumns();

        if (objColumns instanceof ITableColumnModel)
        {
            return (ITableColumnModel) objColumns;
        }

        if (objColumns instanceof Iterator)
        {
            // convert to List
            Iterator objColumnsIterator = (Iterator) objColumns;
            List arrColumnsList = new ArrayList();
            CollectionUtils.addAll(arrColumnsList, objColumnsIterator);
            objColumns = arrColumnsList;
        }

        if (objColumns instanceof List)
        {
            // validate that the list contains only ITableColumn instances
            List arrColumnsList = (List) objColumns;
            int nColumnsNumber = arrColumnsList.size();
            for (int i = 0; i < nColumnsNumber; i++)
            {
                if (!(arrColumnsList.get(i) instanceof ITableColumn))
                    throw new ApplicationRuntimeException(format("columns-only-please", this));
            }
            //objColumns = arrColumnsList.toArray(new ITableColumn[nColumnsNumber]);
            return new SimpleTableColumnModel(arrColumnsList);
        }

        if (objColumns instanceof ITableColumn[])
        {
            return new SimpleTableColumnModel((ITableColumn[]) objColumns);
        }

        if (objColumns instanceof String)
        {
            return generateTableColumnModel((String) objColumns);
        }

        //throw new ApplicationRuntimeException("The columns binding must contain a list of ITableColumn objects or a description string");
        return null;
    }

    /**
     *  Generate a table column model out of the description string provided.
     *  Entries in the description string are separated by commas.
     *  Each column entry is of the format name, name:expression, 
     *  or name:displayName:expression.
     *  An entry prefixed with ! represents a non-sortable column.
     *  If the whole description string is prefixed with *, it represents
     *  columns to be included in a Form. 
     * 
     *  @param strDesc
     *  @return
     */
    protected ITableColumnModel generateTableColumnModel(String strDesc)
    {
        if (strDesc == null)
            return null;

        IComponent objColumnSettingsContainer = getColumnSettingsContainer();
        List arrColumns = new ArrayList();

        boolean bFormColumns = false;
        while (strDesc.startsWith("*"))
        {
            strDesc = strDesc.substring(1);
            bFormColumns = true;
        }

        StringTokenizer objTokenizer = new StringTokenizer(strDesc, ",");
        while (objTokenizer.hasMoreTokens())
        {
            String strToken = objTokenizer.nextToken().trim();

            if (strToken.startsWith("="))
            {
                String strColumnExpression = strToken.substring(1);
                IResourceResolver objResolver = getPage().getEngine().getResourceResolver();

                Object objColumn =
                    OgnlUtils.get(strColumnExpression, objResolver, objColumnSettingsContainer);
                if (!(objColumn instanceof ITableColumn))
                    throw new ApplicationRuntimeException(
                        format("not-a-column", this, strColumnExpression));

                arrColumns.add(objColumn);
                continue;
            }

            boolean bSortable = true;
            if (strToken.startsWith("!"))
            {
                strToken = strToken.substring(1);
                bSortable = false;
            }

            StringTokenizer objColumnTokenizer = new StringTokenizer(strToken, ":");

            String strName = "";
            if (objColumnTokenizer.hasMoreTokens())
                strName = objColumnTokenizer.nextToken();

            String strExpression = strName;
            if (objColumnTokenizer.hasMoreTokens())
                strExpression = objColumnTokenizer.nextToken();

            String strDisplayName = strName;
            if (objColumnTokenizer.hasMoreTokens())
            {
                strDisplayName = strExpression;
                strExpression = objColumnTokenizer.nextToken();
            }

            ExpressionTableColumn objColumn =
                new ExpressionTableColumn(strName, strDisplayName, strExpression, bSortable);
            if (bFormColumns)
                objColumn.setColumnRendererSource(SimpleTableColumn.FORM_COLUMN_RENDERER_SOURCE);
            if (objColumnSettingsContainer != null)
                objColumn.loadSettings(objColumnSettingsContainer);

            arrColumns.add(objColumn);
        }

        return new SimpleTableColumnModel(arrColumns);
    }

    /**
     *  The default session state manager to be used in case no such manager
     *  is provided by the corresponding parameter.
     * 
     *  @return the default session state manager
     */
    public ITableSessionStateManager getDefaultTableSessionStateManager()
    {
        if (m_objDefaultSessionStateManager == null)
            m_objDefaultSessionStateManager = new TableViewSessionStateManager(this);
        return m_objDefaultSessionStateManager;
    }

    /**
     *  Invoked when there is a modification of the table state and it needs to be saved
     *  
     *  @see org.apache.tapestry.contrib.table.model.ITableModelSource#fireObservedStateChange()
     */
    public void fireObservedStateChange()
    {
        saveSessionState();
    }

    /**
     * @see org.apache.tapestry.event.PageRenderListener#pageBeginRender(org.apache.tapestry.event.PageEvent)
     */
    public void pageBeginRender(PageEvent event)
    {
    }

    /**
     *  Ensures that the table state is saved at the end of the rewind phase 
     *  in case there are modifications for which {@link fireObservedStateChange} 
     *  has not been invoked.
     * 
     *  @see org.apache.tapestry.event.PageRenderListener#pageBeginRender(PageEvent)
     */
    public void pageEndRender(PageEvent objEvent)
    {
        // ignore if not rewinding
        if (!objEvent.getRequestCycle().isRewinding())
            return;

        // Save the session state of the table model
        // This is the moment after changes and right before committing the session
        saveSessionState();
    }

    /**
     *  Saves the table state using the SessionStateManager to determine 
     *  what to save and the SessionStoreManager to determine where to save it.  
     *
     */
    protected void saveSessionState()
    {
        ITableModel objModel = getTableModel();
        Serializable objState = getTableSessionStateManager().getSessionState(objModel);
        storeSessionState(objState);
    }

    /**
     *  Loads the table state using the SessionStoreManager.
     * 
     *  @return the stored table state
     */
    protected Serializable loadSessionState()
    {
        ITableSessionStoreManager objManager = getTableSessionStoreManager();
        if (objManager != null)
            return objManager.loadState(getPage().getRequestCycle());
        return getSessionState();
    }

    /**
     *  Stores the table state using the SessionStoreManager.
     * 
     *  @param objState the table state to store
     */
    protected void storeSessionState(Serializable objState)
    {
        ITableSessionStoreManager objManager = getTableSessionStoreManager();
        if (objManager != null)
            objManager.saveState(getPage().getRequestCycle(), objState);
        else
            setSessionState(objState);
    }

    /**
     *  Stores a pointer to this component in the Request Cycle while rendering
     *  so that wrapped components have access to it.
     * 
     *  @see org.apache.tapestry.BaseComponent#renderComponent(IMarkupWriter, IRequestCycle)
     */
    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {
        Object objOldValue = cycle.getAttribute(ITableModelSource.TABLE_MODEL_SOURCE_ATTRIBUTE);
        cycle.setAttribute(ITableModelSource.TABLE_MODEL_SOURCE_ATTRIBUTE, this);

        super.renderComponent(writer, cycle);

        cycle.setAttribute(ITableModelSource.TABLE_MODEL_SOURCE_ATTRIBUTE, objOldValue);
    }

}
