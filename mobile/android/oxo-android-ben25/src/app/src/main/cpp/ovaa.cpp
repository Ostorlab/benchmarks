#include <jni.h>
#include <malloc.h>

extern "C"
JNIEXPORT void JNICALL
Java_com_example_myapplication1_NativeDataHandler_freePtr(JNIEnv *env, jobject thiz,
                                                          jlong ptr) {
    free((void*) ptr);
}
