//
//  AddDocumentViewController.swift
//  DocuShare Pro
//
//  Created by Ostorlab Ostorlab on 9/11/25.
//

import UIKit

class AddDocumentViewController: UIViewController {
    
    var onDocumentAdded: (() -> Void)?
    
    private let scrollView: UIScrollView = {
        let scrollView = UIScrollView()
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        return scrollView
    }()
    
    private let contentView: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    
    private let titleTextField: UITextField = {
        let textField = UITextField()
        textField.placeholder = "Document Title"
        textField.borderStyle = .roundedRect
        textField.font = UIFont.systemFont(ofSize: 16)
        textField.translatesAutoresizingMaskIntoConstraints = false
        return textField
    }()
    
    private let urlTextField: UITextField = {
        let textField = UITextField()
        textField.placeholder = "https://example.com/document.pdf"
        textField.borderStyle = .roundedRect
        textField.keyboardType = .URL
        textField.autocapitalizationType = .none
        textField.autocorrectionType = .no
        textField.font = UIFont.systemFont(ofSize: 16)
        textField.translatesAutoresizingMaskIntoConstraints = false
        return textField
    }()
    
    private let addButton: UIButton = {
        let button = UIButton(type: .system)
        button.setTitle("Add Document", for: .normal)
        button.titleLabel?.font = UIFont.systemFont(ofSize: 18, weight: .medium)
        button.backgroundColor = .systemBlue
        button.setTitleColor(.white, for: .normal)
        button.layer.cornerRadius = 8
        button.translatesAutoresizingMaskIntoConstraints = false
        return button
    }()
    
    private let instructionLabel: UILabel = {
        let label = UILabel()
        label.text = "Add documents by providing a direct URL. DocuShare Pro supports PDF, Word, HTML, and text documents."
        label.font = UIFont.systemFont(ofSize: 14, weight: .regular)
        label.textColor = .secondaryLabel
        label.numberOfLines = 0
        label.textAlignment = .center
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    
    private let exampleLabel: UILabel = {
        let label = UILabel()
        label.text = "Example test URLs:\n• https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf\n• https://file-examples.com/storage/fe68c1e7a0777bb13f0dc8c/2017/10/file_example_JPG_100kB.jpg"
        label.font = UIFont.systemFont(ofSize: 12, weight: .regular)
        label.textColor = .tertiaryLabel
        label.numberOfLines = 0
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }
    
    private func setupUI() {
        title = "Add Document"
        view.backgroundColor = UIColor.systemBackground
        
        navigationItem.leftBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .cancel,
            target: self,
            action: #selector(cancelTapped)
        )
        
        view.addSubview(scrollView)
        scrollView.addSubview(contentView)
        
        contentView.addSubview(instructionLabel)
        contentView.addSubview(titleTextField)
        contentView.addSubview(urlTextField)
        contentView.addSubview(addButton)
        contentView.addSubview(exampleLabel)
        
        addButton.addTarget(self, action: #selector(addButtonTapped), for: .touchUpInside)
        
        NSLayoutConstraint.activate([
            scrollView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            scrollView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            scrollView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            scrollView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            
            contentView.topAnchor.constraint(equalTo: scrollView.topAnchor),
            contentView.leadingAnchor.constraint(equalTo: scrollView.leadingAnchor),
            contentView.trailingAnchor.constraint(equalTo: scrollView.trailingAnchor),
            contentView.bottomAnchor.constraint(equalTo: scrollView.bottomAnchor),
            contentView.widthAnchor.constraint(equalTo: scrollView.widthAnchor),
            
            instructionLabel.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 24),
            instructionLabel.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 20),
            instructionLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -20),
            
            titleTextField.topAnchor.constraint(equalTo: instructionLabel.bottomAnchor, constant: 32),
            titleTextField.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 20),
            titleTextField.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -20),
            titleTextField.heightAnchor.constraint(equalToConstant: 44),
            
            urlTextField.topAnchor.constraint(equalTo: titleTextField.bottomAnchor, constant: 16),
            urlTextField.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 20),
            urlTextField.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -20),
            urlTextField.heightAnchor.constraint(equalToConstant: 44),
            
            addButton.topAnchor.constraint(equalTo: urlTextField.bottomAnchor, constant: 24),
            addButton.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 20),
            addButton.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -20),
            addButton.heightAnchor.constraint(equalToConstant: 50),
            
            exampleLabel.topAnchor.constraint(equalTo: addButton.bottomAnchor, constant: 32),
            exampleLabel.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 20),
            exampleLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -20),
            exampleLabel.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -20)
        ])
    }
    
    @objc private func cancelTapped() {
        dismiss(animated: true)
    }
    
    @objc private func addButtonTapped() {
        print("Add button tapped")
        print("URL text: '\(urlTextField.text ?? "nil")'")
        print("Title text: '\(titleTextField.text ?? "nil")'")
        
        guard let urlString = urlTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines),
              !urlString.isEmpty,
              let url = URL(string: urlString),
              url.scheme == "http" || url.scheme == "https" else {
            print("URL validation failed")
            showAlert(title: "Invalid URL", message: "Please enter a valid HTTP or HTTPS URL.")
            return
        }
        
        print("URL validation passed: \(url)")
        
        let title = titleTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty == false ?
            titleTextField.text! : extractTitleFromURL(url)
        
        print("Final title: '\(title)'")
        
        let document = Document(title: title, url: url)
        DocumentStorage.shared.addDocument(document)
        
        print("Document added. Total documents: \(DocumentStorage.shared.documents.count)")
        
        dismiss(animated: true) {
            self.onDocumentAdded?()
        }
    }
    
    private func extractTitleFromURL(_ url: URL) -> String {
        let fileName = url.lastPathComponent
        if fileName.isEmpty || fileName == "/" {
            return url.host ?? "Document"
        }
        
        let nameWithoutExtension = URL(fileURLWithPath: fileName).deletingPathExtension().lastPathComponent
        return nameWithoutExtension.isEmpty ? fileName : nameWithoutExtension
    }
    
    private func showAlert(title: String, message: String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
}