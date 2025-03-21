function createProfile() {
    return `
        <div style="height: 89vh; width: 100%; display: flex; justify-content: center; align-items: center;">
            <div class="profile-container">
                <header class="profile-header">
                    <h1>Profile</h1>
                    <button class="save-button">Save Changes</button>
                </header>

                <div class="profile-body">
                    <!-- Profile Picture Section -->
                    <div class="profile-picture-section">
                        <img src="icons/10-profile-picture-ideas-to-make-you-stand-out (1).jpg" alt="Profile Picture" class="profile-picture">
                    </div>

                    <!-- Profile Information Section -->
                    <div class="profile-info-section">
                        <div class="info-row">
                            <label for="name">Name</label>
                            <div class="editable-field">
                                <span id="name"></span>
                                <button class="edit-icon" onclick="toggleEdit('name')"><img src="icons/pen.png" alt="edit-logo"></button>
                            </div>
                        </div>

                        <div class="info-row">
                            <label for="email">Email</label>
                            <div class="editable-field">
                                <span id="email"></span>
                                <button class="edit-icon" onclick="toggleEdit('email')"><img src="icons/pen.png" alt="edit-logo"></button>
                            </div>
                        </div>

                        <div class="info-row">
                            <label for="mobile">Mobile</label>
                            <div class="editable-field">
                                <span id="mobile"></span>
                                <button class="edit-icon" onclick="toggleEdit('mobile')"><img src="icons/pen.png" alt="edit-logo"></button>
                            </div>
                        </div>

                        <div class="info-row">
                            <label for="dob">Date of Birth</label>
                            <span class="non-editable-field" id="dob"></span>
                        </div>

                        <div class="info-row">
                            <label for="status">Status</label>
                            <span class="non-editable-field" id="status"></span>
                        </div>

                        <!-- Conditional Fields for Aadhar, PAN, Branch ID -->
                        <div id="aadhar-container" class="info-row" style="display: none;">
                            <label for="aadhar">Aadhar Number</label>
                            <span class="non-editable-field" id="aadhar"></span>
                        </div>

                        <div id="pan-container" class="info-row" style="display: none;">
                            <label for="pan">PAN Number</label>
                            <span class="non-editable-field" id="pan"></span>
                        </div>

                        <div id="role-container" class="info-row" style="display: none;">
                            <label for="role">Role</label>
                            <span class="non-editable-field" id="role"></span>
                        </div>

                        <div id="branchId-container" class="info-row" style="display: none;">
                            <label for="branchId">Branch ID</label>
                            <span class="non-editable-field" id="branchId"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

// Fetches the profile data and displays it
async function fetchProfile() {
    try {
        // Retrieve the JWT token from local storage
        const token = sessionStorage.getItem("jwt");

        // Fetch profile data from the server
        const response = await fetch('api/profile', {
            method: "GET",
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        // Parse response data
        const data = await response.json();

        // If response is OK, render the profile data
        if (response.ok) {
            displayProfile(data.profile);
        } else {
            showNotification(data.message || "Failed to load profile.", "error");
        }
    } catch (error) {
        console.error(error);
        showNotification("Failed loading profile.", "error");
    }
}

// Renders the profile HTML and populates data
function displayProfile(profile) {
    const profileContainer = document.querySelector(".pagebody");
    profileContainer.innerHTML = createProfile();

    // Populate mandatory profile fields
    document.getElementById("name").innerText = profile.name;
    document.getElementById("email").innerText = profile.email;
    document.getElementById("mobile").innerText = profile.mobile;
    document.getElementById("dob").innerText = formatDate(profile.dateOfBirth);
    document.getElementById("status").innerText = profile.status;
    
    // Conditionally display Aadhar, PAN, and Branch ID
    if (profile.aadharNumber) {
        const aadharContainer = document.getElementById("aadhar-container");
        console.log(profile.aadharNumber)
        document.getElementById("aadhar").innerText = profile.aadharNumber;
        aadharContainer.style.display = "flex";
    }

    if (profile.panNumber) {
        const panContainer = document.getElementById("pan-container");
        document.getElementById("pan").innerText = profile.panNumber ;
        panContainer.style.display = "flex";
    }

    if (profile.branchId) {
        const branchIdContainer = document.getElementById("branchId-container");
        document.getElementById("branchId").innerText = profile.branchId;
        branchIdContainer.style.display = "flex";
    }

    if ((profile.role) && profile.role === "EMPLOYEE" || profile.role === "MANAGER") {
        const roleContainer = document.getElementById("role-container");
        document.getElementById("role").innerText = profile.role;
        roleContainer.style.display = "flex";
    }

    const saveButton = document.querySelector(".save-button");
    saveButton.addEventListener("click", () => saveChanges(profile));
}

function formatDate(timestamp) {
    const date = new Date(timestamp);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are zero-indexed
    const year = date.getFullYear();
    return `${day}.${month}.${year}`;
}

// Toggles editing of a specific field
function toggleEdit(fieldId) {
    const field = document.getElementById(fieldId);

    // Check if field is a span and toggle to input
    if (field && field.tagName === 'SPAN') {
        const currentText = field.innerText;

        // Create an input field
        const input = document.createElement('input');
        input.type = 'text';
        input.value = currentText;
        input.classList.add('editable-input');
        input.id = fieldId;

        // Replace span with input field
        field.replaceWith(input);

        // Focus the input for immediate editing
        input.focus();

        // Save on blur or enter key
        input.addEventListener('blur', () => saveEdit(fieldId, input.value));
        input.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                saveEdit(fieldId, input.value);
            }
        });
    }
}

// Saves the edit and restores the span
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

async function saveChanges(originalProfile) {
    const updatedProfile = {};

    // Compare each field with the original data
    const name = document.getElementById("name").innerText;
    if (name !== originalProfile.name) updatedProfile.name = name;

    const email = document.getElementById("email").innerText;
    if (email !== originalProfile.email) updatedProfile.email = email;

    const mobile = document.getElementById("mobile").innerText;
    if (mobile !== originalProfile.mobile) updatedProfile.mobile = mobile;

    // Conditionally add optional fields
    if (document.getElementById("aadhar").style.display === "flex") {
        const aadharNumber = document.getElementById("aadhar").innerText;
        if (aadharNumber !== originalProfile.aadharNumber) updatedProfile.aadharNumber = aadharNumber;
    }

    if (document.getElementById("pan-container").style.display === "flex") {
        const panNumber = document.getElementById("pan").innerText;
        if (panNumber !== originalProfile.panNumber) updatedProfile.panNumber = panNumber;
    }

    // If no changes, notify and return
    if (Object.keys(updatedProfile).length === 0) {
        showNotification("No changes detected for update.", "info");
        return;
    }

    try {
        // Retrieve the JWT token from local storage
        const token = sessionStorage.getItem("jwt");

        // Send updated profile data to the server
        const response = await fetch('api/profile', {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(updatedProfile)
        });

        const result = await response.json();

        if (response.ok) {
            showNotification("Profile updated successfully!");
            fetchProfile(); // Reload the profile to reflect updated data
        } else {
            showNotification(result.message || "Failed to update profile.", "warning");
        }
    } catch (error) {
        console.error(error);
        showNotification("An error occurred while updating the profile.", "error");
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

// Initial call to fetch profile data on page load
window.onload = fetchProfile;
