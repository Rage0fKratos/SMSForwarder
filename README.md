# SMS Forwarder

**SMS Forwarder** is a modern, lightweight Android application built with **Jetpack Compose** and **Material 3**. It automatically monitors incoming SMS messages and redirects them to a pre-configured phone number based on custom keywords.

## 🚀 Features

*   **Keyword Filtering:** Define specific keywords (e.g., "OTP", "Bank", "Urgent"). Only messages containing these keywords are forwarded.
*   **Intelligent Loop Prevention:** Prevents infinite forwarding loops between two phones by automatically detecting if the sender is the destination number.
*   **Smart Sender Handling:** Correctly handles alphanumeric sender IDs (like `AD-ICICI-T`) to ensure bank alerts and service messages are never missed.
*   **Activity Logs:** A dedicated screen to view the history of all incoming messages, categorized by status (Forwarded vs. Skipped) with color-coded entries.
*   **Service Toggle:** Easily enable or pause the forwarding service with a single master switch.
*   **Battery Optimization Handling:** Includes built-in prompts to disable system battery optimizations, ensuring the app stays active in the background for reliable delivery.
*   **Material 3 UI:** A clean, modern "light" aesthetic with edge-to-edge support and dynamic top app bars.

## 🛠 Tech Stack

*   **Language:** Kotlin
*   **UI:** Jetpack Compose (Material 3)
*   **Architecture:** BroadcastReceiver for background monitoring.
*   **Persistence:** SharedPreferences for settings and logs.
*   **Notifications:** Android NotificationManager for real-time status updates.

## 📸 How to Use

1.  **Grant Permissions:** Upon first launch, grant `RECEIVE_SMS`, `SEND_SMS`, and `POST_NOTIFICATIONS` permissions.
2.  **Configure Settings:**
    *   Enter your **Keywords** (separated by commas).
    *   Enter the **Destination Phone Number**.
    *   Click **Save Configuration**.
3.  **Disable Battery Optimization:** Click the prompt on the home screen to ensure Android doesn't kill the app in the background.
4.  **Monitor Logs:** Use the **View Forwarding Logs** button to see a detailed, color-coded history of every message processed.

## 🔒 Permissions

The app requires the following permissions to function:
*   `android.permission.RECEIVE_SMS`: To detect incoming messages.
*   `android.permission.SEND_SMS`: To forward messages to your target number.
*   `android.permission.READ_SMS`: To read message content for filtering.
*   `android.permission.POST_NOTIFICATIONS`: To show status alerts.
*   `android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`: To ensure reliable background operation.

## ⚠️ Disclaimer

This app is designed for personal use (e.g., forwarding OTPs to a secondary device). Please ensure you have the right to forward messages and comply with local privacy laws and telecommunication regulations.

---
*Developed with ❤️ using Android Studio & Jetpack Compose.*
