// Copyright (C) 2013 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.cookbook.client;

import com.google.gerrit.plugin.client.rpc.RestApi;
import com.google.gerrit.plugin.client.screen.Screen;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

class IndexScreen extends VerticalPanel {

  static class Factory implements Screen.EntryPoint {

    @Override
    public void onLoad(Screen screen) {
      screen.setPageTitle("aw snap");
      screen.show(new IndexScreen());
    }
  }

  private String restReply;


  TextArea textArea = new TextArea();

  IndexScreen() {

    setStyleName("cookbook-panel");

    String someText = "some text...";
    textArea.setText(someText);

    Panel panel = new VerticalPanel();
    panel.add(textArea);
    add(panel);

    Button helloButton = new Button("Say Hello To My Little Friend...");
    helloButton.addStyleName("cookbook-helloButton");
    helloButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(final ClickEvent event) {
        sayHello();
      }
    });
    add(helloButton);
    helloButton.setEnabled(true);
  }

  private void sayHello() {

    String projectName = "demo-project";
    RestApi projectRestCall = new RestApi("projects").id(projectName).view("cookbook", "karma");

    restReply = "path: " + projectRestCall.path();

    projectRestCall.get(new AsyncCallback<JavaScriptObject>() {
      @Override
      public void onFailure(Throwable throwable) {
        restReply = restReply + ", error: " + throwable.getMessage();
        textArea.setText(restReply);
      }

      @Override
      public void onSuccess(JavaScriptObject javaScriptObject) {
        restReply = restReply + ", success: " + new JSONObject(javaScriptObject).toString();
        textArea.setText(restReply);
      }
    });
    Window.alert("making rest call...");
  }

}
