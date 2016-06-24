package com.googlesource.gerrit.plugins.cookbook;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.restapi.RestReadView;
import com.google.gerrit.server.account.AccountResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlesource.gerrit.plugins.cookbook.karma.Accounts;
import com.googlesource.gerrit.plugins.cookbook.karma.Changes;
import com.googlesource.gerrit.plugins.cookbook.karma.KarmaInfo;

import java.util.List;

@Singleton
public class GetKarma implements RestReadView<AccountResource> {

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
    public KarmaInfo apply(AccountResource resource) throws Exception {
        String userName = resource.getUser().getAccount().getUserName();
        String description = "average number of lines per change for: " + userName;
        int value = changes.scoreBasedOnAverageNumberOfLinesForUser(userName);
        return new KarmaInfo(description, value);
    }
}
