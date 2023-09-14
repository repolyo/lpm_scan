//
//  PostProcessingViewController.swift
//  lpm_scan
//
//  Created by Christopher Tan on 9/14/23.
//
import UIKit
import GSSDKCore
import GSSDKOCR

final class PostProcessingViewController: UIViewController {

    let scan: GSKScan
    let quadrangle: GSKQuadrangle
    private var currentProcessedImagePath: String?
    private var enhancementBarButtonItem: UIBarButtonItem!
    private var imageView: UIImageView!
    private lazy var curvatureButton: UIButton = {
        let button = UIButton(type: .custom)
        button.setTitle("Correction", for: .normal)
        button.layer.cornerRadius = 4
        button.addTarget(self, action: #selector(correctCurvature), for: .touchUpInside)
        button.sizeToFit()
        return button
    }()
    private var filterType: GSKFilterType = .none
    private var curvatureCorrectionEnabled = false
    private var previewController: UIDocumentInteractionController?

    init(scan: GSKScan, quadrangle: GSKQuadrangle) {
        self.scan = scan
        self.quadrangle = quadrangle

        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .black

        imageView = UIImageView()
        imageView.contentMode = .scaleAspectFit
        view.addSubview(imageView)

        imageView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            imageView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            imageView.leadingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.leadingAnchor),
            imageView.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor),
            imageView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor),
        ])

        enhancementBarButtonItem = UIBarButtonItem(title: "Filter", style: .plain, target: self, action: #selector(edit))

        let correctionDistortionBarButtonItem = UIBarButtonItem(customView: curvatureButton)

        navigationItem.leftBarButtonItem = UIBarButtonItem(barButtonSystemItem: .add, target: self, action: #selector(add))

        navigationItem.rightBarButtonItems = [
            UIBarButtonItem(barButtonSystemItem: .compose, target: self, action: #selector(crop)),
            UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(done)),
            correctionDistortionBarButtonItem,
            enhancementBarButtonItem
        ]
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(false, animated: true)

        refreshImageView()
        configureCurvatureButtonColor()
        processImage(autodetect: true)
    }

    // MARK: - Private
    @objc private func add() {
        Storage.shared.addFile(currentProcessedImagePath!)
        navigationController?.popToRootViewController(animated: true)
    }

    @objc private func crop() {
        let editFrameViewController = EditFrameViewController(scan: scan)
        self.navigationController?.pushViewController(editFrameViewController, animated: true)
    }

    @objc private func edit() {
        let alertController = UIAlertController(title: NSLocalizedString("Choose a filter to apply", comment: ""), message: nil, preferredStyle: .actionSheet)
        alertController.popoverPresentationController?.barButtonItem = enhancementBarButtonItem

        alertController.addAction(UIAlertAction(title: NSLocalizedString("None", comment: ""), style: .default, handler: { action in
            self.filterType = GSKFilterType.none
            self.processImage(autodetect: false)
        }))

        alertController.addAction(UIAlertAction(title: NSLocalizedString("Monochrome", comment: ""), style: .default, handler: { action in
            self.filterType = GSKFilterType.monochrome
            self.processImage(autodetect: false)
        }))

        alertController.addAction(UIAlertAction(title: NSLocalizedString("Black & White", comment: ""), style: .default, handler: { action in
            self.filterType = GSKFilterType.blackAndWhite
            self.processImage(autodetect: false)
        }))

        alertController.addAction(UIAlertAction(title: NSLocalizedString("Color", comment: ""), style: .default, handler: { action in
            self.filterType = GSKFilterType.color
            self.processImage(autodetect: false)
        }))

        alertController.addAction(UIAlertAction(title: NSLocalizedString("Photo", comment: ""), style: .default, handler: { action in
            self.filterType = GSKFilterType.photo
            self.processImage(autodetect: false)
        }))

        alertController.addAction(UIAlertAction(title: NSLocalizedString("Cancel", comment: ""), style: .cancel, handler: { _ in
            //
        }))

        present(alertController, animated: true, completion: nil)
    }

    @objc private func correctCurvature() {
        curvatureCorrectionEnabled.toggle()
        configureCurvatureButtonColor()
        processImage(autodetect: false)
    }

    @objc private func done() {
        Storage.shared.addFile(currentProcessedImagePath!)

        guard let url = generatePDF(nil) else {
            print("Cannot generate url")
            return
        }

        previewController = UIDocumentInteractionController(url: url)
        previewController?.delegate = self
        previewController?.presentPreview(animated: true)
//        let shareViewController = SampleViewController()
//        navigationController?.pushViewController(shareViewController, animated: true)
    }


    // MARK: - Private

    /// This is where the PDF generation happens
    /// - First, create the information to generate the PDF document
    /// - Then generate the PDF document
    private func generatePDF(_ ocrResult: GSKOCRResult?) -> URL? {
        var textLayouts = [String: GSKTextLayout]()

        // Prepare document for PDF generator

        // First we generate a list of pages.
        let pages = Storage.shared.filePaths.map { filePath -> GSKPDFPage in
            // For each page, we specify the document and a size in inches.
            let page = GSKPDFPage(filePath: filePath, inchesSize: GSKPDFSize(width: 8.27, height: 11.69), textLayout: textLayouts[filePath])
            return page
        }

        // We then create a GSKPDFDocument which holds the general information about the PDF document to generate
        let document = GSKPDFDocument(title: "sample.pdf", password: nil, keywords: nil, creationDate: Date(), lastUpdate: Date(), pages: pages)

        // Generate PDF

        // Last, we use the SDK to generate the actual PDF document
        let paths = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)
        let documentsDirectory = paths[0] as NSString
        let outputFilePath = documentsDirectory.appendingPathComponent("output.pdf")

        do {
            // Remove before creating.
            try FileManager.default.removeItem(atPath: outputFilePath)
        } catch {
            // Swallow error
        }

        do {
            try GSKPDF.generate(document, toPath: outputFilePath)
            return URL(fileURLWithPath: outputFilePath)
        } catch {
            print("Error while generating the PDF document: \(error)")
            return nil
        }
    }

    /// This applies the powerful SDK image processing methods:
    /// - correct the perspective of the scan with the quadrangle set at the previous set
    /// - attempt to detect the best post-processing, or use the user defined post-processing
    /// - enhance the image according to this post-processing
    /// - optionally correct the image distortion (book/folded receipt curvature,).
    private func processImage(autodetect: Bool) {
        DispatchQueue.global(qos: .userInitiated).async {
            let perspectiveCorrectionConfiguration = GSKPerspectiveCorrectionConfiguration(quadrangle: self.quadrangle)
            let curvatureCorrectionConfiguration = GSKCurvatureCorrectionConfiguration(curvatureCorrection: self.curvatureCorrectionEnabled)

            let enhancementConfiguration: GSKEnhancementConfiguration
            if autodetect {
                enhancementConfiguration = GSKEnhancementConfiguration.automatic()
            } else {
                enhancementConfiguration = GSKEnhancementConfiguration(filter: self.filterType)
            }

            let result: GSKProcessingResult
            do {
                let configuration = GSKProcessingConfiguration(perspectiveCorrectionConfiguration: perspectiveCorrectionConfiguration,
                                                               curvatureCorrectionConfiguration: curvatureCorrectionConfiguration,
                                                               enhancementConfiguration: enhancementConfiguration,
                                                               rotationConfiguration: .automatic(),
                                                               outputConfiguration: .png())
                result = try GSKScanProcessor().processImage(self.scan.image, configuration: configuration)
            } catch {
                print("Error while processing scan: \(error)")
                return
            }

            if let currentProcessedImagePath = self.currentProcessedImagePath {
                do {
                    try FileManager.default.removeItem(atPath: currentProcessedImagePath)
                } catch {
                    print("Unable to remove previous enhanced file")
                }
            }
            self.currentProcessedImagePath = result.processedImagePath

            DispatchQueue.main.async {
                self.refreshImageView()
            }
        }
    }

    private func refreshImageView() {
        if let currentProcessedImagePath = currentProcessedImagePath {
            imageView.image = UIImage(contentsOfFile: currentProcessedImagePath)
        }
    }

    private func configureCurvatureButtonColor() {
        let blueColor = UIColor(red: 0, green: 122/255, blue: 1, alpha: 1)
        if curvatureCorrectionEnabled {
            curvatureButton.layer.backgroundColor = blueColor.cgColor
            curvatureButton.setTitleColor(.white, for: .normal)
        } else {
            curvatureButton.layer.backgroundColor = UIColor.clear.cgColor
            curvatureButton.setTitleColor(blueColor, for: .normal)
        }
    }

}


extension PostProcessingViewController: UIDocumentInteractionControllerDelegate {

    func documentInteractionControllerViewControllerForPreview(_ controller: UIDocumentInteractionController) -> UIViewController {
        return self
    }

}
