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
public class Ex01 extends Example {

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
        int isKanonymity           = 1; 
        // Privacy model K-anonymity parameter 
        // the identification risk  = 1/K 
        int K = 3 ; 

        int isRecursiveCLDiversity = 1; // it needs sensitive attribute
        // Privacy model RecursiveCLDiversity parameters C, L 
        int C = 3; 
        int L = 2;         
        
        // create data or import from file
        int dataFromFile        = 0;
        
        // modify age and gender   
        int modifyAttributeType   = 1;
        
        //Quality models
        int usecreateHeightMetric = 1;

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
        
        if (dataFromFile == 0) {                       
            // we can add only to default data 
            data1.add("age", "gender", "zipcode");
            data1.add("34", "male", "81667");
            data1.add("45", "female", "81675");
            data1.add("66", "male", "81925");
            data1.add("70", "female", "81931");
            data1.add("34", "female", "81931");
            data1.add("70", "male", "81931");
            data1.add("45", "male", "81931");
                      
            // Define hierarchies
    
            // Only excerpts for readability
            DefaultHierarchy zipcodeHierarchy = Hierarchy.create();
            zipcodeHierarchy.add("81667", "8166*", "816**", "81***", "8****", "*****");
            zipcodeHierarchy.add("81675", "8167*", "816**", "81***", "8****", "*****");
            zipcodeHierarchy.add("81925", "8192*", "819**", "81***", "8****", "*****");
            zipcodeHierarchy.add("81931", "8193*", "819**", "81***", "8****", "*****");
            
            data1.getDefinition().setAttributeType("zipcode", zipcodeHierarchy);
            data1.getDefinition().setDataType("zipcode", DataType.DECIMAL);
            
            if (usecreateHeightMetric==1) {
                // set the minimal generalization height
                data1.getDefinition().setMinimumGeneralization("zipcode", 3);
                data1.getDefinition().setMaximumGeneralization("zipcode", 3);
            }
            if (modifyAttributeType == 1){
                // Usually attributes are quasi-identifiers by default
                // other types do not need  hierarchy  
                
                if (isRecursiveCLDiversity ==1){
                   data1.getDefinition().setAttributeType("age", AttributeType.SENSITIVE_ATTRIBUTE);
                 } else {
                   data1.getDefinition().setAttributeType("age", AttributeType.IDENTIFYING_ATTRIBUTE);     
                 }
                data1.getDefinition().setDataType("age", DataType.INTEGER);

                data1.getDefinition().setAttributeType("gender", AttributeType.INSENSITIVE_ATTRIBUTE);
                data1.getDefinition().setDataType("gender", DataType.STRING);
            } else {
                DefaultHierarchy ageHierarchy = Hierarchy.create();
                ageHierarchy.add("34", "<50", "*");
                ageHierarchy.add("45", "<50", "*");
                ageHierarchy.add("66", ">=50", "*");
                ageHierarchy.add("70", ">=50", "*");
                data1.getDefinition().setAttributeType("age", ageHierarchy);
        
                DefaultHierarchy genderHierarchy = Hierarchy.create();
                genderHierarchy.add("male", "*");
                genderHierarchy.add("female", "*");

                data1.getDefinition().setAttributeType("gender", genderHierarchy);
                
                if (usecreateHeightMetric==1) {
                    // set the minimal generalization height
                    data1.getDefinition().setMinimumGeneralization("gender", 1);
                }
            }
            
            data = data1;

        } else {            
            // Define input files
            data2.getDefinition().setAttributeType("age", Hierarchy.create(ageHierarchyFilePath, StandardCharsets.UTF_8, ';'));
            data2.getDefinition().setAttributeType("gender", Hierarchy.create(genderHierarchyFilePath, StandardCharsets.UTF_8, ';'));
            data2.getDefinition().setAttributeType("zipcode", Hierarchy.create(zipcodeHierarchyFilePath, StandardCharsets.UTF_8, ';'));

            data = data2;
        }

        // Obtain a handle
        DataHandle inHandle = data.getHandle();

        // Read the encoded data
        System.out.println(" - Input data:");       
        System.out.println("Input Data getNumRows         : " + inHandle.getNumRows());
        System.out.println("Input Data getNumColumns      : "  + inHandle.getNumColumns());
        System.out.println("Input Data getAttributeName 0 : "  + inHandle.getAttributeName(0));
        System.out.println("Input Data getValue 0 0       : "  + inHandle.getValue(0, 0));
        
        // Create an instance of the anonymizer
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(K));
        if (isRecursiveCLDiversity ==1){
           config.addPrivacyModel(new RecursiveCLDiversity("age", C, L));
        }

        config.setSuppressionLimit(0d);

        if (usecreateHeightMetric == 1) {
           config.setQualityModel(Metric.createHeightMetric());
        }
        // Execute the algorithm
        ARXResult result  = anonymizer.anonymize(data, config); 
        
        // Obtain a handle for the transformed data
        DataHandle outHandle = result.getOutput(false);

        // Sort the data. This operation is implicitly performed on both
        // representations of the dataset.
        outHandle.sort(false, 2);

        System.out.println(" - Output data:");       
        System.out.println("Output Data getNumRows         : "  + outHandle.getNumRows());
        System.out.println("Output Data getNumColumns      : "  + outHandle.getNumColumns());
        System.out.println("Output Data getAttributeName 0 : "  + outHandle.getAttributeName(0));
        System.out.println("Output Data getValue 0 0       : "  + outHandle.getValue(0, 0));

        
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
