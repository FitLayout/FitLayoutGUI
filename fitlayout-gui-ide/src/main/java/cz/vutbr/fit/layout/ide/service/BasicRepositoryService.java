/**
 * BasicRepositoryService.java
 *
 * Created on 16. 11. 2020, 10:22:34 by burgetr
 */
package cz.vutbr.fit.layout.ide.service;

import cz.vutbr.fit.layout.api.ArtifactRepository;
import cz.vutbr.fit.layout.impl.DefaultArtifactRepository;

/**
 * 
 * @author burgetr
 */
public class BasicRepositoryService extends BaseRepositoryService
{
    //this service always reuses a single default repository
    public static final ArtifactRepository defaultRepository = new DefaultArtifactRepository();
    

    @Override
    public String getId()
    {
        return "FitLayout.GUI.Repo.Basic";
    }

    @Override
    public String getName()
    {
        return "Basic in-memory repository";
    }

    @Override
    public String getDescription()
    {
        return "A default basic in-memory repository";
    }

    @Override
    public ArtifactRepository createRepository()
    {
        return defaultRepository;
    }

}
