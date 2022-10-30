package org.xwalk.core.internal.extension.api.contacts;

import android.util.Pair;
import com.adobe.phonegap.push.PushConstants;
import com.google.android.gms.common.Scopes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.cordova.networkinformation.NetworkManager;
import org.xwalk.core.internal.extension.api.messaging.MessagingSmsConsts;

public class ContactConstants {
    public static final String CUSTOM_MIMETYPE_GENDER = "vnd.android.cursor.item/contact_custom_gender";
    public static final String CUSTOM_MIMETYPE_LASTUPDATED = "vnd.android.cursor.item/contact_custom_lastupdated";
    public static final Map<String, String> addressDataMap;
    public static final Map<String, String> addressTypeMap;
    public static final Map<String, Integer> addressTypeValuesMap;
    public static final Map<String, String> companyDataMap;
    public static final Map<String, Pair<String, String>> contactDataMap;
    public static final List<ContactMap> contactMapList;
    public static final Map<String, String> emailDataMap;
    public static final Map<String, String> emailTypeMap;
    public static final Map<String, Integer> emailTypeValuesMap;
    public static final Map<String, String> findFieldMap;
    public static final Map<String, String> imDataMap;
    public static final Map<String, Integer> imProtocolMap;
    public static final Map<String, String> imTypeMap;
    public static final Map<String, Integer> imTypeValuesMap;
    public static final Map<String, String> jobtitleDataMap;
    public static final Map<String, String> noteDataMap;
    public static final Map<String, String> phoneDataMap;
    public static final Map<String, String> phoneTypeMap;
    public static final Map<String, Integer> phoneTypeValuesMap;
    public static final Map<String, String> photoDataMap;
    public static final Map<String, String> websiteDataMap;
    public static final Map<String, String> websiteTypeMap;
    public static final Map<String, Integer> websiteTypeValuesMap;

    /* renamed from: org.xwalk.core.internal.extension.api.contacts.ContactConstants.1 */
    static class C04851 extends HashMap<String, String> {
        C04851() {
            put(MessagingSmsConsts.TYPE, "data2");
            put("isPrimary", "is_primary");
            put("isSuperPrimary", "is_super_primary");
        }
    }

    /* renamed from: org.xwalk.core.internal.extension.api.contacts.ContactConstants.2 */
    static class C04862 extends HashMap<String, String> {
        C04862() {
            put(MessagingSmsConsts.TYPE, "data2");
            put("isPrimary", "is_primary");
            put("isSuperPrimary", "is_super_primary");
        }
    }

    /* renamed from: org.xwalk.core.internal.extension.api.contacts.ContactConstants.3 */
    static class C04873 extends HashMap<String, String> {
        C04873() {
            put(MessagingSmsConsts.TYPE, "data2");
            put("isPrimary", "is_primary");
            put("isSuperPrimary", "is_super_primary");
        }
    }

    /* renamed from: org.xwalk.core.internal.extension.api.contacts.ContactConstants.4 */
    static class C04884 extends HashMap<String, String> {
        C04884() {
            put(MessagingSmsConsts.TYPE, "data2");
            put("isPrimary", "is_primary");
            put("isSuperPrimary", "is_super_primary");
        }
    }

    /* renamed from: org.xwalk.core.internal.extension.api.contacts.ContactConstants.5 */
    static class C04895 extends HashMap<String, String> {
        C04895() {
            put(MessagingSmsConsts.TYPE, "data2");
            put("isPrimary", "is_primary");
            put("isSuperPrimary", "is_super_primary");
        }
    }

    /* renamed from: org.xwalk.core.internal.extension.api.contacts.ContactConstants.6 */
    static class C04906 extends HashMap<String, Integer> {
        C04906() {
            put("work", Integer.valueOf(2));
            put("home", Integer.valueOf(1));
            put(NetworkManager.MOBILE, Integer.valueOf(4));
        }
    }

    /* renamed from: org.xwalk.core.internal.extension.api.contacts.ContactConstants.7 */
    static class C04917 extends HashMap<String, Integer> {
        C04917() {
            put("blog", Integer.valueOf(2));
            put("ftp", Integer.valueOf(6));
            put("home", Integer.valueOf(4));
            put("homepage", Integer.valueOf(1));
            put("other", Integer.valueOf(7));
            put(Scopes.PROFILE, Integer.valueOf(3));
            put("work", Integer.valueOf(5));
        }
    }

    /* renamed from: org.xwalk.core.internal.extension.api.contacts.ContactConstants.8 */
    static class C04928 extends HashMap<String, Integer> {
        C04928() {
            put("work", Integer.valueOf(2));
            put("home", Integer.valueOf(1));
            put("other", Integer.valueOf(3));
        }
    }

    /* renamed from: org.xwalk.core.internal.extension.api.contacts.ContactConstants.9 */
    static class C04939 extends HashMap<String, Integer> {
        C04939() {
            put("home", Integer.valueOf(1));
            put(NetworkManager.MOBILE, Integer.valueOf(2));
            put("work", Integer.valueOf(3));
            put("fax_work", Integer.valueOf(4));
            put("fax_home", Integer.valueOf(5));
            put("pager", Integer.valueOf(6));
            put("other", Integer.valueOf(7));
            put(PushConstants.CALLBACK, Integer.valueOf(8));
            put("car", Integer.valueOf(9));
            put("company_main", Integer.valueOf(10));
            put("isdn", Integer.valueOf(11));
            put("main", Integer.valueOf(12));
            put("other_fax", Integer.valueOf(13));
            put("radio", Integer.valueOf(14));
            put("telex", Integer.valueOf(15));
            put("tty_tdd", Integer.valueOf(16));
            put(NetworkManager.MOBILE, Integer.valueOf(17));
            put("work_pager", Integer.valueOf(18));
            put("assistant", Integer.valueOf(19));
            put("mms", Integer.valueOf(20));
        }
    }

    public static class ContactMap {
        public Map<String, String> mDataMap;
        public String mMimeType;
        public String mName;
        public Map<String, String> mTypeMap;
        public Map<String, Integer> mTypeValueMap;

        public ContactMap(String n, Map<String, String> datas, Map<String, String> types, Map<String, Integer> typeValues) {
            this.mName = n;
            this.mMimeType = (String) ((Pair) ContactConstants.contactDataMap.get(n)).second;
            this.mDataMap = datas;
            this.mTypeMap = types;
            this.mTypeValueMap = typeValues;
        }
    }

    static {
        findFieldMap = createStringMap(new String[]{"familyName", "familyNames", "givenNames", "givenNames", "middleName", "middleNames", "additionalName", "additionalNames", "honorificPrefix", "honorificPrefixes", "honorificSuffix", "honorificSuffixes", "nickName", "nickNames", Scopes.EMAIL, "emails", "photo", "photos", "url", "urls", "phoneNumber", "phoneNumbers", "organization", "organizations", "jobTitle", "jobTitles", "note", "notes"});
        contactDataMap = createTripleMap(new String[]{"id", "contact_id", null, "displayName", "data1", "vnd.android.cursor.item/name", "familyNames", "data3", "vnd.android.cursor.item/name", "givenNames", "data2", "vnd.android.cursor.item/name", "middleNames", "data5", "vnd.android.cursor.item/name", "additionalNames", "data5", "vnd.android.cursor.item/name", "honorificPrefixes", "data4", "vnd.android.cursor.item/name", "honorificSuffixes", "data6", "vnd.android.cursor.item/name", "nickNames", "data1", "vnd.android.cursor.item/nickname", "categories", "data1", "vnd.android.cursor.item/group_membership", "gender", "data1", CUSTOM_MIMETYPE_GENDER, "lastUpdated", "data1", CUSTOM_MIMETYPE_LASTUPDATED, "birthday", "data1", "vnd.android.cursor.item/contact_event", "anniversary", "data1", "vnd.android.cursor.item/contact_event", "emails", "data1", "vnd.android.cursor.item/email_v2", "photos", "data15", "vnd.android.cursor.item/photo", "urls", "data1", "vnd.android.cursor.item/website", "phoneNumbers", "data1", "vnd.android.cursor.item/phone_v2", "addresses", null, "vnd.android.cursor.item/postal-address_v2", "streetAddress", "data4", "vnd.android.cursor.item/postal-address_v2", "locality", "data6", "vnd.android.cursor.item/postal-address_v2", "region", "data8", "vnd.android.cursor.item/postal-address_v2", "postalCode", "data9", "vnd.android.cursor.item/postal-address_v2", "countryName", "data10", "vnd.android.cursor.item/postal-address_v2", "organizations", "data1", "vnd.android.cursor.item/organization", "jobTitles", "data4", "vnd.android.cursor.item/organization", "notes", "data1", "vnd.android.cursor.item/note", "impp", "data1", "vnd.android.cursor.item/im"});
        photoDataMap = createDataMap("data15");
        companyDataMap = createDataMap("data1");
        jobtitleDataMap = createDataMap("data4");
        noteDataMap = createDataMap("data1");
        emailDataMap = createValueMap("data1");
        websiteDataMap = createValueMap("data1");
        phoneDataMap = createValueMap("data1");
        imDataMap = createValueMap("data1");
        addressDataMap = createStringMap(new String[]{"data4", "streetAddress", "data6", "locality", "data8", "region", "data9", "postalCode", "data10", "countryName"});
        emailTypeMap = new C04851();
        websiteTypeMap = new C04862();
        addressTypeMap = new C04873();
        phoneTypeMap = new C04884();
        imTypeMap = new C04895();
        emailTypeValuesMap = new C04906();
        websiteTypeValuesMap = new C04917();
        addressTypeValuesMap = new C04928();
        phoneTypeValuesMap = new C04939();
        imTypeValuesMap = new HashMap<String, Integer>() {
            {
                put("work", Integer.valueOf(2));
                put("home", Integer.valueOf(1));
                put("other", Integer.valueOf(3));
            }
        };
        imProtocolMap = new HashMap<String, Integer>() {
            {
                put("aim", Integer.valueOf(0));
                put("msn", Integer.valueOf(1));
                put("ymsgr", Integer.valueOf(2));
                put("skype", Integer.valueOf(3));
                put("qq", Integer.valueOf(4));
                put("gtalk", Integer.valueOf(5));
                put("icq", Integer.valueOf(6));
                put("jabber", Integer.valueOf(7));
                put("netmeeting", Integer.valueOf(8));
            }
        };
        contactMapList = new ArrayList<ContactMap>() {
            {
                add(new ContactMap("emails", ContactConstants.emailDataMap, ContactConstants.emailTypeMap, ContactConstants.emailTypeValuesMap));
                add(new ContactMap("photos", ContactConstants.photoDataMap, null, null));
                add(new ContactMap("urls", ContactConstants.websiteDataMap, ContactConstants.websiteTypeMap, ContactConstants.websiteTypeValuesMap));
                add(new ContactMap("phoneNumbers", ContactConstants.phoneDataMap, ContactConstants.phoneTypeMap, ContactConstants.phoneTypeValuesMap));
                add(new ContactMap("addresses", ContactConstants.addressDataMap, ContactConstants.addressTypeMap, ContactConstants.addressTypeValuesMap));
                add(new ContactMap("organizations", ContactConstants.companyDataMap, null, null));
                add(new ContactMap("jobTitles", ContactConstants.jobtitleDataMap, null, null));
                add(new ContactMap("notes", ContactConstants.noteDataMap, null, null));
                add(new ContactMap("impp", ContactConstants.imDataMap, ContactConstants.imTypeMap, ContactConstants.imTypeValuesMap));
            }
        };
    }

    private static Map<String, Pair<String, String>> createTripleMap(String[] triplets) {
        Map<String, Pair<String, String>> result = new HashMap();
        for (int i = 0; i < triplets.length; i += 3) {
            result.put(triplets[i], new Pair(triplets[i + 1], triplets[i + 2]));
        }
        return Collections.unmodifiableMap(result);
    }

    private static Map<String, String> createStringMap(String[] pairs) {
        Map<String, String> result = new HashMap();
        for (int i = 0; i < pairs.length; i += 2) {
            result.put(pairs[i], pairs[i + 1]);
        }
        return Collections.unmodifiableMap(result);
    }

    private static Map<String, String> createDataMap(String name) {
        return createStringMap(new String[]{PushConstants.PARSE_COM_DATA, name});
    }

    private static Map<String, String> createValueMap(String name) {
        return createStringMap(new String[]{name, "value"});
    }
}
