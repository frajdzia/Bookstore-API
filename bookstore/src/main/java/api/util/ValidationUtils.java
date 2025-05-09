package api.util;

import api.exception.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class ValidationUtils {

    private static final Logger logger = LoggerFactory.getLogger(ValidationUtils.class);

//    String fields check
    
    public static void validateStringField(String value, String fieldName, boolean allowNullFields) {
        if (value == null) {
            if (!allowNullFields) {
                logger.warn("{} cannot be null.", fieldName);
                throw new InvalidInputException(fieldName + " cannot be null");
            }
            return;
        }
        if (value.trim().isEmpty()) {
            logger.warn("{} cannot be empty.", fieldName);
            throw new InvalidInputException(fieldName + " cannot be empty");
        }
    }

//    Numeric fields check
    
    public static void validateNumericField(Number value, String fieldName, boolean allowNullFields) {
        if (value == null) {
            if (!allowNullFields) {
                logger.warn("{} cannot be null.", fieldName);
                throw new InvalidInputException(fieldName + " cannot be null");
            }
            return;
        }
        if (value.doubleValue() <= 0) {
            logger.warn("{} must be greater than 0.", fieldName);
            throw new InvalidInputException(fieldName + " must be greater than 0");
        }
    }

//    If in future check (years for book as example)
    
    public static void checkNotFuture(Integer year, String fieldName, int maxYear, boolean allowNullFields) {
        if (year == null) {
            if (!allowNullFields) {
                logger.warn("{} cannot be null.", fieldName);
                throw new InvalidInputException(fieldName + " cannot be null");
            }
            return;
        }
        if (year > maxYear) {
            logger.warn("{} cannot be in the future (after {}).", fieldName, maxYear);
            throw new InvalidInputException(fieldName + " cannot be in the future (after " + maxYear + ")");
        }
    }
    
//    Email format check

    public static void validateEmailFormat(String email, String fieldName, boolean allowNullFields) {
        validateStringField(email, fieldName, allowNullFields);
        if (email != null && !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            logger.warn("Invalid format for {}: {}", fieldName, email);
            throw new InvalidInputException(fieldName + " format is invalid (must contain '@' and a domain)");
        }
    }
    
//    Minimum length for password

    public static void checkMinLength(String value, String fieldName, int minLength, boolean allowNullFields) {
        validateStringField(value, fieldName, allowNullFields);
        if (value != null && value.length() < minLength) {
            logger.warn("{} must be at least {} characters long.", fieldName, minLength);
            throw new InvalidInputException(fieldName + " must be at least " + minLength + " characters long");
        }
    }

//    Validate a list (not null, not empty)
    
    public static <T> void validateList(List<T> list, String fieldName, boolean allowNullFields) {
        if (list == null) {
            if (!allowNullFields) {
                logger.warn("{} cannot be null.", fieldName);
                throw new InvalidInputException(fieldName + " cannot be null");
            }
            return;
        }
        if (list.isEmpty()) {
            logger.warn("{} cannot be empty.", fieldName);
            throw new InvalidInputException(fieldName + " cannot be empty");
        }
    }

}
