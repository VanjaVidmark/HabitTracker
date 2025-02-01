//
//  DetailsScreen.swift
//  iosApp
//
//  Created by Vanja Vidmark on 2025-01-30.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import shared

struct DetailsScreen: View {
    let habitId: String
    @ObservedObject var viewModel: HabitsViewModelWrapper
    var onBack: () -> Void
    
    @State private var showAddEntryDialog = false
    @State private var showDeleteHabitDialog = false
    
    var habit: Habit? {
            viewModel.habits.first { $0.id == habitId }
        }

    var body: some View {
        ZStack(alignment: .bottomTrailing) {
            VStack {
                if viewModel.isLoading {
                    ProgressView()
                        .padding()
                } else if let errorMessage = viewModel.errorMessage {
                    Text("Error: \(errorMessage)")
                        .foregroundColor(.red)
                        .padding()
                } else if let habit = habit {
                    if habit.entries.isEmpty {
                        Text("No entries yet. Add your first entry!")
                            .font(.headline)
                            .foregroundColor(.gray)
                            .padding()
                    } else {
                        EntriesList(entries: habit.entries)
                    }
                    Spacer()
                    Button(action: { showDeleteHabitDialog = true }) {
                        Text("Delete Habit")
                            .font(.headline)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.red)
                            .foregroundColor(.white)
                            .clipShape(RoundedRectangle(cornerRadius: 10))
                            .padding(.horizontal)
                    }
                } else {
                    Text("Habit not found")
                        .foregroundColor(.red)
                        .padding()
                }
            }
            if !viewModel.isLoading && viewModel.errorMessage == nil {
                // ADD BUTTON
                Button(action: { showAddEntryDialog = true }) {
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
        .sheet(isPresented: $showAddEntryDialog) {
            if let habit = habit {
                AddEntryDialog(
                    isPresented: $showAddEntryDialog,
                    onSave: { timestamp, note in
                        viewModel.addEntry(habitId: habit.id, timestamp: timestamp, note: note)
                    }
                )
            }
        }
        .sheet(isPresented: $showDeleteHabitDialog) {
            if let habit = habit {
                DeleteHabitDialog(
                    isPresented: $showDeleteHabitDialog,
                    onYes: {
                        viewModel.deleteHabit(habitId: habit.id)
                        onBack()
                    }
                )
            }
        }
        .navigationTitle(habit?.name ?? "Habit Details")
    }
}

struct EntriesList: View {
    let entries: [Entry]
    
    var body: some View {
        List(entries, id: \.id) { entry in
            EntryItem(entry: entry)
        }
    }
}

struct EntryItem: View {
    let entry: Entry
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Date: \(entry.timestamp)")
                .font(.headline)
            Text(entry.note)
                .font(.subheadline)
                .foregroundColor(.gray)
            Divider()
        }
        .padding(8)
    }
}
