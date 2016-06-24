package com.googlesource.gerrit.plugins.cookbook;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.restapi.RestReadView;
import com.google.gerrit.server.project.ProjectResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlesource.gerrit.plugins.cookbook.karma.Accounts;
import com.googlesource.gerrit.plugins.cookbook.karma.Changes;
import com.googlesource.gerrit.plugins.cookbook.karma.KarmaInfo;

import java.util.List;

@Singleton
public class GetKarma implements RestReadView<ProjectResource> {

    private final GerritApi gerritApi;

    private final Changes changes;
    private final Accounts accounts;

    @Inject
    GetKarma(GerritApi gerritApi) {
        this.gerritApi = gerritApi;
        this.changes = new Changes(gerritApi);
        this.accounts = new Accounts(gerritApi);
    }

    @Override
    public KarmaInfo apply(ProjectResource resource) throws Exception {
        List<ChangeInfo> akilman = changes.getAllChangesForUser("akilman");
        String message = "average number of revisions for akilman: " + changes.averageNumberOfRevisions(akilman);
        return new KarmaInfo(37, message);
    }
}
