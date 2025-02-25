function decodeJWT(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error('Invalid token', e);
        return null;
    }
}

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

document.addEventListener('DOMContentLoaded', async function () {
    const jwtToken = sessionStorage.getItem('jwt');
    const decodedToken = decodeJWT(jwtToken);
    const role = decodedToken ? decodedToken.role : null;

    const fromAccountInput = document.getElementById('fromAccountNumber');
    const fromAccountDropdown = document.getElementById('fromAccountDropdown');

    try{
        const token = sessionStorage.getItem('jwt');
        if (!token) {
            // alert('You must be logged in to view this page.');
            window.location.href = "/login.html";
            return;
        }
    } catch (error) {
        console.log(error)
    }

    const transactionTypeSelect = document.getElementById('transactionType');

    if (role === 'EMPLOYEE' || role === 'MANAGER') {
        const depositOption = document.createElement('option');
        depositOption.value = 'deposit';
        depositOption.textContent = 'Deposit';
        transactionTypeSelect.appendChild(depositOption);

        const withdrawOption = document.createElement('option');
        withdrawOption.value = 'withdraw';
        withdrawOption.textContent = 'Withdraw';
        transactionTypeSelect.appendChild(withdrawOption);
    }

    if (role === 'EMPLOYEE' || role === 'MANAGER') {
        const branchId = decodedToken.branchId;
        // Enable the input field for manual entry
        fromAccountInput.disabled = false;

        // Add event listener to fetch accounts dynamically as the user types
        fromAccountInput.addEventListener('input', async function () {
            const inputValue = fromAccountInput.value.trim();
            if (inputValue.length >= 3) { // Fetch matching accounts after 3+ characters
                try {
                    const criteria = { limit: 3, accountNumber: inputValue, searchSimilarFields: ["accountNumber"] };
                    if(role === 'EMPLOYEE'){
                        criteria.branchId = branchId;
                    }
                    const response = await fetch('api/account', {
                        method: 'POST',
                        headers: {
                            'Authorization': `Bearer ${jwtToken}`,
                            'action': 'GET'
                        },
                        body: JSON.stringify(criteria)
                    });

                    const data = await response.json();
                    if (data.status==200) {
                        if(Array.isArray(data.accounts)) {
                        displayAccountsDropdown(data.accounts, fromAccountDropdown);
                        } else {
                            fromAccountDropdown.innerHTML = '<li>No matching accounts found</li>';
                            fromAccountDropdown.style.display = 'block';
                        }
                    }
                } catch (error) {
                    console.error('Error fetching accounts:', error);
					showNotification("Error fetching accounts.", "error")
                }
            }
        });

        // Handle selecting an account from the dropdown
        fromAccountDropdown.addEventListener('click', function (event) {
            if (event.target.tagName === 'LI') {
                fromAccountInput.value = event.target.dataset.value;
                fromAccountDropdown.style.display = 'none'; // Hide the dropdown after selection
            }
        });
    } else if (role === 'CUSTOMER') {
        fromAccountInput.readOnly = true; // Disable manual typing for customers
    
        // Fetch and populate dropdown for customers on focus
        fromAccountInput.addEventListener('focus', async function () {
            try {
                const response = await fetch('api/account', {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${jwtToken}`,
                        'action': 'GET'
                    }
                });
                const data = await response.json();
    
                if (data.status==200) {
                    if(Array.isArray(data.accounts)) {
                        displayAccountsDropdown(data.accounts, fromAccountDropdown);
                    } else {
                        console.error('No accounts found or error in response');
                        fromAccountDropdown.innerHTML = ''; // Clear existing options
                        const noAccountsItem = document.createElement('li');
                        noAccountsItem.textContent = 'No accounts available';
                        noAccountsItem.style.color = 'gray'; // Optional styling
                        fromAccountDropdown.appendChild(noAccountsItem);
                        fromAccountDropdown.style.display = 'block';
                    }
                }
            } catch (error) {
				showNotification("Error fetching accounts.", "error")
                console.error('Error fetching accounts:', error);
            }
        });
    
        // Handle selecting an account from the dropdown
        fromAccountDropdown.addEventListener('click', function (event) {
            if (event.target.tagName === 'LI') {
                fromAccountInput.value = event.target.dataset.value;
                fromAccountDropdown.style.display = 'none'; // Hide the dropdown after selection
            }
        });
    
        // Hide the dropdown when clicking outside
        document.addEventListener('click', function (event) {
            if (!fromAccountDropdown.contains(event.target) && event.target !== fromAccountInput) {
                fromAccountDropdown.style.display = 'none';
            }
        });
    } else {
        fromAccountInput.disabled = true;
    }
    document.getElementById('transactionType').addEventListener('change', function(event) {
        const transactionType = event.target.value;
        console.log("Selected Transaction Type:", transactionType); // Debugging
        const otherBankFields = document.getElementById('otherBankFields');
        const toAccountNumberField = document.getElementById('toAccountNumber');
        const ifsc = document.getElementById('ifscCode');
        const bankName = document.getElementById('bankName');

        if (transactionType === 'other-bank') {
            console.log("Handling other-bank transaction");
            otherBankFields.classList.remove('hidden');
            ifsc.classList.add('required');
            bankName.classList.add('required');
            toAccountNumberField.classList.remove('hidden');
            toAccountNumberField.classList.add('required');
        } else if (transactionType === 'deposit' || transactionType === 'withdraw') {
            console.log("Handling deposit or withdraw transaction");
            otherBankFields.classList.add('hidden');
            toAccountNumberField.classList.remove('required');
            toAccountNumberField.classList.add('hidden');
            ifsc.classList.remove('required');
            bankName.classList.remove('required');
        } else if (transactionType === 'same-bank') {
            console.log("Handling same-bank transaction");
            otherBankFields.classList.add('hidden');
            toAccountNumberField.classList.add('required');
            toAccountNumberField.classList.remove('hidden');
            ifsc.classList.remove('required');
            bankName.classList.remove('required');
        } else {
            console.log("Unknown transaction type selected");
        }
    });

    
    
    document.getElementById("transactionForm").addEventListener("submit", async function (event) {
        event.preventDefault(); // Prevent default form submission behavior
    
        // Validation for required fields
        const requiredFields = document.querySelectorAll(".required");
        let isValid = true;
    
        requiredFields.forEach(field => {
            if (!field.value.trim()) {
                field.classList.add("error"); // Add error styling
                isValid = false;
            } else {
                field.classList.remove("error"); // Remove error styling if valid
            }
        });
    
        if (!isValid) {
            alert("Please fill out all required fields.");
            return; // Stop further execution if validation fails
        }
    
        // Proceed with form submission logic if validation passes
        const fromAccount = document.getElementById('fromAccountNumber').value || null;
        const toAccount = document.getElementById('toAccountNumber').value || null;
        const amount = document.getElementById('amount').value || null;
        let bankName = document.getElementById('bankName').value || null;
        const transactionType = document.getElementById('transactionType').value || null;

		if(transactionType!="other-bank"){
			bankName = "BOU";
		}
        try {
            const token = sessionStorage.getItem('jwt'); // Get the stored JWT token
    
            const response = await fetch('api/transaction', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ fromAccount, toAccount, amount, bankName, transactionType })
            });
    
            const result = await response.json();
            if (result.status === 200) {
				showNotification("Transaction Success.", "success")
                document.getElementById('fromAccountNumber').value = '';
	            document.getElementById('toAccountNumber').value = '';
	            document.getElementById('amount').value = '';
	            document.getElementById('bankName').value = '';
	            document.getElementById('ifscCode').value = ''; // Clear IFSC code as well
	            document.getElementById('transactionType').value = 'same-bank'; // Optionally reset transaction type to default or empty
	
	            // Optionally, you might want to reset dropdowns if you are using them differently.
	            // For example, if you are using the dropdown for account selection, you might want to clear the displayed dropdown as well.
	            const fromAccountDropdown = document.getElementById('fromAccountDropdown');
            fromAccountDropdown.style.display = 'none'; 
            } else {
                showNotification(result.message, "warning")
            }
        } catch (error) {
            console.error('Error during transaction:', error);
			showNotification("An error occurred during transaction. Please try again.", "error")
        }
    });
    
    
    function displayAccountsDropdown(accounts, dropdownElement) {
        dropdownElement.innerHTML = ''; // Clear existing options
        accounts.forEach(account => {
            const listItem = document.createElement('li');
            listItem.dataset.value = account.accountNumber;
            listItem.textContent = `${account.accountNumber} - ${account.status}`;
            dropdownElement.appendChild(listItem);
        });
        dropdownElement.style.display = 'block'; // Show the dropdown
    }
});