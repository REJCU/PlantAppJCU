# PlantWatch – CP3406 

This is a basic Android app for tracking plants around the house and their watering times. 
It provides the structure for a simple tabular UI using **Jetpack Compose** and **Material Design 3**.

---
## Style Guide 
### Typefaces
- Helvetica Headings 
- Source Sans Pro Body

### Color Palette 
Uses Material3 Theme Builder.

---
## Features 
- Tracks the common name, scientific name, location, type and watering interval of the plant. 
- Uses Perenual Plant Data API to access plant data including name (common and scientific) and watering interval. 
- At a glance view of the status of each plant with a button to reset the watering interval of each plant. 
- Dropdown menu to edit the details and delete plants.
- Floating Action Button for adding plants with a list of options for tracking.

### Setting Screen
- Location and Category filter to specify the desired plant.
- Sort by watering urgency or Alphabetical order. 

---
## Getting Started
### How to Run
1. Clone or download this repo  
2. Open in Android Studio  
3. Run on an emulator or physical device (API 36+ recommended)  

---
## Composables

### UtilityApp()
- Contains the screen layout using a Scaffold
- Toggles content between Utility and Settings

### UtilityScreen()
- Displays a simple counter (replace with your utility logic)  
- Includes a button to increment the counter

### SettingsScreen()
- Placeholder for user preferences or configuration  
- Can be extended to modify main screen behavior (e.g., theme, units, limits)  

---

## Key Concepts Covered

| Week | Concept                 | Used In                                                               |
|------|-------------------------|-----------------------------------------------------------------------|
| 1    | Kotlin + Android Studio | MainActivity.kt                                                       |
| 2    | Jetpack Compose Layouts | UtilityApp(), UtilityScreen(), SettingsScreen()                       |
| 3    | Material Design 3       | CP3406_CP5603UtilityAppStarterTemplateTheme, MaterialTheme.typography |
| 4    | ViewModel               | Not included in starter                                               |
| 5    | Retrofit                | Not included in starter                                               |

---
## 📚 License
This template is provided for educational use in CP3406.  
Feel free to modify and extend it for your assessment.
