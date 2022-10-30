package android.support.v4.print;

import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintAttributes.Builder;

class PrintHelperApi23 extends PrintHelperApi20 {
    protected Builder copyAttributes(PrintAttributes other) {
        return super.copyAttributes(other).setDuplexMode(other.getDuplexMode());
    }

    PrintHelperApi23(Context context) {
        super(context);
        this.mIsMinMarginsHandlingCorrect = false;
    }
}
