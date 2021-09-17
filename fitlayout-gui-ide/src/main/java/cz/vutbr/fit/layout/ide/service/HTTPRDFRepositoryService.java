/**
 * HTTPRepositoryService.java
 *
 * Created on 16. 11. 2020, 19:09:06 by burgetr
 */
package cz.vutbr.fit.layout.ide.service;

import java.util.ArrayList;
import java.util.List;

import cz.vutbr.fit.layout.api.ArtifactRepository;
import cz.vutbr.fit.layout.api.Parameter;
import cz.vutbr.fit.layout.impl.ParameterString;
import cz.vutbr.fit.layout.rdf.RDFArtifactRepository;
import cz.vutbr.fit.layout.rdf.RDFStorage;

/**
 * 
 * @author burgetr
 */
public class HTTPRDFRepositoryService extends BaseRepositoryService
{
    private String serverUrl;
    private String repositoryId;

    
    public HTTPRDFRepositoryService()
    {
        serverUrl = "http://localhost:8080/rdf4j-server";
        repositoryId = "fitlayout2";
    }

    @Override
    public String getId()
    {
        return "FitLayout.GUI.Repo.HTTP";
    }

    @Override
    public String getName()
    {
        return "RDF remote HTTP repository";
    }

    @Override
    public String getDescription()
    {
        return "A remote RDF4J repository connected via HTTP";
    }

    @Override
    public String getCategory()
    {
        return "Storage";
    }

    public String getServerUrl()
    {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl)
    {
        this.serverUrl = serverUrl;
    }

    public String getRepositoryId()
    {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId)
    {
        this.repositoryId = repositoryId;
    }

    @Override
    public List<Parameter> defineParams()
    {
        List<Parameter> ret = new ArrayList<>(1);
        ret.add(new ParameterString("serverUrl", 0, 64));
        ret.add(new ParameterString("repositoryId", 0, 64));
        return ret;
    }

    @Override
    public ArtifactRepository createRepository()
    {
        RDFStorage storage = RDFStorage.createHTTP(serverUrl, repositoryId);
        return new RDFArtifactRepository(storage);
    }

}

