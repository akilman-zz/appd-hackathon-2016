package com.googlesource.gerrit.plugins.cookbook.karma;

import com.google.gerrit.extensions.restapi.RestResource;
import com.google.gerrit.extensions.restapi.RestView;
import com.google.gerrit.server.project.ProjectResource;
import com.google.inject.TypeLiteral;

public class KarmaResource implements RestResource {

    public static final TypeLiteral<RestView<ProjectResource>> PROJECT_KIND =
            new TypeLiteral<RestView<ProjectResource>>() {};
}
