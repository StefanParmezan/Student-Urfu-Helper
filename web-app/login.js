document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const emailInput = document.getElementById('userNameInput');
    const passwordInput = document.getElementById('passwordInput');
    const loginButton = document.getElementById('submitButton');
    const errorDiv = document.getElementById('error');
    const errorText = document.getElementById('errorText');

    function validateEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    function validateForm() {
        const email = emailInput.value.trim();
        const password = passwordInput.value.trim();
        
        const isEmailValid = validateEmail(email);
        const isPasswordValid = password.length >= 1;
        
        return isEmailValid && isPasswordValid;
    }

    function showError(message) {
        errorText.textContent = message;
        errorDiv.style.display = 'block';
    }

    function hideError() {
        errorDiv.style.display = 'none';
    }

    function updateButtonState() {
        if (validateForm()) {
            loginButton.style.background = 'linear-gradient(135deg, #FF9266 0%, #FFA882 100%)';
            loginButton.style.borderColor = '#FF9266';
            loginButton.querySelector('.button-text').style.color = 'white';
            loginButton.disabled = false;
        } else {
            loginButton.style.background = '#F6F6F6';
            loginButton.style.borderColor = '#FF9266';
            loginButton.querySelector('.button-text').style.color = '#A9AAAC';
            loginButton.disabled = true;
        }
    }

    emailInput.addEventListener('input', function() {
        updateButtonState();
        hideError();
    });
    
    passwordInput.addEventListener('input', function() {
        updateButtonState();
        hideError();
    });

    loginForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        if (!validateForm()) {
            showError('Пожалуйста, заполните все поля корректно');
            return;
        }

        const email = emailInput.value.trim();
        const password = passwordInput.value.trim();

        hideError();
        loginButton.querySelector('.button-text').textContent = 'Вход...';
        loginButton.disabled = true;

        try {
            console.log('[LOGIN] Отправка запроса на авторизацию');
            console.log('[LOGIN] Backend URL:', API_CONFIG.BASE_URL);
            console.log('[LOGIN] Email:', email);
            console.log('[LOGIN] Password:', '***');
            
            // Отправляем запрос на backend через API
            const response = await API.login(email, password);
            
            console.log('[LOGIN] Ответ от backend получен:', response);
            
            // Сохраняем токен, если он есть в ответе
            if (response.token) {
                localStorage.setItem('authToken', response.token);
                console.log('[LOGIN] JWT токен сохранен в localStorage');
                
                // Сохраняем данные студента, если есть
                if (response.student) {
                    localStorage.setItem('studentData', JSON.stringify(response.student));
                    console.log('[LOGIN] Данные студента сохранены:', response.student);
                    
                    // Сохраняем также в userData для обратной совместимости
                    localStorage.setItem('userData', JSON.stringify({
                        name: response.student.studentFio || email.split('@')[0],
                        email: email,
                        academicGroup: response.student.academicGroup,
                        timeZone: response.student.timeZone
                    }));
                }
                
                // Перенаправляем на страницу чата
                console.log('[LOGIN] Перенаправление на chat-neiro.html');
                window.location.href = response.redirectUrl || 'chat-neiro.html';
            } else {
                // Если токен не получен, но запрос успешен
                showError(response.message || 'Токен не получен от сервера');
                loginButton.querySelector('.button-text').textContent = 'Войти в аккаунт';
                loginButton.disabled = false;
            }
            
        } catch (error) {
            console.error('[LOGIN] Ошибка при входе:', error);
            
            let errorMessage = 'Неверный email или пароль. Попробуйте снова.';
            
            if (error.message) {
                if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
                    errorMessage = 'Не удалось подключиться к серверу. Проверьте подключение к интернету.';
                } else if (error.message.includes('timeout') || error.message.includes('abort')) {
                    errorMessage = 'Превышено время ожидания. Попробуйте снова.';
                } else {
                    errorMessage = error.message;
                }
            }
            
            showError(errorMessage);
            loginButton.querySelector('.button-text').textContent = 'Войти в аккаунт';
            loginButton.disabled = false;
        }
    });

    emailInput.addEventListener('focus', function() {
        this.parentElement.style.borderColor = '#ff7b4a';
    });

    emailInput.addEventListener('blur', function() {
        this.parentElement.style.borderColor = '#FF9266';
    });

    passwordInput.addEventListener('focus', function() {
        this.parentElement.style.borderColor = '#ff7b4a';
    });

    passwordInput.addEventListener('blur', function() {
        this.parentElement.style.borderColor = '#FF9266';
    });
});
