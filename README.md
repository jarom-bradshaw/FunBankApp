# FunBankApp Backend

A Spring Boot-based backend application for a personal finance management system with features including account management, transaction tracking, budgeting, goal setting, and AI-powered financial analysis.

## 🚀 Features

- **User Authentication & Authorization** - Secure login/register with session management
- **Account Management** - Create and manage multiple financial accounts
- **Transaction Tracking** - Record and categorize financial transactions
- **Budget Management** - Set up and track budgets by category
- **Goal Setting** - Create and monitor financial goals
- **AI Chat Assistant** - Get financial advice and insights
- **Analytics Dashboard** - View financial reports and trends
- **RESTful API** - Comprehensive API with OpenAPI documentation

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.4.4
- **Language**: Java 21
- **Database**: MySQL (Aiven Cloud)
- **Security**: Spring Security with session-based authentication
- **Build Tool**: Gradle
- **Documentation**: OpenAPI 3.0 (Swagger UI)
- **Development**: Spring Boot DevTools

## 📋 Prerequisites

- Java 21 or higher
- Gradle 8.0 or higher
- MySQL database (or Aiven Cloud MySQL)
- Git

## 🔧 Installation & Setup

### 1. Clone the Repository
```bash
git clone <your-github-repo-url>
cd FunBankApp/backend
```

### 2. Database Setup
The application is configured to use Aiven Cloud MySQL. If you need to set up a local database:

1. Install MySQL
2. Create a database named `funbankappv1`
3. Update `application.properties` with your database credentials

### 3. Configuration
Create environment-specific configuration files:

```bash
# Copy the main properties file
cp src/main/resources/application.properties src/main/resources/application-dev.properties

# Update with your local database settings
```

### 4. Run the Application
```bash
# Using Gradle wrapper
./gradlew bootRun

# Or using the Windows batch file
.\gradlew.bat bootRun
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

Once the application is running, you can access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/api/health

## 🔐 Security

### Authentication
- Session-based authentication
- Password encryption using BCrypt
- CSRF protection (disabled in development)
- CORS configuration for cross-origin requests

### Endpoints
- `/api/auth/register` - User registration
- `/api/auth/login` - User login
- `/api/auth/logout` - User logout
- `/api/auth/profile` - Get/update user profile

## 🗄️ Database Schema

The application uses the following main entities:
- **Users** - User accounts and authentication
- **Accounts** - Financial accounts (checking, savings, etc.)
- **Transactions** - Financial transactions with categories
- **Budgets** - Budget categories and limits
- **Goals** - Financial goals and progress

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run tests with coverage
./gradlew test jacocoTestReport
```

## 🚀 Deployment

### Local Development
```bash
./gradlew bootRun
```

### Production Build
```bash
./gradlew build
java -jar build/libs/FunBankApp-0.0.1-SNAPSHOT.jar
```

### Docker (Coming Soon)
```bash
docker build -t funbankapp-backend .
docker run -p 8080:8080 funbankapp-backend
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/jarom/funbankapp/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── exception/      # Exception handling
│   │   ├── model/          # Entity models
│   │   ├── repository/     # Data access layer
│   │   ├── security/       # Security configuration
│   │   └── service/        # Business logic
│   └── resources/
│       ├── application.properties
│       ├── static/         # Static resources
│       └── templates/      # Template files
└── test/                   # Test files
```

## 🔧 Configuration

### Environment Variables
The following environment variables can be set:

- `SPRING_DATASOURCE_URL` - Database connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret
- `SERVER_PORT` - Application port (default: 8080)

### Application Properties
Key configuration options in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://your-db-host:port/database
spring.datasource.username=your-username
spring.datasource.password=your-password

# Security
jwt.secret=your-jwt-secret
jwt.expiration=86400000

# Server
server.port=8080
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🐛 Known Issues

See the [Issues](https://github.com/your-username/FunBankApp/issues) page for current bugs and feature requests.

## 📞 Support

For support, email support@funbankapp.com or create an issue in the GitHub repository.

## 🔄 Changelog

### Version 0.0.1-SNAPSHOT
- Initial release
- Basic authentication system
- Account and transaction management
- Budget and goal tracking
- AI chat integration
- Analytics dashboard

---

**Note**: This is a development version. For production use, ensure all security configurations are properly set up. 