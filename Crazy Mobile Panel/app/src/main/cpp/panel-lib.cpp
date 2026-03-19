#include <jni.h>
#include <string>
#include "MemoryHelper.h"

static MemoryHelper* g_memoryHelper = nullptr;

extern "C" JNIEXPORT jboolean JNICALL
Java_com_crazy_panel_NativeBridge_attach(JNIEnv* env, jobject thiz, jstring package_name) {
    const char* nativePackageName = env->GetStringUTFChars(package_name, nullptr);
    
    if (!g_memoryHelper) {
        g_memoryHelper = new MemoryHelper();
    }
    
    bool result = g_memoryHelper->attach(nativePackageName);
    
    env->ReleaseStringUTFChars(package_name, nativePackageName);
    return result;
}

extern "C" JNIEXPORT void JNICALL
Java_com_crazy_panel_NativeBridge_setAimbotActive(JNIEnv* env, jobject thiz, jboolean active) {
    if (g_memoryHelper) {
        g_memoryHelper->setAimbotActive(active);
    }
}

extern "C" JNIEXPORT jint JNICALL
Java_com_crazy_panel_NativeBridge_getPid(JNIEnv* env, jobject thiz) {
    if (g_memoryHelper) {
        return g_memoryHelper->getPid();
    }
    return -1;
}
