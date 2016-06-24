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

package com.googlesource.gerrit.plugins.cookbook;

import static com.google.gerrit.server.change.RevisionResource.REVISION_KIND;
import static com.google.gerrit.server.project.ProjectResource.PROJECT_KIND;

import com.google.common.collect.ImmutableList;
import com.google.gerrit.extensions.annotations.Exports;
import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.common.InheritableBoolean;
import com.google.gerrit.extensions.events.UsageDataPublishedListener;
import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.gerrit.extensions.restapi.RestApiModule;
import com.google.gerrit.extensions.webui.PatchSetWebLink;
import com.google.gerrit.extensions.webui.ProjectWebLink;
import com.google.gerrit.extensions.webui.TopMenu;
import com.google.gerrit.server.account.AccountResource;
import com.google.gerrit.server.config.ProjectConfigEntry;
import com.google.gerrit.server.git.validators.UploadValidationListener;
import com.google.gerrit.server.plugins.ServerPluginProvider;

import com.googlesource.gerrit.plugins.cookbook.pluginprovider.HelloSshPluginProvider;

/**
 * Extends the server module to provide access to the {@link GerritApi} singletone
 */
public class Module extends com.google.gerrit.server.api.Module {

  @Override
  protected void configure() {
    DynamicSet.bind(binder(), TopMenu.class)
        .to(HelloTopMenu.class);
    DynamicSet.bind(binder(), PatchSetWebLink.class).to(HelloWeblink.class);
    DynamicSet.bind(binder(), ProjectWebLink.class).to(HelloWeblink.class);
    DynamicSet.bind(binder(), ServerPluginProvider.class).to(
        HelloSshPluginProvider.class);
    DynamicSet.bind(binder(), UsageDataPublishedListener.class).to(UsageDataLogger.class);

    install(new RestApiModule() {
      @Override
      protected void configure() {
        post(REVISION_KIND, "hello-revision").to(HelloRevisionAction.class);
        post(PROJECT_KIND, "hello-project").to(HelloProjectAction.class);
        get(REVISION_KIND, "greetings").to(Greetings.class);
        get(AccountResource.ACCOUNT_KIND, "karma").to(GetKarma.class);

      }
    });
    DynamicSet.bind(binder(), UploadValidationListener.class)
        .to(DenyUploadExample.class);
    configurePluginParameters();
  }

  private void configurePluginParameters() {
    bind(ProjectConfigEntry.class)
        .annotatedWith(Exports.named("enable-hello"))
        .toInstance(new ProjectConfigEntry("Enable Greeting", true));
    bind(ProjectConfigEntry.class)
       .annotatedWith(Exports.named("enable-goodbye"))
       .toInstance(new ProjectConfigEntry("Enable Say Goodbye",
           InheritableBoolean.TRUE,
           InheritableBoolean.class, true));
    bind(ProjectConfigEntry.class)
       .annotatedWith(Exports.named("default-greeting"))
       .toInstance(new ProjectConfigEntry("Default Greeting",
           "Hey dude, how are you?", true));
    bind(ProjectConfigEntry.class)
        .annotatedWith(Exports.named("language"))
        .toInstance(new ProjectConfigEntry("Preferred Language", "en",
            ImmutableList.of("en", "de", "fr"), true));
    bind(ProjectConfigEntry.class)
        .annotatedWith(Exports.named("greet-number-per-week"))
        .toInstance(new ProjectConfigEntry("Greets Per Week", 42, true));
    bind(ProjectConfigEntry.class)
       .annotatedWith(Exports.named("greet-number-per-year"))
       .toInstance(new ProjectConfigEntry("Greets Per Year", 4711L, true));
    bind(ProjectConfigEntry.class)
       .annotatedWith(Exports.named("reviewers"))
        .toInstance(
            new ProjectConfigEntry("Reviewers", null,
                ProjectConfigEntry.Type.ARRAY, null, false,
                "Users or groups can be provided as reviewers"));
  }
}
