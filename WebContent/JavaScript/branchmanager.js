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

function formatIndianCurrency(amount) {

    const absoluteAmount = Math.abs(amount);

    const [integerPart, decimalPart = ''] = absoluteAmount.toString().split('.');

    const formattedDecimalPart = decimalPart.padEnd(2, '0').slice(0, 2)
    const lastThreeDigits = integerPart.slice(-3);
    const otherDigits = integerPart.slice(0, -3);
    const formattedIntegerPart = otherDigits.replace(/\B(?=(\d{2})+(?!\d))/g, ',') + (otherDigits ? ',' : '') + lastThreeDigits;

    const formattedAmount = `${formattedIntegerPart}.${formattedDecimalPart}`;

    return formattedAmount;
}

function showAccountDetails(account) {
    const modal = document.getElementById("accountModal");
    const modalContent = document.getElementById("modalContent");

    // Format account details
    modalContent.innerHTML = `
        <p><strong>Account Number:</strong> ${account.accountNumber}</p>
        <p><strong>Account Type:</strong> ${account.accountType}</p>
        <p><strong>Balance:</strong> ${formatIndianCurrency(account.balance)}</p>
        <p><strong>Date of Opening:</strong> ${formatDate(account.dateOfOpening)}</p>
        <p><strong>Account Holder:</strong> ${account.name}</p>
        <p><strong>Branch ID:</strong> ${account.branchId}</p>
        <p><strong>Status:</strong> ${account.status}</p>
    `;

    // Show the modal
    modal.style.display = "block";
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
                <span>${branch.branchId}</span>
            </div>
        </div>
        <div class="info-row">
            <label>Branch Name</label>
            <div class="non-editable-field">
                <span>${branch.name}</span>
            </div>
        </div>
        <div class="info-row">
            <label>Address</label>
            <div class="non-editable-field">
                <span>${branch.address}</span>
            </div>
        </div>
        <div class="info-row">
            <label>IFSC</label>
            <div class="non-editable-field">
                <span>${branch.ifsc}</span>
            </div>
        </div>
        <div class="info-row">
            <label>Manager ID</label>
            <div class="non-editable-field">
                <span>${branch.employeeId}</span>
            </div>
        </div>
    `;

    // Show the modal
    modal.style.display = "block";
}

function createAccountCard(account) {
    const {
        branchId,
        name,
        employeeId,
        ifsc
    } = account;


    return `
        <div class="valueColumn">
            <div class="columnValues accValue">
                ${branchId}
                <div class="more" style="position: fixed; z-index: 2; width: 17%; align-self: center; cursor: pointer; justify-items: flex-end;" data-account='${JSON.stringify(account)}'>
                    <img class="eye-logo-acc more" src="icons/eye-svgrepo-com.svg" alt="view icon" data-account='${JSON.stringify(account)}'>
                </div>
            </div>
            <div class="columnValues">${name}</div>
            <div class="columnValues branchValue">
                ${employeeId}
            </div>
            <div class="columnValues" style="display: flex;">${ifsc}
            </div>
        </div>
    `;
}

function formatDate(timestamp) {
    const date = new Date(timestamp);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are zero-indexed
    const year = date.getFullYear();

    return `${day}.${month}.${year}`;
}

document.addEventListener('DOMContentLoaded', function () {
    const userIdField = document.getElementById('employeeId');
    const ifscField = document.getElementById('ifsc');
    const branchIdField = document.getElementById('branchId');

    // Validate User ID: numbers only, max 6 digits
    userIdField.addEventListener('input', function () {
        this.value = this.value.replace(/[^0-9]/g, ''); // Allow only numbers
        if (this.value.length > 6) {
            this.value = this.value.slice(0, 6); // Restrict to 6 digits
        }
    });

    ifscField.addEventListener('input', function () {
        const value = ifscField.value.toUpperCase(); // Convert to uppercase
        ifscField.value = value.replace(/[^A-Z0-9]/g, ''); // Allow only A-Z and 0-9
    
        // Ensure IFSC pattern: 4 letters, 1 zero, 6 digits
        if (value.length > 11 || !/^([A-Z]{0,4}0?\d{0,6})?$/.test(value)) {
            ifscField.value = value.slice(0, -1); // Remove last invalid character
        }
    });
    

    // Validate Branch ID: numbers only, max 5 digits
    branchIdField.addEventListener('input', function () {
        this.value = this.value.replace(/[^0-9]/g, ''); // Allow only numbers
        if (this.value.length > 5) {
            this.value = this.value.slice(0, 5); // Restrict to 5 digits
        }
    });
});

document.addEventListener('DOMContentLoaded', function () {
    let currentPage = 1; // Current page
    const limit = 8; // Items per page
    let totalPages = 1;
    let searchCriteria = {}; 

    const debounce = (func, delay) => {
        let timeout;
        return function (...args) {
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(this, args), delay);
        };
    };

    const debouncedSearch = debounce(handleRealTimeSearch, 300);

    const employeeIdField = document.getElementById('employeeId');
    const ifscField = document.getElementById('ifsc');
    const branchIdField = document.getElementById('branchId');

    employeeIdField.addEventListener('input', debouncedSearch);
    ifscField.addEventListener('input', debouncedSearch);
    branchIdField.addEventListener('input', debouncedSearch);

    async function handleRealTimeSearch() {
        const employeeId = document.getElementById('employeeId').value.trim();
        const ifsc = document.getElementById('ifsc').value.trim();
        const branchId = document.getElementById('branchId').value.trim();

        // Set search criteria
        searchCriteria = {branchId, employeeId, ifsc};

        // Fetch total count and accounts
        await fetchTotalCount(searchCriteria);
        await fetchAccounts();
    }

    async function fetchTotalCount(criteria) {
        try {
            const token = sessionStorage.getItem('jwt');
            criteria.count = true;
            criteria.searchSimilar = true;
            
            const response = await fetch('api/branch', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'action': 'GET'
                },
                body: JSON.stringify(criteria)
            });
            const data = await response.json();
            if (data.status==200) {
                const totalCount = data.count || 0; // Total accounts count
                totalPages = Math.ceil(totalCount / limit); // Calculate total pages
            } else {
                showNotification(data.message, "warning");
            }
        } catch (error) {
            console.error('Error fetching total count:', error);
            showNotification('Failed to load total count. Please try again.', "error");
        }
    }

    // Function to fetch accounts for the current page
    async function fetchAccounts() {
        try {
            const token = sessionStorage.getItem('jwt');
            if (!token) {
                window.location.href = "/login.html";
                return;
            }

            const criteria = {}

            criteria.currentPage = currentPage;
            criteria.limit = limit;
            criteria.branchId = searchCriteria.branchId;
            criteria.employeeId = searchCriteria.employeeId;
            criteria.ifsc = searchCriteria.ifsc;
            criteria.searchSimilar = true;

            const response = await fetch('api/branch', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'action': 'GET'
                },
                body: JSON.stringify(criteria)
            });
            const data = await response.json();
            if (data.status==200) {
                if (Array.isArray(data.branch)) {
                    displayAccounts(data.branch);
                } else {
                    displayAccounts(null);
                }
                updatePaginationControls();
            } else {
                showNotification(data.message, "warning");
            }
        } catch (error) {
            console.error('Error fetching accounts:', error);
            showNotification("Failed to retrieve accounts.", "error");
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

    // Event listener for the search form
    document.getElementById('searchForm').addEventListener('submit', async function (event) {
        event.preventDefault(); // Prevent default form submission
    
        // Get the input values
        const ifsc = document.getElementById('ifsc').value.trim();
        const employeeId = document.getElementById('employeeId').value.trim();
        const branchId = document.getElementById('branchId').value.trim();
    
        // Validation: At least one input must be provided
        if (!ifsc && !employeeId && !branchId) {
            showNotification("Please enter at least one search criterion.", "info");
            return;
        }
    
        try {
            // Set the search criteria
            searchCriteria.ifsc = ifsc;
            searchCriteria.employeeId = employeeId;
            searchCriteria.branchId = branchId;
    
            // Reset to the first page
            currentPage = 1;
    
            // Fetch total count and accounts
            await fetchTotalCount(searchCriteria);
            await fetchAccounts();
        } catch (error) {
            console.error('Error during search:', error);
            showNotification("Failed to retrieve branchs.", "error");
        }
    });
    

    // Initialize with no filters (default behavior)
    async function initialize() {
        try {
            const token = sessionStorage.getItem('jwt'); // Get the stored JWT token
            if (!token) {
                window.location.href = "/login.html";
                return;
            }
    
            const decodedToken = decodeJWT(token); // Decode the JWT token
            const branchId = decodedToken?.branchId; // Extract the branchId
    
            if (!branchId) {
                showNotification("Branch ID not found in token.", "error");
                return;
            }
    
            // Set the initial search criteria to the branchId
            searchCriteria = { };
    
            // Fetch total count and accounts based on the branchId
            await fetchTotalCount(searchCriteria);
            await fetchAccounts();
        } catch (error) {
            console.error('Error during initialization:', error);
            showNotification("Failed to initialize branchs.", "error");
        }
    }
    initialize();

    // Function to create modal and add the event listener for closing the modal
    function createModal() {
        // Create modal HTML structure
        const modalHTML = `
            <div id="accountModal" class="modal">
                <div class="modal-content">
                    <span class="close-button" id="closeModal">&times;</span>
                    <h2>Account Details</h2>
                    <div id="modalContent"></div>
                </div>
            </div>
        `;

        // Append the modal HTML to the body
        document.body.insertAdjacentHTML('beforeend', modalHTML);

        // Add event listener for closing the modal
        document.getElementById("closeModal").addEventListener("click", function() {
            const modal = document.getElementById("accountModal");
            modal.style.display = "none";
        });
    }

    function createBranchModal() {
        // Create modal HTML structure
        const modalHTML = `
            <div id="branchModal" class="modal">
                <div class="modal-content">
                    <span class="close-button" id="closeModalBranch">&times;</span>
                    <h2>Branch Details</h2>
                    <div id="branchModalContent"></div>
                </div>
            </div>
        `;

        // Append the modal HTML to the body
        document.body.insertAdjacentHTML('beforeend', modalHTML);

        // Add event listener for closing the modal
        document.getElementById("closeModalBranch").addEventListener("click", function() {
            const modal = document.getElementById("branchModal");
            modal.style.display = "none";
        });
    }

    // Event listener to show account details when eye icon is clicked
    document.addEventListener("click", function(event) {
        if (event.target.classList.contains("more")) {
            const account = JSON.parse(event.target.getAttribute("data-account"));
            showBranchDetails(account);
        }
    });

    async function fetchBranchs(branchId) {
        const token = sessionStorage.getItem('jwt');
        criteria = {}
        criteria.branchId = branchId;

        try {
            const response = await fetch('api/branch', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'action': 'GET'
                },
                body: JSON.stringify(criteria)
            });
    
            const data = await response.json();
            if (data.status==200) {
                return data.branch; // Return the branch details
            } else {
                showNotification(data.message, "error");
                return null;
            }
        } catch (error) {
            console.error('Error fetching branch details:', error);
            showNotification("Failed to fetch branch details.", "error");
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

    createModal();
    createBranchModal();
});
