# StayNest 🏡 — Airbnb-Inspired Property Booking Marketplace

A full-stack property rental marketplace built with **Java 21, Spring Boot 3, Spring Security, Spring Data JPA, MySQL, and Thymeleaf**. Built as an MCA final-year major project, structured the way an industry team would build it: layered architecture, DTOs, role-based security, and proper exception handling throughout.

---

## ✨ Features

| Module | What it does |
|---|---|
| **Auth** | Register/login/logout, BCrypt password hashing, role-based access (Guest / Host / Admin) |
| **Properties** | Hosts create/edit/delete listings (Apartment, Villa, House, Cabin, Hotel Room) with multi-photo upload |
| **Search** | Filter by city, state, country, price range, bedrooms, guest count, property type — with pagination |
| **Bookings** | Date-range picking, automatic overlap/availability checking, automatic total-price calculation, host accept/reject flow |
| **Reviews** | 1–5 star ratings + comments, one review per guest per property, edit/delete |
| **Wishlist** | Save/remove properties for later |
| **Profile** | Edit name/phone/bio/profile picture, change password |
| **Admin dashboard** | Platform stats, recent bookings, top-booked properties, manage users & listings |

---

## 🛠 Tech Stack

- **Backend:** Java 21, Spring Boot 3.2, Spring MVC, Spring Security 6, Spring Data JPA / Hibernate, Maven
- **Frontend:** Thymeleaf, Bootstrap 5, Bootstrap Icons, vanilla CSS (custom design system, not default Bootstrap look)
- **Database:** MySQL 8
- **Architecture:** Layered (Controller → Service → Repository), DTO pattern, Repository pattern, global exception handling

---

## 📁 Project Structure

```
staynest/
├── src/main/java/com/staynest/
│   ├── config/         → SecurityConfig, WebConfig
│   ├── controller/     → 8 controllers (Auth, Home, Property, Booking, Host, Admin, Profile, Review, Wishlist)
│   ├── service/        → interfaces
│   │   └── impl/       → implementations (business logic + validation lives here)
│   ├── repository/     → Spring Data JPA repositories
│   ├── entity/         → 8 JPA entities (User, Property, PropertyImage, Booking, Review, Wishlist, + 3 enums)
│   ├── dto/             → form-binding objects (never bind forms directly to entities)
│   ├── security/        → CustomUserDetails, CustomUserDetailsService
│   ├── exception/       → custom exceptions + GlobalExceptionHandler
│   └── util/             → FileStorageUtil, SecurityUtil
├── src/main/resources/
│   ├── templates/        → Thymeleaf HTML, organized by module
│   ├── static/css/       → custom design system (style.css)
│   └── application.properties
├── database/schema.sql    → reference SQL schema + seed data
├── postman/                → Postman collection for manual endpoint testing
└── pom.xml
```

---

## 🚀 Running Locally (VS Code)

### 1. Prerequisites
- **JDK 21** — check with `java -version`
- **Maven** — check with `mvn -version` (or use the included `mvnw` wrapper if you generate one)
- **MySQL 8** running locally
- **VS Code** with the **Extension Pack for Java** and **Spring Boot Extension Pack** (from the Extensions marketplace)

### 2. Unzip and open
Unzip the project, then in VS Code: `File → Open Folder` → select the `staynest` folder.
VS Code will detect the `pom.xml` and automatically download dependencies (first time takes a few minutes).

### 3. Create the database
You don't need to manually create tables — Hibernate does that automatically on first run. You only need the empty database to exist, OR rely on the connection string's `createDatabaseIfNotExist=true` flag (already set in `application.properties`), which creates `staynest_db` for you automatically.

If you'd rather set it up yourself first:
```sql
CREATE DATABASE staynest_db;
```

### 4. Configure your DB credentials
Open `src/main/resources/application.properties` and update:
```properties
spring.datasource.username=root
spring.datasource.password=root
```
to match your actual MySQL username/password.

### 5. Run it
**Option A — VS Code UI:** open `StayNestApplication.java`, click the ▶ "Run" button above the `main` method.

**Option B — Terminal (inside VS Code or any terminal):**
```bash
mvn spring-boot:run
```

**Option C — build a jar and run it:**
```bash
mvn clean package -DskipTests
java -jar target/staynest-1.0.0.jar
```

### 6. Open the app
Visit **http://localhost:8080**

### 7. Demo accounts (optional)
If you load `database/schema.sql`'s seed data, you can log in with:
| Role | Email | Password |
|---|---|---|
| Admin | admin@staynest.com | Password123 |
| Host | host1@staynest.com | Password123 |
| Guest | guest1@staynest.com | Password123 |

Otherwise, just register your own account via **Sign up** — choose Guest or Host (Admin accounts aren't self-registrable, by design, for security).

---

## 📤 Pushing to GitHub (from VS Code)

1. **Initialize git** (skip if VS Code already shows a Source Control icon with changes):
   ```bash
   git init
   git add .
   git commit -m "Initial commit: StayNest full-stack Airbnb-inspired marketplace"
   ```
2. **Create a new repository on GitHub** (via [github.com/new](https://github.com/new)) — don't initialize it with a README, since you already have one.
3. **Connect and push:**
   ```bash
   git branch -M main
   git remote add origin https://github.com/<your-username>/staynest.git
   git push -u origin main
   ```
   Or, in VS Code: open the **Source Control** tab (left sidebar) → click **Publish Branch** → pick "Publish to GitHub" → choose public/private.

The included `.gitignore` already excludes `target/`, `.idea/`, `uploads/`, and compiled files, so your repo stays clean.

---

## 🌐 Deploying & Getting a Real Domain

Getting this live on the internet with a real domain involves a few separate pieces, worth understanding clearly before you commit money/time:

1. **Hosting the app** — options like Railway, Render, or a basic VPS (DigitalOcean/AWS EC2) can run a Spring Boot jar. Most free/cheap tiers have an *ephemeral filesystem* — meaning uploaded property photos would vanish on every redeploy unless you switch `FileStorageUtil` to use cloud storage (AWS S3 / Cloudinary) instead of local disk. The code is already isolated so that's a contained change.
2. **Hosting the database** — a managed MySQL instance (PlanetScale, Railway MySQL, AWS RDS) rather than running MySQL on the same small server as the app.
3. **Domain name** — registrars like Namecheap, GoDaddy, or Google Domains; point its DNS to your hosting provider's IP/CNAME.
4. **HTTPS** — most modern hosts (Render, Railway) provision free TLS certificates automatically once a custom domain is attached.

**Honest note:** none of the above is hard, but turning this into an actual *business* that takes real bookings and real money also needs things outside the codebase — payment gateway compliance (Razorpay/Stripe KYC), terms of service, host/guest dispute handling, and (depending on your country) possibly real-estate or short-term-rental regulations. I can help with any of those next steps individually when you're ready — happy to start with whichever matters most to you.

---

## 🔐 Security Notes
- Passwords are hashed with BCrypt — never stored or logged in plain text.
- CSRF protection is **on** (Spring Security default) — every Thymeleaf form using `th:action` automatically carries a hidden CSRF token.
- Role checks happen at two levels: URL patterns (`SecurityConfig`) AND object-ownership checks inside the service layer (e.g. a Host can only edit *their own* listings) — the first layer alone can't catch that.

## 🗺 Suggested "Phase 2" Enhancements
(left out of this version deliberately, so the delivered code stays reviewable and reliable — not because they're hard)
- Real payment gateway integration (Razorpay/Stripe) instead of the simulated `PaymentStatus` field
- Cloud file storage (S3) instead of local disk
- Admin chart visualizations on the dashboard (Chart.js)
- Amenity as its own entity + many-to-many, instead of a comma-separated string
- Availability calendar UI (visual date blocking) instead of plain date inputs
- Email notifications on booking confirm/reject

---

## 📄 License
This project was built for academic/educational purposes. Feel free to use it as a portfolio piece.
Local run note 1
Local run note 2
Local run note 3
Local run note 4
Local run note 5
Local run note 6
Local run note 7
