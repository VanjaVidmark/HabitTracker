//
//  ChatRoomDemo.swift
//  iosApp
//
//  Created by Vanja Vidmark on 2025-01-30.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import shared

@available(iOS 17.0, *)
struct HomeScreen: View {
    @StateObject private var viewModel = HabitsViewModelWrapper()
    @State private var showAddHabitDialog = false
    @State private var selectedHabit: Habit? = nil

    var body: some View {
        NavigationStack {
            ZStack(alignment: .bottomTrailing) {
                VStack {
                    if viewModel.isLoading {
                        ProgressView()
                            .padding()
                    } else if let errorMessage = viewModel.errorMessage {
                        Text("Error: \(errorMessage)")
                            .foregroundColor(.red)
                            .padding()
                    } else {
                        // page content
                        // say "habit tracker"
                        // below i want a section that shows any habits i should do today, or that is due
                        // can i calculate either from the habits start date OR if there is one, the latest entry for that habit
                        // and if it is marked as eg "weekly" i make it pop up at this top bar if it was 7 days ago or more since the creation/entry
                        
                        // ah i want to store a list of entries like "habit.entries" instead of the other way around like i have now
                        List(viewModel.habits, id: \.id) { habit in
                            Button(action: {
                                selectedHabit = habit
                            }) {
                                VStack(alignment: .leading) {
                                    Text(habit.name).font(.headline)
                                    Text(habit.description_).font(.subheadline)
                                    Text("Start Date: \(habit.startDate)")
                                        .font(.caption)
                                        .foregroundColor(.gray)
                                }
                            }
                        }
                    }
                }
                if !viewModel.isLoading && viewModel.errorMessage == nil {
                    // add button
                    Button(action: { showAddHabitDialog = true }) {
                        Image(systemName: "plus")
                            .font(.system(size: 24))
                            .padding()
                            .background(Color.blue)
                            .foregroundColor(.white)
                            .clipShape(Circle())
                            .shadow(radius: 5)
                    }
                    .padding()
                    
                }
            }
            .sheet(isPresented: $showAddHabitDialog) {
                AddHabitDialog(isPresented: $showAddHabitDialog, onSave: viewModel.addHabit)
            }
            .navigationDestination(item: $selectedHabit) { habit in
                DetailsScreen(habitId: habit.id, viewModel: viewModel, onBack:{
                    selectedHabit = nil
                })
                    .onDisappear{
                        selectedHabit = nil
                    }
            }
        }
    }
}

class HabitsViewModelWrapper: ObservableObject {
    private let viewModel = HabitsViewModel()

    @Published var habits: [Habit] = []
    @Published var isLoading: Bool = true
    @Published var errorMessage: String? = nil

    init() {
        Task {
            await activate()
        }
    }

    @MainActor
    func activate() async {
        for await newState in viewModel.observeHabits() {
            logMessage(tag: "Swift", message: "Fetching habits from API")
            logMessage(tag: "Swift", message: String(newState.habits.count))

            self.habits = newState.habits
            self.isLoading = newState.loading
            self.errorMessage = newState.error
        }
    }
    
    func addHabit(name: String, description: String, frequency: String, startDate: String) {
            Task {
                do {
                    try await viewModel.addHabit(
                        name: name,
                        description: description,
                        frequency: frequency,
                        startDate: startDate
                    )
                } catch {
                    await MainActor.run {
                        self.errorMessage = "Failed to add habit."
                    }
                }
            }
        }
    
    func addEntry(habitId: String, timestamp: String, note: String) {
            Task {
                do {
                    try await viewModel.addEntry(
                        habitId: habitId,
                        timestamp: timestamp,
                        note: note
                    )
                } catch {
                    await MainActor.run {
                        self.errorMessage = "Failed to add entry."
                    }
                }
            }
        }
    
    func deleteHabit(habitId: String) {
            Task {
                do {
                    try await viewModel.deleteHabit(habitId: habitId)
                } catch {
                    await MainActor.run {
                        self.errorMessage = "Failed to delete habit."
                    }
                }
            }
        }
}
