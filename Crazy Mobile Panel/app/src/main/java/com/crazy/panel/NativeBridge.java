package com.crazy.panel;

public class NativeBridge {
    static {
        System.loadLibrary("panel-lib");
    }

    // Connect to the game process
    public native boolean attach(String packageName);

    // Apply Aimbot Active
    public native void setAimbotActive(boolean active);

    // Get current process ID
    public native int getPid();
}
