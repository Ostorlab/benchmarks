//
//  PhotoPreviewViewController.swift
//  PhotoShare Studio
//
//  Full screen photo preview with sharing options
//

import UIKit

class PhotoPreviewViewController: UIViewController {
    
    var photoName: String = ""
    var albumName: String = ""
    var photoImage: UIImage?
    var onShareRequested: ((String) -> Void)?
    
    private let imageView = UIImageView()
    private let scrollView = UIScrollView()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        setupImageView()
    }
    
    private func setupUI() {
        title = photoName
        view.backgroundColor = UIColor.black
        
        navigationItem.leftBarButtonItem = UIBarButtonItem(
            title: "Close",
            style: .done,
            target: self,
            action: #selector(closeTapped)
        )
        
        navigationItem.rightBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .action,
            target: self,
            action: #selector(shareTapped)
        )
        
        // Set navigation bar style for dark background
        if let navBar = navigationController?.navigationBar {
            navBar.barStyle = .black
            navBar.tintColor = UIColor.white
            navBar.titleTextAttributes = [.foregroundColor: UIColor.white]
        }
    }
    
    private func setupImageView() {
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        scrollView.delegate = self
        scrollView.minimumZoomScale = 0.5
        scrollView.maximumZoomScale = 3.0
        scrollView.showsVerticalScrollIndicator = false
        scrollView.showsHorizontalScrollIndicator = false
        
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.contentMode = .scaleAspectFit
        imageView.image = photoImage
        
        view.addSubview(scrollView)
        scrollView.addSubview(imageView)
        
        NSLayoutConstraint.activate([
            scrollView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            scrollView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            scrollView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            scrollView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            
            imageView.topAnchor.constraint(equalTo: scrollView.topAnchor),
            imageView.leadingAnchor.constraint(equalTo: scrollView.leadingAnchor),
            imageView.trailingAnchor.constraint(equalTo: scrollView.trailingAnchor),
            imageView.bottomAnchor.constraint(equalTo: scrollView.bottomAnchor),
            imageView.centerXAnchor.constraint(equalTo: scrollView.centerXAnchor),
            imageView.centerYAnchor.constraint(equalTo: scrollView.centerYAnchor)
        ])
    }
    
    @objc private func closeTapped() {
        dismiss(animated: true)
    }
    
    @objc private func shareTapped() {
        let alert = UIAlertController(title: "Share Photo", message: photoName, preferredStyle: .actionSheet)
        
        alert.addAction(UIAlertAction(title: "Share via WiFi", style: .default) { _ in
            self.dismiss(animated: true) {
                self.onShareRequested?(self.photoName)
            }
        })
        
        alert.addAction(UIAlertAction(title: "Copy Photo Name", style: .default) { _ in
            UIPasteboard.general.string = self.photoName
            self.showCopyConfirmation()
        })
        
        alert.addAction(UIAlertAction(title: "Photo Info", style: .default) { _ in
            self.showPhotoInfo()
        })
        
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
        
        if let popover = alert.popoverPresentationController {
            popover.barButtonItem = navigationItem.rightBarButtonItem
        }
        
        present(alert, animated: true)
    }
    
    private func showCopyConfirmation() {
        let alert = UIAlertController(title: "Copied", message: "Photo name copied to clipboard", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
    
    private func showPhotoInfo() {
        let message = """
        Photo: \(photoName)
        Album: \(albumName)
        
        Tap and hold to zoom
        Use pinch gestures to zoom in/out
        """
        
        let alert = UIAlertController(title: "Photo Information", message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
}

// MARK: - UIScrollViewDelegate
extension PhotoPreviewViewController: UIScrollViewDelegate {
    func viewForZooming(in scrollView: UIScrollView) -> UIView? {
        return imageView
    }
}