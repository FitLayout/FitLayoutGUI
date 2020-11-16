/**
 * RepositoryService.java
 *
 * Created on 16. 11. 2020, 11:22:23 by burgetr
 */
package cz.vutbr.fit.layout.ide.service;

import cz.vutbr.fit.layout.api.ArtifactRepository;
import cz.vutbr.fit.layout.api.ParametrizedOperation;

/**
 * 
 * @author burgetr
 */
public interface RepositoryService extends ParametrizedOperation
{
    
    /**
     * Creates the repository according to the configured parametres.
     * @return the configured repository
     */
    public ArtifactRepository createRepository();

}
