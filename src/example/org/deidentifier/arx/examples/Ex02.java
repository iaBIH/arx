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

package org.deidentifier.arx.examples;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.AttributeType.Hierarchy.DefaultHierarchy;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.DataType;
import org.deidentifier.arx.Data.DefaultData;
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.criteria.RecursiveCLDiversity;
import org.deidentifier.arx.metric.Metric;

/**
 * This class implements an example on:
 *  - Simple anonymization 
 *  - Using data in different ways:
 *       1. Create froma list
 *       2. Import from a file
 *       3. Import from DB ???   
 *  - Using different privacy models
 *  
 *  Note: this replaces examples 1,2,3,4,5,6  
 *  
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 * @author Ibraheem Al-Dhamari
 */
public class Ex02 extends Example {

    /**
     * Entry point.
     * 
     * @param args
     *            the arguments
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {

        // Global variables to control the example options
        
        
        // which privacy model
        int isKanonymity           = 3; 
        // Privacy model K-anonymity parameter 
        // the identification risk  = 1/K 
        int K = 3 ; 

        int isRecursiveCLDiversity = 0; // it needs sensitive attribute
        // Privacy model RecursiveCLDiversity parameters C, L 
        int C = 3; 
        int L = 2;   
        
        // Privacy model t-closeness 
        int isHierarchicalDistanceTCloseness =1;        
        double t = 0.6d; // parameter t 
        
        // create data or import from file
        int dataFromFile        = 0;
        
        // modify age and gender   
        int modifyAttributeType   = 1;
        
        //Quality models
        int usecreateHeightMetric = 0;
        int useEntropyMetric =1 ;

        // file paths 
        String dataFilePath             = "data/test.csv";
        String ageHierarchyFilePath     = "data/test_hierarchy_age.csv";
        String genderHierarchyFilePath  = "data/test_hierarchy_gender.csv";
        String zipcodeHierarchyFilePath = "data/test_hierarchy_zipcode.csv";
        
        String anonymizedDataFilePath   = "data/test_anonymized.csv";
        // Define data
        
        DefaultData data1 = Data.create();
        Data        data2 = Data.create(dataFilePath, StandardCharsets.UTF_8, ';');
        Data data; 
        
        // needed for t-closness 
        DefaultHierarchy diseaseHierarchy = Hierarchy.create();
        
        if (dataFromFile == 0) {                       
            // Define data
            data1.add("zipcode", "age", "disease");
            data1.add("47677",   "29", "gastric ulcer");
            data1.add("47602",   "22", "gastritis");
            data1.add("47678",   "27", "stomach cancer");
            data1.add("47905",   "43", "gastritis");
            data1.add("47909",   "52", "flu");
            data1.add("47906",   "47", "bronchitis");
            data1.add("47605",   "30", "bronchitis");
            data1.add("47673",   "36", "pneumonia");
            data1.add("47607",   "32", "stomach cancer");

            // Define hierarchies
            DefaultHierarchy ageHierarchy = Hierarchy.create();
            ageHierarchy.add("29", "<=40", "*");
            ageHierarchy.add("22", "<=40", "*");
            ageHierarchy.add("27", "<=40", "*");
            ageHierarchy.add("43", ">40", "*");
            ageHierarchy.add("52", ">40", "*");
            ageHierarchy.add("47", ">40", "*");
            ageHierarchy.add("30", "<=40", "*");
            ageHierarchy.add("36", "<=40", "*");
            ageHierarchy.add("32", "<=40", "*");

            // Only excerpts for readability
            DefaultHierarchy zipcodeHierarchy = Hierarchy.create();
            zipcodeHierarchy.add("47677", "4767*", "476**", "47***", "4****", "*****");
            zipcodeHierarchy.add("47602", "4760*", "476**", "47***", "4****", "*****");
            zipcodeHierarchy.add("47678", "4767*", "476**", "47***", "4****", "*****");
            zipcodeHierarchy.add("47905", "4790*", "479**", "47***", "4****", "*****");
            zipcodeHierarchy.add("47909", "4790*", "479**", "47***", "4****", "*****");
            zipcodeHierarchy.add("47906", "4790*", "479**", "47***", "4****", "*****");
            zipcodeHierarchy.add("47605", "4760*", "476**", "47***", "4****", "*****");
            zipcodeHierarchy.add("47673", "4767*", "476**", "47***", "4****", "*****");
            zipcodeHierarchy.add("47607", "4760*", "476**", "47***", "4****", "*****");

            // Define sensitive value hierarchy
            diseaseHierarchy.add("flu","respiratory infection","vascular lung disease","respiratory & digestive system disease");
            diseaseHierarchy.add("pneumonia","respiratory infection","vascular lung disease","respiratory & digestive system disease");
            diseaseHierarchy.add("bronchitis","respiratory infection","vascular lung disease","respiratory & digestive system disease");
            diseaseHierarchy.add("pulmonary edema","vascular lung disease","vascular lung disease","respiratory & digestive system disease");
            diseaseHierarchy.add("pulmonary embolism","vascular lung disease","vascular lung disease","respiratory & digestive system disease");
            diseaseHierarchy.add("gastric ulcer","stomach disease","digestive system disease","respiratory & digestive system disease");
            diseaseHierarchy.add("stomach cancer","stomach disease","digestive system disease","respiratory & digestive system disease");
            diseaseHierarchy.add("gastritis","stomach disease","digestive system disease","respiratory & digestive system disease");
            diseaseHierarchy.add("colitis","colon disease","digestive system disease","respiratory & digestive system disease");
            diseaseHierarchy.add("colon cancer","colon disease","digestive system disease","respiratory & digestive system disease");

            data1.getDefinition().setAttributeType("age",     ageHierarchy);
            data1.getDefinition().setAttributeType("zipcode", zipcodeHierarchy);
            //data1.getDefinition().setAttributeType("disease", diseaseHierarchy);
            data1.getDefinition().setAttributeType("disease", AttributeType.SENSITIVE_ATTRIBUTE);
            
            data = data1;

        } else {            
            // Define input files
            data2.getDefinition().setAttributeType("age", Hierarchy.create(ageHierarchyFilePath, StandardCharsets.UTF_8, ';'));
            data2.getDefinition().setAttributeType("gender", Hierarchy.create(genderHierarchyFilePath, StandardCharsets.UTF_8, ';'));
            data2.getDefinition().setAttributeType("zipcode", Hierarchy.create(zipcodeHierarchyFilePath, StandardCharsets.UTF_8, ';'));

            data = data2;
        }

        // Create an instance of the anonymizer
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(K));

        if (isHierarchicalDistanceTCloseness ==1){
           config.addPrivacyModel(new HierarchicalDistanceTCloseness("disease", t , diseaseHierarchy));
        }
        config.setSuppressionLimit(0d);
        
        if (useEntropyMetric == 1) {
            config.setQualityModel(Metric.createEntropyMetric());
        }
        // Execute the algorithm
        ARXResult result  = anonymizer.anonymize(data, config); 
                
        // Print info
        printResult(result, data);
        
        // Print results
        System.out.println(" - Transformed data:");
        Iterator<String[]> transformed = result.getOutput(false).iterator();
        while (transformed.hasNext()) {
            System.out.print("   ");
            System.out.println(Arrays.toString(transformed.next()));
        }
        
        // Save result
        System.out.print(" - Writing data...");
        result.getOutput(false).save(anonymizedDataFilePath, ';');
        System.out.println("Done!");

    }
}
