/**
 * RepositoryService.java
 *
 * Created on 16. 11. 2020, 10:19:42 by burgetr
 */
package cz.vutbr.fit.layout.ide.service;

import cz.vutbr.fit.layout.impl.BaseParametrizedOperation;

/**
 * A base class for repository configuration services. 
 * 
 * @author burgetr
 */
public abstract class BaseRepositoryService extends BaseParametrizedOperation implements RepositoryService
{

    @Override
    public String toString()
    {
        return getName();
    }
    
}
