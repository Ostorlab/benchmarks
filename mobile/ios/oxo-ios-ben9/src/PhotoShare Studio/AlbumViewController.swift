//
//  AlbumViewController.swift
//  PhotoShare Studio
//
//  Album detail view showing photos in selected album
//

import UIKit

class AlbumViewController: UIViewController {
    
    @IBOutlet weak var photoCollectionView: UICollectionView!
    
    var albumName: String = ""
    private var photos: [String] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        loadPhotos()
        setupCollectionView()
    }
    
    private func setupUI() {
        title = albumName
        view.backgroundColor = UIColor.systemBackground
        
        // Add WiFi sharing status in navigation
        let wifiButton = UIBarButtonItem(title: "📶 WiFi", style: .plain, target: self, action: #selector(showWifiStatus))
        navigationItem.rightBarButtonItem = wifiButton
    }
    
    private func setupCollectionView() {
        let layout = UICollectionViewFlowLayout()
        layout.itemSize = CGSize(width: 100, height: 100)
        layout.minimumInteritemSpacing = 10
        layout.minimumLineSpacing = 10
        layout.sectionInset = UIEdgeInsets(top: 20, left: 20, bottom: 20, right: 20)
        
        photoCollectionView.collectionViewLayout = layout
        photoCollectionView.delegate = self
        photoCollectionView.dataSource = self
        photoCollectionView.backgroundColor = UIColor.clear
        photoCollectionView.register(PhotoCollectionViewCell.self, forCellWithReuseIdentifier: "PhotoCell")
    }
    
    private func loadPhotos() {
        photos = PhotoManager.shared.getPhotosInAlbum(albumName)
        
        // Add sample photos if none exist
        if photos.isEmpty {
            photos = ["photo_001.jpg", "photo_002.jpg", "landscape.jpg", "portrait.png"]
        }
        
        photoCollectionView.reloadData()
    }
    
    @objc private func showWifiStatus() {
        if WiFiSharingServer.shared.isServerRunning() {
            let deviceIP = WiFiSharingServer.shared.getDeviceIPAddress()
            let albumURL = "http://\(deviceIP):8080/album/\(albumName)"
            let homeURL = "http://\(deviceIP):8080"
            let message = "WiFi Sharing is ACTIVE.\n\nAccess this album at:\n\(albumURL)\n\nOr home page at:\n\(homeURL)"
            
            let alert = UIAlertController(title: "WiFi Sharing Active", message: message, preferredStyle: .alert)
            
            alert.addAction(UIAlertAction(title: "Open Album Page", style: .default) { _ in
                if let url = URL(string: albumURL) {
                    UIApplication.shared.open(url)
                }
            })
            
            alert.addAction(UIAlertAction(title: "Open Home Page", style: .default) { _ in
                if let url = URL(string: homeURL) {
                    UIApplication.shared.open(url)
                }
            })
            
            alert.addAction(UIAlertAction(title: "Copy Album Link", style: .default) { _ in
                UIPasteboard.general.string = albumURL
                let copyAlert = UIAlertController(title: "Copied", message: "Album URL copied to clipboard", preferredStyle: .alert)
                copyAlert.addAction(UIAlertAction(title: "OK", style: .default))
                self.present(copyAlert, animated: true)
            })
            
            alert.addAction(UIAlertAction(title: "Stop Server", style: .destructive) { _ in
                WiFiSharingServer.shared.stopServer()
                self.updateWifiButton()
            })
            
            alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
            present(alert, animated: true)
        } else {
            let alert = UIAlertController(title: "WiFi Sharing Inactive", 
                                        message: "Start WiFi sharing to access photos from other devices on the same network.", 
                                        preferredStyle: .alert)
            
            alert.addAction(UIAlertAction(title: "Start Server", style: .default) { _ in
                WiFiSharingServer.shared.startServer()
                self.updateWifiButton()
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
                    self.showWifiStatus() // Show the active status
                }
            })
            
            alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
            present(alert, animated: true)
        }
    }
    
    private func updateWifiButton() {
        let isActive = WiFiSharingServer.shared.isServerRunning()
        navigationItem.rightBarButtonItem?.title = isActive ? "📶 ON" : "📶 WiFi"
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        updateWifiButton()
    }
}

// MARK: - UICollectionView DataSource and Delegate
extension AlbumViewController: UICollectionViewDataSource, UICollectionViewDelegate {
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return photos.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "PhotoCell", for: indexPath) as! PhotoCollectionViewCell
        cell.configure(with: photos[indexPath.item])
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let photoName = photos[indexPath.item]
        showPhotoDetail(photoName)
    }
    
    private func showPhotoDetail(_ photoName: String) {
        // Show photo preview first
        showPhotoPreview(photoName: photoName)
    }
    
    private func showPhotoPreview(photoName: String) {
        // Create a custom view controller for photo preview
        let photoPreviewVC = PhotoPreviewViewController()
        photoPreviewVC.photoName = photoName
        photoPreviewVC.albumName = albumName
        
        // Try to load the actual image
        if let imageData = loadPhotoData(photoName: photoName),
           let image = UIImage(data: imageData) {
            photoPreviewVC.photoImage = image
        }
        
        photoPreviewVC.onShareRequested = { [weak self] photoName in
            self?.checkServerAndShowSharing(for: photoName)
        }
        
        let navController = UINavigationController(rootViewController: photoPreviewVC)
        present(navController, animated: true)
    }
    
    private func loadPhotoData(photoName: String) -> Data? {
        let documentsPath = PhotoManager.shared.getDocumentsDirectory()
        let photoPath = documentsPath.appendingPathComponent("Albums/\(albumName)/\(photoName)")
        return try? Data(contentsOf: photoPath)
    }
    
    private func checkServerAndShowSharing(for photoName: String) {
        if WiFiSharingServer.shared.isServerRunning() {
            showWifiSharingInfo(for: photoName)
        } else {
            let alert = UIAlertController(title: "WiFi Sharing Inactive", 
                                        message: "WiFi sharing server is not running. Would you like to start it?", 
                                        preferredStyle: .alert)
            
            alert.addAction(UIAlertAction(title: "Start Server", style: .default) { _ in
                WiFiSharingServer.shared.startServer()
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
                    self.showWifiSharingInfo(for: photoName)
                }
            })
            
            alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
            present(alert, animated: true)
        }
    }
    
    private func showWifiSharingInfo(for photoName: String) {
        let deviceIP = WiFiSharingServer.shared.getDeviceIPAddress()
        let photoURL = "http://\(deviceIP):8080/photo?file=\(albumName)/\(photoName)"
        let viewURL = "http://\(deviceIP):8080/view?photo=\(albumName)/\(photoName)"
        
        let alert = UIAlertController(title: "WiFi Photo Sharing", 
                                    message: "Share this photo via WiFi:\n\nDirect download:\n\(photoURL)\n\nView page:\n\(viewURL)", 
                                    preferredStyle: .alert)
        
        alert.addAction(UIAlertAction(title: "Open Photo Page", style: .default) { _ in
            if let url = URL(string: viewURL) {
                UIApplication.shared.open(url)
            }
        })
        
        alert.addAction(UIAlertAction(title: "Download Photo", style: .default) { _ in
            if let url = URL(string: photoURL) {
                UIApplication.shared.open(url)
            }
        })
        
        alert.addAction(UIAlertAction(title: "Copy Link", style: .default) { _ in
            UIPasteboard.general.string = viewURL
            let copyAlert = UIAlertController(title: "Copied", message: "Photo viewing URL copied to clipboard", preferredStyle: .alert)
            copyAlert.addAction(UIAlertAction(title: "OK", style: .default))
            self.present(copyAlert, animated: true)
        })
        
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
        present(alert, animated: true)
    }
}

// MARK: - Custom Photo Collection View Cell
class PhotoCollectionViewCell: UICollectionViewCell {
    private let imageView = UIImageView()
    private let nameLabel = UILabel()
    
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
        contentView.layer.cornerRadius = 8
        contentView.layer.shadowColor = UIColor.black.cgColor
        contentView.layer.shadowOffset = CGSize(width: 0, height: 1)
        contentView.layer.shadowOpacity = 0.1
        contentView.layer.shadowRadius = 2
        
        imageView.contentMode = .scaleAspectFit
        imageView.tintColor = UIColor.systemBlue
        imageView.translatesAutoresizingMaskIntoConstraints = false
        
        nameLabel.textAlignment = .center
        nameLabel.font = UIFont.systemFont(ofSize: 12)
        nameLabel.textColor = UIColor.label
        nameLabel.numberOfLines = 2
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        
        contentView.addSubview(imageView)
        contentView.addSubview(nameLabel)
        
        NSLayoutConstraint.activate([
            imageView.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 8),
            imageView.centerXAnchor.constraint(equalTo: contentView.centerXAnchor),
            imageView.widthAnchor.constraint(equalToConstant: 40),
            imageView.heightAnchor.constraint(equalToConstant: 40),
            
            nameLabel.topAnchor.constraint(equalTo: imageView.bottomAnchor, constant: 4),
            nameLabel.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 4),
            nameLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -4),
            nameLabel.bottomAnchor.constraint(lessThanOrEqualTo: contentView.bottomAnchor, constant: -4)
        ])
    }
    
    func configure(with photoName: String) {
        nameLabel.text = photoName
        
        // Try to load actual image thumbnail
        if let albumName = getAlbumName(),
           let imageData = loadPhotoThumbnail(albumName: albumName, photoName: photoName) {
            imageView.image = UIImage(data: imageData)
            imageView.contentMode = .scaleAspectFill
            imageView.clipsToBounds = true
        } else {
            // Fallback to system icons
            if photoName.lowercased().hasSuffix(".jpg") || photoName.lowercased().hasSuffix(".jpeg") {
                imageView.image = UIImage(systemName: "photo.fill")
            } else if photoName.lowercased().hasSuffix(".png") {
                imageView.image = UIImage(systemName: "photo.on.rectangle.angled")
            } else {
                imageView.image = UIImage(systemName: "doc.fill")
            }
            imageView.contentMode = .scaleAspectFit
            imageView.clipsToBounds = false
        }
    }
    
    private func getAlbumName() -> String? {
        // Get album name from parent view controller
        if let albumVC = findViewController() as? AlbumViewController {
            return albumVC.albumName
        }
        return nil
    }
    
    private func loadPhotoThumbnail(albumName: String, photoName: String) -> Data? {
        let documentsPath = PhotoManager.shared.getDocumentsDirectory()
        let photoPath = documentsPath.appendingPathComponent("Albums/\(albumName)/\(photoName)")
        return try? Data(contentsOf: photoPath)
    }
    
    private func findViewController() -> UIViewController? {
        var responder: UIResponder? = self
        while responder != nil {
            responder = responder?.next
            if let viewController = responder as? UIViewController {
                return viewController
            }
        }
        return nil
    }
}