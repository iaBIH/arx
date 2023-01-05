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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXClassificationConfiguration;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXConfiguration.AnonymizationAlgorithm;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.DataType;
import org.deidentifier.arx.ARXLattice.ARXNode;
import org.deidentifier.arx.ARXPopulationModel;
import org.deidentifier.arx.ARXProcessStatistics;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.io.CSVHierarchyInput;
import org.deidentifier.arx.metric.Metric;
import org.deidentifier.arx.metric.MetricConfiguration;
import org.deidentifier.arx.risk.RiskEstimateBuilder;
import org.deidentifier.arx.risk.RiskModelHistogram;
import org.deidentifier.arx.risk.RiskModelPopulationUniqueness;
import org.deidentifier.arx.risk.RiskModelSampleRisks;
import org.deidentifier.arx.risk.RiskModelSampleUniqueness;
import org.eclipse.swt.widgets.List;

import cern.colt.Arrays;

/**
 * This class implements an example on how to compare data mining performance
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class EJPRD extends Example {
    
    /**
     * Loads a dataset from disk
     * @param dataset
     * @return
     * @throws IOException
     */
    
    /**
     * Entry point.
     * 
     * @param args the arguments
     * @throws ParseException
     * @throws IOException
     */
    public static void main(String[] args) throws ParseException, IOException {
        
        //TODO: fix results are different:
        // checkk if the data format ant the hierarchies assigned correctly  
        // check GUI code for setting
        // check other examples
          
        String dataFilePath           = "../ejp-rd-small-datasets/arx_projects/rdDataSmallMinCols.csv";
        String hierarchyFolderPath    = "../ejp-rd-small-datasets/arx_projects/rdDataSmallMinColsSimple_hierarchies";
        String anonymizedDataFilePath = "../ejp-rd-small-datasets/arx_projects/rdDataSmallMinCols_anonymized.csv";
        String lrResultFilePath       = "../ejp-rd-small-datasets/arx_projects/rdDataSmallMinCols_LR.txt";
        System.out.println("Read the data from a csv file ...............");
        Data data = Data.create(dataFilePath, StandardCharsets.UTF_8, ';');
        
        System.out.println("Read all generalization hierarchies in a folder ...............");
        FilenameFilter hierarchyFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {  
                if (name.matches("(.*)_hierarchy.csv")) {
                    return true;
                } else {
                    return false;
                }
            }
        };        

        // read from csv
        ArrayList dataAtributes = new ArrayList(); 
        for (int i=0; i< data.getHandle().getNumColumns(); i++) {
            dataAtributes.add(data.getHandle().getAttributeName(i));   
        }

        String clazz = "hasRD";
        data.getDefinition().setAttributeType(clazz, AttributeType.INSENSITIVE_ATTRIBUTE);
        data.getDefinition().setResponseVariable(clazz, true);

        // Hierarchies   
        File hierarchyFolder = new File(hierarchyFolderPath);
        File[] genHierFiles = hierarchyFolder.listFiles(hierarchyFilter);

        // Using all attributes except the class as features
        // TODO: support multiple classes 
        String[] features = new String[data.getHandle().getNumColumns()-1];

        int fCount =0;
        for (int j=0; j< data.getHandle().getNumColumns(); j++) {
            // add all attributes except class to featuers array
            String currentAttribut =dataAtributes.get(j).toString();
            if (! currentAttribut.equals(clazz)) {
                features[fCount]= currentAttribut;                
                //System.out.println(features[fCount]);
                // set as quasi identifiers
                data.getDefinition().setAttributeType(currentAttribut, AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
                
                // Add hierarchy 
                Pattern pattern = Pattern.compile("(.*)__"+currentAttribut+"_hierarchy.csv");
                for (File file : genHierFiles) {
                    //System.out.println(file.getName());
                    Matcher matcher = pattern.matcher(file.getName());
                    if (matcher.find()) {
                        CSVHierarchyInput hier = new CSVHierarchyInput(file, StandardCharsets.UTF_8, ';');
                        String attributeName = matcher.group(1);
                        data.getDefinition().setAttributeType(currentAttribut, Hierarchy.create(hier.getHierarchy()));
                        System.out.println(currentAttribut + " : "+file.getName());
                        // check if attributes are defined correctly
                        // "zip-code" "race" "sex" "native-country" "hasCD" "RDnames" "recoveredRD"
                        // "birthDate" "diagDate" "isAlive"        

//                        if (currentAttribut.equals("diagDate")) { 
//                            for (int k1=0; k1<data.getDefinition().getHierarchy(currentAttribut).length;k1++) {
//                                for (int k2=0; k2<data.getDefinition().getHierarchy(currentAttribut)[k1].length;k2++) {
//                                     System.out.print(data.getDefinition().getHierarchy(currentAttribut)[k1][k2]+" ");
//                                }
//                                System.out.println();
//                            }
//                            System.exit(1);
//                        }
                        

                    }
                }

                
                fCount++;
                // fix date data type
                if (currentAttribut.contains("Date")) {
                    //TODO: How to change the data format? 
                    data.getDefinition().setDataType(currentAttribut, DataType.DATE);
                }
            }
        }
        
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXConfiguration config = ARXConfiguration.create();
        
        // privacy models, we can add one or more 
        // Some private models requires sensitive attributes or population
        int k = 2;
        config.addPrivacyModel(new KAnonymity(2));
        //Note try::
        // K-map similar to K-anonymity but risks based onpopulation
        // Average risk: marketing
        // Population uniqueness: marketing
        // Sample uniqueness: protect unique records  
        // δ-Presence: membership 
        // ℓ-Diversity: attribute
        // β-Likeness: attribute (replaces t-closeness and delta-disclosure) 
        // Profitability
        // Differential privacy
        config.setSuppressionLimit(1d);
        
        // quality model
        config.setQualityModel(Metric.createLossMetric());
        config.getQualityModel().getConfiguration().setGsFactor(0.5);
        // target attribute already set in the data section above 
        
        //algorithm 
        // Note: optimal only works for a defined search space (default 100000)
        //       search space size = attributes ^ max generation levels 
        //config.setAlgorithm(AnonymizationAlgorithm.BEST_EFFORT_GENETIC);
        config.setAlgorithm(AnonymizationAlgorithm.OPTIMAL);
        
        // Save the project (only available in GUI)
        
        ARXResult result = anonymizer.anonymize(data, config);
        //ARXNode optimum = result.getGlobalOptimum();
        //System.out.println(" - Information loss: " + result.getGlobalOptimum().getLowestScore() + " / " + result.getGlobalOptimum().getHighestScore());
        
        
        ARXProcessStatistics stats = result.getProcessStatistics(); 

        // TODO: apply specific transformation
        
        // print optimal transformation info:
        System.out.println(" ----------------  Info. -------------------");
        System.out.println("Execution time          : " + (double)stats.getDuration() / 1000d + " seconds");
        System.out.println("All Transformations     : " + stats.getTransformationsAvailable());
        System.out.println("Selected Transformation : " + Arrays.toString(stats.getStep(0).getTransformation()));
        System.out.println("Score                   : " + stats.getStep(0).getScore().toString());

        //printResult(result, data);
        
        //apply specifictransformation from the GUI
        //int [] arxTransformation = new int[]{2,1,0,2,2,0,2,0,2,2,0};
        //ARXNode arxTransformationNode = result.getLattice().getNode(arxTransformation);
        int [] arxTransformation = result.getLattice().getTop().getTransformation();
        for (int i=0;i<arxTransformation.length ;i++) {
           System.out.print(arxTransformation[i] + " , ");
        }
        System.out.println();
        ARXNode arxTransformationNode = result.getLattice().getNode(arxTransformation);
        result.getOutput(arxTransformationNode);
        
        // Save anonymized data to csv file 
        result.getOutput(false).save(anonymizedDataFilePath, ';');

        //TODO: save qulity and risk results 
        // check the gui code 
        ARXClassificationConfiguration configLR = ARXClassificationConfiguration.createLogisticRegression();
        configLR.setNumFolds(10); // 5 folds
        System.out.println(k + "-anonymous dataset (logistic regression)");
        String lrResult= result.getOutput().getStatistics().getClassificationPerformance(features, clazz, configLR).toString();
        System.out.println(lrResult);
        try (
                PrintWriter lrResultWriter = new PrintWriter(lrResultFilePath)) {
                lrResultWriter.println(lrResult);
        }
        //result.getLattice().getTop().getTransformation().
        //TODO: print anonymisation process info
        //TODO: print results info e.g data analysis and risk analysis 
        
        /*
        System.out.println("5-anonymous dataset (naive bayes)");
        System.out.println(result.getOutput().getStatistics().getClassificationPerformance(features, clazz, ARXClassificationConfiguration.createNaiveBayes()));
        System.out.println("5-anonymous dataset (random forest)");
        System.out.println(result.getOutput().getStatistics().getClassificationPerformance(features, clazz, ARXClassificationConfiguration.createRandomForest()));
        */
        
        DataHandle inHandle  = data.getHandle();
        DataHandle outHandle = result.getOutput(false);

        ARXPopulationModel populationmodel = ARXPopulationModel.create(ARXPopulationModel.Region.USA);
        RiskEstimateBuilder builder = inHandle.getRiskEstimator(populationmodel);
        RiskModelHistogram classes = builder.getEquivalenceClassModel();
        RiskModelSampleRisks sampleReidentifiationRisk = builder.getSampleBasedReidentificationRisk();
        RiskModelSampleUniqueness sampleUniqueness = builder.getSampleBasedUniquenessRisk();
        RiskModelPopulationUniqueness populationUniqueness = builder.getPopulationBasedUniquenessRisk();
        
        int[] histogram = classes.getHistogram();
        
        System.out.println("   * Equivalence classes:");
        System.out.println("     - Average size: " + classes.getAvgClassSize());
        System.out.println("     - Num classes : " + classes.getNumClasses());
        System.out.println("     - Histogram   :");
        for (int i = 0; i < histogram.length; i += 2) {
            System.out.println("        [Size: " + histogram[i] + ", count: " + histogram[i + 1] + "]");
        }
        System.out.println("   * Risk estimates:");
        System.out.println("     - Sample-based measures");
        System.out.println("       + Average risk     : " + sampleReidentifiationRisk.getAverageRisk());
        System.out.println("       + Lowest risk      : " + sampleReidentifiationRisk.getLowestRisk());
        System.out.println("       + Tuples affected  : " + sampleReidentifiationRisk.getFractionOfRecordsAffectedByLowestRisk());
        System.out.println("       + Highest risk     : " + sampleReidentifiationRisk.getHighestRisk());
        System.out.println("       + Tuples affected  : " + sampleReidentifiationRisk.getFractionOfRecordsAffectedByHighestRisk());
        System.out.println("       + Sample uniqueness: " + sampleUniqueness.getFractionOfUniqueRecords());
        System.out.println("     - Population-based measures");
        System.out.println("       + Population unqiueness (Zayatz): " + populationUniqueness.getFractionOfUniqueTuples(RiskModelPopulationUniqueness.PopulationUniquenessModel.ZAYATZ));

        
        System.out.println("All done!");

    }
}
