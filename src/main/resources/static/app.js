const API_BASE_URL = 'http://localhost:8080/api';
const responseArea = document.getElementById('responseArea');

// --- Helper Functions ---
function displayResponse(message, isError = false) {
    responseArea.textContent = JSON.stringify(message, null, 2);
    responseArea.style.backgroundColor = isError ? '#f8d7da' : '#d4edda';
}

function getToken() {
    return localStorage.getItem('jwtToken');
}

// --- Event Listeners ---

// 1. Registration
document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('regUsername').value;
    const password = document.getElementById('regPassword').value;

    try {
        const res = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        const data = await res.text();
        if (!res.ok) throw new Error(data);
        displayResponse({ success: data });
    } catch (error) {
        displayResponse({ error: error.message }, true);
    }
});

// 2. Login
document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;

    try {
        const res = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Login failed');

        if (data.mfaRequired) {
            displayResponse({ message: 'MFA Required. Please enter your code.' });
            document.getElementById('mfaCard').style.display = 'block';
            document.getElementById('mfaUsername').value = username;
        } else {
            localStorage.setItem('jwtToken', data.token);
            displayResponse({ message: 'Login successful!', token: data.token });
            document.getElementById('mfaCard').style.display = 'none';
        }
    } catch (error) {
        displayResponse({ error: error.message }, true);
    }
});

// 3. MFA Verification
document.getElementById('mfaForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('mfaUsername').value;
    const code = document.getElementById('mfaCode').value;

    try {
        const res = await fetch(`${API_BASE_URL}/auth/verify-mfa`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, code })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'MFA verification failed');

        localStorage.setItem('jwtToken', data.token);
        displayResponse({ message: 'MFA verification successful!', token: data.token });
        document.getElementById('mfaCard').style.display = 'none';
    } catch (error) {
        displayResponse({ error: error.message }, true);
    }
});

// 4. Access Protected Data
document.getElementById('getUserDataBtn').addEventListener('click', async () => {
    try {
        const res = await fetch(`${API_BASE_URL}/user/data`, {
            headers: { 'Authorization': `Bearer ${getToken()}` }
        });
        const data = await res.text();
        if (!res.ok) throw new Error('Failed to fetch user data. Are you logged in with a valid token?');
        displayResponse({ userData: data });
    } catch (error) {
        displayResponse({ error: error.message }, true);
    }
});

document.getElementById('getAdminDataBtn').addEventListener('click', async () => {
    try {
        const res = await fetch(`${API_BASE_URL}/admin/data`, {
            headers: { 'Authorization': `Bearer ${getToken()}` }
        });
        const data = await res.text();
        if (!res.ok) throw new Error('Failed to fetch admin data. Do you have ADMIN role?');
        displayResponse({ adminData: data });
    } catch (error) {
        displayResponse({ error: error.message }, true);
    }
});


// 5. MFA Setup
document.getElementById('setupMfaBtn').addEventListener('click', async () => {
    try {
        const res = await fetch(`${API_BASE_URL}/mfa/setup`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${getToken()}` }
        });
        if (!res.ok) throw new Error('Failed to start MFA setup. Are you logged in?');
        const data = await res.json();

        document.getElementById('qrCodeArea').innerHTML = `<p>Scan this QR Code with your authenticator app:</p><img src="${data.qrCodeUri}" alt="QR Code">`;
        document.getElementById('verifyMfaSetupForm').style.display = 'block';
        displayResponse({ message: "Scan QR code and enter the code to verify." });

    } catch (error) {
        displayResponse({ error: error.message }, true);
    }
});

document.getElementById('verifyMfaSetupForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const code = document.getElementById('verifyMfaSetupCode').value;

    try {
        const res = await fetch(`${API_BASE_URL}/mfa/verify`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${getToken()}`
            },
            body: JSON.stringify({ code }) // Username is taken from principal on backend
        });
        const data = await res.text();
        if (!res.ok) throw new Error(data);

        displayResponse({ success: data });
        document.getElementById('qrCodeArea').innerHTML = '';
        document.getElementById('verifyMfaSetupForm').style.display = 'none';

    } catch (error) {
        displayResponse({ error: error.message }, true);
    }
});