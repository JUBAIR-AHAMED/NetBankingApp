(async function () {
    const token = sessionStorage.getItem("jwt");

    if (token) {
        const expiryTime = getTokenExpiryTime(token);

        if (!expiryTime || Date.now() >= expiryTime) {
            expireSession(); // Expire immediately if already expired
            return;
        }

        // Schedule session expiration at the exact expiry time
        const remainingTime = expiryTime - Date.now();
        setTimeout(expireSession, remainingTime);

        try {
            const response = await fetch('api/validate-token', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });

            const result = await response.json();

            if (result.status !== 200) {
                expireSession();
            }
        } catch (error) {
            console.error('Token validation error:', error);
            expireSession();
        }
    }
})();

// Function to get token expiry time (returns timestamp in milliseconds)
function getTokenExpiryTime(token) {
    try {
        const payloadBase64 = token.split('.')[1]; // Extract payload
        const decodedPayload = JSON.parse(atob(payloadBase64)); // Decode payload
        return decodedPayload.exp * 1000; // Convert expiry to milliseconds
    } catch (error) {
        console.error("Error decoding token:", error);
        return null; // Return null if decoding fails
    }
}

// Function to handle session expiration
function expireSession() {
    sessionStorage.removeItem("jwt");
    showSessionExpiredPopup();
}

// Function to show the session expired popup
function showSessionExpiredPopup() {
    // Create overlay
    const overlay = document.createElement("div");
    overlay.style.position = "fixed";
    overlay.style.top = "0";
    overlay.style.left = "0";
    overlay.style.width = "100vw";
    overlay.style.height = "100vh";
    overlay.style.backgroundColor = "rgba(0, 0, 0, 0.5)";
    overlay.style.display = "flex";
    overlay.style.justifyContent = "center";
    overlay.style.alignItems = "center";
    overlay.style.zIndex = "9999";

    // Create popup box
    const popup = document.createElement("div");
    popup.style.backgroundColor = "#fff";
    popup.style.padding = "20px";
    popup.style.borderRadius = "10px";
    popup.style.boxShadow = "0 4px 8px rgba(0, 0, 0, 0.2)";
    popup.style.textAlign = "center";
    popup.style.width = "300px";

    // Create message
    const message = document.createElement("p");
    message.innerText = "Your session has expired. Please log in again.";

    // Create OK button
    const okButton = document.createElement("button");
    okButton.innerText = "OK";
    okButton.style.padding = "10px 20px";
    okButton.style.marginTop = "10px";
    okButton.style.border = "none";
    okButton.style.backgroundColor = "#007BFF";
    okButton.style.color = "#fff";
    okButton.style.borderRadius = "5px";
    okButton.style.cursor = "pointer";
    
    // Button click event to redirect
    okButton.onclick = function () {
        document.body.removeChild(overlay);
        window.location.href = 'login.html';
    };

    // Append elements
    popup.appendChild(message);
    popup.appendChild(okButton);
    overlay.appendChild(popup);
    document.body.appendChild(overlay);
}
