package SevenZip;

import org.chromium.blink_public.web.WebTextInputFlags;
import org.chromium.ui.base.PageTransition;

public class CRC {
    public static int[] Table;
    int _value;

    public CRC() {
        this._value = -1;
    }

    static {
        Table = new int[WebTextInputFlags.AutocapitalizeWords];
        for (int i = 0; i < WebTextInputFlags.AutocapitalizeWords; i++) {
            int r = i;
            for (int j = 0; j < 8; j++) {
                if ((r & 1) != 0) {
                    r = (r >>> 1) ^ -306674912;
                } else {
                    r >>>= 1;
                }
            }
            Table[i] = r;
        }
    }

    public void Init() {
        this._value = -1;
    }

    public void Update(byte[] data, int offset, int size) {
        for (int i = 0; i < size; i++) {
            this._value = Table[(this._value ^ data[offset + i]) & PageTransition.CORE_MASK] ^ (this._value >>> 8);
        }
    }

    public void Update(byte[] data) {
        for (byte b : data) {
            this._value = Table[(this._value ^ b) & PageTransition.CORE_MASK] ^ (this._value >>> 8);
        }
    }

    public void UpdateByte(int b) {
        this._value = Table[(this._value ^ b) & PageTransition.CORE_MASK] ^ (this._value >>> 8);
    }

    public int GetDigest() {
        return this._value ^ -1;
    }
}
