(async function () {
    const token = sessionStorage.getItem("jwt");

    if (token) {
        try {
            const response = await fetch('api/validate-token', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });

            const result = await response.json();

            if (result.status != 200) {
                // Invalid token, remove it from storage
                sessionStorage.removeItem("jwt");
				window.location.href = 'login.html';
            }
        } catch (error) {
            console.error('Token validation error:', error);
            sessionStorage.removeItem("jwt");
        }
    }
})();