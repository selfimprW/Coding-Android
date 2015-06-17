package net.coding.program.model;

import net.coding.program.common.HtmlContent;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by chenchao on 15/6/4.
 */
public class MergeDetail implements Serializable {
    String merge_request_description = "";
    boolean can_edit_src_branch;
    boolean can_edit;
    Merge merge_request;

    public MergeDetail(JSONObject json) {
        merge_request_description = json.optString("merge_request_description", "");
        can_edit_src_branch = json.optBoolean("can_edit_src_branch");
        can_edit = json.optBoolean("can_edit");

        if (json.has("pull_request")) {
            merge_request = new Merge(json.optJSONObject("pull_request"));
        } else {
            merge_request = new Merge(json.optJSONObject("merge_request"));
        }
    }

    public boolean isCanEditSrcBranch() {
        return merge_request.authorIsMe();
    }

    public boolean isCan_edit_src_branch() {
        return can_edit_src_branch;
    }

    public boolean isCanEdit() {
        return can_edit;
    }

    public String getContent() {
        return HtmlContent.parseReplacePhoto(merge_request_description).text;
    }

    public Merge.PostRequest getHttpMerge(String message, boolean delSrc) {
        return merge_request.getHttpMerge(message, delSrc);
    }

    public String generalMergeMessage() {
        return merge_request.generalMergeMessage();
    }
}