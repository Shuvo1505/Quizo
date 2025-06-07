# Firebase Firestore Pojo/Model rules
# These rules keep the necessary constructors and methods for Firebase Firestore
# to correctly map JSON data to your Java objects using reflection in release builds.

# Keep all fields and methods of your LeaderBoard class
-keepclassmembers class com.purnendu.quizo.models.LeaderBoard {
  <init>();  # Keep the no-argument constructor
  *;         # Keep all fields and methods (including getters and setters)
}

# Keep all fields and methods of your Attempt class
# Ensure this class has a public no-argument constructor and public getters/setters
-keepclassmembers class com.purnendu.quizo.models.Attempt {
  <init>();  # Keep the no-argument constructor
  *;         # Keep all fields and methods (including getters and setters)
}

# Optional: If you have other data classes that Firebase Firestore interacts with via toObject()
# -keepclassmembers class com.purnendu.quizo.data.YourOtherFirebasePojo {
#   <init>();
#   *;
# }

# Firebase common rules (good to include if not already present)
# Prevents issues with Firebase SDK internal classes
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }
-keep interface com.google.firebase.** { *; }

# For Room Database (if not already present)
# Keeps Room entities, DAOs, and database classes from being obfuscated or removed
-keep class * extends androidx.room.RoomDatabase
-keepclassmembers class * extends androidx.room.RoomDatabase {
    @androidx.room.DatabaseView *;
    @androidx.room.Dao *;
    @androidx.room.Entity *;
}
-keep class **.model.** { *; } # Often used if models are in a specific package
-keep class **.entity.** { *; } # Often used if entities are in a specific package
-keep class **.dao.** { *; }
-keep class **.database.** { *; }