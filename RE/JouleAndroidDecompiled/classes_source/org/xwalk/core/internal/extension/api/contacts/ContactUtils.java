package org.xwalk.core.internal.extension.api.contacts;

import android.annotation.TargetApi;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import com.adobe.phonegap.push.PushConstants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.chromium.content.browser.accessibility.captioning.CaptioningChangeDelegate;
import org.xwalk.core.internal.extension.api.messaging.MessagingSmsConsts;

public class ContactUtils {
    private static final String TAG = "ContactUtils";
    public ContentResolver mResolver;

    public java.lang.String getGroupId(java.lang.String r14) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x0076 in list []
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:42)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:58)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r13 = this;
        r11 = 0;
        r9 = "deleted=? and group_visible=?";
        r6 = 0;
        r0 = r13.mResolver;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r1 = android.provider.ContactsContract.Groups.CONTENT_URI;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r2 = 0;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r3 = "deleted=? and group_visible=?";	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r4 = 2;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r4 = new java.lang.String[r4];	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r5 = 0;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r12 = "0";	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r4[r5] = r12;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r5 = 1;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r12 = "1";	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r4[r5] = r12;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r5 = 0;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r6 = r0.query(r1, r2, r3, r4, r5);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r6.moveToFirst();	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r8 = 0;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
    L_0x0021:
        r0 = r6.getCount();	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        if (r8 >= r0) goto L_0x004d;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
    L_0x0027:
        r0 = "title";	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r0 = r6.getColumnIndex(r0);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r10 = r6.getString(r0);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r0 = r10.equals(r14);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        if (r0 == 0) goto L_0x0047;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
    L_0x0037:
        r0 = "_id";	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r0 = r6.getColumnIndex(r0);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r0 = r6.getString(r0);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        if (r6 == 0) goto L_0x0046;
    L_0x0043:
        r6.close();
    L_0x0046:
        return r0;
    L_0x0047:
        r6.moveToNext();	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r8 = r8 + 1;
        goto L_0x0021;
    L_0x004d:
        if (r6 == 0) goto L_0x0052;
    L_0x004f:
        r6.close();
    L_0x0052:
        r0 = r11;
        goto L_0x0046;
    L_0x0054:
        r7 = move-exception;
        r0 = "ContactUtils";	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r1 = new java.lang.StringBuilder;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r1.<init>();	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r2 = "getGroupId: ";	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r1 = r1.append(r2);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r2 = r7.toString();	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r1 = r1.append(r2);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r1 = r1.toString();	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        android.util.Log.e(r0, r1);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        if (r6 == 0) goto L_0x0076;
    L_0x0073:
        r6.close();
    L_0x0076:
        r0 = r11;
        goto L_0x0046;
    L_0x0078:
        r0 = move-exception;
        if (r6 == 0) goto L_0x007e;
    L_0x007b:
        r6.close();
    L_0x007e:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xwalk.core.internal.extension.api.contacts.ContactUtils.getGroupId(java.lang.String):java.lang.String");
    }

    public java.lang.String getGroupTitle(java.lang.String r14) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x0076 in list []
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:42)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:58)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r13 = this;
        r11 = 0;
        r10 = "deleted=? and group_visible=?";
        r6 = 0;
        r0 = r13.mResolver;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r1 = android.provider.ContactsContract.Groups.CONTENT_URI;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r2 = 0;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r3 = "deleted=? and group_visible=?";	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r4 = 2;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r4 = new java.lang.String[r4];	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r5 = 0;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r12 = "0";	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r4[r5] = r12;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r5 = 1;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r12 = "1";	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r4[r5] = r12;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r5 = 0;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r6 = r0.query(r1, r2, r3, r4, r5);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r6.moveToFirst();	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r8 = 0;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
    L_0x0021:
        r0 = r6.getCount();	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        if (r8 >= r0) goto L_0x004d;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
    L_0x0027:
        r0 = "_id";	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r0 = r6.getColumnIndex(r0);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r9 = r6.getString(r0);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r0 = r9.equals(r14);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        if (r0 == 0) goto L_0x0047;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
    L_0x0037:
        r0 = "title";	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r0 = r6.getColumnIndex(r0);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r0 = r6.getString(r0);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        if (r6 == 0) goto L_0x0046;
    L_0x0043:
        r6.close();
    L_0x0046:
        return r0;
    L_0x0047:
        r6.moveToNext();	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r8 = r8 + 1;
        goto L_0x0021;
    L_0x004d:
        if (r6 == 0) goto L_0x0052;
    L_0x004f:
        r6.close();
    L_0x0052:
        r0 = r11;
        goto L_0x0046;
    L_0x0054:
        r7 = move-exception;
        r0 = "ContactUtils";	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r1 = new java.lang.StringBuilder;	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r1.<init>();	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r2 = "getGroupTitle: ";	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r1 = r1.append(r2);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r2 = r7.toString();	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r1 = r1.append(r2);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        r1 = r1.toString();	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        android.util.Log.e(r0, r1);	 Catch:{ SecurityException -> 0x0054, all -> 0x0078 }
        if (r6 == 0) goto L_0x0076;
    L_0x0073:
        r6.close();
    L_0x0076:
        r0 = r11;
        goto L_0x0046;
    L_0x0078:
        r0 = move-exception;
        if (r6 == 0) goto L_0x007e;
    L_0x007b:
        r6.close();
    L_0x007e:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xwalk.core.internal.extension.api.contacts.ContactUtils.getGroupTitle(java.lang.String):java.lang.String");
    }

    public java.lang.String getId(java.lang.String r10) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x0055 in list []
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:42)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:58)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r9 = this;
        r8 = 0;
        r6 = 0;
        r0 = r9.mResolver;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r1 = android.provider.ContactsContract.RawContacts.CONTENT_URI;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r2 = 1;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r2 = new java.lang.String[r2];	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r3 = 0;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r4 = "contact_id";	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r2[r3] = r4;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r3 = "_id=?";	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r4 = 1;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r4 = new java.lang.String[r4];	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r5 = 0;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r4[r5] = r10;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r5 = 0;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r6 = r0.query(r1, r2, r3, r4, r5);	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r0 = r6.moveToFirst();	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        if (r0 == 0) goto L_0x002c;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
    L_0x0021:
        r0 = 0;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r0 = r6.getString(r0);	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        if (r6 == 0) goto L_0x002b;
    L_0x0028:
        r6.close();
    L_0x002b:
        return r0;
    L_0x002c:
        if (r6 == 0) goto L_0x0031;
    L_0x002e:
        r6.close();
    L_0x0031:
        r0 = r8;
        goto L_0x002b;
    L_0x0033:
        r7 = move-exception;
        r0 = "ContactUtils";	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r1 = new java.lang.StringBuilder;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r1.<init>();	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r2 = "getId: ";	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r1 = r1.append(r2);	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r2 = r7.toString();	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r1 = r1.append(r2);	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r1 = r1.toString();	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        android.util.Log.e(r0, r1);	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        if (r6 == 0) goto L_0x0055;
    L_0x0052:
        r6.close();
    L_0x0055:
        r0 = r8;
        goto L_0x002b;
    L_0x0057:
        r0 = move-exception;
        if (r6 == 0) goto L_0x005d;
    L_0x005a:
        r6.close();
    L_0x005d:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xwalk.core.internal.extension.api.contacts.ContactUtils.getId(java.lang.String):java.lang.String");
    }

    public java.lang.String getRawId(java.lang.String r10) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x0055 in list []
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:42)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:58)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r9 = this;
        r8 = 0;
        r6 = 0;
        r0 = r9.mResolver;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r1 = android.provider.ContactsContract.RawContacts.CONTENT_URI;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r2 = 1;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r2 = new java.lang.String[r2];	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r3 = 0;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r4 = "_id";	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r2[r3] = r4;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r3 = "contact_id=?";	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r4 = 1;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r4 = new java.lang.String[r4];	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r5 = 0;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r4[r5] = r10;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r5 = 0;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r6 = r0.query(r1, r2, r3, r4, r5);	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r0 = r6.moveToFirst();	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        if (r0 == 0) goto L_0x002c;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
    L_0x0021:
        r0 = 0;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r0 = r6.getString(r0);	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        if (r6 == 0) goto L_0x002b;
    L_0x0028:
        r6.close();
    L_0x002b:
        return r0;
    L_0x002c:
        if (r6 == 0) goto L_0x0031;
    L_0x002e:
        r6.close();
    L_0x0031:
        r0 = r8;
        goto L_0x002b;
    L_0x0033:
        r7 = move-exception;
        r0 = "ContactUtils";	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r1 = new java.lang.StringBuilder;	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r1.<init>();	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r2 = "getRawId: ";	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r1 = r1.append(r2);	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r2 = r7.toString();	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r1 = r1.append(r2);	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        r1 = r1.toString();	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        android.util.Log.e(r0, r1);	 Catch:{ SecurityException -> 0x0033, all -> 0x0057 }
        if (r6 == 0) goto L_0x0055;
    L_0x0052:
        r6.close();
    L_0x0055:
        r0 = r8;
        goto L_0x002b;
    L_0x0057:
        r0 = move-exception;
        if (r6 == 0) goto L_0x005d;
    L_0x005a:
        r6.close();
    L_0x005d:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xwalk.core.internal.extension.api.contacts.ContactUtils.getRawId(java.lang.String):java.lang.String");
    }

    public ContactUtils(ContentResolver resolver) {
        this.mResolver = resolver;
    }

    public static <K, V> K getKeyFromValue(Map<K, V> map, V value) {
        for (Entry<K, V> entry : map.entrySet()) {
            if (value != null && value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static String makeQuestionMarkList(Set<String> strings) {
        String ret = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
        for (int i = 0; i < strings.size(); i++) {
            ret = ret + "?,";
        }
        return ret.substring(0, ret.length() - 1);
    }

    public boolean hasID(String id) {
        if (id == null) {
            return false;
        }
        Cursor c = null;
        try {
            c = this.mResolver.query(Contacts.CONTENT_URI, null, "_id = ?", new String[]{id}, null);
            boolean z = c.getCount() != 0;
            if (c != null) {
                c.close();
            }
            return z;
        } catch (SecurityException e) {
            Log.e(TAG, "hasID: " + e.toString());
            if (c == null) {
                return false;
            }
            c.close();
            return false;
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
        }
    }

    @TargetApi(18)
    public String getLastUpdated(long contactId) {
        String str = null;
        String[] projection = new String[]{"contact_last_updated_timestamp"};
        Cursor cursor = this.mResolver.query(ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId), projection, null, null, null);
        try {
            if (cursor.moveToNext()) {
                str = timeConvertToJS(cursor.getLong(0));
            } else if (cursor != null) {
                cursor.close();
            }
            return str;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public Set<String> getCurrentRawIds() {
        Cursor c = null;
        try {
            c = this.mResolver.query(RawContacts.CONTENT_URI, new String[]{MessagingSmsConsts.ID}, null, null, null);
            Set<String> hashSet = new HashSet();
            while (c.moveToNext()) {
                hashSet.add(c.getString(0));
            }
            if (c == null) {
                return hashSet;
            }
            c.close();
            return hashSet;
        } catch (SecurityException e) {
            Log.e(TAG, "getCurrentRawIds: " + e.toString());
            return null;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public String[] getDefaultAccountNameAndType() {
        ArrayList<ContentProviderOperation> ops = new ArrayList();
        ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI).withValue("account_name", null).withValue("account_type", null).build());
        try {
            Uri rawContactUri = null;
            long rawContactId = 0;
            for (ContentProviderResult contentProviderResult : this.mResolver.applyBatch("com.android.contacts", ops)) {
                rawContactUri = contentProviderResult.uri;
                rawContactId = ContentUris.parseId(rawContactUri);
            }
            Cursor c = null;
            String accountType = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
            String accountName = CaptioningChangeDelegate.DEFAULT_CAPTIONING_PREF_VALUE;
            try {
                c = this.mResolver.query(RawContacts.CONTENT_URI, new String[]{"account_type", "account_name"}, "_id=?", new String[]{String.valueOf(rawContactId)}, null);
                if (c.moveToFirst() && !c.isAfterLast()) {
                    accountType = c.getString(c.getColumnIndex("account_type"));
                    accountName = c.getString(c.getColumnIndex("account_name"));
                }
                if (c != null) {
                    c.close();
                }
                this.mResolver.delete(rawContactUri, null, null);
                return new String[]{accountName, accountType};
            } catch (SecurityException e) {
                Log.e(TAG, "getDefaultAccountNameAndType: " + e.toString());
                if (c == null) {
                    return null;
                }
                c.close();
                return null;
            } catch (Throwable th) {
                if (c != null) {
                    c.close();
                }
            }
        } catch (Exception e2) {
            if ((e2 instanceof RemoteException) || (e2 instanceof OperationApplicationException) || (e2 instanceof SecurityException)) {
                Log.e(TAG, "getDefaultAccountNameAndType - Failed to apply batch: " + e2.toString());
                return null;
            }
            throw new RuntimeException(e2);
        }
    }

    public String getEnsuredGroupId(String groupTitle) {
        String groupId = getGroupId(groupTitle);
        if (groupId == null) {
            newGroup(groupTitle);
            groupId = getGroupId(groupTitle);
            if (groupId == null) {
                return null;
            }
        }
        return groupId;
    }

    public void newGroup(String groupTitle) {
        String[] accountNameType = getDefaultAccountNameAndType();
        ArrayList<ContentProviderOperation> o = new ArrayList();
        o.add(ContentProviderOperation.newInsert(Groups.CONTENT_URI).withValue(PushConstants.TITLE, groupTitle).withValue("group_visible", Boolean.valueOf(true)).withValue("account_name", accountNameType[0]).withValue("account_type", accountNameType[1]).build());
        try {
            this.mResolver.applyBatch("com.android.contacts", o);
        } catch (Exception e) {
            if ((e instanceof RemoteException) || (e instanceof OperationApplicationException) || (e instanceof SecurityException)) {
                Log.e(TAG, "newGroup - Failed to create new contact group: " + e.toString());
                return;
            }
            throw new RuntimeException(e);
        }
    }

    public void cleanByMimeType(String id, String mimeType) {
        this.mResolver.delete(Data.CONTENT_URI, String.format("%s = ? AND %s = ?", new Object[]{"contact_id", "mimetype"}), new String[]{id, mimeType});
    }

    public String dateTrim(String string) {
        String date = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            date = df.format(df.parse(string));
        } catch (ParseException e) {
            Log.e(TAG, "dateFormat - parse failed: " + e.toString());
        }
        return date;
    }

    private String timeConvertToJS(long seconds) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date(seconds));
    }
}
