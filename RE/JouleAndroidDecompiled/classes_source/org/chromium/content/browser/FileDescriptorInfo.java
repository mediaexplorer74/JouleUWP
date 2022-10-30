package org.chromium.content.browser;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class FileDescriptorInfo implements Parcelable {
    public static final Creator<FileDescriptorInfo> CREATOR;
    public final ParcelFileDescriptor mFd;
    public final int mId;
    public final long mOffset;
    public final long mSize;

    /* renamed from: org.chromium.content.browser.FileDescriptorInfo.1 */
    static class C03421 implements Creator<FileDescriptorInfo> {
        C03421() {
        }

        public FileDescriptorInfo createFromParcel(Parcel in) {
            return new FileDescriptorInfo(in);
        }

        public FileDescriptorInfo[] newArray(int size) {
            return new FileDescriptorInfo[size];
        }
    }

    FileDescriptorInfo(int id, ParcelFileDescriptor fd, long offset, long size) {
        this.mId = id;
        this.mFd = fd;
        this.mOffset = offset;
        this.mSize = size;
    }

    FileDescriptorInfo(Parcel in) {
        this.mId = in.readInt();
        this.mFd = (ParcelFileDescriptor) in.readParcelable(null);
        this.mOffset = in.readLong();
        this.mSize = in.readLong();
    }

    public int describeContents() {
        return 1;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeParcelable(this.mFd, 1);
        dest.writeLong(this.mOffset);
        dest.writeLong(this.mSize);
    }

    static {
        CREATOR = new C03421();
    }
}
