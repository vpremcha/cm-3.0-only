/**
 * Copyright 2010 The Kuali Foundation Licensed under the Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.osedu.org/licenses/ECL-2.0 Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */

/**
 * This widget adds a label with remaining character count for KSTextBox, KSTextArea
 */

package org.kuali.student.common.ui.client.widgets;

import org.kuali.student.common.assembly.data.Metadata;
import org.kuali.student.common.assembly.data.MetadataInterrogator;
import org.kuali.student.common.ui.client.application.Application;
import org.kuali.student.common.ui.client.configurable.mvc.DefaultWidgetFactory;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class KSCharCount extends Composite implements HasText, HasInputWidget {
    VerticalPanel countingPanel;
    Widget inputWidget;
    KSLabel countingLabel;

    private int maxLength;

    public KSCharCount() {}

    public KSCharCount(Metadata metadata) {
        countingPanel = new VerticalPanel();
        countingLabel = new KSLabel();
        super.initWidget(countingPanel);
        this.setWidget(DefaultWidgetFactory.getInstance().getWidget(metadata));
        this.setMaxLength(metadata);

        if (this.inputWidget instanceof TextBoxBase) {
            ((TextBoxBase) (this.inputWidget)).addKeyUpHandler(new KeyUpHandler() {

                @Override
                public void onKeyUp(KeyUpEvent event) {
                    countingLabel.setText(setLabel());
                }

            });
        }

        countingLabel.setText(this.setLabel());
        countingPanel.add(inputWidget);
        countingPanel.add(countingLabel);
    }

    public void setMaxLength(Metadata metadata) {
        this.maxLength = MetadataInterrogator.getSmallestMaxLength(metadata);
    }

    public void setWidget(Widget widget) {
        if (widget instanceof TextBoxBase) {
            this.inputWidget = widget;
        } else {
            this.inputWidget = new KSTextBox();
        }
    }

    public int getRemCount() {
        int rem = 0;

        if ((this.getText() != null) && (this.getText().length() <= maxLength)) {
            rem = this.maxLength - ((TextBoxBase) (this.inputWidget)).getText().length();
        }
        if (this.getText() == null) {
            rem = this.maxLength;
        }

        return rem;
    }

    public String setLabel() {
        return (getRemCount() + " " + Application.getApplicationContext().getUILabel("common", "remainingChars"));
    }

    @Override
    public String getText() {
        return ((TextBoxBase) (this.inputWidget)).getText();
    }

    @Override
    public void setText(String text) {
        ((TextBoxBase) (this.inputWidget)).setText(text);

    }

    @Override
    public Widget getInputWidget() {
        return this.inputWidget;
    }

}
