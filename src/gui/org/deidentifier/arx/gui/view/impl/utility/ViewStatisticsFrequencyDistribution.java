package org.deidentifier.arx.gui.view.impl.utility;

import java.util.ArrayList;

import org.deidentifier.arx.aggregates.StatisticsFrequencyDistribution;

public class ViewStatisticsFrequencyDistribution  {

    /** The data values, sorted. */
    public String[]          values;
    
    /** The corresponding frequencies. */
    public  double[]         frequency;
    
    /** The total number of data values. */
    public  int              count;
    
    public StatisticsFrequencyDistribution viewDistribution; 

    
    //TODO: update frequenct=y and counts 
    /**
     * Internal constructor.
     *
     * @param items
     * @param frequency
     * @param count
     */
    public ViewStatisticsFrequencyDistribution() {
        this.values = viewDistribution.values;
        this.count = viewDistribution.count;
        this.frequency = viewDistribution.frequency;
    }
    

  /**
  * Removing suppressed record.
  */
 
 public void removeSuppressedRecords() {

     this.values = viewDistribution.values;
     this.count = viewDistribution.count;
     this.frequency = viewDistribution.frequency;

     ArrayList <String> ar = new ArrayList<String>();
     for (int i=0; i<this.values.length;i++) {
        if ( (this.values[i]!="*") && (true) ) {                         
            ar.add(this.values[i]);
        }
    }
    String []  new_distValues = new String[ar.size()];
    double [] new_frequencies = new double [ar.size()];

    for (int i=0; i<ar.size();i++) {
        new_distValues[i] = ar.get(i);
        new_frequencies[i] = this.frequency[i];
    }       
    this.values = new_distValues;
    this.frequency = new_frequencies;
 }
    
}