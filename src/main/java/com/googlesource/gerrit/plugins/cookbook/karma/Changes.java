package com.googlesource.gerrit.plugins.cookbook.karma;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.common.*;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;
import java.util.Map;

@Singleton
public class Changes {

    private final GerritApi gerritApi;

    @Inject
    public Changes(GerritApi gerritApi) {
        this.gerritApi = gerritApi;
    }

    public List<ChangeInfo> getAllChangesForUser(String userName) throws RestApiException {
        com.google.gerrit.extensions.api.changes.Changes.QueryRequest queryRequest =
                gerritApi.changes().query("owner:" + userName);
        return queryRequest.get();
    }

    public long ageInDays(ChangeInfo change) {
        long createdMillis = change.created.getTime();
        long updatedMillis = change.updated.getTime();
        long millisInDay = (24*60*60*1000);
        return (createdMillis - updatedMillis)/millisInDay;
    }

    public int totalLinesChanged(ChangeInfo change) {
        return change.insertions + change.deletions;
    }

    public int scoreBasedOnAverageNumberOfLinesForUser(String userName) throws RestApiException {
        int averageLinesForUser = averageNumberOfLinesForUser(userName);
        int worstPossibleScore = 1200000;
        int score = (int) Math.floor(100 * averageLinesForUser / worstPossibleScore) + 1;
        return 100 - score;
    }

    public int averageNumberOfLinesForUser(String userName) throws RestApiException {
        List<ChangeInfo> userChanges = getAllChangesForUser(userName);
        return averageNumberOfLines(userChanges);
    }

    public int averageNumberOfLines(List<ChangeInfo> changes) {

        int nTotalLines = 0;
        int nChanges = changes.size();

        for (ChangeInfo change : changes) {
            nTotalLines += totalLinesChanged(change);
        }

        if (nChanges == 0) {
            return 0;
        }

        return nTotalLines / nChanges;
    }

    public int averageNumberOfRevisions(List<ChangeInfo> changes) throws RestApiException {

        int nChanges = changes.size();
        int nRevisions = 0;

        for (ChangeInfo change : changes) {

            // revision info may not be fetched - explicit fetch required - no idea why...
            ChangeInfo fetchedChange = gerritApi.changes().id(change._number).get();

            Map<String, RevisionInfo> revisions = fetchedChange.revisions;
            if (revisions == null) {
                continue;
            }
            nRevisions += revisions.size();
        }

        return (int) Math.floor(((double)nRevisions)/((double)nChanges));
    }

    public boolean isApproved(ChangeInfo change) throws RestApiException {

        Map<String, LabelInfo> labels = change.labels;

        if (labels == null) {
            // explicitly fetch if no labels found
            change = gerritApi.changes().id(change._number).get();
        }

        LabelInfo codeReview = labels.get("Code-Review");

        if (codeReview == null) {
            // code not reviewed yet
            return false;
        }

        AccountInfo approverAccountInfo = codeReview.approved;
        return approverAccountInfo != null;
    }

    public boolean isRejected(ChangeInfo change) throws RestApiException {

        Map<String, LabelInfo> labels = change.labels;

        if (labels == null) {
            // explicitly fetch if no labels found
            change = gerritApi.changes().id(change._number).get();
        }

        LabelInfo codeReview = labels.get("Code-Review");

        if (codeReview == null) {
            // code not reviewed yet
            return false;
        }

        AccountInfo rejectorAccountInfo = codeReview.rejected;
        return rejectorAccountInfo != null;

    }

    /**
     * Picks up the code review {@link LabelInfo}, picks the earliest review and computes the days between this and
     * the time the patch set was created
     *
     * No, this is not perfect... its a very rough ballpark
     */
    public long daysToReview(ChangeInfo change) throws RestApiException {

        Map<String, LabelInfo> labels = change.labels;

        if (labels == null) {
            // explicitly fetch if no labels found
            change = gerritApi.changes().id(change._number).get();
        }

        LabelInfo codeReview = labels.get("Code-Review");
        if (codeReview == null) {
            return -1;
        }

        // could be several - pick the earliest
        long earliestReviewTimeInMillis = Long.MAX_VALUE;
        for (ApprovalInfo approvalInfo : codeReview.all) {
            if (approvalInfo.date.getTime() < earliestReviewTimeInMillis) {
                earliestReviewTimeInMillis = approvalInfo.date.getTime();
            }
        }

        long createdTimeInMillis = change.created.getTime();
        long millisInDay = (24*60*60*1000);
        return (earliestReviewTimeInMillis - createdTimeInMillis)/millisInDay;
    }

}
