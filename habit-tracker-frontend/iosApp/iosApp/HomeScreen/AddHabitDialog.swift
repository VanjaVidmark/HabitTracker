import SwiftUI

struct AddHabitDialog: View {
    @Binding var isPresented: Bool
    var onSave: (String, String, String, String) -> Void

    @State private var newHabitName = ""
    @State private var newHabitDescription = ""
    @State private var newHabitFrequency = "Daily"
    @State private var newHabitStartDate = Date()

    var body: some View {
        NavigationView {
            Form {
                TextField("Habit Name", text: $newHabitName)
                TextField("Description", text: $newHabitDescription)

                Picker("Frequency", selection: $newHabitFrequency) {
                    Text("Daily").tag("Daily")
                    Text("Weekly").tag("Weekly")
                    Text("Monthly").tag("Monthly")
                }
                .pickerStyle(SegmentedPickerStyle())

                DatePicker("Start Date", selection: $newHabitStartDate, displayedComponents: .date)

                HStack {
                    Button("Cancel") {
                        isPresented = false
                    }
                    .foregroundColor(.red)

                    Spacer()

                    Button("Save") {
                        let formatter = DateFormatter()
                        formatter.dateFormat = "yyyy-MM-dd"
                        let formattedDate = formatter.string(from: newHabitStartDate)

                        onSave(newHabitName, newHabitDescription, newHabitFrequency, formattedDate)
                        isPresented = false
                    }
                    .disabled(newHabitName.isEmpty)
                }
            }
            .navigationTitle("Add Habit")
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
