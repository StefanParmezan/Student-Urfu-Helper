// Базовые утилиты для работы с аутентификацией (упрощено, без backend интеграции)

// Получение данных пользователя из localStorage
function getUserData() {
    const userData = localStorage.getItem('userData');
    return userData ? JSON.parse(userData) : null;
}

// Выход из системы
function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userData');
    window.location.href = 'login.html';
}
