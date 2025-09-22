//
//  ContentView.swift
//  EventTracker
//
//  Created by aitougrram on 11/9/2025.
//

import SwiftUI

struct ContentView: View {
    @StateObject private var activityStore = ActivityStore()
    @StateObject private var analyticsService = AnalyticsService()
    @State private var showingNewActivity = false
    @State private var showingSettings = false
    
    var body: some View {
        NavigationView {
            VStack {
                // Header with sync status
                HStack {
                    VStack(alignment: .leading) {
                        Text("EventTracker")
                            .font(.largeTitle)
                            .fontWeight(.bold)
                        
                        HStack {
                            Circle()
                                .fill(analyticsService.isCloudSyncEnabled ? .green : .gray)
                                .frame(width: 8, height: 8)
                            Text(analyticsService.isCloudSyncEnabled ? "Cloud Sync Active" : "Cloud Sync Disabled")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                    Spacer()
                    
                    HStack {
                        Button(action: {
                            showingSettings = true
                        }) {
                            Image(systemName: "gearshape.fill")
                                .font(.title2)
                                .foregroundColor(.secondary)
                        }
                        
                        Button(action: {
                            showingNewActivity = true
                        }) {
                            Image(systemName: "plus.circle.fill")
                                .font(.title2)
                        }
                    }
                }
                .padding()
                
                // Activity List
                if activityStore.activities.isEmpty {
                    VStack {
                        Image(systemName: "chart.line.uptrend.xyaxis")
                            .font(.system(size: 60))
                            .foregroundColor(.gray)
                        Text("No activities tracked yet")
                            .font(.headline)
                            .foregroundColor(.secondary)
                        Text("Start logging your daily activities to get insights")
                            .font(.caption)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                    }
                    .padding()
                    Spacer()
                } else {
                    List {
                        ForEach(activityStore.activities) { activity in
                            ActivityRowView(activity: activity)
                        }
                        .onDelete(perform: deleteActivity)
                    }
                }
                
                // Analytics Summary
                VStack(alignment: .leading, spacing: 8) {
                    Text("Today's Summary")
                        .font(.headline)
                    
                    HStack {
                        VStack(alignment: .leading) {
                            Text("\(activityStore.todayActivities.count)")
                                .font(.title2)
                                .fontWeight(.bold)
                            Text("Activities")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        
                        Spacer()
                        
                        VStack(alignment: .trailing) {
                            Text("\(activityStore.totalDurationToday, specifier: "%.1f")h")
                                .font(.title2)
                                .fontWeight(.bold)
                            Text("Total Time")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                }
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(12)
                .padding(.horizontal)
            }
            .navigationBarHidden(true)
        }
        .sheet(isPresented: $showingNewActivity) {
            NewActivityView(activityStore: activityStore, analyticsService: analyticsService)
        }
        .sheet(isPresented: $showingSettings) {
            SettingsView(analyticsService: analyticsService)
        }
        .onAppear {
            // Auto-sync activities when app appears
            if analyticsService.isCloudSyncEnabled {
                analyticsService.syncRecentActivities(activityStore.activities)
            }
        }
    }
    
    private func deleteActivity(offsets: IndexSet) {
        activityStore.removeActivities(at: offsets)
    }
}

struct ActivityRowView: View {
    let activity: Activity
    
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(activity.title)
                    .font(.headline)
                
                Text(activity.category.rawValue)
                    .font(.caption)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 2)
                    .background(activity.category.color.opacity(0.2))
                    .foregroundColor(activity.category.color)
                    .cornerRadius(4)
                
                Text(activity.timestamp, style: .time)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            VStack(alignment: .trailing) {
                Text("\(activity.duration, specifier: "%.1f")h")
                    .font(.subheadline)
                    .fontWeight(.semibold)
                
                if !activity.notes.isEmpty {
                    Image(systemName: "note.text")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
        }
        .padding(.vertical, 4)
    }
}

#Preview {
    ContentView()
}
