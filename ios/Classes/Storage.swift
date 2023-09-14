//
//  Storage.swift
//  lpm_scan
//
//  Created by Christopher Tan on 9/14/23.
//

import Foundation

final class Storage {

    static let shared = Storage()
    private(set) var filePaths = [String]()

    private init() {}

    func addFile(_ filePath: String) {
        filePaths.append(filePath)
    }

}
