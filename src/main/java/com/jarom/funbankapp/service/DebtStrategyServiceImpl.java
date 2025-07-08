package com.jarom.funbankapp.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jarom.funbankapp.dto.DebtStrategyDTO;
import com.jarom.funbankapp.exception.ResourceNotFoundException;
import com.jarom.funbankapp.exception.UnauthorizedException;
import com.jarom.funbankapp.model.Debt;
import com.jarom.funbankapp.model.DebtStrategy;
import com.jarom.funbankapp.model.User;
import com.jarom.funbankapp.repository.DebtRepository;
import com.jarom.funbankapp.repository.DebtStrategyRepository;
import com.jarom.funbankapp.repository.UserRepository;

@Service
public class DebtStrategyServiceImpl implements DebtStrategyService {

    private final DebtStrategyRepository debtStrategyRepository;
    private final DebtRepository debtRepository;
    private final UserRepository userRepository;

    public DebtStrategyServiceImpl(DebtStrategyRepository debtStrategyRepository, DebtRepository debtRepository, UserRepository userRepository) {
        this.debtStrategyRepository = debtStrategyRepository;
        this.debtRepository = debtRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public DebtStrategyDTO createStrategy(DebtStrategyDTO strategyDTO) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Validate strategy data
        validateStrategyData(strategyDTO);
        
        // Create strategy model
        DebtStrategy strategy = new DebtStrategy();
        strategy.setUserId(user.getId());
        strategy.setName(strategyDTO.getName());
        strategy.setStrategyType(strategyDTO.getStrategyType());
        strategy.setDescription(strategyDTO.getDescription());
        strategy.setMonthlyPaymentAmount(strategyDTO.getMonthlyPaymentAmount() != null ? 
            strategyDTO.getMonthlyPaymentAmount().toString() : null);
        strategy.setIsActive(strategyDTO.getIsActive() != null ? strategyDTO.getIsActive() : false);
        strategy.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        strategy.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Save to database
        DebtStrategy savedStrategy = debtStrategyRepository.save(strategy);
        
        return convertToDTO(savedStrategy);
    }

    @Override
    public List<DebtStrategyDTO> getUserStrategies() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<DebtStrategy> strategies = debtStrategyRepository.findByUserId(user.getId());
        return strategies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DebtStrategyDTO getStrategyById(Long strategyId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        DebtStrategy strategy = debtStrategyRepository.findById(strategyId)
                .orElseThrow(() -> new ResourceNotFoundException("DebtStrategy", "id", strategyId));
        
        if (!strategy.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this strategy");
        }
        
        return convertToDTO(strategy);
    }

    @Override
    @Transactional
    public DebtStrategyDTO updateStrategy(Long strategyId, DebtStrategyDTO strategyDTO) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        DebtStrategy strategy = debtStrategyRepository.findById(strategyId)
                .orElseThrow(() -> new ResourceNotFoundException("DebtStrategy", "id", strategyId));
        
        if (!strategy.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this strategy");
        }
        
        // Validate strategy data
        validateStrategyData(strategyDTO);
        
        // Update fields
        if (strategyDTO.getName() != null) {
            strategy.setName(strategyDTO.getName());
        }
        if (strategyDTO.getStrategyType() != null) {
            strategy.setStrategyType(strategyDTO.getStrategyType());
        }
        if (strategyDTO.getDescription() != null) {
            strategy.setDescription(strategyDTO.getDescription());
        }
        if (strategyDTO.getMonthlyPaymentAmount() != null) {
            strategy.setMonthlyPaymentAmount(strategyDTO.getMonthlyPaymentAmount().toString());
        }
        
        strategy.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        // Save to database
        DebtStrategy updatedStrategy = debtStrategyRepository.save(strategy);
        
        return convertToDTO(updatedStrategy);
    }

    @Override
    @Transactional
    public void deleteStrategy(Long strategyId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        DebtStrategy strategy = debtStrategyRepository.findById(strategyId)
                .orElseThrow(() -> new ResourceNotFoundException("DebtStrategy", "id", strategyId));
        
        if (!strategy.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this strategy");
        }
        
        debtStrategyRepository.deleteById(strategyId);
    }

    @Override
    @Transactional
    public DebtStrategyDTO activateStrategy(Long strategyId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        DebtStrategy strategy = debtStrategyRepository.findById(strategyId)
                .orElseThrow(() -> new ResourceNotFoundException("DebtStrategy", "id", strategyId));
        
        if (!strategy.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this strategy");
        }
        
        // Deactivate all other strategies for this user
        List<DebtStrategy> userStrategies = debtStrategyRepository.findByUserId(user.getId());
        for (DebtStrategy userStrategy : userStrategies) {
            if (!userStrategy.getId().equals(strategyId)) {
                userStrategy.setIsActive(false);
                userStrategy.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                debtStrategyRepository.save(userStrategy);
            }
        }
        
        // Activate the selected strategy
        strategy.setIsActive(true);
        strategy.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        DebtStrategy activatedStrategy = debtStrategyRepository.save(strategy);
        
        return convertToDTO(activatedStrategy);
    }

    @Override
    public DebtStrategyDTO getActiveStrategy() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        List<DebtStrategy> strategies = debtStrategyRepository.findByUserId(user.getId());
        return strategies.stream()
                .filter(DebtStrategy::getIsActive)
                .findFirst()
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public Map<String, Object> generateSnowballStrategy() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        List<Debt> debts = debtRepository.findByUserId(user.getId());
        // Filter out debts with nulls in critical fields
        List<Debt> validDebts = debts.stream()
            .filter(d -> d.getCurrentBalance() != null && d.getInterestRate() != null && d.getMinimumPayment() != null && d.getPriority() != null)
            .toList();
        if (validDebts.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "No valid debts to generate snowball strategy.");
            return result;
        }
        // Sort by balance (lowest first) for snowball method
        List<Debt> sortedDebts = validDebts.stream()
                .filter(debt -> debt.getCurrentBalance() != null && debt.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(Debt::getCurrentBalance))
                .collect(Collectors.toList());
        Map<String, Object> strategy = new HashMap<>();
        strategy.put("strategyType", "snowball");
        strategy.put("description", "Pay off debts starting with the smallest balance first");
        strategy.put("debtOrder", sortedDebts.stream().map(Debt::getId).collect(Collectors.toList()));
        strategy.put("totalDebts", sortedDebts.size());
        strategy.put("totalBalance", sortedDebts.stream()
                .map(Debt::getCurrentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return strategy;
    }

    @Override
    public Map<String, Object> generateAvalancheStrategy() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        List<Debt> debts = debtRepository.findByUserId(user.getId());
        // Filter out debts with nulls in critical fields
        List<Debt> validDebts = debts.stream()
            .filter(d -> d.getCurrentBalance() != null && d.getInterestRate() != null && d.getMinimumPayment() != null && d.getPriority() != null)
            .toList();
        if (validDebts.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "No valid debts to generate avalanche strategy.");
            return result;
        }
        // Sort by interest rate (highest first) for avalanche method
        List<Debt> sortedDebts = validDebts.stream()
                .filter(debt -> debt.getCurrentBalance() != null && debt.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0 && debt.getInterestRate() != null)
                .sorted(Comparator.comparing(Debt::getInterestRate).reversed())
                .collect(Collectors.toList());
        Map<String, Object> strategy = new HashMap<>();
        strategy.put("strategyType", "avalanche");
        strategy.put("description", "Pay off debts starting with the highest interest rate first");
        strategy.put("debtOrder", sortedDebts.stream().map(Debt::getId).collect(Collectors.toList()));
        strategy.put("totalDebts", sortedDebts.size());
        strategy.put("totalBalance", sortedDebts.stream()
                .map(Debt::getCurrentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return strategy;
    }

    @Override
    public Map<String, Object> calculateStrategyEffectiveness(Long strategyId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        DebtStrategy strategy = debtStrategyRepository.findById(strategyId)
                .orElseThrow(() -> new ResourceNotFoundException("DebtStrategy", "id", strategyId));
        
        if (!strategy.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You don't own this strategy");
        }
        
        List<Debt> debts = debtRepository.findByUserId(user.getId());
        
        Map<String, Object> effectiveness = new HashMap<>();
        effectiveness.put("strategyId", strategyId);
        effectiveness.put("strategyName", strategy.getName());
        effectiveness.put("strategyType", strategy.getStrategyType());
        effectiveness.put("isActive", strategy.getIsActive());
        
        // Calculate effectiveness metrics
        BigDecimal totalDebt = debts.stream()
                .map(Debt::getCurrentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalMinimumPayments = debts.stream()
                .map(Debt::getMinimumPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        effectiveness.put("totalDebt", totalDebt);
        effectiveness.put("totalMinimumPayments", totalMinimumPayments);
        effectiveness.put("activeDebts", debts.stream()
                .filter(debt -> debt.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0)
                .count());
        
        return effectiveness;
    }

    @Override
    public Map<String, Object> compareStrategies() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        List<Debt> debts = debtRepository.findByUserId(user.getId());
        // Filter out debts with nulls in critical fields
        List<Debt> validDebts = debts.stream()
            .filter(d -> d.getCurrentBalance() != null && d.getInterestRate() != null && d.getMinimumPayment() != null && d.getPriority() != null)
            .toList();
        if (validDebts.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("message", "No valid debts to compare strategies.");
            return result;
        }
        // Use validDebts for strategy generation
        Map<String, Object> snowball = generateSnowballStrategy(validDebts);
        Map<String, Object> avalanche = generateAvalancheStrategy(validDebts);
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("snowball", snowball);
        comparison.put("avalanche", avalanche);
        return comparison;
    }

    // Overload strategy methods to accept debts list
    public Map<String, Object> generateSnowballStrategy(List<Debt> debts) {
        // Sort by balance (lowest first) for snowball method
        List<Debt> sortedDebts = debts.stream()
                .filter(debt -> debt.getCurrentBalance() != null && debt.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0)
                .sorted(Comparator.comparing(Debt::getCurrentBalance))
                .collect(Collectors.toList());
        Map<String, Object> strategy = new HashMap<>();
        strategy.put("strategyType", "snowball");
        strategy.put("description", "Pay off debts starting with the smallest balance first");
        strategy.put("debtOrder", sortedDebts.stream().map(Debt::getId).collect(Collectors.toList()));
        strategy.put("totalDebts", sortedDebts.size());
        strategy.put("totalBalance", sortedDebts.stream()
                .map(Debt::getCurrentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return strategy;
    }
    public Map<String, Object> generateAvalancheStrategy(List<Debt> debts) {
        // Sort by interest rate (highest first) for avalanche method
        List<Debt> sortedDebts = debts.stream()
                .filter(debt -> debt.getCurrentBalance() != null && debt.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0 && debt.getInterestRate() != null)
                .sorted(Comparator.comparing(Debt::getInterestRate).reversed())
                .collect(Collectors.toList());
        Map<String, Object> strategy = new HashMap<>();
        strategy.put("strategyType", "avalanche");
        strategy.put("description", "Pay off debts starting with the highest interest rate first");
        strategy.put("debtOrder", sortedDebts.stream().map(Debt::getId).collect(Collectors.toList()));
        strategy.put("totalDebts", sortedDebts.size());
        strategy.put("totalBalance", sortedDebts.stream()
                .map(Debt::getCurrentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return strategy;
    }

    @Override
    public boolean userOwnsStrategy(Long strategyId) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return debtStrategyRepository.findById(strategyId)
                .map(strategy -> strategy.getUserId().equals(user.getId()))
                .orElse(false);
    }

    // Helper methods
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    
    private void validateStrategyData(DebtStrategyDTO strategyDTO) {
        if (strategyDTO.getName() == null || strategyDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Strategy name is required");
        }
        if (strategyDTO.getStrategyType() == null || strategyDTO.getStrategyType().trim().isEmpty()) {
            throw new IllegalArgumentException("Strategy type is required");
        }
    }
    
    private DebtStrategyDTO convertToDTO(DebtStrategy strategy) {
        DebtStrategyDTO dto = new DebtStrategyDTO();
        dto.setId(strategy.getId());
        dto.setName(strategy.getName());
        dto.setStrategyType(strategy.getStrategyType());
        dto.setDescription(strategy.getDescription());
        dto.setIsActive(strategy.getIsActive());
        
        if (strategy.getMonthlyPaymentAmount() != null) {
            try {
                dto.setMonthlyPaymentAmount(new BigDecimal(strategy.getMonthlyPaymentAmount()));
            } catch (NumberFormatException e) {
                dto.setMonthlyPaymentAmount(BigDecimal.ZERO);
            }
        }
        
        if (strategy.getCreatedAt() != null) {
            dto.setCreatedAt(strategy.getCreatedAt().toLocalDateTime());
        }
        if (strategy.getUpdatedAt() != null) {
            dto.setUpdatedAt(strategy.getUpdatedAt().toLocalDateTime());
        }
        
        return dto;
    }
} 