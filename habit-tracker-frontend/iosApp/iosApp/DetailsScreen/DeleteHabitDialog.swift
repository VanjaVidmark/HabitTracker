//
//  DeleteHabitDialog.swift
//  iosApp
//
//  Created by Vanja Vidmark on 2025-02-01.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct DeleteHabitDialog: View {
    @Binding var isPresented: Bool
    var onYes: () -> Void

    var body: some View {
        NavigationView {
            Form {
                Text("Are you sure you wnat to delete the habit?")

                HStack {
                    Button("Cancel") {
                        isPresented = false
                    }
                    .foregroundColor(.red)

                    Spacer()

                    Button("Yes") {
                        onYes()
                        isPresented = false
                    }
                }
            }
            .navigationTitle("Delete Habit")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Close") {
                        isPresented = false
                    }
                }
            }
        }
    }
}

