//
// Created by STEVEN-PC on 7/11/2018.
//

#pragma once

#include "measure_reader.hpp"

namespace sheetmusicreader
{

namespace measuredetection
{

class MeasureDetectionController: public MeasureReader
{
    public:
        MeasureDetectionController();
        void DetectMeasure() override;
    private:
};

} // END of measuredetection

} // END of sheetmusicreader