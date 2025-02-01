//
//  AddEntryDialog.swift
//  iosApp
//
//  Created by Vanja Vidmark on 2025-01-30.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct AddEntryDialog: View {
    @Binding var isPresented: Bool
    var onSave: (String, String) -> Void

    @State private var newEntryTimestamp = Date()
    @State private var newEntryNote = ""

    var body: some View {
        NavigationView {
            Form {
                DatePicker("When?", selection: $newEntryTimestamp, displayedComponents: .date)
                TextField("Optional note", text: $newEntryNote)

                HStack {
                    Button("Cancel") {
                        isPresented = false
                    }
                    .foregroundColor(.red)

                    Spacer()

                    Button("Save") {
                        let formatter = DateFormatter()
                        formatter.dateFormat = "yyyy-MM-dd"
                        let formattedDate = formatter.string(from: newEntryTimestamp)

                        onSave(formattedDate, newEntryNote)
                        isPresented = false
                    }
                }
            }
            .navigationTitle("Add Entry")
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
