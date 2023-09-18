//
//  SampleViewController.swift
//  Pods
//
//  Created by Christopher Tan on 9/15/23.
//
import GSSDKCore
import GSSDKOCR
import UIKit

class SampleViewController: UIViewController {
    let scan: GSKScan
    private var imageView: UIImageView!
    private var currentProcessedImagePath: String?

    init(scan: GSKScan) {
        self.scan = scan

        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        imageView = UIImageView()
        imageView.contentMode = .scaleToFill
        view.addSubview(imageView)

        imageView.translatesAutoresizingMaskIntoConstraints = false
    }

    @IBAction func test(_ sender: Any) {
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(false, animated: true)

        processImage(autodetect: true)
    }

    private func processImage(autodetect: Bool) {
        DispatchQueue.global(qos: .userInitiated).async {
            let result: GSKProcessingResult
            do {
                result = try GSKScanProcessor().processImage(self.scan.image, configuration: .default())
            } catch {
                print("Error while processing scan: \(error)")
                return
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


}
