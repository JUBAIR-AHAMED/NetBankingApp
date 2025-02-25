document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('createBranchForm');

    // Patterns for validation
    const patterns = {
        name: /^[A-Za-z. ]+$/,
        ifsc: /^[A-Z]{4}0\d{6}$/,
        address: /^[A-Za-z0-9,.'\s\-\/]+$/,
        employeeId: /^\d{1,6}$/
    };

    // Custom error messages
    const messages = {
        name: "Branch Name must contain only alphabets, spaces, and dots.",
        ifsc: "IFSC Code must have 4 uppercase letters, followed by a 0, and 6 digits.",
        address: "Address can only contain letters, numbers, spaces, and the following punctuation: , . ' - /",
        employeeId: "Manager Employee ID must be 1 to 6 digits."
    };

    /* --- Input Restriction Helper Functions --- */

    // Blocks any key that is not allowed by the provided regex.
    function restrictInput(event, allowedRegex) {
        // Allow control keys (e.g., backspace, tab, arrow keys)
        if (event.ctrlKey || event.metaKey || event.altKey || event.key.length > 1) return;
        if (!allowedRegex.test(event.key)) {
            event.preventDefault();
        }
    }

    // Filters pasted text so that only allowed characters are inserted.
    function filterPaste(event, allowedRegex) {
        event.preventDefault();
        const pasteData = (event.clipboardData || window.clipboardData).getData('text');
        const filtered = pasteData.split('').filter(char => allowedRegex.test(char)).join('');
        document.execCommand('insertText', false, filtered);
    }

    /* --- Attach Input Restrictions --- */

    // Branch Name: allow only letters, dot, and space.
    const nameField = document.getElementById('name');
    nameField.addEventListener('keypress', (e) => restrictInput(e, /[A-Za-z. ]/));
    nameField.addEventListener('paste', (e) => filterPaste(e, /[A-Za-z. ]/));

    // IFSC Code: allow only letters and digits, limit input to 11 characters.
    const ifscField = document.getElementById('ifsc');
    ifscField.addEventListener('keypress', function(e) {
        // Calculate the available space considering any selected text.
        const selectionLength = this.selectionEnd - this.selectionStart;
        if (this.value.length - selectionLength >= 11) {
            e.preventDefault();
            return;
        }
        restrictInput(e, /[A-Za-z0-9]/);
    });
    ifscField.addEventListener('paste', function(e) {
        e.preventDefault();
        const pasteData = (e.clipboardData || window.clipboardData).getData('text');
        // Allow only letters and digits.
        const filtered = pasteData.split('').filter(char => /[A-Za-z0-9]/.test(char)).join('');
        const selectionLength = this.selectionEnd - this.selectionStart;
        const maxAllowed = 11 - (this.value.length - selectionLength);
        const finalText = filtered.substring(0, maxAllowed);
        document.execCommand('insertText', false, finalText);
    });
    ifscField.addEventListener('input', function() {
        // Force uppercase for IFSC code.
        this.value = this.value.toUpperCase();
    });

    // Address: allow letters, digits, spaces and punctuation: , . ' - /
    const addressField = document.getElementById('address');
    addressField.addEventListener('keypress', (e) => restrictInput(e, /[A-Za-z0-9,.'\s\-\/]/));
    addressField.addEventListener('paste', (e) => filterPaste(e, /[A-Za-z0-9,.'\s\-\/]/));

    // Employee ID: allow only digits, and do not allow more than 6 digits.
    const employeeIdField = document.getElementById('employeeId');
    employeeIdField.addEventListener('keypress', function(e) {
        const selectionLength = this.selectionEnd - this.selectionStart;
        if (this.value.length - selectionLength >= 6) {
            e.preventDefault();
            return;
        }
        restrictInput(e, /[0-9]/);
    });
    employeeIdField.addEventListener('paste', function(e) {
        e.preventDefault();
        const pasteData = (e.clipboardData || window.clipboardData).getData('text');
        const filtered = pasteData.split('').filter(char => /[0-9]/.test(char)).join('');
        const selectionLength = this.selectionEnd - this.selectionStart;
        const maxAllowed = 6 - (this.value.length - selectionLength);
        const finalText = filtered.substring(0, maxAllowed);
        document.execCommand('insertText', false, finalText);
    });

    /* --- Real-Time Field Validation --- */
    document.querySelectorAll('input, textarea').forEach(field => {
        field.addEventListener('input', function() {
            validateField(this);
        });
    });

    function validateField(field) {
        const pattern = patterns[field.name];
        if (pattern) {
            if (field.value && !pattern.test(field.value)) {
                field.setCustomValidity(messages[field.name]);
                field.reportValidity();
            } else {
                field.setCustomValidity('');
            }
        }
    }

    /* --- Form Submission --- */
    form.addEventListener('submit', async function(event) {
        event.preventDefault();

        // Final validation check for all inputs.
        let isValid = true;
        document.querySelectorAll('input, textarea').forEach(field => {
            validateField(field);
            if (!field.checkValidity()) {
                isValid = false;
            }
        });
        if (!isValid) return;

        const formData = {
            name: nameField.value,
            ifsc: ifscField.value,
            address: addressField.value,
            employeeId: employeeIdField.value
        };

        try {
            const token = sessionStorage.getItem('jwt');
            const response = await fetch('api/branch', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(formData)
            });

            const result = await response.json();
            if (result.status === 200) {
                showNotification('Branch created successfully!');
                form.reset();
            } else {
                showNotification(result.message || 'Failed to create branch.', 'warning');
            }
        } catch (error) {
            showNotification('An error occurred while creating the branch.', 'error');
        }
    });
});

/* --- Notification Function --- */
function showNotification(message, type = "success", duration = 5000) {
    const icons = {
        success: "✅",
        error: "❌",
        warning: "⚠️",
        info: "ℹ️",
    };

    const notification = document.createElement("div");
    notification.classList.add("notification", type);
    notification.innerHTML = `
        <span class="icon">${icons[type] || ""}</span>
        <span class="message">${message}</span>
    `;

    const container = document.getElementById("notification-container");
    if (!container) {
        console.error("Notification container not found.");
        return;
    }
    container.appendChild(notification);
    setTimeout(() => {
        notification.style.opacity = "0";
        notification.style.transform = "translateX(100%)";
        setTimeout(() => notification.remove(), 400);
    }, duration);
}
