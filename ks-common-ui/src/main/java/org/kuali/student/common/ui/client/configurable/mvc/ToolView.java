/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.student.common.ui.client.configurable.mvc;

import org.kuali.student.common.ui.client.mvc.Controller;
import org.kuali.student.common.ui.client.mvc.Model;
import org.kuali.student.common.ui.client.mvc.ModelRequestCallback;
import org.kuali.student.common.ui.client.mvc.View;
import org.kuali.student.common.ui.client.mvc.dto.ReferenceModel;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.LazyPanel;

/**
 * 
 * @author Kuali Student Team
 *
 */
public abstract class ToolView extends LazyPanel implements View{
    private Controller controller;    
    private Enum<?> viewEnum;
    private String viewName;    //View name is being used as menu item label   
        
    /**
     * @param controller
     * @param name
     */
    public ToolView(Controller controller, Enum<?> viewEnum, String viewName) {
        this.controller = controller;
        this.viewName = viewName;
        this.viewEnum = viewEnum;
    }


    public ToolView(Enum<?> viewEnum, String viewName) {
        this.controller = null;
        this.viewEnum = viewEnum;
        this.viewName = viewName;
    }
   
    public void beforeShow(){
        if (getWidget() instanceof HasReferenceId){
            controller.requestModel(ReferenceModel.class, new ModelRequestCallback<ReferenceModel>(){
                public void onModelReady(Model<ReferenceModel> model) {
                    HasReferenceId reference = (HasReferenceId)getWidget();
                    reference.setReferenceId(model.get().getReferenceId());
                    reference.setReferenceKey(model.get().getReferenceKey());
                }

                public void onRequestFail(Throwable cause) {
                    Window.alert(cause.toString());
                }
            });
        } else {
            this.setVisible(true);
        }
    }

    /**
     * @see org.kuali.student.common.ui.client.mvc.View#beforeHide()
     */
    @Override
    public boolean beforeHide() {
        return true;
    }


    /**
     * @see org.kuali.student.common.ui.client.mvc.View#getController()
     */
    @Override
    public Controller getController() {
        return this.controller;
    }


    /**
     * @see org.kuali.student.common.ui.client.mvc.View#getName()
     */
    @Override
    public String getName() {
        return this.viewName;
    }

    public Enum<?> getViewEnum() {
        return viewEnum;
    }

    /**
     * @see org.kuali.student.common.ui.client.mvc.View#updateModel()
     */
    @Override
    public void updateModel() {
        //There is no model to update here, reference model is read-only        
    }
    
    public void setController(Controller controller){
        this.controller = controller;
    }
}
