/*
 * ARX: Powerful Data Anonymization
 * Copyright 2012 - 2021 Fabian Prasser and contributors
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

package org.deidentifier.arx.gui.view.impl.common.async;

import org.deidentifier.arx.aggregates.StatisticsFrequencyDistribution;

/**
 * The current context.
 *
 * @author Fabian Prasser
 */
public interface AnalysisContextVisualization {
    
    /**
     * Is the provided attribute selected according to the config?.
     *
     * @param attribute
     * @return
     */
    public boolean isAttributeSelected(String attribute);
    
    /**
     * Is this a valid context.
     *
     * @return
     */
    public boolean isValid();
    
    /**
     * Hide suppressed records.
     * 
     * @param distribution
     * @return
     */
    public void hideSuppressedData(StatisticsFrequencyDistribution distribution) throws InterruptedException;
}
