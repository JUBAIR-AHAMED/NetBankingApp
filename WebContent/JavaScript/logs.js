let tokenBranchId = null;
let tokenRole = null;
// // Function to decode JWT token and extract role and branchId
function decodeJWT(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload); // Return the decoded token payload
    } catch (e) {
        console.error('Invalid token', e);
        return null;
    }
}

function millisToDate(millis, format = "locale") {  // Added a 'format' parameter
  const date = new Date(millis);

  if (isNaN(date)) { // Check for invalid date (important!)
    return "Invalid Date"; // Or throw an error, or return null, as you prefer
  }

  switch (format) {
    case "locale":
      return date.toLocaleDateString(); // Default: locale-specific
    case "detailed":
      return date.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
    case "iso":
      return date.toISOString();
    case "ymd": // Year-Month-Day (YYYY-MM-DD)
      const year = date.getFullYear();
      const month = date.getMonth() + 1;
      const day = date.getDate();
      return `${year}-${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day}`;
    // Add more cases as needed (e.g., "mdy", "dmy", etc.)
    default:
      return date.toLocaleDateString(); // Default if format is not recognized
  }
}


function setupNumericInputValidation(inputElement, maxLength) {
    // Prevent non-numeric input during typing
    inputElement.addEventListener('keypress', function (event) {
        const char = String.fromCharCode(event.which || event.keyCode);
        if (!/^\d$/.test(char)) {
            event.preventDefault(); // Block non-numeric characters
        }
    });

    // Ensure no invalid characters or excess digits after paste or programmatic change
    inputElement.addEventListener('input', function (event) {
        let value = event.target.value;

        // Remove non-numeric characters
        value = value.replace(/\D/g, '');

        // Enforce maximum length
        if (value.length > maxLength) {
            value = value.slice(0, maxLength);
        }

        // Update the input value
        event.target.value = value;
    });
}

setupNumericInputValidation(document.getElementById('actorId'), 16);
setupNumericInputValidation(document.getElementById('userId'), 6);
setupNumericInputValidation(document.getElementById('branchId'), 5);


function showAccountDetails(account) {
    const modal = document.getElementById("accountModal");
    const modalContent = document.getElementById("modalContent");
    modal.dataset.account = JSON.stringify(account);

    // Check if branchId is editable
    const isBranchEditable = tokenRole == "MANAGER" || account.branchId === tokenBranchId;

    // Format account details
    modalContent.innerHTML = `
abc
    `;

    // Show the modal
    modal.style.display = "block";
    setupBranchIdDropdown(); // Setup dropdown after modal is shown
}

function closeModal() {
    const modal = document.getElementById("accountModal");
    modal.style.display = "none";
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


function toggleEdit(fieldId) {
    const field = document.getElementById(fieldId);

    // Check if field is a span and toggle to input
    if (field && field.tagName === 'SPAN') {
        const currentText = field.innerText;

        // Create an input field
        let input;
        if (fieldId === 'statusField') {
            // ... (statusField input creation - no change) ...
            input = document.createElement('select');
            input.id = fieldId;
            input.classList.add('editable-input');
            const options = ['ACTIVE', 'BLOCKED', 'INACTIVE'];
            options.forEach(option => {
                const optionElement = document.createElement('option');
                optionElement.value = option;
                optionElement.text = option;
                if (option === currentText) {
                    optionElement.selected = true;
                }
                input.appendChild(optionElement)
            })
        } else if (fieldId === 'branchIdField') {
            input = document.createElement('input');
            input.type = 'text';
            input.value = currentText;
            input.classList.add('editable-input');
            input.id = fieldId;
            input.dataset.originalValue = currentText; // Store original value
            setupBranchIdDropdownInput(input); // Setup dropdown input behavior
            isBranchIdFieldEditing = true; // Set editing flag to true
        }
        else {
            input = document.createElement('input');
            input = document.createElement('input');
            input.type = 'text';
            input.value = currentText;
            input.classList.add('editable-input');
            input.id = fieldId;
        }


        // Replace span with input field
        field.replaceWith(input);

        // Focus the input for immediate editing
        input.focus();

        // Add blur and keypress listeners for non-branchIdField inputs and delayed blur for branchIdField
        if (fieldId !== 'branchIdField') {
            input.addEventListener('blur', () => {
                if (document.getElementById(fieldId)) {
                    saveEdit(fieldId, input.value);
                }
            });

            input.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    if (document.getElementById(fieldId)) {
                        saveEdit(fieldId, input.value);
                    }
                }
            });
        } 
	
    }
}
function saveEdit(fieldId, newValue) {
    // Create a span to replace input
    const span = document.createElement('span');
    span.id = fieldId;
    span.innerText = newValue;
    const input = document.getElementById(fieldId);
    if (input) {
        input.replaceWith(span);
    }
}

function showBranchDetails(branch) {
    console.log(branch)
    const modal = document.getElementById("branchModal");
    const modalContent = document.getElementById("branchModalContent");

    // Format branch details
    modalContent.innerHTML = `
        <div class="info-row">
            <label>Branch ID</label>
            <div class="non-editable-field">
                <span>${branch[0].branchId}</span>
            </div>
        </div>
        <div class="info-row">
            <label>Branch Name</label>
            <div class="non-editable-field">
                <span>${branch[0].name}</span>
            </div>
        </div>
        <div class="info-row">
            <label>Address</label>
            <div class="non-editable-field">
                <span>${branch[0].address}</span>
            </div>
        </div>
        <div class="info-row">
            <label>IFSC</label>
            <div class="non-editable-field">
                <span>${branch[0].ifsc}</span>
            </div>
        </div>
        <div class="info-row">
            <label>Manager ID</label>
            <div class="non-editable-field">
                <span>${branch[0].employeeId}</span>
            </div>
        </div>
        <div class="modal-actions">
            <span class="close-button" onclick="closeBranchModal()"></span>
        </div>
    `;

    // Show the modal
    modal.style.display = "block";
}

function closeBranchModal() {
    const modal = document.getElementById("branchModal");
    modal.style.display = "none";
}

function createAccountCard(account) {
    const {
        actorId,
        subjectId,
		recordname,
		action,
		keyValue,
		actionTime
    } = account;

    // Get the JWT token and decode it to get role and branchId
    const token = sessionStorage.getItem('jwt');
    const decodedToken = decodeJWT(token);

    const userRole = decodedToken?.role;  // Extract user role (e.g., 'MANAGER' or 'EMPLOYEE')
    const userBranchId = decodedToken?.branchId;  // Extract user branchId

    // Determine if the "Actions" section should be displayed
    let actionsContent = '';

    return `
        <div class="valueColumn">
            <div class="columnValues accValue">
                ${actorId}
                <div class="more" style="position: fixed; z-index: 2; width: 17%; align-self: center; cursor: pointer; justify-items: flex-end;" data-account='${JSON.stringify(account)}'>
                    <img class="eye-logo-acc more" src="icons/eye-svgrepo-com.svg" alt="view icon" data-account='${JSON.stringify(account)}'>
                </div>
            </div>
            <div class="columnValues">${subjectId}</div>
            <div class="columnValues branchValue">
                ${recordname}
            </div>
            <div class="columnValues" style="display: flex;">${keyValue}
            </div>
            <div class="columnValues" style="display: flex;">${action}
            </div>
            <div class="columnValues" style="display: flex;">${millisToDate(actionTime)}
            </div>
        </div>
    `;
}


function viewDetails(accountNumber) {
    console.log(accountNumber)
}

function formatAccountNumber(accountNumber) {
    accountNumber = String(accountNumber);
    return accountNumber.replace(/(.{4})/g, '$1 ').trim();
}

function capitalizeFirstLetter(str) {
    if (!str) return str; // Return if the string is empty
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

function formatDate(timestamp) {
    const date = new Date(timestamp);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are zero-indexed
    const year = date.getFullYear();

    return `${day}.${month}.${year}`;
}

function formatIndianCurrency(amount) {
    const isNegative = amount < 0;

    const absoluteAmount = Math.abs(amount);

    const [integerPart, decimalPart = ''] = absoluteAmount.toString().split('.');

    const formattedDecimalPart = decimalPart.padEnd(2, '0').slice(0, 2);
    const lastThreeDigits = integerPart.slice(-3);
    const otherDigits = integerPart.slice(0, -3);
    const formattedIntegerPart = otherDigits.replace(/\B(?=(\d{2})+(?!\d))/g, ',') + (otherDigits ? ',' : '') + lastThreeDigits;

    const formattedAmount = `${formattedIntegerPart}.${formattedDecimalPart}`;

    return formattedAmount;
}

function displayBranchDropdown(branchs, dropdownElement) {
    dropdownElement.innerHTML = ''; // Clear existing options
    branchs.forEach(branch => {
        const listItem = document.createElement('li');
        listItem.dataset.value = branch.branchId;
        listItem.textContent = `${branch.branchId} - ${branch.name}`; // Display branchId and name
        listItem.addEventListener('click', function () {
            console.log("Dropdown item clicked. Branch ID:", branch.branchId); // ADDED LOG
            const inputField = document.getElementById('branchIdField'); // Get the input field
            if (inputField) {
                console.log("Input field 'branchIdField' found:", inputField); // ADDED LOG
                console.log("Setting input field value to:", branch.branchId); // ADDED LOG
                inputField.value = branch.branchId; // Set input value
                saveEdit('branchIdField', branch.branchId); // Save edit with new value
                console.log("Calling saveEdit('branchIdField',", branch.branchId, ")"); // ADDED LOG
            } else {
                console.log("Input field 'branchIdField' NOT FOUND!"); // ADDED LOG
            }
            dropdownElement.style.display = 'none'; // Hide the dropdown
        });
        dropdownElement.appendChild(listItem);
    });
    dropdownElement.style.display = 'block'; // Show the dropdown
}

let branchDropdownInput; // To hold the branchIdField input element globally within this scope

function setupBranchIdDropdownInput(inputElement) {
    branchDropdownInput = inputElement; // Assign the inputElement to the global variable

    inputElement.addEventListener('input', async function () {
        const inputValue = inputElement.value.trim();
        const dropdownElement = document.getElementById('branchIdDropdown');

        if (inputValue.length >= 1) { // Fetch branches when input length is at least 1
            try {
                const criteria = { branchId: inputValue, searchSimilar: true }; // Enable similar search
                const url = new URL('http://localhost:8080/NetBanking/api/branch');
                const token = sessionStorage.getItem('jwt'); // Retrieve JWT token
                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'action': 'GET',
                        'Content-Type': 'application/json' // Specify content type
                    },
                    body: JSON.stringify(criteria)
                });

                const data = await response.json();
                if (data.status == 200) {
                    if (Array.isArray(data.branch) && data.branch.length > 0) {
                        displayBranchDropdown(data.branch, dropdownElement);
                    } else {
                        dropdownElement.innerHTML = '<li>No matches</li>';
                        dropdownElement.style.display = 'block';
                    }
                } else {
                    dropdownElement.innerHTML = `<li>Error: ${data.message}</li>`;
                    dropdownElement.style.display = 'block';
                }
            } catch (error) {
                console.error('Error fetching branches:', error);
                dropdownElement.innerHTML = '<li>Error fetching branches</li>';
                dropdownElement.style.display = 'block';
            }
        } else {
            dropdownElement.style.display = 'none'; // Hide dropdown if input is too short
        }
    });

    // Close dropdown on blur
    inputElement.addEventListener('blur', () => {
        const dropdownElement = document.getElementById('branchIdDropdown');
        setTimeout(() => {
            if (!dropdownElement.matches(':hover') && document.activeElement !== inputElement) {
                dropdownElement.style.display = 'none';
            }
        }, 150);
    });


    // Handle keypress on input
    inputElement.addEventListener('keypress', function(event) {
        const dropdownElement = document.getElementById('branchIdDropdown');
        if (event.key === 'Enter') {
            dropdownElement.style.display = 'none'; // Hide dropdown on Enter key
            saveEdit('branchIdField', inputElement.value); // Save edit on Enter for branchIdField
            inputElement.blur(); // Optionally remove focus after saving
        }
    });


    // Prevent dropdown from closing immediately on input focus, allow input to gain focus for typing
    inputElement.addEventListener('focus', () => {
        const dropdownElement = document.getElementById('branchIdDropdown');
        if (dropdownElement.style.display !== 'block' && inputElement.value.trim().length >= 1) {
            dropdownElement.style.display = 'block'; // Re-show if it was hidden and input has content
        }
    });
}


function setupBranchIdDropdown() {
    const branchIdElement = document.getElementById("branchIdField");
    if (branchIdElement && branchIdElement.parentElement.classList.contains('editable-field')) {
        branchIdElement.addEventListener('click', function () {
            if (branchIdElement.tagName === 'SPAN') {
                toggleEdit('branchIdField'); // Turn span into input for editing and dropdown
            }
        });
    }
}


document.addEventListener('DOMContentLoaded', function () {
    let currentPage = 1; // Current page
    const limit = 8; // Items per page
    let totalPages = 1; // Total pages will be calculated later
    let searchCriteria = {};

    const debounce = (func, delay) => {
        let timeout;
        return function (...args) {
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(this, args), delay);
        };
    };

    const debouncedSearch = debounce(handleRealTimeSearch, 300);

    const actorIdField = document.getElementById('actorId');
    const userIdField = document.getElementById('userId');
    const branchIdField = document.getElementById('branchId');
	const recordnameField = document.getElementById('recordname');
	const actionField = document.getElementById('action');
	
    actorIdField.addEventListener('input', debouncedSearch);
    userIdField.addEventListener('input', debouncedSearch);
    branchIdField.addEventListener('input', debouncedSearch);
	recordnameField.addEventListener('input', debouncedSearch);
	actionField.addEventListener('input', debouncedSearch);

    async function handleRealTimeSearch() {
        const actorId = document.getElementById('actorId').value.trim();
        const subjectId = document.getElementById('userId').value.trim();
        const keyValue = document.getElementById('branchId').value.trim();
		const recordname = document.getElementById('recordname').value.trim();
		const action = document.getElementById('action').value.trim();
        // Set search criteria
        searchCriteria = { actorId, subjectId, keyValue, recordname, action };

        // Fetch total count and accounts
        await fetchTotalCount(searchCriteria);
        await fetchAccounts();
    }


    // Function to fetch total count based on search criteria
    async function fetchTotalCount(criteria) {
        console.log("tc: ",criteria)
        try {
            const token = sessionStorage.getItem('jwt');
            const url = new URL('http://localhost:8080/NetBanking/api/logs ');
            criteria.count = true;
            criteria.searchSimilarFields = ["actorId", "keyValue", "subjectId"];
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
					'action': 'GET',
                },
                body: JSON.stringify(criteria)
            });
            const data = await response.json();
            if (data.status == 200) {
                const totalCount = data.count || 0; // Total accounts count
                totalPages = Math.ceil(totalCount / limit); // Calculate total pages
            } else {
                alert(data.message);
            }
        } catch (error) {
            console.error('Error fetching total count:', error);
            alert('Failed to load total count. Please try again.');
        }
    }

    // Function to fetch accounts for the current page
    async function fetchAccounts(searchSimilarFields) {
        try {
            const token = sessionStorage.getItem('jwt');
            if (!token) {
                window.location.href = "login.html";
                return;
            }

            const url = new URL('http://localhost:8080/NetBanking/api/logs');
            const criteria = {}
            criteria.currentPage = currentPage;
            criteria.limit = limit;
            if (searchCriteria.actorId) criteria.actorId = searchCriteria.actorId;
            if (searchCriteria.subjectId) criteria.subjectId = searchCriteria.subjectId;
            if (searchCriteria.keyValue) criteria.keyValue = searchCriteria.keyValue;
			if (searchCriteria.recordname) criteria.recordname = searchCriteria.recordname;
			if (searchCriteria.action) criteria.action = searchCriteria.action;
            if (searchSimilarFields == null) {
                criteria.searchSimilarFields = ["actorId", "keyValue", "subjectId"];
            }

            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
					'action': 'GET',
                },
                body: JSON.stringify(criteria)
            });
            const data = await response.json();
            if (data.status == 200) {
                if (Array.isArray(data.logs)) {
                    displayAccounts(data.logs);
                } else {
                    displayAccounts(null);
                }
                updatePaginationControls();
            } else {
                alert(data.message);
            }
        } catch (error) {
            console.error('Error fetching accounts:', error);
            alert("Failed to retrieve accounts.");
        }
    }

    async function fetchUserData(userId) {
        try {
            const token = sessionStorage.getItem('jwt');
            if (!token) {
                window.location.href = "login.html";
                return;
            }

            const url = new URL('http://localhost:8080/NetBanking/api/user');
            const criteria = {}
            criteria.userId = userId;
            criteria.userType = 'customer';
            criteria.moreDetails = false;

            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'action': 'GET'
                },
                body: JSON.stringify(criteria)
            });

            const data = await response.json();
            if (data.status == 200) {
                return data.users
            } else {
                alert(data.message);
            }
        } catch (error) {
            console.error('Error fetching accounts:', error);
            alert("Failed to retrieve accounts.");
        }
    }

    // Function to display accounts in the UI
    function displayAccounts(accounts) {
        const accountsContainer = document.querySelector('.accountInsert');

        if (!accounts || accounts.length === 0) {
            accountsContainer.innerHTML = "<p>No accounts found.</p>";
            return;
        }

        accountsContainer.innerHTML = ''; // Clear previous accounts
        accounts.forEach(account => {
            const accountHTML = createAccountCard(account);
            accountsContainer.insertAdjacentHTML('beforeend', accountHTML);
        });
    }

    // Function to update pagination controls
    function updatePaginationControls() {
        const paginationContainer = document.getElementById('paginationControls');
        if (!paginationContainer) {
            const cardContentDiv = document.querySelector('.accountInsert');
            const controlsDiv = document.createElement('div');
            controlsDiv.id = 'paginationControls';
            controlsDiv.style.display = 'flex';
            controlsDiv.style.justifyContent = 'center';
            controlsDiv.style.marginTop = '20px';
            cardContentDiv.parentElement.appendChild(controlsDiv);
        }

        paginationContainer.innerHTML = `
            <button id="prevPage" ${currentPage === 1 ? 'disabled' : ''}>Prev</button>
            <span style="margin: 0 15px;">Page ${currentPage} of ${totalPages}</span>
            <button id="nextPage" ${currentPage === totalPages ? 'disabled' : ''}>Next</button>
        `;

        document.getElementById('prevPage').addEventListener('click', function () {
            if (currentPage > 1) {
                currentPage--;
                fetchAccounts();
            }
        });

        document.getElementById('nextPage').addEventListener('click', function () {
            if (currentPage < totalPages) {
                currentPage++;
                fetchAccounts();
            }
        });
    }

    // Initialize with no filters (default behavior)
    async function initialize() {
        try {
            const token = sessionStorage.getItem('jwt'); // Get the stored JWT token
            if (!token) {
                window.location.href = "login.html";
                return;
            }

            const decodedToken = decodeJWT(token); // Decode the JWT token
            tokenBranchId = decodedToken?.branchId; // Extract the branchId
			tokenRole = decodedToken?.role;
            console.log(tokenBranchId)
            if (!tokenBranchId) {
                alert("Branch ID not found in token.");
                return;
            }

            // Set the initial search criteria to the branchId
            searchSimilarFields = []
            searchCriteria = { branchId: tokenBranchId };

            // Fetch total count and accounts based on the branchId
            await fetchTotalCount(searchCriteria);
            await fetchAccounts(searchSimilarFields);
        } catch (error) {
            console.error('Error during initialization:', error);
            alert("Failed to initialize accounts. Please check the console for details.");
        }
    }
    initialize();

    // Function to create modal and add the event listener for closing the modal
    function createModal() {
        // Create modal HTML structure
        const modalHTML = `
            <div id="accountModal" class="modal">
                <div class="modal-content">
                    <span class="close-button" id="closeModal">×</span>
                    <h2>Account Details</h2>
                    <div id="modalContent"></div>
                </div>
            </div>
        `;

        // Append the modal HTML to the body
        document.body.insertAdjacentHTML('beforeend', modalHTML);

        // Add event listener for closing the modal
        document.getElementById("closeModal").addEventListener("click", function () {
            const modal = document.getElementById("accountModal");
            modal.style.display = "none";
        });
    }

    function createBranchModal() {
        // Create modal HTML structure
        const modalHTML = `
            <div id="branchModal" class="modal">
                <div class="modal-content">
                    <span class="close-button" id="closeModalBranch">×</span>
                    <h2>Branch Details</h2>
                    <div id="branchModalContent"></div>
                </div>
            </div>
        `;

        // Append the modal HTML to the body
        document.body.insertAdjacentHTML('beforeend', modalHTML);

        // Add event listener for closing the modal
        document.getElementById("closeModalBranch").addEventListener("click", function () {
            const modal = document.getElementById("branchModal");
            modal.style.display = "none";
        });
    }

    // Event listener to show account details when eye icon is clicked
    document.addEventListener("click", async function (event) {
        if (event.target.classList.contains("more")) {
            const account = JSON.parse(event.target.getAttribute("data-account"));
            const user = await fetchUserData(account.userId)
            const name = user[0].name
            const detailedAccount = { ...account, name };
            showAccountDetails(detailedAccount);
        }

        if (event.target.classList.contains("save-button")) {
            saveChanges();
        }
    });

    async function fetchBranchs(branchId) {
        const token = sessionStorage.getItem('jwt');
        const url = `http://localhost:8080/NetBanking/api/branch`;
        criteria = {}
        criteria.branchId = branchId;
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'action': 'GET'
                },
                body: JSON.stringify(criteria)
            });

            const data = await response.json();
            if (data.status == 200) {
                return data.branch; // Return the branch details
            } else {
                alert(data.message);
                return null;
            }
        } catch (error) {
            console.error('Error fetching branch details:', error);
            alert("Failed to fetch branch details.");
            return null;
        }
    }

    document.addEventListener("click", async function (event) {
        if (event.target.classList.contains("moreBranch")) {
            // const branchId = JSON.parse(event.target.getAttribute("data-branch"));
            const account = JSON.parse(event.target.getAttribute("data-branch"));
            const branch = await fetchBranchs(account.branchId);
            // console.log(branch[0])
            if (branch) {
                showBranchDetails(branch);
            }
        }
    });

	const recordElement = document.getElementById('recordname');
    recordElement.addEventListener('change', function() {
        if (this.value) {
            this.classList.add('selected');
        } else {
            this.classList.remove('selected');
        }
    });
	const actionElement = document.getElementById('action');
    actionElement.addEventListener('change', function() {
        if (this.value) {
            this.classList.add('selected');
        } else {
            this.classList.remove('selected');
        }
    });


    createModal();
    createBranchModal();
});