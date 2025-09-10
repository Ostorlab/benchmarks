//
//  InvestmentDetailsViewController.swift
//  WealthPilot
//
//  Created by Ostorlab Ostorlab on 9/10/25.
//

import UIKit

class InvestmentDetailsViewController: UIViewController {
    
    @IBOutlet weak var accountNumberLabel: UILabel!
    @IBOutlet weak var totalValueLabel: UILabel!
    @IBOutlet weak var allInvestmentsTableView: UITableView!
    
    private let portfolio = PortfolioManager.shared.samplePortfolio
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        setupTableView()
        updateDisplay()
    }
    
    private func setupUI() {
        title = "Investment Details"
        view.backgroundColor = UIColor.systemBackground
        
        accountNumberLabel.font = UIFont.systemFont(ofSize: 14, weight: .medium)
        accountNumberLabel.textColor = UIColor.secondaryLabel
        
        totalValueLabel.font = UIFont.systemFont(ofSize: 28, weight: .bold)
        totalValueLabel.textColor = UIColor.label
    }
    
    private func setupTableView() {
        allInvestmentsTableView.delegate = self
        allInvestmentsTableView.dataSource = self
        allInvestmentsTableView.register(InvestmentDetailCell.self, forCellReuseIdentifier: "InvestmentDetailCell")
        allInvestmentsTableView.backgroundColor = UIColor.systemBackground
        allInvestmentsTableView.separatorStyle = .singleLine
        allInvestmentsTableView.rowHeight = 80
    }
    
    private func updateDisplay() {
        accountNumberLabel.text = "Account: \(portfolio.accountNumber)"
        totalValueLabel.text = formatCurrency(portfolio.totalValue)
    }
    
    private func formatCurrency(_ amount: Double) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencyCode = "USD"
        return formatter.string(from: NSNumber(value: amount)) ?? "$0.00"
    }
}

extension InvestmentDetailsViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return portfolio.investments.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "InvestmentDetailCell", for: indexPath) as! InvestmentDetailCell
        let investment = portfolio.investments[indexPath.row]
        cell.configure(with: investment)
        return cell
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return "All Holdings"
    }
}

class InvestmentDetailCell: UITableViewCell {
    
    private let symbolLabel = UILabel()
    private let nameLabel = UILabel()
    private let typeLabel = UILabel()
    private let sharesLabel = UILabel()
    private let valueLabel = UILabel()
    private let gainLossLabel = UILabel()
    
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUI()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setupUI() {
        backgroundColor = UIColor.systemBackground
        
        symbolLabel.font = UIFont.systemFont(ofSize: 16, weight: .bold)
        symbolLabel.textColor = UIColor.label
        
        nameLabel.font = UIFont.systemFont(ofSize: 14, weight: .medium)
        nameLabel.textColor = UIColor.secondaryLabel
        nameLabel.numberOfLines = 2
        
        typeLabel.font = UIFont.systemFont(ofSize: 12, weight: .medium)
        typeLabel.textColor = UIColor.systemBlue
        typeLabel.layer.backgroundColor = UIColor.systemBlue.withAlphaComponent(0.1).cgColor
        typeLabel.layer.cornerRadius = 4
        typeLabel.textAlignment = .center
        
        sharesLabel.font = UIFont.systemFont(ofSize: 12, weight: .regular)
        sharesLabel.textColor = UIColor.secondaryLabel
        
        valueLabel.font = UIFont.systemFont(ofSize: 16, weight: .semibold)
        valueLabel.textColor = UIColor.label
        valueLabel.textAlignment = .right
        
        gainLossLabel.font = UIFont.systemFont(ofSize: 14, weight: .medium)
        gainLossLabel.textAlignment = .right
        
        [symbolLabel, nameLabel, typeLabel, sharesLabel, valueLabel, gainLossLabel].forEach {
            $0.translatesAutoresizingMaskIntoConstraints = false
            contentView.addSubview($0)
        }
        
        NSLayoutConstraint.activate([
            symbolLabel.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 8),
            symbolLabel.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            
            nameLabel.topAnchor.constraint(equalTo: symbolLabel.bottomAnchor, constant: 2),
            nameLabel.leadingAnchor.constraint(equalTo: symbolLabel.leadingAnchor),
            nameLabel.trailingAnchor.constraint(equalTo: contentView.centerXAnchor, constant: 20),
            
            typeLabel.topAnchor.constraint(equalTo: nameLabel.bottomAnchor, constant: 4),
            typeLabel.leadingAnchor.constraint(equalTo: symbolLabel.leadingAnchor),
            typeLabel.widthAnchor.constraint(equalToConstant: 50),
            typeLabel.heightAnchor.constraint(equalToConstant: 20),
            
            sharesLabel.centerYAnchor.constraint(equalTo: typeLabel.centerYAnchor),
            sharesLabel.leadingAnchor.constraint(equalTo: typeLabel.trailingAnchor, constant: 8),
            
            valueLabel.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 12),
            valueLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            
            gainLossLabel.topAnchor.constraint(equalTo: valueLabel.bottomAnchor, constant: 4),
            gainLossLabel.trailingAnchor.constraint(equalTo: valueLabel.trailingAnchor)
        ])
    }
    
    func configure(with investment: Investment) {
        symbolLabel.text = investment.symbol
        nameLabel.text = investment.name
        typeLabel.text = investment.type.rawValue
        
        let sharesText = investment.shares < 1 ? String(format: "%.3f shares", investment.shares) : String(format: "%.0f shares", investment.shares)
        sharesLabel.text = sharesText
        
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencyCode = "USD"
        valueLabel.text = formatter.string(from: NSNumber(value: investment.currentValue))
        
        let gainLoss = investment.gainLoss
        let percentage = investment.gainLossPercentage
        
        if gainLoss >= 0 {
            gainLossLabel.text = String(format: "+%.2f%%", percentage)
            gainLossLabel.textColor = UIColor.systemGreen
        } else {
            gainLossLabel.text = String(format: "%.2f%%", percentage)
            gainLossLabel.textColor = UIColor.systemRed
        }
    }
}