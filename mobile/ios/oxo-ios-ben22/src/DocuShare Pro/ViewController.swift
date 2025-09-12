//
//  ViewController.swift
//  DocuShare Pro - Document Library
//
//  Created by Ostorlab Ostorlab on 9/11/25.
//

import UIKit

class ViewController: UIViewController {
    
    @IBOutlet weak var documentsTableView: UITableView!
    
    private let documentStorage = DocumentStorage.shared
    private let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .none
        return formatter
    }()

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        setupTableView()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        documentsTableView.reloadData()
    }
    
    private func setupUI() {
        title = "DocuShare Pro"
        view.backgroundColor = UIColor.systemBackground
        
        navigationController?.navigationBar.prefersLargeTitles = true
        navigationItem.rightBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .add,
            target: self,
            action: #selector(addDocumentTapped)
        )
    }
    
    private func setupTableView() {
        documentsTableView.delegate = self
        documentsTableView.dataSource = self
        documentsTableView.register(DocumentTableViewCell.self, forCellReuseIdentifier: "DocumentCell")
        documentsTableView.rowHeight = 80
        documentsTableView.separatorStyle = .singleLine
    }
    
    @objc private func addDocumentTapped() {
        let addDocumentVC = AddDocumentViewController()
        addDocumentVC.onDocumentAdded = { [weak self] in
            self?.documentsTableView.reloadData()
        }
        let navController = UINavigationController(rootViewController: addDocumentVC)
        present(navController, animated: true)
    }
}

extension ViewController: UITableViewDataSource, UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return documentStorage.documents.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "DocumentCell", for: indexPath) as! DocumentTableViewCell
        let document = documentStorage.documents[indexPath.row]
        cell.configure(with: document, dateFormatter: dateFormatter)
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        let document = documentStorage.documents[indexPath.row]
        let viewerVC = DocumentViewerViewController()
        viewerVC.document = document
        
        navigationController?.pushViewController(viewerVC, animated: true)
    }
    
    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            documentStorage.removeDocument(at: indexPath.row)
            tableView.deleteRows(at: [indexPath], with: .automatic)
        }
    }
}

