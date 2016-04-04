package com.tencent.qcloud.presentation.presenter;

import android.util.Log;

import com.tencent.TIMCallBack;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMGroupPendencyGetParam;
import com.tencent.TIMGroupPendencyListGetSucc;
import com.tencent.TIMGroupSearchSucc;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.presentation.viewfeatures.GroupInfoView;
import com.tencent.qcloud.presentation.viewfeatures.GroupManageMessageView;
import com.tencent.qcloud.presentation.viewfeatures.GroupMemberManageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 群管理逻辑
 */
public class GroupManagerPresenter {

    private static final String TAG = "GroupManagerPresenter";

    private GroupManageMessageView messageView;
    private GroupInfoView infoView;
    private GroupMemberManageView manageView;
    private long timeStamp = 0;

    public GroupManagerPresenter(GroupManageMessageView view){
        this(view, null, null);
    }

    public GroupManagerPresenter(GroupInfoView view){
        infoView = view;
    }

    public GroupManagerPresenter(GroupMemberManageView view){
        this(null, null, view);
    }

    public GroupManagerPresenter(GroupManageMessageView view1, GroupInfoView view2, GroupMemberManageView view3){
        messageView = view1;
        infoView = view2;
        manageView = view3;
    }


    /**
     * 获取群管理最有一条消息,和未读消息数
     * 包括：加群等已决和未决的消息
     */
    public void getGroupManageLastMessage(){
        TIMGroupPendencyGetParam param = new TIMGroupPendencyGetParam();
        param.setNumPerPage(1);
        param.setTimestamp(0);
        TIMGroupManager.getInstance().getGroupPendencyList(param, new TIMValueCallBack<TIMGroupPendencyListGetSucc>() {
            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError code" + i + " msg " + s);
            }

            @Override
            public void onSuccess(TIMGroupPendencyListGetSucc timGroupPendencyListGetSucc) {
                if (messageView != null && timGroupPendencyListGetSucc.getPendencies().size() > 0){
                    messageView.onGetGroupManageLastMessage(timGroupPendencyListGetSucc.getPendencies().get(0),
                            timGroupPendencyListGetSucc.getPendencyMeta().getUnReadCount());
                }
            }
        });
    }


    /**
     * 获取群管理消息
     *
     * @param pageSize 每次拉取数量
     */
    public void getGroupManageMessage(int pageSize){
        TIMGroupPendencyGetParam param = new TIMGroupPendencyGetParam();
        param.setNumPerPage(pageSize);
        param.setTimestamp(timeStamp);
        TIMGroupManager.getInstance().getGroupPendencyList(param, new TIMValueCallBack<TIMGroupPendencyListGetSucc>() {
            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError code" + i + " msg " + s);
            }

            @Override
            public void onSuccess(TIMGroupPendencyListGetSucc timGroupPendencyListGetSucc) {
                if (messageView != null){
                    messageView.onGetGroupManageMessage(timGroupPendencyListGetSucc.getPendencies());
                }
            }
        });
    }






    /**
     * 按照群名称搜索群
     *
     * @param key 关键字
     */
    public void searchGroupByName(String key){
        long flag = 0;
        flag |= TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_NAME;
        flag |= TIMGroupManager.TIM_GET_GROUP_BASE_INFO_FLAG_OWNER_UIN;

        TIMGroupManager.getInstance().searchGroup(key, flag, null, 0, 30, new TIMValueCallBack<TIMGroupSearchSucc>() {
            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError code" + i + " msg " + s);
            }

            @Override
            public void onSuccess(TIMGroupSearchSucc timGroupSearchSucc) {
                if (infoView == null) return;
                infoView.showGroupInfo(timGroupSearchSucc.getInfoList());
            }
        });
    }


    /**
     * 按照群ID搜索群
     *
     * @param groupId 群组ID
     */
    public void searchGroupByID(String groupId){
        TIMGroupManager.getInstance().getGroupPublicInfo(Collections.singletonList(groupId), new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError code" + i + " msg " + s);
            }

            @Override
            public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                if (infoView == null) return;
                infoView.showGroupInfo(timGroupDetailInfos);
            }
        });
    }


    /**
     * 获取我的群列表
     *
     */
    public void getGroupList(){

        TIMGroupManager.getInstance().getGroupAssistant().getGroupDetailInfoList(new TIMValueCallBack<List<TIMGroupDetailInfo>>() {
            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError code" + i + " msg " + s);
            }

            @Override
            public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
                if (infoView == null) return;
                infoView.showGroupInfo(timGroupDetailInfos);
            }
        });

    }


    /**
     * 申请加入群
     *
     * @param groupId 群组ID
     * @param reason 申请理由
     * @param callBack 回调
     */
    public static void applyJoinGroup(String groupId, String reason, TIMCallBack callBack){
        TIMGroupManager.getInstance().applyJoinGroup(groupId, reason, callBack);
    }


    /**
     * 创建群
     *
     * @param name 群名称
     * @param type 群类型
     * @param members 群成员
     * @param callBack 回调
     */
    public static void createGroup(String name, String type, List<String> members, TIMValueCallBack<String> callBack){
        List<TIMGroupMemberInfo> memberinfos = new ArrayList<>();
        for (String member : members) {
            TIMGroupMemberInfo newMember = new TIMGroupMemberInfo();
            newMember.setUser(member);
            memberinfos.add(newMember);
        }
        TIMGroupManager.CreateGroupParam groupGroupParam = TIMGroupManager.getInstance().new CreateGroupParam();
        groupGroupParam.setGroupName(name);
        groupGroupParam.setMembers(memberinfos);
        groupGroupParam.setGroupType(type);
        TIMGroupManager.getInstance().createGroup(groupGroupParam, callBack);
    }







}
