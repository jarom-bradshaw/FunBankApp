# Issue #1.7: Phase 4 - Implement Advanced Investment Management with Monte Carlo Simulations

## 🎯 **Priority: HIGH** 
**Labels**: `enhancement`, `high-priority`, `backend`, `feature`, `investment`, `monte-carlo`, `phase-4`

## 📋 **Description**
Implement advanced investment management system with Monte Carlo simulations for retirement planning, portfolio management, asset allocation tracking, and investment performance analysis.

## 🏗️ **Architecture**
Following the existing pattern: Controller → Service → Repository → RepositoryImpl

## 📊 **Implementation Tasks**

### **Models to Create:**
- [ ] `src/main/java/com/jarom/funbankapp/model/Investment.java`
- [ ] `src/main/java/com/jarom/funbankapp/model/InvestmentType.java` (Enum)
- [ ] `src/main/java/com/jarom/funbankapp/model/Portfolio.java`
- [ ] `src/main/java/com/jarom/funbankapp/model/MonteCarloSimulation.java`
- [ ] `src/main/java/com/jarom/funbankapp/model/InvestmentPerformance.java`
- [ ] `src/main/java/com/jarom/funbankapp/model/AssetAllocation.java`
- [ ] `src/main/java/com/jarom/funbankapp/model/PortfolioHolding.java`

### **DTOs to Create:**
- [ ] `src/main/java/com/jarom/funbankapp/dto/InvestmentDTO.java`
- [ ] `src/main/java/com/jarom/funbankapp/dto/InvestmentRequest.java`
- [ ] `src/main/java/com/jarom/funbankapp/dto/PortfolioDTO.java`
- [ ] `src/main/java/com/jarom/funbankapp/dto/MonteCarloRequest.java`
- [ ] `src/main/java/com/jarom/funbankapp/dto/MonteCarloResultDTO.java`
- [ ] `src/main/java/com/jarom/funbankapp/dto/AssetAllocationDTO.java`
- [ ] `src/main/java/com/jarom/funbankapp/dto/InvestmentPerformanceDTO.java`
- [ ] `src/main/java/com/jarom/funbankapp/dto/PortfolioHoldingDTO.java`

### **Controllers to Create:**
- [ ] `src/main/java/com/jarom/funbankapp/controller/InvestmentController.java`
- [ ] `src/main/java/com/jarom/funbankapp/controller/PortfolioController.java`
- [ ] `src/main/java/com/jarom/funbankapp/controller/MonteCarloController.java`
- [ ] `src/main/java/com/jarom/funbankapp/controller/InvestmentAnalyticsController.java`

### **Services to Create:**
- [ ] `src/main/java/com/jarom/funbankapp/service/InvestmentService.java`
- [ ] `src/main/java/com/jarom/funbankapp/service/PortfolioService.java`
- [ ] `src/main/java/com/jarom/funbankapp/service/MonteCarloService.java`
- [ ] `src/main/java/com/jarom/funbankapp/service/InvestmentAnalyticsService.java`
- [ ] `src/main/java/com/jarom/funbankapp/service/AssetAllocationService.java`
- [ ] `src/main/java/com/jarom/funbankapp/service/InvestmentPerformanceService.java`

### **Repositories to Create:**
- [ ] `src/main/java/com/jarom/funbankapp/repository/InvestmentRepository.java`
- [ ] `src/main/java/com/jarom/funbankapp/repository/InvestmentRepositoryImpl.java`
- [ ] `src/main/java/com/jarom/funbankapp/repository/PortfolioRepository.java`
- [ ] `src/main/java/com/jarom/funbankapp/repository/PortfolioRepositoryImpl.java`
- [ ] `src/main/java/com/jarom/funbankapp/repository/MonteCarloSimulationRepository.java`
- [ ] `src/main/java/com/jarom/funbankapp/repository/MonteCarloSimulationRepositoryImpl.java`

## 🧪 **Tests to Create (TDD Approach):**
- [ ] `src/test/java/com/jarom/funbankapp/controller/InvestmentControllerTest.java`
- [ ] `src/test/java/com/jarom/funbankapp/controller/PortfolioControllerTest.java`
- [ ] `src/test/java/com/jarom/funbankapp/controller/MonteCarloControllerTest.java`
- [ ] `src/test/java/com/jarom/funbankapp/controller/InvestmentAnalyticsControllerTest.java`
- [ ] `src/test/java/com/jarom/funbankapp/service/InvestmentServiceTest.java`
- [ ] `src/test/java/com/jarom/funbankapp/service/PortfolioServiceTest.java`
- [ ] `src/test/java/com/jarom/funbankapp/service/MonteCarloServiceTest.java`
- [ ] `src/test/java/com/jarom/funbankapp/service/InvestmentAnalyticsServiceTest.java`
- [ ] `src/test/java/com/jarom/funbankapp/repository/InvestmentRepositoryTest.java`
- [ ] `src/test/java/com/jarom/funbankapp/repository/PortfolioRepositoryTest.java`

## 📝 **API Endpoints to Implement:**

### **Investment Management:**
```
POST   /api/investments              - Create investment
GET    /api/investments              - List investments
GET    /api/investments/{id}         - Get investment details
PUT    /api/investments/{id}         - Update investment
DELETE /api/investments/{id}         - Delete investment
POST   /api/investments/{id}/update-price - Update current price
```

### **Portfolio Management:**
```
POST   /api/portfolios               - Create portfolio
GET    /api/portfolios               - List portfolios
GET    /api/portfolios/{id}          - Get portfolio details
PUT    /api/portfolios/{id}          - Update portfolio
DELETE /api/portfolios/{id}          - Delete portfolio
POST   /api/portfolios/{id}/holdings - Add holding to portfolio
GET    /api/portfolios/{id}/holdings - Get portfolio holdings
```

### **Monte Carlo Simulations:**
```
POST   /api/monte-carlo/simulate     - Run Monte Carlo simulation
GET    /api/monte-carlo/simulations  - List simulations
GET    /api/monte-carlo/simulations/{id} - Get simulation results
POST   /api/monte-carlo/retirement-planning - Retirement planning simulation
POST   /api/monte-carlo/portfolio-optimization - Portfolio optimization
```

### **Investment Analytics:**
```
GET    /api/investments/analytics    - Investment analytics
GET    /api/investments/performance  - Performance tracking
GET    /api/investments/asset-allocation - Asset allocation
GET    /api/investments/risk-assessment - Risk assessment
GET    /api/investments/rebalancing-recommendations - Rebalancing suggestions
```

## 🔧 **Database Schema:**
```sql
-- Investment Tables
CREATE TABLE investments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    investment_type VARCHAR(50) NOT NULL,
    symbol VARCHAR(20),
    shares DECIMAL(15,6),
    purchase_price DECIMAL(15,2),
    current_price DECIMAL(15,2),
    purchase_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE portfolios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    target_allocation JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE portfolio_holdings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    portfolio_id BIGINT NOT NULL,
    investment_id BIGINT NOT NULL,
    target_percentage DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (portfolio_id) REFERENCES portfolios(id),
    FOREIGN KEY (investment_id) REFERENCES investments(id)
);

CREATE TABLE monte_carlo_simulations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    simulation_type VARCHAR(50) NOT NULL,
    parameters JSON,
    results JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE investment_performance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    investment_id BIGINT NOT NULL,
    date DATE NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    volume BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (investment_id) REFERENCES investments(id)
);
```

## 🎯 **Features to Implement:**

### **Investment Management:**
- [ ] **Investment Tracking**: Add, edit, delete investments
- [ ] **Price Updates**: Manual and automatic price updates
- [ ] **Investment Types**: Stocks, bonds, ETFs, mutual funds, etc.
- [ ] **Performance Tracking**: Historical performance data
- [ ] **Dividend Tracking**: Dividend payments and yields

### **Portfolio Management:**
- [ ] **Portfolio Creation**: Create and manage portfolios
- [ ] **Asset Allocation**: Target and actual allocation tracking
- [ ] **Portfolio Holdings**: Add/remove investments from portfolios
- [ ] **Portfolio Performance**: Overall portfolio performance
- [ ] **Rebalancing**: Automatic rebalancing recommendations

### **Monte Carlo Simulations:**
- [ ] **Retirement Planning**: Monte Carlo for retirement scenarios
- [ ] **Portfolio Optimization**: Optimal asset allocation
- [ ] **Risk Assessment**: Portfolio risk analysis
- [ ] **Scenario Analysis**: Different market scenarios
- [ ] **Confidence Intervals**: Success probability calculations

### **Investment Analytics:**
- [ ] **Performance Metrics**: Returns, volatility, Sharpe ratio
- [ ] **Risk Metrics**: Beta, alpha, maximum drawdown
- [ ] **Asset Allocation Analysis**: Current vs target allocation
- [ ] **Correlation Analysis**: Investment correlations
- [ ] **Benchmark Comparison**: Compare to market indices

### **Advanced Features:**
- [ ] **Market Data Integration**: Real-time market data
- [ ] **Tax Optimization**: Tax-loss harvesting suggestions
- [ ] **Dollar-Cost Averaging**: DCA strategy implementation
- [ ] **Dividend Reinvestment**: DRIP calculations
- [ ] **Portfolio Stress Testing**: Stress test scenarios

## 🔧 **Technical Requirements:**

### **Dependencies to Add:**
```gradle
// For Monte Carlo simulations
implementation 'org.apache.commons:commons-math3:3.6.1'

// For statistical calculations
implementation 'org.apache.commons:commons-lang3:3.12.0'

// For JSON processing
implementation 'com.fasterxml.jackson.core:jackson-databind'

// For financial calculations
implementation 'org.apache.commons:commons-math:3.6.1'
```

### **Monte Carlo Engine:**
- [ ] Implement random number generation
- [ ] Add probability distributions (normal, log-normal)
- [ ] Create simulation scenarios
- [ ] Build result analysis algorithms

## 📈 **Success Criteria:**
- [ ] All investment CRUD operations work correctly
- [ ] Portfolio management functions properly
- [ ] Monte Carlo simulations are accurate
- [ ] Performance calculations are correct
- [ ] 90%+ test coverage
- [ ] All endpoints documented in Swagger
- [ ] Integration with existing user authentication

## 🚀 **Acceptance Criteria:**
1. User can add a stock investment with symbol, shares, and purchase price
2. Portfolio shows current asset allocation vs target
3. Monte Carlo simulation runs for retirement planning
4. Investment performance shows historical returns
5. Risk assessment provides meaningful metrics
6. Rebalancing recommendations are accurate

## 📅 **Timeline:**
- **Week 6**: Models, DTOs, basic investment and portfolio services
- **Week 7**: Monte Carlo simulation engine and investment analytics
- **Week 8**: Controllers, advanced features, and comprehensive testing

---
**Dependencies:** Phase 1 (Debt Management), Phase 2 (Export), and Phase 3 (Analytics) completed
**Blockers:** None
**Estimated Story Points:** 25 