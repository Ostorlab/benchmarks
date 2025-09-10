//
//  ProductTableViewCell.swift
//  ShopZen
//
//  Created by ShopZen Team on 9/9/25.
//

import UIKit

class ProductTableViewCell: UITableViewCell {
    
    private let productImageView = UIImageView()
    private let nameLabel = UILabel()
    private let priceLabel = UILabel()
    private let addToCartButton = UIButton(type: .system)
    
    private var product: Product?
    
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupViews()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupViews()
    }
    
    private func setupViews() {
        selectionStyle = .none
        
        setupProductImageView()
        setupLabels()
        setupAddToCartButton()
        setupConstraints()
    }
    
    private func setupProductImageView() {
        productImageView.contentMode = .scaleAspectFill
        productImageView.clipsToBounds = true
        productImageView.layer.cornerRadius = 8
        productImageView.backgroundColor = UIColor.systemGray6
        contentView.addSubview(productImageView)
        productImageView.translatesAutoresizingMaskIntoConstraints = false
    }
    
    private func setupLabels() {
        nameLabel.font = UIFont.systemFont(ofSize: 18, weight: .medium)
        nameLabel.numberOfLines = 2
        nameLabel.textColor = UIColor.label
        
        priceLabel.font = UIFont.systemFont(ofSize: 20, weight: .bold)
        priceLabel.textColor = UIColor.systemBlue
        
        [nameLabel, priceLabel].forEach {
            contentView.addSubview($0)
            $0.translatesAutoresizingMaskIntoConstraints = false
        }
    }
    
    private func setupAddToCartButton() {
        addToCartButton.setTitle("Add to Cart", for: .normal)
        addToCartButton.titleLabel?.font = UIFont.systemFont(ofSize: 16, weight: .medium)
        addToCartButton.backgroundColor = UIColor.systemBlue
        addToCartButton.setTitleColor(.white, for: .normal)
        addToCartButton.layer.cornerRadius = 8
        addToCartButton.addTarget(self, action: #selector(addToCartTapped), for: .touchUpInside)
        contentView.addSubview(addToCartButton)
        addToCartButton.translatesAutoresizingMaskIntoConstraints = false
    }
    
    private func setupConstraints() {
        NSLayoutConstraint.activate([
            productImageView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            productImageView.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
            productImageView.widthAnchor.constraint(equalToConstant: 80),
            productImageView.heightAnchor.constraint(equalToConstant: 80),
            
            nameLabel.leadingAnchor.constraint(equalTo: productImageView.trailingAnchor, constant: 12),
            nameLabel.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 16),
            nameLabel.trailingAnchor.constraint(equalTo: addToCartButton.leadingAnchor, constant: -12),
            
            priceLabel.leadingAnchor.constraint(equalTo: productImageView.trailingAnchor, constant: 12),
            priceLabel.topAnchor.constraint(equalTo: nameLabel.bottomAnchor, constant: 8),
            
            addToCartButton.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            addToCartButton.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
            addToCartButton.widthAnchor.constraint(equalToConstant: 100),
            addToCartButton.heightAnchor.constraint(equalToConstant: 36)
        ])
    }
    
    func configure(with product: Product) {
        self.product = product
        nameLabel.text = product.name
        priceLabel.text = product.formattedPrice
        
        if let image = UIImage(named: product.imageURL) {
            productImageView.image = image
        } else {
            productImageView.image = createPlaceholderImage()
        }
    }
    
    private func createPlaceholderImage() -> UIImage? {
        let size = CGSize(width: 80, height: 80)
        UIGraphicsBeginImageContext(size)
        let context = UIGraphicsGetCurrentContext()
        
        context?.setFillColor(UIColor.systemGray5.cgColor)
        context?.fill(CGRect(origin: .zero, size: size))
        
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return image
    }
    
    @objc private func addToCartTapped() {
        guard let product = product else { return }
        
        ShoppingCartManager.shared.addItem(product: product)
        
        addToCartButton.backgroundColor = UIColor.systemGreen
        addToCartButton.setTitle("Added!", for: .normal)
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
            self.addToCartButton.backgroundColor = UIColor.systemBlue
            self.addToCartButton.setTitle("Add to Cart", for: .normal)
        }
        
        NotificationCenter.default.post(name: NSNotification.Name("CartUpdated"), object: nil)
        
        let impactFeedback = UIImpactFeedbackGenerator(style: .medium)
        impactFeedback.impactOccurred()
    }
}