// AUTOGENERATED FILE - DO NOT MODIFY!
// This file generated by Djinni from MeasureDetection.djinni

#pragma once

#include <memory>

namespace sheetmusicreader {

class MeasureReader {
public:
    virtual ~MeasureReader() {}

    static std::shared_ptr<MeasureReader> Create();

    virtual void DetectMeasure() = 0;
};

}  // namespace sheetmusicreader
