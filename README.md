# Inventory Tracking (CS-360)

A lightweight Android app for small teams to record and adjust stock levels quickly. It provides credentialed login, an offline-first SQLite data model, a grid-based inventory view with create/read/update/delete (CRUD) operations, and optional SMS alerts when an item reaches a low or zero threshold. The UI uses an always-on dark theme with a green primary accent and adapts to phone and tablet orientations.

---

## Features

- **Login & account creation:** Local username/password for quick, offline access.
- **Inventory grid (CRUD):** Add items, adjust quantities with one-tap increment/decrement, and delete records; changes persist immediately.
- **Optional SMS alerts:** If permission is granted, the app can send a low/zero-stock notification; if denied, all other features continue to work normally.
- **Always-on dark theme:** High-contrast text and icon tinting for accessibility.
- **Orientation-aware layout:** Grid adapts so item names stay visible and controls remain tappable.

---

## Tech Stack & Architecture

- **Language/Platform:** Java, Android  
- **Minimum/Target SDK:** minSdk 26 (Android 8.0), targetSdk 34 (Android 14)  
- **UI:** Material Components, `RecyclerView` grid with targeted `notifyItem*` updates  
- **Data:** SQLite via `SQLiteOpenHelper` (persistent on-device database)  
- **Permissions:** `SEND_SMS` requested at runtime with a clear rationale screen; app remains fully functional if denied

### Project Structure (packages)

```text
com.cs360.project2.activities   // SmsPermissionActivity, LoginActivity, DataGridActivity
com.cs360.project2.adapter      // DataItemAdapter (RecyclerView)
com.cs360.project2.data         // DatabaseHelper, AppRepository
com.cs360.project2.model        // InventoryItem
com.cs360.project2.util         // SmsUtil
