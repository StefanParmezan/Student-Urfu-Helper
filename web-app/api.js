// API конфигурация
const API_CONFIG = {
    // Базовый URL для backend API
    // Измените на адрес вашего backend сервера
    BASE_URL: 'http://10.221.110.87',
    
    // Endpoints
    ENDPOINTS: {
        AUTH: '/auth',
        USER_PROFILE: '/user/profile',
        USER_INFO: '/user/info',
        CHAT: '/chat',
        CHAT_BY_ID: (chatId) => `/chat/${chatId}`,
        CHAT_MESSAGES: (chatId) => `/chat/${chatId}/message`,
        NEW_CHAT_MESSAGE: '/chat/new/message',
        CHAT_MESSAGE: (chatId) => `/chat/${chatId}/message`
    },
    
    // Настройки запросов
    TIMEOUT: 150000,
    HEADERS: {
        'Content-Type': 'application/json'
    }
};

/**
 * Получить полный URL для endpoint
 */
function getApiUrl(endpoint) {
    return API_CONFIG.BASE_URL + endpoint;
}

/**
 * Получить заголовки для запроса (с токеном, если есть)
 * 
 * Примечание: Браузер автоматически добавляет следующие заголовки:
 * - Origin: адрес текущей страницы (например, http://localhost:3000)
 * - Referer: URL страницы, с которой отправляется запрос
 * - User-Agent: информация о браузере и операционной системе
 */
function getAuthHeaders() {
    const token = localStorage.getItem('authToken');
    const headers = { ...API_CONFIG.HEADERS };
    
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    // Логируем, какие заголовки будут отправлены
    console.log('[API] Request headers:', headers);
    console.log('[API] Браузер автоматически добавит: Origin, Referer, User-Agent');
    
    return headers;
}

/**
 * Обработка ошибок API запроса
 */
async function handleApiResponse(response) {
    console.log(`[API] Response status: ${response.status} ${response.statusText}`);
    
    const contentType = response.headers.get('content-type');
    let data;
    
    if (contentType && contentType.includes('application/json')) {
        data = await response.json();
        console.log('[API] Response data:', data);
    } else {
        const text = await response.text();
        console.log('[API] Response text:', text);
        data = { message: text };
    }
    
    if (!response.ok) {
        throw new Error(data.message || `HTTP error! status: ${response.status}`);
    }
    
    return data;
}

/**
 * POST /api/auth - Аутентификация пользователя
 * 
 * @param {string} username - Email пользователя
 * @param {string} password - Пароль пользователя
 * @returns {Promise<Object>} Данные пользователя и токен
 */
async function login(username, password) {
    const url = getApiUrl(API_CONFIG.ENDPOINTS.AUTH);
    const body = {
        username: username,
        password: password
    };
    
    console.log('[API] POST /auth - Login request');
    console.log('[API] URL:', url);
    console.log('[API] Request body:', { username, password: '***' });
    
    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), API_CONFIG.TIMEOUT);
        
        const response = await fetch(url, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(body),
            signal: controller.signal
        });
        
        clearTimeout(timeoutId);
        
        const data = await handleApiResponse(response);
        
        // Сохраняем токен, если есть
        if (data.token) {
            localStorage.setItem('authToken', data.token);
            console.log('[API] Token saved to localStorage');
        }
        
        console.log('[API] Login successful');
        return data;
        
    } catch (error) {
        console.error('[API] Login error:', error);
        throw error;
    }
}

/**
 * GET /user/profile - Получить профиль пользователя
 * 
 * @returns {Promise<Object>} Данные профиля пользователя
 */
async function getUserProfile() {
    const url = getApiUrl(API_CONFIG.ENDPOINTS.USER_PROFILE);
    
    console.log('[API] GET /user/profile - Get user profile');
    console.log('[API] URL:', url);
    
    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), API_CONFIG.TIMEOUT);
        
        const response = await fetch(url, {
            method: 'GET',
            headers: getAuthHeaders(),
            signal: controller.signal
        });
        
        clearTimeout(timeoutId);
        
        const data = await handleApiResponse(response);
        
        console.log('[API] User profile received:', data);
        return data;
        
    } catch (error) {
        console.error('[API] Get user profile error:', error);
        throw error;
    }
}

/**
 * GET /user/info - Получить информацию о пользователе (альтернативный endpoint)
 * 
 * @returns {Promise<Object>} Информация о пользователе
 */
async function getUserInfo() {
    const url = getApiUrl(API_CONFIG.ENDPOINTS.USER_INFO);
    
    console.log('[API] GET /user/info - Get user info');
    console.log('[API] URL:', url);
    
    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), API_CONFIG.TIMEOUT);
        
        const response = await fetch(url, {
            method: 'GET',
            headers: getAuthHeaders(),
            signal: controller.signal
        });
        
        clearTimeout(timeoutId);
        
        const data = await handleApiResponse(response);
        
        console.log('[API] User info received:', data);
        return data;
        
    } catch (error) {
        console.error('[API] Get user info error:', error);
        throw error;
    }
}

/**
 * GET /user/profile - Получить профиль пользователя
 * 
 * @returns {Promise<Object>} Данные профиля пользователя
 */
async function getUserProfile() {
    const url = getApiUrl(API_CONFIG.ENDPOINTS.USER_PROFILE);
    
    console.log('[API] GET /user/profile - Get user profile');
    console.log('[API] URL:', url);
    
    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), API_CONFIG.TIMEOUT);
        
        const response = await fetch(url, {
            method: 'GET',
            headers: getAuthHeaders(),
            signal: controller.signal
        });
        
        clearTimeout(timeoutId);
        
        const data = await handleApiResponse(response);
        
        console.log('[API] User profile received:', data);
        return data;
        
    } catch (error) {
        console.error('[API] Get user profile error:', error);
        throw error;
    }
}

/**
 * GET /api/chat - Получить все чаты (id и последнее сообщение как превью)
 * 
 * @returns {Promise<Array>} Массив чатов с id и последним сообщением
 */
async function getAllChats() {
    const url = getApiUrl(API_CONFIG.ENDPOINTS.CHAT);
    
    console.log('[API] GET /chat - Get all chats');
    console.log('[API] URL:', url);
    
    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), API_CONFIG.TIMEOUT);
        
        const response = await fetch(url, {
            method: 'GET',
            headers: getAuthHeaders(),
            signal: controller.signal
        });
        
        clearTimeout(timeoutId);
        
        const data = await handleApiResponse(response);
        
        console.log('[API] Chats received:', data.length || 0);
        return data;
        
    } catch (error) {
        console.error('[API] Get all chats error:', error);
        throw error;
    }
}

/**
 * GET /api/chat/{chatId} - Получить все сообщения чата
 * 
 * @param {string} chatId - ID чата
 * @returns {Promise<Array>} Массив сообщений
 */
async function getChatMessages(chatId) {
    const url = getApiUrl(API_CONFIG.ENDPOINTS.CHAT_MESSAGES(chatId));
    
    console.log('[API] GET /chat/{chatId} - Get chat messages');
    console.log('[API] URL:', url);
    console.log('[API] Chat ID:', chatId);
    
    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), API_CONFIG.TIMEOUT);
        
        const response = await fetch(url, {
            method: 'GET',
            headers: getAuthHeaders(),
            signal: controller.signal
        });
        
        clearTimeout(timeoutId);
        
        const data = await handleApiResponse(response);
        
        console.log('[API] Messages received:', data.length || 0);
        return data;
        
    } catch (error) {
        console.error('[API] Get chat messages error:', error);
        throw error;
    }
}

/**
 * DELETE /api/chat/{chatId} - Удалить чат
 * 
 * @param {string} chatId - ID чата для удаления
 * @returns {Promise<Object>} Результат удаления
 */
async function deleteChat(chatId) {
    const url = getApiUrl(API_CONFIG.ENDPOINTS.CHAT_BY_ID(chatId));
    
    console.log('[API] DELETE /chat/{chatId} - Delete chat');
    console.log('[API] URL:', url);
    console.log('[API] Chat ID:', chatId);
    
    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), API_CONFIG.TIMEOUT);
        
        const response = await fetch(url, {
            method: 'DELETE',
            headers: getAuthHeaders(),
            signal: controller.signal
        });
        
        clearTimeout(timeoutId);
        
        const data = await handleApiResponse(response);
        
        console.log('[API] Chat deleted successfully');
        return data;
        
    } catch (error) {
        console.error('[API] Delete chat error:', error);
        throw error;
    }
}

/**
 * POST /chat/new/message - Отправить сообщение в новый чат через SSE
 * 
 * @param {string} message - Текст сообщения
 * @param {Function} onMessage - Callback функция, вызываемая при получении сообщения через SSE
 * @param {Function} onError - Callback функция, вызываемая при ошибке
 * @param {Function} onComplete - Callback функция, вызываемая при завершении потока
 * @returns {Promise<EventSource>} EventSource объект для управления SSE подключением
 */
function sendMessageToNewChat(message, onMessage, onError, onComplete) {
    const url = getApiUrl(API_CONFIG.ENDPOINTS.NEW_CHAT_MESSAGE);
    const body = {
        message: message
    };
    
    console.log('[API] POST /chat/new/message - Send message to new chat via SSE');
    console.log('[API] URL:', url);
    console.log('[API] Request body:', body);
    
    // Создаем POST запрос для отправки сообщения
    return fetch(url, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(body)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        console.log('[API] Message sent, starting SSE connection');
        console.log('[API] Chat ID:', data.chatId || data.id);
        
        // После отправки сообщения, подключаемся к SSE потоку
        // Предполагаем, что SSE endpoint работает на том же URL
        const sseUrl = url + '?chatId=' + (data.chatId || data.id);
        console.log('[API] SSE URL:', sseUrl);
        
        const eventSource = new EventSource(sseUrl);
        
        eventSource.onopen = function() {
            console.log('[API] SSE connection opened');
        };
        
        eventSource.onmessage = function(event) {
            console.log('[API] SSE message received:', event.data);
            
            try {
                const messageData = JSON.parse(event.data);
                if (onMessage) {
                    onMessage(messageData);
                }
            } catch (e) {
                console.error('[API] Failed to parse SSE message:', e);
                if (onError) {
                    onError(e);
                }
            }
        };
        
        eventSource.onerror = function(error) {
            console.error('[API] SSE connection error:', error);
            console.log('[API] EventSource readyState:', eventSource.readyState);
            
            if (eventSource.readyState === EventSource.CLOSED) {
                console.log('[API] SSE connection closed');
                if (onComplete) {
                    onComplete();
                }
            } else {
                if (onError) {
                    onError(error);
                }
            }
        };
        
        return eventSource;
    })
    .catch(error => {
        console.error('[API] Failed to send message:', error);
        if (onError) {
            onError(error);
        }
        throw error;
    });
}

/**
 * POST /chat/{chatId}/message - Отправить сообщение в существующий чат через SSE
 * 
 * @param {string} chatId - ID чата
 * @param {string} message - Текст сообщения
 * @param {Function} onMessage - Callback функция, вызываемая при получении сообщения
 * @param {Function} onError - Callback функция, вызываемая при ошибке
 * @param {Function} onComplete - Callback функция, вызываемая при завершении
 * @returns {EventSource} EventSource объект для управления подключением
 */
function sendMessageToChat(chatId, message, onMessage, onError, onComplete) {
    const url = getApiUrl(API_CONFIG.ENDPOINTS.CHAT_MESSAGE(chatId));
    const body = {
        message: message
    };
    
    console.log('[API] POST /chat/{chatId}/message - Send message to chat via SSE');
    console.log('[API] URL:', url);
    console.log('[API] Chat ID:', chatId);
    console.log('[API] Request body:', body);
    
    // Создаем POST запрос для отправки сообщения
    fetch(url, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(body)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        console.log('[API] Message sent, starting SSE connection');
        
        // После отправки сообщения, подключаемся к SSE потоку
        const sseUrl = url + '?chatId=' + chatId;
        const eventSource = new EventSource(sseUrl);
        
        eventSource.onopen = function() {
            console.log('[API] SSE connection opened');
        };
        
        eventSource.onmessage = function(event) {
            console.log('[API] SSE message received:', event.data);
            
            try {
                const data = JSON.parse(event.data);
                if (onMessage) {
                    onMessage(data);
                }
            } catch (e) {
                console.error('[API] Failed to parse SSE message:', e);
                if (onError) {
                    onError(e);
                }
            }
        };
        
        eventSource.onerror = function(error) {
            console.error('[API] SSE connection error:', error);
            
            if (eventSource.readyState === EventSource.CLOSED) {
                console.log('[API] SSE connection closed');
                if (onComplete) {
                    onComplete();
                }
            } else {
                if (onError) {
                    onError(error);
                }
            }
        };
        
        return eventSource;
    })
    .catch(error => {
        console.error('[API] Failed to send message:', error);
        if (onError) {
            onError(error);
        }
    });
}

/**
 * Выход из системы (удаление токена)
 */
function logout() {
    localStorage.removeItem('authToken');
    console.log('[API] Logged out, token removed');
}

/**
 * Проверка наличия токена
 */
function isAuthenticated() {
    const token = localStorage.getItem('authToken');
    const authenticated = !!token;
    console.log('[API] Is authenticated:', authenticated);
    return authenticated;
}

// Экспорт функций для использования в других файлах
// В браузере можно использовать через window.API
if (typeof window !== 'undefined') {
    window.API = {
        login,
        getUserProfile,
        getUserInfo,
        getAllChats,
        getChatMessages,
        deleteChat,
        sendMessageToNewChat,
        sendMessageToChat,
        logout,
        isAuthenticated,
        getAuthHeaders,
        API_CONFIG
    };
    
    console.log('[API] API functions loaded and available via window.API');
}

// Примеры использования (раскомментируйте для тестирования):

/*
// 1. Аутентификация
API.login('user@example.com', 'password123')
    .then(data => {
        console.log('Login successful:', data);
    })
    .catch(error => {
        console.error('Login failed:', error);
    });

// 2. Получить все чаты
API.getAllChats()
    .then(chats => {
        console.log('Chats:', chats);
    })
    .catch(error => {
        console.error('Failed to get chats:', error);
    });

// 3. Получить сообщения чата
API.getChatMessages('chat-id-123')
    .then(messages => {
        console.log('Messages:', messages);
    })
    .catch(error => {
        console.error('Failed to get messages:', error);
    });

// 4. Удалить чат
API.deleteChat('chat-id-123')
    .then(() => {
        console.log('Chat deleted');
    })
    .catch(error => {
        console.error('Failed to delete chat:', error);
    });

// 5. Отправить сообщение в новый чат
API.sendMessageToNewChat('Привет, это новое сообщение!')
    .then(data => {
        console.log('New chat created:', data);
    })
    .catch(error => {
        console.error('Failed to send message:', error);
    });

// 6. Отправить сообщение в существующий чат
API.sendMessageToChat('chat-id-123', 'Это сообщение в существующий чат')
    .then(data => {
        console.log('Message sent:', data);
    })
    .catch(error => {
        console.error('Failed to send message:', error);
    });
*/

