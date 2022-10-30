package android.support.v4.view;

import android.view.PointerIcon;
import android.view.View;

class ViewCompatApi24 {
    ViewCompatApi24() {
    }

    public static void setPointerCapture(View view) {
        view.setPointerCapture();
    }

    public static boolean hasPointerCapture(View view) {
        return view.hasPointerCapture();
    }

    public static void releasePointerCapture(View view) {
        view.releasePointerCapture();
    }

    public static void setPointerIcon(View view, Object pointerIcon) {
        view.setPointerIcon((PointerIcon) pointerIcon);
    }
}
