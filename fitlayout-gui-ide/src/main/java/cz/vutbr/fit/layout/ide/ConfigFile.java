/**
 * ConfigFile.java
 *
 * Created on 15. 11. 2020, 21:01:06 by burgetr
 */
package cz.vutbr.fit.layout.ide;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cz.vutbr.fit.layout.ide.config.IdeConfig;

/**
 * A config file I/O
 * @author burgetr
 */
public class ConfigFile
{
    public static final String DefaultConfigLocation = Browser.configDir + "/gui-config.json";
    
    private File file;
    private IdeConfig loadedConfig;
    
    
    public ConfigFile()
    {
        this(DefaultConfigLocation);
    }
    
    public ConfigFile(String path)
    {
        file = new File(path);
        File dir = file.getParentFile();
        if (dir != null)
        {
            if (!dir.exists())
                dir.mkdir();
        }
    }
    
    public void save(IdeConfig config) throws IOException
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(config);
        FileWriter save = new FileWriter(file);
        save.write(json);
        save.close();
    }
    
    public IdeConfig load() throws IOException
    {
        FileReader fin = new FileReader(file);
        Gson gson = new Gson();
        IdeConfig config = gson.fromJson(fin, IdeConfig.class);
        loadedConfig = config;
        fin.close();
        return config;
    }
    
    public IdeConfig getLoadedConfig()
    {
        return loadedConfig;
    }
    
}
