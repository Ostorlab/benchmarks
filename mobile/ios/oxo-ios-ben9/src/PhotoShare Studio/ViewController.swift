//
//  ViewController.swift
//  PhotoShare Studio
//
//  Created by Ostorlab Ostorlab on 9/8/25.
//

import UIKit

class ViewController: UIViewController {
    
    @IBOutlet weak var albumCollectionView: UICollectionView!
    @IBOutlet weak var wifiSharingButton: UIButton!
    @IBOutlet weak var statusLabel: UILabel!
    
    private var albums: [String] = []
    private var isWifiSharingActive = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        loadAlbums()
        setupCollectionView()
        setupNotifications()
        
        // Check if login is required
        if AuthManager.shared.requiresLogin() {
            showLoginScreen()
        }
    }
    
    private func setupNotifications() {
        NotificationCenter.default.addObserver(self, selector: #selector(userDidLogout), name: .userDidLogout, object: nil)
    }
    
    @objc private func userDidLogout() {
        showLoginScreen()
    }
    
    private func showLoginScreen() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        if let loginVC = storyboard.instantiateViewController(withIdentifier: "LoginViewController") as? LoginViewController {
            let navController = UINavigationController(rootViewController: loginVC)
            navController.modalPresentationStyle = .fullScreen
            present(navController, animated: true)
        }
    }
    
    private func setupUI() {
        title = "PhotoShare Studio"
        view.backgroundColor = UIColor.systemBackground
        
        navigationController?.navigationBar.prefersLargeTitles = true
        navigationController?.navigationBar.tintColor = UIColor.systemBlue
        
        // Add navigation bar buttons
        navigationItem.rightBarButtonItem = UIBarButtonItem(
            image: UIImage(systemName: "gearshape.fill"),
            style: .plain,
            target: self,
            action: #selector(showSettings)
        )
        
        navigationItem.leftBarButtonItem = UIBarButtonItem(
            image: UIImage(systemName: "plus.circle.fill"),
            style: .plain,
            target: self,
            action: #selector(importPhotos)
        )
        
        statusLabel.text = "WiFi Sharing: Inactive"
        statusLabel.textColor = UIColor.systemGray
        
        wifiSharingButton.setTitle("Start WiFi Sharing", for: .normal)
        wifiSharingButton.backgroundColor = UIColor.systemBlue
        wifiSharingButton.setTitleColor(UIColor.white, for: .normal)
        wifiSharingButton.layer.cornerRadius = 8
        wifiSharingButton.addTarget(self, action: #selector(toggleWifiSharing), for: .touchUpInside)
    }
    
    private func setupCollectionView() {
        let layout = UICollectionViewFlowLayout()
        layout.itemSize = CGSize(width: 150, height: 150)
        layout.minimumInteritemSpacing = 10
        layout.minimumLineSpacing = 10
        layout.sectionInset = UIEdgeInsets(top: 20, left: 20, bottom: 20, right: 20)
        
        albumCollectionView.collectionViewLayout = layout
        albumCollectionView.delegate = self
        albumCollectionView.dataSource = self
        albumCollectionView.backgroundColor = UIColor.clear
        albumCollectionView.register(AlbumCollectionViewCell.self, forCellWithReuseIdentifier: "AlbumCell")
    }
    
    private func loadAlbums() {
        albums = PhotoManager.shared.getAllAlbums()
        
        // Add default albums if none exist
        if albums.isEmpty {
            albums = ["Vacation", "Family", "Work", "Recent"]
        }
        
        albumCollectionView.reloadData()
    }
    
    @objc private func toggleWifiSharing() {
        if isWifiSharingActive {
            WiFiSharingServer.shared.stopServer()
            wifiSharingButton.setTitle("Start WiFi Sharing", for: .normal)
            wifiSharingButton.backgroundColor = UIColor.systemBlue
            statusLabel.text = "WiFi Sharing: Inactive"
            statusLabel.textColor = UIColor.systemGray
            isWifiSharingActive = false
        } else {
            WiFiSharingServer.shared.startServer()
            wifiSharingButton.setTitle("Stop WiFi Sharing", for: .normal)
            wifiSharingButton.backgroundColor = UIColor.systemRed
            statusLabel.text = "WiFi Sharing: Active on port 8080"
            statusLabel.textColor = UIColor.systemGreen
            isWifiSharingActive = true
            
            showWifiSharingInstructions()
        }
    }
    
    private func showWifiSharingInstructions() {
        let deviceIP = WiFiSharingServer.shared.getDeviceIPAddress()
        let wifiURL = "http://\(deviceIP):8080"
        
        let alert = UIAlertController(title: "WiFi Sharing Active", 
                                    message: "Connect to the same WiFi network and visit:\n\(wifiURL)\n\nYou can now transfer photos wirelessly!", 
                                    preferredStyle: .alert)
        
        alert.addAction(UIAlertAction(title: "Open in Safari", style: .default) { _ in
            if let url = URL(string: wifiURL) {
                UIApplication.shared.open(url)
            }
        })
        
        alert.addAction(UIAlertAction(title: "Copy Link", style: .default) { _ in
            UIPasteboard.general.string = wifiURL
            let copyAlert = UIAlertController(title: "Copied", message: "WiFi sharing URL copied to clipboard", preferredStyle: .alert)
            copyAlert.addAction(UIAlertAction(title: "OK", style: .default))
            self.present(copyAlert, animated: true)
        })
        
        alert.addAction(UIAlertAction(title: "OK", style: .cancel))
        present(alert, animated: true)
    }
    
    @objc private func showSettings() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        if let settingsVC = storyboard.instantiateViewController(withIdentifier: "SettingsViewController") as? SettingsViewController {
            let navController = UINavigationController(rootViewController: settingsVC)
            present(navController, animated: true)
        }
    }
    
    @objc private func importPhotos() {
        let alert = UIAlertController(title: "Import Photos", message: "Choose import method", preferredStyle: .actionSheet)
        
        alert.addAction(UIAlertAction(title: "Camera Roll", style: .default) { _ in
            self.simulatePhotoImport(method: "Camera Roll")
        })
        
        alert.addAction(UIAlertAction(title: "Take Photo", style: .default) { _ in
            self.simulatePhotoImport(method: "Camera")
        })
        
        alert.addAction(UIAlertAction(title: "Import from Files", style: .default) { _ in
            self.simulatePhotoImport(method: "Files")
        })
        
        alert.addAction(UIAlertAction(title: "Create New Album", style: .default) { _ in
            self.createNewAlbum()
        })
        
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
        
        if let popover = alert.popoverPresentationController {
            popover.barButtonItem = navigationItem.leftBarButtonItem
        }
        
        present(alert, animated: true)
    }
    
    private func simulatePhotoImport(method: String) {
        let loadingAlert = UIAlertController(title: "Importing Photos", message: "Importing from \(method)...", preferredStyle: .alert)
        present(loadingAlert, animated: true)
        
        // Simulate import process
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
            loadingAlert.dismiss(animated: true) {
                // Simulate adding new photos to Recent album
                self.addSimulatedPhotos(method: method)
                
                let successAlert = UIAlertController(title: "Import Complete", 
                                                   message: "Successfully imported 3 photos to Recent album", 
                                                   preferredStyle: .alert)
                successAlert.addAction(UIAlertAction(title: "View Album", style: .default) { _ in
                    self.openRecentAlbum()
                })
                successAlert.addAction(UIAlertAction(title: "OK", style: .default))
                self.present(successAlert, animated: true)
            }
        }
    }
    
    private func addSimulatedPhotos(method: String) {
        let newPhotoNames: [String]
        
        switch method {
        case "Camera Roll":
            newPhotoNames = ["imported_photo_1.jpg", "imported_photo_2.jpg", "imported_photo_3.jpg"]
        case "Camera":
            newPhotoNames = ["new_photo_\(Date().timeIntervalSince1970).jpg"]
        case "Files":
            newPhotoNames = ["document_scan.jpg", "receipt_photo.jpg"]
        default:
            newPhotoNames = ["new_import.jpg"]
        }
        
        // Add to Recent album
        let recentPath = PhotoManager.shared.getAlbumsDirectory().appendingPathComponent("Recent")
        try? FileManager.default.createDirectory(at: recentPath, withIntermediateDirectories: true)
        
        for photoName in newPhotoNames {
            let photoPath = recentPath.appendingPathComponent(photoName)
            
            // Create a new placeholder image
            if let imageData = createImportedPhotoData(photoName: photoName, method: method) {
                try? imageData.write(to: photoPath)
            }
        }
        
        // Reload collection view
        loadAlbums()
    }
    
    private func createImportedPhotoData(photoName: String, method: String) -> Data? {
        let size = CGSize(width: 300, height: 300)
        
        UIGraphicsBeginImageContext(size)
        
        // Different colors based on import method
        let color: UIColor
        switch method {
        case "Camera Roll": color = .systemBlue
        case "Camera": color = .systemGreen  
        case "Files": color = .systemOrange
        default: color = .systemPurple
        }
        
        color.setFill()
        UIRectFill(CGRect(origin: .zero, size: size))
        
        // Add import method indicator
        let text = "📷 \(method)\n\(photoName.replacingOccurrences(of: ".jpg", with: ""))"
        let attributes: [NSAttributedString.Key: Any] = [
            .foregroundColor: UIColor.white,
            .font: UIFont.boldSystemFont(ofSize: 14)
        ]
        
        let textSize = text.boundingRect(with: size, options: .usesLineFragmentOrigin, attributes: attributes, context: nil).size
        let textRect = CGRect(
            x: (size.width - textSize.width) / 2,
            y: (size.height - textSize.height) / 2,
            width: textSize.width,
            height: textSize.height
        )
        
        text.draw(in: textRect, withAttributes: attributes)
        
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return image?.jpegData(compressionQuality: 0.8)
    }
    
    private func createNewAlbum() {
        let alert = UIAlertController(title: "New Album", message: "Enter album name", preferredStyle: .alert)
        
        alert.addTextField { textField in
            textField.placeholder = "Album name"
            textField.text = "My Album"
        }
        
        alert.addAction(UIAlertAction(title: "Create", style: .default) { _ in
            if let albumName = alert.textFields?.first?.text, !albumName.isEmpty {
                self.createAlbum(name: albumName)
            }
        })
        
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
        present(alert, animated: true)
    }
    
    private func createAlbum(name: String) {
        let albumPath = PhotoManager.shared.getAlbumsDirectory().appendingPathComponent(name)
        try? FileManager.default.createDirectory(at: albumPath, withIntermediateDirectories: true)
        
        // Create sample photo in new album
        let samplePhotoPath = albumPath.appendingPathComponent("sample_photo.jpg")
        if let imageData = createImportedPhotoData(photoName: "sample_photo.jpg", method: "New Album") {
            try? imageData.write(to: samplePhotoPath)
        }
        
        loadAlbums()
        
        let alert = UIAlertController(title: "Album Created", message: "Successfully created '\(name)' album with sample photo", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
    
    private func openRecentAlbum() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        if let albumVC = storyboard.instantiateViewController(withIdentifier: "AlbumViewController") as? AlbumViewController {
            albumVC.albumName = "Recent"
            navigationController?.pushViewController(albumVC, animated: true)
        }
    }
}

// MARK: - UICollectionView DataSource and Delegate
extension ViewController: UICollectionViewDataSource, UICollectionViewDelegate {
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return albums.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "AlbumCell", for: indexPath) as! AlbumCollectionViewCell
        cell.configure(with: albums[indexPath.item])
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let albumName = albums[indexPath.item]
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        
        if let albumVC = storyboard.instantiateViewController(withIdentifier: "AlbumViewController") as? AlbumViewController {
            albumVC.albumName = albumName
            navigationController?.pushViewController(albumVC, animated: true)
        }
    }
}

// MARK: - Custom Collection View Cell
class AlbumCollectionViewCell: UICollectionViewCell {
    private let imageView = UIImageView()
    private let titleLabel = UILabel()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupCell()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupCell()
    }
    
    private func setupCell() {
        contentView.backgroundColor = UIColor.systemGray6
        contentView.layer.cornerRadius = 12
        contentView.layer.shadowColor = UIColor.black.cgColor
        contentView.layer.shadowOffset = CGSize(width: 0, height: 2)
        contentView.layer.shadowOpacity = 0.1
        contentView.layer.shadowRadius = 4
        
        imageView.contentMode = .scaleAspectFit
        imageView.tintColor = UIColor.systemBlue
        imageView.translatesAutoresizingMaskIntoConstraints = false
        
        titleLabel.textAlignment = .center
        titleLabel.font = UIFont.systemFont(ofSize: 16, weight: .medium)
        titleLabel.textColor = UIColor.label
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        
        contentView.addSubview(imageView)
        contentView.addSubview(titleLabel)
        
        NSLayoutConstraint.activate([
            imageView.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 20),
            imageView.centerXAnchor.constraint(equalTo: contentView.centerXAnchor),
            imageView.widthAnchor.constraint(equalToConstant: 60),
            imageView.heightAnchor.constraint(equalToConstant: 60),
            
            titleLabel.topAnchor.constraint(equalTo: imageView.bottomAnchor, constant: 10),
            titleLabel.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 8),
            titleLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -8),
            titleLabel.bottomAnchor.constraint(lessThanOrEqualTo: contentView.bottomAnchor, constant: -10)
        ])
    }
    
    func configure(with albumName: String) {
        titleLabel.text = albumName
        imageView.image = UIImage(systemName: "folder.fill")
    }
}

