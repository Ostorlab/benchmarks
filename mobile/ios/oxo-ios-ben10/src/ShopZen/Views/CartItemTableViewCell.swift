//
//  CartItemTableViewCell.swift
//  ShopZen
//
//  Created by ShopZen Team on 9/9/25.
//

import UIKit

class CartItemTableViewCell: UITableViewCell {
    
    private let productImageView = UIImageView()
    private let nameLabel = UILabel()
    private let priceLabel = UILabel()
    private let quantityLabel = UILabel()
    private let decreaseButton = UIButton(type: .system)
    private let increaseButton = UIButton(type: .system)
    private let totalPriceLabel = UILabel()
    
    weak var delegate: CartItemCellDelegate?
    private var cartItem: CartItem?
    
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
        backgroundColor = UIColor.systemBackground
        
        contentView.layer.cornerRadius = 12
        contentView.layer.shadowColor = UIColor.black.cgColor
        contentView.layer.shadowOpacity = 0.05
        contentView.layer.shadowOffset = CGSize(width: 0, height: 1)
        contentView.layer.shadowRadius = 2
        
        setupProductImageView()
        setupLabels()
        setupQuantityControls()
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
        nameLabel.font = UIFont.systemFont(ofSize: 16, weight: .medium)
        nameLabel.numberOfLines = 2
        nameLabel.textColor = UIColor.label
        
        priceLabel.font = UIFont.systemFont(ofSize: 14)
        priceLabel.textColor = UIColor.systemGray
        
        totalPriceLabel.font = UIFont.systemFont(ofSize: 18, weight: .bold)
        totalPriceLabel.textColor = UIColor.systemBlue
        
        quantityLabel.font = UIFont.systemFont(ofSize: 16, weight: .medium)
        quantityLabel.textAlignment = .center
        quantityLabel.textColor = UIColor.label
        
        [nameLabel, priceLabel, totalPriceLabel, quantityLabel].forEach {
            contentView.addSubview($0)
            $0.translatesAutoresizingMaskIntoConstraints = false
        }
    }
    
    private func setupQuantityControls() {
        decreaseButton.setTitle("-", for: .normal)
        decreaseButton.backgroundColor = UIColor.systemGray5
        decreaseButton.setTitleColor(UIColor.label, for: .normal)
        decreaseButton.layer.cornerRadius = 16
        decreaseButton.titleLabel?.font = UIFont.systemFont(ofSize: 18, weight: .medium)
        decreaseButton.addTarget(self, action: #selector(decreaseQuantity), for: .touchUpInside)
        
        increaseButton.setTitle("+", for: .normal)
        increaseButton.backgroundColor = UIColor.systemBlue
        increaseButton.setTitleColor(.white, for: .normal)
        increaseButton.layer.cornerRadius = 16
        increaseButton.titleLabel?.font = UIFont.systemFont(ofSize: 18, weight: .medium)
        increaseButton.addTarget(self, action: #selector(increaseQuantity), for: .touchUpInside)
        
        [decreaseButton, increaseButton].forEach {
            contentView.addSubview($0)
            $0.translatesAutoresizingMaskIntoConstraints = false
        }
    }
    
    private func setupConstraints() {
        NSLayoutConstraint.activate([
            productImageView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            productImageView.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 12),
            productImageView.widthAnchor.constraint(equalToConstant: 80),
            productImageView.heightAnchor.constraint(equalToConstant: 80),
            productImageView.bottomAnchor.constraint(lessThanOrEqualTo: contentView.bottomAnchor, constant: -12),
            
            nameLabel.leadingAnchor.constraint(equalTo: productImageView.trailingAnchor, constant: 12),
            nameLabel.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 12),
            nameLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            
            priceLabel.leadingAnchor.constraint(equalTo: productImageView.trailingAnchor, constant: 12),
            priceLabel.topAnchor.constraint(equalTo: nameLabel.bottomAnchor, constant: 4),
            
            totalPriceLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            totalPriceLabel.centerYAnchor.constraint(equalTo: productImageView.centerYAnchor, constant: -10),
            
            decreaseButton.leadingAnchor.constraint(equalTo: productImageView.trailingAnchor, constant: 12),
            decreaseButton.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -12),
            decreaseButton.widthAnchor.constraint(equalToConstant: 32),
            decreaseButton.heightAnchor.constraint(equalToConstant: 32),
            
            quantityLabel.leadingAnchor.constraint(equalTo: decreaseButton.trailingAnchor, constant: 12),
            quantityLabel.centerYAnchor.constraint(equalTo: decreaseButton.centerYAnchor),
            quantityLabel.widthAnchor.constraint(equalToConstant: 30),
            
            increaseButton.leadingAnchor.constraint(equalTo: quantityLabel.trailingAnchor, constant: 12),
            increaseButton.centerYAnchor.constraint(equalTo: decreaseButton.centerYAnchor),
            increaseButton.widthAnchor.constraint(equalToConstant: 32),
            increaseButton.heightAnchor.constraint(equalToConstant: 32),
            
            contentView.heightAnchor.constraint(greaterThanOrEqualToConstant: 104)
        ])
    }
    
    func configure(with cartItem: CartItem) {
        self.cartItem = cartItem
        
        nameLabel.text = cartItem.product.name
        priceLabel.text = cartItem.product.formattedPrice
        quantityLabel.text = "\(cartItem.quantity)"
        totalPriceLabel.text = String(format: "$%.2f", cartItem.totalPrice)
        
        if let image = UIImage(named: cartItem.product.imageURL) {
            productImageView.image = image
        } else {
            productImageView.image = createPlaceholderImage()
        }
        
        decreaseButton.isEnabled = cartItem.quantity > 1
        decreaseButton.alpha = cartItem.quantity > 1 ? 1.0 : 0.5
    }
    
    @objc private func decreaseQuantity() {
        guard let cartItem = cartItem, cartItem.quantity > 1 else { return }
        
        let newQuantity = cartItem.quantity - 1
        quantityLabel.text = "\(newQuantity)"
        totalPriceLabel.text = String(format: "$%.2f", cartItem.product.price * Double(newQuantity))
        
        decreaseButton.isEnabled = newQuantity > 1
        decreaseButton.alpha = newQuantity > 1 ? 1.0 : 0.5
        
        delegate?.cartItemCell(self, didUpdateQuantity: newQuantity, for: cartItem.product.id)
        
        let impactFeedback = UIImpactFeedbackGenerator(style: .light)
        impactFeedback.impactOccurred()
    }
    
    @objc private func increaseQuantity() {
        guard let cartItem = cartItem else { return }
        
        let newQuantity = cartItem.quantity + 1
        quantityLabel.text = "\(newQuantity)"
        totalPriceLabel.text = String(format: "$%.2f", cartItem.product.price * Double(newQuantity))
        
        decreaseButton.isEnabled = true
        decreaseButton.alpha = 1.0
        
        delegate?.cartItemCell(self, didUpdateQuantity: newQuantity, for: cartItem.product.id)
        
        let impactFeedback = UIImpactFeedbackGenerator(style: .light)
        impactFeedback.impactOccurred()
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
}