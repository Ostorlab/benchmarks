//
//  ViewController.swift
//  ShopZen
//
//  Created by ShopZen Team on 9/9/25.
//

import UIKit

class ViewController: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    private var cartButton: UIBarButtonItem!
    
    private var products: [Product] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        setupTableView()
        loadProducts()
        observeCartChanges()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        updateCartButton()
    }
    
    private func setupUI() {
        title = "ShopZen"
        navigationController?.navigationBar.prefersLargeTitles = true
        view.backgroundColor = UIColor.systemBackground
        
        let cartImage = UIImage(systemName: "cart")
        cartButton = UIBarButtonItem(image: cartImage, style: .plain, target: self, action: #selector(cartButtonTapped))
        navigationItem.rightBarButtonItem = cartButton
    }
    
    private func setupTableView() {
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(ProductTableViewCell.self, forCellReuseIdentifier: "ProductCell")
        tableView.rowHeight = 120
        tableView.backgroundColor = UIColor.systemBackground
    }
    
    private func loadProducts() {
        products = DataManager.shared.loadSampleProducts()
        tableView.reloadData()
    }
    
    private func observeCartChanges() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(cartDidUpdate),
            name: NSNotification.Name("CartUpdated"),
            object: nil
        )
    }
    
    @objc private func cartDidUpdate() {
        updateCartButton()
    }
    
    private func updateCartButton() {
        let itemCount = ShoppingCartManager.shared.itemCount
        if itemCount > 0 {
            cartButton.title = "Cart (\(itemCount))"
        } else {
            cartButton.title = nil
            cartButton.image = UIImage(systemName: "cart")
        }
    }
    
    @objc private func cartButtonTapped() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        if let cartVC = storyboard.instantiateViewController(withIdentifier: "ShoppingCartViewController") as? ShoppingCartViewController {
            navigationController?.pushViewController(cartVC, animated: true)
        }
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
}

extension ViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return products.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ProductCell", for: indexPath) as! ProductTableViewCell
        
        let product = products[indexPath.row]
        cell.configure(with: product)
        
        return cell
    }
}