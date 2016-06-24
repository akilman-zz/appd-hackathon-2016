package com.googlesource.gerrit.plugins.cookbook.karma;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.common.AccountInfo;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.inject.Inject;

import java.util.List;

public class Accounts {

    private final GerritApi gerritApi;

    @Inject
    public Accounts(GerritApi gerritApi) {
        this.gerritApi = gerritApi;
    }

    public long getAccountAge(AccountInfo account) throws RestApiException {
        Changes changes = new Changes(gerritApi);
        List<ChangeInfo> changeList = changes.getAllChangesForUser("akilman");

        long earliestKnownChangeInMillis = Long.MAX_VALUE;
        for (ChangeInfo change : changeList) {
            if (change.created.getTime() < earliestKnownChangeInMillis) {
                earliestKnownChangeInMillis = change.created.getTime();
            }
        }

        long timeNowInMillis = System.currentTimeMillis();

        long ageInDays = (timeNowInMillis - earliestKnownChangeInMillis)/(24*60*60*1000);
        return ageInDays;
    }
}
