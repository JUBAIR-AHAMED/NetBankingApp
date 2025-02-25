document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('createEmployeeForm');

    // Validation patterns for full-field validation (used on form submission)
    const patterns = {
        name: /^[A-Za-z. ]+$/, 
        password: /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@#$%^&+=!]).{8,}$/, 
        email: /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/, 
        mobile: /^[6-9][0-9]{9}$/, 
        branchId: /^\d{1,5}$/
    };

    // Validation messages
    const messages = {
        name: "Name must contain only alphabets and dots (.)",
        password: "Password must be at least 8 characters long, include at least one uppercase letter, one lowercase letter, one digit, and one special character (@#$%^&+=!).",
        email: "Invalid email format. Please provide a valid email address.",
        mobile: "Mobile number must be a 10-digit number starting with 6, 7, 8, or 9.",
        branchId: "Branch ID must be within 5 digits and contain only numeric characters."
    };

    /* --- Input Restriction Helper Functions --- */

    // For keypress events: block any keystroke that is not allowed by the provided regex.
    function restrictInput(event, allowedRegex) {
        // Allow control keys (backspace, tab, arrow keys, etc.)
        if (event.ctrlKey || event.metaKey || event.altKey || event.key.length > 1) return;
        const char = event.key;
        if (!allowedRegex.test(char)) {
            event.preventDefault();
        }
    }

    // For paste events: filter pasted text to only allowed characters.
    function filterPaste(event, allowedRegex) {
        event.preventDefault();
        const pasteData = (event.clipboardData || window.clipboardData).getData('text');
        // Keep only allowed characters from the pasted text.
        const filtered = pasteData.split('').filter(char => allowedRegex.test(char)).join('');
        // Insert the filtered text at the current cursor position.
        document.execCommand('insertText', false, filtered);
    }

    /* --- Attach Input Restrictions to Fields --- */

    // Name: only letters, dot, and space
    const nameField = document.getElementById('name');
    nameField.addEventListener('keypress', (e) => restrictInput(e, /[A-Za-z. ]/));
    nameField.addEventListener('paste', (e) => filterPaste(e, /[A-Za-z. ]/));

    // Password: allow only letters, digits, and these special characters: @#$%^&+=!
    const passwordField = document.getElementById('password');
    passwordField.addEventListener('keypress', (e) => restrictInput(e, /[A-Za-z0-9@#$%^&+=!]/));
    passwordField.addEventListener('paste', (e) => filterPaste(e, /[A-Za-z0-9@#$%^&+=!]/));

    // Email: allow letters, digits, and . _ % + - and @
    const emailField = document.getElementById('email');
    emailField.addEventListener('keypress', (e) => restrictInput(e, /[A-Za-z0-9._%+\-@]/));
    emailField.addEventListener('paste', (e) => filterPaste(e, /[A-Za-z0-9._%+\-@]/));

    // Mobile: first character must be 6-9; subsequent characters: digits 0-9.
    const mobileField = document.getElementById('mobile');
    mobileField.addEventListener('keypress', function(e) {
        // Allow control keys.
        if (e.ctrlKey || e.metaKey || e.altKey || e.key.length > 1) return;
        const char = e.key;
        // If field is empty, allow only 6-9.
        if (this.value.length === 0) {
            if (!/[6-9]/.test(char)) {
                e.preventDefault();
            }
        } else {
            if (!/[0-9]/.test(char)) {
                e.preventDefault();
            }
        }
    });
    mobileField.addEventListener('paste', function(e) {
        e.preventDefault();
        const pasteData = (e.clipboardData || window.clipboardData).getData('text');
        // Filter the pasted text character-by-character.
        let filtered = '';
        for (let i = 0; i < pasteData.length; i++) {
            const char = pasteData[i];
            // If the field is empty and it's the first character, only allow 6-9.
            if (this.value.length === 0 && filtered.length === 0) {
                if (/[6-9]/.test(char)) {
                    filtered += char;
                }
            } else {
                if (/[0-9]/.test(char)) {
                    filtered += char;
                }
            }
        }
        document.execCommand('insertText', false, filtered);
    });

    // Branch ID: allow only digits.
    const branchField = document.getElementById('branchId');
    branchField.addEventListener('keypress', (e) => restrictInput(e, /[0-9]/));
    branchField.addEventListener('paste', (e) => filterPaste(e, /[0-9]/));

    /* --- Age Validation for Date of Birth --- */
    const dobField = document.getElementById('dob');
    dobField.addEventListener('input', function() {
        if (this.value) {
            const dobDate = new Date(this.value);
            const today = new Date();
            let age = today.getFullYear() - dobDate.getFullYear();
            const monthDiff = today.getMonth() - dobDate.getMonth();
            if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < dobDate.getDate())) {
                age--;
            }
            if (age < 18) {
                this.setCustomValidity("Age must be at least 18 years.");
                this.reportValidity();
            } else {
                this.setCustomValidity("");
            }
        } else {
            // If no date is selected, clear the custom validity.
            this.setCustomValidity("");
        }
    });

    /* --- Standard Real-Time Field Validation --- */

    // Validate each field on input (this provides validation feedback using setCustomValidity)
    document.querySelectorAll('input').forEach(input => {
        input.addEventListener('input', function() {
            validateField(this);
        });
    });

    function validateField(field) {
        const pattern = patterns[field.name];
        if (pattern) {
            if (field.value && !pattern.test(field.value)) {
                field.setCustomValidity(messages[field.name]);
                // Optionally show the message immediately:
                field.reportValidity();
            } else {
                field.setCustomValidity('');
            }
        }
        // The 'dob' field is validated separately by its own listener.
    }

    /* --- Form Submission --- */

    form.addEventListener('submit', async function(event) {
        event.preventDefault();

        // Final validation check
        let isValid = true;
        document.querySelectorAll('input').forEach(input => {
            validateField(input);
            if (!input.checkValidity()) {
                isValid = false;
            }
        });

        if (!isValid) return;

        const formData = {
            name: nameField.value,
            password: passwordField.value,
            email: emailField.value,
            mobile: mobileField.value,
            dateOfBirth: dobField.value,
            branchId: branchField.value || null,
            role: document.getElementById('role').value,
            status: document.getElementById('status').value
        };

        try {
            const token = sessionStorage.getItem('jwt');
            const response = await fetch('http://localhost:8080/NetBanking/api/employee', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(formData)
            });

            const result = await response.json();

            if (result.status == 200) {
                showNotification('Employee created successfully!');
                showNotification('Employee Id is ' + result.employeeId);
                form.reset();
            } else {
                showNotification(result.message || 'Failed to create employee.', 'warning');
            }
        } catch (error) {
            console.error('Error:', error);
            showNotification('An error occurred while creating the employee.', 'error');
        }
    });

    /* --- Additional Select Field Styling (existing code) --- */

    const roleElement = document.getElementById('role');
    roleElement.addEventListener('change', function() {
        if (this.value) {
            this.classList.add('selected');
        } else {
            this.classList.remove('selected');
        }
    });

    const statusElement = document.getElementById('status');
    statusElement.addEventListener('change', function() {
        if (this.value) {
            this.classList.add('selected');
        } else {
            this.classList.remove('selected');
        }
    });
});

/* --- Notification Function (existing code) --- */
function showNotification(message, type = "success", duration = 5000) {
    // Icons for each type
    const icons = {
        success: "✅",
        error: "❌",
        warning: "⚠️",
        info: "ℹ️",
    };

    // Create the notification element
    const notification = document.createElement("div");
    notification.classList.add("notification", type);

    // Add icon and message
    notification.innerHTML = `
        <span class="icon">${icons[type] || ""}</span>
        <span class="message">${message}</span>
    `;

    // Get the notification container
    const container = document.getElementById("notification-container");
    if (!container) {
        console.error("Notification container not found.");
        return;
    }

    // Add the notification to the container
    container.appendChild(notification);

    // Automatically remove the notification after the specified duration
    setTimeout(() => {
        notification.style.opacity = "0";
        notification.style.transform = "translateX(100%)";
        setTimeout(() => notification.remove(), 400); // Wait for animation to complete
    }, duration);
}
