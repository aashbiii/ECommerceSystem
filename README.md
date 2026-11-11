# E-Commerce Desktop Management & Shopping System

![Java](https://img.shields.io/badge/Java-8%20%7C%2011%20%7C%2017%2B-blue.svg)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing%20Nimbus-orange.svg)
![Database](https://img.shields.io/badge/Database-Oracle%2021c%20XE-red.svg)
![Build](https://img.shields.io/badge/Build-Apache%20Ant-green.svg)

An enterprise desktop shopping and inventory management platform built with Java Swing utilizing modern Nimbus styling, integrated with an Oracle 21c Database via JDBC thin driver.

---

## 📌 Key Modules & Features

- **Authentication & Authorization:** Customer self-registration and secure credential validation with role-differentiated access (Administrator / Customer).
- **Interactive Product Catalog:** Visual listing of inventory, category filtering, search, and live stock quantity indicators.
- **Cart & Order Processing:** Real-time quantity adjustments, subtotal/tax/grand total calculations, and atomic multi-table order creation.
- **Administrative Inventory Control:** Dynamic addition, modification, stock level updating, and deletion of products.
- **Order Tracking:** Detailed past order histories, timestamps, delivery tracking statuses, and customer itemizations.
- **System Documentation:** Full technical specifications and architecture diagrams included in `docs/E-Commerce_System_Documentation.pdf`.

---

## 📁 Repository Structure

```
├── build.xml                                  # Apache Ant build configuration
├── create_documentation.py                    # Script generating comprehensive system report
├── docs/                                      # System documentation artifact
│   └── E-Commerce_System_Documentation.pdf   # 540 KB formal specification & architecture
├── nbproject/                                 # NetBeans IDE configuration files
└── src/ecommercesystem/
    ├── DBConnection.java                      # Oracle JDBC connectivity manager
    ├── ECommerceSystem.java                   # Main entry point application class
    ├── LoginFrame.java & RegisterFrame.java   # Authentication views
    ├── DashboardFrame.java                    # Central navigational dashboard
    ├── ProductFrame.java                      # Admin inventory management view
    ├── CustomerFrame.java                     # Customer directory management view
    ├── CartFrame.java                         # Active shopping cart & checkout view
    ├── OrderFrame.java & CustomerOrderFrame.java # Order tracking & status history
    └── UIUtils.java                           # Common design components, styles, & buttons
```

---

## 🛠️ Database Configuration

- **Database:** Oracle Database 21c XE
- **JDBC Driver:** `oracle.jdbc.driver.OracleDriver`
- **Connection URL:** `jdbc:oracle:thin:@localhost:1521/FREE` (configured in `DBConnection.java`)

---

## 🚀 How to Run

1. Open the project in **NetBeans IDE**, **IntelliJ IDEA**, or **Eclipse**.
2. Ensure Oracle Database is running on `localhost:1521` with the required schema tables (`USERS`, `PRODUCTS`, `ORDERS`, `ORDER_ITEMS`).
3. Add the Oracle JDBC driver JAR (`ojdbc8.jar` or `ojdbc11.jar`) to your project classpath / libraries.
4. Run `ECommerceSystem.java` or execute:
   ```bash
   ant run
   ```

---

## 👤 Author
**Ashbi Saju**  
GitHub: [@aashbiii](https://github.com/aashbiii)
