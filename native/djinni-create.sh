#!/usr/bin/env bash
# Djinni file location
djinni_file="djinni/MeasureDetection.djinni"
# c++ namespace
namespace="sheetmusicreader"
# Objective-C prefix
objc_prefix="SMR"
# Java package
java_package="smr.sheetmusicreader"
### Script
# get java directory
java_dir=$(echo $java_package | tr . /)
# directories for the generated src files
cpp_out="../generated-src/cpp"
objc_out="../generated-src/objc"
jni_out="../generated-src/jni"
java_out="../generated-src/java/$java_dir"
# remove everything from these directories
rm -rf $cpp_out
rm -rf $jni_out
rm -rf $objc_out
rm -rf $java_out
# Create our files (using the djinni submodule)
dependencies/deps/djinni/src/run \
    --java-out $java-out \
    --java-package $java_package \
    --ident-java-field mFooBar \
    --cpp-out $cpp_out \
    --cpp-namespace $namespace \
    --jni-out $jni_out \
    --ident-jni-class NativeFooBar \
    --ident-jni-file NativeFooBar \
    --objc-out $objc_out \
    --objc-type-prefix $objc_prefix \
    --objcpp-out $objc_out \
    --idl $djinni_file