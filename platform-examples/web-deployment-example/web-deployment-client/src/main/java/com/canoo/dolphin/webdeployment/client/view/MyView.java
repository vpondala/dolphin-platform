/*
 * Copyright 2015-2017 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.webdeployment.client.view;

import com.canoo.platform.remoting.client.ClientContext;
import com.canoo.platform.remoting.client.javafx.FXBinder;

import com.canoo.platform.remoting.client.javafx.view.AbstractFXMLViewBinder;
import com.canoo.dolphin.webdeployment.Constants;
import com.canoo.dolphin.webdeployment.model.MyModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class MyView extends AbstractFXMLViewBinder<MyModel> {

    @FXML
    private TextField valueField;

    @FXML
    private Button resetButton;

    public MyView(ClientContext clientContext) {
        super(clientContext, Constants.CONTROLLER_NAME, MyView.class.getResource("view.fxml"));
    }


    @Override
    protected void init() {
        FXBinder.bind(valueField.textProperty()).bidirectionalTo(getModel().valueProperty());

        resetButton.setOnAction(e -> invoke(Constants.RESET_ACTION));
    }
}
