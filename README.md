# 🔐 CryptoChat

<p align="center">
  <img src="assets/Screen - Home.png" width="200"/>
  <img src="assets/Screen - Chatrooms.png" width="200"/>
  <img src="assets/Screen - Chat.png" width="200"/>
</p>

<p align="center">
  <a href="https://android-arsenal.com/api?level=35"><img alt="API" src="https://img.shields.io/badge/API-35%2B-brightgreen.svg?style=flat"/></a>
</p>

*Important : This application is in progress, it is a working chat application but cryptographic safety functionalities are not yet implemented.*

**CryptoChat** is a secure and modern messaging application focused on **confidentiality**, **integrity**, and **privacy** of communication. It integrates cutting-edge cryptographic techniques to ensure that all user messages are protected from unauthorized access or tampering.

## ✨ Features

- 🔒 **End-to-End Encryption**: Ensures that only the sender and recipient can read the messages.
- 🛡️ **Forward Secrecy**: Implements ephemeral keys to protect past conversations even if keys are compromised.
- 🔍 **Zero-Knowledge Architecture**: No sensitive data is stored or visible to the server.
- 🧪 **Modern Cryptography**: Uses industry standards like AES, RSA, ECDH, and secure hashing (SHA-256/512).
- 📱 **User-Friendly Interface**: Built with Jetpack Compose for a clean and responsive experience.

## 🚀 Technologies

- Android (Kotlin, Jetpack Compose)
- Firebase for Authentication & Messaging
- Secure storage and cryptographic operations
- MVVM Architecture

## 📌 Goals

CryptoChat is built with the intention of:
- Promoting secure digital communication
- Demonstrating how modern cryptographic techniques can be applied to real-world applications
- Educating developers and users about privacy-first application design

## 🛠️ Future Improvements

- Group messaging with shared key management
- Secure media and file sharing

## 🔧 How to Run the Project

1. **Clone the Repository**  
   ```bash
   git clone git@github.com:GiannhsKer/Android-CryptoChat.git
2. Open in Android Studio
Launch Android Studio and open the cloned folder as a project.

3. Sync & Build
Let Gradle sync the project. Ensure all dependencies are resolved.

4. Run the App
Connect an Android device or start an emulator, then click Run (Shift + F10) to launch the app.
