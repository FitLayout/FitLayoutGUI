/**
 * GUIProcessor.java
 *
 * Created on 5. 2. 2015, 10:59:17 by burgetr
 */
package cz.vutbr.fit.layout.ide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.IRI;

import cz.vutbr.fit.layout.api.AreaTreeOperator;
import cz.vutbr.fit.layout.api.ArtifactService;
import cz.vutbr.fit.layout.api.ServiceManager;
import cz.vutbr.fit.layout.model.AreaTree;
import cz.vutbr.fit.layout.model.Artifact;
import cz.vutbr.fit.layout.provider.OperatorApplicationProvider;

/**
 * 
 * @author burgetr
 */
public class GUIProcessor extends BaseProcessor
{
    //private static Logger log = LoggerFactory.getLogger(GUIProcessor.class);

    private List<AreaTreeOperator> selectedOperators;
    private List<Map<String, Object>> operatorParams;
    
    public GUIProcessor()
    {
        super();
        selectedOperators = new ArrayList<AreaTreeOperator>();
        operatorParams = new ArrayList<Map<String, Object>>();
    }

    @Override
    protected ServiceManager createServiceManager()
    {
        return FLConfig.createServiceManager(null);
    }

    /**
     * Creates a new artifact from the nearest applicable parent using the given provider
     * and adds the new artifact to the artifact tree.
     * @param selected the selected artifact to start with when looking for the nearest applicable parent
     * @param provider the provider to be used for creating the new artifact
     * @return
     */
    public Artifact createArtifact(Artifact selected, ArtifactService provider)
    {
        Artifact parent = null;
        if (provider.getConsumes() != null)
            parent = getNearestArtifact(selected, provider.getConsumes());
        if (parent != null)
        {
            Artifact result = provider.process(parent);
            return result;
        }
        else
            return null;
    }

    public Artifact getParentArtifact(Artifact artifact)
    {
        final IRI parentIri = artifact.getParentIri();
        if (parentIri != null)
            return getRepository().getArtifact(parentIri);
        else
            return null;
    }

    /**
     * Finds the nearest artifact of the given type in the artifact tree.
     * @param start the sartifact to start with
     * @param artifactType the required type
     * @return
     */
    public Artifact getNearestArtifact(Artifact start, IRI artifactType)
    {
        Artifact ret = start;
        while (ret != null && !artifactType.equals(ret.getArtifactType()))
            ret = getParentArtifact(ret);
        return ret;
    }
    
    //========================================================================================
    
    /**
     * Fills the selected operators list according to a given set of operator IDs
     * @param ids the operator IDs to be used
     */
    public void setSelectedOperatorIDs(String[] ids)
    {
        selectedOperators.clear();
        operatorParams.clear();
        for (String id : ids)
        {
            var op = getServiceManager().findParmetrizedService(id);
            if (op != null && op instanceof AreaTreeOperator)
            {
                var params = ServiceManager.getServiceParams(op);
                selectedOperators.add((AreaTreeOperator) op);
                operatorParams.add(params);
            }
        }
    }
    
    /**
     * Gets the list of operators selected using the GUI.
     * @return a list of operators
     */
    public List<AreaTreeOperator> getSelectedOperators()
    {
        return selectedOperators;
    }

    /**
     * Gets the list of operator parametres configured using the GUI.
     * @return a list of operator parameter maps
     */
    public List<Map<String, Object>> getOperatorParams()
    {
        return operatorParams;
    }
    
    /**
     * Configures all the selected operators using their parametres.
     */
    public void configureOperators()
    {
        for (int i = 0; i < selectedOperators.size(); i++)
        {
            var op = selectedOperators.get(i);
            var params = operatorParams.get(i);
            ServiceManager.setServiceParams(op, params);
        }
    }
    
    /**
     * Apply the operators on the given area tree.
     * @param src An area tree
     * @return the same area tree with applied modifications
     */
    public AreaTree applyOperators(AreaTree src)
    {
        for (int i = 0; i < selectedOperators.size(); i++)
        {
            var op = selectedOperators.get(i);
            var params = operatorParams.get(i);
            apply(src, op, params);
        }
        return src;
    }
    
    public AreaTree applyOperatorProvider(Artifact selectedArtifact, OperatorApplicationProvider opProvider)
    {
        // configure the provider
        opProvider.setOperators(selectedOperators);
        // configure the operators
        configureOperators();
        // create the artifact
        Artifact art = createArtifact(selectedArtifact, opProvider);
        return (AreaTree) art;
    }

}
