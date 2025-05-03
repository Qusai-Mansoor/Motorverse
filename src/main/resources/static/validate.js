// Email Validation
function validateEmail(email) {
    const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!email || !emailPattern.test(email)) {
        return {
            isValid: false,
            message: "Please enter a valid email address"
        };
    }
    return { isValid: true };
}

// Password Validation (at least 8 characters, 1 uppercase, 1 lowercase, 1 number)
function validatePassword(password) {
    if (!password || password.length < 8) {
        return {
            isValid: false,
            message: "Password must be at least 8 characters long"
        };
    }
    
    const hasUpperCase = /[A-Z]/.test(password);
    const hasLowerCase = /[a-z]/.test(password);
    const hasNumber = /\d/.test(password);
    
    if (!hasUpperCase || !hasLowerCase || !hasNumber) {
        return {
            isValid: false,
            message: "Password must contain at least one uppercase letter, one lowercase letter, and one number"
        };
    }
    
    return { isValid: true };
}

// Date of Birth Validation (must be at least 18 years old and not in future)
function validateDateOfBirth(dob) {
    const dobDate = new Date(dob);
    const today = new Date();
    const minDate = new Date(today.getFullYear() - 100, today.getMonth(), today.getDate()); // 100 years ago
    const maxDate = new Date(today.getFullYear() - 18, today.getMonth(), today.getDate()); // 18 years ago
    
    if (isNaN(dobDate.getTime())) {
        return {
            isValid: false,
            message: "Please enter a valid date"
        };
    }
    
    if (dobDate > today) {
        return {
            isValid: false,
            message: "Date of birth cannot be in the future"
        };
    }
    
    if (dobDate > maxDate) {
        return {
            isValid: false,
            message: "You must be at least 18 years old"
        };
    }
    
    if (dobDate < minDate) {
        return {
            isValid: false,
            message: "Please enter a valid date of birth"
        };
    }
    
    return { isValid: true };
}

// Phone Number Validation (accepts multiple formats)
function validatePhoneNumber(phone) {
    // Remove all non-numeric characters
    const cleanPhone = phone.replace(/\D/g, '');
    
    // Check for valid length (10-15 digits)
    if (cleanPhone.length < 10 || cleanPhone.length > 15) {
        return {
            isValid: false,
            message: "Phone number must be between 10 and 15 digits"
        };
    }
    
    return { isValid: true };
}

// Name Validation (letters, spaces, hyphens, apostrophes only)
function validateName(name) {
    const namePattern = /^[a-zA-Z\s'-]+$/;
    if (!name || !namePattern.test(name)) {
        return {
            isValid: false,
            message: "Please enter a valid name (letters, spaces, hyphens, and apostrophes only)"
        };
    }
    
    if (name.length < 2 || name.length > 50) {
        return {
            isValid: false,
            message: "Name must be between 2 and 50 characters"
        };
    }
    
    return { isValid: true };
}

// Price Validation (positive number, max 2 decimal places)
function validatePrice(price) {
    const pricePattern = /^\d+(\.\d{1,2})?$/;
    const numPrice = parseFloat(price);
    
    if (isNaN(numPrice) || !pricePattern.test(price)) {
        return {
            isValid: false,
            message: "Please enter a valid price (max 2 decimal places)"
        };
    }
    
    if (numPrice < 0) {
        return {
            isValid: false,
            message: "Price cannot be negative"
        };
    }
    
    return { isValid: true };
}

// Quantity Validation (positive integer)
function validateQuantity(quantity) {
    const numQuantity = parseInt(quantity);
    
    if (isNaN(numQuantity) || numQuantity < 0) {
        return {
            isValid: false,
            message: "Please enter a valid quantity"
        };
    }
    
    return { isValid: true };
}

// Form Validation Helper (validates multiple fields at once)
function validateForm(fields) {
    const errors = {};
    let isValid = true;
    
    fields.forEach(field => {
        let validation;
        
        switch(field.type) {
            case 'email':
                validation = validateEmail(field.value);
                break;
            case 'password':
                validation = validatePassword(field.value);
                break;
            case 'dob':
                validation = validateDateOfBirth(field.value);
                break;
            case 'phone':
                validation = validatePhoneNumber(field.value);
                break;
            case 'name':
                validation = validateName(field.value);
                break;
            case 'price':
                validation = validatePrice(field.value);
                break;
            case 'quantity':
                validation = validateQuantity(field.value);
                break;
        }
        
        if (!validation.isValid) {
            errors[field.name] = validation.message;
            isValid = false;
        }
    });
    
    return { isValid, errors };
}

// Export all validation functions
export {
    validateEmail,
    validatePassword,
    validateDateOfBirth,
    validatePhoneNumber,
    validateName,
    validatePrice,
    validateQuantity,
    validateForm
};
