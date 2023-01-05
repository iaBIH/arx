/*
 * ARX Data Anonymization Tool
 * Copyright 2012 - 2022 Fabian Prasser and contributors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.deidentifier.arx.gui.view.impl.menu;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.gui.resources.Resources;

/**
 * Configuration for the help dialog. Stores help topics and associated URLs
 * @author Fabian Prasser
 */
public class DialogHelpConfig {

    
    
    public String version = ARXAnonymizer.VERSION;

    
    /** The global help variables*/
    // Default: use online help from ARX website
    // To use local help change to false
    public boolean isOnlineHelp = true ; //Boolean.parseBoolean(this.getHelpConfig.get("DialogHelp.OnlineHelp"));

    
    // help files should be placed in a help folder inside the repository e.g. <github-repository>/help/anonymization.html
    // or in  a folder help next to the generated jar file e.g. help/anonymization.html       
    public String helpWebSite =  "http://arx.deidentifier.org/help/v"; //Resources.getMessage("DialogHelp.4");;

    
    /**
     * An entry in the help dialog.
     *
     * @author Fabian Prasser
     */
    public static class Entry {
        
        /** ID */
        public final String id;
        
        /** Title */
        public final String title;
        
        /** URL */
        public final String url;

        /**
         * Creates a new entry.
         *
         * @param id
         * @param title
         * @param url
         */
        private Entry(String id, String title, String url) {
            this.id = id;
            this.title = title;
            this.url = url;
        }
    }
    
    /** Entries */
    private List<Entry> entries = new ArrayList<Entry>();
    
    /**
     * Creates a new config.
     */
    public DialogHelpConfig() {
        
        helpWebSite =  getHelpConfig().get("DialogHelp.4");
        isOnlineHelp = Boolean.parseBoolean(getHelpConfig().get("DialogHelp.OnlineHelp"));
                
        if (!isOnlineHelp) {
        	helpWebSite = "file:///" +  new File("help").getAbsolutePath();  
        	version = "";
        }
        
        // Get all keys for the help web pages    
        List<String> configList = new ArrayList<String>();
        for (String key : getHelpConfig().keySet()) {
            if (key.contains("DialogHelpConfig")){
               configList.add(key.substring(17));
            }
        }
        
        // Sorting
        configList.sort(null);
        
        // Create the help entries
        for (String idx : configList) {
          entries.add(new Entry("id." + idx, //$NON-NLS-1$
                   getHelpConfig().get("DialogHelpConfig." + idx), //$NON-NLS-1$
                   helpWebSite + version + getHelpConfig().get("DialogHelpPage." + idx))); //$NON-NLS-1$

        }
    }
    
    
    /**
     * Read help config file and return a sorted dictonary
     *
     * @return
     */
    public Map<String, String> getHelpConfig () {
        // Read the help config and create a dictionary 
         Map<String, String> helpConfig = new HashMap<String, String>();
        try {
            List<String> allLines = Files.readAllLines(Paths.get("config/ARX_Help.conf"), StandardCharsets.UTF_8);
            for (String line : allLines) {
                helpConfig.put(line.split("=")[0], line.split("=")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return helpConfig;

    }
    
    /**
     * Returns all entries.
     *
     * @return
     */
    public List<Entry> getEntries() {
        return this.entries;
    }
    
    /**
     * Returns the index for a given ID.
     *
     * @param id
     * @return
     */
    public int getIndexForId(String id) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).id.equals("id."+id)) {
                return i;
            }
        }
        return 0;
    }
    
    /**
     * Returns the index of a given URL.
     *
     * @param url
     * @return
     */
    public int getIndexForUrl(String url) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).url.equals(url)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Returns the URL for a given index.
     *
     * @param index
     * @return
     */
    public String getUrlForIndex(int index) {
        return entries.get(index).url;
    }
}
