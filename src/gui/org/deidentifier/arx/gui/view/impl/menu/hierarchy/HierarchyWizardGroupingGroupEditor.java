package org.deidentifier.arx.gui.view.impl.menu.hierarchy;

import org.deidentifier.arx.aggregates.AggregateFunction;
import org.deidentifier.arx.gui.view.SWTUtil;
import org.deidentifier.arx.gui.view.impl.menu.EditorString;
import org.deidentifier.arx.gui.view.impl.menu.hierarchy.HierarchyWizardGroupingFunctionEditor.IHierarchyFunctionEditorParent;
import org.deidentifier.arx.gui.view.impl.menu.hierarchy.HierarchyWizardGroupingModel.HierarchyWizardGroupingGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Editor for groups
 * @author Fabian Prasser
 *
 * @param <T>
 */
public class HierarchyWizardGroupingGroupEditor<T> implements HierarchyWizardGroupingView, IHierarchyFunctionEditorParent<T>{

    /** Var */
    private HierarchyWizardGroupingGroup<T>                group = null;
    /** Var */
    private final EditorString                             editorSize;
    /** Var */
    private final HierarchyWizardGroupingModel<T>          model;
    /** Var */
    private final HierarchyWizardGroupingFunctionEditor<T> editorFunction;

    /**
     * Creates a new instance
     * @param parent
     * @param model
     */
    public HierarchyWizardGroupingGroupEditor(final Composite parent,
                                final HierarchyWizardGroupingModel<T> model) {
        this.model = model;
        this.model.register(this);
        
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(SWTUtil.createGridLayout(2, false));    
        composite.setLayoutData(SWTUtil.createFillHorizontallyGridData());
        this.editorFunction = new HierarchyWizardGroupingFunctionEditor<T>(this, model, composite, false);

        createLabel(composite, "Size:");
        this.editorSize = new EditorString(composite) {
            @Override
            public boolean accepts(final String s) {
                if (group==null) return false;
                try {
                    int i = Integer.parseInt(s);
                    return i>0;
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            @Override
            public String getValue() {
                if (group==null) return "";
                else return String.valueOf(group.size);
            }

            @Override
            public void setValue(final String s) {
                if (group!=null){
                    if (group.size != Integer.valueOf(s)){
                        group.size = Integer.valueOf(s);
                        model.update(HierarchyWizardGroupingGroupEditor.this);
                    }
                }
            }
        };
    }

    @Override
    public void setFunction(AggregateFunction<T> function) {
        if (this.group == null) return;
        if (editorFunction.isDefaultFunction(function)) {
            this.group.function = model.getDefaultFunction();
        } else {
            this.group.function = function;
        }
        model.update(this);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void update() {
        if (model.getSelectedElement() instanceof HierarchyWizardGroupingGroup){
            this.group = (HierarchyWizardGroupingGroup<T>)model.getSelectedElement();
            this.editorFunction.setFunction(group.function);
            this.editorSize.update();
            SWTUtil.enable(editorSize.getControl());
        } else {
            this.group = null;
            this.editorFunction.setFunction(null);
            this.editorSize.update();
            SWTUtil.disable(editorSize.getControl());
        }
    }

    /**
     * Creates a label
     * @param composite
     * @param string
     * @return
     */
    private Label createLabel(Composite composite, String string) {
        Label label = new Label(composite, SWT.NONE);
        label.setText(string);
        GridData data = SWTUtil.createFillVerticallyGridData();
        data.verticalAlignment = SWT.CENTER;
        label.setLayoutData(data);
        return label;
    }
}
