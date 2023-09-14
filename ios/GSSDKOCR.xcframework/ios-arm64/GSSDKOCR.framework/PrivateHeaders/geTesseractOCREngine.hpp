//
//  geTesseractOCREngine.hpp
//  GSSDK
//
//  Created by Bruno Virlet on Nov 7, 2018
//
//

#ifndef geTesseractOCREngine_hpp
#define geTesseractOCREngine_hpp

#include <memory>

#include "geOCREngine.hpp"
#include "geOCREngineConfiguration.hpp"

namespace tesseract {

class TessBaseAPI;

}

class ETEXT_DESC;

namespace ge {

class TesseractOCREngine : public OCREngine {
 private:
  // OCR
  std::shared_ptr<tesseract::TessBaseAPI> tesseract;
  std::shared_ptr<ETEXT_DESC> monitor;
  OCREngineConfiguration configuration;

  std::shared_ptr<Logger> logger;

  bool init();
  bool initialized;
 public:
  std::shared_ptr<OCREngineProgressListener> progress_listener;
  TesseractOCREngine(const OCREngineConfiguration &configuration, const std::shared_ptr<Logger> &logger,
    const std::shared_ptr<OCREngineProgressListener> & progress_listener);
  OCREngineResult recognizeText(const OCREngineInput & input);
};

}  // namespace ge

#endif
