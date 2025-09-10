//
//  PortfolioDashboardViewController.swift
//  WealthPilot
//
//  Created by Ostorlab Ostorlab on 9/10/25.
//

import UIKit

class PortfolioDashboardViewController: UIViewController {
    
    @IBOutlet weak var portfolioValueLabel: UILabel!
    @IBOutlet weak var gainLossLabel: UILabel!
    @IBOutlet weak var gainLossPercentageLabel: UILabel!
    @IBOutlet weak var accountNameLabel: UILabel!
    @IBOutlet weak var investmentsTableView: UITableView!
    @IBOutlet weak var viewDetailsButton: UIButton!
    
    private let portfolio = PortfolioManager.shared.samplePortfolio
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        setupTableView()
        updatePortfolioDisplay()
    }
    
    private func setupUI() {
        title = "Portfolio"
        view.backgroundColor = UIColor.systemBackground
        
        navigationController?.navigationBar.prefersLargeTitles = true
        navigationItem.largeTitleDisplayMode = .always
        
        let logoutButton = UIBarButtonItem(
            title: "Logout",
            style: .plain,
            target: self,
            action: #selector(logoutTapped)
        )
        navigationItem.rightBarButtonItem = logoutButton
        
        portfolioValueLabel.font = UIFont.systemFont(ofSize: 36, weight: .bold)
        portfolioValueLabel.textColor = UIColor.label
        
        gainLossLabel.font = UIFont.systemFont(ofSize: 18, weight: .semibold)
        gainLossPercentageLabel.font = UIFont.systemFont(ofSize: 16, weight: .medium)
        
        accountNameLabel.font = UIFont.systemFont(ofSize: 14, weight: .regular)
        accountNameLabel.textColor = UIColor.secondaryLabel
        
        viewDetailsButton.setTitle("View All Holdings", for: .normal)
        viewDetailsButton.backgroundColor = UIColor.systemBlue
        viewDetailsButton.setTitleColor(.white, for: .normal)
        viewDetailsButton.layer.cornerRadius = 8
        viewDetailsButton.titleLabel?.font = UIFont.systemFont(ofSize: 16, weight: .semibold)
    }
    
    private func setupTableView() {
        investmentsTableView.delegate = self
        investmentsTableView.dataSource = self
        investmentsTableView.register(UITableViewCell.self, forCellReuseIdentifier: "InvestmentCell")
        investmentsTableView.backgroundColor = UIColor.systemBackground
        investmentsTableView.separatorStyle = .singleLine
    }
    
    private func updatePortfolioDisplay() {
        portfolioValueLabel.text = formatCurrency(portfolio.totalValue)
        accountNameLabel.text = portfolio.accountName
        
        let gainLoss = portfolio.totalGainLoss
        let percentage = portfolio.totalGainLossPercentage
        
        gainLossLabel.text = formatCurrency(gainLoss)
        gainLossPercentageLabel.text = String(format: "(%.2f%%)", percentage)
        
        if gainLoss >= 0 {
            gainLossLabel.textColor = UIColor.systemGreen
            gainLossPercentageLabel.textColor = UIColor.systemGreen
        } else {
            gainLossLabel.textColor = UIColor.systemRed
            gainLossPercentageLabel.textColor = UIColor.systemRed
        }
    }
    
    private func formatCurrency(_ amount: Double) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencyCode = "USD"
        return formatter.string(from: NSNumber(value: amount)) ?? "$0.00"
    }
    
    @objc private func logoutTapped() {
        navigationController?.popToRootViewController(animated: true)
    }
    
    @IBAction func viewDetailsButtonTapped(_ sender: UIButton) {
        performSegue(withIdentifier: "showInvestmentDetails", sender: nil)
    }
}

extension PortfolioDashboardViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return min(portfolio.investments.count, 4)
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "InvestmentCell", for: indexPath)
        let investment = portfolio.investments[indexPath.row]
        
        cell.textLabel?.text = "\(investment.symbol) - \(investment.name)"
        cell.textLabel?.font = UIFont.systemFont(ofSize: 16, weight: .medium)
        
        let value = formatCurrency(investment.currentValue)
        let gainLoss = investment.gainLoss
        let percentage = String(format: "%.2f%%", investment.gainLossPercentage)
        
        if gainLoss >= 0 {
            cell.detailTextLabel?.text = "\(value) (+\(percentage))"
            cell.detailTextLabel?.textColor = UIColor.systemGreen
        } else {
            cell.detailTextLabel?.text = "\(value) (\(percentage))"
            cell.detailTextLabel?.textColor = UIColor.systemRed
        }
        
        cell.accessoryType = .disclosureIndicator
        cell.backgroundColor = UIColor.systemBackground
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return "Top Holdings"
    }
}