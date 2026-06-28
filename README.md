# PlantWatch – CP3406 

Intuitive Android utility application to track plant watering schedules around the house. 
---
## Style Guide 
### Typefaces
- Helvetica Headings 
- Source Sans Pro Body

### Color Palette 
Adheres to the **Material Design 3** design system. Dynamic color signaling for individual plants 

---
## Features 
- Tracks the common name, scientific name, location, type and watering interval of the plant. 
- Uses Perenual Plant Data API to access plant data including name (common and scientific) and watering interval. 
- At a glance view of the status of each plant with a button to reset the watering interval. 
- Dropdown menu to edit details and delete plants.
- Floating Action Button for adding plants with a list of options for tracking.

### Setting Screen
- Location and Category filter to specify the desired plant.
- Sort by watering urgency or Alphabetical order. 

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
