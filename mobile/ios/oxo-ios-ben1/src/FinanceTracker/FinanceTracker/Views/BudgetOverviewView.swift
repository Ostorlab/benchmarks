import SwiftUI
import Charts

struct BudgetOverviewView: View {
    @EnvironmentObject var dataManager: DataManager
    @State private var selectedPeriod: TimePeriod = .thisMonth
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    // Period Selector
                    Picker("Period", selection: $selectedPeriod) {
                        ForEach(TimePeriod.allCases, id: \.self) { period in
                            Text(period.rawValue).tag(period)
                        }
                    }
                    .pickerStyle(SegmentedPickerStyle())
                    .padding(.horizontal)
                    
                    // Total Spending Summary
                    TotalSpendingCard(
                        totalSpent: totalSpending,
                        totalBudget: totalBudget,
                        period: selectedPeriod
                    )
                    .padding(.horizontal)
                    
                    // Budget Categories
                    LazyVStack(spacing: 12) {
                        ForEach(dataManager.budgets) { budget in
                            BudgetCategoryCard(budget: budget)
                        }
                    }
                    .padding(.horizontal)
                    
                    // Quick Actions
                    QuickActionsSection()
                        .padding(.horizontal)
                }
                .padding(.vertical)
            }
            .navigationTitle("Budget Overview")
            .background(Color(.systemGroupedBackground))
        }
    }
    
    private var totalSpending: Double {
        dataManager.budgets.reduce(0) { $0 + $1.spent }
    }
    
    private var totalBudget: Double {
        dataManager.budgets.reduce(0) { $0 + $1.monthlyLimit }
    }
}

struct TotalSpendingCard: View {
    let totalSpent: Double
    let totalBudget: Double
    let period: TimePeriod
    
    var progressPercentage: Double {
        guard totalBudget > 0 else { return 0 }
        return min(totalSpent / totalBudget, 1.0)
    }
    
    var body: some View {
        VStack(spacing: 16) {
            HStack {
                VStack(alignment: .leading) {
                    Text("Total Spending")
                        .font(.headline)
                        .foregroundColor(.primary)
                    Text(period.rawValue)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                
                Spacer()
                
                VStack(alignment: .trailing) {
                    Text("$\(totalSpent, specifier: "%.2f")")
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(progressPercentage > 0.9 ? .red : .primary)
                    Text("of $\(totalBudget, specifier: "%.2f")")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
            
            // Progress Bar
            GeometryReader { geometry in
                ZStack(alignment: .leading) {
                    Rectangle()
                        .fill(Color(.systemGray5))
                        .frame(height: 8)
                        .cornerRadius(4)
                    
                    Rectangle()
                        .fill(LinearGradient(
                            colors: progressPercentage > 0.9 ? [.red, .orange] : [.blue, .cyan],
                            startPoint: .leading,
                            endPoint: .trailing
                        ))
                        .frame(width: geometry.size.width * progressPercentage, height: 8)
                        .cornerRadius(4)
                        .animation(.easeInOut, value: progressPercentage)
                }
            }
            .frame(height: 8)
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.1), radius: 2, x: 0, y: 1)
    }
}

struct BudgetCategoryCard: View {
    let budget: Budget
    
    var body: some View {
        HStack(spacing: 12) {
            // Category Icon
            Circle()
                .fill(budget.category.color.opacity(0.2))
                .frame(width: 44, height: 44)
                .overlay(
                    Image(systemName: budget.category.icon)
                        .foregroundColor(budget.category.color)
                        .font(.title3)
                )
            
            // Category Info
            VStack(alignment: .leading, spacing: 4) {
                Text(budget.category.rawValue)
                    .font(.headline)
                    .foregroundColor(.primary)
                
                HStack {
                    Text("$\(budget.spent, specifier: "%.2f")")
                        .font(.subheadline)
                        .fontWeight(.medium)
                        .foregroundColor(budget.isOverBudget ? .red : .primary)
                    
                    Text("/ $\(budget.monthlyLimit, specifier: "%.2f")")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                
                // Progress Bar
                GeometryReader { geometry in
                    ZStack(alignment: .leading) {
                        Rectangle()
                            .fill(Color(.systemGray6))
                            .frame(height: 4)
                            .cornerRadius(2)
                        
                        Rectangle()
                            .fill(budget.isOverBudget ? .red : budget.category.color)
                            .frame(width: geometry.size.width * budget.percentageUsed, height: 4)
                            .cornerRadius(2)
                    }
                }
                .frame(height: 4)
            }
            
            Spacer()
            
            // Remaining Amount
            VStack(alignment: .trailing) {
                Text(budget.isOverBudget ? "Over" : "Left")
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                Text("$\(abs(budget.remaining), specifier: "%.2f")")
                    .font(.headline)
                    .fontWeight(.semibold)
                    .foregroundColor(budget.isOverBudget ? .red : .green)
            }
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.05), radius: 1, x: 0, y: 1)
    }
}

struct QuickActionsSection: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Quick Actions")
                .font(.headline)
                .foregroundColor(.primary)
            
            HStack(spacing: 12) {
                QuickActionButton(
                    title: "Add Expense",
                    icon: "plus.circle.fill",
                    color: .blue
                ) {
                    // This would typically navigate to add expense view
                }
                
                QuickActionButton(
                    title: "View Reports",
                    icon: "chart.bar.fill",
                    color: .green
                ) {
                    // Navigate to reports
                }
                
                QuickActionButton(
                    title: "Set Budget",
                    icon: "target",
                    color: .orange
                ) {
                    // Navigate to budget settings
                }
            }
        }
    }
}

struct QuickActionButton: View {
    let title: String
    let icon: String
    let color: Color
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            VStack(spacing: 8) {
                Image(systemName: icon)
                    .font(.title2)
                    .foregroundColor(color)
                
                Text(title)
                    .font(.caption)
                    .foregroundColor(.primary)
                    .multilineTextAlignment(.center)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 12)
            .background(Color(.systemBackground))
            .cornerRadius(8)
            .shadow(color: .black.opacity(0.05), radius: 1, x: 0, y: 1)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

enum TimePeriod: String, CaseIterable {
    case thisWeek = "This Week"
    case thisMonth = "This Month"
    case lastMonth = "Last Month"
    case thisYear = "This Year"
}

#Preview {
    BudgetOverviewView()
        .environmentObject(DataManager())
}
