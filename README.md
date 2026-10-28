# Student Record & Ranking System

An advanced full-stack academic management project built with **Java 17, Spring Boot 3, Spring Data JPA, MySQL, React, Vite, and Recharts**.

## Features
- **Student management**: add, edit, delete, search, filter (class/status), paginate. Roll numbers and emails are validated and must be unique.
- **Ranking system**: average percentage, grade (A+ to F), rank students, tied students share a rank (1, 1, 3). Top 10 performers and full leaderboard.
- **Dashboard and analytics**: student statistics, grade distribution, subject averages, top performers, performance charts.
- **Search & sort (DSA)**: sort students by marks, name or roll number and search by roll number or name prefix, using hand-written algorithms.
- No sample data is inserted. The database starts empty and you add your own students.

## DSA concepts used
| Concept | Used for | File |
|---|---|---|
| Arrays | Students are loaded into a `Student[]` that the algorithms work on | `dsa/SortingAlgorithms.java` |
| Sorting | Merge, quick, heap and insertion sort (leaderboard uses merge sort) | `dsa/SortingAlgorithms.java` |
| Searching | Linear, binary and prefix (lower-bound) search | `dsa/SearchAlgorithms.java` |
| Hash map | Roll number to student lookup | `dsa/RollNumberIndex.java` |
| Heap | Min-heap picks the Top 10 performers | `dsa/MinHeap.java` |

## Requirements
- JDK 17+
- Maven 3.9+
- Node.js 20+
- MySQL 8+

## 1. MySQL setup
1. Install and start MySQL 8+.
2. Create the database (either way works):
   - Let the app do it: the default URL has `createDatabaseIfNotExist=true`, so just start the backend, **or**
   - Run the script yourself: `mysql -u root -p < database/schema.sql`
3. Tables (`students`, `academic_records`) are created/updated automatically by Hibernate on startup.

## 2. Configure the database connection (keeps your password out of git)
1. Go to `backend/src/main/resources/`.
2. Copy `application.properties.example` to a new file named `application.properties`.
3. Open it and replace `YOUR_MYSQL_PASSWORD_HERE` with your real MySQL password (change the username if it is not `root`).

`application.properties` is listed in `.gitignore`, so your password is **never pushed to GitHub**. Only the `.example` template is committed.

## 3. Run Spring Boot API
```bash
cd backend
mvn spring-boot:run
```
API: `http://localhost:8080/api`
Health: `http://localhost:8080/api/health`

## 4. Run React UI
In a second terminal:
```bash
cd frontend
npm install
npm run dev
```
Open the Vite URL shown (usually `http://localhost:5173`).

## API overview
- `GET /api/health`
- `GET /api/dashboard`
- `GET /api/students?page=0&size=10&search=&className=&status=&sortBy=rollNumber&direction=asc`
- `POST /api/students`, `PUT /api/students/{id}`, `DELETE /api/students/{id}`
- `GET /api/classes`
- `GET /api/rankings?className=`
- `GET /api/rankings/top?limit=10&className=`
- `GET /api/dsa/sort?algorithm=merge|quick|heap|insertion&field=marks|name|roll&order=asc|desc&className=`
- `GET /api/dsa/search?mode=hash|binary|linear|prefix&q=`

## Student payload example
```json
{
  "rollNumber": "CSE2026001",
  "fullName": "Aarav Sharma",
  "email": "aarav@example.com",
  "phone": "9876543210",
  "className": "CSE-A",
  "department": "Computer Science",
  "yearOfStudy": 2,
  "status": "ACTIVE",
  "records": [
    {"term": "Semester 3", "subject": "Data Structures", "marksObtained": 88, "maxMarks": 100}
  ]
}
```

## Ranking rules
Student percentage is the average of all academic record percentages stored for that student. Rank is ordered by percentage descending, then full name, then roll number. Inactive students and students with no marks are not ranked. Equal percentages receive the same competition rank (1, 1, 3).

## Run the tests
```bash
cd backend
mvn test
```
Unit tests cover the sorting algorithms, searching, min-heap, hash index and ranking ties/top-K. They do not need MySQL.

## Important note
This is a college project starter with practical validation and a clear architecture. Before public deployment, add authentication/authorization, audit logging, database migrations, backups, and stricter operational security. Do not expose database credentials in source control.

## Pushing to GitHub safely
- Your MySQL password lives only in `backend/src/main/resources/application.properties`, which is git-ignored.
- Before the first push run `git status` and confirm `application.properties` is **not** listed (only `application.properties.example`).
- Never commit `.env` files, `target/` or `node_modules/` (all ignored already).
