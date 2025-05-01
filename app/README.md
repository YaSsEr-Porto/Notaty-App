Note-Taking App
A simple and interactive note-taking application built with Kotlin, Firebase, and modern Android development practices. This app allows users to create, manage, and organize their notes with options for favorites, deleting, and restoring notes. The app offers seamless synchronization with Firebase Cloud to store and retrieve notes. The user interface is designed to be clean, intuitive, and follows Material Design principles for an optimal user experience.

Features
User Authentication:
Sign up and log in via Firebase Authentication (supports Google Sign-In and email/password sign-in). User details are stored in Firebase and can be accessed in the Profile screen.

Note Management:
Add, edit, and delete notes with rich text formatting options.

Favorites:
Mark notes as favorites for easy access with an intuitive bookmarking feature.

Trash:
Restore notes from trash or permanently delete them.

Cloud Sync:
Notes are synced with Firebase Cloud in real-time, allowing access across multiple devices and ensuring data persistence.

Search:
Real-time search functionality to find notes based on titles or content.

Profile:
User-specific data is managed in Firebase, with the option to update profile information and sync their notes.

Modern UI:
The app follows Material Design guidelines, providing a clean and modern UI with RecyclerView for note listings, Floating Action Buttons for adding notes, Bottom Navigation for easy navigation, and MaterialCardView for displaying note details.

Trash Management:
A dedicated Trash screen to restore or permanently delete notes, with easy swipe gestures for interacting with the notes.

Technologies Used
Kotlin:
The primary programming language for Android development.

Firebase:
Backend services for authentication, real-time database (Firestore), cloud synchronization, and cloud storage.

Jetpack Components:
Including RecyclerView, ConstraintLayout, Navigation components, ViewModel, and LiveData for a robust app architecture with MVVM.

Material Design:
For modern UI components like buttons, cards, floating action buttons, and search bars.

Room Database:
For local persistence of notes, integrated with Firebase for seamless cloud sync.

