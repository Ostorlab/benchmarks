import Foundation
import Combine

class ActivityStore: ObservableObject {
    @Published var activities: [Activity] = []
    
    init() {
        loadSampleData()
    }
    
    func addActivity(_ activity: Activity) {
        activities.insert(activity, at: 0) // Add to beginning for chronological order
    }
    
    func removeActivities(at offsets: IndexSet) {
        activities.remove(atOffsets: offsets)
    }
    
    var todayActivities: [Activity] {
        let today = Calendar.current.startOfDay(for: Date())
        return activities.filter { Calendar.current.isDate($0.timestamp, inSameDayAs: today) }
    }
    
    var totalDurationToday: Double {
        todayActivities.reduce(0) { $0 + $1.duration }
    }
    
    private func loadSampleData() {
        // Add a single example activity to help users understand the app
        let exampleActivity = Activity(
            title: "Welcome to EventTracker!",
            category: .learning,
            duration: 0.1,
            notes: "This is an example activity. Tap the + button to add your own activities and start tracking your day!",
            location: nil
        )
        
        activities = [exampleActivity]
    }
}
