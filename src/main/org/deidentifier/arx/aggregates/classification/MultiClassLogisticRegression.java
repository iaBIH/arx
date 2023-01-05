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
package org.deidentifier.arx.aggregates.classification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.mahout.classifier.sgd.ElasticBandPrior;
import org.apache.mahout.classifier.sgd.L1;
import org.apache.mahout.classifier.sgd.L2;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.classifier.sgd.PriorFunction;
import org.apache.mahout.classifier.sgd.UniformPrior;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.vectorizer.encoders.ConstantValueEncoder;
import org.apache.mahout.vectorizer.encoders.StaticWordValueEncoder;
import org.deidentifier.arx.DataHandleInternal;
import org.deidentifier.arx.aggregates.ClassificationConfigurationLogisticRegression;
import org.deidentifier.arx.common.WrappedBoolean;
import org.hamcrest.core.IsNull;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.collect.Iterators;

/**
 * Implements a classifier
 * @author Fabian Prasser
 */
public class MultiClassLogisticRegression extends ClassificationMethod {

    /** Config */
    private final ClassificationConfigurationLogisticRegression config;
    /** Encoder */
    private final ConstantValueEncoder                          interceptEncoder;
    /** Instance */
    private final OnlineLogisticRegression                      lr;
    /** Specification */
    private final ClassificationDataSpecification               specification;
    /** Encoder */
    private final StaticWordValueEncoder                        wordEncoder;
    /** Input handle */
    private final DataHandleInternal                            inputHandle;

    /** Data */
    private List<Vector>                                              features = new ArrayList<Vector>();
    /** Data */
    private IntArrayList                                        classes  = new IntArrayList();

    /**
     * Creates a new instance
     * @param interrupt
     * @param specification
     * @param config
     * @param inputHandle
     */
    public MultiClassLogisticRegression(WrappedBoolean interrupt,
                                        ClassificationDataSpecification specification,
                                        ClassificationConfigurationLogisticRegression config,
                                        DataHandleInternal inputHandle) {

        super(interrupt);
        
        // Store
        this.config = config;
        this.specification = specification;
        this.inputHandle = inputHandle;
        // Prepare classifier
        PriorFunction prior = null;
        switch (config.getPriorFunction()) {
        case ELASTIC_BAND:
            prior = new ElasticBandPrior();
            break;
        case L1:
            prior = new L1();
            break;
        case L2:
            prior = new L2();
            break;
        case UNIFORM:
            prior = new UniformPrior();
            break;
        default:
            throw new IllegalArgumentException("Unknown prior function");
        }
        this.lr = new OnlineLogisticRegression(this.specification.classMap.size(), config.getVectorLength(), prior);
        
        // Configure
        this.lr.learningRate(config.getLearningRate());
        this.lr.alpha(config.getAlpha());
        this.lr.lambda(config.getLambda());
        this.lr.stepOffset(config.getStepOffset());
        this.lr.decayExponent(config.getDecayExponent());    
        
        // Prepare encoders
        this.interceptEncoder = new ConstantValueEncoder("intercept");
        this.wordEncoder = new StaticWordValueEncoder("feature");
    }

    @Override
    public ClassificationResult classify(DataHandleInternal features, int row) {
        return new MultiClassLogisticRegressionClassificationResult(lr.classifyFull(encodeFeatures(features, row, true)), specification.classMap);
    }

    @Override
    public void close() {
        lr.close();
//        for (int i = 0; i<this.features.size(); i++ ) {
//          System.out.println(this.features.get(i));  
//        }
    }

    @Override
    public void train(DataHandleInternal features, DataHandleInternal clazz, int row) {     

        // How to distinguish between input data  and anonymized data
           // from StatisticsClassification
              // inputClassifier.train (inputHandle,  outputHandle, index);
              // inputZeroR.train      (inputHandle,  outputHandle, index);
              // outputClassifier.train(outputHandle, outputHandle, index);
        
        //preprocess
        int eClazz       = encodeClass   (clazz,    row);
        Vector eFeatures = encodeFeatures(features, row, false);
       
        // try offline training
        this.features.add(eFeatures);
        this.classes.add(eClazz);

//        for (int i = 0; i<features.getNumColumns(); i++ ) {
//            System.out.print(features.getValue(row, i) + "\t");  
//        }
//        System.out.println(clazz.getValue(row,7));
//        for (int i = 0; i<features.getNumColumns(); i++ ) {
//            System.out.print(eFeatures.get(i)+ "\t");  
//        }
//        System.out.println(eClazz);        
        
        //System.exit(1);
        // train using the current row
        lr.train(eClazz, eFeatures);
    }

    /**
     * Encodes a class
     * @param handle
     * @param row
     * @return
     */
    private int encodeClass(DataHandleInternal handle, int row) {
        // classMap Map<String, Integer>
        // the handle here is for the output data 
        
        // String inputValue = handle.getValue(row, specification.classIndex, true);
        final int outputCol = 7;
        String inputValue = handle.getValue(row, outputCol , true);
        //System.out.println(" specification.classIndex: " + specification.classIndex );
        int encClazz = 1000;
        // it seems the output is null for non class index 
        if ( specification.classMap.get(inputValue) != null ){
            encClazz = specification.classMap.get(inputValue);
            //System.out.println(" class inputValue: " + inputValue + "\t\t class output value: "+ encClazz );
        }else {
            //System.out.println(" class inputValue: " + inputValue +" \t\t output is null!");
        }    
        //System.exit(1);
        
        return encClazz;
    }

    /**
     * Encodes a feature
     * @param handle
     * @param row
     * @param classify: is this training or classification
     * @return
     */
    private Vector encodeFeatures(DataHandleInternal handle, int row, boolean classify) {

        // Prepare
        DenseVector vector = new DenseVector(config.getVectorLength());
        //System.out.println("config.getVectorLength() : " + config.getVectorLength());

        interceptEncoder.addToVector("1", vector);
        
        // Special case where there are no features
        if (specification.featureIndices.length == 0) {
            wordEncoder.addToVector("Feature:1", 1, vector);
            return vector;
        }
        
        // For each attribute
        int count = 0;
        // for each feature attribute
        for (int index : specification.featureIndices) {
            
            //System.out.println("index : " + index +"  length: " + specification.featureIndices.length);

            // Obtain data
            ClassificationFeatureMetadata metadata = specification.featureMetadata[count];
            //System.out.println("Attribute: " + metadata.getName() );

            String value = null;
            // if classification: use input handle
            if (classify && metadata.isNumericMicroaggregation()) {
                value = inputHandle.getValue(row, index, true);
                //System.out.println("value inputHandle: " + inputHandle.getValue(row, index, true) );

            } else {
                // if training: use handle (could be input or anonymized data) 
                value = handle.getValue(row, index, true);
            }
            // TODO: how to find are we dealing with input or output?
            Double numeric = metadata.getNumericValue(value);
            if (Double.isNaN(numeric)) {    
                // Adding 1 to the vector
                wordEncoder.addToVector("Attribute-" + index + ":" + value, 1, vector);
                //System.out.println("wordEncoder   value: " + "Attribute-" + index + " : " + value);

            } else {
                // Adding the numeric to the vector
                wordEncoder.addToVector("Attribute-" + index, numeric, vector);
                //System.out.println("wordEncoder  numeric: " + "Attribute-" + index +" : "+ numeric);
            }
            count++;
            if (count>12){
                System.out.println("------------------------------------");
                int k =0;
                // how the features look like
                //while (vector.iterator().hasNext()) {                    
                //    System.out.println(k+ " : " + vector.iterator().next());
                //    k++;
                //}
                //System.exit(1);
            }
            
        }

    
        
        // Return
        return vector;
    }
    
    /**
     * Printout preprocessed classification data 
     */
    private void saveEncodedData(boolean isInput) {        
        // it saves the pre-processed features and class to a csv file using fileName
        String fileName;
        if (isInput) {
              fileName = "projectName_input_classification.csv";
        } else {
              fileName = "projectName_Output_classification.csv";
        }
    }
}
