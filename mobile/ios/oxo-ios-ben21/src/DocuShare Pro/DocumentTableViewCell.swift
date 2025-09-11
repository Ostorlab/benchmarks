//
//  DocumentTableViewCell.swift
//  DocuShare Pro
//
//  Created by Ostorlab Ostorlab on 9/11/25.
//

import UIKit

class DocumentTableViewCell: UITableViewCell {
    
    private let documentIconImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.contentMode = .scaleAspectFit
        imageView.tintColor = .systemBlue
        imageView.translatesAutoresizingMaskIntoConstraints = false
        return imageView
    }()
    
    private let titleLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: 16, weight: .medium)
        label.textColor = .label
        label.numberOfLines = 2
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    
    private let subtitleLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: 14, weight: .regular)
        label.textColor = .secondaryLabel
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    
    private let dateLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: 12, weight: .regular)
        label.textColor = .tertiaryLabel
        label.textAlignment = .right
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUI()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupUI()
    }
    
    private func setupUI() {
        contentView.addSubview(documentIconImageView)
        contentView.addSubview(titleLabel)
        contentView.addSubview(subtitleLabel)
        contentView.addSubview(dateLabel)
        
        NSLayoutConstraint.activate([
            documentIconImageView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            documentIconImageView.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
            documentIconImageView.widthAnchor.constraint(equalToConstant: 40),
            documentIconImageView.heightAnchor.constraint(equalToConstant: 40),
            
            titleLabel.leadingAnchor.constraint(equalTo: documentIconImageView.trailingAnchor, constant: 12),
            titleLabel.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 12),
            titleLabel.trailingAnchor.constraint(equalTo: dateLabel.leadingAnchor, constant: -8),
            
            subtitleLabel.leadingAnchor.constraint(equalTo: titleLabel.leadingAnchor),
            subtitleLabel.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 4),
            subtitleLabel.trailingAnchor.constraint(equalTo: titleLabel.trailingAnchor),
            
            dateLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            dateLabel.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
            dateLabel.widthAnchor.constraint(greaterThanOrEqualToConstant: 60)
        ])
    }
    
    func configure(with document: Document, dateFormatter: DateFormatter) {
        titleLabel.text = document.title
        subtitleLabel.text = document.fileType
        dateLabel.text = dateFormatter.string(from: document.dateAdded)
        
        let iconName: String
        if document.fileType.contains("PDF") {
            iconName = "doc.fill"
        } else if document.fileType.contains("Word") {
            iconName = "doc.text.fill"
        } else if document.fileType.contains("HTML") {
            iconName = "globe"
        } else {
            iconName = "doc.fill"
        }
        
        documentIconImageView.image = UIImage(systemName: iconName)
    }
}