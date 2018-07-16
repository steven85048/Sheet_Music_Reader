// AUTOGENERATED FILE - DO NOT MODIFY!
// This file generated by Djinni from MeasureDetection.djinni

#include "NativeHelloWorld.hpp"  // my header
#include "Marshal.hpp"

namespace djinni_generated {

NativeHelloWorld::NativeHelloWorld() : ::djinni::JniInterface<::sheetmusicreader::HelloWorld, NativeHelloWorld>("smr/sheetmusicreader/HelloWorld$CppProxy") {}

NativeHelloWorld::~NativeHelloWorld() = default;


CJNIEXPORT void JNICALL Java_smr_sheetmusicreader_HelloWorld_00024CppProxy_nativeDestroy(JNIEnv* jniEnv, jobject /*this*/, jlong nativeRef)
{
    try {
        DJINNI_FUNCTION_PROLOGUE1(jniEnv, nativeRef);
        delete reinterpret_cast<::djinni::CppProxyHandle<::sheetmusicreader::HelloWorld>*>(nativeRef);
    } JNI_TRANSLATE_EXCEPTIONS_RETURN(jniEnv, )
}

CJNIEXPORT jstring JNICALL Java_smr_sheetmusicreader_HelloWorld_00024CppProxy_native_1getHelloWorld(JNIEnv* jniEnv, jobject /*this*/, jlong nativeRef)
{
    try {
        DJINNI_FUNCTION_PROLOGUE1(jniEnv, nativeRef);
        const auto& ref = ::djinni::objectFromHandleAddress<::sheetmusicreader::HelloWorld>(nativeRef);
        auto r = ref->get_hello_world();
        return ::djinni::release(::djinni::String::fromCpp(jniEnv, r));
    } JNI_TRANSLATE_EXCEPTIONS_RETURN(jniEnv, 0 /* value doesn't matter */)
}

}  // namespace djinni_generated
