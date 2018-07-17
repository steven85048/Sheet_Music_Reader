//
// Created by STEVEN-PC on 7/11/2018.
//

#include <android/log.h>
#define  LOG_TAG    "test_log"
#define  LOGE(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

#include "MeasureDetectionController.hpp"
#include <opencv2/opencv.hpp>
#include <memory>

namespace sheetmusicreader
{
    // static create method for java access (may want to move this to a factory class)
    std::shared_ptr< sheetmusicreader::MeasureReader > MeasureReader::Create()
    {
        return std::make_shared< sheetmusicreader::measuredetection::MeasureDetectionController > ();
    }
}

sheetmusicreader::measuredetection::MeasureDetectionController::MeasureDetectionController()
{
    LOGE("testing");
}

void sheetmusicreader::measuredetection::MeasureDetectionController::DetectMeasure ()
{
    cv::Mat(2, 2, CV_8UC3, cv::Scalar(0,0,255));

    LOGE("hello!");
}