package org.chromium.content_public.browser;

import java.util.ArrayList;
import org.chromium.base.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
public class AccessibilitySnapshotNode {
    public int bgcolor;
    public boolean bold;
    public ArrayList<AccessibilitySnapshotNode> children;
    public String className;
    public int color;
    public boolean hasStyle;
    public int height;
    public boolean italic;
    public boolean lineThrough;
    public int scrollX;
    public int scrollY;
    public String text;
    public float textSize;
    public boolean underline;
    public int width;
    public int f0x;
    public int f1y;

    public AccessibilitySnapshotNode(int x, int y, int scrollX, int scrollY, int width, int height, String text, String className) {
        this.children = new ArrayList();
        this.f0x = x;
        this.f1y = y;
        this.scrollX = scrollX;
        this.scrollY = scrollY;
        this.width = width;
        this.height = height;
        this.text = text;
        this.className = className;
    }

    public void setStyle(int color, int bgcolor, float textSize, boolean bold, boolean italic, boolean underline, boolean lineThrough) {
        this.color = color;
        this.bgcolor = bgcolor;
        this.textSize = textSize;
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.lineThrough = lineThrough;
        this.hasStyle = true;
    }

    public void addChild(AccessibilitySnapshotNode node) {
        this.children.add(node);
    }
}
