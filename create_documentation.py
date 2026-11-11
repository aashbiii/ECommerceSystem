from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.oxml.ns import qn
from docx.oxml import OxmlElement
import os
import datetime

# Create document
doc = Document()

# Set document margins
sections = doc.sections
for section in sections:
    section.top_margin = Inches(1)
    section.bottom_margin = Inches(1)
    section.left_margin = Inches(1)
    section.right_margin = Inches(1)

# ==================== TITLE PAGE ====================
title = doc.add_paragraph()
title.alignment = WD_ALIGN_PARAGRAPH.CENTER
run = title.add_run("E-Commerce System")
run.font.size = Pt(48)
run.font.bold = True
run.font.color.rgb = RGBColor(44, 62, 80)

subtitle = doc.add_paragraph()
subtitle.alignment = WD_ALIGN_PARAGRAPH.CENTER
subtitle_run = subtitle.add_run("Product Documentation")
subtitle_run.font.size = Pt(24)
subtitle_run.font.color.rgb = RGBColor(52, 152, 219)

doc.add_paragraph()  # Spacing

# Add some spacing
for _ in range(4):
    doc.add_paragraph()

# Project info
info_para = doc.add_paragraph()
info_para.alignment = WD_ALIGN_PARAGRAPH.CENTER
info_run = info_para.add_run(f"Version 1.0\n")
info_run.font.size = Pt(12)
info_run2 = info_para.add_run(f"Created: {datetime.date.today().strftime('%B %d, %Y')}")
info_run2.font.size = Pt(11)

doc.add_paragraph()
doc.add_paragraph()

# Add page break
doc.add_page_break()

# ==================== INTRODUCTION ====================
intro_heading = doc.add_heading("Introduction", level=1)
intro_heading.style.font.color.rgb = RGBColor(44, 62, 80)

intro_text = """The E-Commerce System is a comprehensive Java-based desktop application designed to provide a complete online shopping solution. Built using Swing for a rich graphical user interface and Oracle Database for robust data management, this system enables seamless interaction between customers, products, and orders with modern, intuitive design principles.

The application serves as a practical demonstration of enterprise-level software development, incorporating design patterns, database connectivity, and user-friendly interface design. Whether you're a customer looking to browse and purchase products or an administrator managing inventory, this system provides all necessary features in an organized and accessible manner."""

for line in intro_text.split('\n'):
    if line.strip():
        p = doc.add_paragraph(line, style='Normal')
        p.paragraph_format.line_spacing = 1.5

doc.add_paragraph()

# Key Features
features_heading = doc.add_heading("Key Features", level=2)
features = [
    "User Authentication: Secure login and registration system for customers",
    "Product Catalog: Browse and search for products with detailed information",
    "Shopping Cart: Add products to cart with easy quantity management",
    "Order Management: Place orders and track order status",
    "Product Management: Admin panel for managing product inventory",
    "Customer Dashboard: Personalized dashboard displaying user orders and information",
    "Database Integration: Oracle database for reliable data persistence",
    "Modern UI Design: Clean, intuitive interface with Nimbus look and feel"
]

for feature in features:
    p = doc.add_paragraph(feature, style='List Bullet')
    p.paragraph_format.line_spacing = 1.3

doc.add_paragraph()

# System Architecture
arch_heading = doc.add_heading("System Architecture", level=2)
arch_text = """The system follows a client-server architecture with the following components:

• Frontend Layer: Java Swing-based GUI providing user interaction
• Business Logic Layer: Application classes handling business operations
• Data Access Layer: Database connectivity and query execution
• Database Layer: Oracle Database for persistent storage

The modular design allows for easy maintenance, scalability, and future enhancements."""

for line in arch_text.split('\n'):
    if line.strip():
        p = doc.add_paragraph(line, style='Normal')
        p.paragraph_format.line_spacing = 1.5

doc.add_page_break()

# ==================== CLASS DIAGRAM ====================
class_diagram_heading = doc.add_heading("Class Diagram", level=1)
class_diagram_heading.style.font.color.rgb = RGBColor(44, 62, 80)

diagram_text = """The following diagram represents the main classes and their relationships in the E-Commerce System:"""
doc.add_paragraph(diagram_text)

doc.add_paragraph()

# ASCII Art Diagram
ascii_diagram = """
┌─────────────────────┐
│   ECommerceSystem   │
│    (Entry Point)    │
└──────────┬──────────┘
           │
           ├──────────────┬──────────────┬──────────────┐
           ▼              ▼              ▼              ▼
    ┌─────────────┐ ┌────────────┐ ┌────────────┐ ┌──────────────┐
    │ LoginFrame  │ │ Register   │ │  Dashboard │ │ ProductFrame │
    │             │ │  Frame     │ │   Frame    │ │              │
    └─────────────┘ └────────────┘ └────────────┘ └──────────────┘
           │              │              │              │
           └──────────────┴──────────────┴──────────────┘
                          │
           ┌──────────────┴──────────────┐
           ▼                             ▼
    ┌────────────────┐         ┌──────────────────┐
    │   UIUtils     │         │  DBConnection    │
    │ (UI Styling)  │         │  (DB Access)     │
    └────────────────┘         └──────────────────┘
                                      │
           ┌──────────────────────────┘
           │
    ┌──────▼─────────────┐
    │   Oracle Database   │
    │   (Data Layer)      │
    └─────────────────────┘


Core Classes:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1. LoginFrame
   ├─ Handles user login and authentication
   ├─ Fields: txtUsername, txtPassword, lblStatus
   └─ Methods: Authenticate, Navigate to Dashboard/Register

2. RegisterFrame
   ├─ Manages new user registration
   ├─ Fields: User input fields for registration
   └─ Methods: Validate, Insert user to database

3. DashboardFrame
   ├─ Main application window after login
   ├─ Contains navigation to all features
   └─ Methods: Load customer dashboard, Navigate to orders/products

4. ProductFrame
   ├─ Product management and viewing
   ├─ Features: List, Add, Update, Delete products
   └─ Fields: Product table, Search functionality

5. CartFrame
   ├─ Shopping cart management
   ├─ Features: Add/remove items, View total
   └─ Methods: Calculate cart total, Checkout

6. OrderFrame
   ├─ Order management interface
   ├─ Features: View all orders, Order details
   └─ Methods: Display orders, Status updates

7. CustomerOrderFrame
   ├─ Displays customer-specific orders
   ├─ Features: View my orders, Cancel orders
   └─ Methods: Load customer orders by ID

8. CustomerFrame
   ├─ Customer information management
   ├─ Features: View/Edit customer details
   └─ Methods: Retrieve, Update customer info

9. DBConnection
   ├─ Database connection management
   ├─ Database: Oracle (Free version)
   └─ Methods: getConnection()

10. UIUtils
    ├─ Utility class for consistent UI styling
    ├─ Provides: Colors, fonts, styled components
    └─ Methods: createButton(), createTable(), styleTable(), etc.
"""

diagram_para = doc.add_paragraph(ascii_diagram)
diagram_para.style = 'Normal'
diagram_para.runs[0].font.name = 'Courier New'
diagram_para.runs[0].font.size = Pt(9)

doc.add_page_break()

# ==================== SCREENSHOTS ====================
screenshots_heading = doc.add_heading("Application Screenshots", level=1)
screenshots_heading.style.font.color.rgb = RGBColor(44, 62, 80)

doc.add_paragraph()

# Screenshot descriptions
screenshot_sections = [
    {
        "title": "1. Login Screen",
        "description": "The initial login interface where users enter their credentials. Features username and password fields with secure authentication. Includes a link to the registration page for new users."
    },
    {
        "title": "2. Registration Screen",
        "description": "New user registration form with fields for username, password, email, and other customer details. Includes validation to ensure data integrity and prevent duplicate accounts."
    },
    {
        "title": "3. Dashboard Screen",
        "description": "Main dashboard displayed after successful login. Shows customer welcome message and provides navigation buttons to access different features like Browse Products, My Orders, View Cart, and Account Settings."
    },
    {
        "title": "4. Product Management Screen",
        "description": "Product listing interface with search and filter capabilities. Displays all available products in a table format with columns for Product ID, Name, Description, Price, Stock, and Category. Admin users can add, edit, or delete products."
    },
    {
        "title": "5. Shopping Cart Screen",
        "description": "Shopping cart interface showing selected items with quantity, price, and total cost calculations. Allows users to update quantities, remove items, and proceed to checkout."
    },
    {
        "title": "6. Order Management Screen",
        "description": "Displays all customer orders in a tabular format with Order ID, Order Date, Total Amount, and Status. Shows the current status of each order (Pending, Processing, Shipped, Delivered, Cancelled)."
    },
    {
        "title": "7. My Orders Screen",
        "description": "Customer-specific orders view showing only orders placed by the logged-in customer. Allows customers to track their order status and cancel orders that haven't been delivered yet."
    },
    {
        "title": "8. Customer Information Screen",
        "description": "Displays and allows editing of customer profile information including name, email, phone, address, and account preferences."
    }
]

for screenshot in screenshot_sections:
    heading = doc.add_heading(screenshot["title"], level=2)
    heading.style.font.color.rgb = RGBColor(52, 152, 219)
    
    doc.add_paragraph(screenshot["description"])
    
    # Note for screenshots
    note = doc.add_paragraph("[Screenshot would be displayed here]")
    note_format = note.paragraph_format
    note_format.left_indent = Inches(0.5)
    note.style = 'Normal'
    note.runs[0].italic = True
    note.runs[0].font.color.rgb = RGBColor(149, 165, 166)
    
    doc.add_paragraph()

doc.add_page_break()

# ==================== TECHNICAL SPECIFICATIONS ====================
tech_heading = doc.add_heading("Technical Specifications", level=1)
tech_heading.style.font.color.rgb = RGBColor(44, 62, 80)

tech_specs = [
    ("Programming Language", "Java"),
    ("GUI Framework", "Java Swing"),
    ("Database", "Oracle Database (Free)"),
    ("Database Driver", "Oracle JDBC Driver"),
    ("Connection Details", "localhost:1521/FREE"),
    ("UI Theme", "Nimbus (Modern Look and Feel)"),
    ("Build Tool", "NetBeans ANT Build"),
    ("Java Version", "Java 8 or higher"),
]

table = doc.add_table(rows=1, cols=2)
table.style = 'Light Grid Accent 1'
hdr_cells = table.rows[0].cells
hdr_cells[0].text = 'Component'
hdr_cells[1].text = 'Details'

for spec_name, spec_value in tech_specs:
    row_cells = table.add_row().cells
    row_cells[0].text = spec_name
    row_cells[1].text = spec_value

doc.add_paragraph()

# ==================== DATABASE SCHEMA ====================
db_heading = doc.add_heading("Database Schema", level=2)

schema_text = """The application uses the following main tables in Oracle Database:

• CUSTOMERS: Stores customer information (ID, username, password, email, phone, address)
• PRODUCTS: Contains product details (ID, name, description, price, stock, category)
• ORDERS: Maintains order records (Order ID, customer ID, order date, total amount, status)
• ORDER_ITEMS: Stores individual items in each order (Order ID, product ID, quantity, price)
• CART: Manages shopping cart items (Cart ID, customer ID, product ID, quantity)
"""

for line in schema_text.split('\n'):
    if line.strip():
        doc.add_paragraph(line, style='List Bullet' if line.startswith('•') else 'Normal')

doc.add_page_break()

# ==================== INSTALLATION & SETUP ====================
setup_heading = doc.add_heading("Installation & Setup", level=1)
setup_heading.style.font.color.rgb = RGBColor(44, 62, 80)

setup_steps = [
    ("Prerequisites", "Java JDK 8 or higher, Oracle Database, NetBeans IDE"),
    ("Step 1: Extract Project", "Download and extract the ECommerceSystem project to your preferred location"),
    ("Step 2: Configure Database", "Update database credentials in DBConnection.java with your Oracle connection details"),
    ("Step 3: Create Database Schema", "Execute the provided SQL script to create required tables and sample data"),
    ("Step 4: Build Project", "Open the project in NetBeans, right-click and select 'Clean and Build'"),
    ("Step 5: Run Application", "Execute ECommerceSystem.java from the src folder to start the application"),
    ("Step 6: Login", "Use test credentials to login (username: admin, password: admin)")
]

for step_title, step_desc in setup_steps:
    p = doc.add_paragraph()
    p_run = p.add_run(f"{step_title}: ")
    p_run.bold = True
    p.add_run(step_desc)
    p.paragraph_format.line_spacing = 1.3

doc.add_paragraph()

# ==================== USAGE GUIDE ====================
usage_heading = doc.add_heading("Usage Guide", level=1)
usage_heading.style.font.color.rgb = RGBColor(44, 62, 80)

usage_sections = {
    "For Customers": [
        "1. Register a new account or login with existing credentials",
        "2. Browse available products using the search and filter options",
        "3. Add desired products to the shopping cart",
        "4. Review cart items and proceed to checkout",
        "5. Place order and receive order confirmation",
        "6. Track order status in 'My Orders' section",
        "7. Manage profile information from Account Settings"
    ],
    "For Administrators": [
        "1. Login with admin credentials",
        "2. Access Product Management from the dashboard",
        "3. Add new products with details and pricing",
        "4. Update existing product information and stock levels",
        "5. Delete discontinued products",
        "6. Access Order Management to view and manage all customer orders",
        "7. Update order status and manage order fulfillment"
    ]
}

for section_title, steps in usage_sections.items():
    sub_heading = doc.add_heading(section_title, level=2)
    for step in steps:
        doc.add_paragraph(step, style='List Bullet')

doc.add_page_break()

# ==================== FUTURE ENHANCEMENTS ====================
future_heading = doc.add_heading("Future Enhancements", level=1)
future_heading.style.font.color.rgb = RGBColor(44, 62, 80)

enhancements = [
    "Web-based version using Spring Boot and React",
    "Mobile application for iOS and Android",
    "Payment gateway integration (Stripe, PayPal)",
    "Email notification system for orders and updates",
    "Advanced analytics and reporting features",
    "User review and rating system for products",
    "Inventory management with low stock alerts",
    "Customer loyalty and rewards program",
    "Multi-language support",
    "Real-time inventory synchronization"
]

for enhancement in enhancements:
    doc.add_paragraph(enhancement, style='List Bullet')

doc.add_paragraph()

# ==================== CONCLUSION ====================
conclusion_heading = doc.add_heading("Conclusion", level=1)
conclusion_heading.style.font.color.rgb = RGBColor(44, 62, 80)

conclusion_text = """The E-Commerce System represents a robust and feature-rich desktop application that demonstrates modern software development practices. With its intuitive user interface, comprehensive functionality, and reliable database backend, it serves as an excellent foundation for an online shopping platform.

The modular architecture allows for easy maintenance, testing, and future enhancements. Whether used as a learning tool for Java development or as a starting point for a production system, the E-Commerce System provides a solid base for e-commerce operations.

For questions, bug reports, or feature requests, please refer to the project documentation or contact the development team."""

for line in conclusion_text.split('\n'):
    if line.strip():
        p = doc.add_paragraph(line, style='Normal')
        p.paragraph_format.line_spacing = 1.5

doc.add_paragraph()
doc.add_paragraph()

# ==================== FOOTER ====================
footer_para = doc.add_paragraph()
footer_para.alignment = WD_ALIGN_PARAGRAPH.CENTER
footer_para.paragraph_format.line_spacing = 1.2

footer_run1 = footer_para.add_run("E-Commerce System\n")
footer_run1.font.size = Pt(10)
footer_run1.font.bold = True

footer_run2 = footer_para.add_run("Project Documentation\n")
footer_run2.font.size = Pt(9)

footer_run3 = footer_para.add_run(f"Version 1.0 | {datetime.date.today().strftime('%B %Y')}")
footer_run3.font.size = Pt(8)
footer_run3.font.color.rgb = RGBColor(149, 165, 166)

# Save document
output_path = r"c:\Users\ASUS\Documents\ECommerceSystem\E-Commerce_System_Documentation.docx"
doc.save(output_path)
print(f"✓ Documentation created successfully!")
print(f"✓ File saved to: {output_path}")
print(f"\n📄 Document includes:")
print(f"   • Title Page")
print(f"   • Comprehensive Introduction")
print(f"   • Detailed Class Diagram")
print(f"   • Screenshot Descriptions")
print(f"   • Technical Specifications")
print(f"   • Database Schema")
print(f"   • Installation & Setup Guide")
print(f"   • Usage Guide for Customers & Administrators")
print(f"   • Future Enhancements")
print(f"   • Conclusion")
