//
//  ShoppingCartViewController.swift
//  ShopZen
//
//  Created by ShopZen Team on 9/9/25.
//

import UIKit

class ShoppingCartViewController: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var promoCodeTextField: UITextField!
    @IBOutlet weak var applyPromoButton: UIButton!
    @IBOutlet weak var subtotalLabel: UILabel!
    @IBOutlet weak var discountLabel: UILabel!
    @IBOutlet weak var taxLabel: UILabel!
    @IBOutlet weak var totalLabel: UILabel!
    @IBOutlet weak var checkoutButton: UIButton!
    @IBOutlet weak var emptyCartView: UIView!
    @IBOutlet weak var orderSummaryView: UIView!
    
    private let cartManager = ShoppingCartManager.shared
    private var isValidatingPromoCode = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        setupTableView()
        setupPromoCodeSection()
        updateUI()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        updateUI()
    }
    
    private func setupUI() {
        title = "Shopping Cart"
        navigationController?.navigationBar.prefersLargeTitles = false
        
        view.backgroundColor = UIColor.systemGroupedBackground
        
        setupOrderSummaryView()
        setupCheckoutButton()
        setupEmptyCartView()
    }
    
    private func setupTableView() {
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(CartItemTableViewCell.self, forCellReuseIdentifier: "CartItemCell")
        tableView.separatorStyle = .none
        tableView.backgroundColor = UIColor.clear
    }
    
    private func setupPromoCodeSection() {
        promoCodeTextField.placeholder = "Enter promo code"
        promoCodeTextField.borderStyle = .roundedRect
        promoCodeTextField.backgroundColor = UIColor.systemBackground
        promoCodeTextField.delegate = self
        promoCodeTextField.autocapitalizationType = .allCharacters
        promoCodeTextField.autocorrectionType = .no
        
        applyPromoButton.setTitle("Apply", for: .normal)
        applyPromoButton.backgroundColor = UIColor.systemBlue
        applyPromoButton.setTitleColor(.white, for: .normal)
        applyPromoButton.layer.cornerRadius = 8
        applyPromoButton.titleLabel?.font = UIFont.systemFont(ofSize: 16, weight: .medium)
        applyPromoButton.addTarget(self, action: #selector(applyPromoButtonTapped), for: .touchUpInside)
        
        if let appliedCode = cartManager.appliedPromoCode {
            promoCodeTextField.text = appliedCode
            applyPromoButton.setTitle("Remove", for: .normal)
            applyPromoButton.backgroundColor = UIColor.systemRed
        }
    }
    
    private func setupOrderSummaryView() {
        orderSummaryView.backgroundColor = UIColor.systemBackground
        orderSummaryView.layer.cornerRadius = 12
        orderSummaryView.layer.shadowColor = UIColor.black.cgColor
        orderSummaryView.layer.shadowOpacity = 0.1
        orderSummaryView.layer.shadowOffset = CGSize(width: 0, height: 2)
        orderSummaryView.layer.shadowRadius = 4
    }
    
    private func setupCheckoutButton() {
        checkoutButton.backgroundColor = UIColor.systemGreen
        checkoutButton.setTitleColor(.white, for: .normal)
        checkoutButton.layer.cornerRadius = 12
        checkoutButton.titleLabel?.font = UIFont.systemFont(ofSize: 18, weight: .semibold)
        checkoutButton.addTarget(self, action: #selector(checkoutButtonTapped), for: .touchUpInside)
    }
    
    private func setupEmptyCartView() {
        emptyCartView.backgroundColor = UIColor.clear
        
        let imageView = UIImageView(image: UIImage(systemName: "cart"))
        imageView.tintColor = UIColor.systemGray3
        imageView.contentMode = .scaleAspectFit
        
        let titleLabel = UILabel()
        titleLabel.text = "Your cart is empty"
        titleLabel.font = UIFont.systemFont(ofSize: 24, weight: .bold)
        titleLabel.textColor = UIColor.systemGray2
        titleLabel.textAlignment = .center
        
        let subtitleLabel = UILabel()
        subtitleLabel.text = "Start shopping to add items to your cart"
        subtitleLabel.font = UIFont.systemFont(ofSize: 16)
        subtitleLabel.textColor = UIColor.systemGray
        subtitleLabel.textAlignment = .center
        subtitleLabel.numberOfLines = 0
        
        let stackView = UIStackView(arrangedSubviews: [imageView, titleLabel, subtitleLabel])
        stackView.axis = .vertical
        stackView.spacing = 16
        stackView.alignment = .center
        
        emptyCartView.addSubview(stackView)
        stackView.translatesAutoresizingMaskIntoConstraints = false
        
        NSLayoutConstraint.activate([
            imageView.heightAnchor.constraint(equalToConstant: 80),
            imageView.widthAnchor.constraint(equalToConstant: 80),
            
            stackView.centerXAnchor.constraint(equalTo: emptyCartView.centerXAnchor),
            stackView.centerYAnchor.constraint(equalTo: emptyCartView.centerYAnchor),
            stackView.leadingAnchor.constraint(equalTo: emptyCartView.leadingAnchor, constant: 40),
            stackView.trailingAnchor.constraint(equalTo: emptyCartView.trailingAnchor, constant: -40)
        ])
    }
    
    private func updateUI() {
        let hasItems = !cartManager.items.isEmpty
        
        tableView.isHidden = !hasItems
        orderSummaryView.isHidden = !hasItems
        emptyCartView.isHidden = hasItems
        
        if hasItems {
            updateOrderSummary()
            tableView.reloadData()
        }
    }
    
    private func updateOrderSummary() {
        subtotalLabel.text = String(format: "$%.2f", cartManager.subtotal)
        discountLabel.text = cartManager.discount > 0 ? "-$\(String(format: "%.2f", cartManager.discount))" : "$0.00"
        taxLabel.text = String(format: "$%.2f", cartManager.tax)
        totalLabel.text = String(format: "$%.2f", cartManager.total)
        
        discountLabel.textColor = cartManager.discount > 0 ? UIColor.systemGreen : UIColor.label
        
        let buttonTitle = String(format: "Checkout - $%.2f", cartManager.total)
        checkoutButton.setTitle(buttonTitle, for: .normal)
    }
    
    @objc private func applyPromoButtonTapped() {
        guard !isValidatingPromoCode else { return }
        
        if cartManager.appliedPromoCode != nil {
            removePromoCode()
        } else {
            applyPromoCode()
        }
    }
    
    private func applyPromoCode() {
        guard let promoCode = promoCodeTextField.text?.trimmingCharacters(in: .whitespacesAndNewlines),
              !promoCode.isEmpty else {
            showAlert(title: "Invalid Input", message: "Please enter a promo code")
            return
        }
        
        guard cartManager.subtotal > 0 else {
            showAlert(title: "Empty Cart", message: "Add items to your cart before applying a promo code")
            return
        }
        
        isValidatingPromoCode = true
        applyPromoButton.isEnabled = false
        applyPromoButton.setTitle("Validating...", for: .normal)
        
        cartManager.applyPromoCode(promoCode) { [weak self] success, message in
            DispatchQueue.main.async {
                self?.isValidatingPromoCode = false
                self?.applyPromoButton.isEnabled = true
                
                if success {
                    self?.applyPromoButton.setTitle("Remove", for: .normal)
                    self?.applyPromoButton.backgroundColor = UIColor.systemRed
                    self?.updateOrderSummary()
                    self?.showSuccessAlert(message: message)
                } else {
                    self?.applyPromoButton.setTitle("Apply", for: .normal)
                    self?.showAlert(title: "Invalid Promo Code", message: message)
                }
            }
        }
    }
    
    private func removePromoCode() {
        cartManager.removePromoCode()
        promoCodeTextField.text = ""
        applyPromoButton.setTitle("Apply", for: .normal)
        applyPromoButton.backgroundColor = UIColor.systemBlue
        updateOrderSummary()
        
        showSuccessAlert(message: "Promo code removed")
    }
    
    @objc private func checkoutButtonTapped() {
        guard !cartManager.items.isEmpty else { return }
        
        let alertController = UIAlertController(
            title: "Order Confirmation",
            message: "Complete your purchase of \(cartManager.itemCount) items for \(String(format: "$%.2f", cartManager.total))?",
            preferredStyle: .alert
        )
        
        let confirmAction = UIAlertAction(title: "Complete Purchase", style: .default) { [weak self] _ in
            self?.completeCheckout()
        }
        
        let cancelAction = UIAlertAction(title: "Cancel", style: .cancel)
        
        alertController.addAction(confirmAction)
        alertController.addAction(cancelAction)
        
        present(alertController, animated: true)
    }
    
    private func completeCheckout() {
        let orderTotal = cartManager.total
        let itemCount = cartManager.itemCount
        let promoCode = cartManager.appliedPromoCode
        
        cartManager.clearCart()
        
        let successMessage = promoCode != nil ?
            "Order completed! \(itemCount) items purchased for \(String(format: "$%.2f", orderTotal)) with promo code \(promoCode!)." :
            "Order completed! \(itemCount) items purchased for \(String(format: "$%.2f", orderTotal))."
        
        let alertController = UIAlertController(
            title: "Order Complete!",
            message: successMessage,
            preferredStyle: .alert
        )
        
        let okAction = UIAlertAction(title: "OK", style: .default) { [weak self] _ in
            self?.navigationController?.popViewController(animated: true)
        }
        
        alertController.addAction(okAction)
        present(alertController, animated: true)
        
        updateUI()
    }
    
    private func showAlert(title: String, message: String) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alertController.addAction(UIAlertAction(title: "OK", style: .default))
        present(alertController, animated: true)
    }
    
    private func showSuccessAlert(message: String) {
        let alertController = UIAlertController(title: "Success", message: message, preferredStyle: .alert)
        alertController.addAction(UIAlertAction(title: "OK", style: .default))
        present(alertController, animated: true)
    }
}

extension ShoppingCartViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return cartManager.items.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "CartItemCell", for: indexPath) as! CartItemTableViewCell
        
        let cartItem = cartManager.items[indexPath.row]
        cell.configure(with: cartItem)
        cell.delegate = self
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            cartManager.removeItem(at: indexPath.row)
            tableView.deleteRows(at: [indexPath], with: .fade)
            updateUI()
            NotificationCenter.default.post(name: NSNotification.Name("CartUpdated"), object: nil)
        }
    }
}

extension ShoppingCartViewController: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        if textField == promoCodeTextField {
            let currentText = textField.text ?? ""
            let newText = (currentText as NSString).replacingCharacters(in: range, with: string.uppercased())
            textField.text = newText
            return false
        }
        return true
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        if textField == promoCodeTextField {
            applyPromoButtonTapped()
        }
        return true
    }
}

protocol CartItemCellDelegate: AnyObject {
    func cartItemCell(_ cell: CartItemTableViewCell, didUpdateQuantity quantity: Int, for productId: String)
}

extension ShoppingCartViewController: CartItemCellDelegate {
    func cartItemCell(_ cell: CartItemTableViewCell, didUpdateQuantity quantity: Int, for productId: String) {
        cartManager.updateQuantity(for: productId, quantity: quantity)
        updateOrderSummary()
        NotificationCenter.default.post(name: NSNotification.Name("CartUpdated"), object: nil)
    }
}