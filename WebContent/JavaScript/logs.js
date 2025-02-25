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
	
	    modalContent.innerHTML = `
	            <div class="non-editable-field">
	                ${formatActivityDetails(account)}
	            </div>
	    `;
	
	    // Show the modal
	    modal.style.display = "block";
	}
	
	function formatActivityDetails(details) {
	    // Replace commas and semicolons with <br> tags for line breaks
	    return details.replace(/[,;]/g, '<br>');
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
	
	function closeBranchModal() {
	    const modal = document.getElementById("branchModal");
	    modal.style.display = "none";
	}
	
	function createAccountCard(activity) {
	    const {
			actorId,
	        subjectId,
			recordname,
			action,
			keyValue,
			actionTime,
	        details
	    } = activity;
	
	    return `
	        <div class="valueColumn">
	            <div class="columnValues accValue">
	                ${actorId}
	                <div class="more" style="position: fixed; z-index: 2; width: 17%; align-self: center; cursor: pointer; justify-items: flex-end;" data-account='${JSON.stringify(details)}'>
	                    <img class="eye-logo-acc more" src="icons/eye-svgrepo-com.svg" alt="view icon" data-account='${JSON.stringify(details)}'>
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
	            const url = new URL('api/logs ');
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
	
	            const url = new URL('api/logs');
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
	
	    // Function to display accounts in the UI
	    function displayAccounts(accounts) {
	        const accountsContainer = document.querySelector('.accountInsert');
	
	        if (!accounts || accounts.length === 0) {
	            accountsContainer.innerHTML = "<p>No records found.</p>";
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
	                    <h2>Activity Details</h2>
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

	    // Event listener to show account details when eye icon is clicked
	    document.addEventListener("click", async function (event) {
	        if (event.target.classList.contains("more")) {
	            const details = JSON.parse(event.target.getAttribute("data-account"));
	            showAccountDetails(details);
	        }
	
	        if (event.target.classList.contains("save-button")) {
	            saveChanges();
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