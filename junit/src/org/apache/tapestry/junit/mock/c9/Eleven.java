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

package org.apache.tapestry.junit.mock.c9;

import org.apache.tapestry.ILocatable;
import org.apache.tapestry.html.BasePage;

/**
 *  Testing for correct operation with array properties
 *
 *  @author Howard Lewis Ship
 *  @version $Id$
 *  @since 3.0
 *
 **/

public abstract class Eleven extends BasePage implements ILocatable
{

    public abstract int getIntValue();
    public abstract String[] getStringArrayValue();
    
    public String getStringValue()
    {
        int index = getIntValue();
        return getStringArrayValue()[index];
    }

}
