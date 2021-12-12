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

package org.deidentifier.arx.gui.view.impl.utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.deidentifier.arx.gui.Controller;
import org.deidentifier.arx.gui.model.Model;
import org.deidentifier.arx.gui.model.ModelEvent;
import org.deidentifier.arx.gui.model.ModelEvent.ModelPart;
import org.deidentifier.arx.gui.resources.Resources;
import org.deidentifier.arx.gui.view.def.ILayout;
import org.deidentifier.arx.gui.view.def.IView;
import org.deidentifier.arx.gui.view.impl.common.ComponentTitledFolder;
import org.deidentifier.arx.gui.view.impl.common.ComponentTitledFolderButtonBar;
import org.deidentifier.arx.gui.view.impl.utility.LayoutUtility.ViewUtilityType;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Layouts the visualization and allows enabling/disabling them.
 *
 * @author Fabian Prasser
 */
public class LayoutUtilityStatistics implements ILayout, IView {

    /** Constant */
    private static final String                         TAB_SUMMARY                 = Resources.getMessage("StatisticsView.6");             //$NON-NLS-1$

    /** Constant */
    private static final String                         TAB_DISTRIBUTION            = Resources.getMessage("StatisticsView.0");             //$NON-NLS-1$

    /** Constant */
    private static final String                         TAB_DISTRIBUTION_TABLE      = Resources.getMessage("StatisticsView.4");             //$NON-NLS-1$

    /** Constant */
    private static final String                         TAB_CONTINGENCY             = Resources.getMessage("StatisticsView.1");             //$NON-NLS-1$

    /** Constant */
    private static final String                         TAB_CONTINGENCY_TABLE       = Resources.getMessage("StatisticsView.5");             //$NON-NLS-1$

    /** Constant */
    private static final String                         TAB_CLASSES_TABLE           = Resources.getMessage("StatisticsView.7");             //$NON-NLS-1$

    /** Constant */
    private static final String                         TAB_PROPERTIES              = Resources.getMessage("StatisticsView.2");             //$NON-NLS-1$

    /** Constant */
    private static final String                         TAB_CLASSIFICATION_ANALYSIS = Resources.getMessage("StatisticsView.9");             //$NON-NLS-1$

    /** View */
    private final ComponentTitledFolder                 folder;

    /** View */
    private final ToolItem                              chkbtnVisualisation;
    
    /** View */
    private  Boolean                               hideSuppressedRecords;

    /** View */
    private  ToolItem                              chkbtnSuppressedRecords;

    /** View */
    private final Image                                 icnVisEnabled;

    /** View */
    private final Image                                 icnVisDisabled;

    /** View */
    private final Image                                 icnSREnabled;

    /** View */
    private final Image                                 icnSRDisabled;

    /** View */
    private final Map<Composite, String>                helpids                     = new HashMap<Composite, String>();

    /** Controller */
    private final Controller                            controller;

    /** Model */
    private Model                                       model                       = null;

    /** Control to type */
    private Map<Control, LayoutUtility.ViewUtilityType> types                       = new HashMap<Control, LayoutUtility.ViewUtilityType>();

    /**
     * Creates a new instance.
     *
     * @param parent
     * @param controller
     * @param target
     * @param reset
     */
    public LayoutUtilityStatistics(final Composite parent,
                                   final Controller controller,
                                   final ModelPart target,
                                   final ModelPart reset) {

        // initialize controller components 
        
        // initialize check button icons
        this.icnVisEnabled   = controller.getResources().getManagedImage("tick.png"); //$NON-NLS-1$
        this.icnVisDisabled  = controller.getResources().getManagedImage("cross.png"); //$NON-NLS-1$

        this.icnSREnabled    = controller.getResources().getManagedImage("tickSR.png"); //$NON-NLS-1$
        this.icnSRDisabled   = controller.getResources().getManagedImage("crossSR.png"); //$NON-NLS-1$

        this.controller = controller;
        
        controller.addListener(ModelPart.MODEL, this);
        controller.addListener(ModelPart.SELECTED_UTILITY_VISUALIZATION, this);


        // Create  toolbar
        ComponentTitledFolderButtonBar toolbarVis  = new ComponentTitledFolderButtonBar("id-50", helpids); //$NON-NLS-1$

        // Create suppressed records  and visualisation enable/disable check buttons
        final String chkbtnSuppressedRecordsLabel = Resources.getMessage("StatisticsView.13"); //$NON-NLS-1$
        final String chkbtnVisualisationLabel     = Resources.getMessage("StatisticsView.3"); //$NON-NLS-1$

        // add check buttons to the to toolbars
        if (target == ModelPart.INPUT) {            
            toolbarVis.add(chkbtnVisualisationLabel, icnVisDisabled, true, new Runnable() { @Override public void run() {
                toggleChkbtnVisualization();
                toggleChkbtnVisIcon(chkbtnVisualisation); 
            }});

        }else {
            toolbarVis.add(chkbtnSuppressedRecordsLabel, icnSREnabled, true, new Runnable() { @Override public void run() {
                toggleChkbtnSuppressedRecords();
                toggleChkbtnSRIcon(chkbtnSuppressedRecords); 
            }});
            
            toolbarVis.add(chkbtnVisualisationLabel, icnVisDisabled, true, new Runnable() { @Override public void run() {
                toggleChkbtnVisualization();
                toggleChkbtnVisIcon(chkbtnVisualisation); 
            }});                        
        }   
        
        hideSuppressedRecords = false;
        
        // Create the tab folder
        folder = new ComponentTitledFolder(parent, controller, toolbarVis, null, false, true);
        
        // Register tabs
        this.registerView(new ViewStatisticsSummaryTable(folder.createItem(TAB_SUMMARY, null, true), controller, target, reset), "help.utility.summary"); //$NON-NLS-1$
        this.registerView(new ViewStatisticsDistributionHistogram(folder.createItem(TAB_DISTRIBUTION, null, true), controller, target, reset,hideSuppressedRecords), "help.utility.distribution"); //$NON-NLS-1$
        this.registerView(new ViewStatisticsDistributionTable(folder.createItem(TAB_DISTRIBUTION_TABLE, null, true), controller, target, reset,hideSuppressedRecords), "help.utility.distribution"); //$NON-NLS-1$
        this.registerView(new ViewStatisticsContingencyHeatmap(folder.createItem(TAB_CONTINGENCY, null, true), controller, target, reset), "help.utility.contingency"); //$NON-NLS-1$
        this.registerView(new ViewStatisticsContingencyTable(folder.createItem(TAB_CONTINGENCY_TABLE, null, true), controller, target, reset), "help.utility.contingency"); //$NON-NLS-1$
        this.registerView(new ViewStatisticsEquivalenceClassTable(folder.createItem(TAB_CLASSES_TABLE, null, true), controller, target, reset), "help.utility.classes"); //$NON-NLS-1$
        if (target == ModelPart.INPUT) {
            this.registerView(new ViewPropertiesInput(folder.createItem(TAB_PROPERTIES, null, true), controller), "help.utility.inputproperties"); //$NON-NLS-1$
            this.registerView(new ViewStatisticsClassificationAttributes(folder.createItem(TAB_CLASSIFICATION_ANALYSIS, null, false), controller), "help.utility.accuracy"); //$NON-NLS-1$
        } else {
            this.registerView(new ViewPropertiesOutput(folder.createItem(TAB_PROPERTIES, null, true), controller), "help.utility.outputproperties"); //$NON-NLS-1$
            this.registerView(new ViewStatisticsClassificationConfiguration(folder.createItem(TAB_CLASSIFICATION_ANALYSIS, null, false, new StackLayout()), controller), "help.utility.accuracy"); //$NON-NLS-1$
        }
        
        // Init folder
        this.folder.setSelection(0);
        this.chkbtnVisualisation = folder.getButtonItem(chkbtnVisualisationLabel);
        this.chkbtnVisualisation.setEnabled(false);

        if (! (target == ModelPart.INPUT) ) {            
            this.chkbtnSuppressedRecords = folder.getButtonItem(chkbtnSuppressedRecordsLabel);
            this.chkbtnSuppressedRecords.setEnabled(true);
        };        
        // Set initial visibility
        folder.setVisibleItems(Arrays.asList(new String[] { TAB_SUMMARY,
                                                            TAB_DISTRIBUTION,
                                                            TAB_CONTINGENCY,
                                                            TAB_CLASSES_TABLE,
                                                            TAB_PROPERTIES }));
    }

    /**
     * Adds a selection listener.
     *
     * @param listener
     */
    public void addSelectionListener(final SelectionListener listener) {
        folder.addSelectionListener(listener);
    }

    @Override
    public void dispose() {
        // Empty by design
    }

    /**
     * Returns the selection index.
     *
     * @return
     */
    public ViewUtilityType getSelectedView() {
        return types.get(folder.getSelectedControl());
    }
    
    /**
     * Returns all visible items
     * @return
     */
    public List<String> getVisibleItems() {
        return this.folder.getVisibleItems();
    }

    @Override
    public void reset() {
        model = null;
        chkbtnVisualisation.setSelection(true);
        chkbtnVisualisation.setImage(icnVisEnabled);
        chkbtnVisualisation.setEnabled(false);
    }
    
    /**
     * Sets the according listener
     * @param listener
     */
    public void setItemVisibilityListener(final SelectionListener listener) {
        folder.setItemVisibilityListener(listener);
    }

    /**
     * Sets the selected view type
     * @param type
     */
    public void setSelectedView(ViewUtilityType type) {
        for (Entry<Control, ViewUtilityType> entry : types.entrySet()) {
            if (entry.getValue() == type) {
                this.folder.setSelectedControl(entry.getKey());
                return;
            }
        }
    }
    
    /**
     * Sets all visible items
     * @param items
     */
    public void setVisibleItems(List<String> items) {
        this.folder.setVisibleItems(items);
    }
    
    @Override
    public void update(ModelEvent event) {

        if (event.part == ModelPart.MODEL) {
            this.model = (Model)event.data;
            this.chkbtnVisualisation.setEnabled(true);
            this.chkbtnVisualisation.setSelection(model.isVisualizationEnabled());
            this.toggleChkbtnVisIcon(chkbtnVisualisation);
        } else if (event.part == ModelPart.SELECTED_UTILITY_VISUALIZATION) {
            this.chkbtnVisualisation.setSelection(model.isVisualizationEnabled());
            this.toggleChkbtnVisIcon(chkbtnVisualisation);
        }
    }

    /**
     * Registers a new view
     * @param view
     * @param helpid
     */
    private void registerView(ViewStatistics<?> view, String helpid) {
        types.put(view.getParent(), view.getType());
        helpids.put(view.getParent(), helpid);
    }

    /**
     * Registers a new view
     * @param view
     * @param helpid
     */
    private void registerView(ViewStatisticsBasic view, String helpid) {
        types.put(view.getParent(), view.getType());
        helpids.put(view.getParent(), helpid);
    }

    /**
     * Toggle visualization .
     */
    private void toggleChkbtnVisualization() {
        this.model.setVisualizationEnabled(this.chkbtnVisualisation.getSelection());
        this.controller.update(new ModelEvent(this, ModelPart.SELECTED_UTILITY_VISUALIZATION, chkbtnVisualisation.getSelection()));
    }

    /**
     * Toggle supressed records 
     */
    private void toggleChkbtnSuppressedRecords() {

        System.out.println("its working!!!");
        System.out.println(""+this.chkbtnSuppressedRecords.getSelection());
        this.hideSuppressedRecords = this.chkbtnSuppressedRecords.getSelection();
        //TODO:  how we update the registered tab ??? 
        //TODO: get the output data handles
        //      remove * column 
//        System.out.println("target"+ this.model.getResult().getOutput().getNumColumns());
//        System.out.println("target"+ this.model.getResult().getOutput().getNumRows());
        //this.model.getResult().getOutput().iterator();
        // TODO how to register and unregister items check the action of visible items         
        //this.registerView(new ViewStatisticsDistributionHistogram(folder.createItem(TAB_DISTRIBUTION, null, false), controller, target, reset,this.chkbtnSuppressedRecords.getSelection()), "help.utility.distribution"); //$NON-NLS-1$
    }
    

    /**
     * Toggle image.
     */
    private void toggleChkbtnVisIcon(ToolItem chkbtn){
            if (chkbtn.getSelection()) {
                chkbtn.setImage(icnVisEnabled);
            } else {
                chkbtn.setImage(icnVisDisabled);
            }
        }
    /**
     * Toggle image.
     */
    private void toggleChkbtnSRIcon(ToolItem chkbtn){
            if (chkbtn.getSelection()) {
                chkbtn.setImage(icnSREnabled);
            } else {
                chkbtn.setImage(icnSRDisabled);
            }
        }

}
