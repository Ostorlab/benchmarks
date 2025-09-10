//
//  ImportDocumentViewController.swift
//  DocumentViewerPro
//
//  Created by Ostorlab Ostorlab on 9/9/25.
//

import UIKit

class ImportDocumentViewController: UIViewController {
    @IBOutlet weak var titleTextField: UITextField!
    @IBOutlet weak var urlTextField: UITextField!
    @IBOutlet weak var contentTextView: UITextView!
    @IBOutlet weak var importFromURLButton: UIButton!
    @IBOutlet weak var saveButton: UIButton!
    @IBOutlet weak var segmentedControl: UISegmentedControl!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }
    
    private func setupUI() {
        title = "Import Document"
        
        navigationItem.leftBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .cancel,
            target: self,
            action: #selector(cancelTapped)
        )
        
        contentTextView.layer.borderColor = UIColor.systemGray4.cgColor
        contentTextView.layer.borderWidth = 1
        contentTextView.layer.cornerRadius = 8
        contentTextView.font = UIFont.systemFont(ofSize: 16)
        
        importFromURLButton.backgroundColor = .systemBlue
        importFromURLButton.setTitleColor(.white, for: .normal)
        importFromURLButton.layer.cornerRadius = 8
        
        saveButton.backgroundColor = .systemGreen
        saveButton.setTitleColor(.white, for: .normal)
        saveButton.layer.cornerRadius = 8
        
        titleTextField.placeholder = "Document title"
        urlTextField.placeholder = "https://example.com/document.html"
        contentTextView.text = "Paste or type your document content here..."
        contentTextView.textColor = .placeholderText
    }
    
    @IBAction func segmentedControlChanged(_ sender: UISegmentedControl) {
        
    }
    
    @IBAction func importFromURLTapped(_ sender: UIButton) {
        guard let urlString = urlTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines),
              !urlString.isEmpty,
              let url = URL(string: urlString) else {
            showAlert(title: "Invalid URL", message: "Please enter a valid URL")
            return
        }
        
        importFromURLButton.isEnabled = false
        importFromURLButton.setTitle("Loading...", for: .normal)
        
        fetchContentFromURL(url) { [weak self] content in
            DispatchQueue.main.async {
                self?.importFromURLButton.isEnabled = true
                self?.importFromURLButton.setTitle("Import from URL", for: .normal)
                
                if let content = content {
                    self?.contentTextView.text = content
                    self?.contentTextView.textColor = .label
                    
                    if self?.titleTextField.text?.isEmpty == true {
                        self?.titleTextField.text = url.lastPathComponent.isEmpty ? "Imported Document" : url.lastPathComponent
                    }
                } else {
                    self?.showAlert(title: "Import Failed", message: "Failed to fetch content from URL")
                }
            }
        }
    }
    
    @IBAction func saveButtonTapped(_ sender: UIButton) {
        guard let title = titleTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines),
              !title.isEmpty else {
            showAlert(title: "Missing Title", message: "Please enter a document title")
            return
        }
        
        guard let content = contentTextView.text?.trimmingCharacters(in: .whitespacesAndNewlines),
              !content.isEmpty,
              content != "Paste or type your document content here..." else {
            showAlert(title: "Missing Content", message: "Please enter document content")
            return
        }
        
        let documentType: DocumentType
        switch segmentedControl.selectedSegmentIndex {
        case 0: documentType = .html
        case 1: documentType = .text
        case 2: documentType = .markdown
        default: documentType = .html
        }
        
        let document = Document(title: title, content: content, type: documentType)
        DocumentManager.shared.addDocument(document)
        
        navigationController?.popViewController(animated: true)
    }
    
    @objc private func cancelTapped() {
        navigationController?.popViewController(animated: true)
    }
    
    private func fetchContentFromURL(_ url: URL, completion: @escaping (String?) -> Void) {
        URLSession.shared.dataTask(with: url) { data, response, error in
            if let error = error {
                print("Error fetching URL: \(error)")
                completion(nil)
                return
            }
            
            guard let data = data else {
                completion(nil)
                return
            }
            
            let content = String(data: data, encoding: .utf8) ?? String(data: data, encoding: .ascii)
            completion(content)
        }.resume()
    }
    
    private func showAlert(title: String, message: String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
}

extension ImportDocumentViewController: UITextViewDelegate {
    func textViewDidBeginEditing(_ textView: UITextView) {
        if textView.textColor == .placeholderText {
            textView.text = ""
            textView.textColor = .label
        }
    }
    
    func textViewDidEndEditing(_ textView: UITextView) {
        if textView.text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            textView.text = "Paste or type your document content here..."
            textView.textColor = .placeholderText
        }
    }
}