package SevenZip.Compression.LZ;

import android.support.v4.internal.view.SupportMenu;
import java.io.IOException;
import org.chromium.blink_public.web.WebTextInputFlags;
import org.chromium.ui.base.PageTransition;

public class BinTree extends InWindow {
    private static final int[] CrcTable;
    static final int kBT2HashSize = 65536;
    static final int kEmptyHashValue = 0;
    static final int kHash2Size = 1024;
    static final int kHash3Offset = 1024;
    static final int kHash3Size = 65536;
    static final int kMaxValForNormalize = 1073741823;
    static final int kStartMaxLen = 1;
    boolean HASH_ARRAY;
    int _cutValue;
    int _cyclicBufferPos;
    int _cyclicBufferSize;
    int[] _hash;
    int _hashMask;
    int _hashSizeSum;
    int _matchMaxLen;
    int[] _son;
    int kFixHashSize;
    int kMinMatchCheck;
    int kNumHashDirectBytes;

    public BinTree() {
        this._cyclicBufferSize = kEmptyHashValue;
        this._cutValue = PageTransition.CORE_MASK;
        this._hashSizeSum = kEmptyHashValue;
        this.HASH_ARRAY = true;
        this.kNumHashDirectBytes = kEmptyHashValue;
        this.kMinMatchCheck = 4;
        this.kFixHashSize = 66560;
    }

    public void SetType(int numHashBytes) {
        this.HASH_ARRAY = numHashBytes > 2;
        if (this.HASH_ARRAY) {
            this.kNumHashDirectBytes = kEmptyHashValue;
            this.kMinMatchCheck = 4;
            this.kFixHashSize = 66560;
            return;
        }
        this.kNumHashDirectBytes = 2;
        this.kMinMatchCheck = 3;
        this.kFixHashSize = kEmptyHashValue;
    }

    public void Init() throws IOException {
        super.Init();
        for (int i = kEmptyHashValue; i < this._hashSizeSum; i += kStartMaxLen) {
            this._hash[i] = kEmptyHashValue;
        }
        this._cyclicBufferPos = kEmptyHashValue;
        ReduceOffsets(-1);
    }

    public void MovePos() throws IOException {
        int i = this._cyclicBufferPos + kStartMaxLen;
        this._cyclicBufferPos = i;
        if (i >= this._cyclicBufferSize) {
            this._cyclicBufferPos = kEmptyHashValue;
        }
        super.MovePos();
        if (this._pos == kMaxValForNormalize) {
            Normalize();
        }
    }

    public boolean Create(int historySize, int keepAddBufferBefore, int matchMaxLen, int keepAddBufferAfter) {
        if (historySize > 1073741567) {
            return false;
        }
        this._cutValue = (matchMaxLen >> kStartMaxLen) + 16;
        super.Create(historySize + keepAddBufferBefore, matchMaxLen + keepAddBufferAfter, ((((historySize + keepAddBufferBefore) + matchMaxLen) + keepAddBufferAfter) / 2) + WebTextInputFlags.AutocapitalizeWords);
        this._matchMaxLen = matchMaxLen;
        int cyclicBufferSize = historySize + kStartMaxLen;
        if (this._cyclicBufferSize != cyclicBufferSize) {
            this._cyclicBufferSize = cyclicBufferSize;
            this._son = new int[(cyclicBufferSize * 2)];
        }
        int hs = kHash3Size;
        if (this.HASH_ARRAY) {
            hs = historySize - 1;
            hs |= hs >> kStartMaxLen;
            hs |= hs >> 2;
            hs |= hs >> 4;
            hs = ((hs | (hs >> 8)) >> kStartMaxLen) | SupportMenu.USER_MASK;
            if (hs > PageTransition.FORWARD_BACK) {
                hs >>= kStartMaxLen;
            }
            this._hashMask = hs;
            hs = (hs + kStartMaxLen) + this.kFixHashSize;
        }
        if (hs != this._hashSizeSum) {
            this._hashSizeSum = hs;
            this._hash = new int[hs];
        }
        return true;
    }

    public int GetMatches(int[] distances) throws IOException {
        int lenLimit;
        int matchMinPos;
        int i;
        int hashValue;
        int i2;
        if (this._pos + this._matchMaxLen <= this._streamPos) {
            lenLimit = this._matchMaxLen;
        } else {
            lenLimit = this._streamPos - this._pos;
            if (lenLimit < this.kMinMatchCheck) {
                MovePos();
                return kEmptyHashValue;
            }
        }
        int i3 = kEmptyHashValue;
        if (this._pos > this._cyclicBufferSize) {
            matchMinPos = this._pos - this._cyclicBufferSize;
        } else {
            matchMinPos = kEmptyHashValue;
        }
        int cur = this._bufferOffset + this._pos;
        int maxLen = kStartMaxLen;
        int hash2Value = kEmptyHashValue;
        int hash3Value = kEmptyHashValue;
        if (this.HASH_ARRAY) {
            i = this._bufferBase[cur] & PageTransition.CORE_MASK;
            int temp = CrcTable[r0] ^ (this._bufferBase[cur + kStartMaxLen] & PageTransition.CORE_MASK);
            hash2Value = temp & 1023;
            temp ^= (this._bufferBase[cur + 2] & PageTransition.CORE_MASK) << 8;
            hash3Value = temp & SupportMenu.USER_MASK;
            i = this._bufferBase[cur + 3] & PageTransition.CORE_MASK;
            hashValue = ((CrcTable[r0] << 5) ^ temp) & this._hashMask;
        } else {
            hashValue = (this._bufferBase[cur] & PageTransition.CORE_MASK) ^ ((this._bufferBase[cur + kStartMaxLen] & PageTransition.CORE_MASK) << 8);
        }
        int curMatch = this._hash[this.kFixHashSize + hashValue];
        if (this.HASH_ARRAY) {
            int curMatch2 = this._hash[hash2Value];
            int curMatch3 = this._hash[hash3Value + kHash3Offset];
            this._hash[hash2Value] = this._pos;
            this._hash[hash3Value + kHash3Offset] = this._pos;
            if (curMatch2 > matchMinPos) {
                if (this._bufferBase[this._bufferOffset + curMatch2] == this._bufferBase[cur]) {
                    i2 = kEmptyHashValue + kStartMaxLen;
                    maxLen = 2;
                    distances[kEmptyHashValue] = 2;
                    i3 = i2 + kStartMaxLen;
                    distances[i2] = (this._pos - curMatch2) - 1;
                }
            }
            if (curMatch3 > matchMinPos) {
                if (this._bufferBase[this._bufferOffset + curMatch3] == this._bufferBase[cur]) {
                    if (curMatch3 == curMatch2) {
                        i3 -= 2;
                    }
                    i2 = i3 + kStartMaxLen;
                    maxLen = 3;
                    distances[i3] = 3;
                    i3 = i2 + kStartMaxLen;
                    distances[i2] = (this._pos - curMatch3) - 1;
                    curMatch2 = curMatch3;
                }
            }
            if (i3 != 0 && curMatch2 == curMatch) {
                i3 -= 2;
                maxLen = kStartMaxLen;
            }
        }
        int[] iArr = this._hash;
        i = this.kFixHashSize;
        r0[r0 + hashValue] = this._pos;
        int ptr0 = (this._cyclicBufferPos << kStartMaxLen) + kStartMaxLen;
        int ptr1 = this._cyclicBufferPos << kStartMaxLen;
        int len1 = this.kNumHashDirectBytes;
        int len0 = len1;
        if (this.kNumHashDirectBytes != 0 && curMatch > matchMinPos) {
            byte[] bArr = this._bufferBase;
            i = this._bufferOffset;
            if (r0[(r0 + curMatch) + this.kNumHashDirectBytes] != this._bufferBase[this.kNumHashDirectBytes + cur]) {
                i2 = i3 + kStartMaxLen;
                maxLen = this.kNumHashDirectBytes;
                distances[i3] = maxLen;
                i3 = i2 + kStartMaxLen;
                distances[i2] = (this._pos - curMatch) - 1;
            }
        }
        int count = this._cutValue;
        i2 = i3;
        while (curMatch > matchMinPos) {
            int count2 = count - 1;
            if (count == 0) {
                break;
            }
            int delta = this._pos - curMatch;
            int i4 = this._cyclicBufferPos;
            if (delta <= r0) {
                i4 = this._cyclicBufferPos - delta;
            } else {
                i4 = this._cyclicBufferPos;
                i4 = (r0 - delta) + this._cyclicBufferSize;
            }
            int cyclicPos = i4 << kStartMaxLen;
            int pby1 = this._bufferOffset + curMatch;
            int len = Math.min(len0, len1);
            if (this._bufferBase[pby1 + len] == this._bufferBase[cur + len]) {
                do {
                    len += kStartMaxLen;
                    if (len == lenLimit) {
                        break;
                    }
                } while (this._bufferBase[pby1 + len] == this._bufferBase[cur + len]);
                if (maxLen < len) {
                    i3 = i2 + kStartMaxLen;
                    maxLen = len;
                    distances[i2] = len;
                    i2 = i3 + kStartMaxLen;
                    distances[i3] = delta - 1;
                    if (len == lenLimit) {
                        this._son[ptr1] = this._son[cyclicPos];
                        this._son[ptr0] = this._son[cyclicPos + kStartMaxLen];
                        i3 = i2;
                        break;
                    }
                }
            }
            i3 = i2;
            if ((this._bufferBase[pby1 + len] & PageTransition.CORE_MASK) < (this._bufferBase[cur + len] & PageTransition.CORE_MASK)) {
                this._son[ptr1] = curMatch;
                ptr1 = cyclicPos + kStartMaxLen;
                curMatch = this._son[ptr1];
                len1 = len;
            } else {
                this._son[ptr0] = curMatch;
                ptr0 = cyclicPos;
                curMatch = this._son[ptr0];
                len0 = len;
            }
            count = count2;
            i2 = i3;
        }
        iArr = this._son;
        this._son[ptr1] = kEmptyHashValue;
        iArr[ptr0] = kEmptyHashValue;
        i3 = i2;
        MovePos();
        return i3;
    }

    public void Skip(int num) throws IOException {
        do {
            int lenLimit;
            int matchMinPos;
            int i;
            int hashValue;
            if (this._pos + this._matchMaxLen <= this._streamPos) {
                lenLimit = this._matchMaxLen;
            } else {
                lenLimit = this._streamPos - this._pos;
                int i2 = this.kMinMatchCheck;
                if (lenLimit < r0) {
                    MovePos();
                    num--;
                }
            }
            if (this._pos > this._cyclicBufferSize) {
                matchMinPos = this._pos - this._cyclicBufferSize;
            } else {
                matchMinPos = kEmptyHashValue;
            }
            int cur = this._bufferOffset + this._pos;
            if (this.HASH_ARRAY) {
                i = this._bufferBase[cur] & PageTransition.CORE_MASK;
                int temp = CrcTable[r0] ^ (this._bufferBase[cur + kStartMaxLen] & PageTransition.CORE_MASK);
                int hash2Value = temp & 1023;
                this._hash[hash2Value] = this._pos;
                temp ^= (this._bufferBase[cur + 2] & PageTransition.CORE_MASK) << 8;
                int hash3Value = temp & SupportMenu.USER_MASK;
                this._hash[hash3Value + kHash3Offset] = this._pos;
                i = this._bufferBase[cur + 3] & PageTransition.CORE_MASK;
                hashValue = ((CrcTable[r0] << 5) ^ temp) & this._hashMask;
            } else {
                hashValue = (this._bufferBase[cur] & PageTransition.CORE_MASK) ^ ((this._bufferBase[cur + kStartMaxLen] & PageTransition.CORE_MASK) << 8);
            }
            int curMatch = this._hash[this.kFixHashSize + hashValue];
            int[] iArr = this._hash;
            i = this.kFixHashSize;
            r0[r0 + hashValue] = this._pos;
            int ptr0 = (this._cyclicBufferPos << kStartMaxLen) + kStartMaxLen;
            int ptr1 = this._cyclicBufferPos << kStartMaxLen;
            int len1 = this.kNumHashDirectBytes;
            int len0 = len1;
            int count = this._cutValue;
            while (curMatch > matchMinPos) {
                int count2 = count - 1;
                if (count == 0) {
                    break;
                }
                int delta = this._pos - curMatch;
                i2 = this._cyclicBufferPos;
                if (delta <= r0) {
                    i2 = this._cyclicBufferPos - delta;
                } else {
                    i2 = this._cyclicBufferPos;
                    i2 = (r0 - delta) + this._cyclicBufferSize;
                }
                int cyclicPos = i2 << kStartMaxLen;
                int pby1 = this._bufferOffset + curMatch;
                int len = Math.min(len0, len1);
                if (this._bufferBase[pby1 + len] == this._bufferBase[cur + len]) {
                    do {
                        len += kStartMaxLen;
                        if (len == lenLimit) {
                            break;
                        }
                    } while (this._bufferBase[pby1 + len] == this._bufferBase[cur + len]);
                    if (len == lenLimit) {
                        this._son[ptr1] = this._son[cyclicPos];
                        this._son[ptr0] = this._son[cyclicPos + kStartMaxLen];
                        break;
                    }
                }
                if ((this._bufferBase[pby1 + len] & PageTransition.CORE_MASK) < (this._bufferBase[cur + len] & PageTransition.CORE_MASK)) {
                    this._son[ptr1] = curMatch;
                    ptr1 = cyclicPos + kStartMaxLen;
                    curMatch = this._son[ptr1];
                    len1 = len;
                } else {
                    this._son[ptr0] = curMatch;
                    ptr0 = cyclicPos;
                    curMatch = this._son[ptr0];
                    len0 = len;
                }
                count = count2;
            }
            iArr = this._son;
            this._son[ptr1] = kEmptyHashValue;
            iArr[ptr0] = kEmptyHashValue;
            MovePos();
            num--;
        } while (num != 0);
    }

    void NormalizeLinks(int[] items, int numItems, int subValue) {
        for (int i = kEmptyHashValue; i < numItems; i += kStartMaxLen) {
            int value = items[i];
            if (value <= subValue) {
                value = kEmptyHashValue;
            } else {
                value -= subValue;
            }
            items[i] = value;
        }
    }

    void Normalize() {
        int subValue = this._pos - this._cyclicBufferSize;
        NormalizeLinks(this._son, this._cyclicBufferSize * 2, subValue);
        NormalizeLinks(this._hash, this._hashSizeSum, subValue);
        ReduceOffsets(subValue);
    }

    public void SetCutValue(int cutValue) {
        this._cutValue = cutValue;
    }

    static {
        CrcTable = new int[WebTextInputFlags.AutocapitalizeWords];
        for (int i = kEmptyHashValue; i < WebTextInputFlags.AutocapitalizeWords; i += kStartMaxLen) {
            int r = i;
            for (int j = kEmptyHashValue; j < 8; j += kStartMaxLen) {
                if ((r & kStartMaxLen) != 0) {
                    r = (r >>> kStartMaxLen) ^ -306674912;
                } else {
                    r >>>= kStartMaxLen;
                }
            }
            CrcTable[i] = r;
        }
    }
}
