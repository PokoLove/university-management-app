# ER Diagram

```mermaid
erDiagram
    users {
        bigint id PK
        varchar userName UK
        varchar email UK
        varchar password
        varchar role "GUEST | STUDENT | TEACHER | ADMIN"
        timestamp createdAt
    }

    students {
        bigint id PK
        varchar studentName
        int age "16..100"
        varchar groupName
        double gpa "0.0..4.0"
        bigint user_id FK, UK
    }

    teachers {
        bigint id PK
        varchar teacherName
        int experienceYears "0..60"
        varchar department "IT | MATHEMATICS | PHYSICS | ECONOMICS | MANAGEMENT"
        bigint user_id FK, UK
    }

    courses {
        bigint id PK
        varchar courseName
        int credits "1..10"
        int maxStudents
        bigint teacher_id FK
    }

    student_courses {
        bigint student_id FK
        bigint course_id FK
    }

    user_files {
        bigint id PK
        bigint user_id FK
        varchar fileName
        varchar fileType
        varchar contentType
        varchar path
        timestamp uploadedAt
    }

    users ||--o| students : "one-to-one"
    users ||--o| teachers : "one-to-one"
    users ||--o{ user_files : "one-to-many"
    teachers ||--o{ courses : "one-to-many"
    students }o--o{ courses : "many-to-many"
```

## Relationships

| Relationship | Type | Description |
|---|---|---|
| `users` -- `students` | One-to-One | Each student is linked to exactly one user account |
| `users` -- `teachers` | One-to-One | Each teacher is linked to exactly one user account |
| `users` -- `user_files` | One-to-Many | A user can upload multiple files (avatar, documents) |
| `teachers` -- `courses` | One-to-Many | A teacher can teach multiple courses |
| `students` -- `courses` | Many-to-Many | Students enroll in courses via `student_courses` join table |
