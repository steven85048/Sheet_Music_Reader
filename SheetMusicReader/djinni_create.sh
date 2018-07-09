#!/usr/bin/env bash
# Djinni file location
djinni_file="native/djinni-mapping/MeasureDetection.djinni"
# c++ namespace
namespace="sheetmusicreaderr"
# Objective-C prefix
objc_prefix="SMR"
# Java package
java_package="smr.sheetmusicreader"
### Script
# base directory
base_dir=$(cd "'dirname "0"'" && pwd)
# get java directory
java_dir=$(echo $java_package | tr . /)
# directories for the generated src files
cpp_out="$base_dir/generated-src/cpp"
objc_out="$base_dir/generated-src/objc"
jni_out="$base_dir/generated-src/jni"
java_out="$base_dir/generated-src/java/$java_dir"
# remove everything from these directories
rm -rf $cpp_out
rm -rf $jni_out
rm -rf $objc_out
rm -rf $java_out
# Create our files (using the djinni submodule)
deps/djinni/src/run \
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