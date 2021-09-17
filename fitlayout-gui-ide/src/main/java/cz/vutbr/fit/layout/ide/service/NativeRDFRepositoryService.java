/**
 * NativeRDFRepositoryService.java
 *
 * Created on 16. 11. 2020, 10:38:37 by burgetr
 */
package cz.vutbr.fit.layout.ide.service;

import java.util.ArrayList;
import java.util.List;

import cz.vutbr.fit.layout.api.ArtifactRepository;
import cz.vutbr.fit.layout.api.Parameter;
import cz.vutbr.fit.layout.ide.Browser;
import cz.vutbr.fit.layout.impl.ParameterString;
import cz.vutbr.fit.layout.rdf.RDFArtifactRepository;
import cz.vutbr.fit.layout.rdf.RDFStorage;

/**
 * 
 * @author burgetr
 */
public class NativeRDFRepositoryService extends BaseRepositoryService
{
    private String dataDir;

    
    public NativeRDFRepositoryService()
    {
        dataDir = Browser.configDir + "/storage";
    }

    @Override
    public String getId()
    {
        return "FitLayout.GUI.Repo.Native";
    }

    @Override
    public String getName()
    {
        return "RDF native repository";
    }

    @Override
    public String getDescription()
    {
        return "A RDF4J native repository";
    }

    @Override
    public String getCategory()
    {
        return "Storage";
    }

    public String getDataDir()
    {
        return dataDir;
    }

    public void setDataDir(String dataDir)
    {
        this.dataDir = dataDir;
    }

    @Override
    public List<Parameter> defineParams()
    {
        List<Parameter> ret = new ArrayList<>(1);
        ret.add(new ParameterString("dataDir", 0, 64));
        return ret;
    }

    @Override
    public ArtifactRepository createRepository()
    {
        RDFStorage storage = RDFStorage.createNative(dataDir);
        return new RDFArtifactRepository(storage);
    }

}
