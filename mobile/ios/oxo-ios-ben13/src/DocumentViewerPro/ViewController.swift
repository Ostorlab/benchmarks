//
//  DocumentListViewController.swift
//  DocumentViewerPro
//
//  Created by Ostorlab Ostorlab on 9/9/25.
//

import UIKit

class DocumentListViewController: UIViewController {
    @IBOutlet weak var tableView: UITableView!
    
    private var documents: [Document] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        loadDocuments()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        loadDocuments()
    }
    
    private func setupUI() {
        title = "My Documents"
        
        navigationItem.rightBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .add,
            target: self,
            action: #selector(addDocumentTapped)
        )
        
        tableView.delegate = self
        tableView.dataSource = self
        tableView.rowHeight = 80
    }
    
    private func loadDocuments() {
        documents = DocumentManager.shared.getAllDocuments()
        tableView.reloadData()
    }
    
    @objc private func addDocumentTapped() {
        performSegue(withIdentifier: "showImportDocument", sender: nil)
    }
}

extension DocumentListViewController: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return documents.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "DocumentCell", for: indexPath)
        let document = documents[indexPath.row]
        
        cell.textLabel?.text = document.title
        cell.detailTextLabel?.text = "\(document.type.rawValue) • \(formatDate(document.dateCreated))"
        cell.imageView?.image = UIImage(systemName: document.type.iconName)
        cell.accessoryType = .disclosureIndicator
        
        return cell
    }
    
    private func formatDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        return formatter.string(from: date)
    }
}

extension DocumentListViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        let document = documents[indexPath.row]
        performSegue(withIdentifier: "showDocumentViewer", sender: document)
    }
    
    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            let document = documents[indexPath.row]
            DocumentManager.shared.deleteDocument(withId: document.id)
            documents.remove(at: indexPath.row)
            tableView.deleteRows(at: [indexPath], with: .fade)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showDocumentViewer",
           let viewerVC = segue.destination as? DocumentViewerViewController,
           let document = sender as? Document {
            viewerVC.document = document
        }
    }
}

