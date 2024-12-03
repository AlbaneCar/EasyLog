# EasyLog: Android Application for Cafeteria Stock Management  
Developed by **Albane Carteron** and **Xavier Durumain**

---

## 📖 **Project Overview**  
**EasyLog** is an Android application designed to assist the Bureau des Élèves (BDE) in managing cafeteria stock and sales efficiently.  
The app aims to replace the current manual process involving paper and Excel spreadsheets, ensuring smoother operations, real-time tracking, and historical sales analysis.  

---

## 🎯 **Features**

### **User Management**  
- **Roles:**  
  - **Manager:** Access to all features, including user and product management.  
  - **Member:** Limited access to features, focusing on stock updates and sales tracking.  
- **Authentication:**  
  - User registration via Gmail integration.  
  - Persistent login, with re-authentication only required after manual logout.  

### **Core Functionalities**  
1. **Sales Management:**  
   - View product categories with available stock and pricing.  
   - Register a sale with a simple click and confirm via a pop-up. Stock automatically updates after confirmation.  

2. **Add New Products:**  
   - Input details (name, price, category, description, stock).  
   - New products instantly available for sales.  

3. **Stock Management:**  
   - Update stock quantities directly through a pop-up interface.  

4. **Historical Sales:**  
   - View sales data for a specific day, grouped by category and product.  
   - Analyze daily and monthly total sales and revenue.  

5. **User Management (Managers Only):**  
   - View registered users with email addresses and assigned roles.  

---

## 🛠️ **Technologies Used**  
- **Development Language:** Kotlin for Android.  
- **Database:** Firebase for real-time data storage and user authentication.  

---

## 🚀 **Getting Started**

### **Clone the Repository**  
```bash
git clone https://github.com/AlbaneCar/EasyLog.git
```
---

## 🧑‍💻 **Default Credentials**

### **Manager:**
- **Email:** `responsable@gmail.com`  
- **Password:** `test123`

### **Member:**
- **Email:** `membre@gmail.com`  
- **Password:** `test123`

---

## 📊 **Dashboard Overview**
The dashboard is designed for ease of navigation and intuitive functionality. Each role has specific access:  

| Feature                 | Manager | Member |
|-------------------------|---------|--------|
| Manage Sales            | ✅      | ✅     |
| Add New Products        | ✅      | ❌     |
| Update Stock Quantities | ✅      | ✅     |
| View Sales History      | ✅      | ✅     |
| User Management         | ✅      | ❌     |

The app minimizes clicks and optimizes workflows for enhanced user experience.

---
