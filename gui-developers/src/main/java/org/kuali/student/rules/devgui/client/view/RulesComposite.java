/**
 * 
 */
package org.kuali.student.rules.devgui.client.view;

import org.kuali.student.commons.ui.messages.client.Messages;
import org.kuali.student.commons.ui.mvc.client.ApplicationContext;
import org.kuali.student.commons.ui.mvc.client.Controller;
import org.kuali.student.commons.ui.mvc.client.MVC;
import org.kuali.student.commons.ui.mvc.client.MVCEvent;
import org.kuali.student.commons.ui.mvc.client.model.Model;
import org.kuali.student.commons.ui.mvc.client.widgets.ModelBinding;
import org.kuali.student.commons.ui.viewmetadata.client.ViewMetaData;
import org.kuali.student.commons.ui.widgets.tables.ModelTableSelectionListener;
import org.kuali.student.rules.devgui.client.controller.DevelopersGuiMain;
import org.kuali.student.rules.devgui.client.model.BusinessRule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.VerticalSplitPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Zdenek
 */
public class RulesComposite extends Composite {

    final String FORM_ROW_HEIGHT = "22px";

    // events to be fired to parent controller
    public static class RulesEvent extends MVCEvent {}

    public static class RulesAddEvent extends RulesEvent {}

    public static class RulesUpdateEvent extends RulesEvent {}

    public static class RulesTestEvent extends RulesEvent {}

    // singleton instances of the events
    public static final RulesEvent RULES_EVENT = GWT.create(RulesEvent.class);
    public static final RulesAddEvent RULES_ADD_EVENT = GWT.create(RulesAddEvent.class);
    public static final RulesUpdateEvent RULES_UPDATE_EVENT = GWT.create(RulesUpdateEvent.class);
    public static final RulesTestEvent RULES_REMOVE_EVENT = GWT.create(RulesTestEvent.class);

    // controller and metadata to be looked up externally
    Controller controller;
    ViewMetaData metadata;
    Messages messages;

    // class that binds a widget to a model, instantiation is deferred
    // until application state is guaranteed to be ready
    ModelBinding<BusinessRule> binding;

    // widgets used for Rules forms.....
    final BusinessRulesTree rulesTree = new BusinessRulesTree(); // used to browse Rules
    final ScrollPanel rulesBrowserScrollPanel = new ScrollPanel();
    final HorizontalSplitPanel rulesHorizontalSplitPanel = new HorizontalSplitPanel();
    final ScrollPanel rulesScrollPanel = new ScrollPanel();
    final VerticalSplitPanel rulesVerticalSplitPanel = new VerticalSplitPanel();
    final SimplePanel simplePanel = new SimplePanel();

    // rules Main tab
    final TextBox nameTextBox = new TextBox();
    final TextArea descriptionTextArea = new TextArea();
    final TextArea successMessageTextArea = new TextArea();
    final TextArea failureMessageTextArea = new TextArea();
    final ListBox businessRuleTypeListBox = new ListBox();
    final TextBox anchorTextBox = new TextBox();
    final Label anchorTypeReadOnly = new Label("");

    boolean loaded = false;

    public RulesComposite() {
        super.initWidget(simplePanel);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (!loaded) {
            loaded = true;
            // get a reference to our parent controller
            controller = MVC.findParentController(this);

            // get a reference to our view metadata and internationalization messages
            metadata = ApplicationContext.getViews().get(DevelopersGuiMain.VIEW_NAME);
            messages = metadata.getMessages();

            // bind the list to the parent controller's Model of BusinessRule objects
            Model<BusinessRule> model = (Model<BusinessRule>) controller.getModel(BusinessRule.class);
            binding = new ModelBinding<BusinessRule>(model, rulesTree);

            // create tree-like rules browser
            rulesTree.setSize("100%", "100%");
            rulesBrowserScrollPanel.add(rulesTree);

            // create panel with a tree on left and a form on the right
            rulesHorizontalSplitPanel.setLeftWidget(rulesTree);
            rulesTree.setStyleName("gwt-Tree-rules");
            rulesHorizontalSplitPanel.setRightWidget(addRulesForm());
            rulesHorizontalSplitPanel.setSize("100%", "100%");
            rulesHorizontalSplitPanel.setSplitPosition("30%");

            // scroll panel on the bottom for log/error messages
            rulesScrollPanel.setSize("100%", "100%");

            // add tree/form and scroll panel together
            rulesVerticalSplitPanel.setSize("100%", "800px");
            rulesVerticalSplitPanel.setTopWidget(rulesHorizontalSplitPanel);
            rulesVerticalSplitPanel.setBottomWidget(rulesScrollPanel);
            rulesVerticalSplitPanel.setSplitPosition("80%");
            // simplePanel.setSize("100%", "100%");
            simplePanel.add(rulesVerticalSplitPanel);

            // add selection event listener to rulesTree widget
            /* commented out because fix for org.kuali.student.commons.ui.widgets.trees.SimpleTree.java was not yet
             * checked in to ks-commons-ui-dev module
             */
            /*
            rulesTree.addSelectionListener(new ModelTableSelectionListener<BusinessRule>() {
                public void onSelect(BusinessRule modelObject) {
                    if (modelObject == null) {
                        // selection was cleared
                        clearRulesMainTab(modelObject);
                    } else {
                        // populate fields from new selection
                        populateRulesMainTab(modelObject);
                    }
                }
            }); */
        }
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        // unlink the binding as it is no longer needed
        binding.unlink();
    }

    public void populateRulesMainTab(BusinessRule rule) {
        nameTextBox.setText(rule.getName());
        descriptionTextArea.setText(rule.getDescription());
        successMessageTextArea.setText(rule.getSuccessMessage());
        failureMessageTextArea.setText(rule.getFailureMessage());
        businessRuleTypeListBox.setValue(0, rule.getBusinessRuleTypeKey());
        // businessRuleTypeListBox.setItemSelected(0, true);
        anchorTypeReadOnly.setText(rule.getAnchorTypeKey());
        anchorTextBox.setText(rule.getAnchor());
    }

    public void clearRulesMainTab(BusinessRule rule) {
        nameTextBox.setText("");
        descriptionTextArea.setText("");
        successMessageTextArea.setText("");
        failureMessageTextArea.setText("");
        businessRuleTypeListBox.setItemSelected(0, true);
        anchorTypeReadOnly.setText("");
        anchorTextBox.setText("");
    }

    private Widget addRulesForm() {
        TabPanel rulesFormTabs = new TabPanel();
        rulesFormTabs.add(addRulesMainPage(), "Main");
        rulesFormTabs.add(addRulesPropositionPage(), "Propositions");
        rulesFormTabs.add(new Label("To Do"), "Authoring");
        rulesFormTabs.setSize("90%", "500px");

        // tabs.setStyleName("rulesBorder");
        rulesFormTabs.selectTab(0);

        final Button saveButton = new Button("Save");
        final Button cancelButton = new Button("Cancel");
        HorizontalPanel hp = new HorizontalPanel();
        hp.setSpacing(8);
        hp.add(saveButton);
        hp.add(cancelButton);
        cancelButton.addClickListener(new ClickListener() {
            public void onClick(final Widget sender) {}
        });

        final VerticalPanel rulesFormVerticalPanel = new VerticalPanel();
        rulesFormVerticalPanel.setSpacing(5);
        rulesFormVerticalPanel.add(rulesFormTabs);
        rulesFormVerticalPanel.add(hp);
        rulesFormVerticalPanel.setSize("100%", "100%");

        return rulesFormVerticalPanel;
    }

    private Widget addRulesMainPage() {

        // **********************************************************
        // set rules form margins
        // **********************************************************
        final FlexTable rulesFlexTable = new FlexTable();
        rulesFlexTable.setTitle("Rules");
        rulesFlexTable.setSize("100%", "100%");

        final SimplePanel topMargin = new SimplePanel();
        rulesFlexTable.setWidget(0, 0, topMargin);
        rulesFlexTable.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        rulesFlexTable.getFlexCellFormatter().setColSpan(0, 0, 2);
        rulesFlexTable.getCellFormatter().setHeight(0, 0, "5pix");

        final SimplePanel leftMargin = new SimplePanel();
        rulesFlexTable.setWidget(1, 0, leftMargin);
        rulesFlexTable.getCellFormatter().setWidth(1, 0, "5pix");

        // **********************************************************
        // set rules form size
        // **********************************************************
        final FormPanel rulesFormPanel = new FormPanel();
        rulesFlexTable.setWidget(1, 1, rulesFormPanel);
        rulesFlexTable.getCellFormatter().setWidth(1, 1, "100%");
        rulesFormPanel.setWidth("100%");
        rulesFlexTable.getCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);

        final FlexTable flexFormTable = new FlexTable();
        rulesFormPanel.add(flexFormTable);
        flexFormTable.setSize("100%", "100%");

        // **********************************************************
        // rules form elements
        // **********************************************************

        // heading
        /*
        final SimplePanel heading = new SimplePanel();
        flexFormTable.setWidget(0, 0, heading);
        flexFormTable.getFlexCellFormatter().setColSpan(0, 0, 2);
        flexFormTable.getCellFormatter().setHeight(0, 0, "21px");
        final Label headerLabel = new Label("Rule Definition");
        heading.add(headerLabel);
        headerLabel.setStyleName("gwt-Label-Form-Heading");
        */

        // Name
        final Label nameLabel = new Label("Name");
        flexFormTable.setWidget(1, 0, nameLabel);
        flexFormTable.getCellFormatter().setWidth(1, 0, "200px");
        flexFormTable.getCellFormatter().setHeight(1, 0, FORM_ROW_HEIGHT);

        flexFormTable.setWidget(1, 1, nameTextBox);
        nameTextBox.setWidth("50%");

        // Description
        final Label descriptionLabel = new Label("Description");
        flexFormTable.setWidget(2, 0, descriptionLabel);
        flexFormTable.getCellFormatter().setVerticalAlignment(2, 0, HasVerticalAlignment.ALIGN_TOP);
        flexFormTable.getCellFormatter().setHeight(2, 0, FORM_ROW_HEIGHT);
        flexFormTable.getCellFormatter().setWidth(2, 0, "200px");

        flexFormTable.setWidget(2, 1, descriptionTextArea);
        flexFormTable.getCellFormatter().setWordWrap(2, 1, true);
        flexFormTable.getCellFormatter().setVerticalAlignment(2, 1, HasVerticalAlignment.ALIGN_TOP);
        descriptionTextArea.setSize("75%", "100%");
        flexFormTable.getCellFormatter().setHeight(2, 1, "93px");

        // Success Message
        final Label successMessageLabel = new Label("Success Message");
        flexFormTable.setWidget(3, 0, successMessageLabel);
        flexFormTable.getCellFormatter().setVerticalAlignment(3, 0, HasVerticalAlignment.ALIGN_TOP);
        flexFormTable.getCellFormatter().setHeight(3, 0, FORM_ROW_HEIGHT);
        flexFormTable.getCellFormatter().setWidth(3, 0, "200px");

        flexFormTable.setWidget(3, 1, successMessageTextArea);
        successMessageTextArea.setTextAlignment(TextBoxBase.ALIGN_LEFT);
        flexFormTable.getCellFormatter().setVerticalAlignment(3, 1, HasVerticalAlignment.ALIGN_TOP);
        successMessageTextArea.setSize("75%", "100%");
        flexFormTable.getCellFormatter().setHeight(3, 1, "93px");

        // Failure Message
        final Label failureMessageLabel = new Label("Failure Message");
        flexFormTable.setWidget(4, 0, failureMessageLabel);
        flexFormTable.getCellFormatter().setVerticalAlignment(4, 0, HasVerticalAlignment.ALIGN_TOP);
        flexFormTable.getCellFormatter().setHeight(4, 0, FORM_ROW_HEIGHT);
        flexFormTable.getCellFormatter().setWidth(4, 0, "200px");

        flexFormTable.setWidget(4, 1, failureMessageTextArea);
        failureMessageTextArea.setSize("75%", "100%");
        flexFormTable.getCellFormatter().setHeight(4, 1, "93px");

        // Business Rule Type
        final Label businessRuleTypeLabel = new Label("Business Rule Type");
        flexFormTable.setWidget(5, 0, businessRuleTypeLabel);
        flexFormTable.getCellFormatter().setHeight(5, 0, FORM_ROW_HEIGHT);

        flexFormTable.setWidget(5, 1, businessRuleTypeListBox);
        flexFormTable.getCellFormatter().setHeight(5, 1, FORM_ROW_HEIGHT);
        businessRuleTypeListBox.addItem("Test1");
        businessRuleTypeListBox.addItem("Test2");
        businessRuleTypeListBox.addItem("Test3");

        // Anchor Type
        final Label anchorTypeLabel = new Label("Anchor Type:");
        flexFormTable.setWidget(6, 0, anchorTypeLabel);
        flexFormTable.getCellFormatter().setHeight(6, 0, FORM_ROW_HEIGHT);
        flexFormTable.setWidget(6, 1, anchorTypeReadOnly);

        // Anchor
        final Label anchorLabel = new Label("Anchor");
        flexFormTable.setWidget(7, 0, anchorLabel);
        flexFormTable.getCellFormatter().setWidth(7, 0, "200px");
        flexFormTable.getCellFormatter().setHeight(7, 0, FORM_ROW_HEIGHT);

        flexFormTable.setWidget(7, 1, anchorTextBox);
        anchorTextBox.setWidth("50%");

        // filler
        final SimplePanel filler = new SimplePanel();
        flexFormTable.setWidget(8, 0, filler);
        flexFormTable.getFlexCellFormatter().setColSpan(8, 0, 2);

        flexFormTable.getCellFormatter().setHorizontalAlignment(9, 0, HasHorizontalAlignment.ALIGN_CENTER);
        flexFormTable.getFlexCellFormatter().setColSpan(9, 0, 2);

        return rulesFlexTable;
    }

    private Widget addRulesPropositionPage() {

        // **********************************************************
        // set rules form margins
        // **********************************************************
        final FlexTable propositionsFlexTable = new FlexTable();
        propositionsFlexTable.setSize("100%", "100%");

        final SimplePanel topMargin = new SimplePanel();
        propositionsFlexTable.setWidget(0, 0, topMargin);
        propositionsFlexTable.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        propositionsFlexTable.getFlexCellFormatter().setColSpan(0, 0, 2);
        propositionsFlexTable.getCellFormatter().setHeight(0, 0, "5pix");

        final SimplePanel leftMargin = new SimplePanel();
        propositionsFlexTable.setWidget(1, 0, leftMargin);
        propositionsFlexTable.getCellFormatter().setWidth(1, 0, "526px");

        // **********************************************************
        // set rules form size
        // **********************************************************
        final FormPanel rulesFormPanel = new FormPanel();
        propositionsFlexTable.setWidget(1, 0, rulesFormPanel);
        propositionsFlexTable.getFlexCellFormatter().setColSpan(1, 0, 2);
        propositionsFlexTable.getCellFormatter().setWidth(1, 0, "100%");
        rulesFormPanel.setWidth("100%");
        propositionsFlexTable.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);

        final FlexTable flexFormTable = new FlexTable();
        rulesFormPanel.add(flexFormTable);
        flexFormTable.setSize("100%", "100%");

        // **********************************************************
        // rules form elements
        // **********************************************************

        // List of Propositions
        final Label propositionsLabel = new Label("Propositions");
        flexFormTable.setWidget(1, 0, propositionsLabel);
        flexFormTable.getCellFormatter().setWidth(1, 0, "200px");
        flexFormTable.getCellFormatter().setHeight(1, 0, FORM_ROW_HEIGHT);

        final ListBox propositionsListBox = new ListBox();
        flexFormTable.setWidget(5, 1, propositionsListBox);
        propositionsListBox.addItem("Proposition (P1)");
        propositionsListBox.addItem("Proposition (P2)");
        propositionsListBox.addItem("Proposition (P3)");
        propositionsListBox.setSize("75%", "100%");
        propositionsListBox.setVisibleItemCount(5);

        final TextBox nameTextBox = new TextBox();
        flexFormTable.setWidget(1, 1, nameTextBox);
        nameTextBox.setWidth("50%");

        final TextArea descriptionTextArea = new TextArea();
        flexFormTable.setWidget(2, 0, descriptionTextArea);
        flexFormTable.getCellFormatter().setWordWrap(2, 0, true);
        flexFormTable.getCellFormatter().setVerticalAlignment(2, 0, HasVerticalAlignment.ALIGN_TOP);
        descriptionTextArea.setSize("75%", "100%");
        flexFormTable.getCellFormatter().setHeight(2, 0, "93px");

        // Description
        flexFormTable.getCellFormatter().setVerticalAlignment(2, 0, HasVerticalAlignment.ALIGN_TOP);
        flexFormTable.getCellFormatter().setHeight(2, 0, "150px");
        flexFormTable.getCellFormatter().setWidth(2, 0, "200px");

        // Success Message
        final Label successMessageLabel = new Label("Success Message");
        flexFormTable.setWidget(4, 0, successMessageLabel);
        flexFormTable.getCellFormatter().setVerticalAlignment(4, 0, HasVerticalAlignment.ALIGN_TOP);
        flexFormTable.getCellFormatter().setHeight(4, 0, FORM_ROW_HEIGHT);
        flexFormTable.getCellFormatter().setWidth(4, 0, "200px");

        final TextArea successMessageTextArea = new TextArea();
        flexFormTable.setWidget(10, 0, successMessageTextArea);
        flexFormTable.getFlexCellFormatter().setColSpan(10, 0, 4);
        successMessageTextArea.setTextAlignment(TextBoxBase.ALIGN_LEFT);
        flexFormTable.getCellFormatter().setVerticalAlignment(10, 0, HasVerticalAlignment.ALIGN_TOP);
        successMessageTextArea.setSize("75%", "100%");
        flexFormTable.getCellFormatter().setHeight(10, 0, "93px");

        // Failure Message
        final Label failureMessageLabel = new Label("Failure Message");
        flexFormTable.setWidget(5, 0, failureMessageLabel);
        flexFormTable.getCellFormatter().setVerticalAlignment(5, 0, HasVerticalAlignment.ALIGN_TOP);
        flexFormTable.getCellFormatter().setHeight(5, 0, FORM_ROW_HEIGHT);
        flexFormTable.getCellFormatter().setWidth(5, 0, "200px");

        final TextArea failureMessageTextArea = new TextArea();
        flexFormTable.setWidget(12, 0, failureMessageTextArea);
        flexFormTable.getFlexCellFormatter().setColSpan(12, 0, 4);
        failureMessageTextArea.setSize("75%", "100%");
        flexFormTable.getCellFormatter().setHeight(12, 0, "93px");

        // Business Rule Type
        final Label businessRuleTypeLabel = new Label("Business Rule Type");
        flexFormTable.setWidget(11, 0, businessRuleTypeLabel);
        flexFormTable.getCellFormatter().setHeight(11, 0, FORM_ROW_HEIGHT);

        final ListBox businessRuleTypeListBox = new ListBox();
        flexFormTable.setWidget(6, 1, businessRuleTypeListBox);
        businessRuleTypeListBox.addItem("Test1");
        businessRuleTypeListBox.addItem("Test2");
        businessRuleTypeListBox.addItem("Test3");
        businessRuleTypeListBox.setSize("75%", "100%");
        // businessRuleTypeListBox.setVisibleItemCount(5);

        // Anchor
        final Label anchorLabel = new Label("Anchor");
        flexFormTable.setWidget(9, 0, anchorLabel);
        flexFormTable.getCellFormatter().setWidth(9, 0, "200px");
        flexFormTable.getCellFormatter().setHeight(9, 0, FORM_ROW_HEIGHT);

        final TextBox anchorTextBox = new TextBox();
        flexFormTable.setWidget(7, 1, anchorTextBox);
        anchorTextBox.setWidth("50%");

        // filler
        final SimplePanel filler = new SimplePanel();
        flexFormTable.setWidget(8, 0, filler);
        flexFormTable.getFlexCellFormatter().setColSpan(8, 0, 4);

        flexFormTable.getCellFormatter().setHorizontalAlignment(13, 0, HasHorizontalAlignment.ALIGN_CENTER);
        flexFormTable.getFlexCellFormatter().setColSpan(13, 0, 2);

        final Label label = new Label("YVF");
        flexFormTable.setWidget(2, 1, label);

        final ListBox listBox = new ListBox();
        flexFormTable.setWidget(3, 1, listBox);
        listBox.setVisibleItemCount(5);

        final Label label_1 = new Label("New Label");
        flexFormTable.setWidget(2, 2, label_1);

        return propositionsFlexTable;
    }

}
