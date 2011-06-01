package com.xirgonium.android.listener;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xirgonium.android.util.Constant;
import com.xirgonium.android.veloid.Veloid;

public class UpdateGUIShowMenuHandler extends Handler {

    Veloid veloid = null;

    public UpdateGUIShowMenuHandler(Veloid v) {
        super();
        this.veloid = v;
    }

    public UpdateGUIShowMenuHandler(Looper looper) {
        super(looper);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Constant.HANDLER_VELOID_MSG_SHOW_ADV_MENU:
                veloid.showFinalAdvancedMenuAfterAimation(Constant.MENU_ANIMATED_IS_ADVANCED_ACTIONS);
                break;

            case Constant.HANDLER_VELOID_MSG_HIDE_ADV_MENU:
                veloid.removeAdvancedMenuView(Constant.MENU_ANIMATED_IS_ADVANCED_ACTIONS);
                break;

            case Constant.HANDLER_VELOID_MSG_SHOW_EXPRESS_SEARCH_BIKE_MENU:
                veloid.showFinalAdvancedMenuAfterAimation(Constant.MENU_ANIMATED_IS_EXPRESS_SEARCH_BIKE);
                break;

            case Constant.HANDLER_VELOID_MSG_HIDE_EXPRESS_SEARCH_BIKE_MENU:
                veloid.removeAdvancedMenuView(Constant.MENU_ANIMATED_IS_EXPRESS_SEARCH_BIKE);
                break;

            case Constant.HANDLER_VELOID_MSG_SHOW_EXPRESS_SEARCH_SLOT_MENU:
                veloid.showFinalAdvancedMenuAfterAimation(Constant.MENU_ANIMATED_IS_EXPRESS_SEARCH_SLOT);
                break;

            case Constant.HANDLER_VELOID_MSG_HIDE_EXPRESS_SEARCH_SLOT_MENU:
                veloid.removeAdvancedMenuView(Constant.MENU_ANIMATED_IS_EXPRESS_SEARCH_SLOT);
                break;
        }
    }

}
