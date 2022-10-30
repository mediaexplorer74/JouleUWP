package com.phonegap.plugins.nativesettings;

import android.content.Intent;
import android.net.Uri;
import com.adobe.phonegap.push.PushConstants;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.apache.cordova.networkinformation.NetworkManager;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.json.JSONArray;
import org.xwalk.core.internal.extension.api.messaging.MessagingSmsConsts;

public class NativeSettings extends CordovaPlugin {
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        Status status = Status.OK;
        Uri packageUri = Uri.parse("package:" + this.cordova.getActivity().getPackageName());
        String result = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        if (action.equals("open")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
        } else if (action.equals("accessibility")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
        } else if (action.equals("add_account")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.ADD_ACCOUNT_SETTINGS"));
        } else if (action.equals("airplane_mode")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.AIRPLANE_MODE_SETTINGS"));
        } else if (action.equals("apn")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.APN_SETTINGS"));
        } else if (action.equals("application_details")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", packageUri));
        } else if (action.equals("application_development")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.APPLICATION_DEVELOPMENT_SETTINGS"));
        } else if (action.equals("application")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.APPLICATION_SETTINGS"));
        } else if (action.equals("bluetooth")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.BLUETOOTH_SETTINGS"));
        } else if (action.equals("captioning")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.CAPTIONING_SETTINGS"));
        } else if (action.equals("cast")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.CAST_SETTINGS"));
        } else if (action.equals("data_roaming")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.DATA_ROAMING_SETTINGS"));
        } else if (action.equals(MessagingSmsConsts.DATE)) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.DATE_SETTINGS"));
        } else if (action.equals("device_info")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.DEVICE_INFO_SETTINGS"));
        } else if (action.equals("display")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.DISPLAY_SETTINGS"));
        } else if (action.equals("dream")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.DREAM_SETTINGS"));
        } else if (action.equals("home")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.HOME_SETTINGS"));
        } else if (action.equals("input_method")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.INPUT_METHOD_SETTINGS"));
        } else if (action.equals("input_method_subtype")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.INPUT_METHOD_SUBTYPE_SETTINGS"));
        } else if (action.equals("internal_storage")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.INTERNAL_STORAGE_SETTINGS"));
        } else if (action.equals("locale")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.LOCALE_SETTINGS"));
        } else if (action.equals("location_source")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
        } else if (action.equals("manage_all_applications")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.MANAGE_ALL_APPLICATIONS_SETTINGS"));
        } else if (action.equals("manage_applications")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.MANAGE_APPLICATIONS_SETTINGS"));
        } else if (action.equals("memory_card")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.MEMORY_CARD_SETTINGS"));
        } else if (action.equals("network_operator")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.NETWORK_OPERATOR_SETTINGS"));
        } else if (action.equals("nfcsharing")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.NFCSHARING_SETTINGS"));
        } else if (action.equals("nfc_payment")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.NFC_PAYMENT_SETTINGS"));
        } else if (action.equals("nfc_settings")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.NFC_SETTINGS"));
        } else if (action.equals("print")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.ACTION_PRINT_SETTINGS"));
        } else if (action.equals("privacy")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.PRIVACY_SETTINGS"));
        } else if (action.equals("quick_launch")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.QUICK_LAUNCH_SETTINGS"));
        } else if (action.equals("search")) {
            this.cordova.getActivity().startActivity(new Intent("android.search.action.SEARCH_SETTINGS"));
        } else if (action.equals("security")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.SECURITY_SETTINGS"));
        } else if (action.equals("settings")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.SETTINGS"));
        } else if (action.equals("show_regulatory_info")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.SHOW_REGULATORY_INFO"));
        } else if (action.equals(PushConstants.SOUND)) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.SOUND_SETTINGS"));
        } else if (action.equals("sync")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.SYNC_SETTINGS"));
        } else if (action.equals("usage_access")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.USAGE_ACCESS_SETTINGS"));
        } else if (action.equals("user_dictionary")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.USER_DICTIONARY_SETTINGS"));
        } else if (action.equals("voice_input")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.VOICE_INPUT_SETTINGS"));
        } else if (action.equals("wifi_ip")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.WIFI_IP_SETTINGS"));
        } else if (action.equals(NetworkManager.WIFI)) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.WIFI_SETTINGS"));
        } else if (action.equals("wireless")) {
            this.cordova.getActivity().startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
        } else {
            status = Status.INVALID_ACTION;
        }
        callbackContext.sendPluginResult(new PluginResult(status, result));
        return true;
    }
}
