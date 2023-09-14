// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from sdk-ocr.djinni

#pragma once

#include <cstdint>

namespace ge {

/** A callback object that you can inject in the OCR engine to obtain progress information. */
class OCREngineProgressListener {
public:
    virtual ~OCREngineProgressListener() {}

    /** Progress ranges from 0 to 100 */
    virtual void updateProgress(int32_t progress) = 0;
};

}  // namespace ge
